-- Hàm tính tổng tiền mặt lý tưởng của một ca làm việc để đối soát
-- Tên khớp với DoiSoatDAO.tinhTienMatLyTuong() trong Java
-- Tham số P_TIENKHAIBAODAUCA nhận từ Java nhưng không dùng vì
-- hàm tự đọc từ DOISOAT để đảm bảo tính toàn vẹn dữ liệu.
CREATE OR REPLACE FUNCTION FUNC_TINHTIENMATLYTUONG(
    P_MACA             IN CALAMVIEC.MACA%TYPE,
    P_TIENKHAIBAODAUCA IN NUMBER DEFAULT 0
)
RETURN NUMBER
IS
    V_TONGTIEN    NUMBER := 0;
    V_TIEN_DAUCA  NUMBER := 0;
    V_TIEN_HOADON NUMBER := 0;
    V_TIEN_THU    NUMBER := 0;
    V_TIEN_CHI    NUMBER := 0;
BEGIN
    -- 0. Tiền khai báo đầu ca từ DOISOATA
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

    -- 2. Tổng tiền thu khác trong ca (loại trừ phiếu đã huỷ)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO   V_TIEN_THU
    FROM   PHIEUTHUCHI PTC
    JOIN   LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE  PTC.MACA = P_MACA
      AND  UPPER(LTC.PHANLOAI) = 'THU'
      AND  NVL(UPPER(PTC.TRANGTHAI), 'ACTIVE') != 'CANCELLED';

    -- 3. Tổng tiền chi trong ca (loại trừ phiếu đã huỷ)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO   V_TIEN_CHI
    FROM   PHIEUTHUCHI PTC
    JOIN   LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE  PTC.MACA = P_MACA
      AND  UPPER(LTC.PHANLOAI) = 'CHI'
      AND  NVL(UPPER(PTC.TRANGTHAI), 'ACTIVE') != 'CANCELLED';

    -- 4. Tiền mặt lý tưởng = Đầu ca + Thu - Chi
    V_TONGTIEN := V_TIEN_DAUCA + V_TIEN_HOADON + V_TIEN_THU - V_TIEN_CHI;

    RETURN V_TONGTIEN;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 0;
END;
/

