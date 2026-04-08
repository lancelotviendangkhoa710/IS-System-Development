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

    -- Lỗi INSERT DONDATHANG (dùng trong PROC_DONDATHANG_INSERT)
    ERR_DON_INSERT                  CONSTANT NUMBER := -20001;

    -- Lỗi UPDATE DONDATHANG - Xung đột dữ liệu (dùng trong PROC_DONDATHANG_UPDATE)
    ERR_DON_UPDATE_CONFLICT         CONSTANT NUMBER := -20002;

    -- Lỗi UPDATE DONDATHANG (dùng trong PROC_DONDATHANG_UPDATE)
    ERR_DON_UPDATE                  CONSTANT NUMBER := -20003;

    -- Lỗi DELETE DONDATHANG (dùng trong PROC_DONDATHANG_DELETE)
    ERR_DON_DELETE                  CONSTANT NUMBER := -20004;

    -- Lỗi INSERT CTDONHANG (dùng trong PROC_CTDONHANG_INSERT)
    ERR_CTDON_INSERT                CONSTANT NUMBER := -20005;

    -- Lỗi UPDATE CTDONHANG (dùng trong PROC_CTDONHANG_UPDATE)
    ERR_CTDON_UPDATE                CONSTANT NUMBER := -20006;

    -- Lỗi DELETE CTDONHANG (dùng trong PROC_CTDONHANG_DELETE)
    ERR_CTDON_DELETE                CONSTANT NUMBER := -20007;

    -- Lỗi INSERT CTDONTUYCHINH (dùng trong PROC_CTDONTUYCHINH_INSERT)
    ERR_CTDONTC_INSERT              CONSTANT NUMBER := -20008;

    -- Lỗi UPDATE CTDONTUYCHINH (dùng trong PROC_CTDONTUYCHINH_UPDATE)
    ERR_CTDONTC_UPDATE              CONSTANT NUMBER := -20009;

    -- Lỗi DELETE CTDONTUYCHINH (dùng trong PROC_CTDONTUYCHINH_DELETE)
    ERR_CTDONTC_DELETE              CONSTANT NUMBER := -20010;

    -- Lỗi INSERT HOADON (dùng trong PROC_HOADON_INSERT)
    ERR_HD_INSERT                   CONSTANT NUMBER := -20011;

    -- Lỗi UPDATE HOADON (dùng trong PROC_HOADON_UPDATE)
    ERR_HD_UPDATE                   CONSTANT NUMBER := -20012;

    -- Lỗi DELETE HOADON (dùng trong PROC_HOADON_DELETE)
    ERR_HD_DELETE                   CONSTANT NUMBER := -20013;

    -- Lỗi PHIEUTHUCHI: validation (dùng trong PROC_PHIEUTHUCHI_INSERT)
    ERR_TC_PHIEUTHUCHI_VALIDATION   CONSTANT NUMBER := -20014;

    -- Lỗi INSERT PHIEUTHUCHI (dùng trong PROC_PHIEUTHUCHI_INSERT)
    ERR_TC_PHIEUTHUCHI_INSERT       CONSTANT NUMBER := -20015;

    -- Lỗi UPDATE PHIEUTHUCHI (dùng trong PROC_PHIEUTHUCHI_UPDATE)
    ERR_TC_PHIEUTHUCHI_UPDATE       CONSTANT NUMBER := -20016;

    -- Lỗi DELETE PHIEUTHUCHI (dùng trong PROC_PHIEUTHUCHI_DELETE)
    ERR_TC_PHIEUTHUCHI_DELETE       CONSTANT NUMBER := -20017;

    -- Lỗi SANPHAM: validation giá cơ bản (dùng trong PROC_SANPHAM_INSERT)
    ERR_SP_GIACOBAN_VALIDATION      CONSTANT NUMBER := -20018;

    -- Lỗi INSERT SANPHAM (dùng trong PROC_SANPHAM_INSERT)
    ERR_SP_INSERT                   CONSTANT NUMBER := -20019;

    -- Lỗi UPDATE SANPHAM - Xung đột dữ liệu (dùng trong PROC_SANPHAM_UPDATE)
    ERR_SP_UPDATE_CONFLICT          CONSTANT NUMBER := -20020;

    -- Lỗi UPDATE SANPHAM (dùng trong PROC_SANPHAM_UPDATE)
    ERR_SP_UPDATE                   CONSTANT NUMBER := -20021;

    -- Lỗi DELETE SANPHAM (dùng trong PROC_SANPHAM_DELETE)
    ERR_SP_DELETE                   CONSTANT NUMBER := -20022;

    -- Lỗi INSERT NGUYENLIEU (dùng trong PROC_NGUYENLIEU_INSERT)
    ERR_NL_INSERT                   CONSTANT NUMBER := -20023;

    -- Lỗi UPDATE NGUYENLIEU - Xung đột dữ liệu (dùng trong PROC_NGUYENLIEU_UPDATE)
    ERR_NL_UPDATE_CONFLICT          CONSTANT NUMBER := -20024;

    -- Lỗi UPDATE NGUYENLIEU (dùng trong PROC_NGUYENLIEU_UPDATE)
    ERR_NL_UPDATE                   CONSTANT NUMBER := -20025;

    -- Lỗi DELETE NGUYENLIEU (dùng trong PROC_NGUYENLIEU_DELETE)
    ERR_NL_DELETE                   CONSTANT NUMBER := -20026;

    -- Lỗi INSERT PHIEUNHAPKHO (dùng trong PROC_PHIEUNHAPKHO_INSERT)
    ERR_PNK_INSERT                  CONSTANT NUMBER := -20027;

    -- Lỗi UPDATE PHIEUNHAPKHO (dùng trong PROC_PHIEUNHAPKHO_UPDATE)
    ERR_PNK_UPDATE                  CONSTANT NUMBER := -20028;

    -- Lỗi DELETE PHIEUNHAPKHO: validation (dùng trong PROC_PHIEUNHAPKHO_DELETE)
    ERR_PNK_DELETE_VALIDATION       CONSTANT NUMBER := -20029;

    -- Lỗi DELETE PHIEUNHAPKHO (dùng trong PROC_PHIEUNHAPKHO_DELETE)
    ERR_PNK_DELETE                  CONSTANT NUMBER := -20030;

    -- Lỗi xuất kho: không đủ nguyên liệu (dùng trong PROC_XUATKHOSANXUAT)
    ERR_XUAT_KHO_KHONG_DU_NL        CONSTANT NUMBER := -20031;


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
    ERR_NL_HSD_KHONG_HOPLE         CONSTANT NUMBER := -20303;

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

    -- Không tìm thấy giới hạn sản xuất trong ngày (dùng trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH, TRG_KIEMSOAT_CONGSUAT_DONHANG)
    ERR_KHONG_GIOI_HAN_SX           CONSTANT NUMBER := -20402;

    -- Lỗi khi chuyển trạng thái đơn hàng (dùng trong PROC_CHUYENTRANGTHAIDON)
    ERR_DON_CHUYEN_TRANGTHAI        CONSTANT NUMBER := -20403;
    -- Lỗi quy trình thanh toán
    ERR_QUY_TRINH_THANH_TOAN        CONSTANT NUMBER := -20404;

    -- ========================
    -- NHÓM 05: TÀI CHÍNH
    -- ========================

    -- Vi phạm kế toán: cấm xóa vật lý hóa đơn (dùng trong TRG_CAMXOAKETOAN_HOADON)
    ERR_TC_CAM_XOA_HOADON           CONSTANT NUMBER := -20501;

    -- Vi phạm kế toán: cấm xóa vật lý phiếu thu chi (dùng trong TRG_CAMXOAKETOAN_PHIEUTHUCHI)
    ERR_TC_CAM_XOA_PHIEUTHUCHI      CONSTANT NUMBER := -20502;

END PKG_ERROR_CODES;
/
