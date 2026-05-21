# Màn hình Sửa Sản Phẩm (Hộp thoại)

> **Màn hình:** Sửa thông tin Sản Phẩm (Modal Dialog)
> **File FXML:** [SuaSanPhamDialog.fxml](file:///D:/Clone/src/main/resources/fxml/kho/SuaSanPhamDialog.fxml)
> **Controller:** [SuaSanPhamDialogController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/kho/SuaSanPhamDialogController.java)
> **Kích thước:** 600 px chiều rộng, modal, chặn tương tác màn hình nền
> **Mở từ:** Màn hình Quản lý Sản phẩm khi chọn một dòng rồi nhấn nút `btnSua`

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề header: hiển thị "Sửa thông tin sản phẩm" với chữ trắng trên nền nâu đậm |
| 2 | `imgPreview` | ImageView | | Hiển thị ảnh hiện tại của sản phẩm; tự động tải từ đường dẫn đã lưu khi mở hộp thoại; nếu không có ảnh hoặc file không tồn tại thì vùng ảnh để trống |
| 3 | `btnChonAnh` | Button | | Mở hộp thoại chọn file ảnh PNG; hộp thoại mở tại thư mục chứa ảnh cũ nếu đã có ảnh trước đó |
| 4 | `txtTenSP` | TextField | Bắt buộc nhập, không được để trống | Ô nhập tên sản phẩm; được điền sẵn tên hiện tại khi mở |
| 5 | `cmbDanhMuc` | ComboBox | Bắt buộc chọn | Danh sách thả xuống chọn danh mục; được chọn sẵn đúng danh mục hiện tại của sản phẩm khi mở |
| 6 | `txtGiaBan` | TextField | Phải là số thực hợp lệ | Ô nhập giá bán sản phẩm; được điền sẵn giá bán hiện tại |
| 7 | `chkTuyChinh` | CheckBox | | Đánh dấu nếu sản phẩm cho phép khách tùy chỉnh; trạng thái tương ứng với cài đặt hiện tại của sản phẩm |
| 8 | `txtTGBaoQuan` | TextField | Bắt buộc nhập, phải là số nguyên lớn hơn 0 | Ô nhập thời gian bảo quản tính bằng ngày; được điền sẵn giá trị hiện tại |
| 9 | `txtTGChuanBi` | TextField | Bắt buộc nhập, phải là số nguyên lớn hơn hoặc bằng 0 | Ô nhập thời gian chuẩn bị tính bằng phút; được điền sẵn giá trị hiện tại |
| 10 | `lblLoi` | Label | | Hiển thị thông báo lỗi validation khi dữ liệu không hợp lệ; mặc định ẩn |
| 11 | `btnHuy` | Button | | Hủy bỏ thao tác và đóng hộp thoại; nếu có thay đổi chưa lưu sẽ hiện hộp thoại xác nhận trước |
| 12 | `btnLuuThayDoi` | Button | | Nút "Lưu thay đổi" — kiểm tra hợp lệ rồi lưu thay đổi và đóng hộp thoại |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo hộp thoại | Tất cả trường được điền sẵn dữ liệu của sản phẩm đang chỉnh sửa: tên, danh mục, giá bán, trạng thái tùy chỉnh, thời gian bảo quản, thời gian chuẩn bị. Ảnh hiện tại được tải và hiển thị tại `imgPreview`. Cờ theo dõi thay đổi ở trạng thái chưa có thay đổi. Sự kiện đóng cửa sổ bằng nút X được chặn để bảo vệ dữ liệu chưa lưu. | Mã sản phẩm, giá vốn và tồn kho được giữ nguyên từ sản phẩm gốc, không hiển thị để chỉnh sửa |
| 2 | Người dùng chỉnh sửa bất kỳ trường nào | Cờ theo dõi thay đổi được bật lên, từ thời điểm này hệ thống sẽ cảnh báo nếu người dùng cố đóng hộp thoại mà chưa lưu. | |
| 3 | Người dùng chọn nút `btnChonAnh` | Mở hộp thoại chọn file PNG. Nếu sản phẩm đã có ảnh cũ thì hộp thoại mở thư mục chứa ảnh đó để tiện thay thế. Nếu chọn file hợp lệ thì ảnh mới được hiển thị tại `imgPreview` và đường dẫn mới được lưu lại. | |
| 4 | Người dùng chọn nút `btnLuuThayDoi` khi `txtTenSP` để trống | Hiển thị thông báo lỗi "Tên sản phẩm không được để trống" tại `lblLoi`. Hộp thoại giữ nguyên. | |
| 5 | Người dùng chọn nút `btnLuuThayDoi` khi chưa chọn danh mục | Hiển thị thông báo lỗi "Vui lòng chọn danh mục" tại `lblLoi`. Hộp thoại giữ nguyên. | |
| 6 | Người dùng chọn nút `btnLuuThayDoi` khi thời gian bảo quản không hợp lệ | Hiển thị thông báo lỗi "Thời gian bảo quản phải là số nguyên lớn hơn 0" tại `lblLoi`. Hộp thoại giữ nguyên. | |
| 7 | Người dùng chọn nút `btnLuuThayDoi` khi thời gian chuẩn bị không hợp lệ | Hiển thị thông báo lỗi "Thời gian chuẩn bị phải là số nguyên lớn hơn hoặc bằng 0" tại `lblLoi`. Hộp thoại giữ nguyên. | |
| 8 | Người dùng chọn nút `btnLuuThayDoi` khi giá bán không phải số hợp lệ | Hiển thị thông báo lỗi "Giá bán không hợp lệ" tại `lblLoi`. Hộp thoại giữ nguyên. | |
| 9 | Người dùng chọn nút `btnLuuThayDoi` khi tất cả dữ liệu hợp lệ | Hệ thống tạo đối tượng sản phẩm cập nhật với thông tin mới, giữ nguyên mã sản phẩm, giá vốn và tồn kho từ sản phẩm gốc. Nếu người dùng không chọn ảnh mới thì giữ nguyên đường dẫn ảnh cũ. Sau đó đóng hộp thoại và trả kết quả về màn hình Quản lý Sản phẩm để cập nhật vào cơ sở dữ liệu. | |
| 10 | Người dùng chọn nút `btnHuy` khi chưa có thay đổi | Đóng hộp thoại ngay lập tức, không có thay đổi nào với cơ sở dữ liệu. | |
| 11 | Người dùng chọn nút `btnHuy` khi đã có thay đổi chưa lưu | Hiện hộp thoại cảnh báo "Bạn có thay đổi chưa lưu. Hủy bỏ?". Nếu chọn Đồng ý thì đóng hộp thoại và bỏ qua toàn bộ thay đổi. Nếu chọn Không thì quay lại hộp thoại để tiếp tục chỉnh sửa. | |
| 12 | Người dùng nhấn nút X đóng cửa sổ khi đã có thay đổi chưa lưu | Xử lý giống biến cố 11: hiện hộp thoại cảnh báo. Nếu chọn Không thì sự kiện đóng cửa sổ bị hủy, hộp thoại vẫn còn mở. | |
