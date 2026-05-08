-- ==========================================
-- UC16: HỦY ĐƠN VÀ HOÀN CỌC
-- ==========================================

-- 1) Thêm loại thu chi Hủy đơn nếu chưa có
INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI) 
SELECT N'Hủy đơn', N'Chi' FROM DUAL 
WHERE NOT EXISTS (SELECT 1 FROM LOAITHUCHI WHERE TENLOAITHUCHI = N'Hủy đơn');
COMMIT;

-- 2) Procedure Hủy đơn, Hoàn kho và Hoàn cọc (Tạo phiếu chi)
CREATE OR REPLACE PROCEDURE PROC_HUYDON_HOANCOC(
    P_MADON IN DONDATHANG.MADON%type,
    P_LYDOHUY IN NVARCHAR2,
    P_MANV_CAPNHAT IN NHANVIEN.MANV%type,
    P_SOTIEN_HOAN IN NUMBER
)
IS
    V_MATRANGTHAI_CU NUMBER;
    V_MATT_HUY NUMBER;
    V_MACA NUMBER;
    V_MALOAITC_HUY NUMBER;
BEGIN
    -- A. Gọi logic hủy đơn và hoàn kho cơ bản (để tái sử dụng logic)
    -- Nếu muốn viết inline để tối ưu transaction thì copy code từ PROC_HUYDONVAHOANKHO
    
    -- 1. Lấy trạng thái cũ
    SELECT MATRANGTHAI INTO V_MATRANGTHAI_CU FROM DONDATHANG WHERE MADON = P_MADON;

    -- 2. Lấy ID trạng thái "Hủy"
    SELECT MATRANGTHAI INTO V_MATT_HUY FROM TRANGTHAIDON 
    WHERE TENTRANGTHAI = N'Hủy' OR UPPER(TENTRANGTHAI) = N'HỦY' FETCH FIRST 1 ROW ONLY;

    -- 3. Cập nhật trạng thái đơn
    UPDATE DONDATHANG SET MATRANGTHAI = V_MATT_HUY WHERE MADON = P_MADON;

    -- 4. Hoàn kho (chỉ áp dụng cho bánh bán sẵn trong CTDONHANG)
    FOR ROW_CT IN (SELECT MASP, SOLUONG FROM CTDONHANG WHERE MADON = P_MADON)
    LOOP
        UPDATE SANPHAM SET TONKHO = TONKHO + ROW_CT.SOLUONG WHERE MASP = ROW_CT.MASP;
    END LOOP;

    -- 5. Tạo phiếu chi hoàn tiền (nếu số tiền hoàn > 0)
    IF P_SOTIEN_HOAN > 0 THEN
        -- Lấy ca hiện tại (hoặc ca gần nhất của nhân viên)
        BEGIN
            SELECT MACA INTO V_MACA FROM CALAMVIEC WHERE THOIGIANDONGCA IS NULL FETCH FIRST 1 ROW ONLY;
        EXCEPTION WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_CA_KHONG_TON_TAI, 'Không tìm thấy ca làm việc đang mở để thực hiện hoàn tiền.');
        END;

        -- Lấy mã loại thu chi "Hủy đơn"
        SELECT MALOAITHUCHI INTO V_MALOAITC_HUY FROM LOAITHUCHI WHERE TENLOAITHUCHI = N'Hủy đơn' FETCH FIRST 1 ROW ONLY;

        INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MACA, GHICHU)
        VALUES (V_MALOAITC_HUY, P_SOTIEN_HOAN, P_MANV_CAPNHAT, V_MACA, N'Hoàn cọc đơn #' || P_MADON || ': ' || SUBSTR(P_LYDOHUY, 1, 400));
    END IF;

    -- 6. Ghi nhật ký
    INSERT INTO LICHSUDONHANG(MADON, MATRANGTHAI_CU, MATRANGTHAI_MOI, THOIGIANTHAYDOI, MANV_CAPNHAT, GHICHU)
    VALUES (P_MADON, V_MATRANGTHAI_CU, V_MATT_HUY, CURRENT_TIMESTAMP, P_MANV_CAPNHAT, N'[HỦY] ' || SUBSTR(P_LYDOHUY, 1, 90));

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20100, 'Lỗi hủy đơn hàng #' || P_MADON || ': ' || SQLERRM);
END;
/
