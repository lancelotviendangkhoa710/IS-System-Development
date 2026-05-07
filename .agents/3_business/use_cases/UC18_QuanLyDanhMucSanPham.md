# UC18: Quản lý danh mục sản phẩm

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Quản lý danh mục sản phẩm |
| **Mô tả Use-case** | Cho phép Quản lý cửa hàng thêm, sửa hoặc xóa các danh mục sản phẩm (nhóm sản phẩm) để phân loại hàng hóa trong thực đơn. |
| **Actors** | Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập với quyền Quản lý cửa hàng. |
| **Hậu điều kiện** | Danh sách danh mục sản phẩm được cập nhật trong hệ thống. |
| **Luồng sự kiện chính** | 1. Quản lý chọn chức năng "Quản lý danh mục".<br>2. Hệ thống hiển thị danh sách các danh mục hiện có.<br>3. Quản lý chọn "Thêm mới" hoặc chọn một danh mục để "Sửa".<br>4. Quản lý nhập tên danh mục và mô tả.<br>5. Quản lý nhấn "Lưu".<br>6. Hệ thống kiểm tra trùng lặp và tính hợp lệ.<br>7. Hệ thống gọi Procedure để cập nhật DB.<br>8. Hệ thống thông báo thành công và làm mới danh sách. |
| **Luồng sự kiện phụ** | 3a. Quản lý chọn "Xóa" danh mục.<br>3b. Hệ thống kiểm tra xem danh mục có đang chứa sản phẩm nào không. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 6a. Tên danh mục bị bỏ trống: Hệ thống báo lỗi.<br>6b. Tên danh mục đã tồn tại: Hệ thống báo lỗi trùng lặp.<br>3b1. Danh mục đang có sản phẩm: Hệ thống chặn xóa và báo lỗi "Danh mục đang được sử dụng". |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn Quản lý danh mục;
    :Nhập thông tin danh mục;
    :Nhấn Lưu;
    
    swimlane Hệ thống
    :Kiểm tra trùng lặp;
    if (Hợp lệ?) then (Có)
        swimlane CSDL
        :Lưu vào bảng DANHMUCSANPHAM;
        return Thành công;
        swimlane Hệ thống
        :Thông báo thành công;
    else (Không)
        swimlane Hệ thống
        :Hiển thị lỗi (Trùng/Trống);
    endif
    
    swimlane Người dùng
    stop
```
