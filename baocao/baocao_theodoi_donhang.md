# Báo cáo mô tả màn hình: Theo dõi tiến độ đơn hàng

> **Màn hình:** Theo dõi tiến độ đơn hàng (Order Tracking)
> **File FXML:** [TheoDoiDonHangView.fxml](file:///D:/Clone/src/main/resources/fxml/banhang/TheoDoiDonHangView.fxml)
> **Controller:** [TheoDoiDonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/TheoDoiDonHangViewFXMLController.java)

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimMaDon` | TextField | Phải là số nguyên dương khi nhập | Tìm kiếm đơn hàng theo mã đơn |
| 2 | `txtTimKhachHang` | TextField | | Tìm kiếm đơn hàng theo tên khách hàng |
| 3 | `dpNgayTheoDoi` | DatePicker | | Lọc đơn hàng theo ngày nhận bánh |
| 4 | `cbGioTu` | ComboBox | | Lọc đơn hàng theo mốc bắt đầu của giờ nhận bánh |
| 5 | `cbGioDen` | ComboBox | | Lọc đơn hàng theo mốc kết thúc của giờ nhận bánh |
| 6 | `cbLocTrangThaiTheoDoi` | ComboBox | | Lọc trạng thái đơn hàng (Tất cả, Chưa hoàn thành, Hoàn thành) |
| 7 | `panelChuaDon` | VBox | | Vùng chứa danh sách các card hiển thị tiến độ đơn hàng |
| 8 | `lblThongBao` | Label | | Hiển thị các thông báo trạng thái, thông báo lỗi hoặc kết quả tra cứu |
| 9 | `lblMaDon` | Label (Card) | | Hiển thị mã định danh đơn hàng (VD: #90) |
| 10 | `badge` | Label (Card) | | Huy hiệu hiển thị màu sắc và tên trạng thái hiện tại của đơn |
| 11 | `lblKhach` | Label (Card) | | Hiển thị tên hoặc mã khách hàng đặt đơn |
| 12 | `lblNgayNhan` | Label (Card) | | Hiển thị thời gian cụ thể hẹn nhận bánh |
| 13 | `lblTongTien` | Label (Card) | | Hiển thị tổng số tiền thanh toán của đơn hàng |
| 14 | Nút chuyển trạng thái động | Button (Card) | | Chuyển đơn hàng sang trạng thái tiến kế tiếp hoặc nhảy cóc |
| 15 | `btnHuyDon` | Button (Card) | Vô hiệu hóa/ẩn khi đơn đang sản xuất hoặc đã kết thúc | Hủy đơn đặt bánh của khách hàng |
| 16 | Nút báo trạng thái sản xuất | Label (Card) | | Hiển thị cảnh báo "⚠ Đơn đang SX — không thể hủy" |
| 17 | `btnChiTiet` | Button (Card) | | Mở cửa sổ popup hiển thị chi tiết các sản phẩm của đơn hàng |
| 18 | `lblHeader` | Label (Dialog) | | Tiêu đề hiển thị thông tin tổng quan của đơn trong Dialog chi tiết |
| 19 | `tblBanSan` | TableView (Dialog) | | Hiển thị danh sách các loại bánh bán sẵn của đơn |
| 20 | `tblTuyChinh` | TableView (Dialog) | | Hiển thị danh sách cấu hình chi tiết của bánh tùy chỉnh |
| 21 | `btnDong` | Button (Dialog) | | Đóng popup chi tiết đơn hàng |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Nạp các giá trị giờ từ 00:00 đến 23:30 vào `cbGioTu`, `cbGioDen` và chọn mặc định "Tất cả".<br>- Nạp các tiêu chí lọc trạng thái vào `cbLocTrangThaiTheoDoi` và chọn mặc định "Chưa hoàn thành".<br>- Làm trống ô chọn ngày `dpNgayTheoDoi`.<br>- Nếu là Bếp (`bepMode`) → Tải danh sách đơn bánh tùy chỉnh chưa hoàn thành, không bật tự động làm mới.<br>- Nếu là chế độ thường → Tải toàn bộ đơn hàng hiện tại, khởi động Timeline tự động làm mới sau mỗi 10 giây. | |
| 2 | Chọn button `🔍 Tìm kiếm` hoặc Thay đổi lọc trạng thái ở `cbLocTrangThaiTheoDoi` | - Nếu mã đơn tại `txtTimMaDon` không hợp lệ (không phải số nguyên dương) → Hiển thị thông báo lỗi trên `lblThongBao`.<br>- Ngược lại → Lọc danh sách đơn hàng khớp với bộ lọc (Mã đơn, Tên khách, Ngày nhận, Khung giờ nhận bánh, Trạng thái) từ CSDL.<br>- Hiển thị kết quả lên `panelChuaDon` dưới dạng danh sách các card đơn hàng. | |
| 3 | Chọn các button chuyển trạng thái động (VD: `→ Đã cọc`, `→ Đang sản xuất`...) | - Gọi Presenter cập nhật trạng thái mới của đơn hàng xuống CSDL.<br>- Hệ thống tự động làm mới danh sách đơn hàng hiển thị sau khi cập nhật thành công. | |
| 4 | Chọn button `Hủy đơn` | - Hệ thống tiến hành hủy đơn hàng (chuyển trạng thái đơn về "Hủy").<br>- Tải lại danh sách đơn hàng để cập nhật giao diện. | |
| 5 | Chọn button `Chi tiết` | - Lấy thông tin các sản phẩm bán sẵn và các bánh tùy chỉnh thuộc đơn hàng.<br>- Mở Dialog popup hiển thị chi tiết các bảng sản phẩm cùng lời chúc, ghi chú. | |
| 6 | Chọn button `❌ Đóng` trên Dialog chi tiết | - Đóng Dialog popup chi tiết đơn hàng, quay về giao diện chính của màn hình theo dõi đơn. | |
| 7 | Hết chu kỳ 10 giây (Auto-Refresh) | - Hệ thống tự động thực hiện lại truy vấn tìm kiếm hiện tại để cập nhật tiến độ mới nhất của các đơn hàng từ CSDL mà không cần tải lại trang. | |
