# Báo cáo mô tả màn hình — Quản lý Thẻ kho

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabTheKho` | Tab | | Tab đang được chọn trong TabPane kho hàng |
| 2 | `tabNhaCungCap` | Tab | | Chuyển sang màn hình Nhà cung cấp |
| 3 | `tabKiemKeKho` | Tab | | Chuyển sang màn hình Kiểm kê kho |
| 4 | `tabNhapKho` | Tab | | Chuyển sang màn hình Nhập kho |
| 5 | `tabTruyXuatNguonGoc` | Tab | | Chuyển sang màn hình Truy xuất nguồn gốc |
| 6 | `cboNguyenLieu` | ComboBox | Bắt buộc chọn | Chọn nguyên liệu cần tra cứu thẻ kho |
| 7 | `dpTuNgay` | DatePicker | | Chọn ngày bắt đầu của kỳ tra cứu |
| 8 | `dpDenNgay` | DatePicker | | Chọn ngày kết thúc của kỳ tra cứu |
| 9 | `btnXemTheKho` | Button | | Thực hiện tra cứu thẻ kho theo điều kiện |
| 10 | `btnXoaLoc` | Button | | Xóa toàn bộ điều kiện lọc, đặt lại về mặc định |
| 11 | `lblTonDauKy` | Label | | Hiển thị số lượng tồn kho đầu kỳ |
| 12 | `lblNhapKy` | Label | | Hiển thị tổng số lượng nhập trong kỳ |
| 13 | `lblXuatKy` | Label | | Hiển thị tổng số lượng xuất trong kỳ |
| 14 | `lblTonCuoiKy` | Label | | Hiển thị số lượng tồn kho cuối kỳ |
| 15 | `tblLichSuBienDong` | TableView | | Bảng danh sách lịch sử biến động kho theo kỳ |
| 16 | `colNgayGiaoDich` | TableColumn | | Ngày và giờ phát sinh giao dịch |
| 17 | `colLoai` | TableColumn | | Loại giao dịch (Nhập kho / Xuất sản xuất) |
| 18 | `colMaLo` | TableColumn | | Mã lô hàng của giao dịch |
| 19 | `colSoLuong` | TableColumn | | Số lượng biến động trong giao dịch |
| 20 | `colConLaiTrongLo` | TableColumn | | Số lượng còn lại trong lô sau giao dịch |
| 21 | `lblTimThay` | Label | | Hiển thị tổng số giao dịch tìm thấy |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Nạp danh sách nguyên liệu vào `cboNguyenLieu`. Chọn mặc định nguyên liệu đầu tiên. Để trống `dpTuNgay`, `dpDenNgay`. Đặt các nhãn tổng hợp về 0. Xóa trắng `tblLichSuBienDong`. | |
| 2 | Chọn button `btnXemTheKho` | Tra cứu thẻ kho theo nguyên liệu đã chọn ở `cboNguyenLieu` và khoảng ngày `dpTuNgay` – `dpDenNgay`.<br>- Nếu không chọn nguyên liệu → hiển thị thông báo yêu cầu chọn nguyên liệu.<br>- Nếu hợp lệ → cập nhật `lblTonDauKy`, `lblNhapKy`, `lblXuatKy`, `lblTonCuoiKy` và nạp dữ liệu vào `tblLichSuBienDong`. Cập nhật `lblTimThay` số giao dịch tìm thấy. | |
| 3 | Chọn button `btnXoaLoc` | Xóa giá trị `dpTuNgay`, `dpDenNgay`. Đặt lại `cboNguyenLieu` về mặc định. Đặt lại các nhãn tổng hợp về 0. Xóa trắng `tblLichSuBienDong`. | |
