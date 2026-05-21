# Màn hình Quản lý Sản phẩm (Tab 1 — Sản phẩm)

> **Màn hình:** Quản lý Sản phẩm (Tab 1 trong phân hệ Quản lý Sản phẩm)
> **File FXML chính:** [QuanLySanPhamView.fxml](file:///D:/Clone/src/main/resources/fxml/kho/QuanLySanPhamView.fxml)
> **File FXML tab:** [SanPhamView.fxml](file:///D:/Clone/src/main/resources/fxml/kho/SanPhamView.fxml)
> **Controller:** [SanPhamViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/kho/SanPhamViewFXMLController.java)
> **Truy cập từ:** Sidebar hệ thống

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabSanPham` | Tab | | Tab đang xét: "Sản phẩm" — hiển thị danh sách toàn bộ sản phẩm |
| 2 | `tabDanhMuc` | Tab | | Tab chuyển sang phân hệ Quản lý danh mục sản phẩm |
| 3 | `tabCongThuc` | Tab | | Tab chuyển sang phân hệ Quản lý công thức nguyên liệu |
| 4 | `tabThanhPhan` | Tab | | Tab chuyển sang phân hệ Thành phần bánh tùy chỉnh |
| 5 | `txtTimKiem` | TextField | | Ô tìm kiếm sản phẩm theo tên hoặc mã sản phẩm; lọc tức thời khi người dùng gõ phím |
| 6 | `btnLamMoi` | Button | | Xóa bộ lọc tìm kiếm và tải lại danh sách sản phẩm mới nhất từ cơ sở dữ liệu |
| 7 | `btnSua` | Button | Bị vô hiệu hóa khi chưa chọn dòng nào | Mở hộp thoại chỉnh sửa thông tin sản phẩm đang được chọn |
| 8 | `btnXoa` | Button | Bị vô hiệu hóa khi chưa chọn dòng nào | Xóa sản phẩm đang được chọn khỏi hệ thống |
| 9 | `btnThemMoi` | Button | | Mở hộp thoại thêm sản phẩm mới |
| 10 | `tblSanPham` | TableView | | Bảng hiển thị danh sách sản phẩm; hỗ trợ lọc theo từ khóa và tự động làm mới mỗi 10 giây |
| 11 | `colMaSP` | TableColumn | | Cột mã sản phẩm |
| 12 | `colTenSP` | TableColumn | | Cột tên sản phẩm |
| 13 | `colDanhMuc` | TableColumn | | Cột tên danh mục của sản phẩm |
| 14 | `colGiaVon` | TableColumn | | Cột giá vốn, hiển thị định dạng tiền VNĐ có phân cách hàng nghìn |
| 15 | `colGiaBan` | TableColumn | | Cột giá bán, hiển thị định dạng tiền VNĐ có phân cách hàng nghìn |
| 16 | `colTonKho` | TableColumn | | Cột số lượng tồn kho hiện tại |
| 17 | `lblThongBao` | Label | | Hiển thị thông báo kết quả thao tác hoặc lỗi ở cuối màn hình |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải toàn bộ danh sách sản phẩm và danh mục từ cơ sở dữ liệu, nạp vào bảng `tblSanPham`. Hai nút `btnSua` và `btnXoa` bị vô hiệu hóa ngay từ đầu vì chưa có sản phẩm nào được chọn. Chương trình tự động làm mới dữ liệu mỗi 10 giây để phản ánh thay đổi tồn kho từ bộ phận sản xuất và bán hàng. | |
| 2 | Người dùng nhập từ khóa vào `txtTimKiem` | Bảng lọc ngay lập tức tại giao diện, chỉ hiển thị sản phẩm có tên hoặc mã sản phẩm chứa từ khóa. Không cần gọi lại cơ sở dữ liệu. | Lọc diễn ra tức thời khi gõ phím |
| 3 | Người dùng chọn một dòng trong `tblSanPham` | Kích hoạt hai nút `btnSua` và `btnXoa`. Presenter cập nhật trạng thái sản phẩm đang chọn. | |
| 4 | Người dùng bỏ chọn hoặc không có dòng nào được chọn | Hai nút `btnSua` và `btnXoa` bị vô hiệu hóa trở lại. | |
| 5 | Người dùng chọn nút `btnLamMoi` | Xóa nội dung ô tìm kiếm, đặt lại bộ lọc về hiển thị tất cả, sau đó tải lại danh sách sản phẩm mới nhất từ cơ sở dữ liệu. | |
| 6 | Người dùng chọn nút `btnThemMoi` | Mở hộp thoại "Thêm Sản Phẩm" dạng modal. Hệ thống truyền vào danh sách danh mục hiện có để người dùng chọn. Nếu thêm thành công thì sau khi đóng hộp thoại, hệ thống tự động chuyển sang tab "Công thức" để người dùng nhập công thức cho sản phẩm mới. | Hộp thoại chặn tương tác màn hình chính |
| 7 | Người dùng chọn nút `btnSua` | Mở hộp thoại "Sửa Sản Phẩm" với thông tin của sản phẩm đang chọn được điền sẵn. Nếu lưu thành công thì danh sách tự động tải lại. | |
| 8 | Người dùng chọn nút `btnXoa` | Presenter yêu cầu xác nhận rồi xóa sản phẩm đang chọn khỏi cơ sở dữ liệu. Sau đó tải lại danh sách và bỏ chọn. Nếu thất bại thì hiển thị thông báo lỗi tại `lblThongBao`. | |
| 9 | Hệ thống tự động làm mới mỗi 10 giây | Presenter tải lại danh sách sản phẩm từ cơ sở dữ liệu, cập nhật `tblSanPham`. Điều này giúp cột tồn kho luôn phản ánh số liệu thực tế sau mỗi lần nhập kho hoặc bán hàng. | Chạy ngầm, không ảnh hưởng đến trải nghiệm người dùng |
