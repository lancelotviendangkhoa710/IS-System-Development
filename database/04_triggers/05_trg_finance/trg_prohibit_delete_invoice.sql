-- Trigger cấm xóa dữ lệu hóa đơn
CREATE OR REPLACE TRIGGER TRG_CAMXOAKETOAN_HOADON
BEFORE DELETE ON HOADON
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_TC_CAM_XOA_HOADON, 'Vi phạm kế toán: Cấm tuyệt đối xóa vật lý chứng từ tài chính. Vui lòng sử dụng chức năng Hủy Đơn!');
END;
/