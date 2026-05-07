# Đặc tả Use-case: UC16 - Hủy đơn và hoàn cọc

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | **Hủy đơn và hoàn cọc** |
| **Mô tả Use-case** | Cho phép nhân viên hủy các đơn đặt hàng (thường là đơn đặt bánh tùy chỉnh) và xử lý việc hoàn trả lại tiền đặt cọc cho khách hàng. |
| **Actors** | Thu ngân, Quản lý cửa hàng |
| **Tiền điều kiện** | Đơn hàng đang ở trạng thái chưa hoàn thành (ví dụ: Đã cọc, Đang sản xuất). |
| **Hậu điều kiện** | Trạng thái đơn chuyển sang "Đã hủy", tiền hoàn trả được ghi nhận vào hệ thống tài chính. |
| **Luồng sự kiện chính** | 1. Người dùng truy cập màn hình "Theo dõi đơn hàng".<br>2. Tìm kiếm và chọn đơn hàng cần hủy.<br>3. Hệ thống hiển thị chi tiết đơn và số tiền khách đã đặt cọc.<br>4. Người dùng chọn chức năng chuyển trạng thái sang "Đã hủy".<br>5. Hệ thống yêu cầu xác nhận và nhập lý do hủy đơn.<br>6. Người dùng nhập số tiền thực tế hoàn trả cho khách.<br>7. Hệ thống kiểm tra số tiền hoàn trả không vượt quá số tiền đã cọc.<br>8. Hệ thống lưu trạng thái mới và ghi nhận giao dịch hoàn tiền.<br>9. Hệ thống cập nhật nhãn trạng thái (Badge) sang màu đỏ (Cancelled). |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | - Nếu đơn hàng đã ở trạng thái "Hoàn thành", hệ thống chặn không cho phép hủy.<br>- Nếu số tiền hoàn trả nhập vào lớn hơn số tiền khách đã cọc, hệ thống báo lỗi và yêu cầu nhập lại. |

## Activity Diagram (Sơ đồ hoạt động)

```mermaid
graph TD
    subgraph "Người dùng"
        A[Bắt đầu] --> B[Chọn đơn hàng & Chuyển sang Đã hủy]
        C[Nhập số tiền hoàn & Lý do]
        D[Xác nhận hủy]
    end

    subgraph "Hệ thống"
        E[Hiển thị thông tin cọc & Trạng thái]
        F{Kiểm tra trạng thái đơn}
        G[Thông báo: Đơn đã hoàn thành - không thể hủy]
        H{Kiểm tra số tiền hoàn}
        I[Thông báo: Tiền hoàn vượt mức cọc]
        J[Cập nhật trạng thái Cancelled & Lưu lý do]
    end

    subgraph "CSDL"
        K[(Dữ liệu Đơn hàng & Tài chính)]
    end

    %% Flow
    A --> E
    E --> B
    B --> F
    F -- "Sai quy tắc" --> G
    G --> B
    F -- "Hợp lệ" --> C
    C --> H
    H -- "Vượt mức" --> I
    I --> C
    H -- "Hợp lệ" --> D
    D --> J
    J --> K
    J --> L((Kết thúc))
```
