# Màn hình Quản lý sản phẩm (Tab 1 — Sản phẩm)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabSanPham` | Tab | | Tab chuyển đổi đến phân hệ Quản lý sản phẩm |
| 2 | `txtTimKiem` | TextField | | Nhập tên hoặc mã sản phẩm để tìm kiếm |
| 3 | `btnLamMoi` | Button | | Làm mới, đồng bộ và tải lại danh sách sản phẩm từ cơ sở dữ liệu |
| 4 | `btnSua` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Mở hộp thoại sửa đổi thông tin sản phẩm đang chọn |
| 5 | `btnXoa` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Thực hiện xóa sản phẩm đang chọn |
| 6 | `btnThemMoi` | Button | | Mở hộp thoại thêm sản phẩm mới vào hệ thống |
| 7 | `tblSanPham` | TableView | | Bảng hiển thị danh sách sản phẩm |
| 8 | `colMaSP` | TableColumn | | Cột hiển thị mã sản phẩm |
| 9 | `colTenSP` | TableColumn | | Cột hiển thị tên sản phẩm |
| 10 | `colDanhMuc` | TableColumn | | Cột hiển thị tên danh mục của sản phẩm |
| 11 | `colGiaVon` | TableColumn | | Cột hiển thị giá vốn của sản phẩm |
| 12 | `colGiaBan` | TableColumn | | Cột hiển thị giá bán của sản phẩm |
| 13 | `colTonKho` | TableColumn | | Cột hiển thị số lượng tồn kho của sản phẩm |
| 14 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái tải dữ liệu hoặc thông báo lỗi |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Thiết lập cấu trúc các cột trên bảng `tblSanPham`. Tải danh sách sản phẩm từ CSDL để hiển thị lên bảng. Đồng bộ dữ liệu bản đồ danh mục. Lắng nghe sự kiện chọn dòng trên bảng. Kích hoạt bộ cập nhật tự động định kỳ sau mỗi 10 giây. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | Thực hiện lọc nhanh các sản phẩm trên bảng.<br>- Nếu để trống → hiển thị toàn bộ danh sách sản phẩm.<br>- Nếu nhập từ khóa → chỉ hiển thị các sản phẩm có Tên hoặc Mã chứa từ khóa tìm kiếm (không phân biệt chữ hoa, chữ thường). | Bộ lọc thực hiện tức thì trên Client, không query lại DB |
| 3 | Chọn dòng trên bảng `tblSanPham` | Cập nhật trạng thái của các nút điều khiển.<br>- Nếu chọn một sản phẩm → Kích hoạt (enable) nút `btnSua` và `btnXoa`. Đồng thời tải chi tiết sản phẩm.<br>- Nếu bỏ chọn hoặc bảng rỗng → Vô hiệu hóa (disable) nút `btnSua` và `btnXoa`. | |
| 4 | Chọn button `btnLamMoi` | Xóa từ khóa tìm kiếm tại `txtTimKiem`. Tải lại danh sách sản phẩm mới nhất từ cơ sở dữ liệu. | |
| 5 | Chọn button `btnThemMoi` | Mở hộp thoại `ThemSanPhamDialog.fxml` dưới dạng modal.<br>- Nếu thêm thành công → Tự động chuyển hướng sang tab Công thức `tabCongThuc` để cấu hình nguyên liệu đầu vào cho sản phẩm mới. | |
| 6 | Chọn button `btnSua` | Mở hộp thoại `SuaSanPhamDialog.fxml` dạng modal để chỉnh sửa thông tin chi tiết của sản phẩm đang chọn.<br>- Nếu người dùng lưu thay đổi → Cập nhật thông tin mới vào CSDL và làm mới lại bảng danh sách. | |
| 7 | Chọn button `btnXoa` | Gọi presenter để thực hiện xóa sản phẩm được chọn.<br>- Nếu thành công → Hiển thị thông báo thành công và cập nhật lại danh sách trên bảng. | |
