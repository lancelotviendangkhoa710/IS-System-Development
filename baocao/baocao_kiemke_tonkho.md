# Màn hình Kiểm kê kho — Tổng quan Tồn kho

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Hiển thị tiêu đề màn hình kiểm kê kho |
| 2 | `btnLapBaoCao` | Button | | Thực hiện lập báo cáo kiểm kê phiếu nhập kho đầu kỳ và demo hiện tượng đọc ảo |
| 3 | `tblData` | TableView | | Bảng hiển thị danh sách tổng hợp số lượng tồn kho thực tế của cả nguyên liệu và thành phẩm |
| 4 | `colDate` (cột Loại) | TableColumn | | Hiển thị phân loại vật tư là nguyên liệu hay thành phẩm |
| 5 | `colUser` (cột Tên hàng) | TableColumn | | Hiển thị tên cụ thể của nguyên liệu hoặc thành phẩm |
| 6 | `colDonVi` (cột Đơn vị tính) | TableColumn | | Hiển thị đơn vị tính tương ứng của hàng hóa |
| 7 | `colContent` (cột Tồn kho) | TableColumn | | Hiển thị số lượng tồn kho thực tế trong cơ sở dữ liệu |
| 8 | `colStatus` (cột Trạng thái) | TableColumn | | Hiển thị cảnh báo trạng thái tự động bao gồm hết hàng, sắp hết hoặc đủ hàng |
| 9 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái tải dữ liệu, lỗi hoặc kết quả lập báo cáo |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Gắn nhãn tiêu đề màn hình.<br>- Liên kết các cột dữ liệu của bảng `tblData` với cấu trúc dòng tồn kho tổng hợp.<br>- Kích hoạt luồng tải dữ liệu chạy ngầm để lấy thông tin từ cơ sở dữ liệu cho cả nguyên liệu và sản phẩm thành phẩm, cập nhật kết quả lên bảng. | |
| 2 | Chọn button `btnLapBaoCao` | - Thực hiện truy vấn nhanh số lượng phiếu nhập hiện tại trong hệ thống.<br>- Hiển thị hộp thoại cảnh báo xác nhận lập báo cáo kiểm kê và hướng dẫn người dùng thử demo hiện tượng Phantom Read bằng cách tạo phiếu mới ở phiên làm việc khác.<br>- Nếu người dùng đồng ý: Vô hiệu hóa nút `btnLapBaoCao` và gọi thủ tục cơ sở dữ liệu để lập báo cáo (chạy ngầm trong khoảng thời gian delay 20 giây). | |
| 3 | Hoàn tất quá trình lập báo cáo kiểm kê | - So sánh số lượng phiếu đếm ban đầu với số dòng thực tế được trả về từ thủ tục báo cáo:<br>  + Nếu số dòng lớn hơn số đếm ban đầu: Hiển thị cảnh báo lỗi Phantom Read xảy ra do cấp độ cô lập là READ COMMITTED.<br>  + Nếu số dòng bằng số đếm ban đầu: Hiển thị thông báo xác nhận dữ liệu nhất quán không phát hiện Phantom Read do cấp độ cô lập là SERIALIZABLE.<br>- Thực hiện xuất báo cáo định dạng PDF thông qua JasperReports và tự động mở tệp tin PDF.<br>- Kích hoạt mở khóa vô hiệu hóa của nút `btnLapBaoCao`. | |
