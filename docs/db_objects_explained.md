# 📖 Giải thích 6 DB Object Nổi Bật — Bakery Management System

> **Dành cho ai:** Mọi thành viên team, kể cả chưa quen Oracle PL/SQL.
> **Mục tiêu:** Đọc xong biết mỗi object làm gì, ai gọi nó, và điều gì tự động xảy ra sau đó.

---

## Mục lục

| # | Object | Loại | Tóm tắt 1 dòng |
|---|--------|------|----------------|
| 1 | `TRG_GIAVONTRUNGBINH_SOLUONGTONTONG` | Trigger | Tự cập nhật giá vốn trung bình & tổng tồn kho NL sau mỗi lần nhập/sửa/xóa phiếu nhập |
| 2 | `TRG_KIEMSOAT_CONGSUAT_TUYCHINH` | Trigger | Gác cổng năng lực sản xuất, từ chối đơn tùy chỉnh nếu lò bánh đã hết chỗ |
| 3 | `PROC_TAODONHANG` | Procedure | Tạo đơn hàng hoàn chỉnh từ JSON, bảo vệ chống Lost Update & Non-Repeatable Read |
| 4 | `PROC_XUATKHOSANXUAT` | Procedure | Xuất nguyên liệu làm bánh theo FEFO, dùng Cursor + Pessimistic Lock chống Deadlock |
| 5 | `FUNC_TINHTIENMATLYTUONG` | Function | Tính tiền mặt lý tưởng cuối ca để đối soát két |
| 6 | `TINHSOBANHTOIDA` | Function | Dựa vào tồn kho NL + công thức, trả về số bánh tối đa có thể làm |

---

## 1. `TRG_GIAVONTRUNGBINH_SOLUONGTONTONG`

### 🟡 Một câu mô tả đơn giản
> Mỗi khi có chi tiết phiếu nhập kho nguyên liệu được **thêm / sửa / xóa**, trigger này **tự động tính lại giá vốn trung bình** và **số lượng tồn kho** của nguyên liệu đó trong bảng `NGUYENLIEU`. Java không cần làm gì cả.

---

### 📍 Khi nào nó chạy?
```
Sự kiện: INSERT / UPDATE / DELETE trên bảng CTPHIEUNHAP
```
Tức là: khi thủ kho nhập hàng, sửa lô, hoặc hủy phiếu nhập.

---

### 🔢 Nó tính gì? — Công thức WAC (Weighted Average Cost)

Hệ thống dùng **giá vốn trung bình theo trọng số (WAC)** — chuẩn kế toán phổ biến nhất.

**Ý tưởng đơn giản:**
```
Giá vốn mới = (Tổng giá trị cũ + Giá trị lô mới) / Tổng số lượng mới
```

**Ví dụ thực tế:**
- Tồn kho: **100 kg bột**, giá vốn TB: **10.000 đ/kg** → Tổng giá trị = 1.000.000 đ
- Nhập thêm: **50 kg** với giá **13.000 đ/kg** → Giá trị nhập = 650.000 đ
- Sau nhập:
  - Tổng lượng = 150 kg
  - Tổng giá trị = 1.650.000 đ
  - **Giá vốn mới = 1.650.000 / 150 = 11.000 đ/kg** ✅

---

### 🔄 Xử lý 3 tình huống

| Tình huống | Delta số lượng | Delta giá trị |
|-----------|----------------|----------------|
| INSERT (nhập mới) | `+SL_mới` | `+SL_mới × Đơn_giá_mới` |
| UPDATE (sửa lô) | `+SL_mới - SL_cũ` | Tính lại WAC: trừ cũ, cộng mới |
| DELETE (hủy phiếu) | `-SL_cũ` | Trừ ngược phần giá trị đã tính |

> **Trường hợp đặc biệt:** Nếu sau DELETE mà tồn = 0 → reset giá vốn về 0. Nếu tồn âm (bất thường) → giữ nguyên giá vốn cũ để bảo vệ dữ liệu.

---

### 🛰️ Vệ tinh xung quanh

Trigger này **hoạt động cùng** với:
- **`PROC_TAOPHIEUNHAPKHO`**: Procedure cha gọi INSERT vào `CTPHIEUNHAP` → trigger bắn tự động
- **`PROC_HUYPHIEUNHAPKHO`**: DELETE từ `CTPHIEUNHAP` → trigger tự trừ lại tồn
- **`TINHSOBANHTOIDA`** (object #6): Đọc `SOLUONGTONTONG` từ `NGUYENLIEU` — cột mà trigger này cập nhật

```
Nhập hàng (Java UI)
    ↓
PROC_TAOPHIEUNHAPKHO
    ↓ INSERT vào CTPHIEUNHAP
    ↓ [TRG_GIAVONTRUNGBINH_SOLUONGTONTONG bắn tự động]
        ↓ UPDATE NGUYENLIEU.SOLUONGTONTONG
        ↓ UPDATE NGUYENLIEU.GIAVONTRUNGBINH
    ✅ Java nhận về: tồn kho + giá vốn đã được cập nhật
```

---

### ☕ Java gọi nó như thế nào?

Java **không gọi trigger trực tiếp**. Java chỉ gọi:
```java
// PhieuNhapKhoDAO.java
String sql = "{CALL PROC_TAOPHIEUNHAPKHO(?, ?, ?, ?, ?)}";
CallableStatement cstmt = conn.prepareCall(sql);
```
Trigger tự bắn khi Procedure insert vào `CTPHIEUNHAP`. Đây là thiết kế **"fire and forget"** — logic kế toán được bảo đảm ở tầng DB, tầng Java không bao giờ phải tính WAC.

---

---

## 2. `TRG_KIEMSOAT_CONGSUAT_TUYCHINH`

### 🟡 Một câu mô tả đơn giản
> Mỗi khi khách đặt bánh tùy chỉnh (custom cake), trigger này **tự kiểm tra xem ngày sản xuất đó còn đủ chỗ không**. Nếu vượt giới hạn → **từ chối đơn ngay lập tức**, không để dữ liệu xấu lọt vào DB.

---

### 📍 Khi nào nó chạy?
```
Sự kiện: INSERT / UPDATE / DELETE trên bảng CTDONTUYCHINH
```

---

### 🧠 Logic hoạt động (từng bước)

**Bước 1 — Tính ngày sản xuất thực tế:**
```
Ngày sản xuất = Ngày giao hàng − Thời gian chuẩn bị (ngày)
```
Ví dụ: Khách nhận ngày 5/6, cần 2 ngày chuẩn bị → sản xuất ngày 3/6.

**Bước 2 — Kiểm tra bảng `NANGLUCSANXUAT`:**
- Nếu **chưa có dòng** cho ngày đó → **tự động INSERT** với giới hạn mặc định **50 bánh/ngày** (không ném lỗi như cũ).
- Nếu **đã có** → đọc số đã nhận và giới hạn hiện tại.

**Bước 3 — Kiểm tra vượt ngưỡng:**
```sql
IF (SoBanhDaNhan + SoBanhKhachDatThem) > GioiHan THEN
    → RAISE lỗi, Oracle tự ROLLBACK INSERT này
```

**Bước 4 — Cập nhật số đã nhận:**
```sql
UPDATE NANGLUCSANXUAT SET SOBANHDANHAN = SoBanhDaNhan + Delta
```

---

### 🛡️ Tại sao cần `FOR UPDATE` (Pessimistic Lock)?

```sql
SELECT ... FROM NANGLUCSANXUAT WHERE ... FOR UPDATE;
```

Vì **2 nhân viên có thể tạo đơn cùng lúc** cho cùng ngày sản xuất:
- Không lock → cả 2 đều đọc được `SOBANHDANHAN = 45`, limit = 50
- Cả 2 đều thấy "còn chỗ 5 bánh" → cả 2 đều INSERT 8 bánh → tổng vượt 50 mà không ai biết ❌
- Với `FOR UPDATE` → người đến sau phải chờ người trước COMMIT xong mới đọc được số mới nhất ✅

---

### 🛰️ Vệ tinh xung quanh

```
PROC_TAODONHANG (object #3)
    ↓ INSERT vào CTDONTUYCHINH (isCustom = 'true')
    ↓ [TRG_KIEMSOAT_CONGSUAT_TUYCHINH bắn tự động]
        ├── Tự tạo NANGLUCSANXUAT nếu chưa có (IMP-07)
        ├── Kiểm tra ngưỡng → RAISE lỗi nếu vượt
        └── Cập nhật SOBANHDANHAN
```

Trigger này **không chạy cho bánh thường** (`CTDONHANG`). Bánh thường không cần sản xuất trước, lấy trực tiếp từ kho thành phẩm.

---

### ☕ Java gọi nó như thế nào?

Java gọi `PROC_TAODONHANG`, trigger tự kích hoạt khi INSERT `CTDONTUYCHINH`:
```java
// DonHangDAO.java — line 57
String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
```
Nếu trigger ném lỗi (vượt công suất) → Oracle rollback → Java catch `SQLException` → hiển thị thông báo cho thu ngân.

---

---

## 3. `PROC_TAODONHANG`

### 🟡 Một câu mô tả đơn giản
> Tạo một đơn hàng hoàn chỉnh. Java gửi **một chuỗi JSON** chứa toàn bộ giỏ hàng, procedure tự parse, kiểm tra tồn kho, insert đơn + chi tiết, và trả về mã đơn vừa tạo.

---

### 📍 Java gọi thế nào?

```java
// DonHangDAO.java — line 57
String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
CallableStatement cstmt = conn.prepareCall(sql);
// Param 1: Ngày giờ nhận bánh
// Param 2: Mã khách hàng (nullable)
// Param 3: Mã nhân viên lập đơn
// Param 4: Mã trạng thái
// Param 5: Tiền đặt cọc
// Param 6: Hình thức nhận (ship/tự đến)
// Param 7: Địa chỉ giao (nullable)
// Param 8: JSON chi tiết giỏ hàng ← QUAN TRỌNG
// Param 9: OUT — mã đơn vừa tạo (Oracle trả về)
```

---

### 🗂️ Tại sao dùng JSON? — Giải thích cho người chưa biết

**Vấn đề:** Một đơn hàng có thể có **nhiều sản phẩm** (n dòng chi tiết). Stored Procedure Oracle **không nhận được array/list trực tiếp** từ Java.

**Giải pháp:** Java **gom tất cả sản phẩm thành một chuỗi JSON**, truyền vào 1 tham số kiểu `CLOB`. Oracle dùng hàm `JSON_TABLE` để "mở" chuỗi đó ra thành nhiều dòng bên trong procedure.

```java
// Ví dụ JSON Java tạo ra (DonHangDAO.java — line 351-395)
[
  { "maSP": 5,  "soLuong": 2, "donGia": 85000, "isCustom": "false" },
  { "maSP": 12, "soLuong": 1, "donGia": 120000,"isCustom": "true",
    "ghiChu": "Chúc mừng sinh nhật", "phuKien": "nến hồng",
    "maKC": 3, "maCot": null, "maNhan": 2, "maTrangTri": 1 }
]
```

**Oracle parse lại thành bảng:**
```sql
SELECT J.MASP, J.SOLUONG, J.DONGIA, J.IS_CUSTOM, ...
FROM JSON_TABLE(P_JSONCHITIET, '$[*]'
     COLUMNS (
         MASP    NUMBER PATH '$.maSP',
         SOLUONG NUMBER PATH '$.soLuong',
         ...
     )) J
```

---

### 🔁 Hai phase của procedure (quan trọng để hiểu demo bài báo cáo)

#### Phase 1 — Snapshot (đọc, chốt giá, kiểm kho)
```
EXECUTE IMMEDIATE 'ALTER SESSION SET ISOLATION_LEVEL = SERIALIZABLE'
→ Chốt snapshot tại thời điểm này
→ Đọc tồn kho & cache vào V_TONKHO_CACHE(I)
→ DBMS_SESSION.SLEEP(6)  ← delay giả lập 2 thu ngân cùng vào
→ Đọc lại giá bán (demo Non-Repeatable Read)
→ COMMIT (phase read-only, không DML)
```

#### Phase 2 — Ghi đơn
```
→ INSERT DONDATHANG (đầu đơn)
→ Loop từng sản phẩm:
    isCustom='false' → INSERT CTDONHANG
                       → TRG_CAPNHAT_CTDONHANG tự cộng TONGTIENHDBAN
                       → TRG_DONGBO_SOLUONG_MESANXUAT tự trừ mẻ theo FEFO
    isCustom='true'  → INSERT CTDONTUYCHINH
                       → TRG_KIEMSOAT_CONGSUAT_TUYCHINH kiểm tra công suất
→ UPDATE SANPHAM.SOLUONGTON (trừ kho bánh có sẵn)
→ INSERT LICHSUDONHANG, HOATDONGNHANVIEN
→ COMMIT
```

---

### 🛰️ Vệ tinh xung quanh — Triggers tự động kích hoạt

| Trigger | Bảng kích hoạt | Làm gì |
|---------|---------------|--------|
| `TRG_CAPNHAT_CTDONHANG` | `CTDONHANG` | Cộng tiền vào `DONDATHANG.TONGTIENHDBAN` |
| `TRG_DONGBO_SOLUONG_MESANXUAT` | `CTDONHANG` | Trừ số lượng mẻ bánh theo FEFO |
| `TRG_KIEMSOAT_CONGSUAT_TUYCHINH` | `CTDONTUYCHINH` | Kiểm tra & cập nhật năng lực SX |

> **Lưu ý `G_SKIP_STOCK_TRIGGER`:** Procedure bật flag này trước loop insert để tắt trigger stock (tránh xung đột với logic tự trừ kho của procedure). Flag được tắt lại ngay sau.

---

### ⚠️ Demo Lost Update & Non-Repeatable Read

Đây là lý do procedure có 2 phase và `SLEEP`:
- **Non-Repeatable Read (NRR):** Thu ngân đọc giá 80.000đ → quản lý update giá 90.000đ → thu ngân đọc lại thấy 90.000đ. Với `SERIALIZABLE`: lần đọc sau vẫn trả về 80.000đ (snapshot cũ).
- **Lost Update:** 2 thu ngân cùng cache tồn kho = 10, cùng trừ 3 → cả 2 ghi 7, mất 3 cái. Đây là bug được giữ lại cố ý trong procedure để demo.

---

---

## 4. `PROC_XUATKHOSANXUAT`

### 🟡 Một câu mô tả đơn giản
> Thợ bếp bấm "làm bánh" → procedure này xuất nguyên liệu từ kho, **ưu tiên lô gần hết hạn nhất (FEFO)**, cập nhật tồn kho, tạo mẻ sản xuất, và ghi phiếu xuất kho.

---

### 📍 Java gọi thế nào?

```java
// PhieuXuatKhoDAO.java — line 77
String sql = "{CALL PROC_XUATKHOSANXUAT(?, ?, ?)}";
cs.setInt(1, maSP);           // Mã sản phẩm cần làm
cs.setDouble(2, soLuongSanXuat); // Số bánh cần làm
cs.setInt(3, maNV);           // Mã nhân viên thực hiện
cs.execute();
```

---

### 🔤 Tại sao dùng Cursor? — Giải thích cho người chưa biết

**Vấn đề:** Một sản phẩm có thể có nhiều nguyên liệu trong công thức. Mỗi nguyên liệu lại nằm ở **nhiều lô hàng khác nhau** (nhập nhiều lần). Cần lấy từ lô cũ nhất trước (FEFO).

**Cursor = "Con trỏ duyệt từng dòng":**
```sql
-- Cursor 1: duyệt từng nguyên liệu trong công thức
CURSOR C_CONGTHUC IS
    SELECT C.MANL, N.TENNL, (C.SOLUONGTIEUHAO * P_SOLUONGSANXUAT) AS TONG_CAN_DUNG
    FROM CONGTHUC C JOIN NGUYENLIEU N ON C.MANL = N.MANL
    WHERE C.MASP = P_MASP
    ORDER BY C.MANL ASC;  -- ← thứ tự lock cố định, chống deadlock

-- Cursor 2: với mỗi NL, duyệt từng lô theo FEFO
CURSOR C_LOHANG(P_MANL_TARGET NUMBER) IS
    SELECT MALO, SOLUONGCONLAI
    FROM CTPHIEUNHAP
    WHERE MANL = P_MANL_TARGET AND SOLUONGCONLAI > 0
    ORDER BY HANSUDUNG ASC, MALO ASC  -- ← FEFO: hạn gần nhất trước
    FOR UPDATE OF SOLUONGCONLAI;      -- ← lock lô đang xử lý
```

**FEFO = First Expired, First Out** — bán bánh gần hết hạn trước để giảm hao hụt.

---

### 🔄 Luồng chạy từng bước

```
1. Loop C_CONGTHUC (mỗi NL trong công thức):
   ├── Lock dòng NGUYENLIEU → FOR UPDATE (pessimistic lock)
   ├── Kiểm tra SOLUONGTONTONG >= TongCanDung?
   │   └── Không đủ → RAISE lỗi ngay
   ├── Cache vào V_TAB
   └── SLEEP(6) ← delay demo deadlock §4.4

2. INSERT PHIEUXUATKHO (phiếu xuất tổng)
3. INSERT MESANXUAT (bridge sản phẩm ↔ phiếu xuất, phục vụ truy xuất nguồn gốc)

4. Loop V_TAB (từng NL đã cache):
   └── Loop C_LOHANG (từng lô của NL đó, FEFO):
       ├── Lô đủ?  → INSERT CTPHIEUXUAT_NL đủ lượng cần, thoát
       └── Không đủ? → INSERT hết lô đó, trừ lượng cần, sang lô tiếp

5. UPDATE SANPHAM.SOLUONGTON += P_SOLUONGSANXUAT
   (bánh vừa làm xong → vào kho thành phẩm)

6. INSERT HOATDONGNHANVIEN (audit trail)
7. COMMIT
```

---

### 🛰️ Vệ tinh xung quanh — Trigger tự kích hoạt

```
PROC_XUATKHOSANXUAT
    ↓ INSERT CTPHIEUXUAT_NL (từng lô NL)
    ↓ [TRG_XUATSLNGUYENLIEU bắn tự động]
        ├── UPDATE CTPHIEUNHAP.SOLUONGCONLAI -= xuất
        └── UPDATE NGUYENLIEU.SOLUONGTONTONG -= xuất
    ↓ [TRG_GIAVONTRUNGBINH... KHÔNG bắn vì đây là XUẤT, không phải INSERT CTPHIEUNHAP]
    
    ↓ UPDATE SANPHAM.SOLUONGTON += làm ra
```

---

### ⚙️ Cơ chế chống Deadlock — Demo §4.4

Trong procedure có **BUG** và **FIX** được comment sẵn:
- **BUG:** `ORDER BY C.SOLUONGTIEUHAO DESC` + `FOR UPDATE WAIT 5` → 2 phiên lock nguyên liệu **ngược chiều** nhau → Oracle phát hiện deadlock → ném `ORA-00060`
- **FIX:** `ORDER BY C.MANL ASC` (thứ tự lock **giống nhau**) → không bao giờ deadlock

```
Session A: lock NL_1 → chờ NL_2 (đang bị B lock)
Session B: lock NL_2 → chờ NL_1 (đang bị A lock)
→ DEADLOCK! Oracle chọn 1 phiên làm nạn nhân → ROLLBACK

SQLCODE = -60 → Java hiển thị: "DEADLOCK_DETECTED|TênNguyênLiệu"
SQLCODE = -30006 → Java hiển thị: "LOCK_TIMEOUT|TênNguyênLiệu"
```

---

---

## 5. `FUNC_TINHTIENMATLYTUONG`

### 🟡 Một câu mô tả đơn giản
> Cuối ca, hệ thống dùng function này để tính **"két phải có bao nhiêu tiền mặt"** theo sổ sách. Con số này được so với tiền thực tế đếm được để phát hiện chênh lệch.

---

### 📍 Java gọi thế nào?

```java
// DoiSoatDAO.java — line 33
String sql = "SELECT FUNC_TINHTIENMATLYTUONG(?, ?) FROM DUAL";
ps.setInt(1, maCa);
ps.setBigDecimal(2, tienKhaiBaoDauCa); // tham số deprecated, function tự đọc
ResultSet rs = ps.executeQuery();
BigDecimal tienLyTuong = rs.getBigDecimal(1);
```

> **`FROM DUAL`** là bảng ảo 1 dòng của Oracle, dùng để gọi function trả về scalar value mà không cần bảng thực.

---

### 🧮 Công thức tính

```
Tiền mặt lý tưởng =
    Tiền khai báo đầu ca (từ DOISOAT)
  + Tổng hóa đơn thanh toán bằng tiền mặt trong ca (loại trừ đã hủy)
  + Tổng phiếu THU tiền mặt trong ca
  − Tổng phiếu CHI tiền mặt trong ca
```

---

### 🚀 Tối ưu gộp query (IMP-02)

Function gộp 2 query riêng (thu và chi) thành 1 query bằng `CASE WHEN`:

```sql
-- Thay vì 2 query SELECT riêng:
SELECT
    NVL(SUM(CASE WHEN UPPER(LTC.PHANLOAI) = 'THU' THEN PTC.SOTIEN ELSE 0 END), 0),
    NVL(SUM(CASE WHEN UPPER(LTC.PHANLOAI) = 'CHI' THEN PTC.SOTIEN ELSE 0 END), 0)
INTO V_TIEN_THU, V_TIEN_CHI
FROM PHIEUTHUCHI PTC JOIN LOAITHUCHI LTC ...
WHERE PTC.MACA = P_MACA AND NVL(UPPER(PTC.TRANGTHAI),'ACTIVE') != 'CANCELLED';
```

Lý do: Giảm từ 4 query → 3 query, tránh 2 round-trip đến DB cho cùng một tập dữ liệu.

---

### ⚠️ Tham số `P_TIENKHAIBAODAUCA` — Tại sao Deprecated?

```sql
FUNC_TINHTIENMATLYTUONG(
    P_MACA             IN CALAMVIEC.MACA%TYPE,
    P_TIENKHAIBAODAUCA IN NUMBER DEFAULT 0  -- Deprecated: không dùng
)
```

Ban đầu Java truyền tiền đầu ca vào. Về sau refactor để function **tự đọc từ bảng `DOISOAT`** — chính xác hơn, không phụ thuộc vào Java truyền đúng hay không. **Giữ tham số cũ để không phá Java caller `DoiSoatDAO`** (backward compatible).

---

### 🛰️ Vệ tinh xung quanh

```
DoiSoatDAO.tinhTienMatLyTuong()
    ↓ SELECT FUNC_TINHTIENMATLYTUONG(maCa, 0) FROM DUAL
        ↓ Đọc DOISOAT → tiền đầu ca
        ↓ Đọc HOADON JOIN PHUONGTHUCTT → tiền hóa đơn tiền mặt
        ↓ Đọc PHIEUTHUCHI JOIN LOAITHUCHI → thu + chi
        ↓ Trả về tổng
    ↓ Service giữ kết quả nội bộ (KHÔNG trả lên View trực tiếp)
    ↓ So sánh với tiền đếm thực tế → tính chênh lệch
    ↓ Gọi PROC_DONGCADOISOAT để ghi kết quả & đóng ca
```

---

---

## 6. `TINHSOBANHTOIDA`

### 🟡 Một câu mô tả đơn giản
> Nhập tên sản phẩm vào, function trả về **số bánh tối đa có thể làm được ngay bây giờ**, dựa trên tồn kho nguyên liệu và định mức công thức.

---

### 📍 Ai gọi nó?

Function này không có Java DAO gọi trực tiếp trong codebase hiện tại — nó được **gọi thủ công từ SQL/báo cáo** hoặc phục vụ mục đích **demo / phân tích**.

```sql
-- Gọi thử từ SQL Developer:
SELECT TINHSOBANHTOIDA('Bánh Kem Dâu') FROM DUAL;
-- Kết quả: 23 → có thể làm tối đa 23 cái bánh kem dâu hôm nay
```

---

### 🧮 Logic tính

```sql
SELECT MIN(FLOOR(NL.SOLUONGTONTONG / CT.SOLUONGTIEUHAO))
INTO V_SOBANH
FROM SANPHAM SP
JOIN CONGTHUC CT ON SP.MASP = CT.MASP
JOIN NGUYENLIEU NL ON CT.MANL = NL.MANL
WHERE SP.TENSP = P_TENSP;
```

**Giải thích từng phần:**
- `NL.SOLUONGTONTONG / CT.SOLUONGTIEUHAO` → mỗi NL cho phép làm được bao nhiêu bánh
- `FLOOR(...)` → làm tròn xuống (không thể làm nửa bánh)
- `MIN(...)` → **nguyên liệu nào ít nhất** quyết định số bánh tối đa → bottleneck

**Ví dụ:**
| Nguyên liệu | Tồn kho | Định mức/bánh | Có thể làm |
|-------------|---------|---------------|-----------|
| Bột mì | 5 kg | 0.2 kg/bánh | 25 bánh |
| Bơ | 2.3 kg | 0.1 kg/bánh | 23 bánh |
| Trứng | 30 cái | 1 cái/bánh | 30 bánh |

→ `MIN(25, 23, 30) = 23` → **chỉ làm được 23 bánh** (bị bottleneck bởi bơ)

---

### 🛰️ Vệ tinh xung quanh

```
TINHSOBANHTOIDA
    ↓ Đọc SANPHAM → lấy MASP
    ↓ Đọc CONGTHUC → định mức từng NL
    ↓ Đọc NGUYENLIEU.SOLUONGTONTONG ← được cập nhật bởi TRG_GIAVONTRUNGBINH... (object #1)
    ↓ Trả về số nguyên dương

Nếu sản phẩm không tồn tại → EXCEPTION NO_DATA_FOUND → RETURN 0
```

---

### ⚠️ Lưu ý — Function này tính "tức thời"

Kết quả thay đổi liên tục theo tồn kho thực tế. Không nên cache kết quả. Nếu tích hợp vào UI, cần gọi lại mỗi khi có thao tác xuất/nhập kho.

---

---

## 📊 Sơ đồ tổng quan — 6 Object phối hợp với nhau

```
┌─────────────────────────────────────────────────────────┐
│                    JAVA (Tầng DAO)                      │
│  DonHangDAO          PhieuXuatKhoDAO    DoiSoatDAO       │
│  .taoDonHang()       .xuatKhoSanXuat()  .tinhTien...()   │
└──────────┬───────────────────┬──────────────┬────────────┘
           │ CallableStatement  │               │ PreparedStatement
           ▼                   ▼               ▼ SELECT FUNC FROM DUAL
   PROC_TAODONHANG    PROC_XUATKHOSANXUAT  FUNC_TINHTIENMATLYTUONG
           │                   │
           │ INSERT             │ INSERT CTPHIEUXUAT_NL
           │ CTDONHANG          │          │
           │    │               │          ▼ [auto]
           │    │               │    TRG_XUATSLNGUYENLIEU
           │    ▼ [auto]        │          │
           │  TRG_CAPNHAT_      │          ▼
           │  CTDONHANG         │    NGUYENLIEU.SOLUONGTONTONG ◄──────┐
           │  (cập nhật tổng)   │                                      │
           │                   │                                      │
           │ INSERT             │                           TRG_GIAVONTRUNGBINH_
           │ CTDONTUYCHINH      │                           SOLUONGTONTONG
           │    │               │                           (AFTER INSERT/UPDATE/
           │    ▼ [auto]        │                            DELETE ON CTPHIEUNHAP)
           │  TRG_KIEMSOAT_    │
           │  CONGSUAT_        ▼
           │  TUYCHINH    TINHSOBANHTOIDA ← đọc SOLUONGTONTONG
           │  (kiểm tra        (tính số bánh tối đa)
           │   năng lực)
```

---

## 🔑 Các khái niệm cần nhớ

| Khái niệm | Ý nghĩa ngắn gọn |
|-----------|-----------------|
| **Trigger** | Code tự chạy ở DB khi có INSERT/UPDATE/DELETE — Java không gọi thủ công |
| **Cursor** | Con trỏ duyệt từng dòng kết quả trong PL/SQL, dùng khi cần xử lý từng row |
| **FOR UPDATE** | Lock dòng đang đọc, ngăn session khác sửa cùng lúc (Pessimistic Lock) |
| **JSON_TABLE** | Hàm Oracle parse chuỗi JSON thành bảng — cách truyền List từ Java vào DB |
| **FEFO** | First Expired, First Out — xuất lô gần hết hạn trước |
| **WAC** | Weighted Average Cost — giá vốn trung bình theo trọng số |
| **SERIALIZABLE** | Mức isolation cao nhất — snapshot tại thời điểm giao dịch bắt đầu |
| **CallableStatement** | Cách Java gọi Stored Procedure Oracle |
| **FROM DUAL** | Bảng ảo 1 dòng của Oracle — dùng để SELECT kết quả Function |

---

*Tài liệu này được tạo tự động từ source code thực tế — xem file gốc tại `database/` để kiểm tra chi tiết.*
