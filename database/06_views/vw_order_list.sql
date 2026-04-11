-- Màn hình bếp hiển thị danh sách đơn cần làm, sắp xếp theo ưu tiên
CREATE OR REPLACE VIEW VW_KDS_DonCanLam AS
SELECT
    DDH.MADON,
    DDH.NGAYLAP,
    DDH.NGAYGIONHANBANH,
    TT.TENTRANGTHAI                             AS TRANGTHAI,
    CASE DDH.HINHTHUCNHAN
        WHEN 1 THEN N'Tại quầy'
        WHEN 2 THEN N'Giao tận nơi'
    END                                         AS HINHTHUCNHAN,
    KH.HOTEN                                    AS TENKHACHHANG,
    KH.SDT                                      AS SDTKHACHHANG,
    -- Mô tả yêu cầu bánh (ưu tiên hiển thị trên màn hình bếp)
    SP.TENSP                                    AS TENBANH,
    TC.SOLUONG,
    KC.TENKC                                    AS KICHCO,
    CB.TENCOT                                   AS COTBANH,
    NB.TENNHAN                                  AS NHANBANH,
    KT.TENTRANGTRI                              AS KIEUTRANGTRI,
    TC.LOICHUCTRENBANH,
    TC.GHICHUTHOBANH,
    TC.THOIGIANCHUANBI,
    -- Ngày sản xuất thực tế = ngày giao - thời gian chuẩn bị
    TRUNC(DDH.NGAYGIONHANBANH)
        - NVL(TC.THOIGIANCHUANBI, 0)            AS NGAYSXTHUCTE,
    -- Cờ khẩn cấp: đơn cần làm trong vòng 24 giờ tới
    CASE
        WHEN TRUNC(DDH.NGAYGIONHANBANH)
             - NVL(TC.THOIGIANCHUANBI, 0)
             <= TRUNC(SYSDATE) + 1
        THEN 1 ELSE 0
    END                                         AS KHANCAP
FROM DONDATHANG        DDH
JOIN TRANGTHAIDON      TT  ON TT.MATRANGTHAI  = DDH.MATRANGTHAI
LEFT JOIN KHACHHANG    KH  ON KH.MAKH         = DDH.MAKH
JOIN CTDONTUYCHINH     TC  ON TC.MADON        = DDH.MADON
JOIN SANPHAM           SP  ON SP.MASP         = TC.MASP
LEFT JOIN KICHCOBANH   KC  ON KC.MAKC         = TC.MAKC
LEFT JOIN COTBANH      CB  ON CB.MACOT        = TC.MACOT
LEFT JOIN NHANBANH     NB  ON NB.MANHAN       = TC.MANHAN
LEFT JOIN KIEUTRANGTRI KT  ON KT.MATRANGTRI   = TC.MATRANGTRI
WHERE TT.TENTRANGTHAI IN (
    N'Mới đặt', N'Đã cọc', N'Đang sản xuất'
)
ORDER BY
    KHANCAP DESC,           -- Đơn khẩn lên trước
    NGAYSXTHUCTE ASC,      -- Đơn gần deadline lên trước
    DDH.NGAYLAP ASC;          -- Cùng ngày thì đơn cũ hơn lên trước