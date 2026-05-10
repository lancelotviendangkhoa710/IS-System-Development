# UC10 — Xem lịch sử mua hàng

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Xem lịch sử mua hàng |
| **Mô tả Use-case** | Nhân viên tra cứu danh sách các đơn hàng cũ của một khách hàng cụ thể để hỗ trợ bảo hành hoặc tư vấn. |
| **Actors** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đã đăng nhập và có quyền truy cập thông tin khách hàng. Đã chọn một khách hàng cụ thể từ danh sách. |
| **Hậu điều kiện** | Lịch sử mua hàng của khách hàng được hiển thị trong cửa sổ riêng. Không có dữ liệu nào bị thay đổi. |
| **Luồng sự kiện chính** | 1. Nhân viên vào màn hình Quản lý Khách hàng.<br>2. Hệ thống hiển thị danh sách khách hàng.<br>3. Nhân viên nhấn nút **"📋 Lịch sử"** trên dòng khách hàng muốn xem.<br>4. Hệ thống truy vấn DB lấy danh sách đơn hàng theo mã khách hàng.<br>5. Hệ thống mở cửa sổ hiển thị danh sách đơn: Mã đơn, Ngày nhận, Trạng thái, Tổng tiền, Đã cọc. |
| **Luồng sự kiện phụ** | **3a.** Khách hàng chưa từng mua hàng — hệ thống hiển thị thông báo "Khách hàng chưa có lịch sử giao dịch." |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | **Bước 4:** Lỗi kết nối DB — hệ thống hiển thị thông báo lỗi trên label trạng thái, cửa sổ không mở. |

---

## Activity Diagram

```mermaid
flowchart TD
    subgraph NguoiDung["👤 Nhân viên"]
        A([Bắt đầu]) --> B[Vào màn hình\nKhách hàng]
        B --> C[Nhấn nút\nLịch sử]
    end

    subgraph HeThong["🖥️ Hệ thống"]
        D{Có khách\nđược chọn?}
        E[Gọi Presenter\nxemLichSuMuaHang]
        F[Hiển thị\ndanh sách đơn]
        G[Hiện thông báo\n'Chưa có giao dịch']
        H[Hiện lỗi\ntrên label]
    end

    subgraph CSDL["🗄️ CSDL"]
        DB1[(VW_DanhSachDonHang\nWHERE MAKH = ?)]
    end

    C --> D
    D -- Có --> E
    D -- Không --> H
    E --> DB1
    DB1 -- Có dữ liệu --> F
    DB1 -- Rỗng --> G
    DB1 -- Lỗi DB --> H
    F --> Z([Kết thúc])
    G --> Z
    H --> Z
```

---


