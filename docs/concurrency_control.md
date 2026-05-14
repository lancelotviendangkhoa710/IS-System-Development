# Xử lý Đồng thời (Concurrency Control)
## Bakery Management System — Tài liệu kỹ thuật

> **Phiên bản:** 1.0 | **Cập nhật:** 2026-05-15  
> **Liên quan:** `BaseDAO.java`, `DonHangDAO.java`, `ConcurrencyDemoService.java`, `database/demo_concurrent_test.sql`

---

## 1. Bối cảnh nghiệp vụ

Hệ thống Bakery có nhiều người dùng đồng thời:

| Vai trò | Thao tác nhạy cảm |
|---------|-------------------|
| Thu ngân A & B | Cùng bán sản phẩm tồn kho ít |
| NV quản lý kho | Cùng xuất nguyên liệu cùng lô |
| Quản lý | Xem báo cáo trong khi có giao dịch đang ghi |

Nếu không xử lý đồng thời đúng cách → **số liệu sai, kho âm, mất đơn hàng**.

---

## 2. Bốn vấn đề đồng thời cổ điển

### 2.1 Lost Update (Ghi đè dữ liệu)

```
T1 (Thu ngân A)          T2 (Thu ngân B)
─────────────────────    ─────────────────────
READ tonKho = 10         
                         READ tonKho = 10
UPDATE tonKho = 10 - 3   
COMMIT → tonKho = 7      
                         UPDATE tonKho = 10 - 5  ← đọc giá trị CŨ!
                         COMMIT → tonKho = 5     ← lẽ ra phải là 2!
```

**Hậu quả:** Bán 8 sản phẩm nhưng kho chỉ trừ 5.

### 2.2 Dirty Read (Đọc dữ liệu bẩn)

```
T1                       T2
─────────────────────    ─────────────────────
UPDATE điểm KH += 100
                         READ điểm KH = 1100  ← đọc chưa COMMIT
ROLLBACK ← lỗi!         
                         Hiển thị 1100 cho KH  ← sai!
```

**Hậu quả:** Khách thấy điểm tích lũy sai, có thể dùng điểm chưa thực sự có.

### 2.3 Non-Repeatable Read (Đọc không lặp lại)

```
T1 (Báo cáo)             T2
─────────────────────    ─────────────────────
READ doanh_thu = 50M     
                         UPDATE thêm 1 đơn 5M
                         COMMIT
READ doanh_thu = 55M     ← khác lần 1!
```

**Hậu quả:** Báo cáo tổng kết không nhất quán.

### 2.4 Phantom Read (Đọc bóng ma)

```
T1 (Đếm đơn)             T2
─────────────────────    ─────────────────────
COUNT(*) = 20 đơn        
                         INSERT 1 đơn mới
                         COMMIT
COUNT(*) = 21 đơn        ← dòng mới xuất hiện!
```

---

## 3. Giải pháp đã triển khai

### 3.1 Tầng Database — Stored Procedures

Các stored procedure **đã có sẵn** `SELECT ... FOR UPDATE` (Pessimistic Locking):

| Procedure | File | Mục đích |
|-----------|------|----------|
| `PROC_TAODONHANG` | `proc_order_cud.sql:93` | Lock sản phẩm khi tạo đơn |
| `PROC_XUATKHO_NGUYENLIEU` | `proc_stock_cud.sql:178,187` | Lock lô nguyên liệu khi xuất |
| `PROC_XUATKHO_THANHPHAM` | `proc_stock_cud.sql:331,339` | Lock thành phẩm khi xuất |
| `PROC_CHUYENTRANGTHAIDON` | `proc_change_status.sql:20` | Lock đơn khi đổi trạng thái |

> **Vấn đề trước đây:** Java gọi các proc này với `autoCommit = true` (mặc định JDBC) → Oracle auto-COMMIT sau mỗi statement → `FOR UPDATE` bị giải lock ngay → **locking vô tác dụng**.

### 3.2 Tầng Java — Explicit Transaction (Fix đã triển khai)

**File:** `src/main/java/com/bakery/model/dao/banhang/DonHangDAO.java`

#### Trước khi fix:
```java
public int taoDonHang(...) throws Exception {
    try (Connection conn = moKetNoi()) {  // autoCommit = TRUE ← sai!
        return taoDonHangWithConn(conn, ...);
        // PROC_TAODONHANG chạy xong → Oracle auto-COMMIT
        // → FOR UPDATE bên trong proc được giải ngay
        // → T2 có thể đọc và ghi đồng thời!
    }
}
```

#### Sau khi fix:
```java
public int taoDonHang(...) throws Exception {
    Connection conn = null;
    try {
        conn = moKetNoi();
        conn.setAutoCommit(false);   // ← Bắt đầu explicit transaction
        int maDon = taoDonHangWithConn(conn, ...);
        // PROC_TAODONHANG chạy → FOR UPDATE lock sản phẩm
        // T2 gọi cùng lúc → BỊ BLOCK tại FOR UPDATE
        // T1 tiếp tục xử lý (trừ kho, tạo đơn)...
        conn.commit();               // ← Lúc này lock mới được giải
        // T2 tiếp tục với giá trị tồn kho MỚI (đã trừ của T1)
        return maDon;
    } catch (Exception e) {
        if (conn != null) conn.rollback();
        handleException("taoDonHang", e);
    } finally {
        if (conn != null) { conn.setAutoCommit(true); conn.close(); }
    }
    return -1;
}
```

**Kết quả thực tế:**
```
T1 (Thu ngân A)                    T2 (Thu ngân B) cùng lúc
─────────────────────────────────  ─────────────────────────
setAutoCommit(false)               setAutoCommit(false)
CALL PROC_TAODONHANG(...)          CALL PROC_TAODONHANG(...)
  → FOR UPDATE SANPHAM WHERE ...     → BLOCKED! ← chờ T1
  → READ tonKho = 10              
  → tonKho >= 3 ✓                 
  → UPDATE tonKho = 7             
conn.commit() → lock giải          
                                   → Unblocked! FOR UPDATE thành công
                                   → READ tonKho = 7  ← đúng!
                                   → tonKho >= 5 ✓
                                   → UPDATE tonKho = 2
                                   conn.commit()
```

### 3.3 BaseDAO — Kết nối theo Isolation Level

**File:** `src/main/java/com/bakery/model/dao/BaseDAO.java`

```java
// Dùng cho báo cáo tổng hợp — tránh Non-Repeatable Read
protected Connection moKetNoiSerializable() throws Exception {
    Connection conn = DBConnect.getConnection();
    conn.setAutoCommit(false);
    conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    return conn;
}
```

**Khi nào dùng `moKetNoiSerializable()`:**
- `ThongKeDAO` — tính doanh thu tháng, tồn kho cuối kỳ
- Bất kỳ báo cáo nào cần đọc nhiều bảng trong 1 snapshot nhất quán

---

## 4. Isolation Levels được áp dụng

| Situation | Isolation Level | Method |
|-----------|----------------|--------|
| Tạo đơn hàng (POS) | `READ_COMMITTED` + `FOR UPDATE` | `taoDonHang()` |
| Đổi trạng thái đơn | `READ_COMMITTED` + `FOR UPDATE` | `chuyenTrangThaiDon()` |
| Báo cáo thống kê | `SERIALIZABLE` | `moKetNoiSerializable()` |
| Xuất kho nguyên liệu | `READ_COMMITTED` + `FOR UPDATE` (trong proc) | `PROC_XUATKHO_*` |

---

## 5. Demo trên Java Application

### 5.1 Cách chạy `ConcurrencyDemoService`

**File:** `src/main/java/com/bakery/services/hethong/ConcurrencyDemoService.java`

Service này cung cấp 2 phương thức demo thực (kết nối DB thật):

#### Demo 1: Lost Update với Pessimistic Lock
```java
// Trong Controller hoặc test class:
ConcurrencyDemoService demo = new ConcurrencyDemoService();

// log nhận callback realtime trên FX thread
demo.demoLostUpdate(logMessage -> {
    // Thêm logMessage vào TextArea trong UI
    txtAreaLog.appendText(logMessage + "\n");
});
```

**Kịch bản:** Khởi động 2 thread (`demo-t1-lost-update`, `demo-t2-lost-update`) cùng lúc:
- T1 đọc tồn kho MASP=1, cần trừ 3
- T2 (delay 300ms) cũng đọc tồn kho MASP=1, cần trừ 5
- Với `FOR UPDATE`: T2 bị **BLOCK** cho đến khi T1 COMMIT
- T2 đọc lại → thấy tồn kho mới đúng → kiểm tra đủ/không đủ

#### Demo 2: Non-Repeatable Read vs SERIALIZABLE
```java
// Mode READ_COMMITTED → T1 thấy 2 lần đọc KHÁC nhau
demo.demoNonRepeatableRead(false, logMessage -> {
    txtAreaLog.appendText(logMessage + "\n");
});

// Mode SERIALIZABLE → T1 thấy 2 lần đọc GIỐNG nhau (snapshot)
demo.demoNonRepeatableRead(true, logMessage -> {
    txtAreaLog.appendText(logMessage + "\n");
});
```

### 5.2 Tích hợp vào giao diện (nếu muốn thêm UI demo)

**Bước 1:** Thêm vào FXML admin (ví dụ `KhoiPhucDuLieuView.fxml`):
```xml
<!-- Khu vực demo concurrency — chỉ dùng cho testing/trình bày -->
<VBox styleClass="card" spacing="12">
    <Label text="🔬 Demo Xử lý Đồng thời" styleClass="lbl-title-card"/>
    <HBox spacing="10">
        <Button text="Demo Lost Update"
                onAction="#onDemoLostUpdate" styleClass="btn-secondary"/>
        <Button text="Demo READ_COMMITTED"
                onAction="#onDemoReadCommitted" styleClass="btn-secondary"/>
        <Button text="Demo SERIALIZABLE"
                onAction="#onDemoSerializable" styleClass="btn-primary"/>
        <Button text="🧹 Xóa log"
                onAction="#onXoaLog" styleClass="btn-danger"/>
    </HBox>
    <TextArea fx:id="txtAreaConcurrencyLog"
              prefHeight="200" editable="false"
              style="-fx-font-family: 'Consolas'; -fx-font-size: 12;"/>
</VBox>
```

**Bước 2:** Trong Controller:
```java
@FXML private TextArea txtAreaConcurrencyLog;

private final ConcurrencyDemoService concurrencyDemo = new ConcurrencyDemoService();

@FXML
private void onDemoLostUpdate() {
    txtAreaConcurrencyLog.appendText("\n══ Demo: Lost Update ══\n");
    concurrencyDemo.demoLostUpdate(msg ->
        txtAreaConcurrencyLog.appendText(msg + "\n"));
}

@FXML
private void onDemoReadCommitted() {
    txtAreaConcurrencyLog.appendText("\n══ Demo: Non-Repeatable Read (READ_COMMITTED) ══\n");
    concurrencyDemo.demoNonRepeatableRead(false, msg ->
        txtAreaConcurrencyLog.appendText(msg + "\n"));
}

@FXML
private void onDemoSerializable() {
    txtAreaConcurrencyLog.appendText("\n══ Demo: SERIALIZABLE (snapshot nhất quán) ══\n");
    concurrencyDemo.demoNonRepeatableRead(true, msg ->
        txtAreaConcurrencyLog.appendText(msg + "\n"));
}

@FXML
private void onXoaLog() {
    txtAreaConcurrencyLog.clear();
}
```

### 5.3 Chạy test SQL trực tiếp trên Oracle

**File:** `database/demo_concurrent_test.sql`

Mở 2 SQL Developer session song song:

```sql
-- SESSION 1 (Terminal 1):
BEGIN
    -- Mở transaction, chờ 10 giây trước khi commit
    UPDATE SANPHAM SET SOLUONGTON = SOLUONGTON - 3 WHERE MASP = 1;
    DBMS_LOCK.SLEEP(10);
    COMMIT;
END;

-- SESSION 2 (Terminal 2, chạy ngay sau Session 1):
-- Sẽ bị BLOCK cho đến khi Session 1 COMMIT
SELECT SOLUONGTON FROM SANPHAM WHERE MASP = 1 FOR UPDATE;
```

---

## 6. Giám sát Lock trong Oracle

Khi muốn xem lock đang active (chạy bằng DBA hoặc schema owner):

```sql
-- Xem các session đang bị block
SELECT
    w.sid          AS SESSION_BI_BLOCK,
    h.sid          AS SESSION_GIU_LOCK,
    w.event        AS LY_DO_CHO,
    h.username     AS NGUOI_GIU,
    o.object_name  AS BANG_BI_LOCK
FROM
    v$session w,
    v$session h,
    dba_objects o
WHERE
    w.blocking_session = h.sid
    AND w.row_wait_obj# = o.object_id (+);

-- Xem tất cả lock đang active
SELECT
    s.username,
    s.sid,
    s.serial#,
    l.type,
    l.lmode,
    l.request
FROM
    v$session s
    JOIN v$lock l ON s.sid = l.sid
WHERE
    s.username IS NOT NULL
ORDER BY s.username;
```

---

## 7. Khi nào KHÔNG cần lo về concurrency

Các thao tác **đọc đơn thuần** (SELECT không có business logic phụ thuộc nhau) an toàn với isolation mặc định:
- Xem danh sách sản phẩm
- Tìm kiếm khách hàng
- Xem lịch sử đơn hàng

---

## 8. Các file liên quan

| File | Vai trò |
|------|---------|
| `model/dao/BaseDAO.java` | `moKetNoi()`, `moKetNoiSerializable()` |
| `model/dao/banhang/DonHangDAO.java` | Explicit TX trong `taoDonHang()`, `chuyenTrangThaiDon()` |
| `services/hethong/ConcurrencyDemoService.java` | Demo 2 kịch bản bằng Java threads thực |
| `database/demo_concurrent_test.sql` | 4 kịch bản SQL demo chạy trên Oracle |
| `database/05_procedures/cud/proc_order_cud.sql` | `PROC_TAODONHANG` có `FOR UPDATE` line 93 |
| `database/05_procedures/cud/proc_stock_cud.sql` | `PROC_XUATKHO_*` có `FOR UPDATE` lines 178, 187, 331, 339 |
