-- Trigger tự động tính lại giá vốn và giá bán khi công thức nguyên liệu thay đổi
CREATE OR REPLACE TRIGGER TRG_TINHLAI_GIASP
AFTER INSERT OR UPDATE OR DELETE ON CONGTHUC
FOR EACH ROW
DECLARE
    V_MASP       NUMBER;
    V_GIAVON_MOI NUMBER := 0;
    V_GIABAN_MOI NUMBER := 0;
BEGIN
    -- 1. Xác định MASP bị ảnh hưởng (ưu tiên :NEW, fallback :OLD khi DELETE)
    IF DELETING THEN
        V_MASP := :OLD.MASP;
    ELSE
        V_MASP := :NEW.MASP;
    END IF;

    -- 2. Tính lại tổng giá vốn từ toàn bộ công thức của sản phẩm đó
    V_GIAVON_MOI := FUNC_TONGGIAVON(V_MASP);

    -- 3. Tính giá bán = giá vốn × 2, làm tròn đến nghìn gần nhất
    V_GIABAN_MOI := ROUND(V_GIAVON_MOI * 2, -3);

    -- 4. Cập nhật ngược lên SANPHAM
    UPDATE SANPHAM
    SET GIAVON  = V_GIAVON_MOI,
        GIABAN  = V_GIABAN_MOI,
        PHIENBAN = PHIENBAN + 1
    WHERE MASP = V_MASP;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20100,
            'Loi khi tinh gia ban cho san pham ' || V_MASP || ': ' || SQLERRM);
END;
/
