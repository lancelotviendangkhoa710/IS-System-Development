# Màn hình Quản lý thành phần tùy chỉnh (Tab 4 — Thành phần tùy chỉnh)

Màn hình gồm 3 panel cùng cấu trúc: **Cốt bánh**, **Nhân bánh**, **Kiểu trang trí**. Mỗi panel có bảng danh sách và 3 nút thao tác giống nhau. Mô tả dưới đây áp dụng chung cho cả 3 panel, tên đối tượng được viết theo cú pháp `[Cot | Nhan | TrangTri]` để phân biệt.

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tblCotBanh` | TableView | | Bảng danh sách cốt bánh |
| 2 | `colTenCot` | TableColumn | | Cột tên cốt bánh |
| 3 | `colGiaVonCot` | TableColumn | | Cột giá vốn của cốt bánh |
| 4 | `colPhuPhiCot` | TableColumn | | Cột phụ phí của cốt bánh |
| 5 | `btnThemCot` | Button | | Thêm mới một loại cốt bánh |
| 6 | `btnSuaCot` | Button | | Sửa thông tin cốt bánh đang chọn |
| 7 | `btnXoaCot` | Button | | Xóa cốt bánh đang chọn |
| 8 | `tblNhanBanh` | TableView | | Bảng danh sách nhân bánh |
| 9 | `colTenNhan` | TableColumn | | Cột tên nhân bánh |
| 10 | `colGiaVonNhan` | TableColumn | | Cột giá vốn của nhân bánh |
| 11 | `colPhuPhiNhan` | TableColumn | | Cột phụ phí của nhân bánh |
| 12 | `btnThemNhan` | Button | | Thêm mới một loại nhân bánh |
| 13 | `btnSuaNhan` | Button | | Sửa thông tin nhân bánh đang chọn |
| 14 | `btnXoaNhan` | Button | | Xóa nhân bánh đang chọn |
| 15 | `tblKieuTrangTri` | TableView | | Bảng danh sách kiểu trang trí |
| 16 | `colTenTrangTri` | TableColumn | | Cột tên kiểu trang trí |
| 17 | `colGiaVonTrangTri` | TableColumn | | Cột giá vốn của kiểu trang trí |
| 18 | `colPhuPhiTrangTri` | TableColumn | | Cột phụ phí của kiểu trang trí |
| 19 | `btnThemTrangTri` | Button | | Thêm mới một kiểu trang trí |
| 20 | `btnSuaTrangTri` | Button | | Sửa thông tin kiểu trang trí đang chọn |
| 21 | `btnXoaTrangTri` | Button | | Xóa kiểu trang trí đang chọn |

> **Lưu ý:** Cấu trúc và hành vi của 3 panel hoàn toàn giống nhau. Bảng biến cố dưới đây dùng `[panel]` để chỉ chung cho cả 3 panel (Cốt bánh / Nhân bánh / Kiểu trang trí).

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải và hiển thị danh sách dữ liệu lên cả 3 bảng: Cốt bánh, Nhân bánh, Kiểu trang trí. | |
| 2 | Chọn dòng trên bảng `[panel]` | Ghi nhớ dòng đang chọn để phục vụ thao tác sửa hoặc xóa tiếp theo. | |
| 3 | Chọn button Thêm của `[panel]` | Mở hộp thoại nhập thông tin để thêm mới một bản ghi vào danh sách tương ứng. | |
| 4 | Chọn button Sửa của `[panel]` | Kiểm tra đã chọn dòng chưa:<br>- Nếu chưa chọn dòng: Hiển thị thông báo yêu cầu chọn dòng trước.<br>- Nếu đã chọn: Mở hộp thoại sửa thông tin với dữ liệu của dòng đang chọn. | |
| 5 | Chọn button Xóa của `[panel]` | Kiểm tra đã chọn dòng chưa:<br>- Nếu chưa chọn dòng: Hiển thị thông báo yêu cầu chọn dòng trước.<br>- Nếu đã chọn: Xóa bản ghi đang chọn và làm mới bảng. | |
