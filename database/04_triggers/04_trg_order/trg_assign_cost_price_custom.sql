    -- Trigger gán giá vốn tại thời điểm bán cho chi tiết đơn tùy chỉnh
    CREATE OR REPLACE TRIGGER TRG_CTDONTUYCHINH_GIAVON
    BEFORE INSERT ON CTDONTUYCHINH
    FOR EACH ROW
    BEGIN
        :NEW.DONGIAVON := FUNC_TinhGiaVonDong(:NEW.MASP);
    END;
    /