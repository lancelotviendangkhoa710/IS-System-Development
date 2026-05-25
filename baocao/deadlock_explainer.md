# §4.4 Deadlock Demo — Giải thích luồng & cách fix

> File này giải thích **tại sao deadlock xảy ra** (BUG mode) và **tại sao đổi `C_BUG_MODE = FALSE` thì fix được** (FIX mode).
> Procedure liên quan: [`PROC_XUATKHOSANXUAT`](file:///D:/Clone/database/05_procedures/cud/proc_stock_cud.sql#L157)

---

## 1. Bối cảnh — Bánh nào cần nguyên liệu nào?

Giả sử hệ thống có 2 loại bánh, mỗi loại dùng chung **Đường (NL_5)** và **Bột mì (NL_3)**:

| Bánh | Nguyên liệu | Lượng tiêu hao / cái |
|---|---|---|
| **Bánh bông lan** (SP_A) | NL_5 Đường | **200g** (cao hơn) |
| | NL_3 Bột mì | 100g |
| **Bánh mì** (SP_B) | NL_3 Bột mì | **150g** (cao hơn) |
| | NL_5 Đường | 50g |

---

## 2. BUG Mode — Luồng deadlock (`C_BUG_MODE = TRUE`)

### Nguyên nhân gốc rễ: `ORDER BY SOLUONGTIEUHAO DESC`

Procedure duyệt nguyên liệu **theo lượng tiêu hao giảm dần**. Vì 2 bánh có lượng tiêu hao **đảo ngược nhau**, thứ tự lock cũng đảo ngược:

| | Phiên 1 — Bánh bông lan | Phiên 2 — Bánh mì |
|---|---|---|
| **Lock thứ 1** | NL_5 Đường (200g > 100g) | NL_3 Bột mì (150g > 50g) |
| **Sleep 6s** | 💤 | 💤 |
| **Lock thứ 2** | NL_3 Bột mì ← **bị P2 giữ** | NL_5 Đường ← **bị P1 giữ** |

### Timeline chi tiết

```
T=0s    P1 lock NL_5 (Đường)       ✅ acquired
T=0.1s  P2 lock NL_3 (Bột mì)     ✅ acquired

T=6s    P1 thức dậy, thử lock NL_3 ⏳ đang đợi P2
T=6.1s  P2 thức dậy, thử lock NL_5 ⏳ đang đợi P1
        ↑──────────────────────────────────────────┐
        │            CIRCULAR WAIT                  │
        │  P1 chờ P2 giải phóng NL_3               │
        │  P2 chờ P1 giải phóng NL_5               │
        └───────────────────────────────────────────┘

T=11s   P1 WAIT 5s hết → LOCK_TIMEOUT ❌ → ROLLBACK
        P2 vẫn chờ NL_5 (P1's connection pool giữ advisory lock)
T=11.1s P2 WAIT 5s hết → LOCK_TIMEOUT ❌ → ROLLBACK

→ CẢ 2 PHIÊN THẤT BẠI — Outcome B ✅
```

### Tại sao dùng DBMS_LOCK thay FOR UPDATE?

| Cơ chế | Oracle detect deadlock? | Kết quả |
|---|---|---|
| `FOR UPDATE WAIT 5` | ✅ Có (synchronous, <1s) | Oracle chọn **1 victim** → `ORA-00060` → victim rollback, kia **thành công** → Outcome A |
| `DBMS_LOCK advisory` | ❌ Không phát hiện | Cả 2 phải **tự timeout** sau `C_WAIT_SEC` giây → cả 2 fail → **Outcome B** |

> **Advisory lock** = lock do ứng dụng tự quản lý, Oracle không theo dõi vào deadlock graph.
> Lock này tồn tại trên JDBC session cho đến khi session đóng (không bị ROLLBACK giải phóng).
> Vì thế P2 vẫn bị block dù P1 đã ROLLBACK → P2 cũng timeout → Outcome B.

---

## 3. FIX Mode — Tại sao `C_BUG_MODE = FALSE` fix được? (`FOR UPDATE + ORDER BY ASC`)

### Nguyên lý: **Lock Ordering** (Dijkstra, 1965)

> Nếu tất cả các tiến trình **luôn acquire locks theo cùng một thứ tự**, vòng chờ tròn **không thể hình thành**.

### Timeline FIX mode

```
ORDER BY C.MANL ASC → NL_3 (Bột mì) luôn được lock trước NL_5 (Đường)

T=0s    P1 lock NL_3 (Bột mì) [FOR UPDATE]     ✅
T=0.1s  P2 thử lock NL_3 (Bột mì) [FOR UPDATE] ⏳ đợi P1 release

T=6s    P1 thức dậy, lock NL_5 (Đường)         ✅ (P2 không giữ)
        P1 xử lý xong → COMMIT → RELEASE NL_3, NL_5

T=6s+   P2 nhận được NL_3 (P1 đã release) → tiếp tục
        P2 lock NL_5 → xử lý → COMMIT ✅

→ P1 thành công → P2 thành công (tuần tự, không deadlock) ✅
```

### So sánh trực quan

```
BUG mode (ORDER BY DESC):           FIX mode (ORDER BY ASC):

P1: NL_5 ──→ NL_3                  P1: NL_3 ──→ NL_5
              ↑  ↓                              ↓  (sequential)
P2: NL_3 ──→ NL_5                  P2: NL_3 ──→ NL_5
                                        (chờ P1 release NL_3 trước)

Vòng chờ tròn → DEADLOCK           Thứ tự nhất quán → AN TOÀN
```

---

## 4. Toggle duy nhất — chỉ đổi 1 dòng

```sql
-- Trong PROC_XUATKHOSANXUAT (dòng ~167):
C_BUG_MODE CONSTANT BOOLEAN := TRUE;   -- deadlock demo (Outcome B)
C_BUG_MODE CONSTANT BOOLEAN := FALSE;  -- lock ordering fix (an toàn)
```

Khi đổi flag, tất cả thay đổi sau xảy ra **tự động**:

| | `TRUE` (BUG) | `FALSE` (FIX) |
|---|---|---|
| ORDER BY | `SOLUONGTIEUHAO DESC` | `MANL ASC` |
| Lock mechanism | `DBMS_LOCK` advisory | `SELECT ... FOR UPDATE` |
| Đọc tồn kho | `SELECT` riêng (sau DBMS_LOCK) | Gộp vào `FOR UPDATE` |
| Kết quả demo | Cả 2 LOCK_TIMEOUT ❌ | Tuần tự, cả 2 thành công ✅ |

> Sau khi đổi flag, phải **compile lại procedure** lên Oracle bằng cách chạy lại sqlplus script hoặc tự copy-paste vào SQL Developer.

---

## 5. Cleanup sau demo BUG mode

Advisory locks **không tự giải phóng** khi ROLLBACK — chúng sống trên JDBC pooled connection cho đến khi session thực sự đóng.

```
Option 1 (đơn giản): Restart Java app
  → Connection pool close → Oracle tự release tất cả advisory locks

Option 2 (không restart): Chạy cleanup script qua sqlplus
  → DBMS_LOCK.ALLOCATE_UNIQUE + RELEASE cho từng MANL
```
