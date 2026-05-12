-- Procedure mẫu CUD cho biến động kho (PHIEUXUATKHO + chi tiết)
CREATE OR REPLACE PROCEDURE PROC_STOCKCHANGE_CUD_TEMPLATE (
    P_ACTION            IN VARCHAR2,      -- CREATE | DELETE
    P_MAPX              IN NUMBER,
    P_MANV              IN NUMBER,
    P_LYDOXUAT          IN VARCHAR2,
    P_JSON_CT_NL        IN CLOB,          -- JSON list CTPHIEUXUAT_NL
    P_JSON_CT_TP        IN CLOB,          -- JSON list CTPHIEUXUAT_TP
    P_MAPX_OUT          OUT NUMBER
)
IS
    V_MAPX              NUMBER;
    V_ACTION            VARCHAR2(20) := UPPER(TRIM(P_ACTION));
BEGIN
    -- 1. Tạo phiếu xuất
    IF V_ACTION = 'CREATE' THEN
        INSERT INTO PHIEUXUATKHO (MANV, LYDOXUAT)
        VALUES (P_MANV, P_LYDOXUAT)
        RETURNING MAPX INTO V_MAPX;

        -- 2. Ghi chi tiết xuất nguyên liệu (nếu có)
        IF P_JSON_CT_NL IS NOT NULL THEN
            INSERT INTO CTPHIEUXUAT_NL (MAPX, MALO, SOLUONG)
            SELECT V_MAPX, J.MALO, J.SOLUONG
            FROM JSON_TABLE(P_JSON_CT_NL, '$[*]'
                COLUMNS (
                    MALO    NUMBER PATH '$.maLo',
                    SOLUONG NUMBER PATH '$.soLuong'
                )
            ) J;
        END IF;

        -- 3. Ghi chi tiết xuất thành phẩm (nếu có)
        IF P_JSON_CT_TP IS NOT NULL THEN
            INSERT INTO CTPHIEUXUAT_TP (MAPX, MASP, SOLUONG)
            SELECT V_MAPX, J.MASP, J.SOLUONG
            FROM JSON_TABLE(P_JSON_CT_TP, '$[*]'
                COLUMNS (
                    MASP    NUMBER PATH '$.maSP',
                    SOLUONG NUMBER PATH '$.soLuong'
                )
            ) J;
        END IF;

        P_MAPX_OUT := V_MAPX;
    ELSIF V_ACTION = 'DELETE' THEN
        -- 4. Hủy phiếu xuất (trigger DELETE sẽ tự hoàn tồn nếu đã thiết kế)
        DELETE FROM CTPHIEUXUAT_NL WHERE MAPX = P_MAPX;
        DELETE FROM CTPHIEUXUAT_TP WHERE MAPX = P_MAPX;
        DELETE FROM PHIEUXUATKHO WHERE MAPX = P_MAPX;

        P_MAPX_OUT := P_MAPX;
    ELSE
        RAISE_APPLICATION_ERROR(-20100, 'Hành động CUD không hợp lệ cho xuất kho.');
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20101, 'Lỗi xử lý CUD biến động kho: ' || SQLERRM);
END;
/
