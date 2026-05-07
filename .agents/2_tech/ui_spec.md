# UI SPECIFICATION — LA BOULANGERIE MANAGEMENT SYSTEM
> **Mục đích:** Đây là **nguồn sự thật duy nhất (Single Source of Truth)** về thiết kế UI.
> AI Agent BẮT BUỘC đọc file này trước khi viết bất kỳ file FXML hay CSS nào.
> Review Agent BẮT BUỘC kiểm tra mọi file FXML đã sửa theo checklist trong file này.

---

## 1. CÔNG NGHỆ & RÀNG BUỘC KỸ THUẬT

- **Nền tảng:** JavaFX 25
- **Styling:** CSS (`bakery.css`) — **TUYỆT ĐỐI KHÔNG** viết inline style (`style="..."`) trong FXML
- **Font chữ:** Segoe UI (hệ thống), fallback: Inter, Outfit
- **Layout Root:** Mọi màn hình con PHẢI dùng `<VBox styleClass="bg-app">` làm root
- **Layout con:** Dùng `BorderPane`, `GridPane`, `HBox`, `VBox`. **CẤM** dùng `AnchorPane` với tọa độ cứng
- **Stylesheet:** KHÔNG khai báo `stylesheets="..."` trong FXML con — stylesheet được load từ `App.java` qua Scene
- **Màn hình tối thiểu:** `1280 x 720px`

---

## 2. BẢNG MÀU (COLOR TOKENS — H3K Branding)

### 2.1. Màu thương hiệu (Brand Colors)
| Token | HEX | CSS Class / Dùng cho |
|---|---|---|
| `COLOR_PRIMARY` | `#D85A30` | `.btn-primary`, hành động chính (Thanh toán, Xác nhận) |
| `COLOR_SECONDARY` | `#185FA5` | `.sidebar`, hành động phụ (Xem chi tiết, Điều hướng) |
| `COLOR_PRIMARY_HOVER`| `#993C1D` | Hover nút chính |
| `COLOR_BORDER` | `#D3D1C7` | Border mặc định, đường kẻ phân cách |

### 2.2. Màu nền & Văn bản
| Token | HEX | Dùng cho |
|---|---|---|
| `COLOR_BG_APP` | `#F1EFE8` | `.bg-app` (Màu kem nền toàn màn hình) |
| `COLOR_BG_CARD` | `#FFFFFF` | `.bg-surface`, `.card`, input background |
| `COLOR_TEXT_MAIN` | `#2C2C2A` | Tiêu đề, nội dung bảng, text quan trọng |
| `COLOR_TEXT_MUTED` | `#888780` | Nhãn (Label), Ghi chú, Hint |

### 2.3. Màu trạng thái (Status Badges)
| Trạng thái | Nền (Background) | Chữ (Text) | Dùng cho |
|---|---|---|---|
| Success | `#EAF3DE` | `#27500A` | Đã thanh toán, Hoàn thành |
| Warning | `#FAEEDA` | `#854F0B` | Đang xử lý, Chờ duyệt |
| Danger | `#FCEBEB` | `#791F1F` | Đã hủy, Lỗi hệ thống |

---

## 3. TYPOGRAPHY — CSS CLASS CHUẨN

| CSS Class | Font size | Weight | Color | Dùng cho |
|---|---|---|---|---|
| `.lbl-title-screen` | 22px | 800 | `#2C2C2A` | Tiêu đề màn hình (H1) |
| `.lbl-title-card` | 17px | Bold | `#2C2C2A` | Tiêu đề panel/card |
| `.lbl-title-dialog` | 22px | Bold | `#D85A30` | Tiêu đề dialog |
| `.lbl-body-bold` | 13px | Bold | `#2C2C2A` | Label input |
| `.lbl-body` | 13px | Normal | `#2C2C2A` | Mô tả, subtitle |
| `.lbl-small-bold` | 11px | Bold | `#888780` | Status bar, footer |
| `.lbl-price` | 20px | Bold | `#D85A30` | Hiển thị tiền |
| `.lbl-kpi` | 26px | Bold | `#2C2C2A` | Dashboard KPI |
| `.header-title` | 18px | Bold | `#2C2C2A` | Tiêu đề trong app-header |

> ❌ **CẤM** dùng `style="-fx-font-size: ...; -fx-text-fill: ..."` inline. Phải dùng CSS class.

---

## 4. COMPONENT — CSS CLASS CHUẨN

### 4.1. Buttons
| CSS Class | Nền | Text | Dùng cho |
|---|---|---|---|
| `.btn-primary` | `#D85A30` | Trắng | Hành động chính (Thanh toán, Lưu) |
| `.btn-secondary` | Trắng + border `#185FA5` | `#185FA5` | Quay lại, Làm mới, Xem chi tiết |
| `.btn-danger` | `#791F1F` | Trắng | Xóa, Hủy |
| `.btn-success` | `#27500A` | Trắng | Xác nhận thanh toán |
| `.btn-qty` | `#FAEEDA` | `#854F0B` | Tăng/giảm số lượng |

### 4.2. Inputs
| CSS Class | Dùng cho |
|---|---|
| `.text-field` | `TextField`, `TextArea`, `DatePicker` |
| `.combo-box` | `ComboBox` |
| `.check-box` | `CheckBox` |

### 4.3. Layout & Panel
| CSS Class | Dùng cho |
|---|---|
| `.bg-app` | Root VBox của màn hình con |
| `.card` | Panel form bên phải, detail section |
| `.app-header` | Header 64px toàn màn hình |
| `.sidebar` | Sidebar 220px |
| `.table-view` | Mọi `TableView` |

### 4.4. Badge trạng thái đơn
| CSS Class | Màu | Trạng thái |
|---|---|---|
| `.badge .badge-new` | Vàng nhạt | Mới |
| `.badge .badge-processing` | Xanh dương nhạt | Đang xử lý |
| `.badge .badge-done` | Xanh lá nhạt | Hoàn thành |
| `.badge .badge-cancelled` | Xám | Đã hủy |

---

## 5. TEMPLATE FXML — CẤU TRÚC BẮT BUỘC

### 5.1. Template màn hình quản lý (Master-Detail Layout)
Tất cả màn hình CRUD (NguyenLieu, SanPham, DanhMuc, v.v.) PHẢI tuân theo template:

```xml
<VBox xmlns="http://javafx.com/javafx/25"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="com.bakery.views.controllers.[Tên]ViewFXMLController"
      spacing="20" styleClass="bg-app">

    <padding><Insets top="30" right="40" bottom="30" left="40"/></padding>

    <!-- ① HEADER: Quay lại + Tiêu đề + Nút hành động chính -->
    <HBox alignment="CENTER_LEFT" spacing="15">
        <Button text="← Quay lại" styleClass="btn-secondary" onAction="#onQuayLai"/>
        <VBox spacing="5">
            <Label text="[Tiêu đề màn hình]" styleClass="lbl-title-screen"/>
            <Label text="[Mô tả ngắn]" styleClass="lbl-body"/>
        </VBox>
        <Region HBox.hgrow="ALWAYS"/>
        <Button fx:id="btnThemMoi" text="➕ Thêm mới" styleClass="btn-primary" onAction="#onThemMoi"/>
    </HBox>

    <!-- ② CONTENT: Left TableView + Right Form Card -->
    <HBox spacing="30" VBox.vgrow="ALWAYS">

        <!-- Left: TableView + Search -->
        <VBox spacing="10" HBox.hgrow="ALWAYS">
            <HBox spacing="10" alignment="CENTER_LEFT">
                <Label text="Danh sách [...]" styleClass="lbl-title-card"/>
                <Region HBox.hgrow="ALWAYS"/>
                <TextField fx:id="txtTimKiem" promptText="🔍 Tìm kiếm..."
                           prefWidth="220" onKeyReleased="#onTimKiem" styleClass="text-field"/>
            </HBox>
            <TableView fx:id="tbl[TenMan]" styleClass="table-view" VBox.vgrow="ALWAYS">
                <columnResizePolicy>
                    <TableView fx:constant="CONSTRAINED_RESIZE_POLICY"/>
                </columnResizePolicy>
            </TableView>
        </VBox>

        <!-- Right: Form Card -->
        <VBox spacing="20" styleClass="card" prefWidth="400" alignment="TOP_CENTER">
            <Label text="Chi tiết [...]" styleClass="lbl-title-card"/>

            <!-- Form fields (mỗi field = VBox spacing="8") -->
            <VBox spacing="8">
                <Label text="[Tên trường]: *" styleClass="lbl-body-bold"/>
                <TextField fx:id="txt[TenTruong]" promptText="..." styleClass="text-field"/>
            </VBox>

            <Region VBox.vgrow="ALWAYS"/>

            <!-- Action buttons -->
            <VBox spacing="12" maxWidth="Infinity">
                <Button fx:id="btnLuuThayDoi" text="💾 Lưu thay đổi"
                        styleClass="btn-primary" maxWidth="Infinity" onAction="#onLuuThayDoi"/>
                <Button fx:id="btnXoa" text="🗑 Xóa [...]"
                        styleClass="btn-danger" maxWidth="Infinity" onAction="#onXoa"/>
            </VBox>
        </VBox>

    </HBox>

    <!-- ③ FOOTER STATUS BAR -->
    <HBox>
        <Label fx:id="lblThongBao" styleClass="lbl-small-bold"/>
    </HBox>

</VBox>
```

### 5.2. Template Dialog
```xml
<VBox xmlns="http://javafx.com/javafx/25" xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="..." spacing="0">

    <!-- Header -->
    <HBox styleClass="dialog-header" alignment="CENTER_LEFT" spacing="12">
        <Label text="[Tiêu đề dialog]" styleClass="dialog-header-title"/>
    </HBox>

    <!-- Body -->
    <VBox styleClass="dialog-body" spacing="16" VBox.vgrow="ALWAYS">
        <!-- Nội dung form -->
    </VBox>

    <!-- Footer -->
    <HBox styleClass="dialog-footer" alignment="CENTER_RIGHT" spacing="10">
        <Button text="Hủy" styleClass="btn-secondary" onAction="#onHuy"/>
        <Button text="Xác nhận" styleClass="btn-primary" onAction="#onXacNhan"/>
    </HBox>
</VBox>
```

---

## 6. QUY TẮC ĐỒNG BỘ GIAO DIỆN (UI CONSISTENCY RULES)

### 6.1. Padding & Spacing chuẩn
| Vị trí | Giá trị |
|---|---|
| Root screen padding | `top="30" right="40" bottom="30" left="40"` |
| Header spacing | `spacing="15"` |
| Main content spacing | `spacing="30"` |
| Card nội bộ spacing | `spacing="20"` |
| Form field spacing | `spacing="8"` (label + input) |
| Giữa các field | `spacing="20"` (hoặc VBox wrapper riêng) |
| Action button spacing | `spacing="12"` |

### 6.2. prefWidth card bên phải
- Màn hình đơn giản (1-3 field): `prefWidth="400"`
- Màn hình trung bình (4-6 field): `prefWidth="420"`
- Màn hình phức tạp (>6 field, có ảnh): `prefWidth="450"`

### 6.3. ID Naming Convention (bắt buộc đồng bộ)
| Component | Prefix | VD |
|---|---|---|
| Button | `btn` | `btnThemMoi`, `btnLuuThayDoi`, `btnXoa` |
| TextField | `txt` | `txtTenSP`, `txtTimKiem` |
| Label | `lbl` | `lblThongBao`, `lblTen` |
| TableView | `tbl` | `tblSanPham`, `tblDanhMuc` |
| TableColumn | `col` | `colMaSP`, `colTenSP` |
| ComboBox | `cmb` | `cmbDanhMuc`, `cmbDonViTinh` |
| CheckBox | `chk` | `chkTuyChinh` |
| DatePicker | `dp` | `dpNgay` |
| ScrollPane | `sp` | `spDanhSach` |
| Panel/VBox | `panel` | `panelForm` |

---

## 7. CHECKLIST UI TRƯỚC KHI COMMIT (BẮT BUỘC)

### ✅ Cấu trúc
- [ ] Root element là `<VBox styleClass="bg-app">` (không phải BorderPane hay AnchorPane)
- [ ] Padding: `top="30" right="40" bottom="30" left="40"`
- [ ] Có header với nút **"← Quay lại"** (`btn-secondary`) ở góc trên trái
- [ ] Có footer `<Label fx:id="lblThongBao" styleClass="lbl-small-bold"/>`
- [ ] TableView có `CONSTRAINED_RESIZE_POLICY` và `styleClass="table-view"`

### ✅ Styling
- [ ] **KHÔNG có bất kỳ** `style="..."` inline trong FXML
- [ ] **KHÔNG có** `stylesheets="@../css/bakery.css"` trong FXML con
- [ ] Mọi Button dùng đúng `styleClass`: `btn-primary` / `btn-secondary` / `btn-danger`
- [ ] Mọi Label dùng đúng `styleClass`: `lbl-title-screen` / `lbl-title-card` / `lbl-body-bold` / v.v.
- [ ] Mọi TextField/ComboBox dùng `styleClass="text-field"` / `"combo-box"`

### ✅ Layout
- [ ] Không dùng `AnchorPane` với `layoutX`, `layoutY`
- [ ] `Region HBox.hgrow="ALWAYS"` dùng để đẩy nút về phải
- [ ] Form card bên phải có `alignment="TOP_CENTER"` và đúng `prefWidth`

### ✅ Naming
- [ ] Mọi `fx:id` đúng prefix: `btn`, `txt`, `lbl`, `tbl`, `col`, `cmb`, `chk`, `dp`
- [ ] Controller class đúng tên: `[TenManHinh]ViewFXMLController`
- [ ] Event handler đúng format: `#onThemMoi`, `#onLuuThayDoi`, `#onXoa`, `#onQuayLai`

---

## 8. CÁC LỖI THƯỜNG GẶP (ANTI-PATTERNS)

| Lỗi | Ví dụ sai | Sửa thành |
|---|---|---|
| Inline style màu | `style="-fx-background-color: #92400E"` | `styleClass="btn-primary"` |
| Inline style font | `style="-fx-font-size: 24px; -fx-font-weight: bold"` | `styleClass="lbl-title-screen"` |
| Khai báo stylesheet trong FXML | `stylesheets="@../css/bakery.css"` | Xóa — load từ App.java |
| Root sai layout | `<BorderPane>` làm root màn hình con | `<VBox styleClass="bg-app">` |
| TableView thiếu style | `<TableView fx:id="tv...">` | Thêm `styleClass="table-view"` |
| fx:id không theo prefix | `fx:id="tableView"`, `fx:id="searchField"` | `tblSanPham`, `txtTimKiem` |
| Button không đúng class | `styleClass="button"` mặc định | Chỉ định rõ `btn-primary` / `btn-secondary` |
