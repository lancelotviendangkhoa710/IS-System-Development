
CREATE OR REPLACE PROCEDURE PROC_TAODONHANG_FIX(
    P_NGAYGIONHANBANH IN DONDATHANG.NGAYGIONHANBANH%TYPE,
    P_MAKH          IN DONDATHANG.MAKH%TYPE DEFAULT NULL,
    P_MANV_LAP      IN DONDATHANG.MANV_LAP%TYPE,
    P_MATRANGTHAI   IN DONDATHANG.MATRANGTHAI%TYPE,
    P_TIENDACOC     IN DONDATHANG.TIENDACOC%TYPE DEFAULT 0,
    P_HINHTHUCNHAN  IN DONDATHANG.HINHTHUCNHAN%TYPE DEFAULT 1,
    P_DIACHIGIAO    IN DONDATHANG.DIACHIGIAO%TYPE DEFAULT NULL,
    P_JSONCHITIET   IN CLOB,
    P_MADON_OUT     OUT DONDATHANG.MADON%TYPE
)
IS
    TYPE T_REC_CHITIET IS RECORD (
        MASP        NUMBER,
        SOLUONG     NUMBER,
        DONGIA      NUMBER,
        IS_CUSTOM   VARCHAR2(10),
        GHICHU      NVARCHAR2(200),
        PHUKIEN     NVARCHAR2(500),
        MAKC        NUMBER,
        MACOT       NUMBER,
        MANHAN      NUMBER,
        MATRANGTRI  NUMBER
    );
    TYPE T_TAB_CHITIET IS TABLE OF T_REC_CHITIET INDEX BY PLS_INTEGER;
    V_TAB       T_TAB_CHITIET;
    V_IDX       PLS_INTEGER := 0;

    V_TONGTIEN    NUMBER := 0;
    V_TONKHO      NUMBER := 0;
    V_TENSP       NVARCHAR2(200);
BEGIN
    -- 0. Validate JSON input
    IF P_JSONCHITIET IS NULL OR DBMS_LOB.GETLENGTH(P_JSONCHITIET) = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
            'Du lieu chi tiet don hang rong. Vui long them it nhat 1 san pham vao don.');
    END IF;

    -- 1. Parse JSON vào collection
    FOR J IN (
        SELECT J.MASP, J.SOLUONG, J.DONGIA, J.IS_CUSTOM,
               J.GHICHU, J.PHUKIEN, J.MAKC, J.MACOT, J.MANHAN, J.MATRANGTRI
        FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                SOLUONG NUMBER PATH '$.soLuong',
                DONGIA  NUMBER PATH '$.donGia',
                IS_CUSTOM VARCHAR2(10) PATH '$.isCustom',
                GHICHU  NVARCHAR2(200) PATH '$.ghiChu',
                PHUKIEN NVARCHAR2(500) PATH '$.phuKien',
                MAKC    NUMBER PATH '$.maKC',
                MACOT   NUMBER PATH '$.maCot',
                MANHAN  NUMBER PATH '$.maNhan',
                MATRANGTRI NUMBER PATH '$.maTrangTri'
            )
        ) J
    ) LOOP
        V_IDX := V_IDX + 1;
        V_TAB(V_IDX).MASP := J.MASP;
        V_TAB(V_IDX).SOLUONG := J.SOLUONG;
        V_TAB(V_IDX).DONGIA := J.DONGIA;
        V_TAB(V_IDX).IS_CUSTOM := J.IS_CUSTOM;
        V_TAB(V_IDX).GHICHU := J.GHICHU;
        V_TAB(V_IDX).PHUKIEN := J.PHUKIEN;
        V_TAB(V_IDX).MAKC := J.MAKC;
        V_TAB(V_IDX).MACOT := J.MACOT;
        V_TAB(V_IDX).MANHAN := J.MANHAN;
        V_TAB(V_IDX).MATRANGTRI := J.MATRANGTRI;
        V_TONGTIEN := V_TONGTIEN + NVL(J.SOLUONG * J.DONGIA, 0);
    END LOOP;

    IF V_TAB.COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
            'Du lieu JSON khong chua chi tiet san pham hop le.');
    END IF;

    -- ================================================================
    -- [FIX] Bước 2: Kiểm tra tồn kho VỚI FOR UPDATE → Pessimistic Lock
    -- T2 gọi SELECT này trên cùng MASP → bị BLOCK cho đến khi T1 COMMIT
    -- Sau khi T1 commit (trừ kho), T2 đọc lại → thấy SL = 0 → từ chối bán
    -- ================================================================
    FOR I IN 1..V_TAB.COUNT LOOP
        IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
            SELECT SOLUONGTON, TENSP
            INTO V_TONKHO, V_TENSP
            FROM SANPHAM
            WHERE MASP = V_TAB(I).MASP
            FOR UPDATE;

            IF V_TONKHO < V_TAB(I).SOLUONG THEN
                IF V_TONKHO = 0 THEN
                    RAISE_APPLICATION_ERROR(
                        PKG_ERROR_CODES.ERR_SP_HET_HANG,
                        'San pham "' || V_TENSP || '" da het hang. Co nguoi vua mua truoc ban, vui long chon san pham khac.'
                    );
                ELSE
                    RAISE_APPLICATION_ERROR(
                        PKG_ERROR_CODES.ERR_SP_HET_HANG,
                        'San pham "' || V_TENSP || '" chi con ' || V_TONKHO || ' cai, khong du ' || V_TAB(I).SOLUONG || ' cai yeu cau.'
                    );
                END IF;
            END IF;
        END IF;
    END LOOP;

    -- ================================================================
    -- [DEMO DELAY] Tạo thời gian để quan sát T2 bị block (spinner)
    -- T2 sẽ đứng chờ ở FOR UPDATE trên cho đến khi đây chạy xong và COMMIT
    -- ================================================================
    DECLARE V_X NUMBER := 0;
    BEGIN
        FOR I IN 1..80000000 LOOP V_X := V_X + I; END LOOP;
    END;

    -- 3. Insert Đơn Hàng
    INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, HINHTHUCNHAN, DIACHIGIAO)
    VALUES (P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, NVL(V_TONGTIEN, 0), 0, P_HINHTHUCNHAN, P_DIACHIGIAO)
    RETURNING MADON INTO P_MADON_OUT;

    -- 4. Insert chi tiết
    FOR I IN 1..V_TAB.COUNT LOOP
        IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
            INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
            VALUES (P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA);
        ELSE
            INSERT INTO CTDONTUYCHINH (MADON, MASP, SOLUONG, DONGIA, LOICHUCTRENBANH, GHICHUTHOBANH, MAKC, MACOT, MANHAN, MATRANGTRI)
            VALUES (P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA,
                    V_TAB(I).GHICHU, V_TAB(I).PHUKIEN, V_TAB(I).MAKC, V_TAB(I).MACOT,
                    V_TAB(I).MANHAN, V_TAB(I).MATRANGTRI);
        END IF;
    END LOOP;

    -- 4.5. Gán tiền cọc
    UPDATE DONDATHANG
    SET TIENDACOC = NVL(P_TIENDACOC, 0)
    WHERE MADON = P_MADON_OUT;

    -- 5. Lịch sử & log
    INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT)
    VALUES (P_MADON_OUT, NULL, P_MATRANGTHAI, CURRENT_TIMESTAMP, P_MANV_LAP);

    INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
    VALUES (P_MANV_LAP, 'DON_HANG', 'Tao don hang moi #' || P_MADON_OUT, P_MADON_OUT);

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON, 'Loi he thong khi Tao Don Hang: ' || SQLERRM);
END;
/
