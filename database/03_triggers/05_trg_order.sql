-- Tự động cộng trừ tiền khi có thay đổi hóa đơn( Insert, update )
CREATE OR REPLACE TRIGGER TRG_CTDONHANG_UPDATE
AFTER INSERT OR UPDATE ON CTDONHANG
FOR EACH ROW
BEGIN
    UPDATE DONDATHANG
    SET TongTienHDBan =
        (
            SELECT SUM(SoLuong * DonGia)
            FROM CTDONHANG
            WHERE MaDon = :NEW.MaDon
        )
    WHERE MaDon = :NEW.MaDon;
END;
/

-- Tự động cộng trừ tiền khi có thay đổi khi có thay đổi hóa đơn( delete )
CREATE OR REPLACE TRIGGER TRG_CTDONHANG_DELETE
AFTER DELETE ON CTDONHANG
FOR EACH ROW
BEGIN
    UPDATE DONDATHANG
    SET TongTienHDBan =
    (
        SELECT NVL(SUM(SoLuong * DonGia),0)
        FROM CTDONHANG
        WHERE MaDon = :OLD.MaDon
    )
    WHERE MaDon = :OLD.MaDon;
END;
/