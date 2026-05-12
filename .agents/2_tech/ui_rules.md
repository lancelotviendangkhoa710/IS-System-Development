# UI RULES — LA BOULANGERIE (Single Source of Truth)
> Đọc file này khi **sửa** FXML/CSS. Tạo màn hình **mới**: đọc thêm `ui_templates.md`.

## 1. RÀNG BUỘC KỸ THUẬT
- JavaFX 25 · Styling: `bakery.css` · **CẤM** inline `style="..."` trong FXML
- Root mọi màn hình con: `<VBox styleClass="bg-app">` · Padding: `top="30" right="40" bottom="30" left="40"`
- Layout: BorderPane/GridPane/HBox/VBox · **CẤM** AnchorPane tọa độ cứng
- **CẤM** `stylesheets=` trong FXML con — load từ App.java · Tối thiểu: 1280×720px

## 2. BẢNG MÀU (H3K Branding)
| Token | HEX | CSS / Dùng cho |
|---|---|---|
| Primary | `#D85A30` | `.btn-primary`, hành động chính |
| Secondary | `#185FA5` | `.sidebar`, `.btn-secondary` |
| Primary Hover | `#993C1D` | Hover nút chính |
| Border | `#D3D1C7` | Đường kẻ phân cách |
| BG App | `#F1EFE8` | `.bg-app` nền màn hình |
| BG Card | `#FFFFFF` | `.card`, input background |
| Text Main | `#2C2C2A` | Tiêu đề, nội dung bảng |
| Text Muted | `#888780` | Label, ghi chú |

Status badges: Success `#EAF3DE`/`#27500A` · Warning `#FAEEDA`/`#854F0B` · Danger `#FCEBEB`/`#791F1F`

## 3. TYPOGRAPHY — CSS CLASS CHUẨN
| CSS Class | Size | Weight | Dùng cho |
|---|---|---|---|
| `.lbl-title-screen` | 22px | 800 | Tiêu đề màn hình H1 |
| `.lbl-title-card` | 17px | Bold | Tiêu đề panel/card |
| `.lbl-title-dialog` | 22px | Bold | Tiêu đề dialog |
| `.lbl-body-bold` | 13px | Bold | Label input |
| `.lbl-body` | 13px | Normal | Mô tả, subtitle |
| `.lbl-small-bold` | 11px | Bold | Status bar, footer |
| `.lbl-price` | 20px | Bold | Hiển thị tiền |
| `.lbl-kpi` | 26px | Bold | Dashboard KPI |
| `.header-title` | 18px | Bold | Tiêu đề app-header |

> ❌ CẤM `style="-fx-font-size:...; -fx-text-fill:..."` inline.

## 4. COMPONENT CSS CLASS
**Buttons:** `.btn-primary` · `.btn-secondary` · `.btn-danger` · `.btn-success` · `.btn-qty`

**Inputs:** `.text-field` (TextField/TextArea/DatePicker) · `.combo-box` · `.check-box`

**Layout:** `.bg-app` (root) · `.card` (form panel) · `.app-header` (64px) · `.sidebar` (220px) · `.table-view` (mọi TableView)

**Badges:** `.badge .badge-new` · `.badge .badge-processing` · `.badge .badge-done` · `.badge .badge-cancelled`

## 5. PADDING & SPACING
Root: `30/40/30/40` · Header: `spacing="15"` · Content: `spacing="30"` · Card: `spacing="20"` · Field: `spacing="8"` · Action btn: `spacing="12"`

Card prefWidth: 1–3 field `400` · 4–6 field `420` · >6 field `450`

## 6. FX:ID PREFIX (bắt buộc)
`btn` Button · `txt` TextField · `lbl` Label · `tbl` TableView · `col` TableColumn · `cmb` ComboBox · `chk` CheckBox · `dp` DatePicker · `sp` ScrollPane · `panel` Panel/VBox

## 7. CHECKLIST TRƯỚC COMMIT
**Cấu trúc:** Root `<VBox styleClass="bg-app">` · Padding `30/40/30/40` · Có nút "← Quay lại" (`btn-secondary`) · Có `<Label fx:id="lblThongBao" styleClass="lbl-small-bold"/>` footer · TableView: `styleClass="table-view"` + `CONSTRAINED_RESIZE_POLICY`

**Styling:** Không `style=` inline · Không `stylesheets=` trong FXML con · Button đúng `btn-*` · Label đúng `lbl-*` · Input: `text-field`/`combo-box`

**Layout:** Không AnchorPane tọa độ cứng · `Region HBox.hgrow="ALWAYS"` đẩy nút phải · Card `alignment="TOP_CENTER"` + đúng prefWidth

**Naming:** fx:id đúng prefix · Controller `[TenManHinh]ViewFXMLController` · Handler `#onThemMoi`, `#onLuuThayDoi`, `#onXoa`, `#onQuayLai`

## 8. ANTI-PATTERNS
| Sai | Đúng |
|---|---|
| `style="-fx-background-color: #92400E"` | `styleClass="btn-primary"` |
| `style="-fx-font-size: 24px"` | `styleClass="lbl-title-screen"` |
| `stylesheets="@../css/bakery.css"` trong FXML con | Xóa — load từ App.java |
| `<BorderPane>` làm root màn hình con | `<VBox styleClass="bg-app">` |
| `fx:id="tableView"` | `tblSanPham` |
| `styleClass="button"` mặc định | `btn-primary` / `btn-secondary` |
