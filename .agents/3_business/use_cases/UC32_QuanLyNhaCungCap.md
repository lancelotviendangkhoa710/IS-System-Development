# Đặc tả Use-case: UC32 - Quản lý nhà cung cấp

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | **Quản lý nhà cung cấp** |
| **Mô tả Use-case** | Quản lý thông tin các đơn vị cung cấp nguyên liệu đầu vào cho tiệm bánh (Tên NCC, Địa chỉ, SĐT). |
| **Actors** | Thủ kho, Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập hệ thống. |
| **Hậu điều kiện** | Danh sách NCC được cập nhật để phục vụ việc lập phiếu nhập kho. |
| **Luồng sự kiện chính** | 1. Người dùng mở mục "Nhà cung cấp".<br>2. Hệ thống hiển thị danh sách các đối tác hiện tại.<br>3. Người dùng thực hiện Thêm mới hoặc Sửa thông tin nhà cung cấp.<br>4. Hệ thống kiểm tra các thông tin bắt buộc (Tên, SĐT).<br>5. Hệ thống thực hiện lưu dữ liệu vào bảng NHACUNGCAP.<br>6. Hệ thống hiển thị thông báo thành công và làm mới bảng. |

## Activity Diagram (Sơ đồ hoạt động)

```mermaid
graph TD
    subgraph "Người dùng"
        A(( )) --> B[Mở mục Nhà cung cấp]
        B --> C{Chọn thao tác}
        
        %% Nhánh Thêm/Sửa
        C -- "Thêm/Sửa" --> D[Nhập thông tin NCC]
        D --> E[Nhấn Lưu]
        
        %% Nhánh Xóa
        C -- "Xóa" --> G[Xác nhận xóa NCC]
    end

    subgraph "Hệ thống"
        H[Truy vấn danh sách NCC]
        I[Hiển thị bảng đối tác]
        J{Kiểm tra ràng buộc dữ liệu}
        K[Thông báo lỗi: Thiếu thông tin]
        L[Lưu thông tin NCC]
        M[Kiểm tra ràng buộc xóa]
        N[Cập nhật bảng & Thông báo]
    end

    subgraph "CSDL"
        O[(Đọc bảng NHACUNGCAP)]
        P[(INSERT/UPDATE NHACUNGCAP)]
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
