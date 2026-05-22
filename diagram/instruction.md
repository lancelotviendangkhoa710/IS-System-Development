
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


