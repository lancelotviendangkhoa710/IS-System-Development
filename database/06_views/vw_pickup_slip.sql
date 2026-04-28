CREATE OR REPLACE VIEW VW_PHIEU_HEN_LAY_BANH AS
-- PHẦN 1: LẤY BÁNH TÙY CHỈNH (CUSTOM CAKES)
SELECT
    DDH.MADON,
    DDH.NGAYLAP,
    DDH.NGAYGIONHANBANH,
    DDH.TONGTIENHDBAN,
    DDH.TIENDACOC,
    (DDH.TONGTIENHDBAN - NVL(DDH.TIENDACOC, 0))  AS TIENCONLAI,
    CASE DDH.HINHTHUCNHAN
        WHEN 1 THEN N'Tại quầy'
        WHEN 2 THEN N'Giao tận nơi'
        ELSE N'Chưa xác định'
        END                                           AS HINHTHUCNHAN,
    DDH.DIACHIGIAO,
    TT.TENTRANGTHAI                               AS TRANGTHAIDON,
    KH.HOTEN                                      AS TENKHACHHANG,
    KH.SDT                                        AS SDTKHACHHANG,
    NV.HOTEN                                      AS TENNHANVIEN,
    SP.TENSP                                      AS TENBANH,
    KC.TENKC                                      AS KICHCO,
    CB.TENCOT                                     AS COTBANH,
    NB.TENNHAN                                    AS NHANBANH,
    KT.TENTRANGTRI                                AS KIEUTRANGTRI,
    TC.LOICHUCTRENBANH,
    TC.GHICHUTHOBANH,
    TC.SOLUONG,
    TC.DONGIA,
    TC.THOIGIANCHUANBI
FROM DONDATHANG       DDH
         JOIN TRANGTHAIDON     TT  ON TT.MATRANGTHAI  = DDH.MATRANGTHAI
         JOIN NHANVIEN         NV  ON NV.MANV         = DDH.MANV_LAP
         LEFT JOIN KHACHHANG   KH  ON KH.MAKH         = DDH.MAKH
         JOIN CTDONTUYCHINH    TC  ON TC.MADON        = DDH.MADON
         JOIN SANPHAM          SP  ON SP.MASP         = TC.MASP
         LEFT JOIN KICHCOBANH  KC  ON KC.MAKC         = TC.MAKC
         LEFT JOIN COTBANH     CB  ON CB.MACOT        = TC.MACOT
         LEFT JOIN NHANBANH    NB  ON NB.MANHAN       = TC.MANHAN
         LEFT JOIN KIEUTRANGTRI KT ON KT.MATRANGTRI   = TC.MATRANGTRI

UNION ALL

-- PHẦN 2: LẤY BÁNH PHỔ THÔNG (REGULAR PRODUCTS)
SELECT
    DDH.MADON,
    DDH.NGAYLAP,
    DDH.NGAYGIONHANBANH,
    DDH.TONGTIENHDBAN,
    DDH.TIENDACOC,
    (DDH.TONGTIENHDBAN - NVL(DDH.TIENDACOC, 0))  AS TIENCONLAI,
    CASE DDH.HINHTHUCNHAN
        WHEN 1 THEN N'Tại quầy'
        WHEN 2 THEN N'Giao tận nơi'
        ELSE N'Chưa xác định'
        END                                           AS HINHTHUCNHAN,
    DDH.DIACHIGIAO,
    TT.TENTRANGTHAI                               AS TRANGTHAIDON,
    KH.HOTEN                                      AS TENKHACHHANG,
    KH.SDT                                        AS SDTKHACHHANG,
    NV.HOTEN                                      AS TENNHANVIEN,
    SP.TENSP                                      AS TENBANH,
    NULL                                          AS KICHCO,
    NULL                                          AS COTBANH,
    NULL                                          AS NHANBANH,
    NULL                                          AS KIEUTRANGTRI,
    NULL                                          AS LOICHUCTRENBANH,
    NULL                                          AS GHICHUTHOBANH,
    CT.SOLUONG,
    CT.DONGIA,
    0                                             AS THOIGIANCHUANBI
FROM DONDATHANG       DDH
         JOIN TRANGTHAIDON     TT  ON TT.MATRANGTHAI  = DDH.MATRANGTHAI
         JOIN NHANVIEN         NV  ON NV.MANV         = DDH.MANV_LAP
         LEFT JOIN KHACHHANG   KH  ON KH.MAKH         = DDH.MAKH
         JOIN CTDONHANG        CT  ON CT.MADON        = DDH.MADON
         JOIN SANPHAM          SP  ON SP.MASP         = CT.MASP;
