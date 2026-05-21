# Báo cáo mô tả màn hình — Dialog Thêm / Sửa Nhà Cung Cấp

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề dialog: "Thêm Nhà Cung Cấp" hoặc "Sửa Nhà Cung Cấp" tuỳ chế độ |
| 2 | `txtTenNhaCungCap` | TextField | Không được để trống | Nhập tên nhà cung cấp |
| 3 | `txtSoDienThoai` | TextField | | Nhập số điện thoại nhà cung cấp |
| 4 | `txtDiaChi` | TextArea | | Nhập địa chỉ nhà cung cấp |
| 5 | `btnHuy` | Button | | Đóng dialog, không lưu thay đổi |
| 6 | `btnLuu` | Button | | Lưu thông tin nhà cung cấp |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình (chế độ Thêm) | Đặt `lblTitle` thành "Thêm Nhà Cung Cấp". Để trống `txtTenNhaCungCap`, `txtSoDienThoai`, `txtDiaChi`. | |
| 2 | Khởi tạo màn hình (chế độ Sửa) | Đặt `lblTitle` thành "Sửa Nhà Cung Cấp". Điền sẵn thông tin nhà cung cấp được chọn vào `txtTenNhaCungCap`, `txtSoDienThoai`, `txtDiaChi`. | |
| 3 | Chọn button `btnLuu` | Kiểm tra dữ liệu đầu vào.<br>- Nếu `txtTenNhaCungCap` trống → hiển thị thông báo lỗi, không lưu.<br>- Nếu hợp lệ và chế độ Thêm → thêm mới bản ghi vào cơ sở dữ liệu, đóng dialog.<br>- Nếu hợp lệ và chế độ Sửa → cập nhật bản ghi vào cơ sở dữ liệu, đóng dialog. | |
| 4 | Chọn button `btnHuy` | Đóng dialog, không lưu bất kỳ thay đổi nào. | |
| 5 | Chọn nút X trên title bar | Đóng dialog, không lưu bất kỳ thay đổi nào. | |
