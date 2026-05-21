# Màn hình Thêm / Chỉnh sửa Khách Hàng (Hộp thoại)

> **Màn hình:** Thêm hoặc Chỉnh sửa Khách Hàng (Modal Dialog — 2 chế độ)
> **File FXML:** [KhachHangDialog.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/KhachHangDialog.fxml)
> **Controller:** [KhachHangDialogViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/KhachHangDialogViewFXMLController.java)
> **Kích thước:** 480 px chiều rộng · Modal, chặn tương tác màn hình nền
> **Mở từ:** Màn hình Quản lý Khách hàng khi nhấn nút Thêm Khách Hàng hoặc nhấp đúp vào một dòng trong bảng

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề hộp thoại: hiển thị "Thêm khách hàng mới" ở chế độ thêm mới, hiển thị "Chỉnh sửa Khách Hàng" ở chế độ chỉnh sửa |
| 2 | `lblSubtitle` | Label | | Dòng phụ đề: hiển thị "Điền đầy đủ thông tin bên dưới" |
| 3 | `txtHoTen` | TextField | Bắt buộc nhập, không được để trống | Ô nhập họ và tên khách hàng; ở chế độ chỉnh sửa được điền sẵn tên hiện tại |
| 4 | `txtSDT` | TextField | Bắt buộc nhập, phải từ 10 đến 15 chữ số | Ô nhập số điện thoại; ở chế độ chỉnh sửa được điền sẵn số điện thoại hiện tại |
| 5 | `txtDiaChi` | TextField | Không bắt buộc | Ô nhập địa chỉ; ở chế độ chỉnh sửa được điền sẵn địa chỉ hiện tại |
| 6 | `lblInfoBanner` | Label | | Thông báo thông tin: "Khách hàng mới sẽ được xếp hạng thành viên tự động dựa trên điểm tích lũy" |
| 7 | `lblError` | Label | | Hiển thị thông báo lỗi khi dữ liệu nhập vào không hợp lệ; mặc định để trống |
| 8 | `btnHuy` | Button | | Hủy bỏ thao tác và đóng hộp thoại; nếu có thay đổi chưa lưu sẽ hiện hộp thoại xác nhận trước |
| 9 | `btnLuu` | Button | | Ở chế độ thêm mới hiển thị nhãn "Lưu khách hàng"; ở chế độ chỉnh sửa hiển thị nhãn "Cập nhật"; thực hiện kiểm tra hợp lệ rồi lưu vào cơ sở dữ liệu |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo hộp thoại ở chế độ thêm mới | Các ô nhập liệu để trống. Tiêu đề hiển thị "Thêm khách hàng mới", nhãn nút `btnLuu` là "Lưu khách hàng". Cờ theo dõi thay đổi được đặt về trạng thái chưa có thay đổi. | Mở từ nút `btnThemKhachHang` trên màn hình chính |
| 2 | Khởi tạo hộp thoại ở chế độ chỉnh sửa | Họ tên, số điện thoại và địa chỉ của khách hàng được điền sẵn vào các ô tương ứng. Tiêu đề hiển thị "Chỉnh sửa Khách Hàng", nhãn nút `btnLuu` đổi thành "Cập nhật". | Mở khi nhấp đúp vào dòng khách hàng trong bảng |
| 3 | Người dùng chỉnh sửa bất kỳ ô nào trong `txtHoTen`, `txtSDT`, `txtDiaChi` | Hệ thống đánh dấu hộp thoại có dữ liệu chưa lưu. Cờ theo dõi thay đổi được bật lên để phục vụ xác nhận khi hủy. | Xảy ra ngay khi người dùng gõ phím đầu tiên |
| 4 | Người dùng chọn nút `btnLuu` — chế độ thêm mới | Hệ thống kiểm tra `txtHoTen` không được trống và `txtSDT` phải từ 10 đến 15 chữ số. Nếu không hợp lệ thì hiển thị thông báo lỗi tại `lblError` và giữ nguyên hộp thoại. Nếu hợp lệ thì gọi dịch vụ thêm khách hàng mới, hiện hộp thoại thông báo thành công kèm mã khách hàng vừa tạo, sau đó đóng hộp thoại. | |
| 5 | Người dùng chọn nút `btnLuu` — chế độ chỉnh sửa | Hệ thống kiểm tra dữ liệu tương tự chế độ thêm mới. Nếu hợp lệ thì gọi dịch vụ cập nhật thông tin khách hàng, hiện hộp thoại thông báo thành công, sau đó đóng hộp thoại và trả kết quả về màn hình Quản lý Khách hàng. Nếu không thể cập nhật thì hiển thị thông báo lỗi tại `lblError`. | |
| 6 | Số điện thoại đã tồn tại trong hệ thống | Cơ sở dữ liệu từ chối lưu, hệ thống hiển thị thông báo "Không thể thêm khách hàng. SĐT có thể đã tồn tại" tại `lblError`. Hộp thoại vẫn mở để người dùng chỉnh sửa lại. | Áp dụng ở chế độ thêm mới |
| 7 | Người dùng chọn nút `btnHuy` khi chưa có thay đổi | Đóng hộp thoại ngay lập tức, không thực hiện thay đổi nào đối với dữ liệu. | |
| 8 | Người dùng chọn nút `btnHuy` khi đã có thay đổi chưa lưu | Hiện hộp thoại cảnh báo "Bạn có thay đổi chưa lưu. Hủy bỏ?". Nếu chọn Đồng ý thì đóng hộp thoại và bỏ qua mọi thay đổi. Nếu chọn Không thì quay lại hộp thoại để tiếp tục chỉnh sửa. | |
| 9 | Người dùng nhấn nút X đóng cửa sổ khi đã có thay đổi chưa lưu | Xử lý giống biến cố 8: hiện hộp thoại xác nhận trước khi cho phép đóng. Nếu người dùng chọn Không thì sự kiện đóng cửa sổ bị hủy bỏ và hộp thoại vẫn còn mở. | |
