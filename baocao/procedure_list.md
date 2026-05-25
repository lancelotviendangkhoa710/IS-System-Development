# Danh sách Stored Procedures — Bakery Management System

> Tổng cộng: **66 procedures** · Được gộp thành một bảng duy nhất có đánh số thứ tự và phân nhóm rõ ràng.

---

| STT | Nhóm | Tên Procedure | Tham số IN | Tham số OUT | Ý nghĩa |
|:---:|---|---|---|---|---|
| 1 | Hạng thành viên | `PROC_THEM_HANGTHANHVIEN` | P_TENHANG, P_DIEMTOITHIEU, P_PHANTRAMGIAMGIA | P_MAHANG_OUT | Thêm hạng thành viên mới |
| 2 | Hạng thành viên | `PROC_SUA_HANGTHANHVIEN` | P_MAHANG, P_TENHANG, P_DIEMTOITHIEU, P_PHANTRAMGIAMGIA | — | Sửa thông tin hạng thành viên |
| 3 | Hạng thành viên | `PROC_XOA_HANGTHANHVIEN` | P_MAHANG, P_MANV_XOA | — | Xóa mềm hạng thành viên |
| 4 | Khách hàng | `PROC_THEM_KHACHHANG` | P_HOTEN, P_SDT, P_DIACHI, P_DIEMTICHLUY, P_MAHANG, P_MANV_LAP | P_MAKH_OUT | Thêm khách hàng mới, ghi log hoạt động |
| 5 | Khách hàng | `PROC_SUA_KHACHHANG` | P_MAKH, P_HOTEN, P_SDT, P_DIACHI, P_DIEMTICHLUY, P_MAHANG, P_MANV_CAP | — | Sửa thông tin khách hàng |
| 6 | Khách hàng | `PROC_XOA_KHACHHANG` | P_MAKH, P_MANV_XOA | — | Xóa mềm khách hàng |
| 7 | Nhân viên & Tài khoản | `PROC_THEM_NHANVIEN` | P_HOTEN, P_NGAYSINH, P_SDT, P_TENDANGNHAP, P_MATKHAU, P_TRANGTHAILAMVIEC | P_MANV_OUT | Thêm nhân viên + tạo tài khoản cùng lúc (atomic) |
| 8 | Nhân viên & Tài khoản | `PROC_SUA_NHANVIEN` | P_MANV, P_HOTEN, P_NGAYSINH, P_SDT, P_TENDANGNHAP, P_MATKHAU, P_TRANGTHAILAMVIEC | — | Sửa hồ sơ nhân viên và tài khoản |
| 9 | Nhân viên & Tài khoản | `PROC_XOA_NHANVIEN` | P_MANV | — | Xóa mềm nhân viên (set TRANGTHAILAMVIEC = 0) |
| 10 | Nhân viên & Tài khoản | `PROC_THOIVIEC_NHANVIEN` | P_MANV, P_MANV_THAOTAC | — | Cho thôi việc: vô hiệu tài khoản + ghi log |
| 11 | Nhân viên & Tài khoản | `PROC_DOI_MATKHAU_TAIKHOAN` | P_MANV, P_MATKHAU_MOI | — | Đổi mật khẩu tài khoản |
| 12 | Vai trò & Phân quyền | `PROC_THEM_VAITRO` | P_TENVAITRO, P_MOTA | P_MAVAITRO_OUT | Thêm vai trò mới |
| 13 | Vai trò & Phân quyền | `PROC_SUA_VAITRO` | P_MAVAITRO, P_TENVAITRO, P_MOTA | — | Sửa thông tin vai trò |
| 14 | Vai trò & Phân quyền | `PROC_XOA_VAITRO` | P_MAVAITRO, P_MANV_XOA | — | Xóa mềm vai trò |
| 15 | Vai trò & Phân quyền | `PROC_GAN_VAITRO_NHANVIEN` | P_MANV, P_MAVAITRO | — | Gán vai trò cho tài khoản nhân viên |
| 16 | Vai trò & Phân quyền | `PROC_CAPNHAT_QUYEN_CHI_TIET` | P_MAVAITRO, P_MACHUCNANG, P_VIEW, P_ADD, P_EDIT, P_DELETE, P_DOWNLOAD | — | Cập nhật ma trận quyền chi tiết (MERGE) |
| 17 | Loại thu chi | `PROC_THEM_LOAITHUCHI` | P_TENLOAITHUCHI, P_PHANLOAI | P_MALOAITHUCHI_OUT | Thêm loại thu chi mới |
| 18 | Loại thu chi | `PROC_SUA_LOAITHUCHI` | P_MALOAITHUCHI, P_TENLOAITHUCHI, P_PHANLOAI | — | Sửa loại thu chi (SERIALIZABLE, chống Lost Update) |
| 19 | Loại thu chi | `PROC_XOA_LOAITHUCHI` | P_MALOAITHUCHI, P_MANX | — | Xóa mềm loại thu chi (SERIALIZABLE) |
| 20 | Phiếu thu chi | `PROC_TAOPHIEUTHUCHI` | P_MALOAITHUCHI, P_SOTIEN, P_MANV, P_MAHD, P_MAPN, P_MACA, P_GHICHU | P_MAPHIEUTC_OUT | Tạo phiếu thu/chi mới |
| 21 | Hóa đơn | `PROC_TAOHOADON` | P_MADON, P_MACA, P_THUEVAT, P_TIENHANGGOC, P_TONGTIENTHANHTOAN, P_MAPTTT, P_LOAIHD | P_MAHD_OUT | Tạo hóa đơn cho đơn hàng đã chốt |
| 22 | Hóa đơn | `PROC_HUYHOADONBANLE` | P_MADON, P_LYDOHUY, P_MANV_CAPNHAT | — | Hủy hóa đơn bán lẻ trực tiếp: đổi trạng thái, hoàn kho, ghi lịch sử |
| 23 | Nhà cung cấp | `PROC_THEM_NHACUNGCAP` | P_TENNCC, P_SDT, P_DIACHI | P_MANCC_OUT | Thêm nhà cung cấp |
| 24 | Nhà cung cấp | `PROC_SUA_NHACUNGCAP` | P_MANCC, P_TENNCC, P_SDT, P_DIACHI | — | Sửa thông tin nhà cung cấp |
| 25 | Nhà cung cấp | `PROC_XOA_NHACUNGCAP` | P_MANCC, P_MANX | — | Xóa mềm (có phiếu nhập) hoặc xóa cứng (chưa có) |
| 26 | Nguyên liệu & Kho | `PROC_THEM_NGUYENLIEU` | P_TENNL, P_XUATXU, P_MUCTONANTOAN, P_MADVT, P_HESOQUYDOI | P_MANL_OUT | Thêm nguyên liệu (có hệ số quy đổi) |
| 27 | Nguyên liệu & Kho | `PROC_SUA_NGUYENLIEU` | P_MANL, P_TENNL, P_XUATXU, P_MADVT, P_MUCTONANTOAN, P_HESOQUYDOI | — | Sửa nguyên liệu, tăng PHIENBAN |
| 28 | Nguyên liệu & Kho | `PROC_XOA_NGUYENLIEU` | P_MANL, P_MANX | — | Xóa mềm (đã có lô nhập) hoặc xóa cứng |
| 29 | Nguyên liệu & Kho | `PROC_THEM_NGUYENLIEU_VA_NHAP_KHO` | P_TENNL, P_XUATXU, P_MUCTONANTOAN, P_MADVT, P_MANCC, P_MANV, P_SOLUONG, P_DONGIA, P_NGAYSANXUAT, P_HANSUDUNG, P_HESOQUYDOI | P_MANL_OUT, P_MAPN_OUT | Tạo nguyên liệu MỚI + nhập kho lần đầu (atomic, có quy đổi đơn vị) |
| 30 | Phiếu nhập kho | `PROC_TAOPHIEUNHAPKHO` | P_MANV, P_MANCC, P_JSON_DATALIST (CLOB), P_MACA | P_MAPN_OUT | Tạo phiếu nhập kho: parse JSON → tạo PHIEUNHAPKHO + CTPHIEUNHAP + phiếu chi tự động |
| 31 | Phiếu nhập kho | `PROC_HUYPHIEUNHAPKHO` | P_MAPN | — | Hủy phiếu nhập: kiểm tra chưa xuất kho, hoàn ngược tồn kho, vô hiệu phiếu chi |
| 32 | Phiếu nhập kho | `PROC_LAPBAOCAOPHIEUNHAP` | — | P_SO_PHIEU_OUT, P_CURSOR_OUT | Lập báo cáo kiểm kê phiếu nhập (SERIALIZABLE, trả SYS_REFCURSOR) |
| 33 | Danh mục & Sản phẩm | `PROC_THEM_DANHMUCSP` | P_TENDM | P_MADM_OUT | Thêm danh mục sản phẩm |
| 34 | Danh mục & Sản phẩm | `PROC_SUA_DANHMUCSP` | P_MADM, P_TENDM | — | Sửa tên danh mục |
| 35 | Danh mục & Sản phẩm | `PROC_XOA_DANHMUCSP` | P_MADM, P_MANX | — | Xóa mềm danh mục |
| 36 | Danh mục & Sản phẩm | `PROC_THEM_SANPHAM` | P_MADM, P_TENSP, P_HINHANH, P_CHOPHEPTUYCHINH, P_THOIGIANBAOQUAN, P_THOIGIANCHUANBI, P_GIAVON, P_GIABAN, P_MANV_LAP | P_MASP_OUT | Thêm sản phẩm, tự tính giá bán nếu không truyền |
| 37 | Danh mục & Sản phẩm | `PROC_SUA_SANPHAM` | P_MASP, P_MADM, P_TENSP, P_HINHANH, P_CHOPHEPTUYCHINH, P_THOIGIANBAOQUAN, P_THOIGIANCHUANBI, P_GIAVON, P_GIABAN | — | Sửa sản phẩm, tính lại giá từ BOM nếu giá = 0 |
| 38 | Danh mục & Sản phẩm | `PROC_XOA_SANPHAM` | P_MASP, P_MANX | — | Xóa mềm (có đơn hàng) hoặc xóa cứng kèm công thức |
| 39 | Thuộc tính bánh | `PROC_THEM_KICHCOBANH` | P_TENKC, P_PHUPHI | P_MAKC_OUT | Thêm kích cỡ bánh |
| 40 | Thuộc tính bánh | `PROC_CAPNHAT_KICHCOBANH` | P_MAKC, P_TENKC, P_PHUPHI | — | Sửa kích cỡ bánh |
| 41 | Thuộc tính bánh | `PROC_XOA_KICHCOBANH` | P_MAKC, P_MANX | — | Xóa mềm kích cỡ bánh |
| 42 | Thuộc tính bánh | `PROC_THEM_COTBANH` | P_TENCOT, P_PHUPHI | P_MACOT_OUT | Thêm cốt bánh |
| 43 | Thuộc tính bánh | `PROC_CAPNHAT_COTBANH` | P_MACOT, P_TENCOT, P_PHUPHI | — | Sửa cốt bánh |
| 44 | Thuộc tính bánh | `PROC_XOA_COTBANH` | P_MACOT, P_MANX | — | Xóa mềm cốt bánh |
| 45 | Thuộc tính bánh | `PROC_THEM_NHANBANH` | P_TENNHAN, P_PHUPHI | P_MANHAN_OUT | Thêm nhân bánh |
| 46 | Thuộc tính bánh | `PROC_CAPNHAT_NHANBANH` | P_MANHAN, P_TENNHAN, P_PHUPHI | — | Sửa nhân bánh |
| 47 | Thuộc tính bánh | `PROC_XOA_NHANBANH` | P_MANHAN, P_MANX | — | Xóa mềm nhân bánh |
| 48 | Thuộc tính bánh | `PROC_THEM_KIEUTRANGTRI` | P_TENTRANGTRI, P_PHUPHI | P_MATRANGTRI_OUT | Thêm kiểu trang trí |
| 49 | Thuộc tính bánh | `PROC_CAPNHAT_KIEUTRANGTRI` | P_MATRANGTRI, P_TENTRANGTRI, P_PHUPHI | — | Sửa kiểu trang trí |
| 50 | Thuộc tính bánh | `PROC_XOA_KIEUTRANGTRI` | P_MATRANGTRI, P_MANX | — | Xóa mềm kiểu trang trí |
| 51 | Công thức (BOM) | `PROC_UPSERT_CONGTHUC` | P_MASP, P_MANL, P_SOLUONGTIEUHAO | — | Thêm hoặc sửa một dòng BOM (MERGE theo PK composite) |
| 52 | Công thức (BOM) | `PROC_XOA_CONGTHUC` | P_MASP, P_MANL | — | Xóa một dòng nguyên liệu khỏi công thức |
| 53 | Đơn hàng | `PROC_TAODONHANG` | P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, P_TIENDACOC, P_HINHTHUCNHAN, P_DIACHIGIAO, P_JSONCHITIET (CLOB) | P_MADON_OUT | Tạo đơn hàng: parse JSON, kiểm tra tồn kho (SERIALIZABLE), insert chi tiết, trừ kho |
| 54 | Đơn hàng | `PROC_HUYDON_HOANCOC` *(order_cud)* | P_MADON, P_LYDOHUY, P_MANV, P_SOTIENHOANTIEN, P_MACA | — | Hủy đơn đặt trước: đổi trạng thái + tạo phiếu chi hoàn tiền cọc |
| 55 | Đơn hàng | `PROC_HUYDON_HOANCOC` *(cancel_refund)* | P_MADON, P_LYDOHUY, P_MANV_CAPNHAT, P_SOTIEN_HOAN | — | Hủy đơn + hoàn kho + tạo phiếu chi hoàn cọc |
| 56 | Đơn hàng | `PROC_HUYHOADONBANLE` | P_MADON, P_LYDOHUY, P_MANV_CAPNHAT | — | Hủy hóa đơn bán lẻ (chỉ đơn trực tiếp, đã Hoàn thành): hoàn kho + ghi lịch sử |
| 57 | Đơn hàng | `PROC_CHUYENTRANGTHAIDON` | P_MADON, P_MATRANGTHAI_MOI, P_MANV_CAPNHAT, P_HINHTHUCNHAN | — | Chuyển trạng thái đơn (có logic phân luồng giao/lấy), ghi LICHSUDONHANG |
| 58 | Đơn hàng | `PROC_THANHTOANVATHANGHANG` | P_MAHD, P_MAKH, P_SOTIENTHANHTOAN | — | Chốt thanh toán: đổi trạng thái Hoàn thành + tích điểm + tự động thăng hạng KH |
| 59 | Xuất kho | `PROC_XUATKHOSANXUAT` | P_MASP, P_SOLUONGSANXUAT, P_MANV | — | Xuất nguyên liệu theo BOM (FIFO theo lô), tạo PHIEUXUATKHO + MESANXUAT, cộng tồn thành phẩm |
| 60 | Xuất kho | `PROC_XUATHUYBANH` | P_MASP, P_SOLUONGHUY, P_MANV | — | Xuất hủy bánh thành phẩm hỏng (bảo quản) |
| 61 | Xuất kho | `PROC_XUATNGUYENLIEUHONG` | P_MANL, P_SOLUONGHUY, P_MANV | — | Xuất hủy nguyên liệu hỏng (FIFO theo lô) |
| 62 | Xuất kho | `PROC_XUATSAISOTBANH` | P_MASP, P_SOLUONGHUY, P_MANV | — | Xuất hủy bánh sai sót trong quá trình làm |
| 63 | Xuất kho | `PROC_XUATTHANHPHAM_HETHAN` | P_MANV | P_SO_ME_OUT, P_SO_BANH_OUT | Tự động hủy toàn bộ bánh hết hạn theo mẻ sản xuất (được JOB gọi) |
| 64 | Ca làm việc | `PROC_MOCA` | P_MANV, P_MAMAYPOS, P_TIENKHAIBAODAUCA | P_MACA_OUT | Mở ca làm việc + tạo bản ghi đối soát đầu ca |
| 65 | Ca làm việc | `PROC_DONGCADOISOAT` | P_MACA, P_TIENTHUCTEDEM, P_CHENHLECH, P_LYDOCHENHLECH | — | Ghi kết quả đối soát cuối ca (tính chênh lệch, không tự COMMIT) |
| 66 | Tiện ích hệ thống | `PROC_XOAVINHVIEN_QUAHAN` | P_TENBANG, P_TENCOTPK, P_SO_NGAY, P_MANV | P_SO_DONG_OUT | Xóa vĩnh viễn các bản ghi soft-delete quá N ngày (Dynamic SQL, dùng chung mọi bảng) |
