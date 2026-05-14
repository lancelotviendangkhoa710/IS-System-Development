-- Hàm tính tổng tiền mặt lý tưởng của một ca làm việc để đối soát
-- IMP-02: Gộp 2 query phiếu thu/chi thành 1 bằng conditional SUM (4 query → 3 query)
-- IMP-04: Giữ P_TIENKHAIBAODAUCA (deprecated) để không phá Java caller DoiSoatDAO
CREATE OR REPLACE FUNCTION FUNC_TINHTIENMATLYTUONG(
    P_MACA             IN CALAMVIEC.MACA%TYPE,
    P_TIENKHAIBAODAUCA IN NUMBER DEFAULT 0  -- Deprecated: không dùng, tự đọc từ DOISOAT
)
RETURN NUMBER
IS
    V_TONGTIEN    NUMBER := 0;
    V_TIEN_DAUCA  NUMBER := 0;
    V_TIEN_HOADON NUMBER := 0;
    V_TIEN_THU    NUMBER := 0;
    V_TIEN_CHI    NUMBER := 0;
BEGIN
    -- 0. Tiền khai báo đầu ca từ DOISOAT (tự đọc, không dùng P_TIENKHAIBAODAUCA)
    SELECT NVL(MAX(TIENKHAIBAODAUCA), 0)
    INTO   V_TIEN_DAUCA
    FROM   DOISOAT
    WHERE  MACA = P_MACA;

    -- 1. Tổng tiền hóa đơn thanh toán bằng tiền mặt trong ca (loại trừ hóa đơn đã huỷ)
    SELECT NVL(SUM(HD.TONGTIENTHANHTOAN), 0)
    INTO   V_TIEN_HOADON
    FROM   HOADON HD
    JOIN   PHUONGTHUCTT PT ON HD.MAPTTT = PT.MAPTTT
    WHERE  HD.MACA = P_MACA
      AND  UPPER(PT.TENPTTT) LIKE '%TIỀN MẶT%'
      AND  NVL(UPPER(HD.TRANGTHAI), 'ACTIVE') != 'CANCELLED';

    -- 2. Tổng tiền thu và chi trong ca — gộp 1 query bằng conditional SUM
    SELECT
        NVL(SUM(CASE WHEN UPPER(LTC.PHANLOAI) = 'THU' THEN PTC.SOTIEN ELSE 0 END), 0),
        NVL(SUM(CASE WHEN UPPER(LTC.PHANLOAI) = 'CHI' THEN PTC.SOTIEN ELSE 0 END), 0)
    INTO V_TIEN_THU, V_TIEN_CHI
    FROM   PHIEUTHUCHI PTC
    JOIN   LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE  PTC.MACA = P_MACA
      AND  NVL(UPPER(PTC.TRANGTHAI), 'ACTIVE') != 'CANCELLED';

    -- 3. Tiền mặt lý tưởng = Đầu ca + Hóa đơn tiền mặt + Thu - Chi
    V_TONGTIEN := V_TIEN_DAUCA + V_TIEN_HOADON + V_TIEN_THU - V_TIEN_CHI;

    RETURN V_TONGTIEN;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/
