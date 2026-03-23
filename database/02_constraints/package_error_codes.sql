-- ============================================================
-- PACKAGE: PKG_ERROR_CODES
-- MỤC ĐÍCH: Chuẩn hóa toàn bộ mã lỗi hệ thống quản lý tiệm bánh
-- PHÂN VÙNG:
--   -20000 ~ -20099 : Hệ thống / Dùng chung
--   -20100 ~ -20199 : Nhân sự & Khách hàng
--   -20200 ~ -20299 : Sản phẩm
--   -20300 ~ -20399 : Kho & Công thức
--   -20400 ~ -20499 : Đơn hàng
--   -20500 ~ -20599 : Tài chính
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_ERROR_CODES AS

    -- ========================
    -- NHÓM 00: HỆ THỐNG / DÙNG CHUNG
    -- ========================

    -- Lỗi hệ thống khi khôi phục dữ liệu (dùng trong PROC_KHOIPHUCDULIEU)
    ERR_HE_THONG_KHOIPHUC          CONSTANT NUMBER := -20001;


    -- ========================
    -- NHÓM 01: NHÂN SỰ & KHÁCH HÀNG
    -- ========================

    -- Ca làm việc không tồn tại hoặc đã đóng (dùng trong PROC_DONGCADOISOAT)
    ERR_CA_KHONG_TON_TAI            CONSTANT NUMBER := -20101;


    -- ========================
    -- NHÓM 02: SẢN PHẨM
    -- ========================

    -- Số lượng bánh trong kho đã hết (dùng trong TRG_TRUKHO_PHIEUXUATTP, TRG_TRUKHO_DONHANG)
    ERR_SP_HET_HANG                 CONSTANT NUMBER := -20201;

    -- Không tìm thấy sản phẩm trong hệ thống (dùng trong TRG_TRUKHO_PHIEUXUATTP, TRG_TRUKHO_DONHANG)
    ERR_SP_KHONG_TON_TAI            CONSTANT NUMBER := -20202;


    -- ========================
    -- NHÓM 03: KHO & CÔNG THỨC
    -- ========================

    -- Nguyên liệu không đạt chuẩn VSATTP (dùng trong TRG_KIEMTRAVSATTP)
    ERR_NL_KHONG_DAT_VSATTP         CONSTANT NUMBER := -20301;

    -- Phát hiện gian lận hạn sử dụng - HSD mới lớn hơn HSD cũ (dùng trong TRG_KIEMTRA_HSD)
    ERR_NL_GIAN_LAN_HSD             CONSTANT NUMBER := -20302;

    -- HSD của lô <= ngày nhập kho (dùng trong TRG_KIEMTRA_HSD)
    ERR_NL_HSD_KHONG_HOPLEQ         CONSTANT NUMBER := -20303;

    -- Không tìm thấy phiếu nhập kho (dùng trong TRG_KIEMTRA_HSD, TRG_TONGTIENNHAP)
    ERR_NL_KHONG_CO_PHIEUNHAP       CONSTANT NUMBER := -20304;

    -- Không tìm thấy lô hàng trong kho (dùng trong TRG_XUATSLNGUYENLIEU)
    ERR_NL_KHONG_CO_LO_HANG         CONSTANT NUMBER := -20305;

    -- Không tìm thấy nguyên liệu (dùng trong TRG_GIAVONTRUNGBINH_SOLUONGTONTONG)
    ERR_NL_KHONG_TON_TAI            CONSTANT NUMBER := -20306;

    -- Nguyên liệu đã được sử dụng, không thể hủy phiếu nhập (dùng trong PROC_HUYPHIEUNHAPKHO)
    ERR_NL_KHONG_THE_HUY_PN         CONSTANT NUMBER := -20307;


    -- ========================
    -- NHÓM 04: ĐƠN HÀNG
    -- ========================

    -- Vượt công suất sản xuất trong ngày (dùng trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH, TRG_KIEMSOAT_CONGSUAT_DONHANG)
    ERR_DON_VUOT_CONG_SUAT          CONSTANT NUMBER := -20401;

    -- Lỗi khi chuyển trạng thái đơn hàng (dùng trong PROC_CHUYENTRANGTHAIDON)
    ERR_DON_CHUYEN_TRANGTHAI        CONSTANT NUMBER := -20402;


    -- ========================
    -- NHÓM 05: TÀI CHÍNH
    -- ========================

    -- Vi phạm kế toán: cấm xóa vật lý hóa đơn (dùng trong TRG_CAMXOAKETOAN_HOADON)
    ERR_TC_CAM_XOA_HOADON           CONSTANT NUMBER := -20501;

    -- Vi phạm kế toán: cấm xóa vật lý phiếu thu chi (dùng trong TRG_CAMXOAKETOAN_PHIEUTHUCHI)
    ERR_TC_CAM_XOA_PHIEUTHUCHI      CONSTANT NUMBER := -20502;

END PKG_ERROR_CODES;
/
