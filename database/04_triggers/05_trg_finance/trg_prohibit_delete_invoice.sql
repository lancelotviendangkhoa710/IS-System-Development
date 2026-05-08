-- Trigger cấm xóa dữ lệu hóa đơn
CREATE OR REPLACE TRIGGER TRG_CAMXOAKETOAN_HOADON
BEFORE DELETE ON HOADON
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_TC_CAM_XOA_HOADON, 'Vi pham ke toan: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Don!');
END;
/