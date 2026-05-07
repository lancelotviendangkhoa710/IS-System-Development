# UC33: Tra cứu nhà cung cấp

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Tra cứu nhà cung cấp |
| **Mô tả Use-case** | Tìm kiếm thông tin liên lạc và lịch sử đối tác cung cấp nguyên liệu dựa trên tên hoặc số điện thoại. |
| **Actors** | Thủ kho, Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập. |
| **Hậu điều kiện** | Thông tin nhà cung cấp cần tìm được hiển thị trên màn hình. |
| **Luồng sự kiện chính** | 1. Người dùng truy cập màn hình "Quản lý nhà cung cấp".<br>2. Người dùng nhập tên hoặc số điện thoại vào ô tìm kiếm.<br>3. Hệ thống thực hiện truy vấn và lọc danh sách nhà cung cấp.<br>4. Hệ thống hiển thị các kết quả phù hợp.<br>5. Người dùng chọn một nhà cung cấp để xem chi tiết địa chỉ và thông tin liên hệ. |
| **Luồng sự kiện phụ** | 2a. Người dùng nhấn "Tải lại" để xóa bộ lọc và xem toàn bộ danh sách. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 3a. Không tìm thấy đối tác nào khớp với từ khóa: Hệ thống báo "Không tìm thấy kết quả". |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Nhập từ khóa (Tên/SĐT);
    
    swimlane Hệ thống
    :Tìm kiếm trong danh sách NCC;
    if (Có kết quả?) then (Có)
        :Hiển thị danh sách kết quả;
    else (Không)
        :Thông báo Không tìm thấy đối tác;
    endif
    
    swimlane Người dùng
    stop
```
