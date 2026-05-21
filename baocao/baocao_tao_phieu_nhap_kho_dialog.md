# Màn hình Tạo phiếu nhập kho (Dialog)

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `cbNCC` | ComboBox | Bắt buộc chọn | Ô chọn nhà cung cấp cho phiếu nhập kho |
| 2 | `tblChiTiet` | TableView | Phải có ít nhất 1 dòng | Bảng chi tiết các lô hàng cần nhập. Mỗi ô trong bảng có thể chỉnh sửa trực tiếp |
| 3 | `colNL` | TableColumn | Bắt buộc chọn nguyên liệu | Cột chọn nguyên liệu cần nhập từ danh sách đã có trong hệ thống |
| 4 | `colSL` | TableColumn | Phải là số dương | Cột nhập số lượng của lô hàng |
| 5 | `colDG` | TableColumn | Phải là số không âm | Cột nhập đơn giá của lô hàng (đồng) |
| 6 | `colHSD` | TableColumn | Định dạng yyyy-MM-dd | Cột nhập hạn sử dụng của lô hàng |
| 7 | `btnThemDong` | Button | Bị vô hiệu hóa nếu hệ thống chưa có nguyên liệu | Thêm một dòng nguyên liệu mới vào bảng chi tiết |
| 8 | `btnOK` | Button | | Xác nhận lưu phiếu nhập kho theo dữ liệu đã điền |
| 9 | `btnCancel` | Button | | Hủy bỏ thao tác và đóng hộp thoại |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách nhà cung cấp vào `cbNCC`. Tự động thêm một dòng nguyên liệu mặc định vào `tblChiTiet`. | |
| 2 | Chọn nguyên liệu trong cột `colNL` của một dòng | Gán tên và mã nguyên liệu cho dòng đó trong bảng. | |
| 3 | Chỉnh sửa giá trị trong cột `colSL`, `colDG`, `colHSD` | Cập nhật giá trị tương ứng ngay trong dòng đó của bảng chi tiết. | |
| 4 | Chọn button `btnThemDong` | Thêm một dòng mới vào `tblChiTiet` với nguyên liệu mặc định là nguyên liệu đầu tiên trong danh sách. | |
| 5 | Chọn button `btnOK` | Kiểm tra dữ liệu đầu vào:<br>- Nếu chưa chọn nhà cung cấp: Hiển thị lỗi và không lưu.<br>- Nếu bảng chi tiết trống: Hiển thị lỗi và không lưu.<br>- Nếu hợp lệ: Lưu phiếu nhập kho vào cơ sở dữ liệu, đóng hộp thoại và làm mới danh sách phiếu nhập. | |
| 6 | Chọn button `btnCancel` | Hủy bỏ thao tác và đóng hộp thoại, không lưu bất kỳ thay đổi nào. | |
