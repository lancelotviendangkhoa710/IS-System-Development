# Màn hình Quản lý Khách hàng và Hội viên

> **Màn hình:** Quản lý Khách hàng và Hội viên
> **File FXML:** [KhachHangView.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/KhachHangView.fxml)
> **Controller:** [KhachHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/khachhang/KhachHangViewFXMLController.java)
> **Truy cập từ:** Sidebar hệ thống, dành cho Quản lý và Thu ngân

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabKhachHang` | Tab | | Tab hiển thị danh sách và thông tin khách hàng |
| 2 | `tabHangThanhVien` | Tab | Chỉ Quản lý và Admin mới truy cập được | Tab cấu hình các hạng thành viên trong chương trình tích điểm |
| 3 | `lblTotalCustomers` | Label | | Hiển thị tổng số khách hàng đang hoạt động |
| 4 | `lblNewCustomers` | Label | | Hiển thị số khách hàng đăng ký mới trong tháng hiện tại |
| 5 | `txtTimKiem` | TextField | | Ô tìm kiếm theo tên, số điện thoại hoặc mã khách hàng |
| 6 | `cbTierFilter` | ComboBox | | Lọc danh sách theo hạng thành viên; mặc định hiển thị tất cả hạng |
| 7 | `btnRefresh` | Button | | Tải lại danh sách khách hàng mới nhất từ cơ sở dữ liệu |
| 8 | `btnCheDoThungRac` | Button | | Chuyển đổi giữa chế độ xem danh sách hoạt động và thùng rác |
| 9 | `btnXuatExcel` | Button | | Xuất danh sách khách hàng hiện tại ra file Excel, lưu tự động vào thư mục report |
| 10 | `btnThemKhachHang` | Button | | Mở hộp thoại nhập thông tin để tạo khách hàng mới |
| 11 | `tblKhachHang` | TableView | | Bảng hiển thị danh sách khách hàng sau khi lọc |
| 12 | `colId` | TableColumn | | Cột mã khách hàng |
| 13 | `colName` | TableColumn | | Cột họ tên khách hàng; nhấp đúp vào dòng sẽ mở hộp thoại chỉnh sửa |
| 14 | `colPhone` | TableColumn | | Cột số điện thoại |
| 15 | `colAddress` | TableColumn | | Cột địa chỉ |
| 16 | `colRegDate` | TableColumn | | Cột ngày đăng ký |
| 17 | `colPoints` | TableColumn | | Cột điểm tích lũy, hiển thị dạng "điểm hiện tại / ngưỡng hạng tiếp theo" |
| 18 | `colTier` | TableColumn | | Cột hạng thành viên hiện tại |
| 19 | `colActions` | TableColumn | | Cột chứa các nút thao tác trên từng dòng |
| 20 | `btnLichSu` | Button | Hiển thị ở chế độ bình thường | Mở hộp thoại lịch sử mua hàng của khách hàng đó |
| 21 | `btnXoa` | Button | Chỉ hiển thị với Quản lý và Admin | Chuyển khách hàng vào thùng rác (xóa mềm) |
| 22 | `btnKhoiPhuc` | Button | Chỉ hiển thị khi đang ở chế độ thùng rác | Khôi phục khách hàng trở lại danh sách hoạt động |
| 23 | `lblPageInfo` | Label | | Hiển thị thông tin phân trang, ví dụ "Hiển thị 1–10 của 20" |
| 24 | `paginationBox` | HBox | | Vùng chứa các nút chuyển trang |
| 25 | `lblThongBao` | Label | | Hiển thị thông báo kết quả thao tác hoặc lỗi ở cuối màn hình |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Hệ thống kiểm tra vai trò của người dùng đang đăng nhập. Nếu là Thu ngân thì tab Hạng thành viên bị vô hiệu hóa. Sau đó tải toàn bộ danh sách khách hàng từ cơ sở dữ liệu, nạp danh sách hạng thành viên vào bộ lọc, hiển thị thống kê tổng khách hàng và khách hàng mới trong tháng. Chương trình tự động làm mới dữ liệu mỗi 10 giây. | |
| 2 | Người dùng nhập từ khóa vào ô tìm kiếm hoặc chọn hạng tại bộ lọc | Bảng lọc ngay lập tức phía giao diện mà không cần truy vấn cơ sở dữ liệu. Chỉ giữ lại các khách hàng có họ tên, số điện thoại hoặc mã số chứa từ khóa, đồng thời khớp với hạng thành viên đã chọn. | Lọc diễn ra tức thời |
| 3 | Người dùng chọn nút làm mới | Tải lại toàn bộ danh sách khách hàng mới nhất từ cơ sở dữ liệu và cập nhật bảng hiển thị. | |
| 4 | Người dùng chọn nút thêm khách hàng | Mở hộp thoại "Thêm Khách Hàng" dạng modal. Nếu người dùng lưu thành công, danh sách tự động tải lại. | Hộp thoại chặn tương tác màn hình chính |
| 5 | Người dùng nhấp đúp vào một dòng trong bảng | Mở hộp thoại "Chỉnh Sửa Khách Hàng" với thông tin của khách hàng đó được điền sẵn. Nếu người dùng lưu thành công, danh sách tự động tải lại. | Không hoạt động khi đang ở chế độ thùng rác |
| 6 | Người dùng chọn nút Lịch sử trên một dòng | Mở hộp thoại "Lịch Sử Mua Hàng" của khách hàng tương ứng, hiển thị tổng đơn đã đặt, tổng chi tiêu và danh sách các đơn hàng. | |
| 7 | Người dùng chọn nút Xóa trên một dòng | Hiện hộp thoại xác nhận. Nếu đồng ý, khách hàng được chuyển vào thùng rác và ẩn khỏi danh sách hoạt động; danh sách tự động tải lại sau đó. Nếu hủy, không có thay đổi nào xảy ra. | Chỉ Quản lý và Admin mới thấy nút này |
| 8 | Người dùng chọn nút Thùng Rác | Chuyển sang chế độ xem thùng rác: bảng hiển thị các khách hàng đã xóa mềm, nút thao tác trên mỗi dòng chuyển thành Khôi Phục, nút Thùng Rác đổi nhãn thành Xem Tất Cả. | |
| 9 | Người dùng ở chế độ thùng rác và chọn nút Khôi Phục | Hiện hộp thoại xác nhận. Nếu đồng ý, khách hàng được khôi phục trở lại danh sách hoạt động; bảng tự động tải lại. Nếu hủy, không có thay đổi. | |
| 10 | Người dùng chọn nút Xem Tất Cả khi đang ở chế độ thùng rác | Thoát chế độ thùng rác, trở về danh sách khách hàng đang hoạt động. Nút thao tác trên mỗi dòng chuyển lại thành Lịch Sử và Xóa. | |
| 11 | Người dùng chọn nút xuất Excel | Hệ thống xuất danh sách khách hàng hiện tại ra file Excel, lưu tự động vào thư mục report với tên tệp được đặt theo ngày giờ. | |
| 12 | Người dùng chọn tab Hạng Thành Viên | Chuyển sang màn hình cấu hình hạng thành viên, hiển thị bảng danh sách các hạng kèm điểm tối thiểu và phần trăm giảm giá tương ứng. | Chỉ Quản lý và Admin mới truy cập được |

---

## Bảng 3 — Hộp thoại liên quan

### 3.1 — Hộp thoại Thêm / Chỉnh sửa khách hàng

> **File FXML:** [KhachHangDialog.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/KhachHangDialog.fxml)
> **Controller:** [KhachHangDialogViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/KhachHangDialogViewFXMLController.java)
> **Kích thước:** 480 px chiều rộng, modal

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề hộp thoại: hiển thị "Thêm khách hàng mới" hoặc "Chỉnh sửa khách hàng" tùy chế độ mở |
| 2 | `txtHoTen` | TextField | Bắt buộc nhập | Ô nhập họ và tên khách hàng |
| 3 | `txtSDT` | TextField | Bắt buộc nhập, đúng định dạng 10 chữ số | Ô nhập số điện thoại |
| 4 | `txtDiaChi` | TextField | | Ô nhập địa chỉ, không bắt buộc |
| 5 | `lblError` | Label | | Hiển thị thông báo lỗi khi dữ liệu không hợp lệ |
| 6 | `btnHuy` | Button | | Đóng hộp thoại, không lưu thay đổi |
| 7 | `btnLuu` | Button | | Kiểm tra hợp lệ rồi lưu thông tin khách hàng vào cơ sở dữ liệu |

**Biến cố:**

| STT | Biến cố | Xử lý |
|-----|---------|-------|
| 1 | Khởi tạo hộp thoại ở chế độ thêm mới | Các ô nhập liệu để trống, tiêu đề hiển thị "Thêm khách hàng mới". |
| 2 | Khởi tạo hộp thoại ở chế độ chỉnh sửa | Thông tin khách hàng được điền sẵn vào các ô, tiêu đề hiển thị "Chỉnh sửa khách hàng". |
| 3 | Người dùng chọn nút Luu | Hệ thống kiểm tra họ tên không được trống và số điện thoại phải đúng 10 chữ số. Nếu hợp lệ thì lưu vào cơ sở dữ liệu và đóng hộp thoại. Nếu không hợp lệ thì hiển thị thông báo lỗi tại `lblError` và giữ nguyên hộp thoại. |
| 4 | Người dùng chọn nút Huy | Đóng hộp thoại, không thực hiện thay đổi nào. |

---

### 3.2 — Hộp thoại Lịch sử mua hàng

> **File FXML:** [LichSuMuaHangDialog.fxml](file:///D:/Clone/src/main/resources/fxml/khachhang/LichSuMuaHangDialog.fxml)
> **Controller:** [LichSuMuaHangDialogViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/khachhang/LichSuMuaHangDialogViewFXMLController.java)
> **Kích thước:** 900 x 560 px, modal

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTenKhachHang` | Label | | Hiển thị tên khách hàng đang xem lịch sử |
| 2 | `lblTongDon` | Label | | Hiển thị tổng số đơn hàng đã đặt |
| 3 | `lblTongChiTieu` | Label | | Hiển thị tổng chi tiêu tích lũy của khách hàng |
| 4 | `tblDoanhSach` | TableView | | Bảng danh sách các đơn hàng |
| 5 | `colMaDon` | TableColumn | | Cột mã đơn hàng |
| 6 | `colNgayDat` | TableColumn | | Cột ngày đặt hàng |
| 7 | `colTongTien` | TableColumn | | Cột tổng tiền đơn hàng |
| 8 | `colTrangThai` | TableColumn | | Cột trạng thái đơn hàng |
| 9 | `btnXuatPDF` | Button | | Xuất lịch sử mua hàng của khách hàng ra file PDF |
| 10 | `btnDong` | Button | | Đóng hộp thoại, trả quyền điều khiển về màn hình chính |

**Biến cố:**

| STT | Biến cố | Xử lý |
|-----|---------|-------|
| 1 | Khởi tạo hộp thoại | Nạp thông tin khách hàng vào tiêu đề, tính tổng đơn và tổng chi tiêu, nạp danh sách đơn hàng vào bảng. Nếu khách hàng chưa có đơn nào thì bảng hiển thị thông báo "Khách hàng chưa có đơn hàng nào". |
| 2 | Người dùng chọn nút Xuất PDF | Xuất toàn bộ lịch sử mua hàng của khách hàng ra file PDF và lưu vào thư mục report. |
| 3 | Người dùng chọn nút Đóng | Đóng hộp thoại, trả quyền điều khiển về màn hình Quản lý Khách hàng. |
