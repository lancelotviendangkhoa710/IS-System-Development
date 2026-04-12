-- Trigger chặn hạn sử dụng chỉ còn 10 ngày kể từ lúc nhập & chống gian lận hạn sử dụng
CREATE OR REPLACE TRIGGER TRG_KIEMTRA_HSD

BEFORE INSERT OR UPDATE OF HANSUDUNG ON CTPHIEUNHAP
FOR EACH ROW
DECLARE
    V_NGAYNHAP DATE;
BEGIN
    -- Chống gian lận hạn sử dụng
    IF UPDATING THEN
        IF :NEW.HANSUDUNG > :OLD.HANSUDUNG THEN
            RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_GIAN_LAN_HSD,
                'CẢNH BÁO GIAN LẬN: Hạn sử dụng mới (' || TO_CHAR(:NEW.HANSUDUNG, 'DD/MM/YYYY') ||
                ') không được phép lớn hơn hạn sử dụng cũ (' || TO_CHAR(:OLD.HANSUDUNG, 'DD/MM/YYYY') || ').');
        END IF;
    END IF;

    -- Chặn hạn sử dụng chỉ còn 10 ngày kể từ lúc nhập
    SELECT NGAYNHAP INTO V_NGAYNHAP
    FROM PHIEUNHAPKHO
    WHERE MAPN = :NEW.MAPN;

    IF TRUNC(:NEW.HANSUDUNG) - 10 <= TRUNC(V_NGAYNHAP) THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_HSD_KHONG_HOPLE,
            'LỖI NGHIỆP VỤ: Hạn sử dụng của lô này (' || TO_CHAR(:NEW.HANSUDUNG, 'DD/MM/YYYY') ||
            ') và ngày nhập kho (' || TO_CHAR(V_NGAYNHAP, 'DD/MM/YYYY') || ') đã quá hạn hoặc gần hết hạn!.');
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_NL_KHONG_CO_PHIEUNHAP, 'Lỗi: Không tìm thấy phiếu nhập kho.');
END;
/