# Báo cáo mô tả màn hình: Chi tiết đơn hàng (Dialog)

> **Màn hình:** Chi tiết đơn hàng (Modal Dialog)
> **File FXML:** *(không có — tạo bằng code Java)*
> **Controller:** [TheoDoiDonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/TheoDoiDonHangViewFXMLController.java) — phương thức `showOrderDetails()`
> **Kích thước:** 680 × 520 px · Modal, chặn tương tác màn hình nền
> **Mở từ:** Màn hình Theo dõi đơn hàng khi nhấn nút `Chi tiết` trên card đơn hàng

---

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------| 
| 1 | `lblHeader` | Label | | Hiển thị thông tin tóm tắt đơn hàng: mã đơn, trạng thái, khách hàng, ngày giờ nhận bánh, tổng tiền, tiền đã cọc |
| 2 | `lblBanSan` | Label | | Tiêu đề nhóm "🛒 Bánh bán sẵn" |
| 3 | `tblBanSan` | TableView\<CTDonHangDTO\> | | Danh sách sản phẩm bán sẵn trong đơn hàng |
| 4 | `colTenSP` | TableColumn | | Tên sản phẩm bán sẵn |
| 5 | `colSL` | TableColumn | | Số lượng sản phẩm |
| 6 | `colGia` | TableColumn | | Đơn giá (định dạng tiền VNĐ) |
| 7 | `colTT` | TableColumn | | Thành tiền = đơn giá × số lượng (định dạng tiền VNĐ) |
| 8 | `lblTuyChinh` | Label | | Tiêu đề nhóm "✨ Bánh tùy chỉnh" |
| 9 | `tblTuyChinh` | TableView\<CTDonTuyChinhDTO\> | | Danh sách bánh tùy chỉnh trong đơn hàng |
| 10 | `colTcTen` | TableColumn | | Tên bánh tùy chỉnh |
| 11 | `colTcSL` | TableColumn | | Số lượng bánh tùy chỉnh |
| 12 | `colTcGia` | TableColumn | | Đơn giá bánh tùy chỉnh (định dạng tiền VNĐ) |
| 13 | `colTcLoiChuc` | TableColumn | | Lời chúc in trên bánh (hiển thị "—" nếu không có) |
| 14 | `colTcGhiChu` | TableColumn | | Ghi chú thợ bánh (hiển thị "—" nếu không có) |
| 15 | `btnDong` | Button | | Nút "❌ Đóng" — đóng dialog |

---

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------| 
| 1 | Khởi tạo màn hình | Gọi Presenter lấy danh sách sản phẩm bán sẵn (`CTDonHangDTO`) và bánh tùy chỉnh (`CTDonTuyChinhDTO`) theo mã đơn. Nạp dữ liệu vào `tblBanSan` và `tblTuyChinh`.<br>- Nếu danh sách bánh bán sẵn rỗng → Hiển thị placeholder "Không có sản phẩm bán sẵn".<br>- Nếu danh sách bánh tùy chỉnh rỗng → Hiển thị placeholder "Không có bánh tùy chỉnh". | Mở dưới dạng modal, chặn tương tác màn hình nền |
| 2 | Chọn button `btnDong` | Đóng dialog, trả quyền điều khiển về màn hình Theo dõi đơn hàng. | |
