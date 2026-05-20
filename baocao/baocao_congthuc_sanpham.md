# Màn hình Quản lý công thức sản phẩm (Tab 3 — Công thức)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabCongThuc` | Tab | | Tab chuyển đổi đến phân hệ Quản lý công thức |
| 2 | `cmbChonSanPham` | ComboBox | | Hộp chọn sản phẩm hỗ trợ tìm kiếm nhanh (Autocomplete) để tải công thức |
| 3 | `lblSanPhamDangCauHinh` | Label | | Hiển thị tên sản phẩm hiện đang được cấu hình định mức công thức |
| 4 | `btnLamMoi` | Button | | Làm mới danh sách công thức của sản phẩm đang chọn |
| 5 | `btnThemCongThuc` | Button | Phải chọn một sản phẩm từ `cmbChonSanPham` trước | Mở hộp thoại thêm nguyên liệu mới hoặc tạo nguyên liệu mới vào công thức |
| 6 | `tblCongThuc` | TableView | | Bảng hiển thị danh sách nguyên liệu cấu thành sản phẩm (BOM) |
| 7 | `colTenNguyenLieu` | TableColumn | | Cột hiển thị tên nguyên liệu |
| 8 | `colDinhMuc` | TableColumn | | Cột hiển thị định mức tiêu hao |
| 9 | `colDVT` | TableColumn | | Cột hiển thị đơn vị tính |
| 10 | `colDonGia` | TableColumn | | Cột hiển thị đơn giá nhập |
| 11 | `colThanhTien` | TableColumn | | Cột hiển thị thành tiền (Định mức x Đơn giá) |
| 12 | `lblTongGiaVon` | Label | | Hiển thị tổng giá vốn sản xuất của sản phẩm dựa trên định mức nguyên liệu (BOM) |
| 13 | `cmbNguyenLieu` | ComboBox | | Hộp chọn nguyên liệu cần chỉnh sửa định mức tiêu hao |
| 14 | `txtDinhMuc` | TextField | Phải là số thực dương lớn hơn 0 | Nhập số lượng định mức tiêu hao nguyên liệu |
| 15 | `lblDonViTinh` | Label | | Hiển thị ký hiệu đơn vị tính tương ứng bên cạnh ô nhập định mức |
| 16 | `btnLuuCongThuc` | Button | Phải chọn dòng nguyên liệu trong bảng trước | Cập nhật định mức tiêu hao nguyên liệu mới cho sản phẩm |
| 17 | `btnXoaCongThuc` | Button | Phải chọn dòng nguyên liệu trong bảng trước | Thực hiện xóa bỏ nguyên liệu khỏi công thức của sản phẩm |
| 18 | `lblThongBao` | Label | | Hiển thị thông báo kết quả thực hiện hoặc lỗi thao tác |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Thiết lập hiển thị các cột trên bảng công thức. Tải danh sách nguyên liệu, danh sách đơn vị tính và nhà cung cấp về bộ nhớ cache. Cấu hình tính năng Autocomplete cho hộp chọn `cmbChonSanPham`. | |
| 2 | Nhập từ khóa tại ô gõ của `cmbChonSanPham` | Thực hiện tìm kiếm nhanh sản phẩm theo từ khóa gõ vào.<br>- Chỉ hiển thị các sản phẩm có Tên hoặc Mã chứa từ khóa tìm kiếm và tự động hiển thị danh sách gợi ý. | Bộ lọc thực hiện ngay trên Client thông qua bộ nhớ cache |
| 3 | Chọn một sản phẩm tại `cmbChonSanPham` | Gọi presenter để tải danh sách công thức chi tiết của sản phẩm đó từ CSDL lên bảng `tblCongThuc`. Đồng thời cập nhật nhãn `lblSanPhamDangCauHinh` để biết sản phẩm nào đang được quản lý. | |
| 4 | Chọn dòng nguyên liệu trên bảng `tblCongThuc` | Đồng bộ dữ liệu sang phần chỉnh sửa bên phải.<br>- Chọn nguyên liệu tương ứng trong ComboBox `cmbNguyenLieu`.<br>- Hiển thị định mức hiện tại vào TextField `txtDinhMuc`.<br>- Hiển thị đơn vị tính tương ứng tại `lblDonViTinh`. | |
| 5 | Chọn button `Làm mới` | Tải lại danh sách nguyên liệu và công thức của sản phẩm đang chọn từ CSDL để cập nhật giá vốn mới nhất. | |
| 6 | Chọn button `btnThemCongThuc` | Kiểm tra nếu chưa chọn sản phẩm nào → Cảnh báo lỗi.<br>- Nếu đã chọn sản phẩm → Mở hộp thoại `ThemCongThucDialog.fxml` dạng modal để nhập nguyên liệu mới hoặc thêm mới nguyên liệu chưa có trong danh mục vào công thức. | Hộp thoại dạng Modal chặn tương tác màn hình chính |
| 7 | Chọn button `btnLuuCongThuc` | Kiểm tra dữ liệu đầu vào.<br>- Nếu chưa chọn nguyên liệu → Báo lỗi.<br>- Nếu định mức nhập vào không phải số thực dương hợp lệ → Báo lỗi.<br>- Nếu hợp lệ → Gọi presenter lưu thay đổi định mức của nguyên liệu trong công thức vào CSDL, cập nhật lại bảng và tính lại tổng giá vốn BOM hiển thị tại `lblTongGiaVon`. | Cập nhật được thực hiện ngầm để giao diện mượt mà |
| 8 | Chọn button `btnXoaCongThuc` | Hiển thị xác nhận xóa bỏ nguyên liệu khỏi công thức.<br>- Nếu đồng ý → Gọi presenter để xóa, cập nhật lại bảng hiển thị và tính toán lại tổng giá vốn BOM. | |
