# Màn hình Quản lý nguyên liệu

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimKiem` | TextField | | Nhập tên nguyên liệu để tìm kiếm nhanh |
| 2 | Nút làm mới | Button | | Tải lại toàn bộ danh sách nguyên liệu từ cơ sở dữ liệu |
| 3 | `btnLapBaoCao` | Button | | Chuyển hướng sang màn hình kiểm kê kho để lập báo cáo kiểm kê |
| 4 | `btnSua` | Button | Bị vô hiệu hóa khi chưa chọn dòng trên bảng | Mở hộp thoại sửa đổi thông tin của nguyên liệu được chọn |
| 5 | `btnXoa` | Button | Bị vô hiệu hóa khi chưa chọn dòng trên bảng | Xóa nguyên liệu được chọn ra khỏi cơ sở dữ liệu |
| 6 | `btnThemMoi` | Button | | Mở hộp thoại thêm nguyên liệu mới kèm nhập kho lần đầu |
| 7 | `tblNguyenLieu` | TableView | | Bảng hiển thị danh sách toàn bộ nguyên liệu |
| 8 | `colSoLuongTon` | TableColumn | | Hiển thị tổng số lượng tồn kho thực tế của nguyên liệu |
| 9 | `colTenNL` | TableColumn | | Hiển thị tên cụ thể của nguyên liệu |
| 10 | `colXuatXu` | TableColumn | | Hiển thị nguồn gốc xuất xứ của nguyên liệu |
| 11 | `colDVT` | TableColumn | | Hiển thị đơn vị tính của nguyên liệu |
| 12 | `colMucTon` | TableColumn | | Hiển thị mức tồn kho an toàn tối thiểu của nguyên liệu |
| 13 | `lblThongBao` | Label | | Hiển thị các thông báo trạng thái tác vụ hoặc thông báo lỗi của màn hình |
| 14 | Hộp thoại thêm nguyên liệu mới | Dialog | | Hộp thoại nhập thông tin để thêm nguyên liệu mới kèm nhập kho đầu kỳ |
| 15 | `txtTenNL` (hộp thoại thêm) | TextField | Không được để trống | Nhập tên nguyên liệu mới cần thêm |
| 16 | `cmbDonViTinh` (hộp thoại thêm) | ComboBox | Không được để trống | Chọn đơn vị tính cho nguyên liệu mới |
| 17 | `txtMucTon` (hộp thoại thêm) | TextField | Phải là số lớn hơn hoặc bằng 0 khi nhập | Nhập mức tồn kho an toàn tối thiểu cho nguyên liệu mới |
| 18 | `txtXuatXu` (hộp thoại thêm) | TextField | | Nhập quốc gia hoặc nguồn gốc xuất xứ của nguyên liệu mới |
| 19 | `cmbNhaCungCap` (hộp thoại thêm) | ComboBox | Không được để trống | Chọn nhà cung cấp nguyên liệu cho lần nhập đầu kỳ |
| 20 | `txtSoLuong` (hộp thoại thêm) | TextField | Phải là số lớn hơn 0 khi nhập | Nhập số lượng nguyên liệu nhập kho trong lần đầu tiên |
| 21 | `txtDonGia` (hộp thoại thêm) | TextField | Phải là số lớn hơn hoặc bằng 0 khi nhập | Nhập đơn giá nhập của nguyên liệu trong lần đầu tiên |
| 22 | `dpNgaySanXuat` (hộp thoại thêm) | DatePicker | | Chọn ngày sản xuất của lô nguyên liệu nhập |
| 23 | `dpHanSuDung` (hộp thoại thêm) | DatePicker | | Chọn hạn sử dụng của lô nguyên liệu nhập |
| 24 | `lblLoi` (hộp thoại thêm) | Label | | Hiển thị các thông báo lỗi nhập liệu trong hộp thoại thêm |
| 25 | Nút hủy (hộp thoại thêm) | Button | | Đóng hộp thoại thêm nguyên liệu mới |
| 26 | Nút xác nhận thêm | Button | | Thực hiện thêm nguyên liệu mới, lập phiếu nhập kho và đóng hộp thoại |
| 27 | Hộp thoại sửa thông tin nguyên liệu | Dialog | | Hộp thoại nhập thông tin chỉnh sửa nguyên liệu hiện có |
| 28 | `txtTenNL` (hộp thoại sửa) | TextField | Không được để trống | Chỉnh sửa tên nguyên liệu |
| 29 | `cmbDonViTinh` (hộp thoại sửa) | ComboBox | Không được để trống | Chỉnh sửa đơn vị tính của nguyên liệu |
| 30 | `txtMucTon` (hộp thoại sửa) | TextField | Phải là số lớn hơn hoặc bằng 0 khi nhập | Chỉnh sửa mức tồn kho an toàn tối thiểu |
| 31 | `txtXuatXu` (hộp thoại sửa) | TextField | | Chỉnh sửa nguồn gốc xuất xứ của nguyên liệu |
| 32 | `lblLoi` (hộp thoại sửa) | Label | | Hiển thị các thông báo lỗi nhập liệu trong hộp thoại sửa |
| 33 | Nút hủy (hộp thoại sửa) | Button | | Đóng hộp thoại sửa thông tin nguyên liệu |
| 34 | Nút lưu thay đổi | Button | | Lưu các thông tin chỉnh sửa vào cơ sở dữ liệu và đóng hộp thoại |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Áp dụng phân quyền CUD: Ẩn hoặc vô hiệu hóa các nút thêm, sửa, xóa nếu người dùng không có vai trò phù hợp.<br>- Tải danh sách đơn vị tính và nhà cung cấp vào bộ nhớ đệm.<br>- Tải toàn bộ danh sách nguyên liệu hiện có và hiển thị lên bảng `tblNguyenLieu`. | |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | - Thực hiện lọc danh sách hiển thị trên bảng `tblNguyenLieu` theo từ khóa tên nguyên liệu đã nhập. | |
| 3 | Chọn button làm mới | - Xóa bỏ trạng thái dòng được chọn trên bảng `tblNguyenLieu` và tải lại toàn bộ danh sách nguyên liệu mới nhất từ cơ sở dữ liệu. | |
| 4 | Chọn button `btnLapBaoCao` | - Thực hiện chuyển hướng người dùng sang màn hình kiểm kê kho để thao tác lập báo cáo. | |
| 5 | Chọn dòng trên bảng `tblNguyenLieu` | - Kích hoạt mở khóa vô hiệu hóa của các nút `btnSua` và `btnXoa`. | |
| 6 | Chọn button `btnThemMoi` | - Mở hộp thoại thêm nguyên liệu mới để người dùng nhập thông tin và lập phiếu nhập kho đầu kỳ. | |
| 7 | Chọn button xác nhận thêm trên hộp thoại thêm nguyên liệu | - Thực hiện kiểm tra tính hợp lệ của các trường thông tin bắt buộc.<br>- Nếu không hợp lệ: Hiển thị thông báo lỗi chi tiết lên nhãn `lblLoi`.<br>- Nếu hợp lệ: Thực hiện gọi cơ sở dữ liệu thêm nguyên liệu mới và tự động tạo phiếu nhập kho đầu kỳ, đóng hộp thoại và làm mới danh sách bảng. | |
| 8 | Chọn button `btnSua` | - Tải thông tin nguyên liệu được chọn lên các trường nhập liệu tương ứng trong hộp thoại sửa thông tin nguyên liệu.<br>- Mở hộp thoại sửa thông tin nguyên liệu. | |
| 9 | Chọn button lưu thay đổi trên hộp thoại sửa nguyên liệu | - Kiểm tra tính hợp lệ của các trường thông tin chỉnh sửa.<br>- Nếu không hợp lệ: Hiển thị thông báo lỗi chi tiết lên nhãn `lblLoi`.<br>- Nếu hợp lệ: Gọi cơ sở dữ liệu cập nhật thông tin nguyên liệu, đóng hộp thoại và làm mới danh sách bảng. | |
| 10 | Chọn button `btnXoa` | - Hiển thị hộp thoại xác nhận xóa nguyên liệu được chọn.<br>- Nếu người dùng đồng ý: Thực hiện gọi cơ sở dữ liệu xóa nguyên liệu, hiển thị thông báo thành công lên `lblThongBao` và làm mới danh sách bảng. | |
| 11 | Chọn button hủy trên hộp thoại thêm hoặc sửa | - Đóng hộp thoại hiện tại mà không thực hiện lưu bất kỳ thay đổi nào. | |
