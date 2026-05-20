# Màn hình chính (Dashboard)

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `btnToggleSidebar` | Button | | Thu gọn hoặc mở rộng thanh menu bên trái |
| 2 | `lblLogoText` | Label | | Tên ứng dụng "H3K BAKERY" |
| 3 | `btnTongQuan` | Button | | Mở màn hình Tổng quan (Dashboard) |
| 4 | `btnNhanSu` | Button | | Mở phân hệ Nhân sự |
| 5 | `btnBanHang` | Button | | Mở phân hệ Bán hàng |
| 6 | `btnKhachHang` | Button | | Mở phân hệ Khách hàng |
| 7 | `btnSanPham` | Button | | Mở phân hệ Sản phẩm |
| 8 | `btnKho` | Button | | Mở phân hệ Kho |
| 9 | `btnBep` | Button | | Mở phân hệ Bếp |
| 10 | `btnBaoCao` | Button | | Mở phân hệ Báo cáo |
| 11 | `btnAuditLogs` | Button | | Mở Lịch sử hệ thống |
| 12 | `btnKhoiPhucDuLieu` | Button | | Mở Khôi phục dữ liệu |
| 13 | `lblTenNguoiDung` | Label | | Hiển thị tên người dùng đang đăng nhập |
| 14 | `lblVaiTro` | Label | | Hiển thị vai trò hiện tại |
| 15 | `btnDoiVaiTro` | Button | | Đổi vai trò làm việc (chỉ hiện khi có nhiều vai trò) |
| 16 | `btnAvatar` | Button | | Mở thông tin cá nhân |
| 17 | *(btnDangXuat)* | Button | | Đăng xuất khỏi hệ thống |
| 18 | `lblBannerName` | Label | | Hiển thị tên người dùng trên banner chào mừng |
| 19 | `lblKpiDon` | Label | | Số đơn hàng hôm nay |
| 20 | `lblKpiDoanhThu` | Label | | Doanh thu hôm nay (triệu đồng) |
| 21 | `vboxTop5` | VBox | | Danh sách Top 5 sản phẩm bán chạy nhất |
| 22 | `lblCapNhatTop5` | Label | | Thời gian cập nhật dữ liệu Top 5 |
| 23 | `chartDoanhThu` | LineChart | | Biểu đồ doanh thu 7 ngày gần nhất |
| 24 | `lblCapNhatChart` | Label | | Thời gian cập nhật biểu đồ |
| 25 | `tblThongKe` | TableView | | Bảng chi tiết thống kê theo ngày (7 ngày) |
| 26 | `colNgay` | TableColumn | | Cột ngày |
| 27 | `colDoanhThu` | TableColumn | | Cột doanh thu |
| 28 | `colDonHoanThanh` | TableColumn | | Cột số đơn hoàn thành |
| 29 | `colDonHuy` | TableColumn | | Cột số đơn hủy |
| 30 | `colTongDon` | TableColumn | | Cột tổng số đơn |
| 31 | `lblCapNhatBang` | Label | | Thời gian cập nhật bảng thống kê |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | Hiển thị tên người dùng đang đăng nhập lên banner và header. Tải dữ liệu thống kê từ cơ sở dữ liệu gồm: biểu đồ doanh thu, Top 5 sản phẩm bán chạy, KPI hôm nay và bảng chi tiết 7 ngày. Chạy hiệu ứng animation cho biểu đồ. | Dữ liệu được tải ngầm trên luồng riêng, không làm đơ giao diện |
| 2 | Chọn button `btnToggleSidebar` | Thu gọn hoặc mở rộng thanh menu điều hướng bên trái. | |
| 3 | Chọn các button menu điều hướng (`btnTongQuan`, `btnNhanSu`, `btnBanHang`...) | Tải và hiển thị màn hình phân hệ tương ứng vào vùng nội dung chính. | |
| 4 | Chọn button `btnDoiVaiTro` | Hiển thị hộp thoại cho phép chọn vai trò làm việc khác. | Chỉ hiển thị khi tài khoản có nhiều vai trò |
| 5 | Chọn button `btnAvatar` | Mở màn hình thông tin cá nhân của người dùng. | |
| 6 | Chọn button Đăng xuất | Kết thúc phiên làm việc và quay lại màn hình đăng nhập. | |
