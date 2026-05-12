-- Procedure mẫu CUD cho Đơn hàng (DONDATHANG + chi tiết)
CREATE OR REPLACE PROCEDURE PROC_DONDATHANG_CUD_TEMPLATE (
    P_ACTION            IN VARCHAR2,      -- CREATE | UPDATE | DELETE
    P_MADON             IN NUMBER,
    P_MANV              IN NUMBER,
    P_MAKH              IN NUMBER,
    P_MATRANGTHAI       IN NUMBER,
    P_NGAYGIONHANBANH   IN DATE,
    P_JSON_CHITIET      IN CLOB,          -- JSON list chi tiết đơn
    P_MADON_OUT         OUT NUMBER
)
IS
    V_MADON             NUMBER;
    V_ACTION            VARCHAR2(20) := UPPER(TRIM(P_ACTION));
BEGIN
    -- 1. Tạo đơn mới
    IF V_ACTION = 'CREATE' THEN
        INSERT INTO DONDATHANG (MANV, MAKH, MATRANGTHAI, NGAYGIONHANBANH)
        VALUES (P_MANV, P_MAKH, P_MATRANGTHAI, P_NGAYGIONHANBANH)
        RETURNING MADON INTO V_MADON;

        -- 2. Ghi chi tiết đơn từ JSON_TABLE
        INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
        SELECT V_MADON, J.MASP, J.SOLUONG, J.DONGIA
        FROM JSON_TABLE(P_JSON_CHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                SOLUONG NUMBER PATH '$.soLuong',
                DONGIA  NUMBER PATH '$.donGia'
            )
        ) J;

        P_MADON_OUT := V_MADON;
    ELSIF V_ACTION = 'UPDATE' THEN
        -- 3. Cập nhật đơn
        UPDATE DONDATHANG
        SET MAKH = P_MAKH,
            MATRANGTHAI = P_MATRANGTHAI,
            NGAYGIONHANBANH = P_NGAYGIONHANBANH
        WHERE MADON = P_MADON;

        -- 4. Cập nhật chi tiết (mẫu cơ bản: xóa cũ, chèn lại)
        DELETE FROM CTDONHANG WHERE MADON = P_MADON;
        INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
        SELECT P_MADON, J.MASP, J.SOLUONG, J.DONGIA
        FROM JSON_TABLE(P_JSON_CHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                SOLUONG NUMBER PATH '$.soLuong',
                DONGIA  NUMBER PATH '$.donGia'
            )
        ) J;

        P_MADON_OUT := P_MADON;
    ELSIF V_ACTION = 'DELETE' THEN
        -- 5. Xóa mềm (khuyến nghị) hoặc chuyển trạng thái hủy
        UPDATE DONDATHANG
        SET MATRANGTHAI = P_MATRANGTHAI
        WHERE MADON = P_MADON;

        P_MADON_OUT := P_MADON;
    ELSE
        RAISE_APPLICATION_ERROR(-20090, 'Hành động CUD không hợp lệ cho đơn hàng.');
    END IF;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20091, 'Lỗi xử lý CUD đơn hàng: ' || SQLERRM);
END;
/
