# Màn hình Kiểm kê kho — Quản lý Nguyên liệu

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `btnThemMoi` | Button | Ẩn đối với các tài khoản không có quyền CUD | Mở hộp thoại nhập thông tin để thêm mới nguyên liệu |
| 2 | `onLamMoi` | Button | | Tải lại toàn bộ danh sách nguyên liệu từ CSDL |
| 3 | `txtTimKiem` | TextField | | Nhập tên nguyên liệu để tìm kiếm nhanh |
| 4 | `tblNguyenLieu` | TableView | | Bảng hiển thị danh sách toàn bộ các loại nguyên liệu |
| 5 | `colSoLuongTon` | TableColumn | | Hiển thị số lượng tồn thực tế của nguyên liệu kèm đơn vị tính |
| 6 | `colTenNL` | TableColumn | | Hiển thị tên nguyên liệu |
| 7 | `colXuatXu` | TableColumn | | Hiển thị quốc gia/nơi xuất xứ của nguyên liệu |
| 8 | `colDVT` | TableColumn | | Hiển thị đơn vị tính của nguyên liệu (Kg, gram, cái, lít...) |
| 9 | `colMucTon` | TableColumn | | Hiển thị hạn mức tồn kho an toàn kèm theo đơn vị tính tương ứng |
| 10 | `vboxChiTiet` | VBox | Ẩn mặc định, chỉ hiện khi chọn dòng trên bảng | Bảng thông tin chi tiết và chỉnh sửa của nguyên liệu đang chọn |
| 11 | `txtTenNL` | TextField | Không được để trống | Nhập hoặc chỉnh sửa tên nguyên liệu |
| 12 | `txtXuatXu` | TextField | | Nhập hoặc chỉnh sửa nguồn gốc xuất xứ nguyên liệu |
| 13 | `cmbDonViTinh` | ComboBox | Không được để trống | Lựa chọn đơn vị đo lường tương ứng của nguyên liệu |
| 14 | `txtMucTonAnToan` | TextField | Phải là số dương; Chỉ cho phép Admin/Quản lý sửa | Nhập hạn mức tồn kho tối thiểu để hệ thống cảnh báo khi sắp hết hàng |
| 15 | `btnLuuThayDoi` | Button | Ẩn đối với các tài khoản không có quyền CUD | Lưu các chỉnh sửa thông tin nguyên liệu hiện tại xuống CSDL |
| 16 | `btnXoa` | Button | Ẩn đối với các tài khoản không có quyền CUD | Thực hiện xóa bỏ hoàn toàn nguyên liệu đang chọn khỏi CSDL |
| 17 | `lblThongBao` | Label | | Hiển thị thông điệp thông báo trạng thái, cảnh báo hoặc thành công |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Áp dụng phân quyền CUD: Ẩn hoàn toàn các nút `btnThemMoi`, `btnLuuThayDoi`, `btnXoa` nếu nhân viên không phải Admin, Quản lý hoặc Thủ kho.<br>- Áp dụng phân quyền hạn mức: Nếu nhân viên không phải Admin/Quản lý → Chuyển ô `txtMucTonAnToan` sang chế độ chỉ đọc (Read-only), đổi màu nền và hiển thị Tooltip giải thích quyền.<br>- Tải danh sách đơn vị tính và nhà cung cấp từ CSDL vào cache.<br>- Đăng ký luồng tự động làm mới danh sách (Auto-refresh) từ CSDL sau mỗi 10 giây. | Cơ chế Auto-refresh giúp cập nhật nhanh khi có nguyên liệu được thêm ở máy khác |
| 2 | Nhập từ khóa tại ô `txtTimKiem` | - Thực hiện bắt sự kiện nhả phím để tìm kiếm và lọc danh sách nguyên liệu trên bảng theo từ khóa nhập vào. | |
| 3 | Chọn dòng trên bảng `tblNguyenLieu` | - Hiển thị bảng chi tiết bên phải `vboxChiTiet` bằng cách đổi thuộc tính `visible` và `managed` sang true.<br>- Nạp toàn bộ thông tin nguyên liệu đã chọn lên các trường nhập liệu tương ứng: Tên, Xuất xứ, Mức tồn an toàn và Đơn vị tính. | |
| 4 | Chọn button `Làm mới` | - Xóa các lựa chọn trên bảng, xóa trắng các ô nhập liệu bên form chi tiết, ẩn panel chi tiết và tải lại danh sách mới nhất từ CSDL. | |
| 5 | Chọn button `btnThemMoi` | - Mở cửa sổ Modal Dialog (`ThemNguyenLieuDialog.fxml`) để thêm nguyên liệu.<br>- Nếu người dùng nhập hợp lệ và xác nhận → Gọi dịch vụ lưu thông tin, đồng thời tự động tạo phiếu nhập kho ban đầu cho nguyên liệu đó, sau đó tải lại danh sách hiển thị. | Cho phép thiết lập số lượng nhập và đơn giá nhập ngay lúc tạo mới |
| 6 | Chọn button `btnLuuThayDoi` | - Kiểm tra các ràng buộc: Tên nguyên liệu và Đơn vị tính không được trống. Mức tồn an toàn phải là số hợp lệ.<br>- Gọi presenter cập nhật thông tin xuống CSDL, đưa ra thông báo thành công và reload bảng. | |
| 7 | Chọn button `btnXoa` | - Hiển thị hộp thoại xác nhận xóa nguyên liệu đang chọn.<br>- Gọi presenter xóa nguyên liệu trong CSDL, xóa trắng form chi tiết và reload bảng dữ liệu. | |
