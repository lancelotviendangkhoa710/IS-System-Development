# Đặc tả Use-case: UC03 - Quản lý nhân sự

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | **Quản lý nhân sự** |
| **Mô tả Use-case** | Cho phép người quản trị thêm mới nhân viên, cập nhật thông tin cá nhân hoặc vô hiệu hóa tài khoản của nhân viên trong hệ thống. |
| **Actors** | Quản trị viên, Quản lý cửa hàng |
| **Tiền điều kiện** | Người dùng đã đăng nhập với quyền quản trị. |
| **Hậu điều kiện** | Dữ liệu nhân sự được cập nhật đồng bộ trong CSDL. |
| **Luồng sự kiện chính** | 1. Người dùng mở màn hình "Quản lý nhân sự".<br>2. Hệ thống truy vấn CSDL và hiển thị danh sách nhân viên hiện tại.<br>3. **Nếu Thêm:** Người dùng nhấn "Thêm mới". Hệ thống load danh sách Vai trò từ bảng VAITRO. Người dùng nhập thông tin (Họ tên, SĐT, Tài khoản) và chọn Vai trò.<br>4. **Nếu Sửa:** Người dùng chọn nhân viên, hệ thống hiển thị thông tin hiện tại và danh sách Vai trò để thay đổi.<br>5. Hệ thống kiểm tra tính hợp lệ (không để trống, tài khoản không trùng).<br>6. Hệ thống thực hiện ghi dữ liệu vào bảng NHANVIEN.<br>7. Hệ thống làm mới danh sách và thông báo thành công. |
| **Luồng sự kiện phụ** | 3a. **Nếu Vô hiệu hóa:** Người dùng chọn nhân viên và nhấn "Vô hiệu hóa". Hệ thống cập nhật trạng thái hoạt động về 0. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | - Nếu tên tài khoản hoặc số điện thoại đã tồn tại, hệ thống báo lỗi (Popup) và yêu cầu nhập lại.<br>- Nếu nhân viên đang có các giao dịch liên quan, hệ thống chặn việc xóa vĩnh viễn và gợi ý vô hiệu hóa tài khoản. |

## Activity Diagram (Sơ đồ hoạt động)

```mermaid
graph TD
    subgraph "Người dùng (Quản lý)"
        A(( )) --> B[Mở Quản lý nhân sự]
        B --> C{Chọn thao tác}
        
        %% Nhánh Thêm/Sửa
        C -- "Thêm/Sửa" --> D[Nhập/Sửa thông tin nhân viên]
        D --> E[Nhấn Lưu]
        
        %% Nhánh Vô hiệu hóa
        C -- "Vô hiệu hóa" --> G[Xác nhận vô hiệu hóa]
    end

    subgraph "Hệ thống"
        H[Truy vấn danh sách nhân viên]
        I[Hiển thị bảng nhân sự]
        J{Kiểm tra định dạng & Trùng lặp}
        K[Thông báo lỗi: Tài khoản đã có]
        R[Truy vấn danh sách Vai trò]
        L[Lưu thông tin nhân viên]
        M[Cập nhật trạng thái Disabled]
        N[Làm mới danh sách & Thông báo]
    end

    subgraph "CSDL"
        O[(Đọc bảng NHANVIEN)]
        S[(Đọc bảng VAITRO)]
        P[(INSERT/UPDATE NHANVIEN)]
        Q[(UPDATE Status=0)]
    end

    %% Flow logic
    A --> H
    H --> O
    O -.-> H
    H --> I
    I --> B

    B -- "Thêm/Sửa" --> R
    R --> S
    S -.-> R
    R --> D

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
