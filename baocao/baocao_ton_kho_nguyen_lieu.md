# Màn hình Tồn kho Nguyên liệu

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `dpTonKhoTuNgay` | DatePicker | Mặc định là ngày đầu tiên của tháng hiện tại | Chọn ngày bắt đầu kỳ báo cáo tồn kho nguyên liệu |
| 2 | `dpTonKhoDenNgay` | DatePicker | Mặc định là ngày hiện tại | Chọn ngày kết thúc kỳ báo cáo tồn kho nguyên liệu |
| 3 | `btnXemTonKho` | Button | | Thực hiện truy vấn và tải báo cáo tồn kho nguyên liệu chi tiết |
| 4 | `lblTonKhoHetHang` | Label | | Hiển thị số lượng loại nguyên liệu đã hết hàng trong kho |
| 5 | `lblTonKhoSapHet` | Label | | Hiển thị số lượng loại nguyên liệu sắp hết hàng trong kho |
| 6 | `lblTonKhoDuHang` | Label | | Hiển thị số lượng loại nguyên liệu còn đủ hàng trong kho |
| 7 | `tableTonKho` | TableView | Tự động tô màu đỏ đối với dòng nguyên liệu có tồn cuối kỳ nhỏ hơn hoặc bằng 0 | Bảng hiển thị thông tin tồn kho chi tiết của từng loại nguyên liệu |
| 8 | `lblThongBao` | Label | | Hiển thị thông báo hoặc lỗi của phân hệ tồn kho |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Đặt giá trị ngày bắt đầu `dpTonKhoTuNgay` là ngày đầu tiên của tháng hiện tại, ngày kết thúc `dpTonKhoDenNgay` là ngày hiện tại.<br>- Tải và hiển thị báo cáo tồn kho nguyên liệu hiện tại lên bảng `tableTonKho`.<br>- Kích hoạt chu kỳ tự động làm mới ngầm sau mỗi 10 giây đối với bảng tồn kho nguyên liệu. | |
| 2 | Chọn button `btnXemTonKho` | - Thực hiện gọi CSDL lấy số liệu tổng hợp tồn kho và danh sách báo cáo chi tiết tồn kho nguyên liệu trong khoảng thời gian từ ngày bắt đầu đến ngày kết thúc đã chọn.<br>- Cập nhật số liệu hiển thị lên các nhãn KPI `lblTonKhoHetHang`, `lblTonKhoSapHet`, `lblTonKhoDuHang` và cập nhật dữ liệu lên bảng `tableTonKho`. | |
| 3 | Hết chu kỳ 10 giây (Auto-Refresh) | - Hệ thống tự động gọi CSDL thực hiện cập nhật mới số liệu báo cáo tồn kho nguyên liệu ngầm và hiển thị lên giao diện một cách âm thầm mà không xuất hiện thông báo lỗi để tránh làm phiền người dùng. | |
