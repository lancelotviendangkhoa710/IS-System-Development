# Màn hình Quản lý danh mục sản phẩm (Tab 2 — Danh mục)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabDanhMuc` | Tab | | Tab chuyển đổi đến phân hệ Quản lý danh mục |
| 2 | `txtTimKiem` | TextField | | Nhập từ khóa để lọc danh sách danh mục sản phẩm |
| 3 | `btnLamMoi` | Button | | Thiết lập lại form nhập và tải lại danh sách danh mục từ CSDL |
| 4 | `btnThemMoi` | Button | | Mở hộp thoại nhập tên danh mục để thêm mới |
| 5 | `tblDanhMuc` | TableView | | Bảng hiển thị danh sách các danh mục sản phẩm |
| 6 | `colMaDM` | TableColumn | | Cột hiển thị mã danh mục |
| 7 | `colTenDM` | TableColumn | | Cột hiển thị tên danh mục |
| 8 | `txtTenDanhMuc` | TextField | Không được để trống | Nhập hoặc chỉnh sửa tên danh mục chi tiết |
| 9 | `btnLuuThayDoi` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Lưu cập nhật tên danh mục sản phẩm vừa sửa |
| 10 | `btnXoa` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Thực hiện xóa danh mục sản phẩm đang chọn |
| 11 | `lblThongBao` | Label | | Hiển thị trạng thái hoàn thành hoặc thông báo lỗi |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách danh mục từ cơ sở dữ liệu lên bảng `tblDanhMuc`. Lắng nghe sự kiện chọn dòng trên bảng. Thiết lập thời gian tự động cập nhật lại danh sách danh mục sau mỗi 30 giây. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | Thực hiện tìm kiếm nhanh các danh mục tương ứng theo từ khóa gõ vào thông qua presenter. | |
| 3 | Chọn dòng trên bảng `tblDanhMuc` | Cập nhật thông tin chi tiết sang thẻ nhập liệu bên phải.<br>- Điền tên danh mục đã chọn vào ô nhập `txtTenDanhMuc`.<br>- Kích hoạt (enable) nút `btnLuuThayDoi` và `btnXoa`. | |
| 4 | Chọn button `Làm mới` | Gọi phương thức xóa trắng form nhập liệu `lamMoiForm()`, giải phóng lựa chọn trên bảng và tải lại danh sách danh mục mới nhất từ CSDL. | |
| 5 | Chọn button `btnThemMoi` | Mở hộp thoại `TextInputDialog` yêu cầu người dùng nhập tên danh mục mới.<br>- Nếu bỏ trống hoặc nhấn Hủy → Không thực hiện gì và hiển thị cảnh báo lỗi.<br>- Nếu nhập tên hợp lệ → Gọi presenter để ghi nhận thêm danh mục mới vào CSDL và cập nhật lại bảng hiển thị. | Hộp thoại nhập liệu thiết kế đồng bộ theo giao diện hệ thống |
| 6 | Chọn button `btnLuuThayDoi` | Thực hiện lưu cập nhật tên danh mục.<br>- Nếu tên danh mục bị bỏ trống → Báo lỗi.<br>- Nếu hợp lệ → Gọi presenter chỉnh sửa tên danh mục tương ứng trong CSDL và làm mới dữ liệu. | |
| 7 | Chọn button `btnXoa` | Gọi presenter để thực hiện xóa danh mục đang được chọn. Xóa dữ liệu hiển thị tương ứng và cập nhật lại bảng. | |
