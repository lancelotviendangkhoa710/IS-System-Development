# Màn hình Kiểm kê kho — Tổng quan Tồn kho

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề màn hình kiểm kê kho ("KIỂM KÊ KHO") |
| 2 | `tblData` | TableView | | Bảng hiển thị danh sách tổng hợp số lượng tồn kho thực tế của cả Nguyên liệu và Thành phẩm |
| 3 | `colDate` | TableColumn | | Hiển thị phân loại vật tư ("Nguyên liệu" hoặc "Thành phẩm") |
| 4 | `colUser` | TableColumn | | Hiển thị tên cụ thể của nguyên liệu hoặc sản phẩm thành phẩm |
| 5 | `colDonVi` | TableColumn | | Hiển thị đơn vị đo lường tương ứng (Kg, gram, lít, cái...) |
| 6 | `colContent` | TableColumn | | Hiển thị chính xác số lượng tồn kho thực tế trong CSDL |
| 7 | `colStatus` | TableColumn | | Hiển thị cảnh báo trạng thái tự động bằng biểu tượng màu sắc |
| 8 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái tải dữ liệu, thông báo lỗi hoặc làm mới thành công |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Gắn nhãn tiêu đề màn hình.<br>- Liên kết các cột dữ liệu của bảng `tblData` với cấu trúc dòng tồn kho tổng hợp `TonKhoRow`.<br>- Kích hoạt luồng tải dữ liệu chạy ngầm để lấy thông tin từ hai nguồn `NguyenLieuDAO` và `SanPhamDAO`. | |
| 2 | Tải dữ liệu kho thành công | - Hợp nhất danh sách nguyên liệu và sản phẩm vào bảng hiển thị.<br>- Áp dụng quy tắc tính toán trạng thái:<br>  + Nếu số lượng tồn <= 0 → Hiển thị cảnh báo màu đỏ: `⛔ Hết hàng`.<br>  + Nếu số lượng tồn <= Hạn mức an toàn (với nguyên liệu) hoặc < 5 (với thành phẩm) → Hiển thị cảnh báo màu cam: `⚠️ Sắp hết`.<br>  + Các trường hợp khác → Hiển thị trạng thái an toàn màu xanh lá: `✅ Đủ hàng`. | |
| 3 | Tải dữ liệu kho thất bại | - Đưa ra thông báo lỗi chi tiết trên dòng trạng thái `lblThongBao` để người dùng kiểm tra kết nối CSDL. | |
