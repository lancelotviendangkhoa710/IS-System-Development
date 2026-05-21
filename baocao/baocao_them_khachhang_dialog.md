# Màn hình Thêm Khách Hàng Mới (Hộp thoại)

> **Màn hình:** Thêm Khách Hàng Mới (Modal Dialog)
> **File FXML:** [KhachHangDialog.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/KhachHangDialog.fxml)
> **Controller:** [KhachHangDialogViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/KhachHangDialogViewFXMLController.java)
> **Kích thước:** 480 px chiều rộng, modal, chặn tương tác màn hình nền
> **Mở từ:** Màn hình Quản lý Khách hàng khi nhấn nút `btnThemKhachHang`, hoặc từ hộp thoại Tạo đơn hàng khi nhấn nút thêm khách hàng mới

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề vùng header: hiển thị "Thêm khách hàng mới" với chữ trắng trên nền nâu đậm |
| 2 | `lblSubtitle` | Label | | Dòng phụ đề nhỏ phía dưới tiêu đề: hiển thị "Điền đầy đủ thông tin bên dưới" |
| 3 | `txtHoTen` | TextField | Bắt buộc nhập, không được để trống | Ô nhập họ và tên khách hàng; được tự động focus khi hộp thoại mở ra; viền chuyển sang màu cam khi đang nhập |
| 4 | `txtSDT` | TextField | Bắt buộc nhập, phải từ 10 đến 15 chữ số | Ô nhập số điện thoại; gợi ý mẫu "VD: 0901234567 (10 chữ số)" |
| 5 | `txtDiaChi` | TextField | Không bắt buộc | Ô nhập địa chỉ; gợi ý mẫu "VD: 123 Lê Lợi, Q.1, TP.HCM" |
| 6 | `lblInfoBanner` | Label | | Banner thông tin nền vàng nhạt: nhắc người dùng rằng hạng thành viên sẽ được xếp tự động dựa trên điểm tích lũy |
| 7 | `lblError` | Label | | Hiển thị thông báo lỗi validation khi dữ liệu không hợp lệ; mặc định ẩn (để trống) |
| 8 | `btnHuy` | Button | | Nút "Hủy" viền cam, nền trắng: hủy bỏ thao tác và đóng hộp thoại |
| 9 | `btnLuu` | Button | | Nút "Lưu khách hàng" nền xanh lá: kiểm tra hợp lệ rồi lưu khách hàng mới vào cơ sở dữ liệu |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo hộp thoại | Tất cả ô nhập liệu để trống, cờ theo dõi thay đổi ở trạng thái chưa có thay đổi. Tiêu đề hiển thị "Thêm khách hàng mới", nhãn nút `btnLuu` là "Lưu khách hàng". Hệ thống đăng ký sự kiện lắng nghe thay đổi cho cả ba ô nhập liệu để phát hiện dữ liệu chưa lưu. Sự kiện đóng cửa sổ cũng được chặn để bảo vệ dữ liệu chưa lưu. | |
| 2 | Người dùng nhập vào bất kỳ ô nào trong `txtHoTen`, `txtSDT`, `txtDiaChi` | Cờ theo dõi thay đổi được bật lên ngay lập tức. Từ thời điểm này, nếu người dùng cố đóng hộp thoại mà chưa lưu, hệ thống sẽ hiện cảnh báo xác nhận. | Xảy ra từ ký tự đầu tiên người dùng gõ |
| 3 | Người dùng chọn nút `btnLuu` khi `txtHoTen` để trống | Hiển thị thông báo lỗi "Vui lòng nhập tên khách hàng" tại `lblError`. Hộp thoại giữ nguyên, không đóng. | |
| 4 | Người dùng chọn nút `btnLuu` khi `txtSDT` để trống hoặc không đúng định dạng | Hiển thị thông báo lỗi "Số điện thoại không hợp lệ (10-15 chữ số)" tại `lblError`. Hộp thoại giữ nguyên, không đóng. | |
| 5 | Người dùng chọn nút `btnLuu` khi dữ liệu hợp lệ | Hệ thống gọi dịch vụ thêm khách hàng mới vào cơ sở dữ liệu. Nếu thành công thì hiện hộp thoại thông báo "Đã thêm khách hàng mới" kèm mã khách hàng vừa tạo, sau đó đóng hộp thoại và trả kết quả về màn hình gọi. Nếu thất bại thì hiển thị thông báo lỗi tại `lblError` và giữ nguyên hộp thoại. | |
| 6 | Số điện thoại đã tồn tại trong hệ thống | Cơ sở dữ liệu từ chối lưu, hệ thống hiển thị thông báo lỗi "Không thể thêm khách hàng. SĐT có thể đã tồn tại" tại `lblError`. Người dùng có thể sửa lại số điện thoại và thử lại. | |
| 7 | Người dùng chọn nút `btnHuy` khi chưa nhập gì | Đóng hộp thoại ngay lập tức, không có thông báo xác nhận, không có thay đổi nào với cơ sở dữ liệu. | |
| 8 | Người dùng chọn nút `btnHuy` sau khi đã nhập dữ liệu | Hiện hộp thoại cảnh báo "Bạn có thay đổi chưa lưu. Hủy bỏ?". Nếu chọn Đồng ý thì đóng hộp thoại và bỏ qua toàn bộ dữ liệu đã nhập. Nếu chọn Không thì quay lại hộp thoại để tiếp tục nhập. | |
| 9 | Người dùng nhấn nút X đóng cửa sổ sau khi đã nhập dữ liệu | Xử lý giống biến cố 8: hiện hộp thoại cảnh báo trước khi đóng. Nếu người dùng chọn Không thì sự kiện đóng cửa sổ bị hủy, hộp thoại vẫn còn mở. | |
