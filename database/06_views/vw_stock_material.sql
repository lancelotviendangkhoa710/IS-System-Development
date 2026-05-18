-- Lịch sử toàn bộ biến động nhập/xuất của từng nguyên liệu
CREATE OR REPLACE VIEW VW_THE_KHO_NGUYEN_LIEU AS
-- Các dòng nhập kho
SELECT
    NL.MANL,
    NL.TENNL,
    DVT.TENDVT                              AS DVT,
    PN.NGAYNHAP                             AS THOIDIEM,
    'NHAP'                                  AS LOAIBIENDONG,
    PN.MAPN                                 AS MACHUNGTU,
    NCC.TENNCC                              AS NGUON,
    CT.SOLUONG,
    CT.DONGIA,
    CT.SOLUONG * CT.DONGIA                  AS THANHTIEN,
    CT.NGAYSANXUAT,
    CT.HANSUDUNG,
    CT.MAVACH_LO,
    CT.MALO                                 AS MALO,
    CT.SOLUONGCONLAI                        AS SOLUONGCONLAI
FROM NGUYENLIEU       NL
JOIN DONVITINH        DVT ON DVT.MADVT  = NL.MADVT
JOIN CTPHIEUNHAP      CT  ON CT.MANL    = NL.MANL
JOIN PHIEUNHAPKHO     PN  ON PN.MAPN    = CT.MAPN
JOIN NHACUNGCAP       NCC ON NCC.MANCC  = PN.MANCC

UNION ALL

-- Các dòng xuất kho (xuất sản xuất)
SELECT
    NL.MANL,
    NL.TENNL,
    DVT.TENDVT                              AS DVT,
    PX.NGAYXUAT                             AS THOIDIEM,
    'XUAT_SX'                               AS LOAIBIENDONG,
    PX.MAPX                                 AS MACHUNGTU,
    N'Xuất sản xuất'                        AS NGUON,
    CX.SOLUONG,
    -- Giá vốn tại thời điểm xuất (lấy từ lô hàng gốc)
    CTN.DONGIA                              AS DONGIA,
    CX.SOLUONG * CTN.DONGIA                 AS THANHTIEN,
    CTN.NGAYSANXUAT,
    CTN.HANSUDUNG,
    CTN.MAVACH_LO,
    CTN.MALO                                AS MALO,
    CTN.SOLUONGCONLAI                       AS SOLUONGCONLAI
FROM NGUYENLIEU       NL
JOIN DONVITINH        DVT ON DVT.MADVT   = NL.MADVT
JOIN CTPHIEUNHAP      CTN ON CTN.MANL    = NL.MANL
JOIN CTPHIEUXUAT_NL   CX  ON CX.MALO     = CTN.MALO
JOIN PHIEUXUATKHO     PX  ON PX.MAPX     = CX.MAPX;