# Màn hình Quản lý nhập kho

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `btnXoa` | Button | Chỉ hiển thị với Admin và Quản lý. Bị vô hiệu hóa khi chưa chọn dòng | Hủy phiếu nhập kho đang chọn |
| 2 | `btnInPhieu` | Button | Bị vô hiệu hóa khi chưa chọn dòng | In phiếu nhập kho đang chọn ra file PDF |
| 3 | `btnNhapTuFile` | Button | | Nhập kho theo dữ liệu từ file JSON hoặc CSV |
| 4 | `btnTaoPhieuNhap` | Button | | Mở hộp thoại tạo phiếu nhập kho mới |
| 5 | `tblData` | TableView | | Bảng danh sách tất cả phiếu nhập kho trong hệ thống |
| 6 | `colDate` | TableColumn | | Cột ngày giờ nhập kho |
| 7 | `colUser` | TableColumn | | Cột tên nhân viên thực hiện nhập kho |
| 8 | `colContent` | TableColumn | | Cột nội dung tóm tắt phiếu nhập (nhà cung cấp và tổng tiền) |
| 9 | `colStatus` | TableColumn | | Cột mã số phiếu nhập |
| 10 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái thành công hoặc thông báo lỗi ở cuối màn hình |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải toàn bộ danh sách phiếu nhập kho lên `tblData`. Hiển thị hoặc ẩn `btnXoa` theo vai trò người dùng hiện tại. | |
| 2 | Chọn dòng trên `tblData` | Kích hoạt `btnInPhieu`. Kích hoạt `btnXoa` nếu người dùng có quyền. | |
| 3 | Nhấp đúp vào dòng trên `tblData` | Mở hộp thoại xem chi tiết các lô hàng thuộc phiếu nhập đang chọn, bao gồm tên nguyên liệu, số lượng, đơn giá, thành tiền, hạn sử dụng và tổng tiền phiếu. | |
| 4 | Chọn button `btnTaoPhieuNhap` | Kiểm tra dữ liệu hệ thống:<br>- Nếu chưa có nhà cung cấp: Hiển thị lỗi yêu cầu thêm nhà cung cấp trước.<br>- Nếu chưa có nguyên liệu: Hiển thị lỗi yêu cầu thêm nguyên liệu trước.<br>- Nếu đủ điều kiện: Mở hộp thoại tạo phiếu nhập mới để chọn nhà cung cấp và nhập chi tiết lô hàng. | |
| 5 | Chọn button `btnNhapTuFile` | Mở hộp thoại chọn nhà cung cấp, sau đó mở cửa sổ chọn file JSON hoặc CSV. Hệ thống đọc và kiểm tra dữ liệu, hiển thị bảng xem trước:<br>- Nếu có lỗi dữ liệu: Hiển thị danh sách lỗi, không cho phép lưu.<br>- Nếu không có lỗi: Cho phép xác nhận lưu phiếu nhập. | |
| 6 | Chọn button `btnInPhieu` | Tải chi tiết phiếu nhập đang chọn và xuất ra file PDF. Hiển thị đường dẫn file PDF đã lưu khi thành công. | |
| 7 | Chọn button `btnXoa` | Hiển thị hộp thoại xác nhận hủy phiếu nhập đang chọn:<br>- Nếu xác nhận: Thực hiện hủy phiếu và hoàn kho theo quy tắc hệ thống, làm mới danh sách.<br>- Nếu hủy bỏ: Đóng hộp thoại, không thay đổi gì. | Chỉ Admin và Quản lý có quyền hủy phiếu |
