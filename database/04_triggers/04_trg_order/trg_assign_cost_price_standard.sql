   -- Trigger gán giá vốn tại thời điểm bán cho chi tiết đơn hàng
    CREATE OR REPLACE TRIGGER TRG_GAN_GIAVON_BAN

    BEFORE INSERT ON CTDONHANG
    FOR EACH ROW
    BEGIN
        :NEW.DONGIAVON := FUNC_TinhGiaVonDong(:NEW.MASP);
    END;
    /
