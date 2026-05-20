# Màn hình Quản lý thành phần tùy chỉnh (Tab 4 — Thành phần tùy chỉnh)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabThanhPhan` | Tab | | Tab chuyển đổi đến phân hệ Quản lý thành phần bánh tùy chỉnh |
| 2 | `tblCotBanh` | TableView | | Bảng hiển thị danh sách các loại cốt bánh có thể tùy chỉnh |
| 3 | `colTenCot` | TableColumn | | Cột hiển thị tên loại cốt bánh |
| 4 | `colGiaVonCot` | TableColumn | | Cột hiển thị giá vốn ước lượng của cốt bánh (được tính bằng 40% phụ phí) |
| 5 | `colPhuPhiCot` | TableColumn | | Cột hiển thị giá phụ phí cộng thêm của cốt bánh |
| 6 | *(btnThemCot)* | Button | | Mở hộp thoại thêm cốt bánh tùy chỉnh mới |
| 7 | *(btnSuaCot)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng cốt bánh | Mở hộp thoại chỉnh sửa cốt bánh đang chọn |
| 8 | *(btnXoaCot)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng cốt bánh | Thực hiện xóa mềm cốt bánh đang chọn khỏi danh mục hiển thị |
| 9 | `tblNhanBanh` | TableView | | Bảng hiển thị danh sách các loại nhân bánh có thể tùy chỉnh |
| 10 | `colTenNhan` | TableColumn | | Cột hiển thị tên nhân bánh |
| 11 | `colGiaVonNhan` | TableColumn | | Cột hiển thị giá vốn ước lượng của nhân bánh (được tính bằng 45% phụ phí) |
| 12 | `colPhuPhiNhan` | TableColumn | | Cột hiển thị giá phụ phí cộng thêm của nhân bánh |
| 13 | *(btnThemNhan)* | Button | | Mở hộp thoại thêm nhân bánh tùy chỉnh mới |
| 14 | *(btnSuaNhan)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng nhân bánh | Mở hộp thoại chỉnh sửa nhân bánh đang chọn |
| 15 | *(btnXoaNhan)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng nhân bánh | Thực hiện xóa mềm nhân bánh đang chọn khỏi danh mục hiển thị |
| 16 | `tblKieuTrangTri` | TableView | | Bảng hiển thị danh sách các kiểu trang trí bánh có thể tùy chỉnh |
| 17 | `colTenTrangTri` | TableColumn | | Cột hiển thị tên kiểu trang trí |
| 18 | `colGiaVonTrangTri` | TableColumn | | Cột hiển thị giá vốn ước lượng của kiểu trang trí (được tính bằng 30% phụ phí) |
| 19 | `colPhuPhiTrangTri` | TableColumn | | Cột hiển thị giá phụ phí cộng thêm của kiểu trang trí |
| 20 | *(btnThemTrangTri)* | Button | | Mở hộp thoại thêm kiểu trang trí mới |
| 21 | *(btnSuaTrangTri)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng kiểu trang trí | Mở hộp thoại chỉnh sửa kiểu trang trí đang chọn |
| 22 | *(btnXoaTrangTri)* | Button | Vô hiệu hóa hoặc báo lỗi nếu chưa chọn dòng kiểu trang trí | Thực hiện xóa mềm kiểu trang trí đang chọn khỏi danh mục hiển thị |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách cốt bánh, nhân bánh và kiểu trang trí từ cơ sở dữ liệu lên 3 bảng tương ứng. Đăng ký bộ lắng nghe sự kiện chọn dòng trên từng bảng để tự động tính toán tổng giá vốn ước tính. | |
| 2 | Chọn dòng trên bảng `tblCotBanh` hoặc `tblNhanBanh` hoặc `tblKieuTrangTri` | Thực hiện tính toán ước lượng tổng giá vốn cho tổ hợp bánh tùy chỉnh đang chọn.<br>- Giá vốn = (Phụ phí cốt bánh x 0.4) + (Phụ phí nhân bánh x 0.45) + (Phụ phí trang trí x 0.3). | Cập nhật được tính toán nhanh trực tiếp trên giao diện |
| 3 | Chọn button thêm mới `➕` trên bất kỳ phân hệ nào | Mở hộp thoại nhập liệu tùy chỉnh dạng modal.<br>- Người dùng nhập Tên thành phần bánh mới và Phụ phí (yêu cầu là số nguyên dương hợp lệ).<br>- Nếu nhấn OK → Gọi dịch vụ chạy ngầm để lưu vào CSDL, tải lại bảng dữ liệu tương ứng. | |
| 4 | Chọn button sửa `✏️` trên bất kỳ phân hệ nào | Kiểm tra dòng đang chọn trên bảng tương ứng.<br>- Nếu chưa chọn dòng → Hiển thị cảnh báo lỗi.<br>- Nếu hợp lệ → Mở hộp thoại chỉnh sửa modal, hiển thị thông tin cũ của đối tượng để người dùng cập nhật.<br>- Nếu lưu thành công → Ghi nhận thay đổi vào CSDL và cập nhật lại bảng. | |
| 5 | Chọn button xóa `🗑` trên bất kỳ phân hệ nào | Kiểm tra dòng đang chọn trên bảng tương ứng.<br>- Nếu chưa chọn dòng → Hiển thị cảnh báo lỗi.<br>- Nếu hợp lệ → Hiển thị cảnh báo xác nhận xóa mềm.<br>- Nếu người dùng đồng ý → Gọi dịch vụ cập nhật ẩn thành phần đó khỏi danh sách trong CSDL và làm mới bảng. | |
