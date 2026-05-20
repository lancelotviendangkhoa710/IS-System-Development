# Màn hình Phân quyền hệ thống (Tab 2 — Nhân viên & Vai trò)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabNhanVien` | Tab | | Tab chuyển đến phân hệ Quản lý nhân viên |
| 2 | `tabPhanQuyen` | Tab | | Tab chuyển đến phân hệ Nhân viên & Vai trò |
| 3 | `tabVaiTro` | Tab | | Tab chuyển đến phân hệ Quyền vai trò |
| 4 | *(btnRefresh)* | Button | | Tải lại toàn bộ dữ liệu nhân viên và vai trò từ CSDL |
| 5 | `txtTimNhanVien` | TextField | | Nhập họ tên hoặc tên đăng nhập để tìm nhân viên |
| 6 | *(btnTim)* | Button | | Thực hiện tìm kiếm lọc danh sách nhân viên hiển thị |
| 7 | *(btnLuuThayDoi)* | Button | | Lưu các thay đổi phân bổ vai trò của nhân viên vào CSDL |
| 8 | `scrollMatrix` | ScrollPane | | Cuộn danh sách ma trận phân vai trò động |
| 9 | *(lblHeaderNhanVien)* | Label | | Tiêu đề cột hiển thị thông tin nhân viên ("NHÂN VIÊN") |
| 10 | *(lblHeaderVaiTro)* | Label | | Tiêu đề các cột hiển thị tên vai trò ("Quản lý", "Thu ngân", "Thợ bếp", "Thủ kho") |
| 11 | *(lblNhanVienRow)* | Label | | Nhãn hiển thị họ tên và tên đăng nhập của nhân viên trên từng dòng |
| 12 | *(chkMatrix)* | CheckBox | | Các ô tích chọn để gán hoặc hủy gán vai trò tương ứng cho từng nhân viên |
| 13 | `lblStatusVaiTro` | Label | | Hiển thị thông tin trạng thái tải dữ liệu, kết quả lọc hoặc thông báo lưu thành công |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Gọi CSDL để lấy danh sách nhân viên và vai trò. Dựng ma trận checkbox động gồm các dòng nhân viên và cột vai trò trên `scrollMatrix`. Cập nhật trạng thái tải thành công lên `lblStatusVaiTro`. | Tải dữ liệu trên Thread phụ để tránh gây đơ ứng dụng |
| 2 | Nhập từ khóa tại `txtTimNhanVien` và chọn button `🔍 Tìm` | Lọc danh sách nhân viên theo từ khóa (họ tên hoặc tên đăng nhập) không phân biệt chữ hoa chữ thường. Dựng lại ma trận checkbox động tương ứng với kết quả lọc. Cập nhật nhãn trạng thái kết quả. | |
| 3 | Chọn các checkbox vai trò của nhân viên | Đánh dấu việc thay đổi gán vai trò tạm thời cho nhân viên tương ứng trên giao diện. | |
| 4 | Chọn button `Lưu thay đổi` | Đọc danh sách vai trò được chọn của từng nhân viên từ ma trận checkbox. Chạy Thread phụ gọi dịch vụ `nhanVienService.capNhatVaiTro(maNV, dsVaiTro)` để cập nhật cập nhật quyền vào CSDL. Hiển thị thông báo lưu thành công lên `lblStatusVaiTro`. | Quá trình lưu diễn ra ngầm |
| 5 | Chọn button `Làm mới` (nút ↺) | Gọi lại tiến trình tải dữ liệu nền tảng từ CSDL và đặt lại ma trận checkbox ban đầu, xóa từ khóa tìm kiếm. | |
