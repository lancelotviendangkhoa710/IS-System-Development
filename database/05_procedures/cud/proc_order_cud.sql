CREATE OR REPLACE PROCEDURE PROC_TAODONHANG(
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
    V_TONGTIEN    NUMBER := 0;
    V_TONKHO      NUMBER := 0;
    V_SOLUONG_YC  NUMBER := 0;
    V_TENSP       NVARCHAR2(200);
    EX_HET_HANG   EXCEPTION;
BEGIN
    -- 1. Tính toán tổng tiền trước để tránh vi phạm Check Constraint (TONGTIEN >= TIENDACOC)
    SELECT SUM(J.SOLUONG * J.DONGIA) INTO V_TONGTIEN
    FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
        COLUMNS (
            SOLUONG NUMBER PATH '$.soLuong',
            DONGIA  NUMBER PATH '$.donGia'
        )
    ) J;

    -- 2. Kiểm tra tồn kho (Pessimistic Lock — FOR UPDATE)
    --    Chỉ áp dụng cho bánh bán sẵn (isCustom = false)
    FOR r IN (
        SELECT J.MASP, J.SOLUONG AS SOLUONG_YC
        FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                SOLUONG NUMBER PATH '$.soLuong',
                IS_CUSTOM VARCHAR2(10) PATH '$.isCustom'
            )
        ) J
        WHERE LOWER(NVL(J.IS_CUSTOM, 'false')) = 'false'
    ) LOOP
        -- Lock dòng SANPHAM để ngăn concurrent oversell
        SELECT TONKHO, TENSP
        INTO V_TONKHO, V_TENSP
        FROM SANPHAM
        WHERE MASP = r.MASP
        FOR UPDATE;

        IF V_TONKHO < r.SOLUONG_YC THEN
            RAISE_APPLICATION_ERROR(
                PKG_ERROR_CODES.ERR_SP_HET_HANG,
                'Giao dịch thất bại: Sản phẩm "' || V_TENSP || '" chỉ còn ' || V_TONKHO || ' cái, không đủ ' || r.SOLUONG_YC || ' cái yêu cầu.'
            );
        END IF;
    END LOOP;

    -- 3. Insert Đơn Hàng Gốc (Đã kèm TONGTIENHDBAN)
    INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, HINHTHUCNHAN, DIACHIGIAO)
    VALUES (P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, NVL(V_TONGTIEN, 0), NVL(P_TIENDACOC, 0), P_HINHTHUCNHAN, P_DIACHIGIAO)
    RETURNING MADON INTO P_MADON_OUT;

    -- 4. Đẩy chi tiết bánh bán sẵn & Trừ tồn kho
    INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
    SELECT P_MADON_OUT, J.MASP, J.SOLUONG, J.DONGIA
    FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
        COLUMNS (
            MASP    NUMBER PATH '$.maSP',
            SOLUONG NUMBER PATH '$.soLuong',
            DONGIA  NUMBER PATH '$.donGia',
            IS_CUSTOM VARCHAR2(10) PATH '$.isCustom'
        )
    ) J
    WHERE LOWER(NVL(J.IS_CUSTOM, 'false')) = 'false';

    -- Trừ tồn kho ngay sau khi insert
    UPDATE SANPHAM SP
    SET TONKHO = TONKHO - (
        SELECT J.SOLUONG
        FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                SOLUONG NUMBER PATH '$.soLuong',
                IS_CUSTOM VARCHAR2(10) PATH '$.isCustom'
            )
        ) J
        WHERE J.MASP = SP.MASP
          AND LOWER(NVL(J.IS_CUSTOM, 'false')) = 'false'
    )
    WHERE SP.MASP IN (
        SELECT J.MASP
        FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
            COLUMNS (
                MASP    NUMBER PATH '$.maSP',
                IS_CUSTOM VARCHAR2(10) PATH '$.isCustom'
            )
        ) J
        WHERE LOWER(NVL(J.IS_CUSTOM, 'false')) = 'false'
    );

    -- 5. Đẩy chi tiết bánh Tùy Chỉnh
    INSERT INTO CTDONTUYCHINH (MADON, MASP, SOLUONG, DONGIA, LOICHUCTRENBANH, GHICHUTHOBANH, MAKC, MACOT, MANHAN, MATRANGTRI)
    SELECT P_MADON_OUT, J.MASP, J.SOLUONG, J.DONGIA, J.GHICHU, J.PHUKIEN, J.MAKC, J.MACOT, J.MANHAN, J.MATRANGTRI
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
    WHERE LOWER(J.IS_CUSTOM) = 'true';

    -- 6. Ghi nhận lịch sử tạo đơn
    INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT)
    VALUES (P_MADON_OUT, NULL, P_MATRANGTHAI, CURRENT_TIMESTAMP, P_MANV_LAP);

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON, 'Lỗi hệ thống khi Tạo Đơn Hàng: ' || SQLERRM);
END;
/
