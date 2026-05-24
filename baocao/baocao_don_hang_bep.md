# Màn hình Đơn hàng Bếp

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimMaDon` | TextField | Phải là số nguyên dương khi nhập | Nhập mã số đơn hàng cần tìm kiếm |
| 2 | `txtTimKhachHang` | TextField | | Nhập tên khách hàng cần tìm kiếm |
| 3 | `dpNgayTheoDoi` | DatePicker | | Chọn ngày hẹn nhận bánh để lọc đơn hàng |
| 4 | `cbGioTu` | ComboBox | | Chọn mốc thời gian bắt đầu hẹn nhận bánh |
| 5 | `cbGioDen` | ComboBox | | Chọn mốc thời gian kết thúc hẹn nhận bánh |
| 6 | `cbLocTrangThaiTheoDoi` | ComboBox | | Chọn lọc theo nhóm trạng thái hoàn thành hoặc chưa hoàn thành |
| 7 | Nút tìm kiếm đơn hàng | Button | | Thực hiện lọc danh sách đơn hàng bếp theo các tiêu chí |
| 8 | `panelChuaDon` | VBox | | Vùng chứa danh sách các thẻ hiển thị tiến độ đơn hàng bếp |
| 9 | `lblThongBao` | Label | | Hiển thị kết quả tra cứu hoặc thông báo lỗi của phân hệ đơn hàng bếp |
| 10 | Thẻ đơn hàng bếp | VBox | | Hiển thị thông tin tiến độ và hành động của một đơn đặt bánh |
| 11 | `lblMaDon` | Label | | Hiển thị mã số định danh của đơn hàng |
| 12 | Huy hiệu trạng thái | Label | | Hiển thị màu sắc và tên trạng thái hiện tại của đơn hàng |
| 13 | `lblKhach` | Label | | Hiển thị tên hoặc mã số của khách hàng đặt đơn |
| 14 | `lblNgayNhan` | Label | | Hiển thị cụ thể ngày và giờ hẹn nhận bánh của đơn |
| 15 | `lblTongTien` | Label | | Hiển thị tổng số tiền thanh toán của đơn hàng |
| 16 | Nút chuyển trạng thái động | Button | Chỉ hiển thị các trạng thái cao hơn trạng thái hiện tại của đơn hàng | Chuyển trạng thái đơn hàng sang mốc tiếp theo hoặc nhảy cóc |
| 17 | `btnHuyDon` | Button | Bị ẩn khi đơn hàng đã bắt đầu sản xuất hoặc đã kết thúc | Thực hiện hủy bỏ đơn đặt bánh |
| 18 | Nhãn cảnh báo sản xuất | Label | Chỉ hiển thị khi đơn hàng đang ở trạng thái sản xuất | Cảnh báo đơn hàng đang trong quá trình sản xuất và không thể hủy |
| 19 | `btnChiTiet` | Button | | Mở hộp thoại xem thông tin chi tiết các bánh của đơn hàng |
| 20 | Hộp thoại chi tiết đơn hàng | Dialog | | Hộp thoại hiển thị các bánh bán sẵn và bánh tùy chỉnh trong đơn |
| 21 | `lblHeader` | Label | | Hiển thị tiêu đề thông tin tổng quan của đơn hàng trong hộp thoại |
| 22 | `tblBanSan` | TableView | | Bảng hiển thị danh sách các bánh bán sẵn trong đơn |
| 23 | `tblTuyChinh` | TableView | | Bảng hiển thị danh sách bánh tùy chỉnh kèm lời chúc và ghi chú thợ bánh |
| 24 | `btnDong` | Button | | Thực hiện đóng hộp thoại chi tiết đơn hàng |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Nạp các giá trị giờ từ 00:00 đến 23:30 vào `cbGioTu` và `cbGioDen` đồng thời chọn mặc định là Tất cả.<br>- Nạp các tiêu chí lọc trạng thái vào `cbLocTrangThaiTheoDoi` và chọn mặc định là Chưa hoàn thành.<br>- Đặt giá trị ngày nhận bánh là mặc định rỗng.<br>- Tải danh sách đơn đặt bánh tùy chỉnh chưa hoàn thành từ CSDL và hiển thị kết quả lên `panelChuaDon` dưới dạng danh sách các card đơn hàng. | |
| 2 | Chọn button Tìm kiếm hoặc Thay đổi bộ lọc | - Kiểm tra tính hợp lệ của mã đơn nhập tại `txtTimMaDon`.<br>- Nếu mã đơn không hợp lệ: Hiển thị thông báo lỗi chi tiết trên nhãn `lblThongBao`.<br>- Nếu hợp lệ: Thực hiện gọi CSDL lọc danh sách các đơn đặt bánh bếp khớp với các tiêu chí lọc hiện tại và hiển thị kết quả lên `panelChuaDon`. | |
| 3 | Chọn các button chuyển trạng thái động trên thẻ đơn hàng bếp | - Thực hiện gọi CSDL cập nhật trạng thái mới được chọn cho đơn hàng.<br>- Tải lại danh sách đơn hàng bếp để cập nhật tiến độ mới nhất lên giao diện. | |
| 4 | Chọn button `btnHuyDon` trên thẻ đơn hàng bếp | - Thực hiện gọi CSDL hủy bỏ đơn hàng bếp được chọn.<br>- Tải lại danh sách đơn hàng bếp để cập nhật tiến độ mới nhất lên giao diện. | |
| 5 | Chọn button `btnChiTiet` trên thẻ đơn hàng bếp | - Thực hiện tải danh sách bánh bán sẵn và bánh tùy chỉnh cùng lời chúc, ghi chú của đơn hàng bếp được chọn từ CSDL.<br>- Mở hộp thoại chi tiết đơn hàng và hiển thị dữ liệu lên bảng `tblBanSan` và `tblTuyChinh`. | |
| 6 | Chọn button `btnDong` trên hộp thoại chi tiết đơn | - Đóng hộp thoại chi tiết đơn hàng và quay lại màn hình chính của tab đơn hàng bếp. | |
