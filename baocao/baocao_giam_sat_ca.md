# Màn hình Giám sát tiền mặt đóng ca

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | Nút làm mới | Button | | Tải lại danh sách ca làm việc mới nhất từ cơ sở dữ liệu |
| 2 | `tblCa` | TableView | Tự động tô màu đỏ đối với dòng ca làm việc bị hụt tiền mặt (thực tế < hệ thống) | Bảng hiển thị danh sách các ca làm việc và đối soát tiền mặt khi đóng ca |
| 3 | `colMaCa` | TableColumn | | Hiển thị mã số định danh của ca làm việc dưới dạng nhãn CA_[mã] |
| 4 | `colNhanVien` | TableColumn | | Hiển thị tên nhân viên thực hiện ca làm việc |
| 5 | `colMayPOS` | TableColumn | | Hiển thị tên máy bán hàng hoặc máy POS được phân bổ cho ca |
| 6 | `colMoCa` | TableColumn | | Hiển thị ngày và giờ cụ thể khi mở ca làm việc |
| 7 | `colDongCa` | TableColumn | | Hiển thị ngày và giờ cụ thể khi đóng ca làm việc |
| 8 | `colTrangThai` | TableColumn | | Hiển thị trạng thái hiện tại của ca làm việc bao gồm đang hoạt động hoặc đã kết thúc |
| 9 | `colTienDau` | TableColumn | | Hiển thị số tiền mặt bàn giao ban đầu tại két khi mở ca làm việc |
| 10 | `colHeThong` | TableColumn | | Hiển thị tổng số tiền mặt tính toán theo dữ liệu giao dịch của hệ thống |
| 11 | `colThucTe` | TableColumn | | Hiển thị số tiền mặt thực tế kiểm đếm khi thực hiện đóng ca |
| 12 | `colChenhLech` | TableColumn | | Hiển thị số tiền chênh lệch phát sinh giữa thực tế đếm và tính toán hệ thống |
| 13 | `colLyDo` | TableColumn | | Hiển thị lý do hoặc giải trình của nhân viên trong trường hợp có chênh lệch tiền mặt |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Kích hoạt hiển thị hiệu ứng đang tải dữ liệu.<br>- Thực hiện gọi cơ sở dữ liệu tải danh sách toàn bộ các ca làm việc trong hệ thống và cập nhật thông tin hiển thị lên bảng `tblCa`. | |
| 2 | Chọn button làm mới | - Kích hoạt tải lại danh sách các ca làm việc mới nhất từ cơ sở dữ liệu và làm mới thông tin trên bảng hiển thị `tblCa`. | |
