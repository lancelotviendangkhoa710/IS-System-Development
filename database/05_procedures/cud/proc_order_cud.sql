-- Procedure tạo đơn hàng (Master-Detail + JSON + Concurrent Stock)
-- IMP-06: Validate JSON trước khi parse → error message rõ ràng
-- IMP-10: Parse JSON 1 lần bằng PL/SQL collection, iterate cho tính toán + insert
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
    -- IMP-10: Record type + collection cho one-pass JSON parse
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
    -- 0. IMP-06: Validate JSON input
    IF P_JSONCHITIET IS NULL OR DBMS_LOB.GETLENGTH(P_JSONCHITIET) = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
            'Du lieu chi tiet don hang rong. Vui long them it nhat 1 san pham vao don.');
    END IF;

    -- 1. IMP-10: Parse JSON 1 lần duy nhất vào collection
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

        -- Tính tổng tiền ngay trong vòng lặp (thay cho query riêng)
        V_TONGTIEN := V_TONGTIEN + NVL(J.SOLUONG * J.DONGIA, 0);
    END LOOP;

    -- IMP-06: Validate sau parse — đảm bảo có ít nhất 1 dòng
    IF V_TAB.COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON,
            'Du lieu JSON khong chua chi tiet san pham hop le.');
    END IF;

    -- 2. Kiểm tra tồn kho (Pessimistic Lock — FOR UPDATE)
    --    Chỉ áp dụng cho bánh bán sẵn (isCustom = false)
    FOR I IN 1..V_TAB.COUNT LOOP
        IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
            -- Lock dòng SANPHAM để ngăn concurrent oversell
            SELECT SOLUONGTON, TENSP
            INTO V_TONKHO, V_TENSP
            FROM SANPHAM
            WHERE MASP = V_TAB(I).MASP
            FOR UPDATE;

            IF V_TONKHO < V_TAB(I).SOLUONG THEN
                RAISE_APPLICATION_ERROR(
                    PKG_ERROR_CODES.ERR_SP_HET_HANG,
                    'Giao dich that bai: San pham "' || V_TENSP || '" chi con ' || V_TONKHO || ' cai, khong du ' || V_TAB(I).SOLUONG || ' cai yeu cau.'
                );
            END IF;
        END IF;
    END LOOP;

    -- 3. Insert Đơn Hàng Gốc (Đã kèm TONGTIENHDBAN)
    INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, HINHTHUCNHAN, DIACHIGIAO)
    VALUES (P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, NVL(V_TONGTIEN, 0), NVL(P_TIENDACOC, 0), P_HINHTHUCNHAN, P_DIACHIGIAO)
    RETURNING MADON INTO P_MADON_OUT;

    -- 4. Đẩy chi tiết — iterate collection (IMP-10: không parse JSON_TABLE lại)
    FOR I IN 1..V_TAB.COUNT LOOP
        IF LOWER(NVL(V_TAB(I).IS_CUSTOM, 'false')) = 'false' THEN
            -- Bánh bán sẵn → CTDONHANG
            INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA)
            VALUES (P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA);
            -- Trigger TRG_TRUKHO_DONHANG tự động trừ kho
        ELSE
            -- Bánh tùy chỉnh → CTDONTUYCHINH
            INSERT INTO CTDONTUYCHINH (MADON, MASP, SOLUONG, DONGIA, LOICHUCTRENBANH, GHICHUTHOBANH, MAKC, MACOT, MANHAN, MATRANGTRI)
            VALUES (P_MADON_OUT, V_TAB(I).MASP, V_TAB(I).SOLUONG, V_TAB(I).DONGIA,
                    V_TAB(I).GHICHU, V_TAB(I).PHUKIEN, V_TAB(I).MAKC, V_TAB(I).MACOT,
                    V_TAB(I).MANHAN, V_TAB(I).MATRANGTRI);
        END IF;
    END LOOP;

    -- 5. Ghi nhận lịch sử tạo đơn
    INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT)
    VALUES (P_MADON_OUT, NULL, P_MATRANGTHAI, CURRENT_TIMESTAMP, P_MANV_LAP);

    -- 6. Ghi log hoạt động nhân viên
    INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
    VALUES (P_MANV_LAP, 'DON_HANG', 'Tao don hang moi #' || P_MADON_OUT, P_MADON_OUT);

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HUY_TAO_DON, 'Loi he thong khi Tao Don Hang: ' || SQLERRM);
END;
/

-- ================================================================
-- PROC_HUYDON_HOANCOC
-- Hủy đơn đặt hàng và ghi phiếu chi hoàn tiền (nếu có cọc).
-- Cả 2 bước trong 1 transaction — COMMIT 1 lần cuối.
-- ================================================================
CREATE OR REPLACE PROCEDURE PROC_HUYDON_HOANCOC (
    P_MADON     IN DONDATHANG.MADON%TYPE,
    P_LYDOHUY   IN NVARCHAR2,
    P_MANV      IN NHANVIEN.MANV%TYPE,
    P_SOTIENHOANTIEN IN NUMBER,
    P_MACA      IN CALAMVIEC.MACA%TYPE
)
IS
    V_MATRANGTHAI_HUY   NUMBER;
    V_MALOAITHUCHI_CHI  NUMBER;
    V_MAPHIEUTC_OUT     PHIEUTHUCHI.MAPHIEUTC%TYPE;
BEGIN
    -- 1. Lấy mã trạng thái "Đã hủy"
    SELECT MATRANGTHAI INTO V_MATRANGTHAI_HUY
    FROM TRANGTHAIDON
    WHERE UPPER(TENTRANGTHAI) LIKE N'%H%Y%'
      AND ROWNUM = 1;

    -- 2. Cập nhật trạng thái đơn sang "Đã hủy"
    UPDATE DONDATHANG
    SET MATRANGTHAI = V_MATRANGTHAI_HUY
    WHERE MADON = P_MADON;

    IF SQL%ROWCOUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20910, N'Không tìm thấy đơn hàng để hủy: ' || P_MADON);
    END IF;

    -- 3. Ghi lịch sử hủy
    INSERT INTO LICHSUDONHANG (MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT, GHICHU)
    VALUES (P_MADON, NULL, V_MATRANGTHAI_HUY, CURRENT_TIMESTAMP, P_MANV, P_LYDOHUY);

    -- 4. Ghi log hoạt động nhân viên
    INSERT INTO HOATDONGNHANVIEN (MANV, NHOM, HANHDONG, ENTITY_ID)
    VALUES (P_MANV, 'DON_HANG', N'Huy don hang #' || P_MADON || N' — ' || P_LYDOHUY, P_MADON);

    -- 5. Nếu có hoàn tiền → tạo phiếu chi trong CÙNG transaction
    IF NVL(P_SOTIENHOANTIEN, 0) > 0 THEN
        -- Lấy mã loại chi "Hoàn tiền" (động, không hardcode ID)
        BEGIN
            SELECT MALOAITHUCHI INTO V_MALOAITHUCHI_CHI
            FROM LOAITHUCHI
            WHERE UPPER(TENLOAITHUCHI) LIKE N'%HOAN%TIEN%'
              AND PHANLOAI = 'Chi'
              AND THOIDIEMXOA IS NULL
              AND ROWNUM = 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                -- Tự tạo loại chi "Hoàn tiền" nếu chưa có
                INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI, THOIDIEMXOA, MANX)
                VALUES (N'Hoàn tiền', 'Chi', NULL, NULL)
                RETURNING MALOAITHUCHI INTO V_MALOAITHUCHI_CHI;
        END;

        -- Tạo phiếu chi hoàn tiền — liên kết với đơn hàng
        -- Bug 4 fix: NULLIF(P_MACA, 0) → nếu maCa = 0 (ca chưa mở) → lưu NULL thay vì FK fail
        INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MAHD, MAPN, MACA, GHICHU)
        VALUES (
            V_MALOAITHUCHI_CHI,
            P_SOTIENHOANTIEN,
            P_MANV,
            NULL,                  -- MAHD: không có hóa đơn
            NULL,                  -- MAPN: không phải phiếu nhập kho
            NULLIF(P_MACA, 0),    -- Bug 4: 0 → NULL tránh FK violation
            N'Hoàn tiền cọc đơn hàng #' || P_MADON
        );
    END IF;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20911, N'Lỗi hệ thống khi hủy đơn hàng: ' || SQLERRM);
END;
/
