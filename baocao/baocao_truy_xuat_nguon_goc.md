# Màn hình Truy xuất nguồn gốc

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimKiem` | TextField | | Ô nhập tên sản phẩm để lọc danh sách mẻ sản xuất |
| 2 | `dpTuNgay` | DatePicker | | Ô chọn ngày bắt đầu khoảng thời gian tra cứu |
| 3 | `dpDenNgay` | DatePicker | | Ô chọn ngày kết thúc khoảng thời gian tra cứu |
| 4 | `btnTimKiem` | Button | | Thực hiện tìm kiếm mẻ sản xuất theo điều kiện lọc đã nhập |
| 5 | `btnXoaLoc` | Button | | Xóa toàn bộ điều kiện lọc và tải lại danh sách ban đầu |
| 6 | `tblMe` | TableView | | Bảng danh sách mẻ sản xuất tìm được theo điều kiện lọc |
| 7 | `colMaMe` | TableColumn | | Cột mã mẻ sản xuất |
| 8 | `colTenSP` | TableColumn | | Cột tên sản phẩm của mẻ |
| 9 | `colSoLuong` | TableColumn | | Cột số lượng sản phẩm trong mẻ |
| 10 | `colNgaySX` | TableColumn | | Cột ngày sản xuất của mẻ |
| 11 | `colNhanVien` | TableColumn | | Cột tên nhân viên thực hiện mẻ sản xuất |
| 12 | `tblChiTiet` | TableView | | Bảng chi tiết nguồn gốc nguyên liệu của mẻ đang chọn |
| 13 | `colTenNL` | TableColumn | | Cột tên nguyên liệu đã dùng |
| 14 | `colSoLuongDung` | TableColumn | | Cột số lượng nguyên liệu đã dùng trong mẻ |
| 15 | `colMaLo` | TableColumn | | Cột mã lô nhập kho của nguyên liệu |
| 16 | `colMaVach` | TableColumn | | Cột mã vạch của lô nguyên liệu |
| 17 | `colNSX` | TableColumn | | Cột ngày sản xuất của lô nguyên liệu |
| 18 | `colHSD` | TableColumn | | Cột hạn sử dụng của lô nguyên liệu |
| 19 | `colNCC` | TableColumn | | Cột tên nhà cung cấp của lô nguyên liệu |
| 20 | `colSDT` | TableColumn | | Cột số điện thoại nhà cung cấp |
| 21 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái thành công hoặc thông báo lỗi ở cuối màn hình |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải toàn bộ danh sách mẻ sản xuất lên `tblMe`. `tblChiTiet` hiển thị trống với hướng dẫn chọn mẻ. | |
| 2 | Chọn button `btnTimKiem` | Lọc danh sách mẻ sản xuất trên `tblMe` theo tên sản phẩm và khoảng thời gian đã nhập. Hiển thị kết quả tương ứng. | |
| 3 | Chọn button `btnXoaLoc` | Xóa toàn bộ điều kiện lọc tại `txtTimKiem`, `dpTuNgay`, `dpDenNgay` và tải lại toàn bộ danh sách mẻ sản xuất. | |
| 4 | Chọn dòng trên `tblMe` | Tải và hiển thị toàn bộ chi tiết nguồn gốc nguyên liệu của mẻ sản xuất đang chọn lên `tblChiTiet`. Hiển thị tóm tắt thông tin mẻ tại `lblThongBao`. | |
