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
