---
description: Review Agent — chạy SAU KHI viết xong code, TRƯỚC KHI commit
---

# REVIEW AGENT PROTOCOL

> Kích hoạt sau khi sửa bất kỳ `.java`, `.fxml`, `.css`, `.sql`. Không có ngoại lệ.

## PHASE 1 — IMPACT SCAN
```
gitnexus_impact({ target: "<symbol>", direction: "upstream" })
```
- HIGH/CRITICAL → DỪNG, báo User.
- LOW/MEDIUM → ghi nhận, tiếp tục.
```
gitnexus_detect_changes()   // kiểm tra file ngoài scope
```

## PHASE 2 — MVP AUDIT (tự fix lỗi thấp)

**View/FXML:** V1 Không SQL/DAO · V2 Không business logic · V3 Không inline style · V4 Dùng Amber CSS class · V5 Có "← Quay lại" · V6 Không AnchorPane tọa độ cứng

**Presenter:** P1 Không import JavaFX (trừ Platform.runLater) · P2 Không gọi DAO trực tiếp · P3 Gọi View qua Interface · P4 Mọi lỗi truyền qua View interface

**Service:** S1 Không SQL/JDBC · S2 Validate input trước khi gọi DAO · S3 Không trả null · S4 Business rule phức tạp có comment tiếng Việt

**DAO:** D1 PreparedStatement/CallableStatement · D2 Try-with-resources · D3 CUD qua Procedure · D4 Null guard từ ResultSet · D5 Trả ArrayList rỗng

**DTO:** DTO1 private + getter/setter · DTO2 Constructor rỗng + đầy đủ · DTO3 Nullable → Wrapper class · DTO4 LocalDate/LocalDateTime (không dùng java.util.Date)

## PHASE 2.5 — UI AUDIT (chỉ khi có thay đổi FXML)

UI1 Root `<VBox styleClass="bg-app">` · UI2 Padding `30/40/30/40` · UI3 Không inline `style=` · UI4 Không `stylesheets=` trong FXML con · UI5 Có nút "← Quay lại" · UI6 Có `lblThongBao` footer · UI7 TableView có `styleClass="table-view"` + CONSTRAINED_RESIZE · UI8 Button đúng class btn-primary/secondary/danger · UI9 Input dùng `text-field`/`combo-box` · UI10 Label dùng CSS class · UI11 fx:id đúng prefix · UI12 Event handler `#onXxx`

> Chi tiết CSS class và FXML template: `/.agents/2_tech/ui_spec.md`

## PHASE 3 — SQL AUDIT (chỉ khi có thay đổi .sql)

DB1 Keywords VIẾT HOA · DB2 Param tiền tố `P_` · DB3 Biến cục bộ tiền tố `V_` · DB4 Không hardcode ID · DB5 EXCEPTION block + ROLLBACK + RAISE_APPLICATION_ERROR · DB6 Lỗi tiếng Việt CÓ DẤU · DB7 Header comment · DB8 COMMIT 1 lần cuối · DB9 NVL bọc SUM/MIN/MAX

## PHASE 4 — NAMING SCAN

Java: camelCase thuần Việt · PascalCase class · UPPER_SNAKE hằng số · Không Vinglish thuần (`layDonHang` ✅, `getSanPhamList` ❌ nếu có nghĩa rõ hơn)
SQL: UPPER_CASE object · file lowercase + prefix proc_/func_/trg_ · Constraint `CK_BANG_COT`

## PHASE 5 — REPORT

```
### ✅ REVIEW AGENT REPORT
Task: [Tên task]
Files checked: [Danh sách]

Lỗi đã tự fix:
- [FILE] L[N] [Mô tả] → [Hành động]

Cảnh báo (cần User xác nhận):
- [FILE] [Vấn đề] → [Đề xuất]

GitNexus Risk: LOW/MEDIUM/HIGH/CRITICAL
Changed symbols: [...]
Affected processes: [...]

Status: ✅ CLEAN | ⚠️ WARNINGS | ❌ BLOCKED
```

## QUY TẮC HÀNH VI
- **Tự fix:** naming, null guard, empty catch, missing comment.
- **Hỏi trước:** DB schema, số param Procedure, refactor Interface.
- **Hard Block:** SQL Injection, SQL trong View, lưu order chưa thanh toán.
