# Màn hình Thống kê kinh doanh

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblAdminName` | Label | | Hiển thị tên của nhân viên quản trị đang đăng nhập |
| 2 | `cbLoaiBaoCao` | ComboBox | | Chọn kỳ hạn báo cáo bao gồm thống kê theo ngày, tháng hoặc năm |
| 3 | `dpNgayBaoCao` | DatePicker | | Chọn mốc ngày cụ thể để thực hiện lọc dữ liệu thống kê |
| 4 | Nút Xuất PDF | Button | | Kết xuất toàn bộ dữ liệu thống kê kinh doanh ra tệp tin tài liệu định dạng PDF |
| 5 | Nút Xuất Excel | Button | | Kết xuất toàn bộ dữ liệu thống kê kinh doanh ra tệp tin bảng tính định dạng Excel |
| 6 | Nút Báo cáo | Button | | Thực hiện xuất báo cáo tổng hợp chi tiết lịch sử mua hàng |
| 7 | `lblDoanhThu` | Label | | Hiển thị tổng doanh thu thu được trong kỳ báo cáo hiện tại |
| 8 | `lblChenhLechDoanhThu` | Label | | Hiển thị mức chênh lệch tỷ lệ phần trăm doanh thu so với kỳ trước |
| 9 | `lblLoiNhuan` | Label | | Hiển thị tổng số tiền lợi nhuận thực tế thu được trong kỳ báo cáo |
| 10 | `lblTongDon` | Label | | Hiển thị tổng số đơn hàng đặt bánh đã hoàn thành trong kỳ báo cáo |
| 11 | `lblKhachHang` | Label | | Hiển thị tổng số lượng khách hàng thành viên có phát sinh điểm tích lũy |
| 12 | `revenueChart` | LineChart | | Biểu đồ đường thể hiện diễn biến tăng trưởng doanh thu theo từng mốc thời gian |
| 13 | `revenuePieChart` | PieChart | | Biểu đồ tròn thể hiện tỷ lệ cơ cấu doanh thu theo từng danh mục sản phẩm bánh |
| 14 | `revenueBarChart` | BarChart | | Biểu đồ cột so sánh trực quan tổng doanh thu giữa các danh mục sản phẩm bánh |
| 15 | `vboxBestSellers` | VBox | | Thẻ hiển thị danh sách các sản phẩm bánh bán chạy nhất kèm số lượng bán |
| 16 | `tableGiaoDich` | TableView | | Bảng hiển thị thông tin danh sách các giao dịch đơn hàng mới nhất |
| 17 | Nút xem chi tiết kho | Button | | Phím tắt chuyển hướng nhanh sang màn hình kiểm kê hoặc quản lý kho hàng |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Hiển thị tên người dùng và vai trò quản trị viên lên phần header.<br>- Đặt giá trị mặc định cho ComboBox chọn kỳ hạn báo cáo là thống kê theo Ngày.<br>- Đặt giá trị mặc định cho DatePicker là ngày hiện tại.<br>- Thực hiện gọi cơ sở dữ liệu tính toán và hiển thị số liệu lên các thẻ KPI doanh thu, lợi nhuận, đơn hàng, khách hàng.<br>- Tải dữ liệu và vẽ các biểu đồ đường diễn biến doanh thu, biểu đồ tròn cơ cấu doanh thu, biểu đồ cột so sánh doanh thu.<br>- Tải danh sách bánh bán chạy nhất lên thẻ hiển thị và tải danh sách giao dịch mới nhất lên bảng `tableGiaoDich`. | |
| 2 | Chọn button Xuất PDF | - Thực hiện kết xuất toàn bộ số liệu thống kê kinh doanh và biểu đồ hiện tại ra tệp tin tài liệu định dạng PDF bằng JasperReports và lưu tại thư mục cấu hình.<br>- Hiển thị thông báo kết quả xuất tệp thành công kèm theo đường dẫn tệp tin PDF cho người dùng. | |
| 3 | Chọn button Xuất Excel | - Thực hiện kết xuất toàn bộ số liệu thống kê kinh doanh hiện tại ra tệp tin bảng tính định dạng Excel bằng JasperReports và lưu tại thư mục cấu hình.<br>- Hiển thị thông báo kết quả xuất tệp thành công kèm theo đường dẫn tệp tin Excel cho người dùng. | |
| 4 | Chọn button Báo cáo | - Thực hiện kết xuất báo cáo tổng hợp lịch sử giao dịch mua hàng chi tiết dưới dạng tệp tài liệu PDF và tự động lưu trữ tại thư mục. | |
| 5 | Thay đổi tiêu chí lọc hoặc mốc thời gian thống kê | - Thực hiện gọi cơ sở dữ liệu để tính toán lại toàn bộ số liệu thống kê kinh doanh theo kỳ hạn và mốc thời gian mới chọn.<br>- Cập nhật hiển thị số liệu mới lên các thẻ KPI và vẽ lại toàn bộ các biểu đồ LineChart, PieChart, BarChart trên màn hình. | |
| 6 | Chọn button xem chi tiết kho | - Chuyển màn hình giao diện làm việc hiện tại sang tab màn hình kiểm kê kho để người dùng theo dõi hàng hóa. | |
