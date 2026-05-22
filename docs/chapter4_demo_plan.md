# Plan Demo Chương 4 — Truy xuất đồng thời

> Ngày: 2026-05-22  
> Trạng thái: **APPROVED — Chờ thực thi**

---

## Nguyên tắc bất biến

- Java **chỉ gọi procedure** — không `setAutoCommit`, không `setTransactionIsolation`
- Toggle bug/fix = **comment/bỏ comment 1 dòng trong SQL procedure** trước mặt giáo viên
- Procedure tự quản lý `COMMIT / ROLLBACK`

---

## Phân tích `PROC_TAODONHANG` hiện tại

```
✅ Delay đã có    → dòng 114–117 (80M loop ≈ 3s)
✅ COMMIT có sẵn  → dòng 149
✅ FOR UPDATE có sẵn → dòng 92 ← đây là BẢO VỆ, comment ra để tạo BUG
❌ SERIALIZABLE chưa có → cần thêm (commented) để dùng khi FIX
```

---

## Thay đổi 1 — `database/05_procedures/cud/proc_order_cud.sql`

### Tại đầu BEGIN (thêm mới):
```sql
BEGIN
    -- ============================================================
    -- [DEMO TOGGLE] Chọn chế độ:
    -- BUG (mặc định): dòng dưới đang comment → FOR UPDATE cũng comment
    -- FIX: bỏ comment EXECUTE IMMEDIATE → T2 nhận ORA-08177 → rollback
    --
    -- EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';
    -- ============================================================
```

### Tại SELECT kiểm tra tồn kho (dòng 88–92, sửa):
```sql
-- TRƯỚC:
FROM SANPHAM
WHERE MASP = V_TAB(I).MASP
    FOR UPDATE;   ← BẢO VỆ

-- SAU:
FROM SANPHAM
WHERE MASP = V_TAB(I).MASP;
-- FOR UPDATE;   ← BUG: comment ra → T2 không bị chặn → Lost Update
```

---

## Thay đổi 2 — `src/main/java/com/bakery/model/dao/banhang/DonHangDAO.java`

### Xóa Java-level transaction management:
```java
// TRƯỚC:
conn.setAutoCommit(false);
int maDon = taoDonHangWithConn(...);
conn.commit();
// finally: conn.setAutoCommit(true);

// SAU:
try (Connection conn = moKetNoi()) {
    return taoDonHangWithConn(conn, ...);
}
```

### Hardcode procedure name:
```java
// TRƯỚC:
String tenProc = DemoConfig.getTenProcTaoDon();
String sql = "{CALL " + tenProc + "(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

// SAU:
// Toggle bug/fix bằng comment trong PROC_TAODONHANG
String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
```

### Xóa import:
```java
// XÓA:
import com.bakery.utils.DemoConfig;
```

---

## Thay đổi 3 — `src/main/java/com/bakery/model/dao/BaseDAO.java`

### Nhận diện ORA-00060 (Deadlock) và ORA-08177 (Serializable):
```java
if (e instanceof SQLException sqle) {
    int code = sqle.getErrorCode();
    if (code == 60) {  // ORA-00060: deadlock
        throw new Exception("⚠ Deadlock phát hiện! Giao dịch bị Oracle rollback " +
            "vì xung đột khóa với phiên khác. Vui lòng thử lại.");
    }
    if (code == 8177) {  // ORA-08177: can't serialize
        throw new Exception("⚠ Xung đột dữ liệu! Sản phẩm vừa được cập nhật bởi phiên khác. " +
            "Giao dịch đã bị hủy để bảo vệ tính toàn vẹn dữ liệu.");
    }
    throw new Exception("Lỗi truy xuất dữ liệu hệ thống: " + e.getMessage());
}
```

---

## Thay đổi 4 — Xóa Demo Infrastructure Cũ

| File | Hành động |
|---|---|
| `src/main/java/com/bakery/utils/DemoConfig.java` | **XÓA** |
| `src/.../controllers/banhang/DemoControlPanelViewFXMLController.java` | **XÓA** |
| `src/.../controllers/banhang/DemoPhantomReadViewFXMLController.java` | **XÓA** |
| `src/main/resources/fxml/banhang/DemoControlPanelView.fxml` | **XÓA** |
| `src/main/resources/fxml/banhang/DemoPhantomReadView.fxml` | **XÓA** |

---

## Quy trình demo trước giáo viên

### 4.1 Lost Update — BUG
```sql
-- Reset dữ liệu trước
UPDATE SANPHAM SET SOLUONGTON = 20 WHERE MASP = 1001; COMMIT;
-- Đảm bảo PROC_TAODONHANG: EXECUTE IMMEDIATE đang comment, FOR UPDATE đang comment
```
1. Mở 2 cửa sổ app → đăng nhập 2 thu ngân
2. Thu ngân 1: Bán Hàng → chọn sản phẩm X (3 cái) → Tạo đơn
3. **Trong 3s delay**: Thu ngân 2: Bán Hàng → chọn sản phẩm X (5 cái) → Tạo đơn
4. Cả 2 thành công → `SELECT SOLUONGTON FROM SANPHAM WHERE MASP=1001` → **15** ≠ 12 ❌

### 4.1 Lost Update — FIX (1 dòng)
```sql
-- Trong PROC_TAODONHANG, bỏ comment:
EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL SERIALIZABLE';
-- Compile lại → lặp lại demo → Thu ngân 2 nhận dialog lỗi ORA-08177 ✅
```

### 4.4 Deadlock — Demo trên app
- 2 session sửa MUCTONANTOAN ngược thứ tự → session bị chọn làm nạn nhân nhận dialog:  
  **"⚠ Deadlock phát hiện! Giao dịch bị Oracle rollback..."**

---

## Files thay đổi tổng kết

| File | Loại |
|---|---|
| `database/05_procedures/cud/proc_order_cud.sql` | MODIFY |
| `src/main/java/com/bakery/model/dao/banhang/DonHangDAO.java` | MODIFY |
| `src/main/java/com/bakery/model/dao/BaseDAO.java` | MODIFY |
| `src/main/java/com/bakery/utils/DemoConfig.java` | DELETE |
| `src/main/java/com/bakery/views/controllers/banhang/DemoControlPanelViewFXMLController.java` | DELETE |
| `src/main/java/com/bakery/views/controllers/banhang/DemoPhantomReadViewFXMLController.java` | DELETE |
| `src/main/resources/fxml/banhang/DemoControlPanelView.fxml` | DELETE |
| `src/main/resources/fxml/banhang/DemoPhantomReadView.fxml` | DELETE |
