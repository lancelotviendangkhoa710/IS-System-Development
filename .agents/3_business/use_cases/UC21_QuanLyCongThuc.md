# UC21: Quản lý công thức

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Quản lý công thức |
| **Mô tả Use-case** | Thiết lập định lượng nguyên liệu cần thiết để sản xuất một đơn vị sản phẩm (bánh). |
| **Actors** | Quản lý cửa hàng, Thợ làm bánh |
| **Tiền điều kiện** | Sản phẩm và các nguyên liệu đã tồn tại trong hệ thống. |
| **Hậu điều kiện** | Công thức sản xuất được lưu lại, phục vụ cho việc tính toán tồn kho và giá vốn. |
| **Luồng sự kiện chính** | 1. Người dùng chọn một sản phẩm cần thiết lập công thức.<br>2. Người dùng chọn chức năng "Quản lý công thức/Thành phần".<br>3. Hệ thống hiển thị danh sách các thành phần hiện có của bánh.<br>4. Người dùng thêm một nguyên liệu mới và nhập định lượng.<br>5. Người dùng nhấn "Lưu công thức".<br>6. Hệ thống ghi nhận các thành phần vào DB. |
| **Luồng sự kiện phụ** | 4a. Người dùng chỉnh sửa định lượng của một thành phần cũ.<br>4b. Người dùng xóa một thành phần khỏi công thức. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | 5a. Định lượng nhập vào là số âm hoặc bằng 0: Hệ thống báo lỗi.<br>6a. Lỗi kết nối DB: Hệ thống thông báo không thể lưu. |

## Activity Diagram

```mermaid
activityDiagram
    autonumber
    swimlane Người dùng
    start
    :Chọn sản phẩm;
    :Thêm/Sửa thành phần nguyên liệu;
    :Nhập định lượng tiêu hao;
    :Nhấn Lưu công thức;
    
    swimlane Hệ thống
    :Kiểm tra tính hợp lệ;
    if (Hợp lệ?) then (Có)
        swimlane CSDL
        :Lưu vào bảng CONGTHUC_THANHPHAN;
        return Thành công;
        swimlane Hệ thống
        :Thông báo Lưu công thức thành công;
    else (Không)
        swimlane Hệ thống
        :Báo lỗi định lượng không hợp lệ;
    endif
    
    swimlane Người dùng
    stop
```
