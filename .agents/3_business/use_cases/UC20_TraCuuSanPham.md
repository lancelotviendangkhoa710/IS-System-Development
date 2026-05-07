# UC20: Tra cứu sản phẩm

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Tra cứu sản phẩm |
| **Mô tả Use-case** | Tìm kiếm sản phẩm trong thực đơn dựa trên tên sản phẩm, mã sản phẩm hoặc lọc theo danh mục. |
| **Actors** | Thu ngân, Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập vào hệ thống. |
| **Hậu điều kiện** | Danh sách sản phẩm thỏa mãn điều kiện tìm kiếm được hiển thị. |
| **Luồng sự kiện chính** | 1. Người dùng truy cập màn hình POS hoặc Quản lý sản phẩm.<br>2. Người dùng nhập từ khóa tìm kiếm vào ô tìm kiếm.<br>3. Người dùng chọn danh mục cần lọc (nếu cần).<br>4. Hệ thống tự động lọc và hiển thị danh sách sản phẩm tương ứng theo thời gian thực.<br>5. Người dùng xem thông tin chi tiết hoặc chọn sản phẩm để bán. |
| **Luồng sự kiện phụ** | 4a. Người dùng nhấn nút "Xóa trắng" để hiển thị lại toàn bộ sản phẩm. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 4b. Không tìm thấy sản phẩm nào khớp với từ khóa: Hệ thống hiển thị thông báo "Không tìm thấy kết quả". |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Nhập từ khóa tìm kiếm;
    :Chọn danh mục lọc;
    
    swimlane Hệ thống
    :Thực hiện lọc danh sách theo từ khóa & danh mục;
    if (Có kết quả?) then (Có)
        :Hiển thị danh sách sản phẩm khớp;
    else (Không)
        :Thông báo Không tìm thấy kết quả;
    endif
    
    swimlane Người dùng
    stop
```
