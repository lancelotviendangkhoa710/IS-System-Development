-- ============================================================
-- PACKAGE: PKG_ERROR_CODES
-- MỤC ĐÍCH: Chuẩn hóa toàn bộ mã lỗi hệ thống quản lý tiệm bánh
-- PHÂN VÙNG:
--   -20000 ~ -20099 : Hệ thống / Dùng chung
--   -20100 ~ -20199 : Nhân sự & Khách hàng & Hạng thành viên
--   -20200 ~ -20299 : Sản phẩm & Danh mục sản phẩm
--   -20300 ~ -20399 : Kho & Công thức
--   -20400 ~ -20499 : Đơn hàng
--   -20500 ~ -20599 : Tài chính
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_ERROR_CODES AS

    -- ========================
    -- NHÓM 00: HỆ THỐNG / DÙNG CHUNG
    -- ========================

    -- Lỗi hệ thống khi khôi phục dữ liệu
    ERR_HE_THONG_KHOIPHUC          CONSTANT NUMBER := -20001;

    -- Lỗi hệ thống khi thanh toán giao dịch
    ERR_THANH_TOAN_GIAO_DICH        CONSTANT NUMBER := -20002;

    -- Lỗi hệ thống giao dịch khi thực thi hủy đơn
    ERR_HUY_DON_GIAO_DICH           CONSTANT NUMBER := -20003;

    -- Lỗi hệ thống khi xuất kho sản xuất
    ERR_HUY_XUAT_KHO                CONSTANT NUMBER := -20004;

    -- Lỗi hệ thống khi tạo đơn hàng
    ERR_HUY_TAO_DON                 CONSTANT NUMBER := -20005;


    -- ========================
    -- NHÓM 01: NHÂN SỰ & KHÁCH HÀNG
    -- ========================

    -- Ca làm việc không tồn tại hoặc đã đóng (dùng trong PROC_DONGCADOISOAT)
    ERR_CA_KHONG_TON_TAI            CONSTANT NUMBER := -20101;
    ERR_CA_MO_HE_THONG              CONSTANT NUMBER := -20102;
    ERR_CA_DONG_HE_THONG            CONSTANT NUMBER := -20103;

    --Vai trò
    ERR_VAITRO_THEM_HE_THONG        CONSTANT NUMBER := -20104;
    ERR_VAITRO_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20105;
    ERR_VAITRO_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20106;
    ERR_VAITRO_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20107;
    ERR_VAITRO_XOA_HE_THONG         CONSTANT NUMBER := -20108;
    --Nhân viên
    ERR_NV_THEM_HE_THONG            CONSTANT NUMBER := -20109;
    ERR_NV_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20110;
    ERR_NV_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20111;
    ERR_NV_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20112;
    ERR_NV_XOA_HE_THONG             CONSTANT NUMBER := -20113;

    -- Hạng thành viên
    ERR_HANGTV_THEM_HE_THONG        CONSTANT NUMBER := -20114;
    ERR_HANGTV_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20115;
    ERR_HANGTV_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20116;
    ERR_HANGTV_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20117;
    ERR_HANGTV_XOA_HE_THONG         CONSTANT NUMBER := -20118;
    --Khách Hàng
    ERR_KH_THEM_HE_THONG            CONSTANT NUMBER := -20119;
    ERR_KH_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20120;
    ERR_KH_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20121;
    ERR_KH_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20122;
    ERR_KH_XOA_HE_THONG             CONSTANT NUMBER := -20123;



    -- ========================
    -- NHÓM 02: SẢN PHẨM
    -- ========================
    -- Số lượng bánh trong kho đã hết (dùng trong TRG_TRUKHO_PHIEUXUATTP, TRG_TRUKHO_DONHANG)
    ERR_SP_HET_HANG                 CONSTANT NUMBER := -20201;
    -- Không tìm thấy sản phẩm trong hệ thống (dùng trong TRG_TRUKHO_PHIEUXUATTP, TRG_TRUKHO_DONHANG)
    ERR_SP_KHONG_TON_TAI            CONSTANT NUMBER := -20202;
    -- Số lượng hủy vượt quá tồn kho hiện tại (dùng trong PROC_XUATHUYBANH)
    ERR_XUAT_HUY_BANH               CONSTANT NUMBER := -20203;
    ERR_DM_THEM_HE_THONG            CONSTANT NUMBER := -20204;
    ERR_DM_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20205;
    ERR_DM_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20206;
    ERR_DM_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20207;
    ERR_DM_XOA_HE_THONG             CONSTANT NUMBER := -20208;
    --Sản phẩm
    ERR_SANPHAM_THEM_HE_THONG       CONSTANT NUMBER := -20209;
    ERR_SANPHAM_KHONG_TON_TAI_CN    CONSTANT NUMBER := -20210;
    ERR_SANPHAM_CAPNHAT_HE_THONG    CONSTANT NUMBER := -20211;
    ERR_SANPHAM_KHONG_TON_TAI_XOA   CONSTANT NUMBER := -20212;
    ERR_SANPHAM_XOA_HE_THONG        CONSTANT NUMBER := -20213;




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
    --Lỗi thêm mới nguyên liệu
    ERR_NL_THEM_MOI                 CONSTANT NUMBER := -20310;
    --Lỗi update  nguyên liệu
    ERR_NL_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20311;
    --Lỗi xóa nguyên liệu
    ERR_NL_XOA_HE_THONG             CONSTANT NUMBER := -20312;


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
    --Lỗi thêm phương thức thanh toán
    ERR_PTTT_THEM_HE_THONG          CONSTANT NUMBER := -20503;
    --Lỗi không tìm thấy phương thức thanh toán
    ERR_PTTT_KHONG_TON_TAI          CONSTANT NUMBER := -20504;
    --Lỗi không tìm thấy loại thi chi
    ERR_LOAITHUCHI_KHONG_TON_TAI    CONSTANT NUMBER := -20505;
    --Lỗi thêm phiếu thu chi
    ERR_PHIEUTHUCHI_THEM_HE_THONG   CONSTANT NUMBER := -20506;



END PKG_ERROR_CODES;
/
