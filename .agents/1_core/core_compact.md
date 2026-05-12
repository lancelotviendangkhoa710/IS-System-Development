# CORE RULES — Bakery Management System

## STACK
Java 21 / JavaFX 25 / Oracle 12c+ / Maven / Gson / PDFBox / JasperReports

## KIẾN TRÚC MVP (TUYỆT ĐỐI)
| Tầng | Package | Trách nhiệm | CẤM |
|---|---|---|---|
| View | `views.controllers` | Bắt sự kiện, gọi Presenter | SQL, logic nghiệp vụ, hardcode CSS |
| Presenter | `presenters` | Orchestrator View↔Service | Import JavaFX (trừ Platform.runLater), gọi DAO trực tiếp |
| Service | `services` | Business logic, tính toán | SQL, JDBC |
| DAO | `model.dao` | JDBC + Stored Procedure | Logic nghiệp vụ |
| DTO | `model.dto` | POJO thuần | Logic |

**Hard Block (rollback ngay):** SQL trong View/Presenter · Lưu đơn chưa thanh toán · SQL Injection (nối chuỗi)

## NAMING CONVENTION
**Java — camelCase tiếng Việt không dấu:**
- Biến/Hàm: `layDanhSachSanPham`, `tinhTienHoaDon`
- Prefix động từ Anh OK: `getSanPham()`, `updateKhachHang()`
- Class: PascalCase + suffix chuẩn: `SanPhamDAO`, `KhachHangDTO`, `DonHangService`, `BanHangPresenter`, `TaoDonHangViewFXMLController`
- Hằng số: `MAX_SO_LUONG`, `LUONG_CO_BAN`
- FXML fx:id prefix: `btn` `txt` `lbl` `tbl` `col` `cmb` `chk` `dp`
- Event handler: `#onThemMoi`, `#onLuuThayDoi`, `#onXoa`, `#onQuayLai`

**SQL/PL-SQL — UPPER_CASE:**
- Object DB: `PROC_TAOHOADON`, `FUNC_TINHGIABANHTUYCHINH`, `TRG_CAPNHATKHO`
- File vật lý: `proc_tao_hoadon.sql`, `func_tinh_gia.sql`
- Tham số: `P_MASP`, `P_MADON` | Biến cục bộ: `V_TONG`, `V_MADON`
- Constraint: `CK_DON_THANHTOAN`, `CK_KH_DIEM`

## QUY TẮC JAVA CỐT LÕI
- **DB:** `PreparedStatement` (SQL) / `CallableStatement` (Procedure). CẤM nối chuỗi SQL.
- **Time:** `LocalDate` / `LocalDateTime`. CẤM `java.util.Date` / `java.sql.Date` trong DTO.
- **Thread:** Tác vụ nặng → `Task`/`Service` JavaFX. Update UI từ thread phụ → `Platform.runLater()`.
- **Null:** `rs.wasNull()` cho số. `getTimestamp() != null` trước `.toLocalDateTime()`.
- **Collection:** Trả `ArrayList` rỗng, không trả `null`.
- **Resource:** Try-with-resources cho Connection/Statement/ResultSet.
- **DTO:** private fields + constructor rỗng + constructor đầy đủ + getter/setter. Nullable column → Wrapper (`Integer`, `Double`).

## QUY TẮC SQL/PL-SQL CỐT LÕI
- SQL keywords: VIẾT HOA. Header comment tiếng Việt có dấu trên CREATE OR REPLACE.
- COMMIT chỉ gọi 1 lần cuối, ngay trước EXCEPTION block.
- Mọi Procedure DML phải có: `EXCEPTION WHEN OTHERS THEN ROLLBACK; RAISE_APPLICATION_ERROR(-20xxx, 'Lỗi tiếng Việt có dấu: ' || SQLERRM);`
- CẤM hardcode ID trạng thái — phải SELECT động theo tên.
- CẤM tự tạo/sửa Procedure/Function/Trigger khi chưa được User xác nhận.
- Trước khi tạo mới: grep/gitnexus kiểm tra trùng lặp trong `database/`.
- List Java → Oracle: JSON string + `JSON_TABLE`.
- `NVL(SUM(...), 0)` bọc mọi phép tính có thể NULL.

## UI — AMBER PALETTE (CẤM INLINE STYLE)
Root: `<VBox styleClass="bg-app">` · Padding: `top="30" right="40" bottom="30" left="40"`
- Màu chính: `#D85A30` (btn-primary) · Phụ: `#185FA5` (btn-secondary, sidebar)
- Nền app: `#F1EFE8` · Card: `#FFFFFF` · Text: `#2C2C2A`
- CẤM `AnchorPane` tọa độ cứng · CẤM `stylesheets=` trong FXML con (load từ App.java)
- Stylesheet: `bakery.css` — tham chiếu `ui_spec.md` cho toàn bộ CSS class và FXML template.

## VIBE CODING
1. **Zero-Permission:** Tự fix naming sai, CSS hardcode, null guard thiếu, empty catch.
2. **Ask-Before-Fix:** Sửa DB schema, số param Procedure, refactor Interface.
3. **Hard Block + Rollback:** SQL Injection, SQL trong View, lưu đơn chưa thanh toán.
4. **Boy Scout:** Chạm file nào, dọn file đó.
5. **No Vinglish:** `layDonHang` ✅ `getSanPhamList` ❌
6. Đọc DB trước, codebase Java sau. Comment ngắn gọn (tránh comment get/set).

## GITNEXUS (BẮT BUỘC)
- Trước khi sửa bất kỳ symbol: `gitnexus_impact({target, direction: "upstream"})` → báo blast radius.
- HIGH/CRITICAL → DỪNG, cảnh báo User trước.
- Rename: dùng `gitnexus_rename`, không find-and-replace.
- Trước commit: `gitnexus_detect_changes()`.
