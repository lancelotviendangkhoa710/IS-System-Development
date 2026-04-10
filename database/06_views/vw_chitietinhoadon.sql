-- Dữ liệu in hóa đơn nhiệt cho JasperReports
CREATE OR REPLACE VIEW VW_ChiTietInHoaDon AS
-- Các dòng bánh bán sẵn (từ CTDONHANG)
SELECT
    HD.MAHD,
    HD.NGAYXUATHD,
    HD.THUEVAT,
    HD.TONGTIENTHANHTOAN,
    PT.TENPTTT                          AS PHUONGTHUCTHANHTOAN,
    NV.HOTEN                            AS TENTHUNGAN,
    KH.HOTEN                            AS TENKHACHHANG,
    KH.SDT                              AS SDTKHACHHANG,
    SP.TENSP                            AS TENMON,
    CT.SOLUONG,
    CT.DONGIA,
    CT.PHANTRAMGIAM,
    ROUND(CT.SOLUONG * CT.DONGIA
        * (1 - NVL(CT.PHANTRAMGIAM,0)/100), 0)  AS THANHTIEN,
    CT.DONGIAVON,
    'THUONG'                            AS LOAIDON
FROM HOADON HD
JOIN DONDATHANG      DDH ON DDH.MADON   = HD.MADON
JOIN CALAMVIEC       CA  ON CA.MACA     = HD.MACA
JOIN NHANVIEN        NV  ON NV.MANV     = CA.MANV
JOIN PHUONGTHUCTT    PT  ON PT.MAPTTT   = HD.MAPTTT
LEFT JOIN KHACHHANG  KH  ON KH.MAKH     = DDH.MAKH
JOIN CTDONHANG       CT  ON CT.MADON    = DDH.MADON
JOIN SANPHAM         SP  ON SP.MASP     = CT.MASP

UNION ALL

-- Các dòng bánh tùy chỉnh (từ CTDONTUYCHINH)
SELECT
    HD.MAHD,
    HD.NGAYXUATHD,
    HD.THUEVAT,
    HD.TONGTIENTHANHTOAN,
    PT.TENPTTT                          AS PHUONGTHUCTHANHTOAN,
    NV.HOTEN                            AS TENTHUNGAN,
    KH.HOTEN                            AS TENKHACHHANG,
    KH.SDT                              AS SDTKHACHHANG,
    -- Ghép tên đầy đủ bánh tùy chỉnh thành 1 chuỗi mô tả
    SP.TENSP
        || ' (' || NVL(KC.TENKC,'')
        || ' - '|| NVL(CB.TENCOT,'')
        || ' - '|| NVL(NB.TENNHAN,'')
        || ' - '|| NVL(KT.TENTRANGTRI,'')
        || ')'                          AS TENBANH,
    TC.SOLUONG,
    TC.DONGIA,
    0                                   AS PHANTRAMGIAM,
    ROUND(TC.SOLUONG * TC.DONGIA, 0)    AS THANHTIEN,
    TC.DONGIAVON,
    'TUYCHINH'                          AS LOAIDON
FROM HOADON HD
JOIN DONDATHANG      DDH ON DDH.MADON     = HD.MADON
JOIN CALAMVIEC       CA  ON CA.MACA       = HD.MACA
JOIN NHANVIEN        NV  ON NV.MANV       = CA.MANV
JOIN PHUONGTHUCTT    PT  ON PT.MAPTTT     = HD.MAPTTT
LEFT JOIN KHACHHANG  KH  ON KH.MAKH       = DDH.MAKH
JOIN CTDONTUYCHINH   TC  ON TC.MADON      = DDH.MADON
JOIN SANPHAM         SP  ON SP.MASP       = TC.MASP
LEFT JOIN KICHCOBANH KC  ON KC.MAKC       = TC.MAKC
LEFT JOIN COTBANH    CB  ON CB.MACOT      = TC.MACOT
LEFT JOIN NHANBANH   NB  ON NB.MANHAN     = TC.MANHAN
LEFT JOIN KIEUTRANGTRI KT ON KT.MATRANGTRI = TC.MATRANGTRI;

