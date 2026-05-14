-- Trigger cập nhật giá vốn trung bình và số lượng tồn tổng
-- IMP-08: Thêm xử lý UPDATE và DELETE (không chỉ INSERT) để đảm bảo data integrity
CREATE OR REPLACE TRIGGER TRG_GIAVONTRUNGBINH_SOLUONGTONTONG
AFTER INSERT OR UPDATE OR DELETE ON CTPHIEUNHAP
FOR EACH ROW
DECLARE
    V_TONCU NGUYENLIEU.SOLUONGTONTONG%type := 0;
    V_GIACU NGUYENLIEU.GIAVONTRUNGBINH%type := 0;
    V_TONGSOLUONGMOI NGUYENLIEU.SOLUONGTONTONG%type := 0;
    V_GIAVONMOI NGUYENLIEU.GIAVONTRUNGBINH%type := 0;
    V_MANL NGUYENLIEU.MANL%type;
    V_SOLUONG_DELTA NUMBER := 0;
    V_DONGIA_DELTA NUMBER := 0;
BEGIN
    -- 1. Xác định nguyên liệu và delta theo event
    IF INSERTING THEN
        V_MANL := :NEW.MANL;
        V_SOLUONG_DELTA := :NEW.SOLUONG;
        V_DONGIA_DELTA := :NEW.DONGIA;
    ELSIF UPDATING THEN
        -- Khi sửa chi tiết phiếu nhập: trừ cũ, cộng mới
        V_MANL := :NEW.MANL;
    ELSIF DELETING THEN
        -- Khi xóa chi tiết phiếu nhập (hủy phiếu): trừ lại số lượng
        V_MANL := :OLD.MANL;
        V_SOLUONG_DELTA := -(:OLD.SOLUONG);
    END IF;

    -- 2. Lấy tồn kho và giá vốn hiện tại
    SELECT NVL(SOLUONGTONTONG, 0), NVL(GIAVONTRUNGBINH, 0)
    INTO V_TONCU, V_GIACU
    FROM NGUYENLIEU
    WHERE MANL = V_MANL;

    -- 3. Tính toán WAC theo từng event
    IF INSERTING THEN
        V_TONGSOLUONGMOI := V_TONCU + V_SOLUONG_DELTA;
        IF V_TONGSOLUONGMOI > 0 THEN
            V_GIAVONMOI := ((V_TONCU * V_GIACU) + (V_SOLUONG_DELTA * V_DONGIA_DELTA)) / V_TONGSOLUONGMOI;
        ELSE
            V_GIAVONMOI := V_GIACU;
        END IF;

    ELSIF UPDATING THEN
        -- Recalc: loại bỏ lô cũ, thêm lô mới
        V_TONGSOLUONGMOI := V_TONCU - NVL(:OLD.SOLUONG, 0) + NVL(:NEW.SOLUONG, 0);
        IF V_TONGSOLUONGMOI > 0 THEN
            V_GIAVONMOI := (
                (V_TONCU * V_GIACU) - (NVL(:OLD.SOLUONG, 0) * NVL(:OLD.DONGIA, 0))
                + (NVL(:NEW.SOLUONG, 0) * NVL(:NEW.DONGIA, 0))
            ) / V_TONGSOLUONGMOI;
        ELSE
            V_GIAVONMOI := V_GIACU;
        END IF;

    ELSIF DELETING THEN
        V_TONGSOLUONGMOI := V_TONCU + V_SOLUONG_DELTA; -- delta âm
        IF V_TONGSOLUONGMOI > 0 THEN
            -- Khi xóa lô, recalc WAC: loại bỏ phần giá trị của lô bị xóa
            V_GIAVONMOI := ((V_TONCU * V_GIACU) - (NVL(:OLD.SOLUONG, 0) * NVL(:OLD.DONGIA, 0))) / V_TONGSOLUONGMOI;
        ELSIF V_TONGSOLUONGMOI = 0 THEN
            V_GIAVONMOI := 0; -- Hết hàng hoàn toàn → reset giá vốn
        ELSE
            V_GIAVONMOI := V_GIACU; -- Bảo vệ trường hợp bất thường
        END IF;
    END IF;

    -- 4. Cập nhật NGUYENLIEU
    UPDATE NGUYENLIEU
    SET GIAVONTRUNGBINH = V_GIAVONMOI,
        SOLUONGTONTONG = V_TONGSOLUONGMOI
    WHERE MANL = V_MANL;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(PKG_ERROR_CODES.ERR_KHONG_CO_NGUYEN_LIEU, 'Loi: Khong tim thay du lieu nguyen lieu.');
END;
/
