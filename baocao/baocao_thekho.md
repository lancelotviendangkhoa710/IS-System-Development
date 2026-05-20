# Màn hình Tra cứu thẻ kho nguyên liệu

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `cmbNguyenLieu` | ComboBox | | Lựa chọn nguyên liệu cần tra cứu thẻ kho (kèm tổng số lượng tồn hiển thị bên cạnh) |
| 2 | `dpTuNgay` | DatePicker | | Chọn ngày bắt đầu giới hạn thời gian tra cứu |
| 3 | `dpDenNgay` | DatePicker | | Chọn ngày kết thúc giới hạn thời gian tra cứu |
| 4 | `btnTimKiem` | Button | | Xác nhận thực hiện tìm kiếm biến động thẻ kho theo các bộ lọc đã chọn |
| 5 | `btnXoaLoc` | Button | | Xóa sạch các bộ lọc ngày và thiết lập lại danh sách thẻ kho |
| 6 | `lblTonDauKy` | Label | | Hiển thị lượng hàng tồn đầu kỳ tra cứu |
| 7 | `lblNhapKy` | Label | | Hiển thị lượng hàng đã nhập thêm trong kỳ tra cứu |
| 8 | `lblXuatKy` | Label | | Hiển thị lượng hàng đã xuất đi trong kỳ tra cứu |
| 9 | `lblTonCuoiKy` | Label | | Hiển thị lượng hàng tồn cuối kỳ tra cứu |
| 10 | `tblBienDong` | TableView | | Bảng danh sách lịch sử biến động thẻ kho nguyên liệu |
| 11 | `colNgay` | TableColumn | | Hiển thị ngày và giờ thực hiện giao dịch |
| 12 | `colLoai` | TableColumn | | Hiển thị loại biến động (Ví dụ: Nhập kho, Xuất sản xuất...) |
| 13 | `colMaLo` | TableColumn | | Hiển thị mã lô nguyên liệu chịu biến động |
| 14 | `colSoLuong` | TableColumn | | Hiển thị số lượng thay đổi (dấu cộng xanh dương đối với Nhập, dấu trừ đỏ đối với Xuất) |
| 15 | `colConLai` | TableColumn | | Hiển thị số lượng còn lại thực tế trong lô hàng đó |
| 16 | `lblThongBao` | Label | | Hiển thị tổng số lượng giao dịch tìm thấy hoặc thông điệp cảnh báo, thông báo thành công |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Nạp toàn bộ danh sách nguyên liệu vào `cmbNguyenLieu`. Mặc định chọn phần tử đầu tiên.<br>- Thiết lập các ô DatePicker về trống.<br>- Cấu hình bảng hiển thị `tblBienDong` và bộ định dạng số (vi_VN) lên các cột số lượng. | |
| 2 | Chọn button `btnTimKiem` | - Gọi Presenter lọc biến động thẻ kho dựa trên nguyên liệu đang chọn và khoảng thời gian từ `dpTuNgay` đến `dpDenNgay`.<br>- Cập nhật dữ liệu lên bảng `tblBienDong`.<br>- Tính toán tổng hợp và hiển thị lên các KPI thẻ: `lblTonDauKy`, `lblNhapKy`, `lblXuatKy`, `lblTonCuoiKy`. | |
| 3 | Chọn button `btnXoaLoc` | - Đặt lại giá trị của hai bộ lọc ngày `dpTuNgay` và `dpDenNgay` về null.<br>- Gọi lại luồng tra cứu mặc định để hiển thị toàn bộ lịch sử biến động của nguyên liệu hiện tại. | |
| 4 | Chọn dòng trên `cmbNguyenLieu` | - Cập nhật nguyên liệu mục tiêu cần xem.<br>- (Tùy thuộc nghiệp vụ) Tự động tra cứu hoặc chờ người dùng nhấn nút "Xem thẻ kho". | |
