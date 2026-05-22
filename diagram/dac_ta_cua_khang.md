# Đặc tả usecase (Phần của Khang)
1.1.2.8. Quản lý kho và nguyên liệu
1.1.2.8.1. Thêm nguyên liệu
Bảng 47. Đặc tả Use – case Thêm nguyên liệu
Tên Use-case	Thêm nguyên liệu
Mô tả	-	Thủ kho ghi nhận thông tin một loại nguyên liệu mới dùng cho sản xuất kèm đơn vị tính vào hệ thống.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và đang ở màn hình Nguyên liệu.
Hậu điều kiện	-	Nguyên liệu mới được lưu vào hệ thống.
-	Danh sách nguyên liệu được làm mới.
Luồng sự kiện chính	1.	Thủ kho chọn thêm nguyên liệu mới.
2.	Hệ thống hiển thị biểu mẫu nhập thông tin.
3.	Thủ kho nhập tên nguyên liệu, quy cách bảo quản và chọn đơn vị tính.
4.	Thủ kho nhấn lưu.
5.	Hệ thống kiểm tra thông tin nhập vào hợp lệ và tên nguyên liệu không bị trùng lặp.
6.	Hệ thống lưu nguyên liệu mới.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	4a. Thủ kho nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Thông tin không hợp lệ hoặc trùng tên, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.2. Sửa nguyên liệu
Bảng 48. Đặc tả Use – case Sửa nguyên liệu
Tên Use-case	Sửa nguyên liệu
Mô tả	-	Thủ kho thay đổi thông tin tên gọi, quy cách bảo quản hoặc đơn vị tính của một nguyên liệu hiện có.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và đang ở màn hình Nguyên liệu.
-	Có ít nhất một nguyên liệu trong hệ thống.
Hậu điều kiện	-	Thông tin nguyên liệu được cập nhật trong hệ thống.
-	Danh sách nguyên liệu được làm mới.
Luồng sự kiện chính	1.	Thủ kho chọn sửa một nguyên liệu.
2.	Hệ thống hiển thị biểu mẫu với thông tin hiện tại.
3.	Thủ kho chỉnh sửa thông tin nguyên liệu.
4.	Thủ kho nhấn lưu.
5.	Hệ thống kiểm tra thông tin nhập vào hợp lệ và tên không bị trùng lặp.
6.	Hệ thống lưu thay đổi.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Thủ kho không thay đổi gì và nhấn lưu, hệ thống vẫn tiến hành cập nhật dữ liệu.
4a. Thủ kho nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Thông tin không hợp lệ hoặc trùng tên, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.3. Cập nhật trạng thái nguyên liệu
Bảng 49. Đặc tả Use – case Cập nhật trạng thái nguyên liệu
Tên Use-case	Cập nhật trạng thái nguyên liệu
Mô tả	-	Thủ kho vô hiệu hóa hoặc khôi phục nguyên liệu để ngừng hoặc tiếp tục sử dụng khi lập phiếu kho.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và đang ở màn hình Nguyên liệu.
-	Có ít nhất một nguyên liệu trong hệ thống.
Hậu điều kiện	-	Trạng thái nguyên liệu được cập nhật trong hệ thống.
-	Lịch sử thẻ kho và giá trị tồn kho không bị ảnh hưởng.
Luồng sự kiện chính	1.	Thủ kho chọn một nguyên liệu cần thay đổi trạng thái.
2.	Thủ kho nhấn vô hiệu hóa hoặc khôi phục tương ứng.
3.	Hệ thống hiển thị yêu cầu xác nhận.
4.	Thủ kho đồng ý xác nhận.
5.	Hệ thống ghi nhận trạng thái mới của nguyên liệu.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Thủ kho chuyển sang xem danh sách nguyên liệu đã bị vô hiệu hóa, hệ thống hiển thị danh sách đó.
4a. Thủ kho từ chối xác nhận, hệ thống hủy thao tác, trạng thái nguyên liệu giữ nguyên.
Luồng sự kiện lỗi	-	Bước 5: Lỗi lưu, hệ thống báo lỗi, trạng thái không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.4. Lập phiếu nhập kho
	Bảng 50. Đặc tả Use – case Lập phiếu nhập kho
Tên Use-case	Lập phiếu nhập kho
Mô tả	-	Thủ kho ghi nhận số lượng và đơn giá nguyên liệu mới mua từ nhà cung cấp để tăng tồn kho hệ thống.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và đang ở màn hình Kho.
-	Hệ thống có sẵn nhà cung cấp và nguyên liệu đang hoạt động.
Hậu điều kiện	-	Phiếu nhập kho được lưu vào hệ thống.
-	Số lượng tồn kho và thẻ kho tương ứng được cập nhật.
Luồng sự kiện chính	1.	Thủ kho chọn chức năng lập phiếu nhập kho.
2.	Hệ thống hiển thị biểu mẫu lập phiếu mới.
3.	Thủ kho chọn nhà cung cấp và thông tin chứng từ.
4.	Thủ kho chọn nguyên liệu, nhập số lượng, đơn giá và thêm vào chi tiết phiếu.
5.	Thủ kho nhấn xác nhận lưu phiếu nhập.
6.	Hệ thống kiểm tra phiếu có ít nhất một nguyên liệu và các số liệu lớn hơn 0.
7.	Hệ thống lưu phiếu, cập nhật tồn kho và thẻ kho.
8.	Hệ thống thông báo thành công và làm mới màn hình.
Luồng sự kiện phụ	4a. Thủ kho thay đổi số lượng hoặc xóa nguyên liệu khỏi phiếu, hệ thống tự động tính lại tổng tiền.
5a. Thủ kho nhấn hủy thao tác, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 6: Phiếu rỗng hoặc số liệu không hợp lệ, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 7: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.5. Lạp phiếu xuất kho
Bảng 51. Đặc tả Use – case Lập phiếu xuất kho
Tên Use-case	Lập phiếu xuất kho
Mô tả	-	Thợ bếp ghi nhận thao tác xuất nguyên liệu để sản xuất, làm giảm số lượng tồn kho trong hệ thống.
Tác nhân	-	Thợ bếp
Tiền điều kiện	-	Thợ bếp đã đăng nhập và đang ở màn hình Kho.
-	Có sẵn nguyên liệu đang hoạt động với tồn kho lớn hơn 0.
Hậu điều kiện	-	Phiếu xuất kho được lưu vào hệ thống.
-	Số lượng tồn kho giảm tương ứng và thẻ kho được cập nhật.
Luồng sự kiện chính	1.	Thợ bếp chọn chức năng lập phiếu xuất kho.
2.	Hệ thống hiển thị biểu mẫu lập phiếu mới.
3.	Thợ bếp nhập thông tin người nhận và ghi chú.
4.	Thợ bếp chọn nguyên liệu, nhập số lượng và thêm vào chi tiết phiếu.
5.	Thợ bếp nhấn xác nhận lưu phiếu xuất.
6.	Hệ thống kiểm tra phiếu có ít nhất một nguyên liệu và số lượng xuất không vượt quá tồn kho hiện tại.
7.	Hệ thống lưu phiếu, trừ tồn kho và cập nhật thẻ kho.
8.	Hệ thống thông báo thành công và làm mới màn hình.
Luồng sự kiện phụ	4a. Thợ bếp thay đổi số lượng hoặc xóa nguyên liệu khỏi phiếu, hệ thống cập nhật lại chi tiết.
5a. Thợ bếp nhấn hủy thao tác, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 6: Phiếu rỗng hoặc số lượng xuất lớn hơn tồn kho, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 7: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.6. Lập phiếu xuất hủy
Bảng 52. Đặc tả Use – case Lập phiếu xuất hủy
Tên Use-case	Lập phiếu xuất hủy
Mô tả	-	Thủ kho ghi nhận thao tác xuất hủy nguyên liệu do hỏng hóc hoặc hết hạn, làm giảm tồn kho hệ thống.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và đang ở màn hình Kho.
-	Có sẵn nguyên liệu đang hoạt động với tồn kho lớn hơn 0.
Hậu điều kiện	-	Phiếu xuất hủy được lưu vào hệ thống.
-	Số lượng tồn kho giảm tương ứng và thẻ kho được cập nhật.
Luồng sự kiện chính	1.	Thủ kho chọn chức năng lập phiếu xuất hủy.
2.	Hệ thống hiển thị biểu mẫu lập phiếu mới.
3.	Thủ kho nhập lý do xuất hủy.
4.	Thủ kho chọn nguyên liệu, nhập số lượng và thêm vào chi tiết phiếu.
5.	Thủ kho nhấn xác nhận lưu phiếu xuất hủy.
6.	Hệ thống kiểm tra phiếu có ít nhất một nguyên liệu và số lượng hủy không vượt quá tồn kho hiện tại.
7.	Hệ thống lưu phiếu, trừ tồn kho và cập nhật thẻ kho.
8.	Hệ thống thông báo thành công và làm mới màn hình.
Luồng sự kiện phụ	4a. Thủ kho thay đổi số lượng hoặc xóa nguyên liệu khỏi phiếu, hệ thống cập nhật lại chi tiết.
5a. Thủ kho nhấn hủy thao tác, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 6: Phiếu rỗng hoặc số lượng hủy lớn hơn tồn kho, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 7: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.8.7. Cảnh báo tồn kho 
Bảng 53. Đặc tả Use – case Cảnh báo tồn kho
Tên Use-case	Cảnh báo tồn kho
Mô tả	-	Hệ thống tự động cảnh báo cho thủ kho khi có nguyên liệu rớt xuống dưới mức tồn kho an toàn.
Tác nhân	-	Hệ thống
Tiền điều kiện	-	Hệ thống đang chạy và có ít nhất một nguyên liệu với mức tồn kho an toàn được thiết lập.
Hậu điều kiện	-	Cảnh báo được gửi đến thủ kho để biết được các nguyên liệu cần được nhập thêm.
Luồng sự kiện chính	1.	Hệ thống định kỳ kiểm tra tồn kho của từng nguyên liệu so với mức an toàn.
2.	Hệ thống phát hiện một nguyên liệu có tồn kho nhỏ hơn hoặc bằng mức an toàn.
3.	Hệ thống gửi cảnh báo đến màn hình thủ kho hoặc hộp thoại thông báo.
4.	Thủ kho xem cảnh báo và biết được cần nhập thêm nguyên liệu nào.
Luồng sự kiện phụ	1a. Nhiều nguyên liệu rớt dưới mức an toàn cùng một lúc, hệ thống liệt kê tất cả các nguyên liệu trong một cảnh báo chung.
3a. Thủ kho nhấn "Xem chi tiết", hệ thống hiển thị bảng chi tiết tồn kho hiện tại của nguyên liệu đó.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối hoặc lỗi truy vấn, hệ thống không thể kiểm tra được, cảnh báo bị bỏ qua.

1.1.2.8.8. Tra cứu thẻ kho
Bảng 54. Đặc tả Use – case Tra cứu thẻ kho
Tên Use-case	Tra cứu thẻ kho
Mô tả	-	Thủ kho xem lại toàn bộ lịch sử biến động nhập, xuất, tồn của một loại nguyên liệu cụ thể.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền xem thẻ kho.
-	Có ít nhất một phiếu nhập, xuất hoặc hủy trong hệ thống.
Hậu điều kiện	-	Lịch sử biến động thẻ kho được hiển thị đầy đủ.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Thủ kho chọn chức năng tra cứu thẻ kho.
2.	Hệ thống hiển thị danh sách nguyên liệu.
3.	Thủ kho tìm kiếm hoặc chọn một nguyên liệu.
4.	Hệ thống tải và hiển thị lịch sử thẻ kho với các cột: ngày, loại phiếu, số lượng, tồn kho, người thực hiện.
5.	Thủ kho xem lịch sử để kiểm tra độ chính xác hoặc truy vết nguồn gốc.
Luồng sự kiện phụ	2a. Thủ kho nhập từ khóa để lọc nhanh nguyên liệu, hệ thống lọc danh sách theo tên.
4a. Thủ kho chọn một dòng trong lịch sử, hệ thống hiển thị chi tiết phiếu tương ứng.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối, hệ thống báo lỗi, danh sách hiển thị rỗng. 

1.1.2.9. Quản lý nhà cung cấp
1.1.2.9.1. Truy vết nguồn gốc
Bảng 55. Đặc tả Use – case Truy vết nguồn gốc
Tên Use-case	Truy vết nguồn gốc
Mô tả	-	Thủ kho tra cứu ngược lại các phiếu nhập kho để xác định nhà cung cấp của lô nguyên liệu gặp sự cố.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền xem thẻ kho.
-	Có ít nhất một phiếu nhập kho liên kết với nguyên liệu cần truy vết.
Hậu điều kiện	-	Thông tin nhà cung cấp được xác định rõ ràng.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Thủ kho vào trang thẻ kho của nguyên liệu gặp sự cố 
2.	Thủ kho tìm các phiếu nhập liên quan bằng cách lọc theo loại phiếu nhập.
3.	Thủ kho chọn một phiếu nhập.
4.	Hệ thống hiển thị chi tiết phiếu nhập: ngày nhập, nhà cung cấp, số lô, thời hạn sử dụng.
5.	Thủ kho xác định được nhà cung cấp và thông tin lô hàng gặp sự cố.
Luồng sự kiện phụ	2a. Có nhiều phiếu nhập cùng nguyên liệu, thủ kho so sánh thời gian và thời hạn để xác định lô hàng gặp sự cố.
4a. Thủ kho nhấn "Xem thông tin nhà cung cấp", hệ thống mở chi tiết hồ sơ nhà cung cấp.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối hoặc thẻ kho rỗng, hệ thống báo lỗi, không hiển thị lịch sử. 

1.1.2.9.2. Thêm nhà cung cấp
Bảng 56. Đặc tả Use – case Thêm nhà cung cấp
Tên Use-case	Thêm nhà cung cấp
Mô tả	-	Thủ kho tạo mới hồ sơ một đối tác cung cấp nguyên liệu vào hệ thống với thông tin liên hệ cần thiết.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền quản lý nhà cung cấp.
Hậu điều kiện	-	Nhà cung cấp mới được lưu và hiển thị trong danh sách.
-	Có thể sử dụng nhà cung cấp này khi lập phiếu nhập kho.
Luồng sự kiện chính	1.	Thủ kho chọn chức năng thêm nhà cung cấp mới.
2.	Hệ thống hiển thị biểu mẫu nhập thông tin.
3.	Thủ kho nhập tên nhà cung cấp, địa chỉ, số điện thoại, email và người đại diện.
4.	Thủ kho nhấn lưu.
5.	Hệ thống kiểm tra thông tin bắt buộc đầy đủ và tên nhà cung cấp không trùng lặp.
6.	Hệ thống lưu nhà cung cấp mới.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	4a. Thủ kho nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Thông tin không hợp lệ hoặc trùng tên, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.9.3. Sửa nhà cung cấp
Bảng 57. Đặc tả Use – case Sửa nhà cung cấp
Tên Use-case	Sửa nhà cung cấp
Mô tả	-	Thủ kho thay đổi thông tin địa chỉ, số điện thoại hoặc người đại diện của đối tác cung cấp hiện tại.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền quản lý nhà cung cấp.
-	Có ít nhất một nhà cung cấp trong hệ thống.
Hậu điều kiện	-	Thông tin nhà cung cấp được cập nhật trong hệ thống.
-	Danh sách nhà cung cấp được làm mới.
Luồng sự kiện chính	1.	Thủ kho chọn sửa một nhà cung cấp.
2.	Hệ thống hiển thị biểu mẫu với thông tin hiện tại.
3.	Thủ kho chỉnh sửa thông tin cần thay đổi.
4.	Thủ kho nhấn lưu.
5.	Hệ thống kiểm tra thông tin nhập vào hợp lệ và tên không bị trùng lặp với nhà cung cấp khác.
6.	Hệ thống lưu thay đổi.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Thủ kho không thay đổi gì và nhấn lưu, hệ thống vẫn tiến hành cập nhật dữ liệu.
4a. Thủ kho nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Thông tin không hợp lệ hoặc trùng tên, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.9.4. Cập nhật trạng thái nhà cung cấp
Bảng 58. Đặc tả Use – case Cập nhật trạng thái nhà cung cấp
Tên Use-case	Cập nhật trạng thái nhà cung cấp
Mô tả	-	Thủ kho khóa hồ sơ đối tác khi ngừng hợp tác hoặc mở khóa để tiếp tục giao dịch, bảo toàn phiếu cũ.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền quản lý nhà cung cấp.
-	Có ít nhất một nhà cung cấp trong hệ thống.
Hậu điều kiện	-	Trạng thái nhà cung cấp được cập nhật trong hệ thống.
-	Lịch sử phiếu nhập cũ không bị ảnh hưởng.
Luồng sự kiện chính	1.	Thủ kho chọn một nhà cung cấp cần thay đổi trạng thái.
2.	Thủ kho nhấn khóa hoặc mở khóa tương ứng.
3.	Hệ thống hiển thị yêu cầu xác nhận.
4.	Thủ kho đồng ý xác nhận.
5.	Hệ thống ghi nhận trạng thái mới của nhà cung cấp.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Thủ kho chuyển sang xem danh sách nhà cung cấp đã bị khóa, hệ thống hiển thị danh sách đó.
4a. Thủ kho từ chối xác nhận, hệ thống hủy thao tác, trạng thái không thay đổi.
Luồng sự kiện lỗi	-	Bước 5: Lỗi lưu, hệ thống báo lỗi, trạng thái không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.9.5. Tra cứu nhà cung cấp
Bảng 59. Đặc tả Use – case Tra cứu nhà cung cấp
Tên Use-case	Tra cứu nhà cung cấp
Mô tả	-	Thủ kho tìm kiếm và xem thông tin chi tiết của các nhà cung cấp hiện có trong hệ thống.
Tác nhân	-	Thủ kho
Tiền điều kiện	-	Thủ kho đã đăng nhập và có quyền xem thông tin nhà cung cấp.
-	Có ít nhất một nhà cung cấp đang hoạt động trong hệ thống.
Hậu điều kiện	-	Danh sách nhà cung cấp được hiển thị theo điều kiện.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Hệ thống tải và hiển thị danh sách nhà cung cấp đang hoạt động khi vào màn hình.
2.	Thủ kho gõ từ khóa vào ô tìm kiếm, hệ thống lọc và cập nhật danh sách ngay.
3.	Thủ kho chọn một nhà cung cấp, hệ thống hiển thị chi tiết hồ sơ: tên, địa chỉ, điện thoại, email, người đại diện, ngày tạo.
Luồng sự kiện phụ	2a. Thủ kho nhập từ khóa rỗng, hệ thống hiển thị toàn bộ danh sách.
3a. Thủ kho nhấn "Xem lịch sử giao dịch", hệ thống hiển thị danh sách các phiếu nhập có liên quan đến nhà cung cấp này.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối, hệ thống báo lỗi, danh sách hiển thị rỗng.

1.1.2.10. Báo cáo
1.1.2.10.1. Báo cáo lợi nhuận
Bảng 60. Đặc tả Use – case Báo cáo lợi nhuận
Tên Use-case	Báo cáo lợi nhuận
Mô tả	-	Quản lý xem thống kê lợi nhuận của hệ thống trong một khoảng thời gian dựa trên doanh thu và chi phí.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền xem báo cáo tài chính.
-	Có ít nhất một hóa đơn bán hàng hoàn tất trong hệ thống.
Hậu điều kiện	-	Báo cáo lợi nhuận được hiển thị đầy đủ với các chỉ số chi tiết.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Quản lý chọn chức năng báo cáo lợi nhuận.
2.	Hệ thống hiển thị biểu mẫu chọn khoảng thời gian từ ngày nào đến ngày nào.
3.	Quản lý chọn thời gian cần báo cáo và nhấn "Tạo báo cáo".
4.	Hệ thống tính toán tổng doanh thu từ các hóa đơn bán hàng hoàn tất.
5.	Hệ thống tính toán tổng chi phí từ các phiếu nhập kho trong khoảng thời gian.
6.	Hệ thống hiển thị báo cáo: doanh thu, chi phí, lợi nhuận gộp, tỷ suất lợi nhuận.
Luồng sự kiện phụ	4a. Báo cáo trống nếu không có doanh thu. 
Luồng sự kiện lỗi	-	Bước 4: Lỗi tính toán hoặc kết nối, hệ thống báo lỗi, không hiển thị báo cáo. 

1.1.2.10.2. Báo cáo doanh thu
Bảng 61. Đặc tả Use – case Báo cáo doanh thu
Tên Use-case	Báo cáo doanh thu
Mô tả	-	Quản lý xem thống kê số tiền thu được theo ca, ngày, tháng hoặc năm kèm theo biểu đồ xu hướng doanh thu.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền xem báo cáo thống kê.
Hậu điều kiện	-	Hệ thống hiển thị số liệu và biểu đồ doanh thu.
-	Không có dữ liệu nào bị thay đổi.
Luồng sự kiện chính	1.	Quản lý chọn chức năng xem báo cáo doanh thu.
2.	Quản lý chọn tiêu chí và khoảng thời gian cần thống kê.
3.	Hệ thống tổng hợp dữ liệu hóa đơn và thu tiền trong khoảng thời gian yêu cầu.
4.	Hệ thống tính toán tổng doanh thu tương ứng.
5.	Hệ thống hiển thị bảng số liệu chi tiết và biểu đồ xu hướng.
Luồng sự kiện phụ	2a. Quản lý không chọn khoảng thời gian, hệ thống mặc định thống kê cho ngày hiện tại.
4a. Không có dữ liệu giao dịch trong kỳ, hệ thống hiển thị số liệu bằng không và thông báo trống.
Luồng sự kiện lỗi	-	Bước 3: Lỗi truy xuất dữ liệu, hệ thống báo lỗi, không hiển thị được báo cáo.

1.1.2.10.3. Báo cáo tồn kho
Bảng 62. Đặc tả Use – case Báo cáo tồn kho
Tên Use-case	Báo cáo tồn kho
Mô tả	-	Quản lý xem thống kê số lượng, giá trị tồn kho của nguyên liệu và cảnh báo mặt hàng sắp hết hạn.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền xem báo cáo thống kê.
Hậu điều kiện	-	Hệ thống hiển thị số liệu tồn kho và cảnh báo.
-	Không có dữ liệu nào bị thay đổi.
Luồng sự kiện chính	1.	Quản lý chọn chức năng xem báo cáo tồn kho.
2.	Hệ thống rà soát toàn bộ nguyên liệu đang lưu kho.
3.	Hệ thống tổng hợp số lượng, đơn giá và tính toán tổng giá trị tồn kho.
4.	Hệ thống lọc ra các lô nguyên liệu sắp hết hạn.
5.	Hệ thống hiển thị bảng số liệu tồn kho chi tiết, tổng giá trị và danh sách mặt hàng cần chú ý.
Luồng sự kiện phụ	4a. Không có nguyên liệu nào sắp hết hạn, hệ thống chỉ hiển thị số liệu tổng hợp, ẩn phần cảnh báo.
Luồng sự kiện lỗi	-	Bước 2: Lỗi truy xuất dữ liệu, hệ thống báo lỗi, không hiển thị được báo cáo.

1.1.2.11. Quản lý thu chi
1.1.2.11.1. Thêm loại thu chi
Bảng 63. Đặc tả Use – case Thêm loại thu chi
Tên Use-case	Thêm loại thu chi
Mô tả	-	Quản lý tạo mới một danh mục thu hoặc chi để phân loại giao dịch khi lập phiếu trong ca làm việc.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sổ quỹ.
-	Hệ thống tải xong danh sách loại thu chi hiện tại.
Hậu điều kiện	-	Loại thu chi mới được lưu vào hệ thống.
-	Danh sách loại thu chi được làm mới.
Luồng sự kiện chính	1.	Quản lý chọn thêm loại thu chi mới.
2.	Hệ thống hiển thị biểu mẫu với phân loại mặc định là khoản thu.
3.	Quản lý nhập tên và chọn phân loại thu hoặc chi.
4.	Quản lý nhấn lưu.
5.	Hệ thống kiểm tra tên không rỗng và phân loại hợp lệ.
6.	Hệ thống lưu loại thu chi mới.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Quản lý không thay đổi phân loại, hệ thống giữ nguyên phân loại mặc định.
4a. Quản lý nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Tên trống hoặc phân loại không hợp lệ, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.11.2. Sửa loại thu chi
Bảng 64. Đặc tả Use – case Sửa loại thu chi
Tên Use-case	Sửa loại thu chi
Mô tả	-	Quản lý chỉnh sửa tên hoặc phân loại của một danh mục đã có, thay đổi có hiệu lực ngay trên hệ thống.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sổ quỹ.
-	Hệ thống tải xong danh sách loại thu chi hiện tại.
Hậu điều kiện	-	Thông tin loại thu chi được cập nhật trong hệ thống.
-	Danh sách loại thu chi được làm mới.
Luồng sự kiện chính	1.	Quản lý chọn sửa một loại thu chi.
2.	Hệ thống hiển thị biểu mẫu với thông tin hiện tại.
3.	Quản lý chỉnh sửa thông tin.
4.	Quản lý nhấn lưu.
5.	Hệ thống kiểm tra tên không rỗng.
6.	Hệ thống lưu thay đổi.
7.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Quản lý không thay đổi gì và nhấn lưu, hệ thống vẫn tiến hành cập nhật dữ liệu.
4a. Quản lý nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Tên trống, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.11.3. Cập nhật trạng thái loại thu chi 
Bảng 65. Đặc tả Use – case Cập nhật trạng thái loại thu chi
Tên Use-case	Cập nhật trạng thái loại thu chi
Mô tả	-	Quản lý vô hiệu hóa loại thu chi để ngừng sử dụng, hoặc khôi phục để đưa danh mục đó trở lại.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sổ quỹ.
-	Có ít nhất một loại thu chi trong hệ thống.
Hậu điều kiện	-	Trạng thái loại thu chi được cập nhật trong hệ thống.
-	Dữ liệu lịch sử giao dịch liên quan không bị ảnh hưởng.
Luồng sự kiện chính	1.	Quản lý chọn một loại thu chi cần thay đổi trạng thái.
2.	Quản lý nhấn khóa hoặc mở khóa tương ứng.
3.	Hệ thống xác nhận và ghi nhận thời điểm thay đổi.
4.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Quản lý chuyển sang xem danh sách loại thu chi đã bị khóa, hệ thống hiển thị danh sách tương ứng.
Luồng sự kiện lỗi	-	Bước 3: Lỗi lưu, hệ thống báo lỗi, trạng thái không thay đổi, quá trình lưu bị hủy bỏ.

1.1.2.11.4. Lập phiếu thu chi
Bảng 66. Đặc tả Use – case Lập phiếu thu chi
Tên Use-case	Lập phiếu thu chi
Mô tả	-	Người dùng ghi nhận một khoản thu hoặc chi phát sinh trong ca làm việc bằng cách tạo phiếu mới.
Tác nhân	-	Thu ngân
-	Quản lý
Tiền điều kiện	-	Người dùng đã đăng nhập vào hệ thống.
-	Ca làm việc đang được mở.
Hậu điều kiện	-	Phiếu thu chi mới được lưu vào hệ thống.
-	Danh sách giao dịch được làm mới.
Luồng sự kiện chính	1.	Người dùng chọn tạo giao dịch mới.
2.	Hệ thống hiển thị biểu mẫu lập phiếu, mặc định chọn loại khoản thu.
3.	Người dùng chọn loại giao dịch và danh mục tương ứng.
4.	Người dùng nhập số tiền, mô tả và nhấn xác nhận.
5.	Hệ thống kiểm tra danh mục và số tiền hợp lệ.
6.	Hệ thống lưu phiếu, làm mới danh sách và hiển thị thông báo thành công.
Luồng sự kiện phụ	3a. Người dùng đổi loại giao dịch, hệ thống cập nhật danh sách danh mục tương ứng.
4a. Người dùng nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Chưa chọn danh mục hoặc số tiền không hợp lệ hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Ca làm việc đã bị đóng hoặc lỗi lưu, hệ thống báo lỗi, quá trình lưu bị hủy bỏ.

1.1.2.11.5. Hủy phiếu thu chi
Bảng 67. Đặc tả Use – case Hủy phiếu thu chi
Tên Use-case	Hủy giao dịch
Mô tả	-	Người dùng hủy một phiếu thu chi đang hiệu lực. Phiếu bị hủy không còn được tính vào tổng quan quỹ.
Tác nhân	-	Thu ngân
-	Quản lý
Tiền điều kiện	-	Người dùng đã đăng nhập vào hệ thống.
-	Phiếu cần hủy đang ở trạng thái còn hiệu lực.
Hậu điều kiện	-	Trạng thái phiếu được cập nhật thành đã hủy.
-	Lý do hủy được ghi nhận và số liệu được tính lại.
Luồng sự kiện chính	1.	Người dùng chọn hủy một phiếu giao dịch.
2.	Hệ thống cảnh báo hành động không thể hoàn tác và yêu cầu nhập lý do.
3.	Người dùng nhập lý do hủy và xác nhận.
4.	Hệ thống cập nhật trạng thái phiếu và ghi nhận lý do.
5.	Hệ thống làm mới danh sách và tính lại tổng quan quỹ.
Luồng sự kiện phụ	3a. Người dùng từ chối xác nhận, hệ thống hủy thao tác, phiếu vẫn giữ nguyên trạng thái.
Luồng sự kiện lỗi	-	Bước 3: Lý do rỗng, hệ thống báo lỗi, yêu cầu nhập lý do.
-	Bước 4: Lỗi lưu, hệ thống báo lỗi, trạng thái phiếu không thay đổi, quá trình hủy bị hủy bỏ.


 
