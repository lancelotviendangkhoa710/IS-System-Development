-- Hàm tính tổng tiền mặt lý tưởng của một ca làm việc để đối soát
CREATE OR REPLACE FUNCTION FUNC_TIENMATLYTUONG(
    P_MACA IN CALAMVIEC.MACA%type
)
RETURN NUMBER
IS
    V_TONGTIEN NUMBER := 0;
    V_TIEN_DAUCA NUMBER := 0;
    V_TIEN_HOADON NUMBER := 0;
    V_TIEN_THU NUMBER := 0;
    V_TIEN_CHI NUMBER := 0;
BEGIN
    -- 0. Lấy Tiền khai báo đầu ca (Declared Cash) từ bảng DOISOAT
    SELECT NVL(MAX(TIENKHAIBAODAUCA), 0)
    INTO V_TIEN_DAUCA
    FROM DOISOAT
    WHERE MACA = P_MACA;

    -- 1. Tính tổng tiền từ các Hóa đơn thanh toán bằng Tiền mặt trong ca (Total Sales Revenue)
    SELECT NVL(SUM(HD.TONGTIENTHANHTOAN), 0)
    INTO V_TIEN_HOADON
    FROM HOADON HD
    JOIN PHUONGTHUCTT PT ON HD.MAPTTT = PT.MAPTTT
    WHERE HD.MACA = P_MACA
      AND UPPER(PT.TENPTTT) LIKE '%TIỀN MẶT%';

    -- 2. Tính tổng tiền từ các Phiếu Thu trong ca (Other Revenue)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO V_TIEN_THU
    FROM PHIEUTHUCHI PTC
    JOIN LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE PTC.MACA = P_MACA
      AND UPPER(LTC.PHANLOAI) = 'THU';

    -- 3. Tính tổng tiền từ các Phiếu Chi trong ca (Other Expenses)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO V_TIEN_CHI
    FROM PHIEUTHUCHI PTC
    JOIN LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE PTC.MACA = P_MACA
      AND UPPER(LTC.PHANLOAI) = 'CHI';

    -- 4. Tổng kết: Ideal Cash = [Declared Cash] + [Total Sales Revenue] + [Other Revenue] - [Other Expenses]
    V_TONGTIEN := V_TIEN_DAUCA + V_TIEN_HOADON + V_TIEN_THU - V_TIEN_CHI;

    RETURN V_TONGTIEN;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;

/
