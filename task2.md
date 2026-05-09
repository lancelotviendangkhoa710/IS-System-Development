# Use-case: Xem lịch sử mua hàng

| Thuộc tính | Nội dung |
|---|---|
| **Tên Use-case** | Xem lịch sử mua hàng |
| **Mô tả** | Nhân viên tra cứu danh sách các đơn hàng cũ của một khách hàng cụ thể để hỗ trợ bảo hành hoặc tư vấn. |
| **Tác nhân** | - Thu ngân <br> - Quản lý |
| **Tiền điều kiện** | - Nhân viên đã đăng nhập và có quyền truy cập thông tin khách hàng. <br> - Đã chọn một khách hàng cụ thể trên hệ thống. |
| **Hậu điều kiện** | - Lịch sử mua hàng của khách hàng được hiển thị. <br> - Không có dữ liệu nào bị thay đổi. |

## Luồng sự kiện chính

1. Nhân viên chọn chức năng xem lịch sử mua hàng từ hồ sơ khách hàng.
2. Hệ thống tìm kiếm các hóa đơn và đơn đặt hàng từng giao dịch của khách hàng đó.
3. Hệ thống hiển thị danh sách lịch sử mua hàng.
4. Nhân viên chọn xem chi tiết một đơn hàng cụ thể.
5. Hệ thống hiển thị thông tin sản phẩm, số lượng, tổng tiền.

## Luồng sự kiện phụ

- **3a.** Khách hàng chưa từng mua hàng, hệ thống hiển thị thông báo chưa có lịch sử giao dịch.

## Luồng sự kiện lỗi

- **Bước 2:** Lỗi truy xuất dữ liệu, hệ thống báo lỗi, không hiển thị được lịch sử mua hàng.

---

# Use-case: Tra cứu danh sách đơn

| Thuộc tính | Nội dung |
|---|---|
| **Tên Use-case** | Tra cứu danh sách đơn |
| **Mô tả** | Thu ngân lọc và tìm kiếm đơn đặt trước theo ngày, giờ nhận, mã đơn hoặc trạng thái. |
| **Tác nhân** | - Thu ngân |
| **Tiền điều kiện** | - Thu ngân đã đăng nhập và ca làm việc đang mở. |
| **Hậu điều kiện** | - Danh sách đơn khớp điều kiện lọc được hiển thị. <br> - Không có thay đổi dữ liệu nào được thực hiện. |

## Luồng sự kiện chính

1. Thu ngân vào màn hình theo dõi đơn hàng.
2. Hệ thống tải và hiển thị danh sách đơn chưa hoàn thành của ngày hiện tại.
3. Thu ngân điều chỉnh bộ lọc theo ngày, giờ, mã đơn hoặc trạng thái rồi nhấn **"Tìm kiếm"**.
4. Hệ thống làm mới danh sách theo điều kiện đã chọn.

## Luồng sự kiện phụ

- **3a.** Thu ngân thay đổi bộ lọc trạng thái, hệ thống tự động làm mới danh sách không cần nhấn tìm.
- **3b.** Thu ngân chọn **"Tất cả"** trạng thái, hệ thống hiển thị toàn bộ đơn kể cả đã hoàn thành.
- **3c.** Thu ngân để trống mã đơn, hệ thống chỉ lọc theo ngày và trạng thái.

## Luồng sự kiện lỗi

- **Bước 3:** Mã đơn nhập không hợp lệ, hệ thống báo lỗi, danh sách không cập nhật.
- **Bước 4:** Không có đơn khớp hoặc lỗi kết nối, hệ thống hiển thị danh sách rỗng.


Hoàn thành luôn chức năng cấu hình giới hạn nhận đơn mà bạn làm chưa xong 
// TODO: inject CauHinhGioiHanPresenter khi xây dựng tầng Service+DAO 
// TODO: gọi CauHinhGioiHanPresenter.luuCauHinh(...)