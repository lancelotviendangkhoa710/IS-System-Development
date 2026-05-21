# Màn hình Quản lý Hạng Thành Viên (Tab 2)

> **Màn hình:** Quản lý Hạng Thành Viên (Tab lồng trong màn hình Quản lý Khách hàng)
> **File FXML:** [HangThanhVienView.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/HangThanhVienView.fxml)
> **Controller:** [HangThanhVienController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/khachhang/HangThanhVienController.java)
> **Truy cập từ:** Màn hình Quản lý Khách hàng, tab "Hạng thành viên" — chỉ Quản lý và Admin mới vào được

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTieuDeCard` | Label | | Tiêu đề card: hiển thị "Danh sách hạng thành viên" |
| 2 | `lblMoTaCard` | Label | | Mô tả nhỏ bên dưới: "Thiết lập mức thưởng và điều kiện cho khách hàng thân thiết" |
| 3 | `tblHangThanhVien` | TableView | | Bảng hiển thị toàn bộ các hạng thành viên trong hệ thống |
| 4 | `colTenHang` | TableColumn | | Cột tên hạng thành viên (ví dụ: Không, Bạc, Vàng, Kim cương) |
| 5 | `colDiemToiThieu` | TableColumn | | Cột điểm tối thiểu để đạt hạng tương ứng |
| 6 | `colPhanTramGiamGia` | TableColumn | | Cột phần trăm giảm giá áp dụng cho hạng đó |
| 7 | `colThaoTac` | TableColumn | | Cột chứa nút thao tác trên từng dòng |
| 8 | `btnSua` | Button | Hiển thị trên mỗi dòng của bảng | Nút "Sửa" mở hộp thoại chỉnh sửa điểm tối thiểu và phần trăm giảm giá của hạng tương ứng |
| 9 | `lblGhiChu` | Label | | Ghi chú phía dưới bảng: "Hạng được cập nhật tự động định kỳ dựa trên điểm tích lũy" |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Presenter tải toàn bộ danh sách hạng thành viên từ cơ sở dữ liệu và nạp vào `tblHangThanhVien`. Mỗi dòng hiển thị tên hạng, điểm tối thiểu, phần trăm giảm giá và nút "Sửa". Bảng bị vô hiệu hóa trong thời gian đang tải dữ liệu. | Chỉ hiển thị khi người dùng có vai trò Quản lý hoặc Admin |
| 2 | Người dùng chọn nút `btnSua` trên một dòng | Mở hộp thoại chỉnh sửa hạng thành viên tương ứng với dữ liệu hiện tại được điền sẵn. Hộp thoại gồm ba trường: tên hạng (chỉ xem, không chỉnh sửa được), điểm tối thiểu và phần trăm giảm giá. | |
| 3 | Người dùng nhập giá trị không phải số tại ô điểm tối thiểu hoặc phần trăm giảm giá rồi chọn Luu | Hiện hộp thoại thông báo lỗi "Vui lòng nhập số hợp lệ". Hộp thoại chỉnh sửa đóng lại, không lưu thay đổi nào. | |
| 4 | Người dùng nhập giá trị hợp lệ rồi chọn Luu | Hiện hộp thoại xác nhận "Cập nhật hạng [tên hạng]?". Nếu chọn Đồng ý thì Presenter gọi dịch vụ cập nhật hạng vào cơ sở dữ liệu, sau đó tải lại danh sách. Nếu chọn Hủy thì đóng hộp thoại xác nhận, không lưu thay đổi. | |
| 5 | Người dùng chọn Huy trong hộp thoại chỉnh sửa | Đóng hộp thoại chỉnh sửa, không thực hiện thay đổi nào với cơ sở dữ liệu, bảng giữ nguyên. | |
| 6 | Cập nhật hạng thành công | Hiện hộp thoại thông báo thành công. Danh sách trong `tblHangThanhVien` tự động tải lại để phản ánh thông tin mới nhất. | |
| 7 | Cập nhật hạng thất bại | Hiện hộp thoại thông báo lỗi với nội dung mô tả nguyên nhân. Bảng giữ nguyên dữ liệu cũ. | |

---

## Bảng 3 — Hộp thoại Sửa hạng thành viên (tạo động bằng code)

> **Kiểu:** Dialog tạo bằng Java (không có FXML), được tạo động khi nhấn nút Sửa
> **Tiêu đề:** "Sửa hạng: [tên hạng]"

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTenHang` | TextField | Chỉ đọc, không chỉnh sửa được | Hiển thị tên hạng hiện tại để người dùng tham chiếu |
| 2 | `txtDiemToiThieu` | TextField | Phải là số nguyên hợp lệ | Ô chỉnh sửa điểm tối thiểu để đạt hạng; điền sẵn giá trị hiện tại |
| 3 | `txtPhanTramGiamGia` | TextField | Phải là số thực hợp lệ | Ô chỉnh sửa phần trăm giảm giá của hạng; điền sẵn giá trị hiện tại |
| 4 | `btnLuu` | Button | | Xác nhận lưu thay đổi, kích hoạt kiểm tra hợp lệ và luồng xác nhận |
| 5 | `btnHuy` | Button | | Hủy bỏ, đóng hộp thoại không lưu gì |
