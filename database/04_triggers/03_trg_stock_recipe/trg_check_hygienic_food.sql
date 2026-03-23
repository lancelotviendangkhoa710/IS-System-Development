-- Trigger kiểm tra vệ sinh an toàn thực phẩm
    CREATE OR REPLACE TRIGGER TRG_KIEMTRAVSATTP
    BEFORE INSERT OR UPDATE ON NGUYENLIEU
    FOR EACH ROW
    BEGIN
        IF :NEW.DATCHUANVSATTP = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'NGUYEN_LIEU_KHONG_DAT_CHUAN: Nguyen lieu khong dat chuan, cam nhap kho');
        END IF;
    END;
    /