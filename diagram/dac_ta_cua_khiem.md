3.2.4. Quản lý bán hàng
3.2.4.1. Lập hóa đơn bán lẻ
­	Đặc tả:
Tên Use-case	Lập hóa đơn bán lẻ
Mô tả	-	Thu ngân chọn sản phẩm, xác nhận thanh toán tại quầy và xuất hóa đơn cho khách ngay sau giao dịch.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Thu ngân đã đăng nhập và ca làm việc đang mở.
-	Có ít nhất một sản phẩm đang hoạt động trong hệ thống.
Hậu điều kiện	-	Đơn hàng được lưu với trạng thái Hoàn thành.
-	Hóa đơn được tạo, phiếu thu ghi vào sổ quỹ ca.
-	Điểm và hạng thành viên được cập nhật tự động.
Luồng sự kiện chính	1.	Thu ngân chọn sản phẩm và số lượng vào giỏ hàng trên màn hình POS.
2.	Thu ngân nhấn "Thanh toán", hệ thống mở hộp thoại thông tin khách.
3.	Thu ngân nhập số điện thoại tìm khách rồi nhấn "Tiếp theo" sang bước thanh toán.
4.	Thu ngân chọn hình thức thanh toán và nhập số tiền khách đưa.
5.	Hệ thống tính và hiển thị tiền thừa ngay lập tức.
6.	 Thu ngân nhấn "Xác nhận".
7.	Hệ thống lưu đơn hàng, xuất hóa đơn, cộng điểm và ghi phiếu thu trong một lần.
8.	Hệ thống hiển thị hóa đơn; thu ngân in cho khách.
Luồng sự kiện phụ	3a. Thu ngân không nhập SĐT hoặc không tìm thấy khách, hệ thống đặt là "Khách vãng lai", không áp dụng giảm giá thành viên, tiếp tục bình thường.
3b. Tìm thấy khách thành viên, hệ thống xác nhận và áp dụng mức giảm giá tương ứng vào tổng tiền.
4a. Thu ngân chọn hình thức chuyển khoản, hệ thống hiển thị mã thanh toán, không yêu cầu nhập tiền thủ công.
Luồng sự kiện lỗi	-	Bước 4: Số tiền khách đưa chưa đủ, hệ thống báo lỗi và giữ nguyên hộp thoại.
-	Bước 7: Lỗi cơ sở dữ liệu, hệ thống báo lỗi, không lưu bất kỳ dữ liệu nào.

­	Sơ đồ hoạt động:

3.2.4.2. Hủy hóa đơn bán lẻ
­	Đặc tả:
Tên Use-case	Hủy hóa đơn bán lẻ
Mô tả	-	Nhân viên xử lý hủy đơn hoặc trả hàng cho khách, hệ thống tự động tính hoàn tiền và cập nhật kho.
Tác nhân	-	Quản lý 
-	Thu ngân
Tiền điều kiện	-	Nhân viên đã đăng nhập và có quyền xử lý đơn.
-	Đơn hàng cần xử lý đang tồn tại trên hệ thống.
Hậu điều kiện	-	Trạng thái đơn hàng được chuyển sang bị hủy.
-	Số tiền được hoàn lại và tồn kho được cập nhật.
Luồng sự kiện chính	1.	Nhân viên chọn chức năng trả hàng hoặc hủy đơn.
2.	Nhân viên tìm kiếm và chọn đơn hàng cần xử lý.
3.	Hệ thống hiển thị chi tiết thông tin đơn hàng.
4.	 Nhân viên chọn xác nhận hủy và nhập lý do.
5.	 Hệ thống kiểm tra trạng thái đơn hàng hiện tại có đủ điều kiện cho phép hủy hay không.
6.	Hệ thống lưu trạng thái đơn bị hủy, tự động tính hoàn tiền và cộng lại tồn kho.
7.	 Hệ thống thông báo thành công và làm mới màn hình.
Luồng sự kiện phụ	4a. Nhân viên nhấn hủy thao tác, hệ thống đóng biểu mẫu, không thay đổi dữ liệu đơn hàng.
Luồng sự kiện lỗi	-	Bước 5: Đơn hàng không đủ điều kiện hủy, hệ thống báo lỗi, từ chối thực hiện thao tác.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, đơn hàng không thay đổi, quá trình lưu bị hủy bỏ.

­	Sơ đồ hoạt động:

3.2.4.3. Lập đơn đặt bánh tùy chỉnh
­	Đặc tả:
Tên Use-case	Lập đơn đặt bánh tùy chỉnh
Mô tả	-	Thu ngân tạo đơn đặt trước cho khách muốn bánh theo yêu cầu riêng, thu cọc và hẹn ngày giờ nhận bánh.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Thu ngân đã đăng nhập và ca làm việc đang mở.
-	Có ít nhất một sản phẩm đang hoạt động.
-	Danh sách tùy chỉnh đã được cấu hình trong hệ thống
Hậu điều kiện	-	Đơn hàng được lưu với trạng thái Đã cọc.
-	Tiền cọc được ghi nhận và phiếu thu được tạo.
-	Điểm thành viên chưa cộng, sẽ cộng khi giao hàng.
Luồng sự kiện chính	1.	Thu ngân chọn sản phẩm, số lượng và tùy chỉnh vào giỏ hàng trên màn hình bán hàng.
2.	Thu ngân nhấn "Thanh toán", hệ thống mở hộp thoại thông tin khách.
3.	Thu ngân nhập số điện thoại và nhấn "Tìm".
4.	Thu ngân nhấn "Tiếp theo", hệ thống chuyển sang bước thanh toán theo luồng đặt trước.
5.	Thu ngân chọn ngày giờ nhận, nhập địa chỉ giao và số tiền cọc.
6.	Thu ngân chọn hình thức thanh toán cọc.
7.	Thu ngân nhấn "Xác nhận".
8.	Hệ thống lưu đơn, ghi phiếu thu cọc và thông báo tạo đơn thành công.
Luồng sự kiện phụ	3a. Thu ngân không nhập SĐT hoặc không tìm thấy khách, hệ thống đặt là "Khách vãng lai", tiếp tục bình thường.
3b. Tìm thấy khách thành viên, hệ thống xác nhận và áp dụng mức giảm giá tương ứng vào tổng tiền.
6a. Thu ngân chọn chuyển khoản, hệ thống hiển thị mã thanh toán, không yêu cầu nhập tiền thủ công.
Luồng sự kiện lỗi	-	Bước 5: Ngày giờ nhận trong quá khứ hoặc tiền cọc chưa đủ 50%, hệ thống báo lỗi, giữ nguyên hộp thoại.
-	Bước 8: Lỗi cơ sở dữ liệu, hệ thống báo lỗi, không lưu bất kỳ dữ liệu nào.

­	Sơ đồ hoạt động:

3.2.4.4. Cập nhật trạng thái đơn
­	Đặc tả:
Tên Use-case	Cập nhật trạng thái đơn
Mô tả	-	Thu ngân theo dõi danh sách đơn đặt trước và chuyển trạng thái đơn qua từng bước cho đến khi hoàn thành.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Thu ngân đã đăng nhập và ca làm việc đang mở.
-	Có ít nhất một đơn đặt trước chưa hoàn thành
Hậu điều kiện	-	Trạng thái đơn được cập nhật trong hệ thống.
-	Nếu chuyển sang Hoàn thành: hóa đơn được tạo, phiếu thu và điểm thành viên được ghi nhận.
Luồng sự kiện chính	1.	Hệ thống hiển thị danh sách đơn chưa hoàn thành của ngày hiện tại.
2.	Thu ngân chọn trạng thái mới cho đơn cần cập nhật.
3.	Thu ngân nhấn "Cập nhật".
4.	Hệ thống lưu trạng thái mới và làm mới danh sách.
Luồng sự kiện phụ	1a. Thu ngân lọc danh sách theo ngày hoặc trạng thái — hệ thống làm mới danh sách ngay lập tức.
3a. Thu ngân chuyển sang Hoàn thành khi còn tiền chưa thanh toán — hệ thống mở hộp thoại xác nhận và ghi phiếu thu, cộng điểm khi thu ngân xác nhận.
3b. Thu ngân nhấn "Chi tiết" — hệ thống hiển thị thông tin đầy đủ của đơn hàng.
Luồng sự kiện lỗi	-	Bước 3: Đơn đã Hoàn thành hoặc trạng thái mới trùng hiện tại, hệ thống báo lỗi, giữ nguyên.
-	Bước 4: Lỗi cơ sở dữ liệu, hệ thống báo lỗi, trạng thái đơn không thay đổi.

­	Sơ đồ hoạt động:

3.2.4.5. Tra cứu danh sách đơn
­	Đặc tả:
Tên Use-case	Tra cứu danh sách đơn
Mô tả	-	Thu ngân lọc và tìm kiếm đơn đặt trước theo ngày, giờ nhận, mã đơn hoặc trạng thái.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Thu ngân đã đăng nhập và ca làm việc đang mở.
Hậu điều kiện	-	Danh sách đơn khớp điều kiện lọc được hiển thị.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Thu ngân vào màn hình theo dõi đơn hàng.
2.	Hệ thống tải và hiển thị danh sách đơn chưa hoàn thành của ngày hiện tại.
3.	Thu ngân điều chỉnh bộ lọc theo ngày, giờ, mã đơn hoặc trạng thái rồi nhấn "Tìm kiếm".
4.	Hệ thống làm mới danh sách theo điều kiện đã chọn.
Luồng sự kiện phụ	3a. Thu ngân thay đổi bộ lọc trạng thái, hệ thống tự động làm mới danh sách không cần nhấn tìm.
3b. Thu ngân chọn "Tất cả" trạng thái, hệ thống hiển thị toàn bộ đơn kể cả đã hoàn thành.
3c. Thu ngân để trống mã đơn, hệ thống chỉ lọc theo ngày và trạng thái.
Luồng sự kiện lỗi	-	Bước 3: Mã đơn nhập không hợp lệ, hệ thống báo lỗi, danh sách không cập nhật.
-	Bước 4: Không có đơn khớp hoặc lỗi kết nối, hệ thống hiển thị danh sách rỗng. 

­	Sơ đồ hoạt động:

3.2.4.6. Hủy đơn và hoàn cọc
­	Đặc tả:
Tên Use-case	Hủy đơn và hoàn cọc
Mô tả	-	Hủy đơn đặt hàng trước ngày giao, giải phóng năng lực sản xuất và xử lý hoàn trả tiền cọc cho khách.
Tác nhân	-	Thu ngân
-	Quản lý
Tiền điều kiện	-	Người dùng đã đăng nhập và có quyền xử lý đơn.
-	Đơn đặt hàng đang ở trạng thái chưa giao.
Hậu điều kiện	-	Đơn hàng được chuyển sang trạng thái đã hủy.
-	Hệ thống ghi nhận hoàn tiền cọc. 
Luồng sự kiện chính	1.	Người dùng chọn chức năng hủy đơn từ màn hình chi tiết đơn đặt hàng.
2.	Hệ thống hiển thị biểu mẫu xác nhận hủy đơn.
3.	Người dùng nhập lý do hủy đơn và nhấn tiếp tục.
4.	Hệ thống kiểm tra đơn hàng có tiền cọc để hiển thị thông tin hoàn cọc.
5.	Người dùng xác nhận số tiền hoàn cọc và phương thức hoàn tiền.
6.	Hệ thống lưu trạng thái đơn bị hủy và ghi nhận giao dịch hoàn cọc.
7.	Hệ thống thông báo thành công và làm mới màn hình.
Luồng sự kiện phụ	4a. Đơn hàng không có tiền cọc, hệ thống bỏ qua bước hoàn cọc và chuyển thẳng sang bước lưu.
5a. Người dùng nhấn hủy thao tác, hệ thống đóng biểu mẫu, không hủy đơn.
Luồng sự kiện lỗi	-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.

­	Sơ đồ hoạt động:

3.2.5. Quản lý ca làm việc
3.2.5.1. Mở ca làm việc
­	Đặc tả:
Tên Use-case	Mở ca làm việc
Mô tả	-	Nhân viên khai báo số tiền trong két và chọn máy thanh toán để bắt đầu ca làm việc mới.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Nhân viên đã đăng nhập thành công vào hệ thống.
-	Chưa có ca làm việc nào đang mở cho tài khoản này.
Hậu điều kiện	-	Ca làm việc mới được tạo với trạng thái "Đang mở".
-	Bản ghi đối soát đầu ca được tạo kèm số tiền khai báo.
-	Hệ thống chuyển sang màn hình làm việc chính.
Luồng sự kiện chính	1.	Hệ thống hiển thị màn hình mở ca, điền sẵn họ tên nhân viên đang đăng nhập.
2.	Hệ thống tự chọn sẵn máy thanh toán đầu tiên trong danh sách.
3.	Nhân viên chọn máy thanh toán phù hợp với vị trí làm việc.
4.	Nhân viên nhập số tiền thực có trong két đầu ca.
5.	Nhân viên nhấn nút "Bắt Đầu Làm Việc".
6.	Hệ thống kiểm tra máy thanh toán đã chọn và số tiền khai báo hợp lệ.
7.	Hệ thống tạo ca làm việc và bản ghi đối soát đầu ca.
8.	Hệ thống chuyển thẳng sang màn hình làm việc chính.
Luồng sự kiện phụ	4a. Nhân viên bỏ trống ô tiền đầu ca, hệ thống tự ghi nhận số tiền đầu ca là 0 đồng.
5a. Nhân viên nhấn "Đăng xuất" thay vì "Bắt Đầu", hệ thống xoá phiên và quay về màn hình đăng nhập.
Luồng sự kiện lỗi	-	Bước 6: Chưa chọn máy hoặc số tiền không hợp lệ, hệ thống báo lỗi, giữ nguyên màn hình.
-	Bước 7: Máy đã có ca đang mở hoặc lỗi kết nối, hệ thống báo lỗi, ca không được tạo.

­	Sơ đồ hoạt động:

3.2.5.2. Đóng ca và đối soát
­	Đặc tả:
Tên Use-case	Đóng ca làm việc
Mô tả	-	Nhân viên đếm tiền thực tế trong két, đối chiếu với số liệu hệ thống rồi khoá sổ để kết thúc ca.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Nhân viên đã đăng nhập và có ca đang mở.
Hậu điều kiện	-	Ca làm việc chuyển trạng thái sang "Đã đóng".
-	Kết quả đối soát được lưu và báo cáo ca được tạo.
-	Hệ thống quay về màn hình đăng nhập.
Luồng sự kiện chính	1.	Nhân viên mở hộp thoại "Đối Soát & Đóng Ca" từ màn hình làm việc chính.
2.	Hệ thống tải và hiển thị thông tin ca: tiền khai báo đầu ca và doanh thu phát sinh trong ca.
3.	Nhân viên đếm tiền thực tế trong két rồi nhập vào ô kiểm tra.
4.	Nhân viên nhấn nút "Kiểm Tra".
5.	Hệ thống đối chiếu số tiền nhập với số tiền theo tính toán của hệ thống, hiển thị kết quả khớp.
6.	Nhân viên nhấn nút "Khoá Sổ".
7.	Hệ thống lưu kết quả đối soát, đóng ca và hiển thị màn hình tổng kết.
8.	Nhân viên nhấn "Hoàn Tất", hệ thống đóng hộp thoại và quay về màn hình đăng nhập.
Luồng sự kiện phụ	4a. Nhân viên nhấn "Sửa Lại" sau khi kiểm tra, hệ thống cho phép nhập lại số tiền.
5a. Số tiền khớp hoàn toàn, nút "Khoá Sổ" được bật ngay, không cần nhập lý do.
5b. Số tiền lệch, hệ thống yêu cầu nhập lý do trước khi cho phép nhấn "Khoá Sổ".
Luồng sự kiện lỗi	-	Bước 4: Ô tiền để trống hoặc không hợp lệ, hệ thống báo lỗi, chưa thực hiện kiểm tra.
-	Bước 7: Lỗi kết nối, hệ thống báo lỗi, ca vẫn ở trạng thái đang mở. 

­	Sơ đồ hoạt động:

3.2.6. Quản lý sản phẩm
3.2.6.1. Thêm danh mục sản phẩm 
­	Đặc tả:
Tên Use-case	Thêm danh mục sản phẩm
Mô tả	-	Quản lý tạo mới một nhóm phân loại sản phẩm bằng cách nhập tên danh mục và nhấn "Thêm mới".
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý danh mục.
Hậu điều kiện	-	Danh mục mới được lưu và hiển thị trong danh sách.
Ô nhập tên được xóa trắng sau khi thêm thành công.
Luồng sự kiện chính	1.	Hệ thống tải và hiển thị danh sách danh mục đang hoạt động khi vào màn hình.
2.	Quản lý nhập tên danh mục vào ô "Tên danh mục".
3.	Quản lý nhấn "Thêm mới".
4.	Hệ thống kiểm tra tên không rỗng và chưa trùng với danh mục hiện có.
5.	Thiết lập công thức để tính giá vốn của sản phẩm
6.	Hệ thống lưu danh mục mới.
7.	Hệ thống thông báo thành công, xóa trắng ô nhập và làm mới danh sách.
Luồng sự kiện phụ	
Luồng sự kiện lỗi	-	Bước 4: Tên để trống hoặc đã tồn tại, hệ thống báo lỗi, giữ nguyên ô nhập.
-	Bước 5: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.2. Sửa danh mục sản phẩm 
­	Đặc tả:
Tên Use-case	Sửa danh mục sản phẩm
Mô tả	-	Quản lý chọn một danh mục trong bảng, chỉnh sửa tên và lưu thay đổi.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý danh mục.
-	Có ít nhất một danh mục trong danh sách.
Hậu điều kiện	-	Tên danh mục được cập nhật trong hệ thống.
Danh sách danh mục được làm mới sau khi lưu.
Luồng sự kiện chính	1.	Quản lý chọn hàng danh mục cần sửa trong bảng.
2.	Hệ thống điền tên danh mục hiện tại vào ô nhập và kích hoạt nút lưu.
3.	Quản lý chỉnh sửa tên và nhấn "Lưu thay đổi".
4.	Hệ thống kiểm tra tên không rỗng và chưa trùng với danh mục khác.
5.	Hệ thống cập nhật tên danh mục.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Quản lý bỏ chọn hàng, hệ thống xóa trắng ô nhập và vô hiệu hóa các nút thao tác.
Luồng sự kiện lỗi	-	Bước 4: Tên để trống hoặc trùng với danh mục khác, hệ thống báo lỗi, giữ nguyên ô nhập.
-	Bước 5: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.3. Cập nhật trạng thái danh mục sản phẩm
­	Đặc tả:
Tên Use-case	Cập nhật trạng thái danh mục sản phẩm
Mô tả	-	Quản lý khóa hoặc khôi phục danh mục sản phẩm để ẩn hoặc hiện lại trên màn hình bán hàng.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý danh mục.
-	Có ít nhất một danh mục trong hệ thống.
Hậu điều kiện	-	Trạng thái danh mục được cập nhật trong hệ thống.
-	Danh mục bị khóa ẩn khỏi màn hình bán hàng.
-	Dữ liệu sản phẩm và hóa đơn liên quan không đổi.
Luồng sự kiện chính	1.	Quản lý chọn hàng danh mục cần khóa trong bảng.
2.	Quản lý nhấn "Xóa".
3.	Hệ thống kiểm tra danh mục không còn sản phẩm đang hoạt động.
4.	Hệ thống ghi nhận thời điểm khóa và tên nhân viên thực hiện.
5.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Quản lý chuyển sang xem danh sách đã khóa, hệ thống hiển thị các danh mục bị khóa.
1b. Quản lý chọn danh mục đã khóa và nhấn "Khôi phục", hệ thống xóa trạng thái khóa và đưa danh mục về danh sách hoạt động.
Luồng sự kiện lỗi	-	Bước 3: Danh mục còn sản phẩm đang hoạt động, hệ thống báo lỗi, trạng thái không thay đổi.
-	Bước 4: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.4. Thêm sản phẩm 
­	Đặc tả:
Tên Use-case	Thêm sản phẩm
Mô tả	-	Quản lý đưa một loại bánh mới vào thực đơn bằng cách điền thông tin, chọn ảnh minh họa và lưu.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sản phẩm.
-	Có ít nhất một danh mục sản phẩm đang hoạt động.
Hậu điều kiện	-	Sản phẩm mới được lưu và hiển thị trong danh sách.
-	Hình ảnh minh họa được lưu vào hệ thống.
Luồng sự kiện chính	1.	Hệ thống hiển thị màn hình thêm sản phẩm với danh sách danh mục đã tải sẵn.
2.	Quản lý điền tên, chọn danh mục, nhập giá và các thông số thời gian bảo quản, thời gian chuẩn bị.
3.	Quản lý nhấn "Thêm mới".
4.	Hệ thống kiểm tra tính hợp lệ các trường bắt buộc.
5.	Hệ thống lưu sản phẩm mới.
6.	Hệ thống thông báo thành công và làm mới danh sách sản phẩm.
Luồng sự kiện phụ	2a. Quản lý chọn ảnh minh họa, hệ thống hiển thị ảnh xem trước và lưu ảnh vào hệ thống.
2b. Quản lý không chọn ảnh, hệ thống dùng ảnh mặc định.
3a. Quản lý nhấn "Làm mới", hệ thống xóa trắng toàn bộ ô nhập về trạng thái ban đầu.
Luồng sự kiện lỗi	-	Bước 4: Tên trống, danh mục chưa chọn hoặc giá trị số không hợp lệ, hệ thống báo lỗi, giữ nguyên.
-	Bước 5: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.5. Sửa sản phẩm 
­	Đặc tả:
Tên Use-case	Sửa sản phẩm
Mô tả	-	Quản lý chọn sản phẩm từ danh sách, chỉnh sửa thông tin và lưu thay đổi.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sản phẩm.
-	Có ít nhất một sản phẩm trong danh sách.
Hậu điều kiện	-	Thông tin sản phẩm được cập nhật trong hệ thống.
-	Danh sách sản phẩm được làm mới sau khi lưu.
Luồng sự kiện chính	1.	Quản lý chọn hàng sản phẩm cần sửa trong bảng.
2.	Hệ thống điền toàn bộ thông tin hiện tại vào các ô nhập và kích hoạt nút lưu.
3.	Quản lý chỉnh sửa các trường cần thay đổi và nhấn "Lưu thay đổi".
4.	Hệ thống kiểm tra tính hợp lệ các trường bắt buộc.
5.	Hệ thống cập nhật thông tin sản phẩm.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Quản lý bỏ chọn hàng, hệ thống xóa trắng ô nhập và vô hiệu hóa các nút thao tác.
3a. Quản lý chọn ảnh mới, hệ thống hiển thị ảnh xem trước và lưu ảnh vào hệ thống khi nhấn lưu.
3b. Quản lý không đổi ảnh, hệ thống giữ nguyên ảnh cũ của sản phẩm.
Luồng sự kiện lỗi	-	Bước 4: Tên trống, danh mục chưa chọn hoặc giá trị số không hợp lệ, hệ thống báo lỗi, giữ nguyên.
-	Bước 5: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.6. Cập nhật trạng thái sản phẩm
­	Đặc tả:
Tên Use-case	Cập nhật trạng thái sản phẩm
Mô tả	-	Quản lý khóa hoặc khôi phục sản phẩm để ẩn hoặc hiện lại trên màn hình bán hàng.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý sản phẩm.
-	Có ít nhất một sản phẩm trong hệ thống.
Hậu điều kiện	-	Trạng thái sản phẩm được cập nhật trong hệ thống.
-	Sản phẩm bị khóa ẩn khỏi màn hình bán hàng.
-	Dữ liệu hóa đơn cũ liên quan không bị ảnh hưởng.
Luồng sự kiện chính	1.	Quản lý chọn hàng sản phẩm cần khóa trong bảng.
2.	Quản lý nhấn "Khóa".
3.	Hệ thống kiểm tra sản phẩm không còn đơn hàng chưa hoàn tất.
4.	Hệ thống ghi nhận thời điểm khóa và tên nhân viên thực hiện.
5.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Quản lý chuyển sang xem danh sách đã khóa, hệ thống hiển thị các sản phẩm bị khóa.
1b. Quản lý chọn sản phẩm đã khóa và nhấn "Khôi phục", hệ thống xóa trạng thái khóa và đưa sản phẩm về danh sách hoạt động.
Luồng sự kiện lỗi	-	Bước 3: Sản phẩm còn đơn hàng chưa hoàn tất, hệ thống báo lỗi, trạng thái không thay đổi.
-	Bước 4: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.6.7. Tra cứu sản phẩm
­	Đặc tả:
Tên Use-case	Tra cứu sản phẩm
Mô tả	-	Thu ngân tìm kiếm và xem thông tin sản phẩm để tư vấn cho khách tại quầy.
Tác nhân	-	Thu ngân
Tiền điều kiện	-	Thu ngân đã đăng nhập và ca làm việc đang mở.
-	Có ít nhất một sản phẩm đang hoạt động.
Hậu điều kiện	-	Danh sách sản phẩm khớp điều kiện được hiển thị.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Hệ thống tải và hiển thị toàn bộ sản phẩm đang hoạt động khi vào màn hình bán hàng.
2.	Thu ngân gõ từ khóa vào ô tìm kiếm, hệ thống lọc và cập nhật danh sách ngay lập tức.
3.	Thu ngân xem thông tin sản phẩm để tư vấn khách.
Luồng sự kiện phụ	2a. Thu ngân chọn một danh mục, hệ thống lọc ngay danh sách theo danh mục, kết hợp từ khóa nếu có.
2b. Sản phẩm hết hàng, hệ thống đánh dấu hết hàng và vô hiệu hóa nút thêm vào giỏ.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối, hệ thống báo lỗi, danh sách hiển thị rỗng. 
-	Bước 2: Từ khóa không khớp sản phẩm nào, danh sách rỗng, không báo lỗi.

­	Sơ đồ hoạt động:

3.2.7. Quản lý công thức và sản xuất 
3.2.7.1. Thêm công thức 
­	Đặc tả:
Tên Use-case	Thêm công thức
Mô tả	-	Quản lý khởi tạo bảng định lượng nguyên liệu cho một sản phẩm chưa có công thức và lưu vào hệ thống.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý công thức.
-	Có ít nhất một sản phẩm và một nguyên liệu đang hoạt động trong hệ thống.
-	Sản phẩm được chọn chưa có công thức nào.
Hậu điều kiện	-	Công thức mới được lưu và hiển thị trong danh sách.
-	Danh sách công thức được làm mới.
Luồng sự kiện chính	1.	Quản lý chọn sản phẩm cần tạo công thức.
2.	Quản lý nhấn "Thêm dòng nguyên liệu" — hệ thống hiển thị một hàng mới trong bảng định lượng.
3.	Quản lý chọn nguyên liệu và nhập định lượng.
4.	Quản lý lặp lại thêm dòng cho đến khi đủ thành phần.
5.	Quản lý nhấn "Lưu công thức".
6.	Hệ thống kiểm tra: ít nhất 1 dòng, không trùng nguyên liệu, định lượng phải lớn hơn 0.
7.	Hệ thống lưu toàn bộ công thức.
8.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	3a. Quản lý nhấn "Xóa dòng" trên một hàng, hệ thống xóa hàng đó khỏi bảng định lượng tạm thời.
Luồng sự kiện lỗi	-	Bước 6: Bảng rỗng, trùng nguyên liệu hoặc định lượng không hợp lệ, hệ thống báo lỗi, giữ nguyên.
-	Bước 7: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi. Use-case dừng hoạt động.

­	Sơ đồ hoạt động:

3.2.7.2. Sửa công thức 
­	Đặc tả:
Tên Use-case	Sửa công thức
Mô tả	-	Quản lý chọn sản phẩm đã có công thức, điều chỉnh định lượng, thêm hoặc bớt nguyên liệu rồi lưu lại.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý công thức.
-	Sản phẩm cần sửa đã có ít nhất một dòng công thức.
Hậu điều kiện	-	Công thức được cập nhật trong hệ thống.
-	Danh sách công thức được làm mới.
Luồng sự kiện chính	1.	Quản lý chọn sản phẩm, hệ thống tải và hiển thị toàn bộ dòng công thức hiện tại vào bảng.
2.	Quản lý chỉnh sửa định lượng các dòng cần thay đổi.
3.	Quản lý nhấn "Lưu công thức".
4.	Hệ thống kiểm tra: không trùng nguyên liệu, định lượng phải lớn hơn 0, có ít nhất một dòng.
5.	Hệ thống lưu lại toàn bộ công thức.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	2a. Quản lý nhấn "Thêm dòng nguyên liệu", hệ thống thêm một hàng trống để chọn nguyên liệu mới.
2b. Quản lý nhấn "Xóa dòng" trên một hàng, hệ thống xóa hàng đó khỏi bảng tạm thời.
Luồng sự kiện lỗi	-	Bước 4: Trùng nguyên liệu, định lượng không hợp lệ hoặc bảng rỗng, hệ thống báo lỗi, giữ nguyên.
-	Bước 5: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi.

­	Sơ đồ hoạt động:

3.2.7.3. Cập nhật trạng thái công thức
­	Đặc tả:
Tên Use-case	Cập nhật trạng thái công thức
Mô tả	-	Quản lý vô hiệu hóa hoặc khôi phục công thức của một sản phẩm để ngừng hoặc tiếp tục sử dụng trong sản xuất.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền quản lý công thức.
-	Có ít nhất một công thức trong hệ thống.
Hậu điều kiện	-	Trạng thái công thức được cập nhật trong hệ thống.
-	Dữ liệu lịch sử sản xuất liên quan không bị ảnh hưởng.
Luồng sự kiện chính	1.	Quản lý chọn sản phẩm — hệ thống hiển thị trạng thái công thức hiện tại.
2.	Quản lý nhấn "Vô hiệu hóa".
3.	Hệ thống hiển thị hộp xác nhận với tên sản phẩm.
4.	Quản lý xác nhận.
5.	Hệ thống ghi nhận thời điểm vô hiệu hóa.
6.	Hệ thống thông báo thành công và làm mới danh sách.
Luồng sự kiện phụ	1a. Quản lý chuyển sang xem danh sách công thức đã vô hiệu hóa, hệ thống hiển thị danh sách đó.
1b. Quản lý chọn công thức đã vô hiệu và nhấn "Khôi phục", hệ thống đưa công thức về trạng thái hoạt động và thông báo thành công.
4a. Quản lý nhấn "Hủy”, hệ thống đóng hộp xác nhận, không thay đổi gì.
Luồng sự kiện lỗi	-	Bước 5: Lỗi lưu, hệ thống báo lỗi, trạng thái không thay đổi.

­	Sơ đồ hoạt động:

3.2.7.4. Tra cứu công thức 
­	Đặc tả:
Tên Use-case	­	Tra cứu công thức
Mô tả	-	Quản lý tìm kiếm và xem chi tiết định lượng nguyên liệu của sản phẩm để phục vụ lập kế hoạch sản xuất.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền xem công thức.
-	Có ít nhất một công thức đang hoạt động trong hệ thống.
Hậu điều kiện	-	Danh sách công thức được hiển thị theo điều kiện.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Hệ thống tải và hiển thị danh sách sản phẩm đang có công thức khi vào màn hình.
2.	Quản lý gõ từ khóa vào ô tìm kiếm, hệ thống lọc và cập nhật danh sách ngay.
3.	Quản lý chọn một sản phẩm, hệ thống hiển thị bảng chi tiết công thức của sản phẩm đó.
Luồng sự kiện phụ	3a. Quản lý nhập số lượng bánh cần làm và nhấn "Tính", hệ thống tính và hiển thị tổng nguyên liệu cần dùng tương ứng.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối, hệ thống báo lỗi, danh sách hiển thị rỗng. 
-	Bước 3a: Số lượng không hợp lệ, hệ thống báo lỗi, không cập nhật kết quả tính.

­	Sơ đồ hoạt động:

3.2.7.5. Tính toán số lượng bánh làm ra
­	Đặc tả:
Tên Use-case	Tính toán số lượng bánh làm ra
Mô tả	-	Quản lý chọn sản phẩm để hệ thống tính số lượng bánh tối đa có thể sản xuất dựa trên tồn kho nguyên liệu.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền xem tính toán.
-	Có ít nhất một sản phẩm đang hoạt động và có công thức trong hệ thống.
Hậu điều kiện	-	Số lượng bánh tối đa có thể làm được hiển thị.
-	Nguyên liệu giới hạn năng suất được đánh dấu.
-	Không có thay đổi dữ liệu nào được thực hiện.
Luồng sự kiện chính	1.	Hệ thống tải danh sách sản phẩm có công thức khi vào màn hình.
2.	Quản lý chọn sản phẩm.
3.	Hệ thống tính số lượng bánh tối đa dựa trên tồn kho và định lượng từng nguyên liệu.
4.	Hệ thống hiển thị kết quả và bảng chi tiết từng nguyên liệu, đánh dấu nguyên liệu giới hạn nhất.
Luồng sự kiện phụ	2a. Quản lý nhấn "Làm mới", hệ thống xóa kết quả và trả về trạng thái ban đầu.
4a. Một nguyên liệu hết hàng, hệ thống hiển thị số bánh tối đa là 0 và đánh dấu nguyên liệu đó.
Luồng sự kiện lỗi	-	Bước 1: Lỗi kết nối, hệ thống báo lỗi, danh sách hiển thị rỗng.
-	Bước 3: Lỗi tính toán, hệ thống báo lỗi, không hiển thị kết quả.

­	Sơ đồ hoạt động:

3.2.7.6. Cấu hình giới hạn nhận đơn
­	Đặc tả:
Tên Use-case	Cấu hình giới hạn nhận đơn
Mô tả	-	Quản lý thiết lập số lượng bánh tối đa mà bếp có thể sản xuất trong một ngày để kiểm soát việc nhận đơn.
Tác nhân	-	Quản lý
Tiền điều kiện	-	Quản lý đã đăng nhập và có quyền thiết lập hệ thống.
Hậu điều kiện	-	Cấu hình giới hạn nhận đơn được cập nhật trong hệ thống.
-	Các đơn đặt hàng mới sẽ chịu sự kiểm soát của giới hạn này.
Luồng sự kiện chính	1.	Quản lý chọn chức năng cấu hình giới hạn nhận đơn.
2.	Hệ thống hiển thị biểu mẫu cùng giới hạn hiện tại.
3.	Quản lý nhập số lượng bánh tối đa mới.
4.	Quản lý nhấn lưu cấu hình.
5.	Hệ thống kiểm tra số lượng phải là số nguyên dương hợp lệ.
6.	Hệ thống lưu cấu hình giới hạn mới.
7.	Hệ thống thông báo cập nhật thành công.
Luồng sự kiện phụ	4a. Quản lý nhấn hủy, hệ thống đóng biểu mẫu, không lưu dữ liệu.
Luồng sự kiện lỗi	-	Bước 5: Số lượng không hợp lệ, hệ thống báo lỗi, giữ nguyên biểu mẫu.
-	Bước 6: Lỗi lưu, hệ thống báo lỗi, dữ liệu không thay đổi, quá trình lưu bị hủy bỏ.
