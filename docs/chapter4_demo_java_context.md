# Context — Bước tiếp theo: Demo Java cho Chương 4

## Mục tiêu
Tạo 4 Java demo class minh họa 4 lỗi truy xuất đồng thời.
**Java trung lập hoàn toàn** — không setTransactionIsolation, không retry.
Toàn bộ cơ chế bảo vệ nằm trong DB procedure (comment/bỏ comment).

---

## Stored Procedures đã có (`D:\Clone\database\demo\`)

| File | Procedure | Tham số |
|---|---|---|
| `proc_taodonhang_bug.sql` | `PROC_TAODONHANG_BUG` | `(P_NGAYGIONHANBANH, P_MAKH, P_MANV_LAP, P_MATRANGTHAI, P_TIENDACOC, P_HINHTHUCNHAN, P_DIACHIGIAO, P_JSONCHITIET CLOB, P_MADON_OUT OUT)` |
| `proc_nonrepeatableread_demo.sql` | `PROC_XACNHAN_GIA_DEMO` | `(P_MASP IN, P_GIA_LAN1 OUT NUMBER, P_GIA_LAN2 OUT NUMBER)` |
| `proc_phantomread_demo.sql` | `PROC_KIEMKE_TONKHO_DEMO` | `(P_MANL IN, P_TONG_LAN1 OUT NUMBER, P_TONG_LAN2 OUT NUMBER)` |
| `proc_deadlock_demo.sql` | `PROC_CAPNHAT_MUCTON_DEMO` | `(P_MANL_1 IN, P_MUCTON_1 IN, P_MANL_2 IN, P_MUCTON_2 IN)` |

### Cơ chế toggle Bug/Fix (trong mỗi procedure)

| Procedure | Dòng toggle |
|---|---|
| `PROC_TAODONHANG_BUG` | `-- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';` |
| `PROC_XACNHAN_GIA_DEMO` | `-- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';` |
| `PROC_KIEMKE_TONKHO_DEMO` | `-- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';` |
| `PROC_CAPNHAT_MUCTON_DEMO` | Khối `-- IF V_MANL_A > V_MANL_B THEN ... swap ... END IF` (7 dòng) |

---

## Cấu trúc Java cần tạo

```
D:\Clone\src\main\java\com\bakery\demo\
├── LostUpdateDemo.java
├── NonRepeatableReadDemo.java
├── PhantomReadDemo.java
└── DeadlockDemo.java
```

- Dùng `DBConnect.getConnection()` (package `com.bakery.utils`)
- Dùng `CallableStatement` để gọi stored procedure
- 2 thread mỗi demo, timing bằng `Thread.sleep()`

---

## Thiết kế từng demo

### 4.1 LostUpdateDemo.java

**Kịch bản:** Hai thu ngân cùng bán Bánh Bông Lan Trứng (MASP=1001), mỗi người 3 cái. Tồn kho = 5.

```
Thread thuNganA → PROC_TAODONHANG_BUG(..., JSON=[{maSP:1001, soLuong:3, donGia:50000}])
Thread thuNganB → PROC_TAODONHANG_BUG(..., JSON=[{maSP:1001, soLuong:3, donGia:50000}])
thuNganA.start()
Thread.sleep(500)   // B bắt đầu trong khi A đang delay (2–4s) bên trong procedure
thuNganB.start()
```

**Sau khi join:** `SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1001`
- Bug: `-1` → in "BUG: Tồn kho âm!"
- Fix: `2` → in "OK"

**Reset data trước khi chạy:**
```sql
UPDATE SANPHAM SET SOLUONGTON = 5 WHERE MASP = 1001; COMMIT;
```

---

### 4.2 NonRepeatableReadDemo.java

**Kịch bản:** Thu ngân đọc GIABAN 2 lần trong cùng giao dịch. Quản lý UPDATE GIABAN giữa 2 lần đọc.

```
Thread thuNgan → PROC_XACNHAN_GIA_DEMO(1001, :gia1, :gia2)
Thread quanLy  → UPDATE SANPHAM SET GIABAN=180000 WHERE MASP=1001; COMMIT;

thuNgan.start()
Thread.sleep(1000)  // quản lý cập nhật khi thu ngân đang delay bên trong procedure
quanLy.start()
```

**Sau khi join:** So sánh `P_GIA_LAN1` vs `P_GIA_LAN2`
- Bug: `150000 ≠ 180000` → in "BUG: Non-repeatable Read"
- Fix: `150000 = 150000` → in "OK"

**Reset data:**
```sql
UPDATE SANPHAM SET GIABAN = 150000 WHERE MASP = 1001; COMMIT;
```

---

### 4.3 PhantomReadDemo.java

**Kịch bản:** Quản lý kho truy vấn SUM(SOLUONGCONLAI) 2 lần. Nhân viên INSERT lô mới giữa 2 lần.

```
Thread quanLyKho  → PROC_KIEMKE_TONKHO_DEMO(5, :tong1, :tong2)
Thread nhanVienKho → INSERT INTO CTPHIEUNHAP(..., MANL=5, SOLUONGCONLAI=20,...); COMMIT;

quanLyKho.start()
Thread.sleep(1000)  // nhân viên insert khi quản lý đang delay
nhanVienKho.start()
```

**Sau khi join:** So sánh `P_TONG_LAN1` vs `P_TONG_LAN2`
- Bug: `100 ≠ 120` → in "BUG: Phantom Read"
- Fix: `100 = 100` → in "OK"

**Reset data:**
```sql
DELETE FROM CTPHIEUNHAP WHERE MAPN = 10 AND MANL = 5; COMMIT;
-- (xóa lô demo đã insert)
```

> **Lưu ý:** MAPN=10 phải là phiếu nhập tồn tại. Kiểm tra bảng PHIEUNHAPKHO trước.

---

### 4.4 DeadlockDemo.java

**Kịch bản:** Hai nhân viên cập nhật MUCTONANTOAN ngược thứ tự.

```
Thread threadA → PROC_CAPNHAT_MUCTON_DEMO(1001, 50, 1002, 40)
                 // A khóa 1001 trước → chờ 1002

Thread threadB → PROC_CAPNHAT_MUCTON_DEMO(1002, 55, 1001, 45)
                 // B khóa 1002 trước → chờ 1001 → DEADLOCK

threadA.start()
Thread.sleep(200)  // B bắt đầu khi A đã khóa 1001 và đang delay
threadB.start()
```

**Sau khi join:** `SELECT MUCTONANTOAN FROM NGUYENLIEU WHERE MANL IN (1001,1002)`
- Bug: Thread bị ORA-00060 → in "BUG: Deadlock! Thread B rollback"
- Fix: Cả hai thành công → in "OK"

**Reset data:**
```sql
UPDATE NGUYENLIEU SET MUCTONANTOAN = 20 WHERE MANL = 1001;
UPDATE NGUYENLIEU SET MUCTONANTOAN = 15 WHERE MANL = 1002;
COMMIT;
```

---

## Utility cần dùng

```java
// Lấy connection
import com.bakery.utils.DBConnect;
Connection conn = DBConnect.getConnection();

// Gọi procedure
CallableStatement cs = conn.prepareCall("{call TÊN_PROC(?, ?, ...)}");
cs.setInt(1, value);
cs.registerOutParameter(2, Types.NUMERIC);
cs.execute();
double result = cs.getDouble(2);

// KHÔNG cần:
// conn.setTransactionIsolation(...)   ← do DB tự quản lý
// retry loop                          ← Java chỉ hiển thị lỗi
```

---

## Tài liệu liên quan

- Lý thuyết: `D:\Clone\docs\chapter4_concurrent_access.md`
- Bảng kịch bản SQL: `D:\Clone\docs\chapter4_demo.md`
- DB Procedures: `D:\Clone\database\demo\`
