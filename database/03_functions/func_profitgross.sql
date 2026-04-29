-- Hàm tính lợi nhuận gộp thực tế của từng hóa đơn
CREATE OR REPLACE FUNCTION FUNC_LOINHUANGOP(
    P_MAHD IN HOADON.MAHD%type
) RETURN NUMBER
IS
    V_MADON NUMBER;
    V_LAI_THUONG NUMBER := 0;
    V_LAI_TUYCHINH NUMBER := 0;
    V_LOINHUANGOP NUMBER := 0;
BEGIN
    -- 1. Truy ngược chứng từ: Lấy MADON từ HOADON
    BEGIN
        SELECT MADON INTO V_MADON
        FROM HOADON
        WHERE MAHD = P_MAHD;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
    END;

    -- Nếu đơn hàng bán lẻ không liên kết qua DONDATHANG
    IF V_MADON IS NULL THEN
        RETURN 0; 
    END IF;

    -- 2. Quét lợi nhuận dòng bánh Bán sẵn
    SELECT NVL(SUM((DONGIA - NVL(DONGIAVON, 0)) * SOLUONG), 0)
    INTO V_LAI_THUONG
    FROM CTDONHANG
    WHERE MADON = V_MADON;

    -- 3. Quét lợi nhuận dòng bánh Tùy chỉnh
    SELECT NVL(SUM((DONGIA - NVL(DONGIAVON, 0)) * SOLUONG), 0)
    INTO V_LAI_TUYCHINH
    FROM CTDONTUYCHINH
    WHERE MADON = V_MADON;

    -- 4. Thực thi công thức chốt
    V_LOINHUANGOP := V_LAI_THUONG + V_LAI_TUYCHINH;

    RETURN V_LOINHUANGOP;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/
