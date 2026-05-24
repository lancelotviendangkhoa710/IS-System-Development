# Màn hình Sổ quỹ thu chi

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblThongBao` | Label | | Hiển thị các thông báo kết quả thao tác hoặc thông báo lỗi của màn hình sổ quỹ |
| 2 | `btnTabTongQuan` | Button | | Thực hiện chuyển đổi xem tab Tổng quan tình hình sổ quỹ thu chi |
| 3 | `btnTabLichSu` | Button | | Thực hiện chuyển đổi xem tab Lịch sử giao dịch chi tiết của sổ quỹ |
| 4 | `btnTabCauHinh` | Button | Chỉ hiển thị với vai trò quản lý hoặc quản trị viên | Thực hiện chuyển đổi xem tab Cấu hình các loại danh mục thu chi |
| 5 | `btnThemGiaoDich` | Button | Chỉ hiển thị trên giao diện tab Lịch sử giao dịch | Mở hộp thoại lập phiếu thu chi mới để nạp tiền hoặc xuất quỹ |
| 6 | `btnThemDanhMucMoi` | Button | Chỉ hiển thị trên giao diện tab Cấu hình danh mục | Mở hộp thoại thêm loại danh mục thu chi mới |
| 7 | `lblTongThu` (tab Tổng quan) | Label | | Hiển thị tổng số tiền thu của toàn bộ hệ thống |
| 8 | `lblTongChi` (tab Tổng quan) | Label | | Hiển thị tổng số tiền chi của toàn bộ hệ thống |
| 9 | `lblSoDu` (tab Tổng quan) | Label | | Hiển thị số tiền tồn quỹ thực tế hiện tại của cửa hàng |
| 10 | `lblTyLeThu` (tab Tổng quan) | Label | | Hiển thị tỷ lệ phần trăm của tổng thu so với tổng số giao dịch phát sinh |
| 11 | `barThu` (tab Tổng quan) | ProgressBar | | Thanh tiến trình hiển thị trực quan tỷ trọng tổng số tiền thu |
| 12 | `lblTyLeChi` (tab Tổng quan) | Label | | Hiển thị tỷ lệ phần trăm của tổng chi so với tổng số giao dịch phát sinh |
| 13 | `barChi` (tab Tổng quan) | ProgressBar | | Thanh tiến trình hiển thị trực quan tỷ trọng tổng số tiền chi |
| 14 | `tfTimKiem` (tab Lịch sử) | TextField | | Nhập từ khóa mã phiếu hoặc mô tả để tìm kiếm nhanh giao dịch |
| 15 | `cbBoLoc` (tab Lịch sử) | ComboBox | | Bộ lọc nhanh danh sách giao dịch theo loại phiếu thu hoặc phiếu chi |
| 16 | `tableGiaoDich` (tab Lịch sử) | TableView | | Bảng hiển thị danh sách các phiếu giao dịch thu chi trong hệ thống |
| 17 | `lblFooterGiaoDich` (tab Lịch sử) | Label | | Hiển thị số lượng giao dịch và thông tin trang hiện tại |
| 18 | `btnTruoc` (tab Lịch sử) | Button | | Nút bấm chuyển về trang dữ liệu lịch sử phía trước |
| 19 | `btnSau` (tab Lịch sử) | Button | | Nút bấm chuyển sang trang dữ liệu lịch sử phía sau |
| 20 | `tableLoai` (tab Cấu hình) | TableView | | Bảng hiển thị danh sách các danh mục phân loại thu chi được cấu hình |
| 21 | `lblFooterLoai` (tab Cấu hình) | Label | | Hiển thị số lượng danh mục và thông tin phân trang cấu hình |
| 22 | `btnTruocLoai` (tab Cấu hình) | Button | | Nút bấm chuyển về trang cấu hình danh mục phía trước |
| 23 | `btnSauLoai` (tab Cấu hình) | Button | | Nút bấm chuyển sang trang cấu hình danh mục phía sau |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Tải các dữ liệu tổng hợp về tổng thu, tổng chi và số dư tồn quỹ để cập nhật lên các nhãn KPI tương ứng của tab Tổng quan.<br>- Tính toán tỷ trọng và nạp dữ liệu lên các ProgressBar tỷ trọng thu chi.<br>- Kiểm tra vai trò đăng nhập của người dùng để quyết định hiển thị nút tab cấu hình các danh mục phân loại thu chi.<br>- Thiết lập mặc định hiển thị giao diện tab Tổng quan và ẩn các nút lập phiếu mới, thêm danh mục mới. | |
| 2 | Chọn button `Tổng quan` | - Chuyển sang hiển thị giao diện tab Tổng quan với các thẻ KPI và biểu đồ ProgressBar tỷ trọng.<br>- Ẩn các nút context lập phiếu mới và thêm danh mục mới. | |
| 3 | Chọn button `Lịch sử giao dịch` | - Chuyển sang hiển thị giao diện danh sách lịch sử giao dịch.<br>- Thực hiện tải dữ liệu danh sách giao dịch từ cơ sở dữ liệu lên bảng `tableGiaoDich` kèm phân trang hiển thị.<br>- Hiển thị nút context lập phiếu mới để cho phép lập phiếu mới. | |
| 4 | Chọn button `Cấu hình Danh mục` | - Chuyển sang hiển thị giao diện quản lý danh mục phân loại thu chi.<br>- Thực hiện tải danh sách danh mục phân loại từ cơ sở dữ liệu lên bảng `tableLoai` kèm phân trang hiển thị.<br>- Hiển thị nút context thêm danh mục mới để cho phép thêm loại mới. | |
| 5 | Nhập từ khóa tại ô `tfTimKiem` hoặc Thay đổi tiêu chí lọc `cbBoLoc` | - Thực hiện gọi cơ sở dữ liệu lọc danh sách các giao dịch thu chi tương ứng và cập nhật dữ liệu hiển thị lên bảng `tableGiaoDich` từ trang đầu tiên. | |
| 6 | Chọn button lập phiếu mới | - Mở hộp thoại lập phiếu giao dịch thu chi mới để người dùng nhập thông tin phiếu.<br>- Nếu người dùng xác nhận và lưu thành công: Cập nhật cơ sở dữ liệu tạo phiếu thu chi mới, hiển thị thông báo thành công và tải lại bảng giao dịch lịch sử. | |
| 7 | Chọn button thêm danh mục mới | - Mở hộp thoại thêm danh mục thu chi mới.<br>- Nếu người dùng nhập hợp lệ và xác nhận: Cập nhật cơ sở dữ liệu tạo thêm loại danh mục mới, hiển thị thông báo thành công và tải lại bảng danh mục cấu hình. | |
| 8 | Chọn các button chuyển trang | - Thực hiện thay đổi trang dữ liệu hiển thị hiện tại và gọi cơ sở dữ liệu lấy danh sách tương ứng của trang mới để cập nhật lên bảng dữ liệu. | |
| 9 | Chọn button `Hủy` trên một dòng giao dịch trong bảng `tableGiaoDich` | - Hiển thị hộp thoại yêu cầu xác nhận hủy phiếu giao dịch và yêu cầu người dùng cung cấp lý do hủy.<br>- Nếu đồng ý và cung cấp lý do: Gọi cơ sở dữ liệu cập nhật trạng thái phiếu thành 'cancelled' (Đã hủy), tải lại danh sách giao dịch lịch sử và cập nhật lại số dư trên tab Tổng quan. | |
| 10 | Chọn button `Sửa` trên một dòng danh mục trong bảng `tableLoai` | - Mở hộp thoại chỉnh sửa danh mục thu chi.<br>- Nếu người dùng lưu thay đổi thành công: Cập nhật cơ sở dữ liệu, tải lại bảng danh mục và hiển thị thông báo thành công. | |
| 11 | Chọn button `Khóa` / `Mở khóa` trên một dòng danh mục trong bảng `tableLoai` | - Gọi cơ sở dữ liệu cập nhật trạng thái hoạt động (khóa hoặc mở khóa) của danh mục thu chi tương ứng.<br>- Tải lại bảng danh mục và hiển thị thông báo thành công. | |
