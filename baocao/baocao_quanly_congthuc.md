# Màn hình Quản lý công thức nguyên liệu (Tab 3 — Công thức)

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `cmbChonSanPham` | ComboBox | | Ô tìm kiếm và chọn sản phẩm để xem công thức. Hỗ trợ nhập nhanh để lọc tên sản phẩm |
| 2 | `lblSanPhamDangCauHinh` | Label | | Hiển thị tên sản phẩm đang được cấu hình công thức |
| 3 | `btnLamMoi` | Button | | Làm mới dữ liệu công thức của sản phẩm đang chọn |
| 4 | `btnThemNguyenLieu` | Button | | Mở hộp thoại thêm nguyên liệu mới vào công thức |
| 5 | `tblCongThuc` | TableView | | Bảng danh sách nguyên liệu trong công thức của sản phẩm đang chọn |
| 6 | `colTenNguyenLieu` | TableColumn | | Cột tên nguyên liệu |
| 7 | `colDinhMuc` | TableColumn | | Cột định mức tiêu hao của nguyên liệu |
| 8 | `colDVT` | TableColumn | | Cột đơn vị tính của nguyên liệu |
| 9 | `colDonGia` | TableColumn | | Cột đơn giá của nguyên liệu |
| 10 | `colThanhTien` | TableColumn | | Cột thành tiền (định mức nhân đơn giá) |
| 11 | `lblTongGiaVon` | Label | | Hiển thị tổng giá vốn BOM của công thức sản phẩm đang chọn |
| 12 | `cmbNguyenLieu` | ComboBox | Bắt buộc chọn khi cập nhật định mức | Ô chọn nguyên liệu cần sửa định mức trong panel bên phải |
| 13 | `txtDinhMuc` | TextField | Bắt buộc, phải là số dương | Ô nhập định mức tiêu hao mới của nguyên liệu đang chọn |
| 14 | `lblDonViTinh` | Label | | Hiển thị đơn vị tính của nguyên liệu được chọn ở `cmbNguyenLieu` |
| 15 | `btnLuuCongThuc` | Button | | Cập nhật định mức tiêu hao của nguyên liệu đã chọn trong công thức |
| 16 | `btnXoaCongThuc` | Button | | Xóa nguyên liệu đang chọn trong bảng ra khỏi công thức |
| 17 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái thành công hoặc thông báo lỗi ở cuối màn hình |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách sản phẩm vào `cmbChonSanPham`, tải danh sách nguyên liệu vào `cmbNguyenLieu`. Bảng công thức hiển thị rỗng. | |
| 2 | Chọn sản phẩm từ `cmbChonSanPham` | Tải và hiển thị danh sách nguyên liệu trong công thức của sản phẩm vừa chọn lên `tblCongThuc`. Cập nhật `lblTongGiaVon` theo tổng giá vốn BOM tương ứng. | |
| 3 | Nhập text vào `cmbChonSanPham` | Lọc danh sách sản phẩm theo từ khóa đã nhập và hiển thị gợi ý để người dùng chọn nhanh. | |
| 4 | Chọn dòng trên `tblCongThuc` | Điền thông tin nguyên liệu và định mức của dòng được chọn vào `cmbNguyenLieu` và `txtDinhMuc` trong panel bên phải. | |
| 5 | Chọn nguyên liệu từ `cmbNguyenLieu` | Hiển thị đơn vị tính tương ứng của nguyên liệu đó tại `lblDonViTinh` ngay cạnh ô nhập định mức. | |
| 6 | Chọn button `btnThemNguyenLieu` | Kiểm tra đã chọn sản phẩm chưa:<br>- Nếu chưa chọn sản phẩm: Hiển thị lỗi yêu cầu chọn sản phẩm trước.<br>- Nếu đã chọn sản phẩm: Mở hộp thoại Thêm nguyên liệu mới vào công thức. | |
| 7 | Chọn button `btnLuuCongThuc` | Kiểm tra dữ liệu đầu vào:<br>- Nếu chưa chọn nguyên liệu: Hiển thị lỗi yêu cầu chọn dòng từ bảng.<br>- Nếu định mức không hợp lệ hoặc không phải số dương: Hiển thị lỗi.<br>- Nếu hợp lệ: Cập nhật định mức nguyên liệu trong công thức và làm mới bảng. | |
| 8 | Chọn button `btnXoaCongThuc` | Xóa nguyên liệu đang được chọn trong `tblCongThuc` ra khỏi công thức sản phẩm và làm mới bảng. | |
| 9 | Chọn button `btnLamMoi` | Tải lại danh sách công thức của sản phẩm đang chọn và xóa dữ liệu đang nhập trên form bên phải. | |
