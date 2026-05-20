# Báo cáo mô tả màn hình: Tạo đơn POS (Bán hàng)

> **Màn hình:** Tạo đơn POS
> **File FXML:** [DonHangView.fxml](file:///D:/Clone/src/main/resources/fxml/banhang/DonHangView.fxml)
> **Controller:** [DonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/DonHangViewFXMLController.java)

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `tabTaoDon` | HBox | | Vùng chứa toàn bộ giao diện tạo đơn POS |
| 2 | `cbLocDanhMuc` | ComboBox | | Lọc danh sách sản phẩm theo danh mục hoặc chuyển sang giao diện Bánh Tùy Chỉnh |
| 3 | `txtTimKiemSanPham` | TextField | | Tìm kiếm sản phẩm theo tên |
| 4 | `scrollSanPham` | ScrollPane | | Chứa vùng hiển thị danh sách sản phẩm thông thường |
| 5 | `tileSanPham` | FlowPane | | Hiển thị danh sách các card sản phẩm dưới dạng grid |
| 6 | `scrollTuyChinh` | ScrollPane | | Chứa vùng giao diện cấu hình bánh tùy chỉnh |
| 7 | `cbCustomSp` | ComboBox | Không được để trống | Chọn loại bánh nền tùy chỉnh |
| 8 | `cbCustomKichCo` | ComboBox | | Chọn kích cỡ bánh tùy chỉnh |
| 9 | `cbCustomCotBanh` | ComboBox | | Chọn loại cốt bánh tùy chỉnh |
| 10 | `cbCustomNhanBanh` | ComboBox | | Chọn loại nhân bánh tùy chỉnh |
| 11 | `cbCustomTrangTri` | ComboBox | | Chọn kiểu trang trí bánh tùy chỉnh |
| 12 | `txtCustomLoiChuc` | TextArea | Tối đa 200 ký tự | Nhập lời chúc ghi trên bánh |
| 13 | `txtCustomGhiChu` | TextArea | Tối đa 500 ký tự | Nhập ghi chú đặc biệt cho thợ bánh |
| 14 | `spCustomSoLuong` | Spinner | Từ 1 đến 99 | Chọn số lượng bánh tùy chỉnh cần thêm |
| 15 | `lblGiaTuyChinh` | Label | | Hiển thị đơn giá ước tính của bánh tùy chỉnh hiện tại |
| 16 | `btnThemTuyChinh` | Button | | Thêm bánh tùy chỉnh đã cấu hình vào giỏ hàng |
| 17 | `tblGioHang` | TableView | | Hiển thị danh sách sản phẩm đã thêm vào giỏ hàng |
| 18 | `colTenSP` | TableColumn | | Hiển thị tên sản phẩm trong giỏ hàng |
| 19 | `colSoLuong` | TableColumn | | Hiển thị số lượng món kèm nút tăng/giảm nhanh số lượng |
| 20 | `colDonGia` | TableColumn | | Hiển thị đơn giá của sản phẩm |
| 21 | `colThanhTien` | TableColumn | | Hiển thị thành tiền tương ứng với số lượng |
| 22 | `colXoa` | TableColumn | | Chứa nút xóa nhanh sản phẩm khỏi giỏ hàng |
| 23 | `lblTongTienHang` | Label | | Hiển thị tạm tính tổng tiền hàng trước giảm giá |
| 24 | `lblTienGiamGia` | Label | | Hiển thị số tiền giảm giá (10%) |
| 25 | `lblTongThanhToan` | Label | | Hiển thị tổng tiền thanh toán thực tế sau giảm giá |
| 26 | `lblCocToiThieu` | Label | | Hiển thị số tiền cọc tối thiểu (50% tổng thanh toán) |
| 27 | `lblThongBao` | Label | | Hiển thị thông báo kết quả thao tác hoặc lỗi hệ thống |
| 28 | `btnThanhToan` | Button | Vô hiệu hóa khi giỏ hàng trống | Mở giao diện tạo đơn và tiến hành thanh toán |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Nạp danh sách danh mục từ CSDL vào `cbLocDanhMuc`.<br>- Tải danh sách sản phẩm thông thường hiển thị lên `tileSanPham`.<br>- Tải danh sách các thành phần tùy chỉnh (loại bánh, kích cỡ, cốt bánh, nhân bánh, kiểu trang trí) để nạp vào các ComboBox bánh tùy chỉnh.<br>- Thiết lập giỏ hàng trống, vô hiệu hóa `btnThanhToan`. | |
| 2 | Chọn lọc danh mục tại `cbLocDanhMuc` | - Nếu chọn "Tất cả" → Hiển thị tất cả sản phẩm thường, ẩn vùng tùy chỉnh.<br>- Nếu chọn một danh mục cụ thể → Lọc sản phẩm thường theo danh mục đó, ẩn vùng tùy chỉnh.<br>- Nếu chọn "✨ Tùy chỉnh" → Ẩn vùng sản phẩm thường (`scrollSanPham`), hiển thị giao diện tùy chỉnh bánh (`scrollTuyChinh`). | |
| 3 | Nhập từ khóa tại `txtTimKiemSanPham` | - Hệ thống lọc động danh sách trên `tileSanPham` sao cho tên sản phẩm có chứa từ khóa (không phân biệt chữ hoa, chữ thường). | |
| 4 | Chọn button `Thêm` trên card sản phẩm hoặc click vào card sản phẩm | - Thêm sản phẩm tương ứng vào giỏ hàng với số lượng mặc định là 1.<br>- Nếu sản phẩm đã có trong giỏ hàng → Tăng số lượng lên 1.<br>- Tính toán lại tổng tiền hàng, tiền giảm giá, tổng thanh toán, tiền cọc tối thiểu.<br>- Kích hoạt button `btnThanhToan`. | |
| 5 | Thay đổi thuộc tính bánh tùy chỉnh (loại bánh, kích cỡ, cốt, nhân, trang trí) | - Gọi Presenter tính toán lại đơn giá bánh dựa trên các thành phần được chọn.<br>- Cập nhật đơn giá mới lên label `lblGiaTuyChinh`. | |
| 6 | Chọn button `btnThemTuyChinh` | - Nếu chưa chọn loại bánh (`cbCustomSp`) → Hiển thị thông báo lỗi.<br>- Ngược lại → Thêm bánh tùy chỉnh cùng các lựa chọn thành phần, lời chúc, ghi chú và số lượng vào giỏ hàng.<br>- Tính toán lại các khoản tiền và kích hoạt button `btnThanhToan`. | |
| 7 | Chọn button `+` trên dòng giỏ hàng | - Tăng số lượng của sản phẩm tương ứng trong giỏ hàng lên 1 đơn vị.<br>- Cập nhật lại thành tiền của dòng và các tổng trị giá của giỏ hàng. | |
| 8 | Chọn button `-` trên dòng giỏ hàng | - Nếu số lượng lớn hơn 1 → Giảm số lượng đi 1 đơn vị.<br>- Nếu số lượng bằng 1 → Xóa sản phẩm ra khỏi giỏ hàng.<br>- Cập nhật lại các khoản tiền, nếu giỏ hàng trống thì vô hiệu hóa button `btnThanhToan`. | |
| 9 | Chọn button `btnXoa` (✕) trên dòng giỏ hàng | - Xóa hẳn sản phẩm tương ứng ra khỏi giỏ hàng.<br>- Cập nhật lại các tổng trị giá và vô hiệu hóa button `btnThanhToan` nếu giỏ hàng trống. | |
| 10 | Chọn button `btnThanhToan` | - Gọi Presenter mở hộp thoại `TaoDonHangView` để nhập thông tin khách hàng, số tiền khách đưa/cọc và hoàn tất tạo đơn đặt hàng. | |
