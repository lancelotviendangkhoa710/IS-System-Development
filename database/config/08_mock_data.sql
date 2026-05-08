-- =======================================================
-- MOCK DATA CHO HỆ THỐNG H3K BAKERY
-- Lưu ý: Bạn cần chạy các bảng từ 01 đến 05 trước khi chạy file này.
-- Các Foreign Keys đến KHACHHANG, NHANVIEN, CALAMVIEC (tùy thuộc vào các bảng của bạn) giả định đang dùng ID là 1.
-- MANX và THOIDIEMXOA được để trống vì đây là dữ liệu đang hoạt động.
-- =======================================================

-- =======================================================
-- 1. TỪ 02_product.sql (Sản phẩm & Tuỳ chỉnh)
-- =======================================================
INSERT INTO DANHMUCSP (TENDM) VALUES (N'Bánh kem sinh nhật');
INSERT INTO DANHMUCSP (TENDM) VALUES (N'Bánh mì & Ngọt');
INSERT INTO DANHMUCSP (TENDM) VALUES (N'Cookie & Tráng miệng');

-- Note: CHOPHEPTUYCHINH (1: có, 0: không)
INSERT INTO SANPHAM (MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, THOIGIANBAOQUAN, SOLUONGTON, THOIGIANCHUANBI) 
VALUES (1, N'Bánh kem dâu tây', 250000, 'kem-dau.png', 1, 3, 0, 120);
INSERT INTO SANPHAM (MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, THOIGIANBAOQUAN, SOLUONGTON, THOIGIANCHUANBI) 
VALUES (2, N'Bánh mì bơ tỏi', 35000, 'bmt.png', 0, 2, 50, 0);
INSERT INTO SANPHAM (MADM, TENSP, GIACOBAN, HINHANH, CHOPHEPTUYCHINH, THOIGIANBAOQUAN, SOLUONGTON, THOIGIANCHUANBI) 
VALUES (3, N'Macaron mix vị', 80000, 'macaron.png', 0, 7, 100, 0);

-- Tùy chỉnh kích cỡ bánh
INSERT INTO KICHCOBANH (TENKC, PHUPHI) VALUES (N'Size 16cm', 0);
INSERT INTO KICHCOBANH (TENKC, PHUPHI) VALUES (N'Size 20cm', 50000);

-- Tùy chỉnh cốt bánh
INSERT INTO COTBANH (TENCOT, PHUPHI) VALUES (N'Cốt Vani', 0);
INSERT INTO COTBANH (TENCOT, PHUPHI) VALUES (N'Cốt Socola', 30000);

-- Tùy chỉnh nhân bánh
INSERT INTO NHANBANH (TENNHAN, PHUPHI) VALUES (N'Nhân mứt dâu', 0);
INSERT INTO NHANBANH (TENNHAN, PHUPHI) VALUES (N'Nhân sầu riêng', 40000);

-- Tùy chỉnh trang trí
INSERT INTO KIEUTRANGTRI (TENTRANGTRI, PHUPHI) VALUES (N'Trang trí tiêu chuẩn', 0);
INSERT INTO KIEUTRANGTRI (TENTRANGTRI, PHUPHI) VALUES (N'Gắn vương miện 3D', 80000);

-- =======================================================
-- 2. TỪ 03_stock_recipe.sql (Kho & Công thức)
-- =======================================================

-- Đơn vị tính
INSERT INTO DONVITINH (TENDVT) VALUES (N'Kg');
INSERT INTO DONVITINH (TENDVT) VALUES (N'Lít');
INSERT INTO DONVITINH (TENDVT) VALUES (N'Cái');

-- Nguyên liệu
INSERT INTO NGUYENLIEU (TENNL, XUATXU, MADVT, GIAVONTRUNGBINH, MUCTONANTOAN, SOLUONGTONTONG, DATCHUANVSATTP)
VALUES (N'Bột mì đa dụng', N'Việt Nam', 1, 20000, 10, 50, 1);
INSERT INTO NGUYENLIEU (TENNL, XUATXU, MADVT, GIAVONTRUNGBINH, MUCTONANTOAN, SOLUONGTONTONG, DATCHUANVSATTP)
VALUES (N'Sữa tươi không đường', N'Hàn Quốc', 2, 35000, 5, 20, 1);

-- Công thức
INSERT INTO CONGTHUC (MASP, MANL, SOLUONGTIEUHAO) VALUES (2, 1, 0.2); 
INSERT INTO CONGTHUC (MASP, MANL, SOLUONGTIEUHAO) VALUES (2, 2, 0.05); 

-- Nhà cung cấp
INSERT INTO NHACUNGCAP (TENNCC, SDT, DIACHI) 
VALUES (N'Công ty CP Bột Mì VN', '0901234558', N'123 Nguyễn Văn Linh, HCM');

-- Phiếu nhập & CT
INSERT INTO PHIEUNHAPKHO (MANV, MANCC, TONGTIENNHAP) VALUES (6, 24, 1000000);
INSERT INTO CTPHIEUNHAP (MAPN, MANL, SOLUONG, DONGIA, SOLUONGCONLAI, NGAYSANXUAT, HANSUDUNG) 
VALUES (1, 1, 50, 20000, 50, TRUNC(SYSDATE), TRUNC(SYSDATE) + 180);

-- =======================================================
-- 3. TỪ 04_order.sql (Đơn hàng)
-- =======================================================

-- Trạng thái đơn hàng
INSERT INTO TRANGTHAIDON (TENTRANGTHAI) VALUES ('Chưa hoàn thành');
INSERT INTO TRANGTHAIDON (TENTRANGTHAI) VALUES ('Đã hoàn thành');
INSERT INTO TRANGTHAIDON (TENTRANGTHAI) VALUES ('Đã hủy');

-- Năng lực sản xuất
INSERT INTO NANGLUCSANXUAT (NGAYSANXUAT, GIOIHANSOBANH, SOBANHDANHAN) VALUES (TRUNC(SYSDATE), 500, 10);

-- Đơn đặt hàng & Chi tiết
INSERT INTO DONDATHANG (NGAYGIONHANBANH, MAKH, MANV_LAP, MATRANGTHAI, TONGTIENHDBAN, TIENDACOC, HINHTHUCNHAN)
VALUES (SYSDATE + 1, 1, 6, 1, 280000, 100000, 2);

INSERT INTO CTDONHANG (MADON, MASP, SOLUONG, DONGIA, PHANTRAMGIAM) 
VALUES (1, 2, 2, 35000, 0);

INSERT INTO CTDONTUYCHINH (MADON, MASP, SOLUONG, MAKC, MACOT, MANHAN, MATRANGTRI, DONGIA)
VALUES (1, 1, 1, 1, 1, 1, 1, 250000);

-- =======================================================
-- 4. TỪ 05_finance.sql (Tài chính)
-- =======================================================

-- Phương thức thanh toán
INSERT INTO PHUONGTHUCTT (TENPTTT) VALUES (N'Tiền mặt');
INSERT INTO PHUONGTHUCTT (TENPTTT) VALUES (N'Chuyển khoản VNPay');

-- Phân loại Thu Chi
INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI) VALUES (N'Thu tiền đơn hàng', N'THU');
INSERT INTO LOAITHUCHI (TENLOAITHUCHI, PHANLOAI) VALUES (N'Chi trả nhà cung cấp', N'CHI');

-- Phiếu thu chi
INSERT INTO PHIEUTHUCHI (MALOAITHUCHI, SOTIEN, MANV, MACA, GHICHU)
VALUES (1, 100000, 1, 1, N'Thu tiền cọc đơn hàng #1');
