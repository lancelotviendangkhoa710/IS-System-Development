# Màn hình Truy xuất nguồn gốc nguyên liệu

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `txtTimKiem` | TextField | | Nhập từ khóa tên sản phẩm để tìm kiếm mẻ sản xuất liên quan |
| 2 | `dpTuNgay` | DatePicker | | Bộ lọc ngày bắt đầu sản xuất mẻ |
| 3 | `dpDenNgay` | DatePicker | | Bộ lọc ngày kết thúc sản xuất mẻ |
| 4 | `btnTimKiem` | Button | | Thực hiện tìm kiếm danh sách mẻ sản xuất theo bộ lọc sản phẩm và ngày |
| 5 | `btnXoaLoc` | Button | | Làm sạch các bộ lọc tìm kiếm và tải lại danh sách mẻ mặc định |
| 6 | `tblMe` | TableView | | Bảng hiển thị danh sách các mẻ sản xuất thành phẩm |
| 7 | `colMaMe` | TableColumn | | Hiển thị mã số nhận diện duy nhất của mẻ sản xuất |
| 8 | `colTenSP` | TableColumn | | Hiển thị tên sản phẩm thành phẩm được chế biến trong mẻ |
| 9 | `colSoLuong` | TableColumn | | Hiển thị số lượng thành phẩm tạo ra trong mẻ (Ví dụ: "5 cái") |
| 10 | `colNgaySX` | TableColumn | | Hiển thị ngày và giờ bắt đầu tiến hành sản xuất mẻ |
| 11 | `colNhanVien` | TableColumn | | Hiển thị tên nhân viên thợ bánh đảm nhận mẻ sản xuất |
| 12 | `tblChiTiet` | TableView | | Bảng hiển thị thông tin truy xuất chi tiết nguồn gốc nguyên liệu dùng cho mẻ đang chọn |
| 13 | `colTenNL` | TableColumn | | Hiển thị tên nguyên liệu đã được dùng |
| 14 | `colSoLuongDung` | TableColumn | | Hiển thị chính xác số lượng nguyên liệu đã hao phí cho mẻ sản xuất |
| 15 | `colMaLo` | TableColumn | | Hiển thị mã lô nhập kho ban đầu của nguyên liệu đó |
| 16 | `colMaVach` | TableColumn | | Hiển thị mã vạch định danh lô hàng nguyên liệu |
| 17 | `colNSX` | TableColumn | | Hiển thị Ngày sản xuất của lô nguyên liệu do Nhà cung cấp công bố |
| 18 | `colHSD` | TableColumn | | Hiển thị Hạn sử dụng của lô nguyên liệu |
| 19 | `colNCC` | TableColumn | | Hiển thị tên Nhà cung cấp lô nguyên liệu đó |
| 20 | `colSDT` | TableColumn | | Hiển thị số điện thoại liên lạc của Nhà cung cấp để truy cứu khi có sự cố |
| 21 | `lblThongBao` | Label | | Hiển thị thông tin kết quả giao tác hoặc trạng thái tìm kiếm |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Gán định dạng hiển thị ngày giờ và các thuộc tính liên kết dữ liệu cho hai bảng `tblMe` và `tblChiTiet`.<br>- Lắng nghe sự kiện chọn dòng trên bảng `tblMe` để tải chi tiết nguyên liệu.<br>- Gọi Presenter nạp danh sách các mẻ sản xuất hiện tại lên bảng. | |
| 2 | Chọn button `btnTimKiem` | - Lấy các tham số lọc: Từ khóa tại `txtTimKiem`, ngày bắt đầu ở `dpTuNgay`, ngày kết thúc ở `dpDenNgay`.<br>- Gọi Presenter lọc danh sách mẻ tương ứng trong CSDL và cập nhật kết quả lên bảng `tblMe`. | |
| 3 | Chọn button `btnXoaLoc` | - Xóa trắng ô tìm kiếm và hai DatePicker.<br>- Gọi presenter để nạp lại danh sách mẻ sản xuất mặc định ban đầu và xóa trắng bảng chi tiết nguồn gốc nguyên liệu `tblChiTiet`. | |
| 4 | Chọn dòng trên bảng `tblMe` | - Lấy thông tin mẻ đang chọn.<br>- Gọi presenter truy xuất chi tiết toàn bộ các lô nguyên liệu đã tiêu hao cho mẻ sản xuất đó thông qua CSDL.<br>- Hiển thị kết quả chi tiết nguồn gốc (tên nguyên liệu, mã lô, mã vạch, NSX, HSD, thông tin nhà cung cấp) lên bảng `tblChiTiet`. | Hỗ trợ quản lý chất lượng và xử lý khi phát hiện nguyên liệu lỗi |
