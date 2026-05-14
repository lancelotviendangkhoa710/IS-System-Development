# BÁO CÁO: XỬ LÝ ĐỒNG THỜI TRONG HỆ THỐNG QUẢN LÝ TIỆM BÁNH

**Hệ QTCSDL:** Oracle 12c+  
**Hệ thống:** Bakery Management System  
**Kiến trúc:** MVP — Java 21 / JavaFX / Oracle JDBC + Stored Procedures

---

## I. TỔNG QUAN CÁC VẤN ĐỀ XỬ LÝ ĐỒNG THỜI

Hệ thống Bakery có nhiều người dùng (thu ngân, thủ kho, quản lý) thao tác đồng thời trên cùng dữ liệu. Các bảng có nguy cơ cao:

| Bảng | Lý do rủi ro |
|---|---|
| `SANPHAM` | Nhiều thu ngân bán cùng lúc, trừ `SOLUONGTON` |
| `NGUYENLIEU` | Thủ kho nhập + sản xuất xuất cùng lúc |
| `DONDATHANG` | Nhiều NV cập nhật trạng thái đơn cùng lúc |
| `KHACHHANG` | Cộng điểm tích lũy khi thanh toán đồng thời |

---

## II. VẤN ĐỀ 1: LOST UPDATE (Cập nhật bị mất)

### Mô tả
Hai giao dịch cùng đọc một giá trị, cùng sửa, và ghi đè lên nhau khiến một thao tác bị mất hoàn toàn.

### Kịch bản thực tế
**Bối cảnh:** Hai thu ngân T1 và T2 cùng bán bánh `MASP = 5` (bánh kem dâu), kho còn **10 cái**.  
**Procedure liên quan:** `PROC_TAODONHANG` → trigger `TRG_TRUKHO_DONHANG` → `UPDATE SANPHAM SET SOLUONGTON`.

| Thời điểm | T1 (Thu ngân A — bán 7 cái) | T2 (Thu ngân B — bán 6 cái) |
|---|---|---|
| t1 | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP=5` → **10** | |
| t2 | | `SELECT SOLUONGTON FROM SANPHAM WHERE MASP=5` → **10** |
| t3 | Kiểm tra: 10 ≥ 7 ✔ → tạo đơn, `UPDATE SANPHAM SET SOLUONGTON = 10 - 7 = 3` | |
| t4 | `COMMIT` → SOLUONGTON = **3** | |
| t5 | | Kiểm tra: 10 ≥ 6 ✔ (đọc giá trị cũ!) → `UPDATE SANPHAM SET SOLUONGTON = 10 - 6 = 4` |
| t6 | | `COMMIT` → SOLUONGTON = **4** ← **SAI! Phải là -3** |

**Hậu quả:** Hệ thống bán **13 cái** trong khi kho chỉ có **10 cái** — tồn kho âm!

### Giải pháp áp dụng trong dự án

**Cơ chế: `SELECT ... FOR UPDATE` (Pessimistic Lock)**

```sql
-- Trong PROC_TAODONHANG (proc_order_cud.sql — dòng 42-46)
SELECT SOLUONGTON, TENSP
INTO V_TONKHO, V_TENSP
FROM SANPHAM
WHERE MASP = r.MASP
FOR UPDATE;  -- Khoá hàng, T2 phải chờ đến khi T1 COMMIT/ROLLBACK
```

**Timeline sau khi áp dụng:**

| Thời điểm | T1 | T2 |
|---|---|---|
| t1 | `SELECT ... FOR UPDATE MASP=5` → Lock hàng, đọc **10** | |
| t2 | | `SELECT ... FOR UPDATE MASP=5` → **BỊ BLOCK**, chờ T1 |
| t3 | Kiểm tra OK, `UPDATE SOLUONGTON = 3`, `COMMIT` | |
| t4 | Lock giải phóng | T2 được cấp lock, đọc **3** |
| t5 | | Kiểm tra: 3 < 6 → `RAISE_APPLICATION_ERROR` — **Từ chối bán** |

**Kết quả:** Tồn kho chính xác = **3**, T2 thông báo lỗi "Chỉ còn 3 cái, không đủ 6 cái yêu cầu."

---

## III. VẤN ĐỀ 2: UNCOMMITTED READ (Đọc dữ liệu chưa commit — Dirty Read)

### Mô tả
T2 đọc dữ liệu mà T1 đã sửa nhưng chưa `COMMIT`. Nếu T1 `ROLLBACK`, T2 đang thao tác trên dữ liệu "ma".

### Kịch bản thực tế
**Bối cảnh:** T1 đang nhập kho nguyên liệu bột mì `MANL = 3` (thêm 50kg), T2 đang kiểm tra tồn để quyết định sản xuất.

| Thời điểm | T1 (Thủ kho — nhập 50kg bột) | T2 (Tổ trưởng — kiểm tra để sản xuất) |
|---|---|---|
| t1 | Bắt đầu transaction | |
| t2 | `UPDATE NGUYENLIEU SET SOLUONGTONTONG = 20 + 50 = 70` (chưa COMMIT) | |
| t3 | | `SELECT SOLUONGTONTONG FROM NGUYENLIEU WHERE MANL=3` → đọc **70** (dữ liệu bẩn!) |
| t4 | | Quyết định sản xuất 100 ổ bánh (cần 60kg), xuất kho thành công |
| t5 | Phát hiện nhập nhầm NCC → `ROLLBACK` → SOLUONGTONTONG trở lại **20** | |
| t6 | | Kho thực tế chỉ còn **-40kg** — **DỮ LIỆU KHÔNG NHẤT QUÁN** |

### Giải pháp áp dụng trong dự án

**Oracle mặc định: `READ COMMITTED` — Không bao giờ đọc uncommitted data.**

```sql
-- Oracle mặc định là READ COMMITTED, không cần cấu hình thêm
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- T2 SELECT tại t3 sẽ thấy SOLUONGTONTONG = 20 (giá trị trước UPDATE của T1)
-- vì T1 chưa COMMIT → Oracle đọc từ UNDO segment
SELECT SOLUONGTONTONG FROM NGUYENLIEU WHERE MANL = 3;
-- → Kết quả: 20 (đúng, không bị "bẩn")
```

**Thêm: `FOR UPDATE` trong `PROC_XUATKHOSANXUAT` (dòng 157-161):**

```sql
SELECT SOLUONGTONTONG
INTO V_TONGTON
FROM NGUYENLIEU
WHERE MANL = REC.MANL
FOR UPDATE;  -- Đảm bảo đọc giá trị committed mới nhất trước khi xuất
```

---

## IV. VẤN ĐỀ 3: NON-REPEATABLE READ (Đọc không lặp lại được)

### Mô tả
T1 đọc cùng một dòng hai lần trong cùng transaction nhưng nhận kết quả khác nhau vì T2 đã `UPDATE` và `COMMIT` ở giữa.

### Kịch bản thực tế
**Bối cảnh:** Quản lý lập báo cáo điểm tích lũy của `MAKH = 101`. Đồng thời thu ngân thanh toán đơn cho khách này.

| Thời điểm | T1 (Quản lý — lập báo cáo) | T2 (Thu ngân — thanh toán 500.000đ) |
|---|---|---|
| t1 | `SELECT DIEMTICHLUY FROM KHACHHANG WHERE MAKH=101` → **200 điểm** | |
| t2 | | `UPDATE KHACHHANG SET DIEMTICHLUY = 200 + 50 = 250 WHERE MAKH=101` |
| t3 | | `COMMIT` |
| t4 | `SELECT DIEMTICHLUY FROM KHACHHANG WHERE MAKH=101` → **250 điểm** | |
| t5 | **250 ≠ 200** — Báo cáo không nhất quán! | |

**Hậu quả:** Báo cáo tổng hợp tính điểm sai, có thể tặng quà sai hạng thành viên.

### Giải pháp áp dụng trong dự án

**Cơ chế: `SERIALIZABLE` Isolation Level cho transaction báo cáo.**

```sql
-- Áp dụng cho session báo cáo/thống kê (ThongKeDAO.java)
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- T1 lần 1: SELECT DIEMTICHLUY → 200 (snapshot tại thời điểm bắt đầu)
SELECT DIEMTICHLUY FROM KHACHHANG WHERE MAKH = 101; -- → 200

-- T2 COMMIT (UPDATE DIEMTICHLUY = 250) xảy ra ở giữa

-- T1 lần 2: vẫn thấy snapshot cũ → 200 (KHÔNG bị ảnh hưởng bởi T2)
SELECT DIEMTICHLUY FROM KHACHHANG WHERE MAKH = 101; -- → 200 (nhất quán!)

COMMIT; -- Giải phóng snapshot
```

**Trong Java (ThongKeDAO):**
```java
conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
```

---

## V. VẤN ĐỀ 4: PHANTOM READ (Đọc ma)

### Mô tả
T1 đọc một tập dòng theo điều kiện, T2 `INSERT` dòng mới thoả điều kiện đó và `COMMIT`, T1 đọc lại thấy dòng "ma" xuất hiện.

### Kịch bản thực tế
**Bối cảnh:** Quản lý kiểm tra danh sách đơn "Chờ xác nhận" để phân công sản xuất. Thu ngân tạo đơn mới cùng lúc.

| Thời điểm | T1 (Quản lý — đang phân công) | T2 (Thu ngân — tạo đơn mới) |
|---|---|---|
| t1 | `SELECT COUNT(*) FROM DONDATHANG WHERE MATRANGTHAI=1` → **5 đơn** | |
| t2 | *(đang xử lý phân công cho 5 đơn...)* | `PROC_TAODONHANG(...)` → INSERT đơn mới MATRANGTHAI=1 |
| t3 | | `COMMIT` |
| t4 | `SELECT COUNT(*) FROM DONDATHANG WHERE MATRANGTHAI=1` → **6 đơn** | |
| t5 | **Xuất hiện đơn thứ 6 không có trong kế hoạch ban đầu!** | |

**Hậu quả:** Đơn hàng thứ 6 bị bỏ sót → trễ hạn giao bánh cho khách.

### Giải pháp áp dụng trong dự án

**Cơ chế 1: `SERIALIZABLE` — Snapshot cố định không thấy INSERT mới**

```sql
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Lần 1: snapshot cố định
SELECT MADON, NGAYGIONHANBANH
FROM DONDATHANG WHERE MATRANGTHAI = 1; -- → 5 đơn

-- T2 INSERT + COMMIT → không ảnh hưởng snapshot

-- Lần 2: vẫn thấy đúng 5 đơn (Phantom bị chặn)
SELECT COUNT(*) FROM DONDATHANG WHERE MATRANGTHAI = 1; -- → 5
```

**Cơ chế 2: Optimistic Lock qua cột `PHIENBAN` (bảng `DONDATHANG`, `NGUYENLIEU`)**

```sql
-- Khi cập nhật, kiểm tra version trước
UPDATE DONDATHANG
SET MATRANGTHAI = :newStatus,
    PHIENBAN = PHIENBAN + 1
WHERE MADON = :madon
  AND PHIENBAN = :expectedVersion;
-- Nếu ai đó đã sửa → SQL%ROWCOUNT = 0 → báo lỗi xung đột → retry
```

---

## VI. VẤN ĐỀ 5: DEADLOCK

### Mô tả
T1 giữ Lock A, chờ Lock B. T2 giữ Lock B, chờ Lock A. Hai giao dịch chờ nhau vô tận.

### Kịch bản thực tế
**Bối cảnh:** T1 xuất kho làm bánh kem dâu (công thức: bột mì `MANL=3` → bơ `MANL=7`). T2 xuất kho làm bánh mousse (công thức: bơ `MANL=7` → bột mì `MANL=3`). Cả hai dùng `PROC_XUATKHOSANXUAT` với cursor `C_CONGTHUC` duyệt `FOR UPDATE`.

| Thời điểm | T1 (Sản xuất bánh kem dâu) | T2 (Sản xuất bánh mousse) |
|---|---|---|
| t1 | `FOR UPDATE` trên `NGUYENLIEU MANL=3` → **Lock bột mì** ✔ | |
| t2 | | `FOR UPDATE` trên `NGUYENLIEU MANL=7` → **Lock bơ** ✔ |
| t3 | Tiếp theo cần `FOR UPDATE MANL=7` → **BLOCK** (T2 giữ) | |
| t4 | | Tiếp theo cần `FOR UPDATE MANL=3` → **BLOCK** (T1 giữ) |
| t5 | **DEADLOCK!** Cả hai chờ nhau mãi mãi | |

### Giải pháp 3 tầng

#### Tầng 1: Lock Ordering — Luôn khóa theo MANL tăng dần

```sql
-- Sửa cursor C_CONGTHUC trong PROC_XUATKHOSANXUAT
-- Thêm ORDER BY C.MANL ASC để mọi transaction lock theo cùng thứ tự
CURSOR C_CONGTHUC IS
    SELECT C.MANL, N.TENNL, (C.SOLUONGTIEUHAO * P_SOLUONGSANXUAT) AS TONG_CAN_DUNG
    FROM CONGTHUC C
    JOIN NGUYENLIEU N ON C.MANL = N.MANL
    WHERE C.MASP = P_MASP
    ORDER BY C.MANL ASC;  -- ← Cả T1 và T2 đều lock MANL=3 trước → Hết deadlock
```

**Timeline sau fix:**

| Thời điểm | T1 (Bánh kem dâu) | T2 (Bánh mousse) |
|---|---|---|
| t1 | `FOR UPDATE MANL=3` → **Lock** ✔ | |
| t2 | | `FOR UPDATE MANL=3` → **BLOCK** (chờ T1) |
| t3 | `FOR UPDATE MANL=7` → **Lock** ✔ → Xong → `COMMIT` | |
| t4 | | Được cấp MANL=3, `FOR UPDATE MANL=7` ✔ → Xong → `COMMIT` |

#### Tầng 2: `FOR UPDATE NOWAIT` — Từ chối ngay thay vì chờ mãi

```sql
SELECT SOLUONGTONTONG
INTO V_TONGTON
FROM NGUYENLIEU
WHERE MANL = REC.MANL
FOR UPDATE NOWAIT;  -- ORA-00054 nếu bị lock → xử lý ngay, không treo app

-- Hoặc timeout có kiểm soát
FOR UPDATE WAIT 5;  -- Chờ tối đa 5 giây
```

#### Tầng 3: Oracle Auto-Detect (ORA-00060) + EXCEPTION BLOCK

```sql
-- Oracle tự phát hiện deadlock, chọn victim (transaction ít UNDO hơn)
-- Victim nhận ORA-00060 → procedure bắt và rollback sạch

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(
            PKG_ERROR_CODES.ERR_HUY_XUAT_KHO,
            'Loi he thong khi xuat kho san xuat: ' || SQLERRM
            -- SQLERRM = "ORA-00060: deadlock detected while waiting..."
        );
```

---

## VII. DEADLOCK_PRIORITY — Khái niệm và áp dụng

> **Lưu ý:** `DEADLOCK_PRIORITY` là cú pháp SQL Server (`SET DEADLOCK_PRIORITY`). Oracle không dùng cú pháp này. Trong Oracle, victim được chọn tự động dựa trên lượng UNDO đã dùng.

**SQL Server syntax (minh họa khái niệm):**
```sql
-- Transaction quan trọng hơn — ưu tiên sống sót khi deadlock
SET DEADLOCK_PRIORITY HIGH;   -- Giá trị: LOW(-5), NORMAL(0), HIGH(5)
BEGIN TRANSACTION;
    UPDATE DONDATHANG SET MATRANGTHAI = 2 WHERE MADON = 1001;
COMMIT;

-- Transaction ít quan trọng — sẽ bị chọn làm victim
SET DEADLOCK_PRIORITY LOW;
BEGIN TRANSACTION;
    UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 1 WHERE MASP = 5;
COMMIT;
-- → Khi deadlock: transaction DEADLOCK_PRIORITY LOW bị ROLLBACK trước
```

**Trong Oracle — tương đương:**
```sql
-- Giữ transaction "nhỏ và nhanh" = ít UNDO = ít bị chọn làm victim
-- Hoặc dùng DBMS_LOCK để kiểm soát thứ tự acquisition thủ công
DBMS_LOCK.REQUEST(lockhandle => v_handle, lockmode => DBMS_LOCK.X_MODE, timeout => 5);
```

---

## VIII. BẢNG TỔNG HỢP

| Vấn đề | Kịch bản trong dự án | Cơ chế giải quyết | Isolation Level |
|---|---|---|---|
| **Lost Update** | 2 thu ngân bán cùng 1 sản phẩm | `SELECT ... FOR UPDATE` trong `PROC_TAODONHANG` | READ COMMITTED |
| **Uncommitted Read** | Thủ kho nhập chưa commit, sản xuất đọc | Oracle default — không bao giờ đọc uncommitted | READ COMMITTED (default) |
| **Non-repeatable Read** | Quản lý đọc điểm KH 2 lần, thu ngân sửa ở giữa | `SERIALIZABLE` — snapshot cố định | SERIALIZABLE |
| **Phantom Read** | Quản lý đếm đơn, thu ngân thêm đơn ở giữa | `SERIALIZABLE` + Optimistic Lock (`PHIENBAN`) | SERIALIZABLE |
| **Deadlock** | Xuất kho 2 NL theo thứ tự ngược nhau | Lock Ordering (ORDER BY MANL ASC) + NOWAIT + ORA-00060 | READ COMMITTED |

---

## IX. KẾT LUẬN

Hệ thống đã áp dụng đầy đủ cơ chế kiểm soát đồng thời:

1. **`FOR UPDATE`** — Khóa bi quan ngăn Lost Update (`PROC_TAODONHANG`, `PROC_XUATKHOSANXUAT`, `PROC_XUATNGUYENLIEUHONG`)
2. **`READ COMMITTED` (Oracle default)** — Loại bỏ Dirty Read không cần cấu hình
3. **`SERIALIZABLE`** — Dùng cho module báo cáo (`ThongKeDAO`) để snapshot nhất quán
4. **Lock Ordering (ORDER BY MANL ASC)** — Ngăn Deadlock khi xuất kho nhiều nguyên liệu
5. **`NOWAIT` / `WAIT n`** — Phát hiện xung đột sớm, tránh treo ứng dụng
6. **Oracle ORA-00060 Auto-Detect** — Bắt Deadlock trong EXCEPTION block của mọi procedure
7. **Optimistic Lock (`PHIENBAN`)** — Cột version trên `DONDATHANG` và `NGUYENLIEU` phát hiện xung đột cập nhật
