# UC11: Cấu hình hạng thành viên

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Cấu hình hạng thành viên |
| **Mô tả Use-case** | Cho phép Quản lý cửa hàng thiết lập các mức hạng thành viên, điểm tối thiểu để đạt hạng và tỷ lệ giảm giá tương ứng. |
| **Actors** | Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập với quyền Quản lý cửa hàng. |
| **Hậu điều kiện** | Thông tin hạng thành viên được cập nhật trong hệ thống và áp dụng cho các giao dịch sau đó. |
| **Luồng sự kiện chính** | 1. Quản lý chọn chức năng "Cấu hình hạng thành viên".<br>2. Hệ thống hiển thị danh sách các hạng hiện có (Đồng, Bạc, Vàng, Kim cương...).<br>3. Quản lý chọn một hạng để chỉnh sửa.<br>4. Quản lý nhập điểm tối thiểu và % giảm giá mới.<br>5. Quản lý nhấn "Lưu".<br>6. Hệ thống kiểm tra tính hợp lệ của dữ liệu.<br>7. Hệ thống gọi Procedure `PROC_SUA_HANGTHANHVIEN` để cập nhật DB.<br>8. Hệ thống thông báo thành công và cập nhật lại danh sách hiển thị. |
| **Luồng sự kiện phụ** | 3a. Quản lý chọn xem chi tiết hạng.<br>3b. Quản lý tìm kiếm hạng theo tên. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 6a. Điểm tối thiểu hoặc % giảm giá không phải là số hợp lệ: Hệ thống báo lỗi và yêu cầu nhập lại.<br>6b. % giảm giá vượt quá mức quy định (ví dụ > 100%): Hệ thống chặn và báo lỗi. |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn chức năng Cấu hình hạng;
    :Chọn hạng cần sửa;
    :Nhập thông tin mới (Điểm, % Giảm);
    :Nhấn Lưu;
    
    swimlane Hệ thống
    :Kiểm tra tính hợp lệ dữ liệu;
    if (Dữ liệu hợp lệ?) then (Có)
        swimlane CSDL
        :Gọi PROC_SUA_HANGTHANHVIEN;
        :Ghi nhận thay đổi vào bảng HANGTHANHVIEN;
        return Cập nhật thành công;
        swimlane Hệ thống
        :Thông báo thành công;
        :Tải lại danh sách;
    else (Không)
        swimlane Hệ thống
        :Hiển thị lỗi nhập liệu;
    endif
    
    swimlane Người dùng
    stop
```
