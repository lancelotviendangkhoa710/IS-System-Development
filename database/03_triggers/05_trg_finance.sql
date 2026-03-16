-- Trigger cấm xóa dữ lệu hóa đơn
CREATE OR REPLACE TRIGGER TRG_CAMXOAKETOAN_HOADON
BEFORE DELETE ON HOADON
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Don!');
END;
/

-- Trigger cấm xóa dữ liệu phiếu thu chi
CREATE OR REPLACE TRIGGER TRG_CAMXOAKETOAN_PHIEUTHUCHI
BEFORE DELETE ON PHIEUTHUCHI
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Phieu!');
END;
/