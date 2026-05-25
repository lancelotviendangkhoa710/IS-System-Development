-- Procedure Hủy phiếu nhập kho (hoàn ngược nhập kho, vô hiệu phiếu chi liên quan)
CREATE OR REPLACE PROCEDURE PROC_HUYPHIEUNHAPKHO (
    P_MAPN IN PHIEUNHAPKHO.MAPN%TYPE
)
IS
    V_MAPN    PHIEUNHAPKHO.MAPN%TYPE;
    V_DA_XUAT NUMBER;
BEGIN
    -- 1. Kiểm tra phiếu nhập tồn tại (lock để tránh concurrent delete)
    BEGIN
        SELECT MAPN INTO V_MAPN
        FROM PHIEUNHAPKHO
        WHERE MAPN = P_MAPN
        FOR UPDATE;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_KHONG_CO_PHIEUNHAP,
                N'Không tìm thấy phiếu nhập kho với mã: ' || P_MAPN);
    END;

    -- 2. Kiểm tra chưa có lô nào được xuất kho (SOLUONGCONLAI < SOLUONG)
    SELECT COUNT(*) INTO V_DA_XUAT
    FROM CTPHIEUNHAP
    WHERE MAPN = P_MAPN
      AND SOLUONGCONLAI < SOLUONG;

    IF V_DA_XUAT > 0 THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_THE_HUY_PN,
            N'Không thể hủy phiếu nhập #' || P_MAPN ||
            N' — có ' || V_DA_XUAT || N' lô nguyên liệu đã được xuất kho.');
    END IF;

    -- 3. Vô hiệu hóa phiếu chi liên quan (FIX ORA-02292: phá FK_PTC_PN trước khi DELETE)
    --    Không xóa vật lý phiếu chi — tuân thủ nguyên tắc kế toán
    UPDATE PHIEUTHUCHI
    SET TRANGTHAI = N'Đã hủy',
        MAPN      = NULL
    WHERE MAPN = P_MAPN;

    -- 4. Xóa chi tiết lô hàng → TRG_GIAVONTRUNGBINH_SOLUONGTONTONG tự hoàn ngược
    --    tồn kho (SOLUONGTONTONG) và giá vốn trung bình (GIAVONTRUNGBINH)
    DELETE FROM CTPHIEUNHAP
    WHERE MAPN = P_MAPN;

    -- 5. Xóa chứng từ gốc (FK_PTC_PN đã được phá ở bước 3)
    DELETE FROM PHIEUNHAPKHO
    WHERE MAPN = P_MAPN;

    COMMIT;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        IF SQLCODE = PKG_ERROR_CODES.ERR_KHONG_CO_PHIEUNHAP
        OR SQLCODE = PKG_ERROR_CODES.ERR_NL_KHONG_THE_HUY_PN THEN
            RAISE;
        END IF;
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_HOAN_PHIEU_NHAP_KHO,
            N'Lỗi hệ thống khi hủy phiếu nhập kho: ' || SQLERRM);
END;
/
