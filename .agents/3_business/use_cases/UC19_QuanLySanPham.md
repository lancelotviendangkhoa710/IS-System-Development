# UC19: Quản lý sản phẩm

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Quản lý sản phẩm |
| **Mô tả Use-case** | Quản lý thông tin chi tiết của các loại bánh, đồ uống bao gồm tên, giá cơ bản, hình ảnh và danh mục phân loại. |
| **Actors** | Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập với quyền Quản lý cửa hàng. |
| **Hậu điều kiện** | Thông tin sản phẩm được cập nhật, sẵn sàng để hiển thị trên màn hình bán hàng (POS). |
| **Luồng sự kiện chính** | 1. Quản lý chọn chức năng "Quản lý sản phẩm".<br>2. Hệ thống hiển thị danh sách sản phẩm hiện tại.<br>3. Quản lý chọn "Thêm mới" hoặc chọn một sản phẩm để "Sửa".<br>4. Quản lý nhập Tên SP, Giá cơ bản, chọn Danh mục và tải lên Hình ảnh.<br>5. Quản lý nhấn "Lưu".<br>6. Hệ thống kiểm tra tính hợp lệ dữ liệu.<br>7. Hệ thống gọi Procedure `PROC_THEM_SANPHAM` hoặc `PROC_SUA_SANPHAM` để cập nhật DB.<br>8. Hệ thống thông báo thành công. |
| **Luồng sự kiện phụ** | 3a. Quản lý chọn "Vô hiệu hóa" sản phẩm để ngừng kinh doanh.<br>4a. Quản lý thay đổi hình ảnh sản phẩm. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 6a. Giá bán nhỏ hơn hoặc bằng 0: Hệ thống báo lỗi.<br>6b. Thiếu các thông tin bắt buộc: Hệ thống báo lỗi.<br>7a. Lỗi trùng mã sản phẩm: Hệ thống báo lỗi. |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn Quản lý sản phẩm;
    :Nhập thông tin sản phẩm (Tên, Giá, Ảnh);
    :Nhấn Lưu;
    
    swimlane Hệ thống
    :Kiểm tra tính hợp lệ;
    if (Hợp lệ?) then (Có)
        swimlane CSDL
        :Gọi Procedure CUD sản phẩm;
        :Ghi vào bảng SANPHAM;
        return Thành công;
        swimlane Hệ thống
        :Thông báo thành công;
    else (Không)
        swimlane Hệ thống
        :Hiển thị lỗi nghiệp vụ;
    endif
    
    swimlane Người dùng
    stop
```
