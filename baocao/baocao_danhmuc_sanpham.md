# Màn hình Quản lý danh mục sản phẩm (Tab 2 — Danh mục)

> **Màn hình:** Quản lý danh mục sản phẩm (Tab lồng trong màn hình Quản lý Sản phẩm)
> **File FXML chính:** [QuanLySanPhamView.fxml](file:///D:/Clone/src/main/resources/fxml/kho/QuanLySanPhamView.fxml)
> **File FXML tab:** [DanhMucSPView.fxml](file:///D:/Clone/src/main/resources/fxml/kho/DanhMucSPView.fxml)
> **Controller:** [DanhMucSPViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/kho/DanhMucSPViewFXMLController.java)
> **Truy cập từ:** Sidebar hệ thống -> Phân hệ Quản lý Sản phẩm -> Chọn Tab "Danh mục"

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabDanhMuc` | Tab | | Tab chuyển đổi sang phân hệ Quản lý danh mục sản phẩm |
| 2 | `btnLamMoi` | Button | | Nút "Làm mới" (↺) — giải phóng lựa chọn, xóa sạch form chi tiết bên phải và tải lại danh sách danh mục từ cơ sở dữ liệu |
| 3 | `btnThemMoi` | Button | | Nút "Thêm mới" (➕) — mở hộp thoại nhập tên để tạo danh mục sản phẩm mới |
| 4 | `txtTimKiem` | TextField | | Ô nhập từ khóa để lọc danh sách danh mục (nhập tên hoặc mã danh mục) |
| 5 | `tblDanhMuc` | TableView | | Bảng hiển thị danh sách các danh mục sản phẩm trong hệ thống |
| 6 | `colMaDM` | TableColumn | | Cột hiển thị Mã danh mục |
| 7 | `colTenDM` | TableColumn | | Cột hiển thị Tên danh mục |
| 8 | `txtTenDanhMuc` | TextField | Bắt buộc nhập, không được để trống | Ô nhập tên danh mục chi tiết ở thẻ bên phải; tự động điền sẵn tên danh mục được chọn |
| 9 | `btnLuuThayDoi` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Nút "Lưu thay đổi" (💾) — kiểm tra hợp lệ rồi cập nhật tên danh mục vào cơ sở dữ liệu |
| 10 | `btnXoa` | Button | Vô hiệu hóa khi chưa chọn dòng trên bảng | Nút "Xóa danh mục" (🗑) — xóa danh mục đang được chọn khỏi hệ thống (nếu không có ràng buộc liên quan) |
| 11 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái thành công hoặc thông báo lỗi ở cuối màn hình |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Gọi Presenter để truy vấn cơ sở dữ liệu lấy toàn bộ danh sách danh mục sản phẩm hiện có, nạp vào bảng `tblDanhMuc`. Đăng ký sự kiện lắng nghe chọn dòng cho bảng. Khởi chạy bộ đếm thời gian tự động làm mới ngầm (Auto-Refresh) sau mỗi 30 giây. Vô hiệu hóa hai nút `btnLuuThayDoi` và `btnXoa`. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | Lọc nhanh danh sách danh mục ngay tại bộ nhớ phía giao diện theo từ khóa được nhập (không phân biệt chữ hoa, chữ thường và không cần gọi lại cơ sở dữ liệu). | Lọc diễn ra tức thì khi người dùng gõ phím |
| 3 | Chọn một dòng trên bảng `tblDanhMuc` | Lấy danh mục được chọn, điền tên danh mục vào ô `txtTenDanhMuc`. Đồng thời kích hoạt (enable) hai nút `btnLuuThayDoi` và `btnXoa`. | Dòng được chọn hiển thị màu nền vàng nhạt nổi bật |
| 4 | Người dùng bỏ chọn dòng hoặc bảng rỗng | Thiết lập lại form chi tiết bằng phương thức `lamMoiForm()`: xóa nội dung ô `txtTenDanhMuc`, hủy lựa chọn trên bảng và vô hiệu hóa trở lại hai nút `btnLuuThayDoi`, `btnXoa`. | Tránh thao tác nhầm lẫn dữ liệu |
| 5 | Chọn nút `btnLamMoi` (↺) | Gọi phương thức `lamMoiForm()` để dọn dẹp các lựa chọn cũ và ô nhập liệu, sau đó gọi Presenter truy vấn lại danh sách danh mục mới nhất từ cơ sở dữ liệu. | |
| 6 | Chọn nút `btnThemMoi` (➕) | Mở hộp thoại `TextInputDialog` đồng bộ giao diện Amber để người dùng nhập tên danh mục mới.<br>- Nếu người dùng để trống tên hoặc chọn Hủy: Hiển thị thông báo lỗi "Tên danh mục không được để trống." tại `lblThongBao`.<br>- Nếu nhập tên hợp lệ: Gọi Presenter ghi nhận thêm danh mục mới vào cơ sở dữ liệu. Khi thành công, hệ thống tự động làm mới danh sách và hiện thông báo thành công. | Hộp thoại nhập liệu thiết kế đồng bộ theo giao diện Amber của hệ thống |
| 7 | Chọn nút `btnLuuThayDoi` (💾) | Hệ thống kiểm tra tên danh mục trong `txtTenDanhMuc`:<br>- Nếu trống: hiển thị thông báo lỗi "Tên danh mục không được để trống." tại `lblThongBao`.<br>- Nếu hợp lệ: Gọi Presenter cập nhật tên danh mục tương ứng vào cơ sở dữ liệu. Khi thành công, cập nhật trực tiếp tên mới lên dòng trên bảng hiển thị và báo thành công. | |
| 8 | Chọn nút `btnXoa` (🗑) | Gọi Presenter để thực hiện xóa danh mục sản phẩm đang được chọn.<br>- Nếu danh mục đã chứa sản phẩm (ràng buộc khóa ngoại): cơ sở dữ liệu từ chối xóa, hệ thống hiển thị thông báo lỗi ràng buộc tại `lblThongBao`.<br>- Nếu xóa thành công: Hiển thị thông báo thành công, xóa danh mục khỏi bảng và tự động làm mới form chi tiết. | Đảm bảo tính toàn vẹn của cơ sở dữ liệu |
