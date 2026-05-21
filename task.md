# Task List — Chuẩn hóa layout toolbar cho các màn hình quản lý

## Fix nhanh
- `[ ]` Fix CSS Dialog Tạo Phiếu Nhập Kho (NhapKhoViewFXMLController.java)

## Màn hình 1: Kiểm kê kho (Nguyên liệu)
- `[ ]` NguyenLieuView.fxml — Xóa colHanhDong, thêm btnSua/btnXoa vào toolbar
- `[ ]` NguyenLieuViewFXMLController.java — Xóa setupActionsColumn, thêm btnSua/btnXoa, selection listener

## Màn hình 2: Nhà cung cấp
- `[ ]` QuanLyNhaCungCapView.fxml — Xóa colHanhDong, thêm btnSua/btnXoa vào toolbar
- `[ ]` QuanLyNhaCungCapViewFXMLController.java — Xóa setupActionsColumn, thêm btnSua/btnXoa, selection listener

## Màn hình 3: Khách hàng
- `[ ]` KhachHangView.fxml — Xóa colActions, thêm btnSua/btnXoa/btnLichSu vào toolbar
- `[ ]` KhachHangViewFXMLController.java — Xóa setupActionsColumn, thêm nút toolbar, selection listener

## Màn hình 4: Danh mục SP
- `[ ]` DanhMucSPView.fxml — Xóa inline form bên phải, bảng full-width, thêm btnSua/btnXoa vào toolbar
- `[ ]` DanhMucSPViewFXMLController.java — Xóa inline form fields, thêm btnSua/btnXoa, handlers

## Màn hình 5: Hạng thành viên
- `[ ]` HangThanhVienView.fxml — Xóa colThaoTac, thêm btnSua vào toolbar
- `[ ]` HangThanhVienController.java — Xóa colThaoTac cell factory, thêm btnSua, selection listener

## Màn hình 6: Công thức (BOM)
- `[ ]` CongThucView.fxml — Xóa inline form bên phải, bảng full-width, thêm btnSua/btnXoa vào toolbar
- `[ ]` CongThucViewFXMLController.java — Xóa inline form fields, thêm btnSua/btnXoa, handlers

## Hậu kiểm
- `[ ]` mvn compile — kiểm tra không có lỗi biên dịch
- `[ ]` POST-CODE REVIEW theo review_agent.md
