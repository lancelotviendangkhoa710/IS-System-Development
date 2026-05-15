-- Trigger tự tính hạn sử dụng và số lượng còn lại khi ghi nhận mẻ sản xuất mới
CREATE OR REPLACE TRIGGER TRG_TINHHSD_MESANXUAT
BEFORE INSERT ON MESANXUAT
FOR EACH ROW
DECLARE
    V_THOIGIANBAOQUAN SANPHAM.THOIGIANBAOQUAN%TYPE;
BEGIN
    IF INSERTING THEN
        -- 1. Lấy thời gian bảo quản (số ngày) từ sản phẩm
        SELECT NVL(THOIGIANBAOQUAN, 0)
        INTO V_THOIGIANBAOQUAN
        FROM SANPHAM
        WHERE MASP = :NEW.MASP;

        -- 2. Tính hạn sử dụng = ngày sản xuất + số ngày bảo quản
        :NEW.HANSUDUNG     := TRUNC(:NEW.NGAYSANXUAT) + V_THOIGIANBAOQUAN;

        -- 3. Khởi tạo số lượng còn lại = số lượng sản xuất
        :NEW.SOLUONGCONLAI := :NEW.SOLUONGSANXUAT;
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_TINHHSD_MESANXUAT,
            N'Lỗi: Không tìm thấy sản phẩm (MASP=' || :NEW.MASP || N') khi tính hạn sử dụng mẻ.'
        );
END;
/
