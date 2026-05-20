# Màn hình Phân quyền chức năng (Tab 3 — Quyền vai trò)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabNhanVien` | Tab | | Tab chuyển đến phân hệ Quản lý nhân viên |
| 2 | `tabPhanQuyen` | Tab | | Tab chuyển đến phân hệ Nhân viên & Vai trò |
| 3 | `tabVaiTro` | Tab | | Tab chuyển đến phân hệ Quyền vai trò |
| 4 | `btnLuuPhanQuyen` | Button | | Lưu các thiết lập phân quyền chức năng của vai trò đang chọn vào CSDL |
| 5 | *(btnLamMoi)* | Button | | Tải lại thông tin phân quyền hiện tại của vai trò đang chọn |
| 6 | `lstVaiTro` | ListView | | Hiển thị danh sách vai trò để chọn phân quyền |
| 7 | `lblTenVaiTro` | Label | | Tiêu đề hiển thị vai trò đang được thiết lập phân quyền (Ví dụ: "Phân quyền: Quản lý") |
| 8 | `tblChucNang` | TableView | | Bảng hiển thị danh sách chức năng và ma trận quyền tương ứng |
| 9 | `colTenChucNang` | TableColumn | | Cột hiển thị tên chức năng hệ thống |
| 10 | `colModule` | TableColumn | | Cột hiển thị tên module kỹ thuật của chức năng |
| 11 | `colView` | TableColumn | | Cột ô CheckBox quyền "Xem" dữ liệu |
| 12 | `colAdd` | TableColumn | | Cột ô CheckBox quyền "Thêm" dữ liệu |
| 13 | `colEdit` | TableColumn | | Cột ô CheckBox quyền "Sửa" dữ liệu |
| 14 | `colDelete` | TableColumn | | Cột ô CheckBox quyền "Xóa" dữ liệu |
| 15 | `colDownload` | TableColumn | | Cột ô CheckBox quyền "Xuất" (In/Xuất báo cáo) |
| 16 | *(btnCapTatCa)* | Button | | Tích chọn tất cả các quyền của mọi chức năng đang hiển thị |
| 17 | *(btnThuHoiTatCa)* | Button | | Bỏ chọn tất cả các quyền của mọi chức năng đang hiển thị |
| 18 | `lblThongBao` | Label | | Hiển thị thông báo trạng thái tải dữ liệu, thông báo lưu thành công hoặc báo lỗi |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Tải danh sách vai trò đang hoạt động từ CSDL hiển thị lên `lstVaiTro` bên trái, tự động chọn vai trò đầu tiên. Tải danh sách quyền chi tiết tương ứng hiển thị lên bảng `tblChucNang` bên phải. Cập nhật nhãn trạng thái `lblThongBao`. | Tác vụ chạy ngầm trên Thread riêng |
| 2 | Chọn một vai trò trên `lstVaiTro` | Cập nhật tiêu đề `lblTenVaiTro` tương ứng. Gọi dịch vụ tải danh sách chức năng và quyền chi tiết được cấu hình cho vai trò đó từ CSDL hiển thị lên bảng `tblChucNang`. | |
| 3 | Tích chọn/bỏ chọn checkbox quyền trên dòng `tblChucNang` | Thay đổi trạng thái boolean của quyền tương ứng cho chức năng đó tạm thời trên giao diện. | |
| 4 | Chọn button `Cấp tất cả` | Tự động tích chọn (True) cho toàn bộ checkbox các quyền (Xem, Thêm, Sửa, Xóa, Xuất) của toàn bộ các chức năng đang hiển thị trên bảng. | |
| 5 | Chọn button `Thu hồi tất cả` | Tự động bỏ tích chọn (False) cho toàn bộ checkbox các quyền (Xem, Thêm, Sửa, Xóa, Xuất) của toàn bộ các chức năng đang hiển thị trên bảng. | |
| 6 | Chọn button `btnLuuPhanQuyen` | Vô hiệu hóa nút và chạy tác vụ ngầm gọi CSDL thông qua `phanQuyenDAO.capNhatQuyenChiTiet(...)` để lưu lại cấu hình phân quyền của từng chức năng của vai trò đó vào CSDL. Kích hoạt lại nút và thông báo lưu thành công. | Chạy ngầm tránh gây đơ giao diện |
| 7 | Chọn button `Làm mới` | Tải lại cấu hình quyền hiện tại từ CSDL của vai trò đang chọn và cập nhật lại bảng `tblChucNang`. | |
