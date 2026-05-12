# CUD Refactor Audit — JavaFX + Oracle

Ngày quét: 2026-05-12

## 1) Trùng lặp Procedure trong `database/`

| Object | Số bản định nghĩa | Vị trí |
|---|---:|---|
| `PROC_HUYDON_HOANCOC` | 2 | `database/05_procedures/cud/proc_order_cud.sql`, `database/05_procedures/proc_cancel_refund.sql` |

Rủi ro: Oracle sẽ compile bản chạy sau cùng, dễ gây lệch tham số/hành vi giữa môi trường.

## 2) Procedure có trong DB script nhưng chưa thấy Java call trực tiếp

Danh sách tiêu biểu:
- `PROC_TAOHOADON`
- `PROC_SUA_VAITRO`, `PROC_XOA_VAITRO`
- `PROC_THEM_COTBANH`, `PROC_CAPNHAT_COTBANH`, `PROC_XOA_COTBANH`
- `PROC_THEM_KICHCOBANH`, `PROC_CAPNHAT_KICHCOBANH`, `PROC_XOA_KICHCOBANH`
- `PROC_THEM_NHANBANH`, `PROC_CAPNHAT_NHANBANH`, `PROC_XOA_NHANBANH`
- `PROC_THEM_KIEUTRANGTRI`, `PROC_CAPNHAT_KIEUTRANGTRI`, `PROC_XOA_KIEUTRANGTRI`
- `PROC_THEM_PHUONGTHUCTT`, `PROC_SUA_PHUONGTHUCTT`, `PROC_XOA_PHUONGTHUCTT`
- `PROC_CAPNHAT_QUYEN_CHI_TIET`
- `PROC_KHOIPHUCDULIEU`

Lưu ý: đây là quét chuỗi call từ Java source; có thể vẫn được gọi gián tiếp qua procedure khác.

## 3) Tình trạng CUD ở Java DAO

- Một số module đã dùng `CallableStatement` đúng hướng (đơn hàng, nhập kho, xuất kho, nhân sự, phân quyền).
- Vẫn còn DAO dùng `INSERT/UPDATE/DELETE` trực tiếp bằng `PreparedStatement` cho CUD.

Khuyến nghị rollout:
1. Chốt danh sách CUD “core business change” ưu tiên cao (đơn hàng, kho, nhân sự, thu chi).
2. Mỗi nhóm bảng chuẩn hóa theo 1 procedure CUD hoặc bộ procedure theo hành động.
3. Java tầng DAO chỉ giữ `CallableStatement`.

## 4) Gap hiện tại liên quan task

- `PHIEUNHAPKHO`: đã có `PROC_HUYPHIEUNHAPKHO` và đã dùng ở controller.
- `PHIEUXUATKHO`: chưa có procedure hủy phiếu xuất tương ứng trong script hiện tại.
  - UI đã giới hạn nút xóa theo RBAC Admin/Quản lý.
  - Muốn bật xóa thực sự cần bổ sung procedure hủy có rollback tồn kho chuẩn.
