# Màn hình Quản lý Xuất Kho

## Bảng 1 — Mô tả các đối tượng trên màn hình

| STT | Tên | Kiểu | Ràng buộc | Chức năng |
|-----|-----|------|-----------|-----------|
| 1 | `lblTitle` | Label | | Hiển thị tiêu đề Quản lý xuất kho |
| 2 | `btnXoa` | Button | Chỉ hiển thị với vai trò quản trị viên hoặc quản lý | Thực hiện xóa phiếu xuất kho được chọn |
| 3 | `btnInPhieu` | Button | Bị vô hiệu hóa khi không chọn dòng nào trong bảng | Kết xuất thông tin phiếu xuất kho ra tệp tin tài liệu dưới dạng PDF |
| 4 | Nút tạo phiếu xuất | Button | | Mở hộp thoại chọn lý do xuất kho mới |
| 5 | `tblData` | TableView | | Bảng hiển thị danh sách các phiếu xuất kho đã được lập |
| 6 | `colDate` | TableColumn | | Hiển thị ngày và giờ thực hiện lập phiếu xuất kho |
| 7 | `colUser` | TableColumn | | Hiển thị tên nhân viên thực hiện lập phiếu xuất kho |
| 8 | `colContent` | TableColumn | | Hiển thị lý do thực hiện xuất kho |
| 9 | `colStatus` | TableColumn | | Hiển thị mã số định danh của phiếu xuất kho dưới dạng nhãn "Phiếu #[mã]" |
| 10 | `lblThongBao` | Label | | Hiển thị thông tin hoặc thông báo lỗi của phân hệ xuất kho |
| 11 | Hộp thoại chọn lý do xuất kho | Dialog | | Hộp thoại cho phép lựa chọn một trong bốn lý do để lập phiếu xuất kho |
| 12 | `rdoLamBanh` | RadioButton | | Chọn lý do xuất nguyên liệu để phục vụ sản xuất làm bánh |
| 13 | `rdoNLHong` | RadioButton | | Chọn lý do xuất hủy vì nguyên liệu bị hỏng không sử dụng được |
| 14 | `rdoSPHong` | RadioButton | | Chọn lý do xuất hủy vì sản phẩm bánh bảo quản bị hỏng |
| 15 | `rdoSaiSot` | RadioButton | | Chọn lý do xuất hủy vì sai sót lỗi kỹ thuật trong quá trình làm bánh |
| 16 | Hộp thoại xuất nguyên liệu làm bánh | Dialog | | Hộp thoại nhập thông tin chi tiết xuất nguyên liệu làm bánh theo công thức |
| 17 | `cbSP` (hộp thoại làm bánh) | ComboBox | Phải chọn sản phẩm cụ thể | Chọn sản phẩm bánh cần làm để hệ thống truy xuất công thức nguyên liệu |
| 18 | `lblToiDa` | Label | | Hiển thị số lượng bánh tối đa có thể làm dựa trên lượng nguyên liệu hiện có trong kho |
| 19 | `lblGioiHan` | Label | | Hiển thị giới hạn số lượng bánh tối đa được làm và số đã làm trong ngày |
| 20 | `txtSL` (hộp thoại làm bánh) | TextField | Phải nhập số nguyên dương lớn hơn 0 và không vượt quá số lượng tối đa cho phép | Nhập số lượng sản phẩm bánh cần sản xuất |
| 21 | Hộp thoại xuất hủy nguyên liệu hỏng | Dialog | | Hộp thoại nhập thông tin chi tiết xuất hủy nguyên liệu bị hỏng không đạt chất lượng |
| 22 | `cbNL` (hộp thoại nguyên liệu hỏng) | ComboBox | Phải chọn nguyên liệu cụ thể | Chọn nguyên liệu bị hỏng cần thực hiện xuất hủy |
| 23 | `txtSL` (hộp thoại nguyên liệu hỏng) | TextField | Phải nhập số lớn hơn 0 và không vượt quá số lượng tồn kho thực tế | Nhập số lượng nguyên liệu bị hỏng cần xuất hủy |
| 24 | Hộp thoại xuất hủy bánh bảo quản hỏng | Dialog | | Hộp thoại nhập thông tin chi tiết xuất hủy bánh thành phẩm bị hỏng trong kho |
| 25 | `cbSP` (hộp thoại bánh bảo quản hỏng) | ComboBox | Phải chọn sản phẩm cụ thể | Chọn sản phẩm bánh bảo quản bị hỏng cần xuất hủy |
| 26 | `txtSL` (hộp thoại bánh bảo quản hỏng) | TextField | Phải nhập số nguyên dương lớn hơn 0 và không vượt quá số lượng tồn kho của bánh | Nhập số lượng bánh bảo quản bị hỏng cần xuất hủy |
| 27 | Hộp thoại xuất hủy bánh do sai sót sản xuất | Dialog | | Hộp thoại nhập thông tin chi tiết xuất hủy bánh thành phẩm bị lỗi trong sản xuất |
| 28 | `cbSP` (hộp thoại sai sót sản xuất) | ComboBox | Phải chọn sản phẩm cụ thể | Chọn sản phẩm bánh bị lỗi trong sản xuất cần xuất hủy |
| 29 | `txtSL` (hộp thoại sai sót sản xuất) | TextField | Phải nhập số nguyên dương lớn hơn 0 và không vượt quá số lượng tồn kho của bánh | Nhập số lượng bánh bị lỗi trong sản xuất cần xuất hủy |
| 30 | Hộp thoại chi tiết phiếu xuất | Dialog | | Hộp thoại hiển thị chi tiết danh sách nguyên liệu và thành phẩm thực tế đã xuất |
| 31 | `tbl` | TableView | | Bảng hiển thị danh sách các dòng chi tiết hàng hóa thực tế đã xuất của phiếu |
| 32 | `colLoai` | TableColumn | | Hiển thị phân loại mặt hàng xuất kho là nguyên liệu hay thành phẩm |
| 33 | `colTen` | TableColumn | | Hiển thị tên của nguyên liệu hoặc thành phẩm thực tế đã xuất |
| 34 | `colDVT` | TableColumn | | Hiển thị đơn vị tính của mặt hàng xuất kho |
| 35 | `colSL` (chi tiết phiếu) | TableColumn | | Hiển thị số lượng hàng hóa thực tế đã xuất kho |
| 36 | `colGia` | TableColumn | | Hiển thị đơn giá vốn của mặt hàng tại thời điểm lập phiếu xuất kho |
| 37 | `btnDong` | Button | | Thực hiện đóng hộp thoại hiển thị chi tiết phiếu xuất kho |

## Bảng 2 — Danh sách biến cố và xử lý tương ứng trên màn hình

| STT | Biến cố | Xử lý | Ghi chú |
|-----|---------|-------|---------|
| 1 | Khởi tạo màn hình | - Thiết lập cấu trúc bảng phiếu xuất kho.<br>- Tải danh sách phiếu xuất từ CSDL và hiển thị lên bảng `tblData`.<br>- Ẩn nút `btnXoa` nếu vai trò người dùng không phải quản trị viên hoặc quản lý. | |
| 2 | Chọn button tạo phiếu xuất | - Nếu vai trò là Thủ kho: Bỏ qua hộp thoại chọn lý do, mở thẳng hộp thoại xuất hủy nguyên liệu hỏng.<br>- Với vai trò khác: Hệ thống hiển thị hộp thoại chọn lý do xuất kho mới.<br>- Nếu chọn xuất làm bánh: Mở hộp thoại xuất nguyên liệu làm bánh.<br>- Nếu chọn xuất nguyên liệu hỏng: Mở hộp thoại xuất hủy nguyên liệu hỏng.<br>- Nếu chọn xuất bánh bảo quản hỏng: Mở hộp thoại xuất hủy bánh bảo quản hỏng.<br>- Nếu chọn xuất bánh bị lỗi: Mở hộp thoại xuất hủy bánh do lỗi sản xuất.<br>- Nếu chọn hủy bỏ: Đóng hộp thoại chọn lý do. | |
| 3 | Chọn button `OK` trên hộp thoại xuất nguyên liệu làm bánh | - Kiểm tra tính hợp lệ của số lượng sản phẩm bánh cần làm.<br>- Nếu số lượng nhập không hợp lệ hoặc vượt quá giới hạn ngày hoặc vượt quá số lượng tối đa có thể sản xuất dựa trên nguyên liệu hiện có: Hiển thị thông báo lỗi chi tiết trên nhãn của hộp thoại.<br>- Nếu hợp lệ: Thực hiện gọi CSDL trừ lượng tồn kho nguyên liệu tương ứng theo công thức cấu hình và tạo phiếu xuất kho mới, sau đó đóng hộp thoại và tải lại danh sách phiếu xuất kho. | |
| 4 | Chọn button `OK` trên hộp thoại xuất hủy nguyên liệu hỏng | - Kiểm tra số lượng nguyên liệu cần hủy.<br>- Nếu số lượng không hợp lệ hoặc lớn hơn số lượng tồn kho thực tế của nguyên liệu được chọn: Hiển thị thông báo lỗi trên nhãn của hộp thoại.<br>- Nếu hợp lệ: Thực hiện gọi CSDL trừ tồn kho nguyên liệu bị hỏng và tạo phiếu xuất kho hủy, sau đó đóng hộp thoại và tải lại danh sách phiếu xuất kho. | |
| 5 | Chọn button `OK` trên hộp thoại xuất hủy bánh bảo quản hỏng | - Kiểm tra số lượng bánh bảo quản hỏng cần hủy.<br>- Nếu số lượng không hợp lệ hoặc lớn hơn số lượng tồn kho thực tế của sản phẩm bánh đó: Hiển thị thông báo lỗi trên nhãn của hộp thoại.<br>- Nếu hợp lệ: Thực hiện gọi CSDL trừ tồn kho thành phẩm bánh bảo quản bị hỏng và tạo phiếu xuất kho hủy, sau đó đóng hộp thoại và tải lại danh sách phiếu xuất kho. | |
| 6 | Chọn button `OK` trên hộp thoại xuất hủy bánh do sai sót sản xuất | - Kiểm tra số lượng bánh lỗi sản xuất cần hủy.<br>- Nếu số lượng không hợp lệ hoặc lớn hơn số lượng tồn kho thực tế của sản phẩm bánh đó: Hiển thị thông báo lỗi trên nhãn của hộp thoại.<br>- Nếu hợp lệ: Thực hiện gọi CSDL trừ tồn kho thành phẩm bánh bị lỗi trong sản xuất và tạo phiếu xuất kho hủy, sau đó đóng hộp thoại và tải lại danh sách phiếu xuất kho. | |
| 7 | Double click một dòng trên bảng `tblData` | - Thực hiện tải chi tiết danh sách nguyên liệu và thành phẩm thực tế đã xuất của phiếu xuất kho được chọn từ CSDL.<br>- Mở hộp thoại chi tiết phiếu xuất và hiển thị dữ liệu lên bảng `tbl`. | |
| 8 | Chọn button `btnInPhieu` | - Thực hiện kết xuất thông tin chi tiết của phiếu xuất kho đang được chọn ra tệp tin tài liệu định dạng PDF bằng JasperReports và lưu tại thư mục cấu hình.<br>- Hiển thị thông báo kết quả xuất phiếu in thành công kèm theo đường dẫn tệp tin PDF cho người dùng. | |
| 9 | Chọn button `btnDong` hoặc chọn nút đóng hộp thoại chi tiết phiếu xuất | - Đóng hộp thoại chi tiết phiếu xuất và quay lại giao diện chính của màn hình xuất kho. | |
