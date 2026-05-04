# UI SPECIFICATION — LA BOULANGERIE MANAGEMENT SYSTEM
> **Mục đích:** Định nghĩa toàn bộ design tokens, quy tắc component và layout chuẩn cho ứng dụng JavaFX.
> AI Agent BẮT BUỘC đọc file này trước khi viết bất kỳ file FXML hay CSS nào.

---

## 1. CÔNG NGHỆ & RÀNG BUỘC KỸ THUẬT

- **Nền tảng:** JavaFX 25
- **Styling:** CSS (`bakery.css`)
- **Font chữ:** Ưu tiên dùng font hệ thống hiện đại (Segoe UI, Inter) hoặc Google Fonts (Outfit).
- **Layout:** Sử dụng `BorderPane`, `GridPane`, `HBox`, `VBox`. TUYỆT ĐỐI KHÔNG dùng Absolute Positioning (AnchorPane với tọa độ cứng).
- **Màn hình tối thiểu hỗ trợ:** `1280 x 720px`.

---

## 2. BẢN SẮC MÀU (COLOR TOKENS)

### 2.1. Bảng màu chính — Amber (Màu nhận diện thương hiệu)

| Token | Mã màu HEX | Mô tả sử dụng |
|---|---|---|
| `COLOR_PRIMARY` | `#92400E` | Nút chính, sidebar item active, header accent |
| `COLOR_PRIMARY_HOVER` | `#78350F` | Trạng thái hover của nút chính |
| `COLOR_PRIMARY_LIGHT` | `#FDE68A` | Border mặc định, đường kẻ phân cách |
| `COLOR_PRIMARY_SUBTLE` | `#FEF3C7` | Nền hover menu, nền section phụ |

### 2.2. Màu nền (Background)

| Token | Mã màu HEX | Mô tả sử dụng |
|---|---|---|
| `COLOR_BG_APP` | `#FDFBF7` | Nền toàn ứng dụng (màu kem nhạt) |
| `COLOR_BG_SURFACE` | `#FFFFFF` | Nền card, panel, dialog, sidebar |
| `COLOR_BG_MUTED` | `#F9FAFB` | Nền input, nền row hover trong table |

---

## 3. TYPOGRAPHY & DATA FORMAT

### 3.1. Quy tắc định dạng dữ liệu
- **Tiền tệ:** Format `#,### đ`. VD: `65.000 đ`.
- **Ngày giờ:** Format `dd/MM/yyyy HH:mm`. VD: `25/04/2026 08:30`.
- **Mã ID:** Viết hoa, in đậm. VD: **DH001**.

---

## 4. COMPONENT STYLING (CSS)

### 4.1. Buttons
- **.btn-primary:** Nền `#92400E`, text trắng, bo góc `8px`.
- **.btn-secondary:** Nền trắng, border `#92400E`, text `#92400E`.
- **.btn-danger:** Nền `#DC2626`, text trắng.

### 4.2. Inputs (TextField, ComboBox)
- Border: `2px solid #FDE68A`.
- Focus: Border `#FCD34D`.
- Padding: `8px 12px`.

### 4.3. Tables (TableView)
- Header: Nền `#FEF3C7`, text `#78350F`, font-weight bold.
- Cell: Padding `10px`, row-height `40px`.
- Selection: Nền `#FEF3C7`, text `#1C0A00`.

---

## 5. LAYOUT STRUCTURE

### 5.1. Sidebar (256px)
- Nền: `COLOR_BG_SURFACE` (#FFFFFF).
- Border phải: `1px solid COLOR_PRIMARY_LIGHT`.
- Menu items: HBox/Button với icon và text, gap `12px`.

### 5.2. Header (64px)
- Nền: `COLOR_BG_SURFACE`.
- Border dưới: `1px solid COLOR_PRIMARY_LIGHT`.
- Tiêu đề màn hình: 20px Bold, màu `#92400E`.

### 5.3. Content Area
- Padding: `24px`.
- Nền: `COLOR_BG_APP`.

---

## 6. CHECKLIST UI TRƯỚC KHI GIAO NỘP
- [ ] Sử dụng đúng Color Tokens, không hardcode mã màu trong FXML/Java.
- [ ] Mọi TextField/ComboBox phải có Label đi kèm.
- [ ] TableView phải được căn chỉnh cột hợp lý (hết chiều ngang).
- [ ] Dialog phải có nút X đóng và footer nút căn phải.
- [ ] Luôn sử dụng CSS class thay vì inline style trong FXML.
