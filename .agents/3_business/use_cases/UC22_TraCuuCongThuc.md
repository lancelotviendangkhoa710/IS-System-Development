# UC22: Tra cứu công thức

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Tra cứu công thức |
| **Mô tả Use-case** | Xem chi tiết các nguyên liệu và định lượng cấu thành nên một loại bánh cụ thể. |
| **Actors** | Thợ làm bánh, Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập. |
| **Hậu điều kiện** | Thông tin chi tiết công thức được hiển thị rõ ràng. |
| **Luồng sự kiện chính** | 1. Người dùng chọn sản phẩm cần xem công thức.<br>2. Người dùng nhấn nút "Xem công thức" hoặc "Thành phần".<br>3. Hệ thống truy xuất dữ liệu từ bảng công thức.<br>4. Hệ thống hiển thị bảng kê nguyên liệu, đơn vị tính và định lượng tương ứng.<br>5. Thợ làm bánh dựa vào đó để chuẩn bị nguyên liệu sản xuất. |
| **Luồng sự kiện phụ** | Không có. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 3a. Sản phẩm chưa được thiết lập công thức: Hệ thống hiển thị thông báo "Sản phẩm này chưa có dữ liệu công thức". |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn sản phẩm cần xem;
    
    swimlane Hệ thống
    :Truy vấn CSDL bảng công thức;
    if (Đã có dữ liệu?) then (Có)
        :Hiển thị danh sách nguyên liệu & định lượng;
    else (Không)
        :Thông báo Chưa có dữ liệu công thức;
    endif
    
    swimlane Người dùng
    stop
```
