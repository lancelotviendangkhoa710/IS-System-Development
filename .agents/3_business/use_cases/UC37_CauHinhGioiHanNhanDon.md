# UC37 — Cấu hình giới hạn nhận đơn

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Cấu hình giới hạn nhận đơn |
| **Mô tả Use-case** | Quản lý thiết lập số lượng bánh tối đa bếp có thể sản xuất trong một ngày để hệ thống tự động từ chối đơn vượt công suất. |
| **Actors** | Quản lý |
| **Tiền điều kiện** | Quản lý đã đăng nhập với quyền truy cập cấu hình hệ thống. |
| **Hậu điều kiện** | Bảng `NANGLUCSANXUAT` được cập nhật với giới hạn mới cho ngày được chọn. Trigger `TRG_KIEMSOAT_CONGSUAT_DONHANG` sẽ tự động áp dụng giới hạn này khi có đơn mới. |
| **Luồng sự kiện chính** | 1. Quản lý vào màn hình **Cấu hình giới hạn nhận đơn**.<br>2. Hệ thống tải và hiển thị danh sách cấu hình hiện tại (60 bản ghi gần nhất).<br>3. Quản lý nhập số lượng bánh tối đa vào trường **"Giới hạn bánh tùy chỉnh"**.<br>4. Quản lý nhấn **"💾 Lưu cấu hình"**.<br>5. Hệ thống validate input (số nguyên dương).<br>6. Hệ thống thực hiện UPSERT vào `NANGLUCSANXUAT` cho ngày hiện tại.<br>7. Hệ thống làm mới danh sách và hiển thị thông báo thành công. |
| **Luồng sự kiện phụ** | **3a.** Quản lý không nhập gì → nhấn Hủy → hệ thống xóa form, không thay đổi DB. |
| **Luồng sự kiện lỗi hoặc ngoại lệ** | **Bước 5:** Input không phải số nguyên dương → hệ thống hiển thị lỗi trên label, không gọi DB.<br>**Bước 6:** Lỗi DB → hệ thống hiển thị thông báo lỗi, trạng thái DB không thay đổi. |

---

## Activity Diagram

```mermaid
flowchart TD
    subgraph NguoiDung["👤 Quản lý"]
        A([Bắt đầu]) --> B[Vào màn hình\nCấu hình giới hạn]
        C[Nhập giới hạn\nsố bánh/ngày]
        D[Nhấn Lưu]
    end

    subgraph HeThong["🖥️ Hệ thống"]
        E[Tải danh sách\ncấu hình hiện tại]
        F{Validate\ninput hợp lệ?}
        G[Hiển thị lỗi\ntrên label]
        H[Gọi Service\nluuCauHinh]
        I[Làm mới danh sách\nHiển thị thành công]
        J[Hiển thị lỗi DB]
    end

    subgraph CSDL["🗄️ CSDL"]
        DB1[(NANGLUCSANXUAT\nSELECT 60 rows)]
        DB2[(NANGLUCSANXUAT\nMERGE ngày hiện tại)]
    end

    B --> E --> DB1 --> C --> D
    D --> F
    F -- Không hợp lệ --> G --> Z
    F -- Hợp lệ --> H --> DB2
    DB2 -- Thành công --> I --> Z([Kết thúc])
    DB2 -- Lỗi --> J --> Z
```

---

## Ghi chú kỹ thuật

| Tầng | Class | Method |
|---|---|---|
| View | `CauHinhGioiHanDonViewFXMLController` | `onLuuCauHinh()` |
| Presenter | `CauHinhGioiHanPresenter` | `luuCauHinhTuyChinh(str)` |
| Service | `CauHinhGioiHanService` | `luuCauHinh(ngay, gioiHan)` |
| DAO | `CauHinhGioiHanDAO` | `luuCauHinh(ngay, gioiHan)` |
| DB | `NANGLUCSANXUAT` | `MERGE INTO` (upsert) |
| DB Trigger | `TRG_KIEMSOAT_CONGSUAT_DONHANG` | Auto-enforce khi INSERT CTDONHANG |
