# Màn hình Thêm / Sửa thành phần tùy chỉnh (Dialog dùng chung)

Dialog này được dùng chung cho 6 thao tác: Thêm và Sửa của cả 3 panel Cốt bánh, Nhân bánh, Kiểu trang trí. Tiêu đề dialog thay đổi theo từng thao tác (ví dụ: Thêm Cốt Bánh, Sửa Nhân Bánh...). Dữ liệu cũ được điền sẵn khi ở chế độ Sửa, để trống khi ở chế độ Thêm.

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTieuDe` | Label | | Tiêu đề dialog, thay đổi theo loại thao tác và loại thành phần |
| 2 | `txtTen` | TextField | Không được để trống | Ô nhập tên thành phần (tên cốt bánh / nhân bánh / kiểu trang trí) |
| 3 | `txtPhuPhi` | TextField | Phải là số nguyên không âm | Ô nhập phụ phí (đơn vị VNĐ) |
| 4 | `btnOK` | Button | Bị vô hiệu hóa khi tên trống | Xác nhận lưu thông tin |
| 5 | `btnCancel` | Button | | Hủy bỏ thao tác và đóng dialog |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình (chế độ Thêm) | Hiển thị dialog với tiêu đề tương ứng, các ô nhập liệu để trống, nút OK bị vô hiệu hóa. | |
| 2 | Khởi tạo màn hình (chế độ Sửa) | Hiển thị dialog với tiêu đề tương ứng, `txtTen` và `txtPhuPhi` được điền sẵn dữ liệu cũ, nút OK được kích hoạt. | |
| 3 | Nhập text vào `txtTen` | Kích hoạt nút OK nếu tên không trống, vô hiệu hóa nút OK nếu tên trống. | |
| 4 | Chọn button `btnOK` | Kiểm tra dữ liệu đầu vào:<br>- Nếu tên trống: Hiển thị thông báo lỗi và không đóng dialog.<br>- Nếu phụ phí không hợp lệ hoặc âm: Hiển thị thông báo lỗi và không đóng dialog.<br>- Nếu hợp lệ: Lưu thông tin vào cơ sở dữ liệu, đóng dialog và làm mới bảng danh sách tương ứng. | |
| 5 | Chọn button `btnCancel` | Hủy bỏ thao tác và đóng dialog, không lưu bất kỳ thay đổi nào. | |
