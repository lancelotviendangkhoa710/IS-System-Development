# Báo cáo mô tả màn hình: Tạo đơn hàng mới (Dialog)

> **Màn hình:** Tạo đơn hàng mới (Modal Dialog — 2 bước)
> **File FXML:** [TaoDonHangDialog.fxml](file:///D:/Clone/src/main/resources/fxml/banhang/TaoDonHangDialog.fxml)
> **Controller:** [TaoDonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/TaoDonHangViewFXMLController.java)
> **Kích thước tối thiểu:** 600 × 550 px · Modal, chặn tương tác màn hình nền
> **Mở từ:** Màn hình Bán hàng khi nhân viên thu ngân nhấn nút tạo đơn

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------| 
| 1 | `lblStep` | Label | | Hiển thị tiến trình hiện tại của dialog: "Bước 1 / 2" hoặc "Bước 2 / 2" |
| **— BƯỚC 1: Thông tin khách hàng —** |||||
| 2 | `txtSDT` | TextField | | Nhập số điện thoại để tra cứu khách hàng thành viên |
| 3 | Nút "🔍 Tìm" | Button | | Kích hoạt tra cứu khách hàng theo số điện thoại đã nhập |
| 4 | Nút "+ Thêm KH" | Button | | Mở dialog tạo mới khách hàng |
| 5 | Nút "Sửa KH" | Button | Vô hiệu hóa nếu chưa tìm được khách hàng | Mở dialog chỉnh sửa thông tin khách hàng đang chọn |
| 6 | `lblKhachInfo` | Label | | Hiển thị thông tin khách hàng tìm được hoặc thông báo đặt hàng vãng lai |
| **— BƯỚC 2A: Thanh toán ngay (mặc định) —** |||||
| 7 | `lblTongTien` | Label | | Hiển thị tổng số tiền cần thanh toán của đơn hàng |
| 8 | `btnImmediateFlow` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn luồng "💵 Thanh toán ngay" |
| 9 | `btnPreorderFlow` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn luồng "📅 Đặt trước (Preorder)" |
| 10 | `btnCash` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn hình thức "💵 Tiền mặt" |
| 11 | `btnTransfer` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn hình thức "🏦 Chuyển khoản" |
| 12 | `txtKhachDua` | TextField | Phải ≥ tổng tiền cần thanh toán | Nhập số tiền khách đưa (chỉ hiện khi chọn Tiền mặt) |
| 13 | `lblDocChuKhachDua` | Label | | Hiển thị số tiền khách đưa bằng chữ để kiểm tra tránh nhầm |
| 14 | `lblTienThua` | Label | | Hiển thị tiền thừa trả lại khách (chỉ hiện khi chọn Tiền mặt) |
| 15 | `imgQR` | ImageView | | Hiển thị mã QR thanh toán chuyển khoản (chỉ hiện khi chọn Chuyển khoản) |
| 16 | `lblQRAmount` | Label | | Hiển thị số tiền cần chuyển khoản tương ứng với mã QR |
| **— BƯỚC 2B: Đặt trước / Preorder (khi chọn toggle Đặt trước) —** |||||
| 17 | `dpNgayGiao` | DatePicker | Không được là ngày trong quá khứ | Chọn ngày nhận bánh |
| 18 | `lblNangLuc` | Label | | Hiển thị năng lực sản xuất của ngày được chọn (VD: "📦 3/10 bánh") |
| 19 | `cbGioGiao` | ComboBox | Không được để trống | Chọn giờ nhận bánh (07:00 – 21:00, bước 30 phút) |
| 20 | `txtDiaChiGiao` | TextField | Không được để trống | Nhập địa chỉ giao bánh |
| 21 | `btnFullPay` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn "Thanh toán đủ" — tự động điền 100% vào tiền cọc |
| 22 | `btnDeposit` | ToggleButton | Chọn 1 trong 2, không được bỏ chọn cả hai | Chọn "Cọc 50%" — mặc định |
| 23 | `txtTienCoc` | TextField | Phải ≥ 50% tổng tiền; bị khóa khi chọn Thanh toán đủ | Nhập số tiền đặt cọc |
| 24 | `lblCocToiThieu` | Label | | Hiển thị mức cọc tối thiểu yêu cầu |
| 25 | `lblDocChuTienCoc` | Label | | Hiển thị số tiền cọc bằng chữ để kiểm tra tránh nhầm |
| **— FOOTER —** |||||
| 26 | `btnBack` | Button | Ẩn/vô hiệu hóa ở Bước 1 | Quay lại Bước 1 |
| 27 | `btnNext` | Button | Hiển thị ở Bước 1, ẩn ở Bước 2 | Chuyển sang Bước 2 |
| 28 | `btnConfirm` | Button | Hiển thị ở Bước 2, ẩn ở Bước 1; vô hiệu hóa nếu năng lực sản xuất đã đầy | Xác nhận và tạo đơn hàng |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------| 
| 1 | Khởi tạo màn hình | Hiển thị Bước 1. Nạp giờ nhận từ 07:00 đến 21:00 vào `cbGioGiao`. Đặt mặc định: luồng "Thanh toán ngay", hình thức "Tiền mặt", kiểu cọc "Cọc 50%". Ngày giao mặc định là ngày hiện tại + 1. Hiển thị tổng tiền tại `lblTongTien`. `lblKhachInfo` hiển thị "Chưa xác định — sẽ tạo đơn vãng lai". | |
| 2 | Chọn button "🔍 Tìm" hoặc nhấn Enter tại `txtSDT` | - Nếu `txtSDT` trống → Đặt trạng thái vãng lai, cập nhật `lblKhachInfo`.<br>- Nếu tìm thấy khách hàng → Hiển thị tên khách tại `lblKhachInfo` (kiểu `lbl-success`).<br>- Nếu không tìm thấy → `lblKhachInfo` báo "Không tìm thấy — đặt đơn vãng lai" (kiểu `lbl-danger`). | |
| 3 | Chọn button "+ Thêm KH" | Mở dialog `KhachHangDialog` để tạo khách hàng mới. Nếu thành công → Tự động điền SĐT và cập nhật `lblKhachInfo`. | |
| 4 | Chọn button "Sửa KH" | - Nếu chưa chọn khách hàng → Hiển thị cảnh báo.<br>- Ngược lại → Mở dialog `KhachHangDialog` với thông tin khách hiện tại để chỉnh sửa. Nếu lưu thành công → Cập nhật lại `lblKhachInfo`. | |
| 5 | Chọn button `btnNext` (Tiếp theo) | Ẩn Bước 1, hiển thị Bước 2. Bật `btnBack`, ẩn `btnNext`, hiển thị `btnConfirm`. Cập nhật `lblStep` thành "Bước 2 / 2". | |
| 6 | Chọn button `btnBack` (Quay lại) | Ẩn Bước 2, hiển thị lại Bước 1. Ẩn `btnConfirm`, hiển thị `btnNext`. Cập nhật `lblStep` thành "Bước 1 / 2". | |
| 7 | Chọn toggle `btnImmediateFlow` / `btnPreorderFlow` | - Nếu "Thanh toán ngay" → Hiển thị `panelImmediate`, ẩn `panelPreorder`.<br>- Nếu "Đặt trước" → Hiển thị `panelPreorder`, ẩn `panelImmediate`. Cập nhật lại hiển thị thanh toán. | |
| 8 | Chọn toggle `btnCash` / `btnTransfer` | - Nếu "Tiền mặt" → Hiển thị `panelTienMat` (ô khách đưa, tiền thừa), ẩn `panelQR`.<br>- Nếu "Chuyển khoản" → Hiển thị `panelQR` với mã QR sinh tự động, ẩn `panelTienMat`. | |
| 9 | Nhập số tiền tại `txtKhachDua` | Tính và cập nhật `lblTienThua`. Nếu tiền khách đưa ≥ tổng tiền → Hiển thị tiền thừa (kiểu `lbl-tien-thua-success`). Nếu thiếu → Hiển thị "Thiếu [số tiền]" (kiểu `lbl-tien-thua-danger`). Đồng thời hiển thị số tiền bằng chữ tại `lblDocChuKhachDua`. | |
| 10 | Chọn toggle `btnFullPay` / `btnDeposit` | - Nếu "Thanh toán đủ" → Điền tự động 100% tổng tiền vào `txtTienCoc`, khóa không cho chỉnh sửa.<br>- Nếu "Cọc 50%" → Điền 50% vào `txtTienCoc`, cho phép chỉnh sửa. Cập nhật `lblCocToiThieu`. | |
| 11 | Nhập số tiền tại `txtTienCoc` | Hiển thị số tiền cọc bằng chữ tại `lblDocChuTienCoc`. Cập nhật lại tính toán hiển thị thanh toán. | |
| 12 | Chọn ngày tại `dpNgayGiao` | Truy vấn năng lực sản xuất cho ngày sản xuất (= ngày giao − 1). Cập nhật `lblNangLuc`.<br>- Nếu đã đầy công suất → Hiển thị "ĐẦY" (kiểu `lbl-danger`), vô hiệu hóa `btnConfirm`.<br>- Ngược lại → Hiển thị số bánh còn nhận được (kiểu `lbl-success`), kích hoạt `btnConfirm`. | |
| 13 | Chọn button `btnConfirm` (Xác nhận) | **Luồng Thanh toán ngay:**<br>- Nếu hình thức Tiền mặt và số tiền khách đưa < tổng tiền → Hiển thị lỗi validate.<br>- Ngược lại → Tạo `YeuCauDonHang` loại IMMEDIATE, đóng dialog, trả kết quả về Presenter.<br>**Luồng Đặt trước:**<br>- Kiểm tra lại năng lực sản xuất, nếu đầy → Hiển thị lỗi.<br>- Nếu ngày giờ nhận trong quá khứ → Hiển thị lỗi.<br>- Nếu địa chỉ giao trống → Hiển thị lỗi.<br>- Nếu tiền cọc < 50% → Hiển thị lỗi.<br>- Nếu hình thức Tiền mặt và khách đưa < tiền cọc → Hiển thị lỗi.<br>- Ngược lại → Tạo `YeuCauDonHang` loại PREORDER, đóng dialog, trả kết quả về Presenter. | |
