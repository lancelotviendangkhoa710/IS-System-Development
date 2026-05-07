# Đặc tả Use-case: UC08 - Quản lý khách hàng

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | **Quản lý khách hàng** |
| **Mô tả Use-case** | Hệ thống lưu trữ và quản lý thông tin khách hàng (SĐT, Họ tên, Địa chỉ) để phục vụ tích điểm và chăm sóc khách hàng VIP. |
| **Actors** | Thu ngân, Quản lý cửa hàng |
| **Tiền điều kiện** | Nhân viên đã đăng nhập vào hệ thống. |
| **Hậu điều kiện** | Thông tin khách hàng được lưu trữ sẵn sàng để tra cứu tại màn hình bán hàng. |
| **Luồng sự kiện chính** | 1. Người dùng mở màn hình "Quản lý khách hàng".<br>2. Hệ thống hiển thị danh sách khách hàng từ CSDL.<br>3. **Nếu Thêm:** Người dùng nhấn "Thêm mới" và nhập SĐT, Họ tên.<br>4. **Nếu Sửa:** Người dùng chọn khách hàng và cập nhật thông tin.<br>5. Hệ thống kiểm tra SĐT phải đủ 10 số và không trùng lặp.<br>6. Hệ thống thực hiện ghi dữ liệu vào bảng KHACHHANG.<br>7. Hệ thống cập nhật lại danh sách và thông báo thành công. |

## Activity Diagram (Sơ đồ hoạt động)

```mermaid
graph TD
    subgraph "Người dùng (Nhân viên)"
        A(( )) --> B[Mở Quản lý khách hàng]
        B --> C{Chọn thao tác}
        
        %% Nhánh Thêm/Sửa
        C -- "Thêm/Sửa" --> D[Nhập SĐT & Thông tin khách]
        D --> E[Nhấn Lưu]
        
        %% Nhánh Xóa
        C -- "Xóa" --> G[Xác nhận xóa khách hàng]
    end

    subgraph "Hệ thống"
        H[Truy vấn danh sách khách hàng]
        I[Hiển thị bảng dữ liệu khách]
        J{Kiểm tra SĐT & Định dạng}
        K[Thông báo lỗi: SĐT đã tồn tại]
        L[Lưu thông tin khách hàng]
        M[Đánh dấu xóa bản ghi]
        N[Cập nhật bảng & Thông báo]
    end

    subgraph "CSDL"
        O[(Đọc bảng KHACHHANG)]
        P[(INSERT/UPDATE KHACHHANG)]
        Q[(UPDATE IsDeleted=1)]
    end

    %% Flow logic
    A --> H
    H --> O
    O -.-> H
    H --> I
    I --> B
    E --> J
    J -- "Lỗi" --> K
    K --> D
    J -- "Hợp lệ" --> L
    L --> P
    
    G --> M
    M --> Q
    
    P --> N
    Q --> N
    N --> I
    N --> Z(( ))
```
