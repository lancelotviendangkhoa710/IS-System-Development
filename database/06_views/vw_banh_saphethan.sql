-- View Bánh thành phẩm sắp hết hạn và đã hết hạn — phục vụ cảnh báo Dashboard
-- =============================================================================
-- TÍCH HỢP JAVA (TODO): Tạo BangDieuKhienDAO.layBanhSapHetHan() SELECT từ view này.
-- Use-case: Widget cảnh báo trên Dashboard — hiển thị số lượng bánh sắp hết hạn.
-- Không xóa view: đang chờ implement feature Dashboard Alert.
-- Caller dự kiến: BangDieuKhienDAO → BangDieuKhienService → DashboardPresenter
-- =============================================================================
CREATE OR REPLACE VIEW V_BANH_SAPHETHAN AS
SELECT
    ME.MAME,
    SP.MASP,
    SP.TENSP,
    ME.SOLUONGSANXUAT,
    ME.SOLUONGCONLAI,
    ME.NGAYSANXUAT,
    ME.HANSUDUNG,
    TRUNC(ME.HANSUDUNG) - TRUNC(SYSDATE)   AS SO_NGAY_CON_LAI,
    CASE
        WHEN TRUNC(ME.HANSUDUNG) < TRUNC(SYSDATE) THEN N'Da het han'
        WHEN TRUNC(ME.HANSUDUNG) - TRUNC(SYSDATE) <= 1 THEN N'Het han hom nay/ngay mai'
        WHEN TRUNC(ME.HANSUDUNG) - TRUNC(SYSDATE) <= 3 THEN N'Sap het han (<=3 ngay)'
        ELSE N'Binh thuong'
    END                                    AS TRANG_THAI_HSD,
    NV.HOTEN                               AS TEN_NGUOI_LAM
FROM MESANXUAT ME
JOIN SANPHAM   SP ON ME.MASP = SP.MASP
JOIN NHANVIEN  NV ON ME.MANV = NV.MANV
WHERE NVL(ME.SOLUONGCONLAI, 0) > 0
  AND TRUNC(ME.HANSUDUNG) - TRUNC(SYSDATE) <= 3
ORDER BY SO_NGAY_CON_LAI ASC;
