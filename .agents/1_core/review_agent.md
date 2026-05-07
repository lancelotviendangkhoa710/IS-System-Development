# REVIEW AGENT PROTOCOL — HẬU KIỂM TỰ ĐỘNG SAU CODE

> **Mục đích:** Sau khi hoàn thành bất kỳ task nào (feature, fix, refactor), AI BẮT BUỘC chạy quy trình Review Agent này trước khi báo cáo "DONE" với User.
> Không có exception. Không có ngoại lệ dù là hotfix nhỏ.

---

## PHASE 0 — KÍCH HOẠT (Trigger)
Review Agent được kích hoạt TỰ ĐỘNG sau khi:
- Viết xong file Java mới (DAO, Service, Presenter, Controller).
- Sửa xong bất kỳ file `.java`, `.fxml`, `.css`, `.sql` nào.
- Trước mỗi lần Commit.

---

## PHASE 1 — PHÂN TÍCH TÁC ĐỘNG (Impact Scan)

### 1.1. Blast Radius Check
Với MỖI symbol (function/class/method) đã sửa, bắt buộc chạy:
```
gitnexus_impact({ target: "<tên symbol>", direction: "upstream" })
```
- Nếu risk = **HIGH / CRITICAL** → DỪNG, cảnh báo User, chờ xác nhận.
- Nếu risk = **LOW / MEDIUM** → ghi nhận, tiếp tục.

### 1.2. Change Scope Verification
```
gitnexus_detect_changes()
```
- Kiểm tra xem có file nào bị sửa nằm ngoài phạm vi task không.
- Nếu có → giải thích lý do hoặc hoàn tác thay đổi ngoài phạm vi.

---

## PHASE 2 — KIỂM TRA KIẾN TRÚC MVP (Architecture Audit)

Kiểm tra lần lượt các file đã sửa theo checklist sau:

### 2.1. Tầng View / FXML Controller
| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| V1 | Không có SQL/DAO call trong Controller | Chuyển sang Presenter |
| V2 | Không có business logic / tính toán | Chuyển sang Service |
| V3 | Không có inline style hardcode | Thay bằng CSS class |
| V4 | Màu sắc phải dùng Amber Palette CSS class | Sửa sang `getStyleClass().add(...)` |
| V5 | Có nút "← Quay lại Menu chính" | Thêm nếu thiếu |
| V6 | Layout không dùng AnchorPane với tọa độ cứng | Chuyển sang BorderPane/GridPane |

### 2.2. Tầng Presenter
| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| P1 | Không import thư viện JavaFX (trừ Platform.runLater) | Tách logic ra |
| P2 | Không gọi DAO trực tiếp (phải qua Service) | Thêm Service layer |
| P3 | Gọi View qua Interface (không reference class trực tiếp) | Refactor |
| P4 | Mọi lỗi phải được bắt và truyền qua View interface | Thêm try-catch |

### 2.3. Tầng Service
| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| S1 | Không chứa SQL hoặc JDBC code | Chuyển sang DAO |
| S2 | Validation input đầy đủ trước khi gọi DAO | Thêm validation |
| S3 | Không trả về null (trả về Optional hoặc List rỗng) | Sửa return type |
| S4 | Mọi business rule phức tạp có comment tiếng Việt | Thêm comment |

### 2.4. Tầng DAO
| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| D1 | Dùng PreparedStatement/CallableStatement (không nối chuỗi SQL) | Fix SQL Injection |
| D2 | Dùng try-with-resources cho Connection/Statement/ResultSet | Wrap bằng TWR |
| D3 | Mọi CUD phải qua Stored Procedure | Chuyển sang PROC call |
| D4 | Xử lý null từ ResultSet (wasNull(), NVL) | Thêm null guard |
| D5 | Trả về ArrayList rỗng thay vì null | Fix return |

### 2.5. Tầng DTO
| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| DTO1 | Thuộc tính private + đủ Getter/Setter | Thêm |
| DTO2 | Constructor rỗng + Constructor đầy đủ | Thêm |
| DTO3 | Nullable column → Wrapper class (Integer, Double) | Sửa type |
| DTO4 | Không dùng `java.util.Date` hay `java.sql.Date` | Thay bằng `LocalDate`/`LocalDateTime` |

---

## PHASE 2.5 — KIỂM TRA ĐỒNG BỘ GIAO DIỆN (UI Consistency Audit)

Với mỗi file `.fxml` đã sửa, kiểm tra theo checklist từ `ui_spec.md`:

| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| UI1 | Root element là `<VBox styleClass="bg-app">` (không phải BorderPane/AnchorPane) | Refactor cấu trúc |
| UI2 | Padding: `top="30" right="40" bottom="30" left="40"` | Sửa Insets |
| UI3 | **KHÔNG có** `style="..."` inline bất kỳ đâu trong file | Xóa, thay bằng CSS class |
| UI4 | **KHÔNG có** `stylesheets="@../css/bakery.css"` trong FXML con | Xóa khai báo |
| UI5 | Có `<Button text="← Quay lại" styleClass="btn-secondary">` trong header | Thêm nếu thiếu |
| UI6 | Có `<Label fx:id="lblThongBao" styleClass="lbl-small-bold"/>` ở footer | Thêm nếu thiếu |
| UI7 | TableView có `styleClass="table-view"` và `CONSTRAINED_RESIZE_POLICY` | Sửa |
| UI8 | Mọi Button dùng đúng class: `btn-primary`/`btn-secondary`/`btn-danger` | Sửa styleClass |
| UI9 | Mọi TextField/ComboBox dùng `styleClass="text-field"`/`"combo-box"` | Sửa styleClass |
| UI10 | Mọi Label dùng đúng class (không hardcode size/color) | Sửa styleClass |
| UI11 | `fx:id` đúng prefix: `btn`, `txt`, `lbl`, `tbl`, `col`, `cmb`, `chk`, `dp` | Đổi tên + sync Controller |
| UI12 | Event handler đúng format: `#onThemMoi`, `#onLuuThayDoi`, `#onXoa` | Đổi tên + sync |
| UI13 | Form card bên phải có `styleClass="card"` và `alignment="TOP_CENTER"` | Sửa |
| UI14 | Spacing đồng bộ: root `20`, header `15`, content `30`, card `20`, field `8` | Chỉnh |

> Chi tiết template và bảng màu: xem `/.agents/2_tech/ui_spec.md`.

---

## PHASE 3 — KIỂM TRA DATABASE (SQL / PL-SQL Audit)

Với mỗi file `.sql` đã sửa:

| # | Kiểm tra | Hành động nếu vi phạm |
|---|---|---|
| DB1 | Keywords SQL viết HOA | Sửa |
| DB2 | Tham số đầu vào có tiền tố `P_` | Đổi tên |
| DB3 | Biến cục bộ có tiền tố `V_` | Đổi tên |
| DB4 | Không hardcode ID (SELECT động theo tên) | Refactor |
| DB5 | Có EXCEPTION block với ROLLBACK + RAISE_APPLICATION_ERROR | Thêm |
| DB6 | Thông báo lỗi tiếng Việt CÓ DẤU | Sửa |
| DB7 | Có header comment mô tả Procedure/Function | Thêm |
| DB8 | COMMIT chỉ gọi 1 lần cuối luồng chính | Kiểm tra |
| DB9 | NVL bọc các phép SUM/MIN/MAX | Thêm |

---

## PHASE 4 — KIỂM TRA NAMING CONVENTION

Quét toàn bộ file đã sửa:

### Java:
- [ ] Biến/Hàm: `camelCase`, thuần Việt không dấu (VD: `layDanhSachSanPham`)
- [ ] Class: `PascalCase` (VD: `SanPhamDAO`)
- [ ] Hằng số: `UPPER_SNAKE_CASE`
- [ ] Không mix Vinglish (VD: cấm `getSanPhamList` khi có nghĩa rõ)
- [ ] Prefix FXML widget: `btn`, `lbl`, `txt`, `tbl`, `cmb`

### SQL:
- [ ] Object trong DB: `UPPER_CASE` + tiếng Việt không dấu
- [ ] File SQL: `lowercase` + tiền tố `proc_`, `func_`, `trg_`
- [ ] Constraint: `CK_[BANG]_[COT]`

---

## PHASE 5 — BÁO CÁO KẾT QUẢ (Review Report)

Sau khi hoàn thành tất cả phase, trả về báo cáo theo format:

```
### ✅ REVIEW AGENT REPORT
**Task:** [Tên task vừa làm]
**Files checked:** [Danh sách file]

#### Lỗi đã tự fix:
- [FILE] [Line] [Mô tả lỗi] → [Hành động đã thực hiện]

#### Cảnh báo (cần User xác nhận):
- [FILE] [Mô tả vấn đề] → [Đề xuất hành động]

#### GitNexus Risk Summary:
- Impact Risk: LOW / MEDIUM / HIGH / CRITICAL
- Changed symbols: [Danh sách]
- Affected processes: [Danh sách]

#### Status: ✅ CLEAN | ⚠️ WARNINGS | ❌ BLOCKED
```

---

## QUY TẮC HÀNH VI CỦA REVIEW AGENT

1. **Tự động fix (Zero-Permission):** Các lỗi cấp độ thấp (naming, null guard, missing comment, empty catch) → TỰ ĐỘNG SỬA không cần hỏi User.
2. **Cảnh báo và chờ (Ask-Before-Fix):** Các thay đổi liên quan tới DB Schema, số tham số Procedure, tái cấu trúc Interface → PHẢI HỎI User trước.
3. **Chặn hoàn toàn (Hard Block):** Nếu phát hiện SQL Injection (nối chuỗi SQL), lưu order chưa thanh toán, SQL trong View → PHẢI ROLLBACK ngay và báo cáo.
4. **Boy Scout Rule:** Nếu phát hiện code dơ trong file đang sửa (không liên quan task nhưng cùng file) → dọn luôn, ghi vào báo cáo phần "Bonus cleanup".
