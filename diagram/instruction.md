# Đặc tả usecase và các tác nhân trong hệ thống

---

## 4.2.1. Danh sách actor

| STT | Tên Actor | Ý nghĩa |
|----|-----------|---------|
| 1 | Quản lý | Người quản lý cửa hàng, có quyền truy cập toàn bộ chức năng quản trị nội bộ, bao gồm nhân sự, sản phẩm, công thức, báo cáo và cấu hình hệ thống. |
| 2 | Thu ngân | Nhân viên tại quầy, thực hiện các nghiệp vụ bán hàng trực tiếp, bao gồm lập hóa đơn, quản lý đơn đặt, mở hoặc đóng ca và lập phiếu thu chi. |
| 3 | Thợ bếp | Nhân viên sản xuất, chịu trách nhiệm lập phiếu xuất kho nguyên liệu phục vụ quá trình làm bánh. |
| 4 | Thủ kho | Nhân viên quản lý kho, thực hiện nhập, xuất, hủy nguyên liệu, theo dõi tồn kho và quản lý thông tin nhà cung cấp. |

## 4.2.2. Danh sách Use-case

| STT | Tên Use-case | Ý nghĩa |
|----|--------------|---------|
| 1 | Đăng nhập | Nhân viên nhập tài khoản để xác thực danh tính với hệ thống và được điều hướng đến màn hình phù hợp. |
| 2 | Đăng xuất | Nhân viên kết thúc phiên làm việc, hệ thống xóa thông tin đăng nhập và quay về màn hình đăng nhập. |
| 3 | Đổi mật khẩu cá nhân | Cho phép nhân viên đang đăng nhập tự cập nhật lại mật khẩu mới để bảo vệ tài khoản. |
| 4 | Thêm nhân sự | Tạo mới hồ sơ và cấp phát tài khoản truy cập hệ thống cho nhân viên mới gia nhập. |
| 5 | Sửa nhân sự | Chỉnh sửa thông tin cá nhân, cập nhật vị trí làm việc hoặc thay đổi chức vụ của nhân viên đang hoạt động. |
| 6 | Cập nhật trạng thái nhân sự | Thao tác vô hiệu hóa hoặc kích hoạt lại tài khoản nhân viên để chặn hoặc cho phép truy cập hệ thống, nhưng vẫn giữ nguyên lịch sử hoạt động cũ. |
| 7 | Tra cứu nhân sự | Tìm kiếm và xem thông tin chi tiết của các nhân viên đang làm việc. |
| 8 | Phân quyền tài khoản | Cấp phát hoặc giới hạn quyền truy cập vào các chức năng hệ thống tùy theo vai trò. |
| 9 | Xem nhật ký hoạt động | Tra cứu lịch sử các thao tác nhạy cảm trong hệ thống như hủy hóa đơn, sửa giá, hủy phiếu thu chi, bao gồm thông tin ai thực hiện, thời điểm và nội dung thay đổi. |
| 10 | Thêm khách hàng | Ghi nhận hồ sơ thành viên mới bao gồm tên, số điện thoại liên lạc để phục vụ tích điểm. |
| 11 | Sửa khách hàng | Điều chỉnh thông tin liên lạc hoặc cập nhật thông tin cá nhân của khách hàng hiện tại. |
| 12 | Cập nhật trạng thái khách hàng | Vô hiệu hóa hồ sơ thành viên, ẩn khách hàng khỏi hệ thống tìm kiếm nhưng vẫn bảo toàn dữ liệu lịch sử mua hàng và điểm tích lũy trong quá khứ. |
| 13 | Tra cứu khách hàng | Tìm kiếm thông tin khách hàng thành viên thông qua tên hoặc số điện thoại |
| 14 | Tra cứu lịch sử mua hàng | Tìm kiếm khách hàng và xem lại danh sách các hóa đơn họ đã từng giao dịch. |
| 15 | Cấu hình hạng thành viên | Thiết lập tỷ lệ tích điểm và quy tắc tự động nâng hạng thẻ. |
| 16 | Lập hóa đơn bán lẻ | Thực hiện quy trình chọn món, tính tiền, áp dụng khuyến mãi và in hóa đơn tại quầy. |
| 17 | Hủy hóa đơn bán lẻ | Thao tác vô hiệu hóa hóa đơn đã thanh toán do thu ngân thao tác sai sót, bắt buộc nhập lý do, tự động hoàn trả số lượng bánh về tồn kho và cập nhật lại doanh thu ca để phục vụ đối soát. |
| 18 | Lập đơn đặt bánh tùy chỉnh | Tạo đơn đặt hàng riêng bao gồm việc chọn mẫu bánh gốc, thêm phụ kiện, tính thời gian chuẩn bị và thu cọc. |
| 19 | Cập nhật trạng thái đơn | Chuyển đổi trạng thái đơn hàng |
| 20 | Tra cứu danh sách đơn | Quản lý và lọc danh sách toàn bộ đơn hàng theo ngày hẹn giao hoặc trạng thái hiện tại. |
| 21 | Hủy đơn và hoàn cọc | Hủy bỏ đơn đặt hàng trước ngày giao, giải phóng năng lực sản xuất và xử lý hoàn trả tiền cọc cho khách. |
| 22 | Mở ca làm việc | Thao tác được thực hiện khi nhân viên thu ngân bắt đầu ca làm việc mới. Hệ thống ghi nhận số tiền mặt ban đầu có sẵn trong két sắt để làm cơ sở tính toán cho ca hiện tại. |
| 23 | Đóng ca và đối soát | Thao tác thực hiện khi kết thúc ca làm việc. Nhân viên kiểm đếm tiền mặt thực tế tại quầy, nhập vào hệ thống để đối chiếu với tổng doanh thu bằng tiền mặt phát sinh trong ca. Hệ thống sẽ tự động tính toán mức độ chênh lệch (nếu có) và lưu lại báo cáo đóng ca |
| 24 | Thêm danh mục sản phẩm | Tạo mới một nhóm phân loại hàng hóa. |
| 25 | Sửa danh mục sản phẩm | Đổi tên hoặc điều chỉnh thông tin mô tả của một nhóm phân loại hiện có. |
| 26 | Cập nhật trạng thái danh mục sản phẩm | Khóa các danh mục không còn kinh doanh để ẩn khỏi màn hình bán hàng, đảm bảo không làm vỡ dữ liệu của các bánh đã bán thuộc danh mục này trước đây. |
| 27 | Thêm sản phẩm | Đưa một loại bánh mới vào thực đơn với đầy đủ thông tin giá bán, hình ảnh và danh mục tương ứng. |
| 28 | Sửa sản phẩm | Thay đổi giá bán, cập nhật lại hình ảnh hoặc thông tin mô tả của sản phẩm đang kinh doanh. |
| 29 | Cập nhật trạng thái sản phẩm | Thao tác khóa hoặc mở khóa sản phẩm, giúp ẩn hoặc hiện trên màn hình bán hàng mà không làm mất dữ liệu trong chi tiết các hóa đơn cũ. |
| 30 | Tra cứu sản phẩm | Tìm kiếm và xem thông tin giá bán, hình ảnh của sản phẩm phục vụ cho việc tư vấn tại quầy. |
| 31 | Thêm công thức | Khởi tạo bảng định lượng các thành phần nguyên liệu chuẩn xác cho một loại bánh mới. |
| 32 | Sửa công thức | Điều chỉnh lại tỷ lệ, thêm hoặc bớt nguyên liệu trong một công thức chế biến đã có. |
| 33 | Cập nhật trạng thái công thức | Thao tác vô hiệu hóa bảng định lượng nguyên liệu của một loại bánh khi tiệm ngừng áp dụng, nhằm ngăn thợ bếp chế biến theo tỷ lệ cũ nhưng vẫn bảo toàn liên kết dữ liệu để tính toán chi phí sản xuất trong quá khứ. |
| 34 | Tra cứu công thức | Tìm kiếm và xem chi tiết định lượng nguyên liệu của một loại bánh cụ thể. |
| 35 | Tính toán số lượng bánh làm ra | Tính toán dựa trên nguyên liệu tồn kho |
| 36 | Cấu hình giới hạn nhận đơn | Cài đặt giới hạn số lượng bánh tối đa mà bếp có thể làm trong một ngày. |
| 37 | Thêm nguyên liệu | Ghi nhận thông tin một loại vật tư, nguyên liệu mới dùng cho sản xuất kèm theo đơn vị tính chuẩn. |
| 38 | Sửa nguyên liệu | Chỉnh sửa tên gọi, quy cách bảo quản hoặc đơn vị tính của nguyên liệu đang có. |
| 39 | Cập nhật trạng thái nguyên liệu | Vô hiệu hóa nguyên liệu không còn sử dụng để ẩn khỏi biểu mẫu lập phiếu nhập hoặc xuất kho, giữ nguyên vẹn lịch sử thẻ kho và giá trị tồn kho cũ. |
| 40 | Lập phiếu nhập kho | Ghi nhận số lượng và đơn giá nguyên liệu mới mua từ nhà cung cấp vào kho. |
| 41 | Lập phiếu xuất kho | Trừ số lượng nguyên liệu trong kho khi thợ bếp xuất đồ đi làm bánh. |
| 42 | Lập phiếu xuất hủy | Trừ hao hụt nguyên liệu do hết hạn sử dụng, hỏng hóc hoặc đổ vỡ. |
| 43 | Cảnh báo tồn kho | Hệ thống tự động báo động khi nguyên liệu rớt xuống dưới mức an toàn hoặc sắp hết hạn. |
| 44 | Tra cứu thẻ kho | Xem lại toàn bộ lịch sử biến động (nhập/xuất/tồn) của một loại nguyên liệu trong quá khứ. |
| 45 | Truy vết nguồn gốc | Dò tìm nguồn gốc lô hàng của một loại nguyên liệu đang có vấn đề để quy trách nhiệm. |
| 46 | Thêm nhà cung cấp | Khởi tạo hồ sơ đối tác cung cấp nguyên liệu mới bao gồm tên công ty và thông tin liên hệ. |
| 47 | Sửa nhà cung cấp | Thay đổi địa chỉ, số điện thoại hoặc người đại diện của đối tác cung cấp hiện tại. |
| 48 | Cập nhật trạng thái nhà cung cấp | Khóa hồ sơ đối tác khi tiệm ngừng hợp tác, đảm bảo không bị mất thông tin liên hệ trong các phiếu nhập kho đã lập trước đó. |
| 49 | Tra cứu nhà cung cấp | Tìm kiếm và xem thông tin chi tiết của các nhà cung cấp để phục vụ việc liên hệ đặt hàng hoặc truy vết sự cố nguyên liệu. |
| 50 | Báo cáo lợi nhuận | Tính lợi nhuận dựa trên giá vốn và đơn giá bán |
| 51 | Báo cáo doanh thu | Thống kê số tiền thu được theo ca, ngày, tháng, năm và biểu đồ xu hướng. |
| 52 | Báo cáo tồn kho | Tổng hợp số lượng nguyên liệu đầu kỳ, nhập, xuất và tồn cuối kỳ. |
| 53 | Thêm loại thu chi | Tạo mới một hạng mục lý do thu chi để phân loại dòng tiền minh bạch. |
| 54 | Sửa loại thu chi | Chỉnh sửa tên gọi hoặc nội dung mô tả của một hạng mục thu chi hiện tại. |
| 55 | Cập nhật trạng thái loại thu chi | Thao tác vô hiệu hóa hoặc kích hoạt lại danh mục thu chi, giúp ẩn hoặc hiện danh mục khi lập phiếu mới mà vẫn bảo toàn toàn vẹn dữ liệu lịch sử. |
| 56 | Lập phiếu thu chi | Ghi nhận các khoản tiền ra hoặc vào két sắt không phát sinh trực tiếp từ giao dịch bán bánh trên máy bán hàng. |
| 57 | Hủy phiếu thu chi | Vô hiệu hóa các phiếu thu chi bị lập sai, hệ thống tự động hoàn tác dòng tiền và tính toán lại số dư tồn quỹ thực tế. |

## 4.2.3. Đặc tả Use-case (Phần của Huy)

### 4.2.3.1. Quản lý tài khoản

#### 4.2.3.1.1. Đăng nhập

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Đăng nhập |
| **Mô tả** | Nhân viên nhập tài khoản để xác thực danh tính với hệ thống và được điều hướng đến màn hình phù hợp. |
| **Tác nhân** | Quản lý, Thu ngân, Thợ bếp, Thủ kho |
| **Tiền điều kiện** | Ứng dụng đang chạy, màn hình đăng nhập đang hiện. Nhân viên có tài khoản còn đang hoạt động. |
| **Hậu điều kiện** | Phiên làm việc của nhân viên được lưu vào bộ nhớ trong của ứng dụng. Hệ thống chuyển đến màn hình phù hợp với vai trò. |
| **Luồng sự kiện chính** | 1. Nhân viên nhập tên đăng nhập và mật khẩu.<br>2. Nhân viên nhấn nút "Đăng Nhập".<br>3. Hệ thống kiểm tra hai trường không được bỏ trống.<br>4. Hệ thống xác thực tài khoản với cơ sở dữ liệu.<br>5. Hệ thống lưu thông tin nhân viên vào phiên làm việc hiện tại.<br>6. Hệ thống kiểm tra vai trò và trạng thái ca làm việc của nhân viên.<br>7. Hệ thống chuyển nhân viên đến màn hình phù hợp. |
| **Luồng sự kiện phụ** | 1a. Nhân viên nhấn "Hiện" để xem mật khẩu dạng chữ thường, nhấn "Ẩn" để che lại.<br>6a. Nhân viên là thu ngân và chưa có ca mở, hệ thống chuyển sang màn hình mở ca làm việc.<br>6b. Nhân viên là quản lý, hệ thống chuyển thẳng sang màn hình làm việc chính, không qua mở ca. |
| **Luồng sự kiện lỗi** | Bước 3: Tên đăng nhập hoặc mật khẩu để trống — hệ thống báo lỗi ngay tại ô tương ứng.<br>Bước 4: Thông tin sai, tài khoản nghỉ việc hoặc lỗi kết nối, hệ thống báo lỗi tương ứng và dừng. |

### 4.2.3.1.2. Đăng xuất

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Đăng xuất |
| **Mô tả** | Nhân viên kết thúc phiên làm việc, hệ thống xoá thông tin đăng nhập và quay về màn hình đăng nhập. |
| **Tác nhân** | Thu ngân, Quản lý, Thợ bếp, Thủ kho |
| **Tiền điều kiện** | Nhân viên đã đăng nhập thành công vào hệ thống. Màn hình làm việc chính đang hiển thị. |
| **Hậu điều kiện** | Phiên làm việc bị xoá khỏi bộ nhớ ứng dụng. Hệ thống quay về màn hình đăng nhập. |
| **Luồng sự kiện chính** | 1. Nhân viên nhấn nút "Đăng xuất" trên thanh điều hướng bên trái màn hình chính.<br>2. Hệ thống xoá toàn bộ thông tin phiên làm việc đang lưu trong bộ nhớ ứng dụng.<br>3. Hệ thống chuyển về màn hình đăng nhập. |
| **Luồng sự kiện phụ** | 1a. Nhân viên nhấn "Đăng xuất" từ màn hình mở ca khi chưa bắt đầu ca, hệ thống cũng xoá phiên và về màn hình đăng nhập như bình thường. |
| **Luồng sự kiện lỗi** | Thao tác đăng xuất không tương tác với cơ sở dữ liệu nên không phát sinh lỗi kỹ thuật. Nếu nhân viên là Thu ngân còn ca đang mở, ca đó vẫn ở trạng thái "Đang mở" cho đến khi được đóng đúng quy trình qua màn hình Đối Soát và Đóng Ca. |

### 4.2.3.1.3. Đổi mật khẩu cá nhân

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Đổi mật khẩu cá nhân |
| **Mô tả** | Cho phép nhân viên đang làm việc trên hệ thống tự cập nhật lại mật khẩu mới để bảo vệ tài khoản. |
| **Tác nhân** | Quản lý, Thu ngân, Thợ bếp, Thủ kho |
| **Tiền điều kiện** | Nhân viên đang trong phiên đăng nhập hợp lệ. |
| **Hậu điều kiện** | Mật khẩu mới được cập nhật vào hệ thống. |
| **Luồng sự kiện chính** | 1. Nhân viên chọn chức năng đổi mật khẩu cá nhân.<br>2. Hệ thống hiển thị biểu mẫu đổi mật khẩu.<br>3. Nhân viên nhập mật khẩu hiện tại, mật khẩu mới và xác nhận lại mật khẩu mới.<br>4. Nhân viên nhấn nút xác nhận thay đổi.<br>5. Hệ thống kiểm tra mật khẩu hiện tại chính xác và hai lần nhập mật khẩu mới trùng khớp.<br>6. Hệ thống lưu mật khẩu mới.<br>7. Hệ thống thông báo thành công và đóng biểu mẫu. |
| **Luồng sự kiện phụ** | 4a. Nhân viên nhấn hủy, hệ thống đóng biểu mẫu, không thực hiện thay đổi. |
| **Luồng sự kiện lỗi** | Bước 5: Sai mật khẩu cũ hoặc mật khẩu mới không khớp, hệ thống báo lỗi, giữ nguyên biểu mẫu.<br>Bước 6: Lỗi lưu, hệ thống báo lỗi, mật khẩu không đổi, quá trình lưu bị hủy bỏ. |

## 4.2.3.2. Quản lý nhân sự

### 4.2.3.2.1. Thêm nhân sự

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Thêm nhân sự |
| **Mô tả** | Quản lý tạo mới hồ sơ nhân viên và cấp tài khoản đăng nhập cho người mới gia nhập tiệm bánh. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và đang ở màn hình Nhân sự. Hệ thống có ít nhất một vai trò đang hoạt động. |
| **Hậu điều kiện** | Hồ sơ nhân viên mới được lưu vào cơ sở dữ liệu với mã nhân viên do hệ thống tự cấp. Tài khoản đăng nhập được kích hoạt ngay lập tức. Danh sách nhân viên trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Quản lý nhấn "Thêm mới", hệ thống xóa trắng biểu mẫu và đặt con trỏ vào ô Họ tên.<br>2. Quản lý điền họ tên, số điện thoại, tên đăng nhập, mật khẩu và chọn vai trò từ danh sách.<br>3. Quản lý nhấn "Lưu".<br>4. Hệ thống kiểm tra các trường bắt buộc và định dạng dữ liệu đầu vào.<br>5. Hệ thống mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu.<br>6. Hệ thống kiểm tra trùng số điện thoại và tên đăng nhập trong cơ sở dữ liệu.<br>7. Hệ thống lưu hồ sơ và nhận lại mã nhân viên mới.<br>8. Hệ thống làm mới danh sách, xóa trắng biểu mẫu và thông báo thêm thành công. |
| **Luồng sự kiện phụ** | 2a. Quản lý không nhập ngày sinh, hệ thống cho phép bỏ trống, lưu giá trị rỗng vào cơ sở dữ liệu.<br>2b. Quản lý tích "Hoạt động" hoặc bỏ tích, hệ thống lưu trạng thái tài khoản tương ứng ngay khi tạo. |
| **Luồng sự kiện lỗi** | Bước 4: Thiếu trường bắt buộc hoặc dữ liệu không hợp lệ, hệ thống báo lỗi tại ô tương ứng.<br>Bước 6: Số điện thoại hoặc tên đăng nhập đã tồn tại, hệ thống báo lỗi và yêu cầu nhập lại. |

### 4.2.3.2.2. Sửa nhân sự

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Sửa nhân sự |
| **Mô tả** | Quản lý chỉnh sửa thông tin cá nhân, vai trò hoặc mật khẩu của nhân viên đang có trong hệ thống. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và đang ở màn hình Nhân sự. Nhân viên cần sửa đã có trong danh sách. |
| **Hậu điều kiện** | Thông tin nhân viên được cập nhật trong cơ sở dữ liệu, các trường bỏ trống giữ nguyên giá trị cũ. Danh sách nhân viên trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Quản lý chọn nhân viên từ danh sách, hệ thống điền thông tin hiện tại của nhân viên lên biểu mẫu.<br>2. Quản lý chỉnh sửa các trường cần thay đổi.<br>3. Quản lý nhấn "Lưu".<br>4. Hệ thống kiểm tra các trường bắt buộc và định dạng dữ liệu đầu vào.<br>5. Nếu ô mật khẩu được điền, hệ thống mã hóa mật khẩu mới trước khi gửi xuống cơ sở dữ liệu.<br>6. Hệ thống kiểm tra trùng số điện thoại và tên đăng nhập với các nhân viên khác.<br>7. Hệ thống cập nhật thông tin nhân viên vào cơ sở dữ liệu.<br>8. Hệ thống làm mới danh sách, xóa trắng biểu mẫu và thông báo cập nhật thành công. |
| **Luồng sự kiện phụ** | 2a. Quản lý để trống ô mật khẩu, hệ thống giữ nguyên mật khẩu cũ, không yêu cầu nhập lại.<br>2b. Quản lý thay đổi vai trò, hệ thống ghi nhận và cập nhật vai trò mới khi lưu. |
| **Luồng sự kiện lỗi** | Bước 4: Thiếu trường bắt buộc hoặc dữ liệu không hợp lệ, hệ thống báo lỗi tại ô tương ứng.<br>Bước 6: Số điện thoại hoặc tên đăng nhập trùng với nhân viên khác, hệ thống báo lỗi và yêu cầu sửa. |

### 4.2.3.2.3. Cập nhật trạng thái nhân sự

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Cập nhật trạng thái nhân sự |
| **Mô tả** | Quản lý vô hiệu hóa hoặc kích hoạt lại tài khoản nhân viên để chặn hoặc cho phép truy cập hệ thống. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và đang ở màn hình nhân sự. Nhân viên cần thay đổi trạng thái có trong danh sách. |
| **Hậu điều kiện** | Trạng thái tài khoản nhân viên được cập nhật trong cơ sở dữ liệu, lịch sử hoạt động giữ nguyên. Danh sách nhân viên trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Quản lý chọn nhân viên từ danh sách, hệ thống điền thông tin hiện tại của nhân viên lên biểu mẫu.<br>2. Quản lý nhấn "Vô hiệu hóa".<br>3. Hệ thống hiện hộp xác nhận với tên nhân viên.<br>4. Quản lý xác nhận "Có".<br>5. Hệ thống cập nhật trạng thái nhân viên về "Ngừng việc" và ghi vào cơ sở dữ liệu.<br>6. Hệ thống làm mới danh sách và thông báo đã vô hiệu hóa tài khoản. |
| **Luồng sự kiện phụ** | 1a. Kích hoạt lại tài khoản, quản lý chọn nhân viên đang ngừng việc, tích vào ô "Hoạt động" trên ô trống rồi nhấn "Lưu". Hệ thống cập nhật trạng thái về "Đang làm việc" qua luồng sửa nhân sự.<br>4a. Quản lý chọn "Không" ở hộp xác nhận — hệ thống đóng hộp thoại, không thay đổi gì. |
| **Luồng sự kiện lỗi** | Bước 2: Chưa chọn nhân viên mà nhấn "Vô hiệu hóa" — hệ thống báo "Vui lòng chọn nhân viên để vô hiệu hóa."<br>Bước 5: Lỗi cơ sở dữ liệu khi cập nhật — hệ thống báo lỗi và giữ nguyên trạng thái cũ. |

### 4.2.3.2.4. Tra cứu nhân sự

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Tra cứu nhân sự |
| **Mô tả** | Quản lý tìm kiếm và xem thông tin chi tiết của nhân viên thông qua từ khóa hoặc bộ lọc trạng thái. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và đang ở màn hình Nhân sự. |
| **Hậu điều kiện** | Danh sách nhân viên khớp điều kiện tìm kiếm được hiển thị trên bảng. Không có thay đổi dữ liệu nào được thực hiện. |
| **Luồng sự kiện chính** | 1. Hệ thống tải toàn bộ danh sách nhân viên khi vào màn hình, sắp xếp theo vai trò rồi theo mã nhân viên.<br>2. Quản lý gõ từ khóa vào ô tìm kiếm theo họ tên, số điện thoại hoặc tên đăng nhập.<br>3. Hệ thống lọc danh sách ngay khi có ký tự thay đổi, không cần nhấn nút.<br>4. Quản lý chọn một nhân viên từ danh sách kết quả.<br>5. Hệ thống hiển thị đầy đủ thông tin nhân viên đó lên biểu mẫu bên cạnh. |
| **Luồng sự kiện phụ** | 2a. Quản lý chọn bộ lọc trạng thái, hệ thống kết hợp lọc đồng thời cả từ khóa lẫn trạng thái.<br>2b. Quản lý để trống ô tìm kiếm và chọn "Tất cả trạng thái", hệ thống hiển thị lại toàn bộ danh sách. |
| **Luồng sự kiện lỗi** | Bước 1: Lỗi kết nối cơ sở dữ liệu khi tải dữ liệu, hệ thống hiển thị thông báo lỗi trên thanh trạng thái, bảng hiển thị rỗng.<br>Bước 3: Không có nhân viên nào khớp từ khóa, bảng hiển thị rỗng, không có thông báo lỗi. |

### 4.2.3.2.5. Phân quyền tài khoản

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Phân quyền tài khoản |
| **Mô tả** | Quản lý cấp phát hoặc giới hạn quyền truy cập vào các chức năng hệ thống cho nhân viên tùy theo vai trò. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và có quyền thiết lập hệ thống. Tài khoản nhân viên đang tồn tại. |
| **Hậu điều kiện** | Quyền truy cập của nhân viên được cập nhật. Không làm ảnh hưởng đến dữ liệu hoạt động cũ. |
| **Luồng sự kiện chính** | 1. Quản lý chọn chức năng phân quyền tài khoản.<br>2. Hệ thống hiển thị danh sách các tài khoản nhân viên.<br>3. Quản lý chọn một tài khoản cần phân quyền.<br>4. Hệ thống hiển thị danh sách các quyền truy cập.<br>5. Quản lý chọn cấp phát hoặc thu hồi quyền tương ứng.<br>6. Quản lý nhấn lưu thay đổi.<br>7. Hệ thống cập nhật quyền hạn cho tài khoản đó.<br>8. Hệ thống thông báo thành công và làm mới màn hình. |
| **Luồng sự kiện phụ** | 4a. Tài khoản đang bị vô hiệu hóa, hệ thống hiển thị cảnh báo và không cho phép thay đổi quyền.<br>5a. Quản lý không thay đổi quyền và nhấn lưu, hệ thống vẫn tiến hành cập nhật dữ liệu. |
| **Luồng sự kiện lỗi** | Bước 7: Lỗi lưu, hệ thống báo lỗi, quyền không thay đổi, quá trình lưu bị hủy bỏ. |

### 4.2.3.2.6. Xem nhật ký hoạt động

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Xem nhật ký hoạt động |
| **Mô tả** | Quản lý tra cứu lịch sử các thao tác nhạy cảm như hủy đơn, sửa giá để kiểm soát rủi ro hệ thống. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và có quyền xem nhật ký hoạt động của hệ thống. |
| **Hậu điều kiện** | Lịch sử nhật ký hoạt động được hiển thị chính xác. Không có dữ liệu nào bị thay đổi. |
| **Luồng sự kiện chính** | 1. Quản lý chọn chức năng xem nhật ký hoạt động.<br>2. Quản lý chọn loại thao tác và khoảng thời gian cần tra cứu.<br>3. Quản lý yêu cầu hệ thống tìm kiếm.<br>4. Hệ thống truy xuất dữ liệu từ nhật ký hoạt động.<br>5. Hệ thống hiển thị danh sách các thao tác bao gồm người thực hiện, thời gian và tóm tắt nội dung.<br>6. Quản lý chọn xem chi tiết một bản ghi để kiểm tra. |
| **Luồng sự kiện phụ** | 2a. Quản lý không chọn khoảng thời gian, hệ thống mặc định lấy nhật ký của ngày hiện tại.<br>5a. Không có dữ liệu phù hợp, hệ thống hiển thị danh sách rỗng và thông báo trống. |
| **Luồng sự kiện lỗi** | Bước 4: Lỗi truy xuất dữ liệu, hệ thống báo lỗi, không hiển thị được danh sách nhật ký. |

## 4.2.3.3. Quản lý khách hàng

### 4.2.3.3.1. Thêm khách hàng

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Thêm khách hàng |
| **Mô tả** | Nhân viên ghi nhận hồ sơ thành viên mới với tên và số điện thoại để phục vụ tích điểm về sau. |
| **Tác nhân** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và đang ở màn hình Khách hàng. |
| **Hậu điều kiện** | Hồ sơ khách hàng mới được lưu vào cơ sở dữ liệu với ngày đăng ký là hôm nay và điểm tích lũy = 0. Danh sách khách hàng trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Nhân viên nhấn "Thêm khách hàng", hệ thống mở hộp thoại nhập liệu với các trường nhập liệu.<br>2. Nhân viên điền họ tên, số điện thoại và địa chỉ và nhấn “Lưu”.<br>3. Hệ thống kiểm tra định dạng và độ dài dữ liệu đầu vào.<br>4. Hệ thống tự gán ngày đăng ký là hôm nay và điểm tích lũy ban đầu bằng 0.<br>5. Hệ thống lưu hồ sơ khách hàng vào cơ sở dữ liệu trên luồng nền, không làm đơ giao diện.<br>6. Hệ thống thông báo "Đã thêm khách hàng" và làm mới danh sách. |
| **Luồng sự kiện phụ** | 2a. Nhân viên bỏ trống địa chỉ, hệ thống cho phép lưu với địa chỉ rỗng, không báo lỗi.<br>2b. Nhân viên nhấn "Hủy", hệ thống đóng hộp thoại, không lưu gì. |
| **Luồng sự kiện lỗi** | Bước 3: Họ tên để trống hoặc dữ liệu không đúng định dạng, hệ thống báo lỗi tại ô tương ứng.<br>Bước 5: Lỗi cơ sở dữ liệu, hệ thống thông báo lỗi, không lưu dữ liệu. |

### 4.2.3.3.2. Sửa khách hàng

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Sửa khách hàng |
| **Mô tả** | Nhân viên điều chỉnh thông tin liên lạc hoặc cập nhật thông tin cá nhân của khách hàng hiện tại. |
| **Tác nhân** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và đang ở màn hình khách hàng. Khách hàng cần sửa đang hiển thị trong danh sách. |
| **Hậu điều kiện** | Thông tin khách hàng được cập nhật trong cơ sở dữ liệu, điểm tích lũy và hạng thành viên giữ nguyên. Danh sách khách hàng trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Nhân viên nhấn "Sửa" trên dòng khách hàng cần chỉnh sửa trong bảng.<br>2. Hệ thống tải thông tin hiện tại của khách hàng từ cơ sở dữ liệu trên luồng nền.<br>3. Hệ thống mở hộp thoại "Cập nhật khách hàng" với các trường đã điền sẵn: Họ tên, SĐT, Địa chỉ.<br>4. Nhân viên chỉnh sửa các trường cần thay đổi rồi nhấn "OK".<br>5. Hệ thống kiểm tra định dạng và độ dài dữ liệu đầu vào.<br>6. Hệ thống cập nhật thông tin khách hàng vào cơ sở dữ liệu trên luồng nền.<br>7. Hệ thống thông báo "Đã cập nhật khách hàng" và làm mới danh sách. |
| **Luồng sự kiện phụ** | 4a. Nhân viên nhấn "Hủy", hệ thống đóng hộp thoại, không lưu thay đổi nào.<br>4b. Nhân viên xóa trắng địa chỉ, hệ thống cho phép lưu với địa chỉ rỗng, không báo lỗi. |
| **Luồng sự kiện lỗi** | Bước 5: Họ tên để trống hoặc dữ liệu không đúng định dạng, hệ thống báo lỗi tại ô tương ứng.<br>Bước 6: Lỗi cơ sở dữ liệu, hệ thống thông báo lỗi, dữ liệu không thay đổi. |

### 4.2.3.3.3. Cập nhật trạng thái khách hàng

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Cập nhật trạng thái khách hàng |
| **Mô tả** | Nhân viên vô hiệu hóa hoặc khôi phục hồ sơ khách hàng, dữ liệu lịch sử mua hàng và điểm giữ nguyên. |
| **Tác nhân** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và đang ở màn hình Khách hàng. Khách hàng cần thay đổi trạng thái có trong danh sách. |
| **Hậu điều kiện** | Trạng thái khách hàng được cập nhật trong cơ sở dữ liệu, điểm và lịch sử giao dịch giữ nguyên. Danh sách khách hàng trên màn hình được làm mới. |
| **Luồng sự kiện chính** | 1. Nhân viên nhấn "Xóa" trên dòng khách hàng cần vô hiệu hóa trong bảng.<br>2. Hệ thống hiển thị hộp xác nhận với tên khách hàng.<br>3. Nhân viên xác nhận "OK".<br>4. Hệ thống ghi nhận thời điểm xóa và mã nhân viên thực hiện vào cơ sở dữ liệu trên luồng nền.<br>5. Hệ thống thông báo "Đã xóa khách hàng" và ẩn khách hàng khỏi danh sách chính. |
| **Luồng sự kiện phụ** | 3a. Nhân viên nhấn "Hủy" ở hộp xác nhận, hệ thống đóng hộp thoại, không thay đổi gì.<br>1b. Khôi phục khách hàng, nhân viên nhấn nút "Thùng rác" để chuyển sang chế độ xem danh sách đã xóa. Nhân viên nhấn "Khôi phục" trên dòng khách hàng cần kích hoạt lại. Hệ thống xóa thời điểm xóa, khôi phục khách hàng về danh sách chính và thông báo thành công. |
| **Luồng sự kiện lỗi** | Bước 4: Lỗi cơ sở dữ liệu khi vô hiệu hóa, hệ thống hiển thị thông báo lỗi, trạng thái không thay đổi.<br>Luồng phụ 1b: Lỗi cơ sở dữ liệu khi khôi phục, hệ thống hiển thị thông báo lỗi, khách hàng vẫn nằm trong thùng rác. |

### 4.2.3.3.4. Tra cứu khách hàng

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Tra cứu khách hàng |
| **Mô tả** | Nhân viên tìm kiếm thông tin khách hàng thành viên thông qua từ khóa hoặc bộ lọc ngày đăng ký và hạng. |
| **Tác nhân** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và đang ở màn hình khách hàng. |
| **Hậu điều kiện** | Danh sách khách hàng khớp điều kiện được hiển thị theo trang. Không có thay đổi dữ liệu nào được thực hiện. |
| **Luồng sự kiện chính** | 1. Hệ thống tải toàn bộ danh sách khách hàng đang hoạt động khi vào màn hình.<br>2. Nhân viên gõ từ khóa vào ô tìm kiếm theo họ tên, số điện thoại, địa chỉ hoặc mã khách hàng.<br>3. Hệ thống gửi truy vấn lên cơ sở dữ liệu và cập nhật danh sách ngay khi có ký tự thay đổi.<br>4. Nhân viên điều hướng qua các trang bằng thanh phân trang ở cuối danh sách. |
| **Luồng sự kiện phụ** | 2a. Nhân viên nhấn "Bộ lọc" để mở bảng lọc nâng cao, hệ thống hiển thị thêm các ô: Từ ngày, Đến ngày và Hạng thành viên.<br>2b. Nhân viên chọn khoảng ngày đăng ký hoặc hạng thành viên rồi nhấn "Áp dụng", hệ thống lọc kết hợp cả từ khóa lẫn điều kiện nâng cao ngay trên dữ liệu đang hiển thị.<br>2c. Nhân viên nhấn "Xóa bộ lọc", hệ thống đặt lại toàn bộ điều kiện lọc và hiển thị đầy đủ danh sách. |
| **Luồng sự kiện lỗi** | Bước 1: Lỗi kết nối cơ sở dữ liệu, hệ thống hiển thị thông báo lỗi, bảng hiển thị rỗng.<br>Bước 3: Không có khách hàng khớp từ khóa, bảng hiển thị rỗng. |

### 4.2.3.3.5. Tra cứu lịch sử mua hàng

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Tra cứu lịch sử mua hàng |
| **Mô tả** | Nhân viên tra cứu danh sách các đơn hàng cũ của một khách hàng cụ thể để hỗ trợ bảo hành hoặc tư vấn. |
| **Tác nhân** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và có quyền truy cập thông tin khách hàng. Đã chọn một khách hàng cụ thể trên hệ thống. |
| **Hậu điều kiện** | Lịch sử mua hàng của khách hàng được hiển thị. Không có dữ liệu nào bị thay đổi. |
| **Luồng sự kiện chính** | 1. Nhân viên chọn chức năng xem lịch sử mua hàng từ hồ sơ khách hàng.<br>2. Hệ thống tìm kiếm các hóa đơn và đơn đặt hàng từng giao dịch của khách hàng đó.<br>3. Hệ thống hiển thị danh sách lịch sử mua hàng.<br>4. Nhân viên chọn xem chi tiết một đơn hàng cụ thể.<br>5. Hệ thống hiển thị thông tin sản phẩm, số lượng, tổng tiền và trạng thái đơn hàng. |
| **Luồng sự kiện phụ** | 3a. Khách hàng chưa từng mua hàng, hệ thống hiển thị thông báo chưa có lịch sử giao dịch. |
| **Luồng sự kiện lỗi** | Bước 2: Lỗi truy xuất dữ liệu, hệ thống báo lỗi, không hiển thị được lịch sử mua hàng. |

### 4.2.3.3.6. Cấu hình hạng thành viên

| Thuộc tính | Nội dung |
|------------|----------|
| **Tên Use-case** | Cấu hình hạng thành viên |
| **Mô tả** | Quản lý thiết lập ngưỡng điểm tối thiểu và tỷ lệ giảm giá cho từng hạng thành viên hiện có. |
| **Tác nhân** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập và đang ở màn hình Hạng thành viên. Có ít nhất một hạng thành viên trong hệ thống. |
| **Hậu điều kiện** | Ngưỡng điểm và tỷ lệ giảm giá của hạng được cập nhật trong cơ sở dữ liệu. Danh sách hạng thành viên được làm mới. |
| **Luồng sự kiện chính** | 1. Hệ thống tải và hiển thị danh sách hạng thành viên sắp xếp theo điểm tối thiểu tăng dần.<br>2. Quản lý nhấn "Sửa" trên hạng cần điều chỉnh.<br>3. Hệ thống mở hộp thoại với tên hạng hiển thị sẵn, điểm tối thiểu và tỷ lệ giảm giá có thể sửa.<br>4. Quản lý chỉnh sửa điểm tối thiểu và/hoặc tỷ lệ giảm giá rồi nhấn "Lưu".<br>5. Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào.<br>6. Hệ thống cập nhật thông tin hạng vào cơ sở dữ liệu trên luồng nền.<br>7. Hệ thống thông báo cập nhật thành công và làm mới danh sách. |
| **Luồng sự kiện phụ** | 4a. Quản lý nhấn "Hủy", hệ thống đóng hộp thoại, không lưu thay đổi nào. |
| **Luồng sự kiện lỗi** | Bước 4-5: Dữ liệu nhập vào không hợp lệ, hệ thống báo lỗi tại ô tương ứng.<br>Bước 6: Lỗi cơ sở dữ liệu, hệ thống thông báo lỗi, dữ liệu không thay đổi. |

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


 
