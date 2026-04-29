   -- Trigger gán giá vốn tại thời điểm bán cho chi tiết đơn hàng
    CREATE OR REPLACE TRIGGER TRG_CTDONHANG_GIAVON
    BEFORE INSERT ON CTDONHANG
    FOR EACH ROW
    BEGIN
        :NEW.DONGIAVON := FUNC_TONGGIAVON(:NEW.MASP);
    END;
    /
