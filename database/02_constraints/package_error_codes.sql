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

    -- Lỗi hệ thống khi thanh toán giao dịch (dùng trong PROC_THANHTOANVATHANGHANG)
    ERR_THANH_TOAN_GIAO_DICH        CONSTANT NUMBER := -20002;

    -- Lỗi hệ thống giao dịch khi thực thi hủy đơn (dùng trong PROC_HUYDONVAHOANKHO)
    ERR_HUY_DON_GIAO_DICH           CONSTANT NUMBER := -20003;

    -- Lỗi hệ thống khi xuất kho sản xuất (dùng trong PROC_XUATKHOSANXUAT)
    ERR_HUY_XUAT_KHO                CONSTANT NUMBER := -20004;

    -- Lỗi hệ thống khi tạo đơn hàng (dùng trong PROC_TAODONHANG)
    ERR_HUY_TAO_DON                 CONSTANT NUMBER := -20005;


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

    -- Số lượng hủy vượt quá tồn kho hiện tại (dùng trong PROC_XUATHUYBANH)
    ERR_XUAT_HUY_BANH               CONSTANT NUMBER := -20203;


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

    -- Lỗi hệ thống khi thực thi Nhập kho (dùng trong PROC_NHAPKHO)
    ERR_NHAP_KHO                    CONSTANT NUMBER := -20308;

    -- Không đủ tồn kho nguyên liệu để xuất sản xuất (dùng trong PROC_XUATKHOSANXUAT)
    ERR_NL_KHONG_DU                 CONSTANT NUMBER := -20309;


    -- ========================
    -- NHÓM 04: ĐƠN HÀNG
    -- ========================

    -- Vượt công suất sản xuất trong ngày (dùng trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH, TRG_KIEMSOAT_CONGSUAT_DONHANG)
    ERR_DON_VUOT_CONG_SUAT          CONSTANT NUMBER := -20401;

    -- Không tìm thấy giới hạn sản xuất trong ngày (dùng trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH, TRG_KIEMSOAT_CONGSUAT_DONHANG)
    ERR_KHONG_GIOI_HAN_SX           CONSTANT NUMBER := -20402;

    -- Lỗi khi chuyển trạng thái đơn hàng (dùng trong PROC_CHUYENTRANGTHAIDON)
    ERR_DON_CHUYEN_TRANGTHAI        CONSTANT NUMBER := -20403;


    -- ========================
    -- NHÓM 05: TÀI CHÍNH
    -- ========================

    -- Vi phạm kế toán: cấm xóa vật lý hóa đơn (dùng trong TRG_CAMXOAKETOAN_HOADON)
    ERR_TC_CAM_XOA_HOADON           CONSTANT NUMBER := -20501;

    -- Vi phạm kế toán: cấm xóa vật lý phiếu thu chi (dùng trong TRG_CAMXOAKETOAN_PHIEUTHUCHI)
    ERR_TC_CAM_XOA_PHIEUTHUCHI      CONSTANT NUMBER := -20502;


    -- ========================
    -- NHÓM 06: CUD (CRUD API)
    -- ========================

    -- Nguyên liệu
    ERR_NL_THEM_MOI                 CONSTANT NUMBER := -20601;
    ERR_NL_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20602;
    ERR_NL_XOA_HE_THONG             CONSTANT NUMBER := -20603;

    -- Vai trò & Nhân viên
    ERR_VAITRO_THEM_HE_THONG        CONSTANT NUMBER := -20611;
    ERR_VAITRO_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20612;
    ERR_VAITRO_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20613;
    ERR_VAITRO_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20614;
    ERR_VAITRO_XOA_HE_THONG         CONSTANT NUMBER := -20615;
    ERR_NV_THEM_HE_THONG            CONSTANT NUMBER := -20616;
    ERR_NV_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20617;
    ERR_NV_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20618;
    ERR_NV_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20619;
    ERR_NV_XOA_HE_THONG             CONSTANT NUMBER := -20620;

    -- Khách hàng & Hạng thành viên
    ERR_HANGTV_THEM_HE_THONG        CONSTANT NUMBER := -20621;
    ERR_HANGTV_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20622;
    ERR_HANGTV_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20623;
    ERR_HANGTV_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20624;
    ERR_HANGTV_XOA_HE_THONG         CONSTANT NUMBER := -20625;
    ERR_KH_THEM_HE_THONG            CONSTANT NUMBER := -20626;
    ERR_KH_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20627;
    ERR_KH_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20628;
    ERR_KH_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20629;
    ERR_KH_XOA_HE_THONG             CONSTANT NUMBER := -20630;

    -- Thuộc tính sản phẩm
    ERR_KICHCO_KHONG_TON_TAI        CONSTANT NUMBER := -20631;
    ERR_COTBANH_KHONG_TON_TAI       CONSTANT NUMBER := -20632;
    ERR_NHANBANH_KHONG_TON_TAI      CONSTANT NUMBER := -20633;
    ERR_TRANGTRI_KHONG_TON_TAI      CONSTANT NUMBER := -20634;

    -- Danh mục & Sản phẩm
    ERR_DM_THEM_HE_THONG            CONSTANT NUMBER := -20641;
    ERR_DM_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20642;
    ERR_DM_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20643;
    ERR_DM_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20644;
    ERR_DM_XOA_HE_THONG             CONSTANT NUMBER := -20645;
    ERR_SANPHAM_THEM_HE_THONG       CONSTANT NUMBER := -20646;
    ERR_SANPHAM_KHONG_TON_TAI_CN    CONSTANT NUMBER := -20647;
    ERR_SANPHAM_CAPNHAT_HE_THONG    CONSTANT NUMBER := -20648;
    ERR_SANPHAM_KHONG_TON_TAI_XOA   CONSTANT NUMBER := -20649;
    ERR_SANPHAM_XOA_HE_THONG        CONSTANT NUMBER := -20650;

    -- Tài chính
    ERR_PTTT_THEM_HE_THONG          CONSTANT NUMBER := -20651;
    ERR_PTTT_KHONG_TON_TAI          CONSTANT NUMBER := -20652;
    ERR_LOAITHUCHI_KHONG_TON_TAI    CONSTANT NUMBER := -20653;
    ERR_PHIEUTHUCHI_THEM_HE_THONG   CONSTANT NUMBER := -20654;

    -- Ca làm việc
    ERR_CA_MO_HE_THONG              CONSTANT NUMBER := -20658;
    ERR_CA_DONG_HE_THONG            CONSTANT NUMBER := -20659;

END PKG_ERROR_CODES;
/
