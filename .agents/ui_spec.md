# UI SPECIFICATION — LA BOULANGERIE MANAGEMENT SYSTEM
> **Mục đích:** Định nghĩa toàn bộ design tokens, quy tắc component và layout chuẩn cho ứng dụng Java Swing.
> AI Agent BẮT BUỘC đọc file này trước khi viết bất kỳ file View (JPanel/JDialog) hoặc style nào.

---

## 1. CÔNG NGHỆ & RÀNG BUỘC KỸ THUẬT

- **Nền tảng:** Java Swing + FlatLaf (IntelliJ theme)
- **Font chữ:** `new Font("Segoe UI", Font.PLAIN, size)` theo thứ tự ưu tiên. Fallback sang `SansSerif` nếu không có.
- **Icon:** Thư viện **FlatLaf Extras** hoặc dùng `ImageIcon` từ thư mục `resources/images/`. Có thể dùng Unicode emoji cho icon đơn giản.
- **Đơn vị kích thước:** Tất cả dùng `px` tuyệt đối khi set `setPreferredSize`, `setBorder`, `setFont`. KHÔNG dùng layout cứng tọa độ — bắt buộc dùng Layout Manager.
- **Màn hình tối thiểu hỗ trợ:** `1280 x 720px`.

---

## 2. BẢN SẮC MÀU (COLOR TOKENS)

### 2.1. Bảng màu chính — Amber (Màu nhận diện thương hiệu)

| Token | Mã màu HEX | Mô tả sử dụng |
|---|---|---|
| `COLOR_PRIMARY` | `#92400E` | Nút chính, sidebar item active, header accent |
| `COLOR_PRIMARY_HOVER` | `#78350F` | Trạng thái hover của nút chính |
| `COLOR_PRIMARY_LIGHT` | `#FDE68A` | Border mặc định, đường kẻ phân cách |
| `COLOR_PRIMARY_SUBTLE` | `#FEF3C7` | Nền hover menu, nền section phụ, nền table header |
| `COLOR_PRIMARY_TEXT` | `#78350F` | Tiêu đề, text nhấn mạnh màu amber |
| `COLOR_PRIMARY_MUTED` | `#D97706` | Text phụ màu amber, label, icon phụ |

### 2.2. Màu nền (Background)

| Token | Mã màu HEX | Mô tả sử dụng |
|---|---|---|
| `COLOR_BG_APP` | `#FDFBF7` | Nền toàn ứng dụng (màu kem nhạt) |
| `COLOR_BG_SURFACE` | `#FFFFFF` | Nền card, panel, dialog, sidebar, header |
| `COLOR_BG_MUTED` | `#F9FAFB` | Nền input, nền row hover trong table |
| `COLOR_BG_OVERLAY` | `rgba(0,0,0,0.5)` | Nền phủ mờ khi mở Dialog/Modal |

### 2.3. Màu trạng thái (Semantic Colors)

| Token | Mã màu | Dùng cho |
|---|---|---|
| `COLOR_SUCCESS` | `#16A34A` | Trạng thái hoàn thành, icon xác nhận |
| `COLOR_SUCCESS_BG` | `#DCFCE7` | Nền badge/tag trạng thái thành công |
| `COLOR_WARNING` | `#D97706` | Cảnh báo tồn kho, đơn sắp đến hạn |
| `COLOR_WARNING_BG` | `#FEF3C7` | Nền badge/tag cảnh báo |
| `COLOR_DANGER` | `#DC2626` | Lỗi validation, đơn khẩn cấp, tồn kho nguy hiểm |
| `COLOR_DANGER_BG` | `#FEE2E2` | Nền badge/tag nguy hiểm |
| `COLOR_INFO` | `#2563EB` | Trạng thái đang xử lý, thông tin trung tính |
| `COLOR_INFO_BG` | `#DBEAFE` | Nền badge/tag thông tin |
| `COLOR_PURPLE` | `#7C3AED` | Trạng thái chờ giao / chờ khách lấy |
| `COLOR_PURPLE_BG` | `#EDE9FE` | Nền badge/tag trạng thái chờ |

### 2.4. Màu text & border

| Token | Mã màu | Dùng cho |
|---|---|---|
| `COLOR_TEXT_PRIMARY` | `#1C0A00` | Text nội dung chính (amber-950) |
| `COLOR_TEXT_SECONDARY` | `#6B7280` | Text phụ, placeholder, hint, mô tả |
| `COLOR_TEXT_DISABLED` | `#D1D5DB` | Text bị vô hiệu hóa |
| `COLOR_BORDER` | `#FDE68A` | Border mặc định nhạt (amber-100) |
| `COLOR_BORDER_FOCUS` | `#FCD34D` | Border khi focus/active (amber-300) |

---

## 3. TYPOGRAPHY

### 3.1. Bảng kích thước & kiểu chữ

| Token | Size | Weight | Dùng cho |
|---|---|---|---|
| `TEXT_TITLE_SCREEN` | `20px` | Bold (700) | Tiêu đề màn hình trên Header |
| `TEXT_TITLE_CARD` | `17px` | Bold (700) | Tiêu đề card, tiêu đề panel section |
| `TEXT_TITLE_DIALOG` | `22px` | Bold (700) | Tiêu đề Dialog / Modal |
| `TEXT_BODY_LG` | `15px` | Regular (400) | Nội dung chính cần dễ đọc |
| `TEXT_BODY` | `13px` | Regular (400) | Text thông thường trong table, form |
| `TEXT_BODY_BOLD` | `13px` | Bold (700) | Text nhấn mạnh, giá tiền, mã đơn hàng |
| `TEXT_SMALL` | `11px` | Regular (400) | Hint, mô tả phụ dưới input |
| `TEXT_SMALL_BOLD` | `11px` | Bold (700) | Label form viết hoa, category tag |
| `TEXT_PRICE` | `20px` | Bold (700) | Tổng tiền trong giỏ hàng, hóa đơn |
| `TEXT_KPI` | `26px` | Bold (700) | Số liệu KPI trên Dashboard |

### 3.2. Quy tắc định dạng dữ liệu

- **Tiền tệ:** Dùng `NumberFormat` format `#,###` + ký tự `đ`. VD: `65.000đ`, `1.250.000đ`
- **Ngày giờ:** Format `dd/MM/yyyy HH:mm`. VD: `28/10/2024 18:00`
- **Mã ID:** Luôn dùng `TEXT_BODY_BOLD` + màu `COLOR_PRIMARY_TEXT`. VD: `DH001`, `KH03`
- **Số lượng âm / chênh lệch:** Màu `COLOR_DANGER` nếu âm, `COLOR_SUCCESS` nếu dương

---

## 4. SPACING & LAYOUT

### 4.1. Spacing scale

| Token | Giá trị | Dùng cho |
|---|---|---|
| `SPACE_XS` | `4px` | Gap icon — text, padding bên trong tag nhỏ |
| `SPACE_SM` | `8px` | Padding nút nhỏ, gap giữa các item nhỏ |
| `SPACE_MD` | `12px` | Padding cell trong table, gap form fields |
| `SPACE_LG` | `16px` | Padding card nội dung, khoảng giữa sections |
| `SPACE_XL` | `24px` | Padding màn hình content, padding dialog |
| `SPACE_2XL` | `32px` | Khoảng cách lớn giữa nhóm section |

### 4.2. Border radius scale

| Token | Giá trị | Dùng cho |
|---|---|---|
| `RADIUS_SM` | `6px` | Badge trạng thái, tag nhỏ |
| `RADIUS_MD` | `10px` | Input, ComboBox, nút nhỏ, nút pill |
| `RADIUS_LG` | `14px` | Card, panel, TableView |
| `RADIUS_XL` | `20px` | Dialog, Modal, sidebar logo block |
| `RADIUS_FULL` | `999px` | Pill button, chip lọc category, avatar |

### 4.3. Layout tổng thể ứng dụng

```
┌─────────────────────────────────────────────────────────────┐
│ SIDEBAR (256px cố định)    │ HEADER (cao 64px, full width)  │
│ bg: COLOR_BG_SURFACE       ├────────────────────────────────│
│ border-right: COLOR_BORDER │                                │
│ ┌──────────────────────┐   │        CONTENT AREA            │
│ │ LOGO BLOCK (h=72px)  │   │  bg: COLOR_BG_APP              │
│ └──────────────────────┘   │  padding: SPACE_XL (24px)      │
│                             │  overflow-y: scroll            │
│  NAV MENU (flex-1, scroll) │                                │
│  spacing giữa items: 6px   │                                │
│ ┌──────────────────────┐   │                                │
│ │ USER INFO (h=72px)   │   │                                │
│ └──────────────────────┘   │                                │
└─────────────────────────────────────────────────────────────┘
```

**Sidebar — chi tiết:**
- Rộng: `256px` cố định, không co giãn — set bằng `setPreferredSize(new Dimension(256, height))`
- Nền: `COLOR_BG_SURFACE` (#FFFFFF)
- Border phải: `1px solid COLOR_BORDER` — dùng `MatteBorder(0, 0, 0, 1, COLOR_BORDER)`
- Shadow: `DropShadowBorder` hoặc mô phỏng bằng `MatteBorder` nhạt

**Header — chi tiết:**
- Cao: `64px` cố định
- Nền: `COLOR_BG_SURFACE`
- Border dưới: `1px solid COLOR_BORDER` — dùng `MatteBorder(0, 0, 1, 0, COLOR_BORDER)`
- Padding ngang: `32px` — dùng `EmptyBorder(0, 32, 0, 32)`
- Bên trái: Tiêu đề màn hình `TEXT_TITLE_SCREEN`
- Bên phải: Ô tìm kiếm + nút nhanh + avatar nhân viên

**Logo Block — chi tiết:**
- Padding: `24px` — dùng `EmptyBorder(24, 24, 24, 24)`
- Icon store: `40x40px`, nền `COLOR_PRIMARY`, bo góc `RADIUS_MD` — dùng custom `paintComponent`
- Tên tiệm: `18px Bold`, màu `COLOR_PRIMARY_TEXT`
- Tagline: `10px Bold Uppercase`, màu `COLOR_PRIMARY_MUTED`, letter-spacing rộng

---

## 5. COMPONENT LIBRARY

### 5.1. Button

#### Nút chính (Primary) — dùng cho hành động chính của màn hình
```java
// Áp dụng trong UIHelper hoặc trực tiếp khi khởi tạo nút
JButton btn = new JButton("Lưu đơn");
btn.setBackground(new Color(0x92400E));
btn.setForeground(Color.WHITE);
btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
btn.setBorder(new EmptyBorder(10, 20, 10, 20));
btn.setFocusPainted(false);
btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
// Hover: dùng MouseAdapter → setBackground(new Color(0x78350F))
// Disabled: btn.setEnabled(false) → FlatLaf tự xử lý màu xám
```

#### Nút phụ (Secondary / Ghost) — dùng cho hành động thứ cấp
```java
JButton btn = new JButton("Hủy");
btn.setBackground(Color.WHITE);
btn.setForeground(new Color(0x92400E));
btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
btn.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0x92400E), 2),
    new EmptyBorder(8, 18, 8, 18)
));
btn.setFocusPainted(false);
btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
// Hover: setBackground(new Color(0xFEF3C7))
```

#### Nút nguy hiểm (Danger) — dùng cho hủy đơn, xóa
```java
JButton btn = new JButton("Hủy đơn");
btn.setBackground(new Color(0xDC2626));
btn.setForeground(Color.WHITE);
btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
btn.setBorder(new EmptyBorder(10, 20, 10, 20));
btn.setFocusPainted(false);
// Hover: setBackground(new Color(0xB91C1C))
```

#### Nút Pill (Category Filter) — dùng cho bộ lọc danh mục sản phẩm
```java
// Dùng JToggleButton với custom paintComponent để bo góc tròn
// Active: nền #92400E, text trắng
// Inactive: nền trắng, border #FDE68A, text #92400E
```

---

### 5.2. TextField & TextArea

```java
// TextField chuẩn
JTextField tf = new JTextField();
tf.setBackground(new Color(0xF9FAFB));
tf.setForeground(new Color(0x1C0A00));
tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
tf.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0xFDE68A), 2),
    new EmptyBorder(8, 10, 8, 10)
));
// Focus: dùng FocusListener → setBorder với màu #FCD34D, width 2px
// Error: setBorder với màu #DC2626, width 2px
```

**Cấu trúc bắt buộc của 1 form field:**
```
[JLabel — 11px Bold, màu #6B7280, margin-bottom 4px]
[JTextField / JComboBox]
[JLabel error — 11px Bold, màu #DC2626, icon ⚠, chỉ setVisible(true) khi có lỗi]
```

---

### 5.3. JComboBox

```java
JComboBox<String> combo = new JComboBox<>();
combo.setBackground(Color.WHITE);
combo.setForeground(new Color(0x1C0A00));
combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
combo.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0xFDE68A), 2),
    new EmptyBorder(6, 10, 6, 10)
));
// FlatLaf tự xử lý hover/selected item với màu #FEF3C7 / #92400E
// Có thể override bằng UIManager.put("ComboBox.selectionBackground", ...)
```

---

### 5.4. JTable

```java
JTable table = new JTable(model);
table.setBackground(Color.WHITE);
table.setForeground(new Color(0x1C0A00));
table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
table.setRowHeight(44);
table.setShowGrid(false);
table.setIntercellSpacing(new Dimension(0, 0));
table.setSelectionBackground(new Color(0xFEF3C7));
table.setSelectionForeground(new Color(0x1C0A00));

// Header
JTableHeader header = table.getTableHeader();
header.setBackground(new Color(0xFEF3C7));
header.setForeground(new Color(0x78350F));
header.setFont(new Font("Segoe UI", Font.BOLD, 12));
header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xFDE68A)));

// Row hover: dùng custom DefaultTableCellRenderer
// Bọc trong JScrollPane với border: LineBorder(COLOR_BORDER, 1)
```

---

### 5.5. Badge trạng thái đơn hàng (Status Badge)

Dùng `Label` với style class tương ứng. Bo góc `RADIUS_SM` (6px), padding `4px 10px`, font `11px Bold`.

| Trạng thái | Class CSS | Nền | Màu text |
|---|---|---|---|
| Mới đặt | `.badge-new` | `#FEF3C7` | `#D97706` |
| Đã cọc | `.badge-deposited` | `#FEF3C7` | `#D97706` |
| Đang sản xuất | `.badge-processing` | `#DBEAFE` | `#1D4ED8` |
| Chờ giao | `.badge-shipping` | `#EDE9FE` | `#6D28D9` |
| Chờ khách lấy | `.badge-pickup` | `#EDE9FE` | `#6D28D9` |
| Hoàn thành | `.badge-done` | `#DCFCE7` | `#15803D` |
| Hủy | `.badge-cancelled` | `#F3F4F6` | `#6B7280` |

**Badge tồn kho:**

| Trạng thái | Nền | Màu text |
|---|---|---|
| Đầy đủ | `#DCFCE7` | `#15803D` |
| Cảnh báo (dưới mức an toàn) | `#FEF3C7` | `#D97706` |
| Sắp hết (dưới 50% mức an toàn) | `#FEE2E2` | `#DC2626` |

---

### 5.6. Card / Panel

```java
// Card chuẩn — JPanel với custom border và background
JPanel card = new JPanel();
card.setBackground(Color.WHITE);
card.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0xFDE68A), 1),
    new EmptyBorder(20, 20, 20, 20)
));
// Shadow: dùng thư viện ShadowBorder hoặc vẽ thủ công trong paintComponent

// Card Header — JPanel phần đầu card có nền riêng
JPanel cardHeader = new JPanel();
cardHeader.setBackground(new Color(0xFFFBEB));
cardHeader.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xFDE68A)),
    new EmptyBorder(16, 20, 16, 20)
));
```

**KPI Card (Dashboard):**
- Nền: trắng, shadow nhẹ, bo góc `16px` — override `paintComponent`
- Layout: `BorderLayout` hoặc `FlowLayout` — Icon circle bên trái, Label + Giá trị bên phải
- Icon circle: `JLabel` với custom `paintComponent` vẽ hình tròn, nền màu semantic
- Label: `12px Regular`, màu `COLOR_TEXT_SECONDARY`
- Giá trị: `TEXT_KPI` (26px Bold), màu `COLOR_TEXT_PRIMARY`

---

### 5.7. Dialog / Modal

```java
// JDialog chuẩn
JDialog dialog = new JDialog(parentFrame, "Tiêu đề", true); // modal=true
dialog.setBackground(Color.WHITE);
dialog.getRootPane().setBorder(new EmptyBorder(32, 32, 32, 32));

// Overlay phủ nền: dùng GlassPane của JFrame với nền rgba(0,0,0,0.5)
// Set trước khi setVisible(true) trên dialog
```

**Quy tắc bắt buộc cho Dialog:**
- Kích thước tối thiểu: `400px` rộng — `dialog.setMinimumSize(new Dimension(400, 0))`
- Kích thước tối đa: `900px` rộng, chiều cao tự co giãn theo nội dung
- **Tiêu đề:** `TEXT_TITLE_DIALOG` (22px Bold), màu `COLOR_PRIMARY_TEXT`, có icon bên trái, gap `8px`
- **Nút đóng X:** Góc trên phải, hình tròn `32x32px`, nền `#F3F4F6`, hover `#E5E7EB`
- **Footer nút:** Luôn ở cuối, căn phải (`FlowLayout.RIGHT`), gap `12px` giữa nút Hủy và nút Xác nhận
- **Nút Hủy:** Luôn dùng style `btn-secondary`
- **Nút Xác nhận:** Luôn dùng style `btn-primary` hoặc `btn-danger` tùy hành động

---

### 5.8. Sidebar Navigation Item

```java
// Dùng JButton custom hoặc JPanel với MouseListener
JButton navItem = new JButton("Bán hàng (POS)");
navItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
navItem.setForeground(new Color(0xB45309));
navItem.setBackground(new Color(0, 0, 0, 0)); // transparent
navItem.setBorder(new EmptyBorder(10, 16, 10, 16));
navItem.setFocusPainted(false);
navItem.setContentAreaFilled(false);
navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
navItem.setHorizontalAlignment(SwingConstants.LEFT);
// Hover: setBackground(new Color(0xFEF3C7)), setForeground(new Color(0x78350F))
// Active: setBackground(new Color(0x92400E)), setForeground(Color.WHITE), contentAreaFilled=true
```

- Icon: `18px`, dùng `ImageIcon` hoặc Unicode
- Gap icon — text: `12px` — dùng `setIconTextGap(12)`
- Spacing giữa các nav-item trong panel: `6px` — dùng `BoxLayout` với `Box.createRigidArea(new Dimension(0, 6))`

---

### 5.9. Loading Overlay (Bắt buộc khi gọi DB)

Hiển thị khi `SwingWorker.execute()` được gọi. Ẩn trong `done()` của SwingWorker.

**Cách triển khai với GlassPane:**
```java
// Trong MainFrame hoặc Dialog — khai báo GlassPane
JPanel glassPane = new JPanel() {
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 90)); // rgba(0,0,0,0.35)
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
};
glassPane.setOpaque(false);
glassPane.setLayout(new GridBagLayout());

// Panel loading ở giữa
JPanel loadingBox = new JPanel(new BorderLayout(0, 12));
loadingBox.setBackground(Color.WHITE);
loadingBox.setBorder(new EmptyBorder(24, 32, 24, 32));
loadingBox.add(new JProgressBar(), BorderLayout.CENTER); // indeterminate=true
loadingBox.add(new JLabel("Đang xử lý...", SwingConstants.CENTER), BorderLayout.SOUTH);

glassPane.add(loadingBox);
frame.setGlassPane(glassPane);

// Hiện/ẩn:
glassPane.setVisible(true);   // trước worker.execute()
glassPane.setVisible(false);  // trong done() của SwingWorker
```

---

### 5.10. Toast Message (Thông báo nhanh tự động ẩn)

Dùng cho cảnh báo thiếu nguyên liệu, thông báo không quan trọng (không dùng JOptionPane popup).

**Vị trí:** Góc dưới phải màn hình, margin `16px` so với cạnh
**Kích thước:** min-width `280px`, padding `12px 16px`, bo góc `10px`
**Thời gian hiển thị:** `3 giây` — dùng `javax.swing.Timer`
**Animation:** Trượt lên khi hiện, mờ dần khi ẩn — dùng `javax.swing.Timer` với `setOpacity()` trên `JWindow`

```java
// Tạo JWindow không có frame (transparent)
JWindow toast = new JWindow(parentFrame);
toast.setBackground(new Color(0, 0, 0, 0));
// Set nội dung, màu theo loại (xem bảng dưới)
toast.setVisible(true);

// Tự ẩn sau 3 giây
Timer timer = new Timer(3000, e -> toast.dispose());
timer.setRepeats(false);
timer.start();
```

| Loại | Nền | Text | Icon |
|---|---|---|---|
| Lỗi | `#DC2626` | Trắng | ⚠ |
| Cảnh báo | `#D97706` | Trắng | ⚠ |
| Thành công | `#16A34A` | Trắng | ✓ |

---

## 6. MÀN HÌNH KDS (KITCHEN DISPLAY — THỢ BẾP)

KDS thiết kế khác biệt so với phần còn lại — ưu tiên dễ nhìn từ xa, thao tác nhanh bằng tay.

- **Nền app KDS:** `#F3F4F6` (xám nhạt, không dùng kem như màn hình khác)
- **Font size tối thiểu toàn màn hình:** `15px` cho tất cả text
- **Layout:** Grid lưới các card đơn hàng, tối thiểu 2 cột, tối đa 3 cột
- **Card đơn hàng trong KDS:**
  - Bo góc: `16px`
  - **Border-top: `8px solid`** — màu `#EF4444` nếu urgent, màu `#F59E0B` nếu bình thường
  - Nền: trắng, shadow rõ hơn bình thường
  - Padding: `16px`
- **Nút hành động (Bắt đầu làm / Hoàn thành):** Chiều cao tối thiểu `44px`, font `14px Bold`, full width trong card
- **Nút "Bắt đầu làm":** Màu `COLOR_INFO` (`#2563EB`)
- **Nút "Hoàn thành & In Tem":** Màu `COLOR_SUCCESS` (`#16A34A`)
- **Badge "ĐƠN CẦN GẤP":** Nền `#FEE2E2`, text `#DC2626`, font `12px Bold`, có icon `fas-exclamation-triangle`
- **Giờ đơn hàng:** `14px Regular`, màu `#6B7280`, icon `fas-clock` bên trái

---

## 7. PREVIEW IN ẤN — JASPERREPORTS

### Khổ hóa đơn nhiệt 80mm

- **Chiều rộng cố định:** `302px` (tương đương 80mm ở 96dpi)
- **Chiều cao:** Tự động giãn theo số lượng món
- **Font bắt buộc:** `Courier New` hoặc font monospace — đảm bảo căn cột chuẩn khi in
- **Font size nội dung:** `11px` cho dòng thường, `13px Bold` cho tiêu đề và dòng tổng tiền
- **Đường kẻ phân cách:** Dashed (`- - - - -`), màu `#9CA3AF`
- **Nền cửa sổ preview:** `#E5E7EB` (xám nhạt mô phỏng máy in)
- **Tiêu đề cửa sổ:** `"Preview Hóa Đơn (80mm)"` — hiển thị qua `JasperViewer.viewReport(print, false)`
- **Nút "In Hóa Đơn":** Màu `#2563EB` (xanh dương), padding `8px 24px`

---

## 8. QUY TẮC STYLE JAVA SWING BẮT BUỘC

```java
// ✅ ĐÚNG — Set màu nền và text bằng Java
component.setBackground(new Color(0x92400E));
component.setForeground(Color.WHITE);
component.setFont(new Font("Segoe UI", Font.BOLD, 13));

// ❌ SAI — Không dùng HTML inline style trừ JLabel
// component.putClientProperty("style", "background: #92400E"); // TRÁNH

// ✅ Bo góc: override paintComponent trong subclass
@Override
protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // radius=10px mỗi chiều
    g2.dispose();
    super.paintComponent(g);
}
setOpaque(false); // BẮT BUỘC khi override paintComponent

// ✅ Border compound (border + padding)
component.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(0xFDE68A), 1),
    new EmptyBorder(12, 16, 12, 16) // top, left, bottom, right
));

// ✅ Hover effect — dùng MouseAdapter
button.addMouseListener(new MouseAdapter() {
    @Override public void mouseEntered(MouseEvent e) { button.setBackground(new Color(0x78350F)); }
    @Override public void mouseExited(MouseEvent e)  { button.setBackground(new Color(0x92400E)); }
});

// ✅ Màu với alpha (transparency)
new Color(0, 0, 0, 128); // rgba(0,0,0,0.5) — alpha từ 0-255
```

---

## 9. CHECKLIST UI TRƯỚC KHI GIAO NỘP CODE

Agent tự kiểm tra toàn bộ các điểm sau trước khi submit file View/Presenter:

**Màu sắc & Typography:**
- [ ] Tất cả màu sử dụng đúng token từ Mục 2, không hardcode màu tùy ý
- [ ] Font size nằm trong bảng Mục 3.1, không dùng kích thước ngoài danh sách
- [ ] Tiền tệ được format đúng `#,###đ`

**Component:**
- [ ] Nút chính dùng style `btn-primary`, nút phụ/hủy dùng `btn-secondary`, nút nguy hiểm dùng `btn-danger`
- [ ] Badge trạng thái dùng đúng màu theo bảng Mục 5.5
- [ ] Mọi JTextField/JComboBox có JLabel phía trên và error label phía dưới
- [ ] JDialog có nút X ở góc trên phải, footer nút căn phải

**Layout & UX:**
- [ ] Loading Overlay / GlassPane (Mục 5.9) được khai báo và bind với SwingWorker
- [ ] Không có SQL hay logic nghiệp vụ trong Presenter hoặc View
- [ ] Giao diện hoạt động đúng ở độ phân giải tối thiểu `1280 x 720px`
- [ ] Màn hình KDS dùng đúng thiết kế riêng (Mục 6), không dùng style chung
