# UC56 — Lập phiếu thu chi

| Thành phần | Nội dung |
|:---|:---|
| **Tên Use-case** | Lập phiếu thu chi |
| **Mô tả** | Ghi nhận các khoản tiền ra/vào két sắt không phát sinh trực tiếp từ giao dịch bán bánh trên POS (như trả tiền nhà cung cấp, rút tiền mặt nộp ngân hàng). |
| **Actors** | Thu ngân, Quản lý |
| **Tiền điều kiện** | Nhân viên đang trong ca làm việc đã mở. |
| **Hậu điều kiện** | Phiếu thu/chi được tạo và lưu trữ. Tiền trong két được cập nhật tương ứng. |

## Luồng sự kiện chính

1. Nhân viên chọn chức năng Quản lý Sổ quỹ / Thu chi.
2. Hệ thống hiển thị danh sách các phiếu thu chi gần đây và nút Tạo mới.
3. Nhân viên nhấn Tạo mới phiếu thu/chi.
4. Hệ thống hiển thị form nhập liệu bao gồm: Loại phiếu (Thu hoặc Chi), Phân loại lý do, Số tiền, và Ghi chú chi tiết.
5. Nhân viên điền đầy đủ thông tin và nhấn Lưu.
6. Hệ thống xác thực tính hợp lệ của dữ liệu (số tiền > 0, đã chọn phân loại).
7. Hệ thống tiến hành ghi nhận phiếu thu/chi vào cơ sở dữ liệu.
8. Hệ thống thông báo thành công và cập nhật lại danh sách phiếu thu chi hiện tại.

## Luồng sự kiện phụ

- Bước 4a: Quản lý muốn thêm loại lý do mới không có trong danh sách phân loại, hệ thống cho phép chọn "Thêm loại thu chi mới" (chuyển hướng sang quy trình thêm danh mục).

## Luồng sự kiện lỗi

- Bước 6: Số tiền nhập vào không hợp lệ hoặc thiếu thông tin bắt buộc, hệ thống hiển thị thông báo lỗi ngay trên form.
- Bước 7: Trong trường hợp tạo phiếu chi mà số tiền chi lớn hơn tổng quỹ hiện tại, hệ thống báo lỗi quỹ không đủ (nếu có cấu hình chặn âm quỹ) hoặc cảnh báo quỹ âm.

## Activity Diagram

```mermaid
flowchart TD
    subgraph "Nhân viên"
        A([Chọn Quản lý Thu chi])
        B([Nhấn Tạo mới])
        C([Nhập thông tin phiếu])
        D([Nhấn Lưu])
    end

    subgraph "Hệ thống"
        E[Hiển thị sổ quỹ]
        F[Hiển thị form nhập liệu]
        G{Validate thông tin}
        G1[Báo lỗi nhập liệu]
        H{Kiểm tra tồn quỹ (nếu chi)}
        H1[Báo lỗi quỹ không đủ]
        I[Tạo mới phiếu thu chi]
        J[Thông báo thành công]
        K[Làm mới danh sách]
    end

    subgraph "CSDL"
        DB[(Lưu phiếu thu chi)]
    end

    A --> E --> B --> F --> C --> D --> G
    G -->|Không hợp lệ| G1
    G -->|Hợp lệ| H
    H -->|Không đủ quỹ| H1
    H -->|Đủ quỹ / Phiếu thu| I
    I -->|Thành công| DB --> J --> K
```
