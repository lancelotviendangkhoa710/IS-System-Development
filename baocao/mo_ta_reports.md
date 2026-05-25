# Báo cáo Kinh doanh

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `P_TIEU_DE` | Label | | Tên báo cáo, hiển thị giữa header nền cam |
| 2 | Dòng kỳ / ngày xuất / người xuất | Label | | Hiển thị kỳ báo cáo, ngày xuất, người xuất trên một dòng |
| 3 | `P_CHART_IMAGE` | ImageView | | Ảnh snapshot biểu đồ xu hướng doanh thu; chỉ hiển thị khi có dữ liệu |
| 4 | Hộp KPI Doanh thu (`P_DOANH_THU`) | VBox | | Thẻ tóm tắt tổng doanh thu trong kỳ, viền cam |
| 5 | Hộp KPI Giá vốn (`P_GIA_VON`) | VBox | | Thẻ tóm tắt tổng giá vốn hàng bán, viền xanh dương |
| 6 | Hộp KPI Lợi nhuận (`P_LOI_NHUAN`) | VBox | | Thẻ tóm tắt lợi nhuận trong kỳ, viền xanh lá |
| 7 | Hộp KPI Giao dịch (`P_TONG_GIAO_DICH`) | VBox | | Thẻ tóm tắt tổng số đơn hàng, viền vàng |
| 8 | Bảng giao dịch | TableView | | Danh sách chi tiết các giao dịch trong kỳ |
| 9 | Cột STT | TableColumn\<String\> | | Số thứ tự dòng |
| 10 | Cột Mã đơn (`MA_DON`) | TableColumn\<String\> | | Mã định danh đơn hàng |
| 11 | Cột Khách hàng (`TEN_KHACH`) | TableColumn\<String\> | | Tên khách hàng |
| 12 | Cột Món / Sản phẩm (`MON_HANG`) | TableColumn\<String\> | | Tên mặt hàng trong đơn |
| 13 | Cột Số tiền (`SO_TIEN`) | TableColumn\<String\> | | Giá trị đơn hàng, căn phải |
| 14 | Cột Trạng thái (`TRANG_THAI`) | TableColumn\<String\> | | Trạng thái thanh toán của đơn, căn giữa |
| 15 | Footer trang | HBox | | Label tên hệ thống bên trái, Label số trang bên phải |

---

# Hóa Đơn Bán Hàng

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `P_TIEU_DE` | Label | | Tên hóa đơn, hiển thị giữa trang, mặc định là "HÓA ĐƠN BÁN HÀNG" |
| 2 | `P_MA_HOA_DON` | Label | | Số hóa đơn, màu cam nổi bật |
| 3 | `P_MA_DON` | Label | | Mã đơn hàng tương ứng |
| 4 | `P_NGAY_LAP` | Label | | Ngày lập hóa đơn |
| 5 | `P_TEN_KHACH` | Label | | Tên khách hàng |
| 6 | Bảng sản phẩm | TableView | | Danh sách các mặt hàng trong hóa đơn |
| 7 | Cột STT | TableColumn\<String\> | | Số thứ tự dòng |
| 8 | Cột Tên sản phẩm (`TEN_SP`) | TableColumn\<String\> | | Tên sản phẩm |
| 9 | Cột Số lượng (`SO_LUONG`) | TableColumn\<String\> | | Số lượng mua, căn phải |
| 10 | Cột Đơn giá (`DON_GIA`) | TableColumn\<String\> | | Đơn giá sản phẩm, căn phải |
| 11 | Cột Thành tiền (`THANH_TIEN`) | TableColumn\<String\> | | Thành tiền từng dòng, căn phải |
| 12 | `P_TONG_HANG` | Label | | Tổng tiền hàng trước thuế và giảm giá |
| 13 | `P_THUE_VAT` | Label | | Thuế VAT (8.5%) |
| 14 | `P_GIAM_GIA` | Label | | Số tiền giảm giá, màu cam |
| 15 | `P_TONG_THANH_TOAN` | Label | | Tổng tiền khách phải thanh toán, nền cam nổi bật, chữ trắng |
| 16 | `P_TIEN_KHACH_DUA` | Label | | Số tiền khách đưa |
| 17 | `P_TIEN_THUA` | Label | | Tiền thừa trả lại cho khách |
| 18 | `P_DOC_CHU` | Label | | Số tiền tổng thanh toán đọc bằng chữ, in nghiêng |
| 19 | Footer trang | HBox | | Label lời cảm ơn và chính sách bảo hành chất lượng trong ngày |

---

# Lịch Sử Mua Hàng

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `P_TIEU_DE` | Label | | Tên báo cáo, hiển thị trên header nền cam |
| 2 | Khoảng thời gian (`P_TU_NGAY` / `P_DEN_NGAY`) | Label | | Hiển thị từ ngày đến ngày trên một dòng |
| 3 | Hộp KPI Tổng đơn (`P_TONG_DON`) | VBox | | Thẻ tóm tắt tổng số đơn hàng trong khoảng thời gian |
| 4 | Hộp KPI Tổng tiền (`P_TONG_TIEN`) | VBox | | Thẻ tóm tắt tổng giá trị đơn hàng, màu cam |
| 5 | Bảng lịch sử | TableView | | Danh sách chi tiết các đơn hàng trong khoảng thời gian |
| 6 | Cột STT | TableColumn\<String\> | | Số thứ tự dòng |
| 7 | Cột Mã đơn (`MA_DON`) | TableColumn\<String\> | | Mã định danh đơn hàng |
| 8 | Cột Ngày mua (`NGAY_MUA`) | TableColumn\<String\> | | Ngày thực hiện giao dịch |
| 9 | Cột Khách hàng (`TEN_KHACH`) | TableColumn\<String\> | | Tên khách hàng |
| 10 | Cột Món / Sản phẩm (`MON_HANG`) | TableColumn\<String\> | | Tên mặt hàng đã mua |
| 11 | Cột Số lượng (`SO_LUONG`) | TableColumn\<String\> | | Số lượng, căn phải |
| 12 | Cột Thành tiền (`SO_TIEN`) | TableColumn\<String\> | | Giá trị đơn hàng, căn phải |
| 13 | Cột Trạng thái (`TRANG_THAI`) | TableColumn\<String\> | | Trạng thái thanh toán, căn giữa |
| 14 | Footer trang | HBox | | Label tên hệ thống, ngày xuất, người xuất bên trái; Label số trang bên phải |

---

# Báo Cáo Kiểm Kê Nhập Kho

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | Tiêu đề báo cáo | Label | | Hiển thị "H3K BAKERY — BÁO CÁO PHIẾU NHẬP KHO", nền xanh đậm |
| 2 | `P_NGUOI_LAP` | Label | | Tên người lập báo cáo |
| 3 | `P_NGAY_LAP` | Label | | Ngày lập báo cáo |
| 4 | `P_NGAY_IN` | Label | | Ngày in báo cáo |
| 5 | `P_SO_PHIEU` | Label | | Tổng số phiếu nhập kho trong kỳ |
| 6 | `P_TONG_TIEN` | Label | | Tổng tiền nhập kho trong kỳ |
| 7 | Header nhóm Nhà cung cấp (`NHA_CUNG_CAP`) | HBox | | Dải nền xanh dương hiển thị tên nhà cung cấp, phân tách nhóm |
| 8 | Header nhóm Phiếu (`MA_PHIEU`) | HBox | | Dải nền xanh nhạt hiển thị mã phiếu, ngày nhập, người nhập và tổng tiền phiếu |
| 9 | Bảng nguyên liệu | TableView | | Danh sách chi tiết nguyên liệu trong từng phiếu nhập |
| 10 | Cột Nguyên liệu (`TEN_NL`) | TableColumn\<String\> | | Tên nguyên liệu nhập kho |
| 11 | Cột Số lượng (`SO_LUONG`) | TableColumn\<String\> | | Số lượng nhập, căn phải |
| 12 | Cột ĐVT (`TEN_DVT`) | TableColumn\<String\> | | Đơn vị tính của nguyên liệu |
| 13 | Cột Đơn giá (`DON_GIA`) | TableColumn\<String\> | | Đơn giá nhập, căn phải |
| 14 | Cột Thành tiền (`THANH_TIEN`) | TableColumn\<String\> | | Thành tiền từng dòng, màu xanh đậm, căn phải |
| 15 | Footer nhóm Nhà cung cấp (`SUBTOTAL_NCC`) | HBox | | Label "Cộng nhà cung cấp" và Label tổng tiền nhóm, nền xanh nhạt |
| 16 | Tổng cộng toàn kỳ (`GRAND_TOTAL`) | HBox | | Label tổng và Label giá trị toàn kỳ, nền xanh đậm chữ vàng |
| 17 | Footer trang | HBox | | Label tên hệ thống bên trái, Label số trang bên phải |

---

# Phiếu Nhập Kho

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | Tiêu đề phiếu | Label | | Hiển thị "H3K BAKERY — PHIẾU NHẬP KHO", nền xanh dương |
| 2 | `P_MA_PHIEU` | Label | | Số phiếu nhập kho, màu cam nổi bật |
| 3 | `P_NGAY_NHAP` | Label | | Ngày thực hiện nhập kho |
| 4 | `P_NGUOI_NHAP` | Label | | Tên người thực hiện nhập kho |
| 5 | `P_NHA_CUNG_CAP` | Label | | Tên nhà cung cấp lô hàng |
| 6 | `P_TONG_DONG` | Label | | Tổng số dòng nguyên liệu trong phiếu |
| 7 | `P_NGAY_IN` | Label | | Ngày in phiếu |
| 8 | `P_TONG_TIEN` | Label | | Tổng giá trị lô hàng nhập, nổi bật trong hộp nền xanh nhạt |
| 9 | Bảng nguyên liệu | TableView | | Danh sách chi tiết nguyên liệu trong phiếu nhập |
| 10 | Cột STT | TableColumn\<String\> | | Số thứ tự dòng |
| 11 | Cột Tên nguyên liệu (`TEN_NL`) | TableColumn\<String\> | | Tên nguyên liệu nhập kho |
| 12 | Cột Số lượng (`SO_LUONG`) | TableColumn\<String\> | | Số lượng nhập, căn phải |
| 13 | Cột Đơn giá (`DON_GIA`) | TableColumn\<String\> | | Đơn giá nhập, căn phải |
| 14 | Cột Thành tiền (`THANH_TIEN`) | TableColumn\<String\> | | Thành tiền từng dòng, căn phải |
| 15 | Cột Hạn sử dụng (`HAN_SD`) | TableColumn\<String\> | | Hạn sử dụng của nguyên liệu, căn giữa |
| 16 | Footer trang | HBox | | Label tên hệ thống bên trái, Label số trang bên phải |
| 17 | Vùng chữ ký | HBox | | Ba Label chứa chức danh: Người lập phiếu, Thủ kho, Giám đốc / Quản lý |

---

# Phiếu Xuất Kho

## Bảng 1 — Mô tả các đối tượng trên báo cáo

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | Tiêu đề phiếu | Label | | Hiển thị "H3K BAKERY — PHIẾU XUẤT KHO", nền cam đỏ |
| 2 | `P_MA_PHIEU` | Label | | Số phiếu xuất kho, màu cam nổi bật |
| 3 | `P_NGAY_XUAT` | Label | | Ngày thực hiện xuất kho |
| 4 | `P_NGUOI_XUAT` | Label | | Tên người thực hiện xuất kho |
| 5 | `P_LY_DO` | Label | | Lý do xuất kho |
| 6 | `P_TONG_DONG` | Label | | Tổng số mặt hàng trong phiếu |
| 7 | `P_NGAY_IN` | Label | | Ngày in phiếu |
| 8 | `P_GHI_CHU` | Label | | Ghi chú bổ sung, hiển thị trong hộp nền cam nhạt |
| 9 | Bảng hàng hóa | TableView | | Danh sách chi tiết hàng hóa xuất kho |
| 10 | Cột STT | TableColumn\<String\> | | Số thứ tự dòng |
| 11 | Cột Tên hàng hóa (`TEN_HANG`) | TableColumn\<String\> | | Tên hàng hóa xuất kho |
| 12 | Cột Loại hàng (`LOAI_HANG`) | TableColumn\<String\> | | Phân loại hàng hóa, căn giữa |
| 13 | Cột Số lượng (`SO_LUONG`) | TableColumn\<String\> | | Số lượng xuất, căn phải |
| 14 | Cột ĐVT (`DON_VI`) | TableColumn\<String\> | | Đơn vị tính, căn giữa |
| 15 | Cột Ghi chú (`GHI_CHU`) | TableColumn\<String\> | | Ghi chú riêng cho từng dòng hàng |
| 16 | Footer trang | HBox | | Label tên hệ thống bên trái, Label số trang bên phải |
| 17 | Vùng chữ ký | HBox | | Ba Label chứa chức danh: Người lập phiếu, Thủ kho, Giám đốc / Quản lý |
