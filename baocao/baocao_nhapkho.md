# Màn hình Quản lý Nhập kho

## Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Tiêu đề phân hệ ("QUẢN LÝ NHẬP KHO") |
| 2 | `btnXoa` | Button | Chỉ hiển thị và cho phép Admin/Quản lý tương tác | Thực hiện hủy bỏ (xóa mềm) phiếu nhập và tự động hoàn trả kho |
| 3 | `btnInPhieu` | Button | Vô hiệu hóa khi chưa chọn phiếu trong danh sách | Kết xuất và in hóa đơn phiếu nhập kho ra định dạng PDF thông qua JasperReports |
| 4 | *(btnNhapTuFile)* | Button | | Hỗ trợ nhập hàng nhanh hàng loạt từ các file dữ liệu (JSON/CSV) |
| 5 | *(btnTaoPhieu)* | Button | | Mở cửa sổ giao diện lập phiếu nhập kho nguyên liệu mới thủ công |
| 6 | `tblData` | TableView | | Bảng hiển thị danh sách lịch sử toàn bộ các phiếu nhập kho |
| 7 | `colDate` | TableColumn | | Hiển thị ngày và giờ lập phiếu nhập kho |
| 8 | `colUser` | TableColumn | | Hiển thị tên nhân viên thực hiện lập phiếu |
| 9 | `colContent` | TableColumn | | Hiển thị tóm tắt tên nhà cung cấp và tổng giá trị tiền hàng của phiếu nhập |
| 10 | `colStatus` | TableColumn | | Hiển thị mã định danh số hiệu phiếu nhập dạng "Phiếu #ID" |
| 11 | `lblThongBao` | Label | | Hiển thị trạng thái kết quả thực hiện giao dịch hoặc cảnh báo lỗi hệ thống |

## Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Thiết lập tiêu đề và liên kết dữ liệu cho bảng `tblData`.<br>- Tải danh sách lịch sử phiếu nhập kho từ CSDL.<br>- Kiểm tra phân quyền: Nếu tài khoản là Admin/Quản lý thì cho phép hiển thị nút `btnXoa` để sẵn sàng hủy phiếu.<br>- Lắng nghe sự kiện chọn hàng trên bảng để kích hoạt (enable) nút `btnInPhieu` và `btnXoa`. | |
| 2 | Double-click dòng trên bảng | - Gọi phương thức `onXemChiTietPhieuNhap()` mở một cửa sổ con (Popup Stage) hiển thị chi tiết danh sách nguyên liệu, số lượng, đơn giá và số lô cụ thể được nhập trong phiếu đó. | |
| 3 | Chọn button `+ Tạo phiếu nhập` | - Mở Modal Dialog giao diện lập phiếu nhập.<br>- Người dùng chọn nhà cung cấp, thêm các nguyên liệu, nhập số lượng, đơn giá, ngày sản xuất và hạn sử dụng của từng lô hàng.<br>- Bấm Lưu để gọi Procedure CSDL lưu phiếu nhập và tăng tồn kho tương ứng, sau đó làm mới lại bảng. | Sử dụng Stored Procedure Oracle để đảm bảo tính nhất quán dữ liệu |
| 4 | Chọn button `Nhập từ File` | - Yêu cầu người dùng lựa chọn Nhà cung cấp mục tiêu trước.<br>- Mở hộp thoại chọn file (FileChooser) để tải file JSON/CSV chứa danh sách nguyên liệu nhập.<br>- Chuyển dữ liệu qua `NhapKhoService` để phân tích cú pháp, kiểm tra tính hợp lệ và tự động lập phiếu hàng loạt giúp tiết kiệm thời gian. | |
| 5 | Chọn button `In phiếu` | - Sử dụng thư viện JasperReports và đường dẫn cấu hình báo cáo để biên dịch dữ liệu phiếu nhập đang chọn sang file PDF phục vụ việc lưu trữ, in ấn. | |
| 6 | Chọn button `Xóa phiếu` | - Hiển thị hộp thoại cảnh báo (Confirmation Alert) xác nhận việc hủy bỏ phiếu nhập.<br>- Nếu người dùng đồng ý → Gọi Procedure `huyPhieuNhap()` trong CSDL để hoàn trả lại số lượng tồn kho (trừ số lượng lô đã nhập trong phiếu), đồng thời reload bảng lịch sử. | Cơ chế trigger Oracle tự động xử lý tính toán giảm số lượng tồn kho tương ứng |
