# Màn hình Giới hạn nhận đơn

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tblCauHinh` | TableView | | Bảng hiển thị danh sách các cấu hình giới hạn nhận đơn hiện tại |
| 2 | `colLoai` | TableColumn | | Hiển thị loại đơn hoặc loại sản phẩm được cấu hình giới hạn |
| 3 | `colGioiHan` | TableColumn | | Hiển thị số lượng giới hạn đơn hoặc sản phẩm tối đa trong ngày |
| 4 | `colMoTa` | TableColumn | | Hiển thị mô tả ngắn về cấu hình giới hạn nhận đơn |
| 5 | `colCapNhat` | TableColumn | | Hiển thị thời gian cập nhật cấu hình giới hạn lần cuối |
| 6 | `txtGioiHanTuyChinhMoi` | TextField | Phải là số nguyên dương khi nhập | Nhập giới hạn số đơn bánh tùy chỉnh tối đa trong ngày |
| 7 | `cmbSanPhamBanLe` | ComboBox | | Chọn sản phẩm bán lẻ cần thiết lập cấu hình giới hạn số lượng |
| 8 | `txtGioiHanSanPham` | TextField | Phải là số nguyên dương khi nhập | Nhập giới hạn số lượng sản phẩm bán lẻ tối đa được nhận trong ngày |
| 9 | Nút làm mới giới hạn | Button | | Thực hiện tải lại danh sách cấu hình giới hạn nhận đơn từ hệ thống |
| 10 | `btnLuuCauHinh` | Button | | Thực hiện lưu các thông tin thiết lập cấu hình giới hạn mới |
| 11 | `btnHuy` | Button | | Hủy bỏ các thông tin đang chỉnh sửa và làm sạch form thiết lập |
| 12 | `lblThongBao` | Label | | Hiển thị thông báo kết quả lưu hoặc thông báo lỗi của phân hệ giới hạn |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Tải danh sách cấu hình giới hạn nhận đơn hiện tại từ CSDL và hiển thị lên bảng `tblCauHinh`.<br>- Làm trống các ô nhập liệu của form. | |
| 2 | Chọn button làm mới giới hạn | - Thực hiện tải lại danh sách cấu hình giới hạn nhận đơn hiện tại từ CSDL và làm mới dữ liệu trên bảng `tblCauHinh`. | |
| 3 | Chọn button `btnLuuCauHinh` | - Kiểm tra tính hợp lệ của giới hạn số lượng bánh tùy chỉnh tại `txtGioiHanTuyChinhMoi` hoặc số lượng sản phẩm bán lẻ tại `txtGioiHanSanPham`.<br>- Nếu không hợp lệ: Hiển thị thông báo lỗi chi tiết lên nhãn `lblThongBao`.<br>- Nếu hợp lệ: Thực hiện gọi CSDL lưu thông tin thiết lập giới hạn nhận đơn mới.<br>- Hiển thị thông báo lưu cấu hình thành công lên nhãn `lblThongBao`, cập nhật danh sách hiển thị trên bảng `tblCauHinh` và làm trống các trường nhập liệu của form. | |
| 4 | Chọn button `btnHuy` | - Thực hiện xóa sạch toàn bộ các thông tin đang nhập trong form thiết lập giới hạn nhận đơn để người dùng thực hiện nhập lại. | |
