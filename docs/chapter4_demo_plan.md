# Plan Demo Chương 4 — Truy xuất đồng thời

> Ngày: 2026-05-22  
> Trạng thái: **APPROVED — Chờ thực thi**

---

## Nguyên tắc bất biến

- Java **chỉ gọi procedure có sẳn và chỉnh sửa lại để nó có bug và fix bug  3 trường hợp đề dùng serilizable** — không `setAutoCommit`, không `setTransactionIsolation`
- Toggle bug/fix = **comment/bỏ comment 1 dòng trong SQL procedure** trước mặt giáo viên
- Procedure tự quản lý `COMMIT / ROLLBACK`

---


## Quy trình demo trước giáo viên
