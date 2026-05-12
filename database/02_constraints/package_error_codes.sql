-- ============================================================
-- PACKAGE: PKG_ERROR_CODES
-- Muc dich: Chuan hoa toan bo ma loi he thong quan ly tiem banh
-- Phan vung:
--   -20000 ~ -20099 : He thong / Dung chung
--   -20100 ~ -20199 : Nhan su & Khach hang & Hang thanh vien
--   -20200 ~ -20299 : San pham & Danh muc san pham
--   -20300 ~ -20399 : Kho & Cong thuc
--   -20400 ~ -20499 : Don hang
--   -20500 ~ -20599 : Tai chinh
-- ============================================================

CREATE OR REPLACE PACKAGE PKG_ERROR_CODES AS

    -- ========================
    -- NHOM 00: HE THONG / DUNG CHUNG
    -- ========================

    -- Loi he thong khi khoi phuc du lieu
    ERR_HE_THONG_KHOIPHUC          CONSTANT NUMBER := -20001;

    -- Loi he thong khi thanh toan giao dich
    ERR_THANH_TOAN_GIAO_DICH        CONSTANT NUMBER := -20002;

    -- Loi he thong giao dich khi thuc thi huy don
    ERR_HUY_DON_GIAO_DICH           CONSTANT NUMBER := -20003;

    -- Loi he thong khi xuat kho san xuat
    ERR_HUY_XUAT_KHO                CONSTANT NUMBER := -20004;

    -- Loi he thong khi tao don hang
    ERR_HUY_TAO_DON                 CONSTANT NUMBER := -20005;


    -- ========================
    -- NHOM 01: NHAN SU & KHACH HANG
    -- ========================

    -- Ca lam viec khong ton tai hoac da dong (dung trong PROC_DONGCADOISOAT)
    ERR_CA_KHONG_TON_TAI            CONSTANT NUMBER := -20101;
    ERR_CA_MO_HE_THONG              CONSTANT NUMBER := -20102;
    ERR_CA_DONG_HE_THONG            CONSTANT NUMBER := -20103;

    -- Vai tro
    ERR_VAITRO_THEM_HE_THONG        CONSTANT NUMBER := -20104;
    ERR_VAITRO_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20105;
    ERR_VAITRO_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20106;
    ERR_VAITRO_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20107;
    ERR_VAITRO_XOA_HE_THONG         CONSTANT NUMBER := -20108;

    -- Nhan vien
    ERR_NV_THEM_HE_THONG            CONSTANT NUMBER := -20109;
    ERR_NV_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20110;
    ERR_NV_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20111;
    ERR_NV_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20112;
    ERR_NV_XOA_HE_THONG             CONSTANT NUMBER := -20113;

    -- Hang thanh vien
    ERR_HANGTV_THEM_HE_THONG        CONSTANT NUMBER := -20114;
    ERR_HANGTV_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20115;
    ERR_HANGTV_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20116;
    ERR_HANGTV_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20117;
    ERR_HANGTV_XOA_HE_THONG         CONSTANT NUMBER := -20118;

    -- Khach Hang
    ERR_KH_THEM_HE_THONG            CONSTANT NUMBER := -20119;
    ERR_KH_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20120;
    ERR_KH_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20121;
    ERR_KH_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20122;
    ERR_KH_XOA_HE_THONG             CONSTANT NUMBER := -20123;

    -- Nhan vien (bo sung)
    ERR_NV_SDT_TON_TAI              CONSTANT NUMBER := -20124;
    ERR_NV_TENDANGNHAP_TON_TAI      CONSTANT NUMBER := -20125;

    -- Nhan vien: cap nhat & doi mat khau (ma loi chinh thuc thay the hardcode -20199/-20198/-20197)
    ERR_NV_SUA_HE_THONG             CONSTANT NUMBER := -20126;
    ERR_NV_KHONG_CO_TAIKHOAN        CONSTANT NUMBER := -20127;
    ERR_NV_DOI_MATKHAU_HE_THONG     CONSTANT NUMBER := -20128;


    -- ========================
    -- NHOM 02: SAN PHAM
    -- ========================

    -- So luong banh trong kho da het (dung trong TRG_TRUKHO_DONHANG, TRG_TRUKHO_PHIEUXUATTP)
    ERR_SP_HET_HANG                 CONSTANT NUMBER := -20201;

    -- Khong tim thay san pham trong he thong (dung trong TRG_TRUKHO_DONHANG, TRG_TRUKHO_PHIEUXUATTP)
    ERR_SP_KHONG_TON_TAI            CONSTANT NUMBER := -20202;

    -- So luong huy vuot qua ton kho hien tai (dung trong PROC_XUATHUYBANH)
    ERR_XUAT_HUY_BANH               CONSTANT NUMBER := -20203;

    -- Danh muc san pham
    ERR_DM_THEM_HE_THONG            CONSTANT NUMBER := -20204;
    ERR_DM_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20205;
    ERR_DM_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20206;
    ERR_DM_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20207;
    ERR_DM_XOA_HE_THONG             CONSTANT NUMBER := -20208;

    -- San pham
    ERR_SANPHAM_THEM_HE_THONG       CONSTANT NUMBER := -20209;
    ERR_SANPHAM_KHONG_TON_TAI_CN    CONSTANT NUMBER := -20210;
    ERR_SANPHAM_CAPNHAT_HE_THONG    CONSTANT NUMBER := -20211;
    ERR_SANPHAM_KHONG_TON_TAI_XOA   CONSTANT NUMBER := -20212;
    ERR_SANPHAM_XOA_HE_THONG        CONSTANT NUMBER := -20213;

    -- Kich co banh
    ERR_KICHCO_THEM_HE_THONG        CONSTANT NUMBER := -20214;
    ERR_KICHCO_KHONG_TON_TAI_CN     CONSTANT NUMBER := -20215;
    ERR_KICHCO_CAPNHAT_HE_THONG     CONSTANT NUMBER := -20216;
    ERR_KICHCO_KHONG_TON_TAI_XOA    CONSTANT NUMBER := -20217;
    ERR_KICHCO_XOA_HE_THONG         CONSTANT NUMBER := -20218;

    -- Cot banh
    ERR_COTBANH_THEM_HE_THONG       CONSTANT NUMBER := -20219;
    ERR_COTBANH_KHONG_TON_TAI_CN    CONSTANT NUMBER := -20220;
    ERR_COTBANH_CAPNHAT_HE_THONG    CONSTANT NUMBER := -20221;
    ERR_COTBANH_KHONG_TON_TAI_XOA   CONSTANT NUMBER := -20222;
    ERR_COTBANH_XOA_HE_THONG        CONSTANT NUMBER := -20223;

    -- Nhan banh
    ERR_NHANBANH_THEM_HE_THONG      CONSTANT NUMBER := -20224;
    ERR_NHANBANH_KHONG_TON_TAI_CN   CONSTANT NUMBER := -20225;
    ERR_NHANBANH_CAPNHAT_HE_THONG   CONSTANT NUMBER := -20226;
    ERR_NHANBANH_KHONG_TON_TAI_XOA  CONSTANT NUMBER := -20227;
    ERR_NHANBANH_XOA_HE_THONG       CONSTANT NUMBER := -20228;

    -- Trang tri
    ERR_TRANGTRI_THEM_HE_THONG      CONSTANT NUMBER := -20229;
    ERR_TRANGTRI_KHONG_TON_TAI_CN   CONSTANT NUMBER := -20230;
    ERR_TRANGTRI_CAPNHAT_HE_THONG   CONSTANT NUMBER := -20231;
    ERR_TRANGTRI_KHONG_TON_TAI_XOA  CONSTANT NUMBER := -20232;
    ERR_TRANGTRI_XOA_HE_THONG       CONSTANT NUMBER := -20233;


    -- ========================
    -- NHOM 03: KHO & CONG THUC
    -- ========================

    -- Nguyen lieu khong dat chuan VSATTP (dung trong TRG_KIEMTRAVSATTP)
    ERR_NL_KHONG_DAT_VSATTP         CONSTANT NUMBER := -20301;

    -- Phat hien gian lan han su dung (dung trong TRG_KIEMTRA_HSD)
    ERR_GIAN_LAN_HSD                CONSTANT NUMBER := -20302;
    ERR_HSD_KHONG_HOPLE             CONSTANT NUMBER := -20303;
    ERR_KHONG_CO_PHIEUNHAP          CONSTANT NUMBER := -20304;

    -- Khong tim thay phieu nhap kho (dung trong TRG_TONGTIENNHAP)
    ERR_NL_KHONG_CO_PHIEUNHAP       CONSTANT NUMBER := -20305;

    -- Khong tim thay lo hang trong kho (dung trong TRG_XUATSLNGUYENLIEU)
    ERR_NL_KHONG_CO_LO_HANG         CONSTANT NUMBER := -20306;

    -- Khong tim thay nguyen lieu (dung trong TRG_GIAVONTRUNGBINH_SOLUONGTONTONG)
    ERR_KHONG_CO_NGUYEN_LIEU        CONSTANT NUMBER := -20307;

    -- Nguyen lieu da duoc su dung, khong the huy phieu nhap (dung trong PROC_HUYPHIEUNHAPKHO)
    ERR_NL_KHONG_THE_HUY_PN         CONSTANT NUMBER := -20308;

    -- Loi he thong khi thuc thi Nhap kho (dung trong PROC_NHAPKHO)
    ERR_NHAP_KHO                    CONSTANT NUMBER := -20309;

    -- Khong du ton kho nguyen lieu de xuat san xuat (dung trong PROC_XUATKHOSANXUAT)
    ERR_NL_KHONG_DU                 CONSTANT NUMBER := -20310;

    -- Nguyen lieu
    ERR_NL_THEM_MOI                 CONSTANT NUMBER := -20320;
    ERR_NL_KHONG_TON_TAI_CN         CONSTANT NUMBER := -20311;
    ERR_NL_CAPNHAT_HE_THONG         CONSTANT NUMBER := -20312;
    ERR_NL_KHONG_TON_TAI_XOA        CONSTANT NUMBER := -20313;
    ERR_NL_XOA_HE_THONG             CONSTANT NUMBER := -20314;

    -- Xuat huy nguyen lieu hong (dung trong PROC_XUATNGUYENLIEUHO NG)
    ERR_XUAT_HUY_NL                 CONSTANT NUMBER := -20321;

    -- Nha cung cap
    ERR_NCC_THEM_HE_THONG           CONSTANT NUMBER := -20315;
    ERR_NCC_KHONG_TON_TAI_CN        CONSTANT NUMBER := -20316;
    ERR_NCC_CAPNHAT_HE_THONG        CONSTANT NUMBER := -20317;
    ERR_NCC_KHONG_TON_TAI_XOA       CONSTANT NUMBER := -20318;
    ERR_NCC_XOA_HE_THONG            CONSTANT NUMBER := -20319;


    -- ========================
    -- NHOM 04: DON HANG
    -- ========================

    -- Vuot cong suat san xuat trong ngay (dung trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH)
    ERR_DON_VUOT_CONG_SUAT          CONSTANT NUMBER := -20401;

    -- Khong tim thay gioi han san xuat trong ngay (dung trong TRG_KIEMSOAT_CONGSUAT_TUYCHINH)
    ERR_KHONG_GIOI_HAN_SX           CONSTANT NUMBER := -20402;

    -- Loi khi chuyen trang thai don hang (dung trong PROC_CHUYENTRANGTHAIDON)
    ERR_DON_CHUYEN_TRANGTHAI        CONSTANT NUMBER := -20403;

    -- Khong tim thay don hang de cap nhat tong tien (dung trong TRG_CAPNHAT_CTDONHANG)
    ERR_DONDATHANG_KHONG_TON_TAI    CONSTANT NUMBER := -20404;

    -- Loi he thong khi cap nhat tong tien don hang (dung trong TRG_CAPNHAT_DONHANG)
    ERR_HUY_CAPNHAT_TONGTIEN        CONSTANT NUMBER  := - 20406;

    -- ========================
    -- NHOM 05: TAI CHINH
    -- ========================

    -- Vi pham ke toan: cam xoa vat ly hoa don
    ERR_TC_CAM_XOA_HOADON           CONSTANT NUMBER := -20501;

    -- Vi pham ke toan: cam xoa vat ly phieu thu chi
    ERR_TC_CAM_XOA_PHIEUTHUCHI      CONSTANT NUMBER := -20502;

    -- Loi them phuong thuc thanh toan
    ERR_PTTT_THEM_HE_THONG          CONSTANT NUMBER := -20503;

    -- Loi khong tim thay phuong thuc thanh toan
    ERR_PTTT_KHONG_TON_TAI          CONSTANT NUMBER := -20504;

    -- Loi khong tim thay loai thu chi
    ERR_LOAITHUCHI_KHONG_TON_TAI    CONSTANT NUMBER := -20505;

    -- Loi them phieu thu chi
    ERR_PHIEUTHUCHI_THEM_HE_THONG   CONSTANT NUMBER := -20506;

    -- Cap nhat & xoa phuong thuc thanh toan
    ERR_PTTT_CAPNHAT_HE_THONG       CONSTANT NUMBER := -20507;
    ERR_PTTT_KHONG_TON_TAI_CN       CONSTANT NUMBER := -20508;
    ERR_PTTT_KHONG_TON_TAI_XOA      CONSTANT NUMBER := -20509;
    ERR_PTTT_XOA_HE_THONG           CONSTANT NUMBER := -20510;

    -- Loai thu chi
    ERR_LOAITHUCHI_THEM_HE_THONG    CONSTANT NUMBER := -20511;
    ERR_LOAITHUCHI_KHONG_TON_TAI_CN CONSTANT NUMBER := -20512;
    ERR_LOAITHUCHI_CAPNHAT_HE_THONG CONSTANT NUMBER := -20513;
    ERR_LOAITHUCHI_KHONG_TON_TAI_XOA CONSTANT NUMBER := -20514;
    ERR_LOAITHUCHI_XOA_HE_THONG     CONSTANT NUMBER := -20515;

    -- Hoa don
    ERR_HOADON_THEM_HE_THONG        CONSTANT NUMBER := -20516;

    -- Phieu nhap kho & xuat banh
    ERR_PHIEU_NHAP_KHO              CONSTANT NUMBER := -20517;
    ERR_HOAN_PHIEU_NHAP_KHO         CONSTANT NUMBER := -20518;
    ERR_HOAN_XUAT_BANH              CONSTANT NUMBER := -20519;
    ERR_NL_TON_AO                   CONSTANT NUMBER := -20520;

END PKG_ERROR_CODES;
/