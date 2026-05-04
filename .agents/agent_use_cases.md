# Tài liệu Đặc tả Use Case - Hệ thống Quản lý Tiệm bánh (H3K Bakery)

Tài liệu này cung cấp cái nhìn tổng quan và chi tiết về các Use Case của hệ thống, đặc biệt chú trọng vào các luồng sự kiện ngoại lệ (extend) và các luồng sự kiện bao gồm (include), được thiết kế tối ưu để Agent có thể đọc hiểu và nắm bắt toàn bộ nghiệp vụ.

## 1. Danh sách Actor (Người dùng hệ thống)
- **Quản trị viên:** Nắm toàn quyền kiểm soát nền tảng kỹ thuật, quản lý tài khoản, phân quyền, xem nhật ký và khôi phục dữ liệu.
- **Quản lý cửa hàng:** Điều hành hoạt động kinh doanh, cấu hình quy tắc vận hành, quản lý hàng hóa, đối tác và xem báo cáo.
- **Thu ngân:** Phụ trách thanh toán, lập hóa đơn, quản lý đơn đặt bánh, hồ sơ khách hàng và đối soát ca.
- **Thợ làm bánh:** Tương tác màn hình điều phối bếp, nhận lệnh, cập nhật tiến độ, tra cứu công thức, xuất kho sản xuất.
- **Thủ kho:** Quản lý nguyên liệu đầu vào, lập phiếu nhập/xuất/hủy, theo dõi tồn kho và truy vết nguồn gốc.

---

## 2. Chi tiết các Use Case (36 Use Cases)

### UC01: Đăng nhập/Đăng xuất
- **Mô tả:** Thao tác truy cập/thoát phiên làm việc của tài khoản cá nhân.
- **Actors:** Tất cả người dùng.
- **Include:** `Kiểm tra tính hợp lệ` (xác thực tài khoản).
- **Extend:**
  - `Thông báo lỗi sai thông tin` (khi nhập sai).
  - `Từ chối truy cập` (khi tài khoản bị vô hiệu hóa).

### UC02: Đổi mật khẩu
- **Mô tả:** Cho phép người dùng tự thay đổi mật mã bảo vệ.
- **Actors:** Tất cả người dùng.
- **Include:** `Đối chiếu mật khẩu hiện tại`.
- **Extend:**
  - `Hủy bỏ đổi mật khẩu`.
  - `Cảnh báo sai mật khẩu cũ`.
  - `Cảnh báo mật khẩu mới không khớp`.
  - `Cảnh báo mật khẩu không đạt chuẩn`.

### UC03: Thêm/Xóa/Sửa nhân sự (Cập nhật nhân sự)
- **Mô tả:** Quản lý hồ sơ, tạo mới, vô hiệu hóa tài khoản nhân viên.
- **Actors:** Quản trị viên, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Báo lỗi trùng tên đăng nhập`.
  - `Từ chối hộp thoại vô hiệu hóa`.
  - `Hủy bỏ điền biểu mẫu`.
  - `Cảnh báo bỏ trống trường bắt buộc`.

### UC04: Tra cứu nhân sự
- **Mô tả:** Tìm kiếm thông tin nhân viên theo từ khóa/bộ lọc.
- **Actors:** Quản trị viên, Quản lý cửa hàng.
- **Include:** `Truy xuất cơ sở dữ liệu`.
- **Extend:**
  - `Xóa từ khóa (Hiển thị lại ban đầu)`.
  - `Thông báo không tìm thấy kết quả`.

### UC05: Phân quyền tài khoản
- **Mô tả:** Cấp phát/giới hạn quyền truy cập chức năng cho nhân viên.
- **Actors:** Quản trị viên.
- **Include:** Không có.
- **Extend:**
  - `Hủy bỏ lưu phân quyền`.
  - `Chặn thu hồi toàn bộ quyền bản thân`.

### UC06: Xem nhật ký hoạt động
- **Mô tả:** Kiểm tra lịch sử thao tác của hệ thống.
- **Actors:** Quản trị viên.
- **Include:** Không có.
- **Extend:**
  - `Xuất báo cáo nhật ký`.
  - `Thông báo lỗi mất kết nối máy chủ`.

### UC07: Khôi phục dữ liệu
- **Mô tả:** Khôi phục dữ liệu đã bị xóa mềm.
- **Actors:** Quản trị viên.
- **Include:** Không có.
- **Extend:**
  - `Hủy bỏ khôi phục`.
  - `Báo lỗi xung đột mã định danh`.

### UC08: Thêm/Xóa/Sửa khách hàng (Quản lý hồ sơ khách hàng)
- **Mô tả:** Quản lý thông tin liên hệ của khách hàng thành viên.
- **Actors:** Quản lý cửa hàng, Thu ngân.
- **Include:** Không có.
- **Extend:**
  - `Lỗi trùng lặp số điện thoại`.
  - `Hủy bỏ trên hộp thoại xóa`.
  - `Chặn xóa (Có đơn chưa hoàn thành)`.
  - `Hủy bỏ biểu mẫu`.
  - `Cảnh báo bỏ trống trường bắt buộc`.

### UC09: Tra cứu khách hàng
- **Mô tả:** Tìm kiếm thông tin khách hàng thành viên để tư vấn/bán hàng.
- **Actors:** Quản lý cửa hàng, Thu ngân.
- **Include:** Không có.
- **Extend:**
  - `Xóa từ khóa (Hiển thị lại)`.
  - `Không tìm thấy & Gợi ý tạo mới`.

### UC10: Tra cứu lịch sử mua hàng
- **Mô tả:** Xem lại danh sách hóa đơn mà khách đã giao dịch.
- **Actors:** Quản lý cửa hàng, Thu ngân.
- **Include:** Không có.
- **Extend:**
  - `Thiết lập bộ lọc khoảng thời gian`.
  - `Thông báo khách hàng mới (Trống)`.

### UC11: Cấu hình hạng thành viên
- **Mô tả:** Cài đặt tỷ lệ tích điểm và quy tắc nâng hạng.
- **Actors:** Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Hủy bỏ cài đặt`.
  - `Báo lỗi vượt mức giảm giá / trùng điểm`.

### UC12: Lập hóa đơn bán lẻ
- **Mô tả:** Bán bánh có sẵn, tính tiền, áp dụng khuyến mãi hạng thẻ, in hóa đơn.
- **Actors:** Thu ngân.
- **Include:** `Kiểm tra thông tin thành viên`.
- **Extend:**
  - `Khách vãng lai (Bỏ qua nhập SĐT)`.
  - `Xóa bớt món trước khi thanh toán`.
  - `Lỗi nhập thiếu tiền khách đưa`.

### UC13: Lập đơn đặt bánh tùy chỉnh
- **Mô tả:** Tạo đơn yêu cầu riêng (chọn mẫu, thêm phụ kiện, hẹn giờ, thu cọc).
- **Actors:** Thu ngân.
- **Include:** Không có.
- **Extend:**
  - `Bỏ qua bước thêm phụ kiện`.
  - `Từ chối nhận đơn (Giờ hẹn quá cận)`.
  - `Chặn thao tác (Tiền cọc thấp hơn tối thiểu)`.

### UC14: Cập nhật trạng thái đơn
- **Mô tả:** Chuyển đổi trạng thái đơn (Chờ xử lý -> Đang làm -> Chờ giao -> Hoàn thành).
- **Actors:** Thu ngân, Thợ làm bánh, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Cảnh báo vi phạm (Chuyển tắt trạng thái)`.

### UC15: Tra cứu danh sách đơn
- **Mô tả:** Xem toàn bộ giao dịch theo các tiêu chí (ngày, trạng thái, mã).
- **Actors:** Thu ngân, Quản lý cửa hàng, Thợ làm bánh.
- **Include:** Không có.
- **Extend:**
  - `Xóa trắng các bộ lọc`.
  - `Thông báo không tìm thấy kết quả`.

### UC16: Hủy đơn và hoàn cọc
- **Mô tả:** Chấm dứt đơn đặt trước và xử lý trả cọc/tính phí bồi thường.
- **Actors:** Thu ngân, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Khách hàng đổi ý (Không hủy nữa)`.
  - `Chặn hủy (Bánh đã làm xong / Chờ giao)`.

### UC17: Đối soát tiền cuối ca
- **Mô tả:** Kiểm đếm tiền thực tế so với doanh thu ca để bàn giao.
- **Actors:** Thu ngân, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Ghi chú giải trình (khi lệch tiền)`.
  - `Tính tổng chi tiết theo mệnh giá`.
  - `Báo lỗi điền số âm`.
  - `Chặn chốt ca (thiếu giải trình)`.

### UC18: Thêm/Xóa/Sửa danh mục sản phẩm
- **Mô tả:** Phân loại nhóm hàng hóa.
- **Actors:** Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Báo lỗi trùng lặp`.
  - `Chặn xóa (Nhóm đang chứa sản phẩm)`.
  - `Hủy bỏ lưu`.
  - `Cảnh báo trống tên nhóm`.

### UC19: Thêm/Xóa/Sửa sản phẩm (Quản lý sản phẩm)
- **Mô tả:** Quản lý thông tin bánh, đồ uống trong thực đơn.
- **Actors:** Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Cảnh báo (Sản phẩm đang nằm trong đơn chờ)`.
  - `Lỗi giá bán âm hoặc bằng không`.

### UC20: Tra cứu sản phẩm
- **Mô tả:** Tìm kiếm thông tin mặt hàng phục vụ bán hàng.
- **Actors:** Thu ngân, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Xóa trắng từ khóa / Bỏ bộ lọc`.
  - `Thông báo không tìm thấy kết quả`.

### UC21: Thêm/Xóa/Sửa công thức (Quản lý công thức)
- **Mô tả:** Cài đặt định lượng tiêu hao nguyên liệu cho từng loại bánh.
- **Actors:** Quản lý cửa hàng, Thợ làm bánh.
- **Include:** Không có.
- **Extend:**
  - `Lỗi trùng lặp bánh đã có công thức`.
  - `Hủy bỏ lưu`.
  - `Lỗi định lượng âm hoặc bằng không`.

### UC22: Tra cứu công thức
- **Mô tả:** Xem chi tiết bảng kê nguyên liệu cấu thành sản phẩm.
- **Actors:** Quản lý cửa hàng, Thợ làm bánh.
- **Include:** Không có.
- **Extend:**
  - `Xóa trắng từ khóa tìm kiếm`.
  - `Thông báo không tìm thấy kết quả`.

### UC23: Tính toán số lượng bánh làm ra
- **Mô tả:** Dự báo số lượng bánh làm được dựa trên kho hiện tại.
- **Actors:** Quản lý cửa hàng, Thợ làm bánh.
- **Include:** Không có.
- **Extend:**
  - `Dừng tính toán (Phát hiện vật tư tồn bằng 0)`.

### UC24: Cấu hình giới hạn nhận đơn
- **Mô tả:** Đặt ngưỡng sản xuất tối đa trong một ngày.
- **Actors:** Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Hủy bỏ cài đặt`.
  - `Báo lỗi điền số âm`.
  - `Từ chối hạ mức (Nhỏ hơn số đơn đã lỡ nhận)`.

### UC25: Thêm/Xóa/Sửa nguyên liệu (Quản lý nguyên liệu)
- **Mô tả:** Quản lý danh mục vật tư nhập kho.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Báo lỗi trùng lặp tên`.
  - `Chặn xóa (Đang tồn kho hoặc có trong công thức)`.
  - `Hủy bỏ lưu`.
  - `Cảnh báo thiếu thông tin bắt buộc`.

### UC26: Lập phiếu nhập kho
- **Mô tả:** Ghi nhận mua nguyên liệu từ nhà cung cấp.
- **Actors:** Thủ kho.
- **Include:** Không có.
- **Extend:**
  - `Xóa dòng nguyên liệu nhập sai`.
  - `Lưu tạm nháp phiếu nhập`.
  - `Lỗi HSD nhỏ hơn ngày sản xuất`.
  - `Chặn lưu (Thiếu nhà cung cấp / phiếu trống)`.

### UC27: Lập phiếu xuất kho
- **Mô tả:** Trừ nguyên liệu đi sản xuất.
- **Actors:** Thủ kho, Thợ làm bánh.
- **Include:** `Kiểm tra số lượng tồn`.
- **Extend:**
  - `Hủy giao dịch (Kho không đủ vật tư)`.
  - `Yêu cầu chờ (Xung đột thao tác xuất trùng lặp - Khóa an toàn)`.

### UC28: Lập phiếu xuất hủy
- **Mô tả:** Ghi nhận hao hụt nguyên liệu hỏng/hết hạn.
- **Actors:** Thủ kho.
- **Include:** Không có.
- **Extend:**
  - `Bôi đỏ lô nguyên liệu đã quá hạn`.
  - `Báo lỗi xuất lố (Số lượng hủy lớn hơn tồn)`.

### UC29: Cảnh báo tồn kho
- **Mô tả:** Tự động thông báo khi nguyên liệu sắp hết/hết hạn.
- **Actors:** Thủ kho.
- **Include:** Không có.
- **Extend:**
  - `Chuyển hướng lập phiếu nhập / xuất hủy`.
  - `Thông báo kho ổn định (Không có lô cận hạn/thiếu)`.

### UC30: Tra cứu thẻ kho
- **Mô tả:** Xem lại biến động (nhập/xuất/tồn) của vật tư.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Xuất báo cáo dạng bảng tính`.
  - `Báo lỗi chọn khoảng thời gian không hợp lệ`.

### UC31: Truy vết nguồn gốc
- **Mô tả:** Dò tìm lô hàng gốc từ phiếu xuất báo lỗi để quy trách nhiệm.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Báo lỗi không tìm thấy dữ liệu chứng từ`.

### UC32: Thêm/Xóa/Sửa nhà cung cấp (Quản lý nhà cung cấp)
- **Mô tả:** Quản lý đối tác cung cấp vật tư.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Báo lỗi trùng lặp SĐT liên lạc`.
  - `Chặn xóa (Có công nợ/giao dịch -> Chuyển ngừng giao dịch)`.
  - `Hủy bỏ biểu mẫu`.
  - `Cảnh báo để trống trường bắt buộc`.

### UC33: Tra cứu nhà cung cấp
- **Mô tả:** Xem thông tin liên lạc và lịch sử nhập kho của đối tác.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Xóa trắng từ khóa (Tải lại ban đầu)`.
  - `Thông báo không tìm thấy đối tác`.

### UC34: Báo cáo lợi nhuận
- **Mô tả:** Tính toán lợi nhuận dựa trên giá vốn thực tế và giá bán.
- **Actors:** Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Kết xuất báo cáo tải về máy`.
  - `Thông báo không có dữ liệu để tính toán`.

### UC35: Báo cáo doanh thu
- **Mô tả:** Xem dòng tiền, biểu đồ doanh thu theo mốc thời gian.
- **Actors:** Quản lý cửa hàng, Thu ngân.
- **Include:** Không có.
- **Extend:**
  - `Xem danh sách hóa đơn chi tiết trên biểu đồ`.
  - `Cảnh báo lỗi thời gian (Mốc nằm trong tương lai)`.

### UC36: Báo cáo tồn kho
- **Mô tả:** Đối chiếu tồn kho đầu kỳ, biến động và cuối kỳ.
- **Actors:** Thủ kho, Quản lý cửa hàng.
- **Include:** Không có.
- **Extend:**
  - `Kết xuất danh sách tồn kho`.
  - `Báo lỗi gián đoạn truy xuất cơ sở dữ liệu`.
