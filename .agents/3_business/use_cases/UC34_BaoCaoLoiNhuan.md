# Đặc tả Use-case: UC34 - Báo cáo lợi nhuận

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | **Báo cáo lợi nhuận** |
| **Mô tả Use-case** | Hệ thống tổng hợp doanh thu và chi phí (giá vốn, chi phí vận hành) để tính toán lợi nhuận thực tế theo khoảng thời gian. |
| **Actors** | Quản lý cửa hàng |
| **Tiền điều kiện** | Quản lý đã đăng nhập vào hệ thống. |
| **Hậu điều kiện** | Báo cáo lợi nhuận được hiển thị dưới dạng bảng biểu hoặc biểu đồ, hỗ trợ xuất file. |
| **Luồng sự kiện chính** | 1. Quản lý truy cập vào mục "Báo cáo & Thống kê".<br>2. Chọn loại báo cáo "Lợi nhuận".<br>3. Chọn khoảng thời gian (Từ ngày - Đến ngày) cần xem.<br>4. Hệ thống truy vấn dữ liệu doanh thu từ hóa đơn và chi phí từ phiếu nhập kho/công thức.<br>5. Hệ thống thực hiện tính toán: Lợi nhuận = Doanh thu - Giá vốn.<br>6. Hệ thống hiển thị kết quả lên màn hình. |
| **Luồng sự kiện phụ** | 6a. Quản lý có thể chọn "Xuất báo cáo" để tải file định dạng PDF hoặc Excel. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | - Nếu trong khoảng thời gian chọn không có dữ liệu giao dịch, hệ thống hiển thị thông báo "Không có dữ liệu để báo cáo".<br>- Nếu ngày kết thúc nhỏ hơn ngày bắt đầu, hệ thống báo lỗi khoảng thời gian không hợp lệ. |

## Activity Diagram (Sơ đồ hoạt động)

```mermaid
graph TD
    subgraph "Người dùng (Quản lý)"
        A[Bắt đầu] --> B[Chọn loại báo cáo Lợi nhuận]
        B --> C[Chọn khoảng thời gian]
        D[Nhấn Xuất báo cáo]
    end

    subgraph "Hệ thống"
        E[Hiển thị giao diện báo cáo]
        F{Kiểm tra thời gian}
        G[Thông báo lỗi: Thời gian sai]
        H[Truy vấn Doanh thu & Giá vốn]
        I[Tính toán Lợi nhuận & Hiển thị]
        J[Khởi tạo file PDF/Excel]
    end

    subgraph "CSDL"
        K[(Dữ liệu Hóa đơn)]
        L[(Dữ liệu Nhập kho/Công thức)]
    end

    %% Flow
    A --> E
    E --> B
    B --> C
    C --> F
    F -- "Không hợp lệ" --> G
    G --> C
    F -- "Hợp lệ" --> H
    H --> K
    H --> L
    H --> I
    I --> D
    D --> J
    J --> M((Kết thúc))
```
