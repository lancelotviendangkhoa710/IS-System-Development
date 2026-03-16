CREATE OR REPLACE FUNCTION FUNC_TinhTienMatLyTuong (
    p_MaCa IN NUMBER,
    p_TienKhaiBaoDauCa IN NUMBER
) RETURN NUMBER
IS
    v_TongThuBanHang NUMBER(15,2) := 0;
    v_TongThuKhac NUMBER(15,2) := 0;
    v_TongChiKhac NUMBER(15,2) := 0;
    v_TienMatLyTuong NUMBER(15,2) := 0;
BEGIN
    -- Tính Tổng Thu từ Bán hàng (Chỉ lấy hóa đơn Tiền mặt)
    --NVL là hàm nếu truy vấn ra null thì sẽ gán kết quả select là 0
    SELECT NVL(SUM(HD.TONGTIENTHANHTOAN), 0)
    INTO v_TongThuBanHang
    FROM HOADON HD
    JOIN PHUONGTHUCTT PT ON HD.MAPTTT = PT.MAPTTT
    WHERE HD.MACA = p_MaCa
      AND (PT.TENPTTT = 'Tiền mặt' );

    -- Tính Tổng Thu Khác (Phiếu Thu không phải từ hóa đơn bán hàng)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO v_TongThuKhac
    FROM PHIEUTHUCHI PTC
    JOIN LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE PTC.MACA = p_MaCa
      AND LTC.PHANLOAI = 'Thu'; -- Thu (IN)

    -- Tính Tổng Chi Khác (Phiếu Chi ra khỏi két)
    SELECT NVL(SUM(PTC.SOTIEN), 0)
    INTO v_TongChiKhac
    FROM PHIEUTHUCHI PTC
    JOIN LOAITHUCHI LTC ON PTC.MALOAITHUCHI = LTC.MALOAITHUCHI
    WHERE PTC.MACA = p_MaCa
      AND LTC.PHANLOAI = 'Chi'; -- Chi (OUT)

    -- Thực thi công thức chốt
    v_TienMatLyTuong := p_TienKhaiBaoDauCa + v_TongThuBanHang + v_TongThuKhac - v_TongChiKhac;

    RETURN v_TienMatLyTuong;
END;
/