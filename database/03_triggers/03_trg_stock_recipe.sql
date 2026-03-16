--Trigger kiểm tra VSATTP (Rollback nếu không đạt chuẩn).
CREATE OR REPLACE TRIGGER TRG_KiemTraVSATTP
    BEFORE INSERT OR UPDATE ON NGUYENLIEU
    FOR EACH ROW
BEGIN
    IF :NEW.DATCHUANVSATTP = 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'NGUYEN_LIEU_KHONG_DAT_CHUAN: Nguyen lieu khong dat chuan, cam nhap kho');
    END IF;
END;

--Trigger Cấm xóa dữ liệu kế toán Bảng HOADON,
CREATE OR REPLACE TRIGGER TRG_CamXoaKeToan_HoaDon
BEFORE DELETE ON HOADON
FOR EACH ROW
BEGIN

    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Don!');
END;
/
--Trigger Cấm xóa dữ liệu kế toán Bảng PHIEUTHUCHI

CREATE OR REPLACE TRIGGER TRG_CamXoaKeToan_PhieuThuChi
BEFORE DELETE ON PHIEUTHUCHI
FOR EACH ROW
BEGIN
    RAISE_APPLICATION_ERROR(-20003, 'VI_PHAM_KE_TOAN: Cam tuyet doi xoa vat ly chung tu tai chinh. Vui long su dung chuc nang Huy Phieu!');
END;

--Trigger Chống gian lận hạn sử dụng (AFTER UPDATE CTPHIEUNHAP).
    CREATE OR REPLACE TRIGGER TRG_KhongDuocGiaHanHSD
        BEFORE UPDATE ON CTPHIEUNHAP
          FOR EACH ROW
BEGIN
    IF :NEW.HanSuDung > :OLD.HanSuDung THEN
        RAISE_APPLICATION_ERROR(-20002,'HAN_SU_DUNG_KHONG_THE_CHINH_SUA');

    end if;
end;





