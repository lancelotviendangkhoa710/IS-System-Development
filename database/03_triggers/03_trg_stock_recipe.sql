CREATE OR REPLACE TRIGGER TRG_KiemTraVSATTP
    BEFORE INSERT OR UPDATE ON NGUYENLIEU
    FOR EACH ROW
BEGIN
    IF :NEW.DATCHUANVSATTP = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'NGUYEN_LIEU_KHONG_DAT_CHUAN: Nguyen lieu khong dat chuan, cam nhap kho');
    END IF;
END;
/

CREATE OR REPLACE TRIGGER TRG_ChongGianLanHSD
AFTER UPDATE OF HanSuDung ON CTPHIEUNHAP
FOR EACH ROW
BEGIN
    IF :NEW.HanSuDung > :OLD.HanSuDung THEN

        RAISE_APPLICATION_ERROR(-20002,
            'CANH_BAO_GIAN_LAN: Han su dung moi (' || TO_CHAR(:NEW.HanSuDung, 'DD/MM/YYYY') ||
            ') khong duoc phep muon hon han cu (' || TO_CHAR(:OLD.HanSuDung, 'DD/MM/YYYY') || ').');
    END IF;
END;
/
CREATE OR REPLACE TRIGGER TRG_CamXoaKeToan_HoaDon
BEFORE DELETE ON HOADON
FOR EACH ROW
BEGIN

    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Don!');
END;
/

CREATE OR REPLACE TRIGGER TRG_CamXoaKeToan_PhieuThuChi
BEFORE DELETE ON PHIEUTHUCHI
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Phieu!');
END;
/