# Danh Sách Use-Case Hệ Thống Quản Lý Tiệm Bánh (Đã Tinh Gọn)

> Khi code Presenter và Service, phải đảm bảo cover đủ các use-case liên quan đến màn hình đó.
> Đây là danh sách use-case chính thức đã được xác nhận cuối cùng.

---

## I. Phân hệ Quản trị & Nhân sự

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC01 | Đăng nhập | Nhân viên nhập tài khoản để xác thực danh tính với hệ thống và được điều hướng đến màn hình phù hợp. |
| UC02 | Đăng xuất | Nhân viên kết thúc phiên làm việc, hệ thống xóa thông tin đăng nhập và quay về màn hình đăng nhập. |
| UC03 | Đổi mật khẩu cá nhân | Cho phép nhân viên đang đăng nhập tự cập nhật lại mật khẩu mới để bảo vệ tài khoản. |
| UC04 | Thêm nhân sự | Tạo mới hồ sơ và cấp phát tài khoản truy cập hệ thống cho nhân viên mới gia nhập. |
| UC05 | Sửa nhân sự | Chỉnh sửa thông tin cá nhân, cập nhật vị trí làm việc hoặc thay đổi chức vụ của nhân viên đang hoạt động. |
| UC06 | Cập nhật trạng thái nhân sự | Thao tác vô hiệu hóa hoặc kích hoạt lại tài khoản nhân viên để chặn hoặc cho phép truy cập hệ thống, nhưng vẫn giữ nguyên lịch sử hoạt động cũ. |
| UC07 | Tra cứu nhân sự | Tìm kiếm và xem thông tin chi tiết của các nhân viên đang làm việc. |
| UC08 | Phân quyền tài khoản | Cấp phát hoặc giới hạn quyền truy cập vào các chức năng hệ thống tùy theo vai trò. |
| UC09 | Xem nhật ký hoạt động | Tra cứu lịch sử các thao tác nhạy cảm trong hệ thống như hủy hóa đơn, sửa giá, hủy phiếu thu chi, bao gồm thông tin ai thực hiện, thời điểm và nội dung thay đổi. |

---

## II. Phân hệ Quản lý Khách hàng

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC10 | Thêm khách hàng | Ghi nhận hồ sơ thành viên mới bao gồm tên, số điện thoại liên lạc để phục vụ tích điểm. |
| UC11 | Sửa khách hàng | Điều chỉnh thông tin liên lạc hoặc cập nhật thông tin cá nhân của khách hàng hiện tại. |
| UC12 | Cập nhật trạng thái khách hàng | Vô hiệu hóa hồ sơ thành viên, ẩn khách hàng khỏi hệ thống tìm kiếm nhưng vẫn bảo toàn dữ liệu lịch sử mua hàng và điểm tích lũy trong quá khứ. |
| UC13 | Tra cứu khách hàng | Tìm kiếm thông tin khách hàng thành viên thông qua tên hoặc số điện thoại. |
| UC14 | Tra cứu lịch sử mua hàng | Tìm kiếm khách hàng và xem lại danh sách các hóa đơn họ đã từng giao dịch. |
| UC15 | Cấu hình hạng thành viên | Thiết lập tỷ lệ tích điểm và quy tắc tự động nâng hạng thẻ. |

---

## III. Phân hệ Bán hàng & Đơn hàng

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC16 | Lập hóa đơn bán lẻ | Thực hiện quy trình chọn món, tính tiền, áp dụng khuyến mãi và in hóa đơn tại quầy. |
| UC17 | Hủy hóa đơn bán lẻ | Thao tác vô hiệu hóa hóa đơn đã thanh toán do thu ngân thao tác sai sót, bắt buộc nhập lý do, tự động hoàn trả số lượng bánh về tồn kho và cập nhật lại doanh thu ca để phục vụ đối soát. |
| UC18 | Lập đơn đặt bánh tùy chỉnh | Tạo đơn đặt hàng riêng bao gồm việc chọn mẫu bánh gốc, thêm phụ kiện, tính thời gian chuẩn bị và thu cọc. |
| UC19 | Cập nhật trạng thái đơn | Chuyển đổi trạng thái đơn hàng. |
| UC20 | Tra cứu danh sách đơn | Quản lý và lọc danh sách toàn bộ đơn hàng theo ngày hẹn giao hoặc trạng thái hiện tại. |
| UC21 | Hủy đơn và hoàn cọc | Hủy bỏ đơn đặt hàng trước ngày giao, giải phóng năng lực sản xuất và xử lý hoàn trả tiền cọc cho khách. |
| UC22 | Mở ca làm việc | Thao tác được thực hiện khi nhân viên thu ngân bắt đầu ca làm việc mới. Hệ thống ghi nhận số tiền mặt ban đầu có sẵn trong két sắt để làm cơ sở tính toán cho ca hiện tại. |
| UC23 | Đóng ca và đối soát | Thao tác thực hiện khi kết thúc ca làm việc. Nhân viên kiểm đếm tiền mặt thực tế tại quầy, nhập vào hệ thống để đối chiếu với tổng doanh thu bằng tiền mặt phát sinh trong ca. Hệ thống sẽ tự động tính toán mức độ chênh lệch (nếu có) và lưu lại báo cáo đóng ca. |

---

## IV. Phân hệ Quản lý Danh mục

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC24 | Thêm danh mục sản phẩm | Tạo mới một nhóm phân loại hàng hóa. |
| UC25 | Sửa danh mục sản phẩm | Đổi tên hoặc điều chỉnh thông tin mô tả của một nhóm phân loại hiện có. |
| UC26 | Cập nhật trạng thái danh mục sản phẩm | Khóa các danh mục không còn kinh doanh để ẩn khỏi màn hình bán hàng, đảm bảo không làm vỡ dữ liệu của các bánh đã bán thuộc danh mục này trước đây. |
| UC27 | Thêm sản phẩm | Đưa một loại bánh mới vào thực đơn với đầy đủ thông tin giá bán, hình ảnh và danh mục tương ứng. |
| UC28 | Sửa sản phẩm | Thay đổi giá bán, cập nhật lại hình ảnh hoặc thông tin mô tả của sản phẩm đang kinh doanh. |
| UC29 | Cập nhật trạng thái sản phẩm | Thao tác khóa hoặc mở khóa sản phẩm, giúp ẩn hoặc hiện trên màn hình bán hàng mà không làm mất dữ liệu trong chi tiết các hóa đơn cũ. |
| UC30 | Tra cứu sản phẩm | Tìm kiếm và xem thông tin giá bán, hình ảnh của sản phẩm phục vụ cho việc tư vấn tại quầy. |
| UC31 | Thêm công thức | Khởi tạo bảng định lượng các thành phần nguyên liệu chuẩn xác cho một loại bánh mới. |
| UC32 | Sửa công thức | Điều chỉnh lại tỷ lệ, thêm hoặc bớt nguyên liệu trong một công thức chế biến đã có. |
| UC33 | Cập nhật trạng thái công thức | Thao tác vô hiệu hóa bảng định lượng nguyên liệu của một loại bánh khi tiệm ngừng áp dụng, nhằm ngăn thợ bếp chế biến theo tỷ lệ cũ nhưng vẫn bảo toàn liên kết dữ liệu để tính toán chi phí sản xuất trong quá khứ. |
| UC34 | Tra cứu công thức | Tìm kiếm và xem chi tiết định lượng nguyên liệu của một loại bánh cụ thể. |
| UC35 | Tính toán số lượng bánh làm ra | Tính toán dựa trên nguyên liệu tồn kho. |
| UC36 | Cấu hình giới hạn nhận đơn | Cài đặt giới hạn số lượng bánh tối đa mà bếp có thể làm trong một ngày. |
| UC37 | Thêm nguyên liệu | Ghi nhận thông tin một loại vật tư, nguyên liệu mới dùng cho sản xuất kèm theo đơn vị tính chuẩn. |
| UC38 | Sửa nguyên liệu | Chỉnh sửa tên gọi, quy cách bảo quản hoặc đơn vị tính của nguyên liệu đang có. |
| UC39 | Cập nhật trạng thái nguyên liệu | Vô hiệu hóa nguyên liệu không còn sử dụng để ẩn khỏi biểu mẫu lập phiếu nhập hoặc xuất kho, giữ nguyên vẹn lịch sử thẻ kho và giá trị tồn kho cũ. |

---

## V. Phân hệ Quản lý Kho

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC40 | Lập phiếu nhập kho | Ghi nhận số lượng và đơn giá nguyên liệu mới mua từ nhà cung cấp vào kho. |
| UC41 | Lập phiếu xuất kho | Trừ số lượng nguyên liệu trong kho khi thợ bếp xuất đồ đi làm bánh. |
| UC42 | Lập phiếu xuất hủy | Trừ hao hụt nguyên liệu do hết hạn sử dụng, hỏng hóc hoặc đổ vỡ. |
| UC43 | Cảnh báo tồn kho | Hệ thống tự động báo động khi nguyên liệu rớt xuống dưới mức an toàn hoặc sắp hết hạn. |
| UC44 | Tra cứu thẻ kho | Xem lại toàn bộ lịch sử biến động (nhập/xuất/tồn) của một loại nguyên liệu trong quá khứ. |
| UC45 | Truy vết nguồn gốc | Dò tìm nguồn gốc lô hàng của một loại nguyên liệu đang có vấn đề để quy trách nhiệm. |
| UC46 | Thêm nhà cung cấp | Khởi tạo hồ sơ đối tác cung cấp nguyên liệu mới bao gồm tên công ty và thông tin liên hệ. |
| UC47 | Sửa nhà cung cấp | Thay đổi địa chỉ, số điện thoại hoặc người đại diện của đối tác cung cấp hiện tại. |
| UC48 | Cập nhật trạng thái nhà cung cấp | Khóa hồ sơ đối tác khi tiệm ngừng hợp tác, đảm bảo không bị mất thông tin liên hệ trong các phiếu nhập kho đã lập trước đó. |
| UC49 | Tra cứu nhà cung cấp | Tìm kiếm và xem thông tin chi tiết của các nhà cung cấp để phục vụ việc liên hệ đặt hàng hoặc truy vết sự cố nguyên liệu. |

---

## VI. Phân hệ Thống kê & Báo cáo

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC50 | Báo cáo lợi nhuận | Tính lợi nhuận dựa trên giá vốn và đơn giá bán. |
| UC51 | Báo cáo doanh thu | Thống kê số tiền thu được theo ca, ngày, tháng, năm và biểu đồ xu hướng. |
| UC52 | Báo cáo tồn kho | Tổng hợp số lượng nguyên liệu đầu kỳ, nhập, xuất và tồn cuối kỳ. |

---

## VII. Quản lý Sổ Quỹ

| UC | Tên Use-case | Mô tả |
|---|---|---|
| UC53 | Thêm loại thu chi | Tạo mới một hạng mục lý do thu chi để phân loại dòng tiền minh bạch. |
| UC54 | Sửa loại thu chi | Chỉnh sửa tên gọi hoặc nội dung mô tả của một hạng mục thu chi hiện tại. |
| UC55 | Cập nhật trạng thái loại thu chi | Thao tác vô hiệu hóa hoặc kích hoạt lại danh mục thu chi, giúp ẩn hoặc hiện danh mục khi lập phiếu mới mà vẫn bảo toàn toàn vẹn dữ liệu lịch sử. |
| UC56 | Lập phiếu thu chi | Ghi nhận các khoản tiền ra hoặc vào két sắt không phát sinh trực tiếp từ giao dịch bán bánh trên máy bán hàng. |
| UC57 | Hủy phiếu thu chi | Vô hiệu hóa các phiếu thu chi bị lập sai, hệ thống tự động hoàn tác dòng tiền và tính toán lại số dư tồn quỹ thực tế. |

---

## Mapping: Màn hình → Use-case liên quan

| Màn hình | Use-case cần cover |
|---|---|
| Đăng nhập | UC01 |
| Mở ca làm việc | UC22 |
| Dashboard | UC02, UC23, UC51 |
| POS | UC16, UC17, UC18 |
| Đơn hàng / KDS | UC19, UC20, UC21 |
| Kho & Nguyên liệu | UC40, UC41, UC42, UC43, UC44, UC45 |
| Sản phẩm & Công thức | UC27, UC28, UC29, UC30, UC31, UC32, UC33, UC34, UC35, UC36 |
| Danh mục sản phẩm | UC24, UC25, UC26 |
| Nguyên liệu | UC37, UC38, UC39 |
| Khách hàng | UC10, UC11, UC12, UC13, UC14, UC15 |
| Nhân sự | UC04, UC05, UC06, UC07, UC08, UC09 |
| Nhà cung cấp | UC46, UC47, UC48, UC49 |
| Báo cáo | UC50, UC51, UC52 |
| Sổ quỹ | UC53, UC54, UC55, UC56, UC57 |
| Tài khoản | UC03, UC08 |
| Nhật ký hoạt động | UC09 |

---

## Phân quyền vai trò

**1. Quản lý** 
Actor "Quản lý" có quyền hạn cao nhất, được truy cập vào hầu hết các tính năng nội bộ và quản trị của hệ thống, bao gồm:
*   **Quản lý tài khoản:** Đăng nhập, Đăng xuất, Đổi mật khẩu cá nhân.
*   **Quản lý nhân sự:** Thêm nhân sự, Sửa nhân sự, Cập nhật trạng thái nhân sự, Tra cứu nhân sự, Phân quyền tài khoản, Xem nhật ký hoạt động.
*   **Quản lý khách hàng:** Thêm khách hàng, Sửa khách hàng, Cập nhật trạng thái khách hàng, Tra cứu khách hàng, Xem lịch sử mua hàng, Cấu hình hạng thành viên.
*   **Quản lý bán hàng:** Hủy hóa đơn bán lẻ, Hủy đơn đặt bánh và hoàn cọc.
*   **Quản lý sản phẩm:** Thêm danh mục sản phẩm, Sửa danh mục sản phẩm, Cập nhật trạng thái danh mục sản phẩm, Thêm sản phẩm, Sửa sản phẩm, Cập nhật trạng thái sản phẩm.
*   **Quản lý công thức & kế hoạch sản xuất:** Thêm công thức, Sửa công thức, Cập nhật trạng thái công thức, Tra cứu công thức, Tính toán số lượng bánh làm ra, Cấu hình giới hạn nhận đơn.
*   **Báo cáo thống kê:** Báo cáo lợi nhuận, Báo cáo doanh thu, Báo cáo tồn kho.
*   **Quản lý thu chi:** Thêm loại thu chi, Sửa loại thu chi, Cập nhật trạng thái loại thu chi, Lập phiếu thu chi, Hủy phiếu thu chi.

**2. Thu ngân** 
Actor "Thu ngân" chủ yếu thao tác các nghiệp vụ bán hàng, chăm sóc khách hàng và quản lý ca làm việc tại quầy:
*   **Quản lý tài khoản:** Đăng nhập, Đăng xuất, Đổi mật khẩu cá nhân.
*   **Quản lý khách hàng:** Thêm khách hàng, Sửa khách hàng, Cập nhật trạng thái khách hàng, Tra cứu khách hàng, Xem lịch sử mua hàng.
*   **Bán hàng:** Lập hóa đơn bán lẻ, Hủy hóa đơn bán lẻ, Lập đơn đặt bánh tùy chỉnh, Cập nhật trạng thái đơn, Tra cứu danh sách đơn, Hủy đơn và hoàn cọc, Tra cứu thông vị sản phẩm.
*   **Ca làm việc:** Mở ca làm việc, Đóng ca và đối soát.
*   **Quản lý thu chi:** Lập phiếu thu chi, Hủy phiếu thu chi.

**3. Thủ kho** 
Actor "Thủ kho" chuyên trách các tác vụ kiểm soát nguồn nguyên liệu và đối tác cung ứng:
*   **Quản lý tài khoản:** Đăng nhập, Đăng xuất, Đổi mật khẩu cá nhân.
*   **Quản lý kho và nguyên liệu:** Thêm nguyên liệu, Sửa nguyên liệu, Cập nhật trạng thái nguyên liệu, Lập phiếu nhập kho, Lập phiếu xuất hủy, Tra cứu thẻ kho.
*   **Quản lý nhà cung cấp:** Truy vết nguồn gốc, Thêm nhà cung cấp, Sửa nhà cung cấp, Cập nhật trạng thái nhà cung cấp, Tra cứu nhà cung cấp.

**4. Thợ bếp** 
Actor "Thợ bếp" giới hạn ở các thao tác liên quan đến lấy nguyên liệu và làm việc với hệ thống ở mức cơ bản:
*   **Quản lý tài khoản:** Đăng nhập, Đăng xuất, Đổi mật khẩu cá nhân.
*   **Quản lý kho:** Lập phiếu xuất kho để lấy nguyên liệu đi làm bánh.

*(Ngoài ra, có một Use-case tự động do tác nhân là **Hệ thống** tự thực hiện, đó là chức năng Cảnh báo tồn kho).*
