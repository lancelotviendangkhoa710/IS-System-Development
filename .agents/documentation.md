# H3K Bakery Management System — Tài liệu Kỹ thuật

> Được tạo tự động vào ngày 24/04/2026. Mô tả layout, props và logic của từng component trong dự án.

---

## Mục lục

1. [Tổng quan dự án](#1-tổng-quan-dự-án)
2. [Cấu trúc thư mục](#2-cấu-trúc-thư-mục)
3. [Cấu hình hệ thống](#3-cấu-hình-hệ-thống)
4. [App.jsx — Gốc ứng dụng](#4-appjsx--gốc-ứng-dụng)
5. [Common Components](#5-common-components)
   - [KPICard](#51-kpicard)
   - [Pagination](#52-pagination)
   - [StatusBadge](#53-statusbadge)
6. [View Components](#6-view-components)
   - [AuthView](#61-authview)
   - [DashboardView](#62-dashboardview)
   - [POSView](#63-posview)
   - [OrderKDSView](#64-orderkdsview)
   - [InventoryView](#65-inventoryview)
   - [CustomerView](#66-customerview)
   - [ProductRecipeView](#67-productrecipeview)
   - [HRView](#68-hrview)
   - [OpenShiftView](#69-openshiftview)
   - [AccountView](#610-accountview)
   - [CashbookView](#611-cashbookview)
   - [SupplierView](#612-supplierview)
7. [Modal Components](#7-modal-components)
8. [Dữ liệu mẫu (Mock Data)](#8-dữ-liệu-mẫu-mock-data)
9. [Luồng dữ liệu & State Management](#9-luồng-dữ-liệu--state-management)
10. [Quy ước giao diện](#10-quy-ước-giao-diện)

---

## 1. Tổng quan dự án

| Thuộc tính | Giá trị |
|---|---|
| Tên dự án | H3K Bakery Management System |
| Loại ứng dụng | Dashboard quản lý tiệm bánh/café |
| Framework | React 19.2.5 + Vite 8.0.9 |
| Styling | Tailwind CSS 4.2.2 |
| Icons | Lucide React 1.8.0 |
| Ngôn ngữ | JavaScript (ES Modules) |

Hệ thống gồm 3 nhóm người dùng chính:

| Vai trò | Tiếng Anh | Quyền truy cập |
|---|---|---|
| Quản lý | Manager | Toàn bộ hệ thống |
| Thu ngân | Cashier | POS, Dashboard, Đơn hàng |
| Thợ bếp | Baker | KDS, Kho hàng |

---

## 2. Cấu trúc thư mục

```
h3k-bakery/
├── src/
│   ├── App.jsx                         # Root component, quản lý state toàn cục
│   ├── main.jsx                        # Entry point
│   ├── index.css                       # Global styles
│   ├── constants/
│   │   └── index.js                    # Cấu hình vai trò & module
│   ├── data/
│   │   └── mockData.js                 # Dữ liệu mẫu (thay thế database)
│   └── components/
│       ├── common/
│       │   ├── KPICard.jsx             # Card hiển thị chỉ số
│       │   ├── Pagination.jsx          # Phân trang
│       │   └── StatusBadge.jsx         # Badge trạng thái màu sắc
│       ├── views/                      # 12 màn hình chính
│       │   ├── AuthView.jsx
│       │   ├── DashboardView.jsx
│       │   ├── POSView.jsx
│       │   ├── OrderKDSView.jsx
│       │   ├── InventoryView.jsx
│       │   ├── CustomerView.jsx
│       │   ├── ProductRecipeView.jsx
│       │   ├── HRView.jsx
│       │   ├── OpenShiftView.jsx
│       │   ├── CashbookView.jsx
│       │   ├── AccountView.jsx
│       │   └── SupplierView.jsx
│       └── modals/                     # 15+ hộp thoại
│           ├── CustomCakeOrderModal.jsx
│           ├── RecipeModal.jsx
│           └── ...
├── public/
├── package.json
├── tailwind.config.js
└── vite.config.js
```

---

## 3. Cấu hình hệ thống

**File:** `src/constants/index.js`

Định nghĩa các hằng số dùng chung:

```
ROLES:
  - "Quản lý"   → Quản trị toàn bộ
  - "Thu ngân"  → Bán hàng & ca làm
  - "Thợ bếp"  → Bếp & kho

MODULES (11 module):
  dashboard, pos, orders, inventory, products,
  customers, cashbook, hr, account, reports, suppliers
```

Mỗi module được cấu hình với:
- `id` — định danh
- `label` — tên hiển thị (tiếng Việt)
- `icon` — icon Lucide
- `roles` — mảng vai trò được phép truy cập

---

## 4. App.jsx — Gốc ứng dụng

**File:** `src/App.jsx`

### Layout tổng thể

```
┌─────────────────────────────────────────────┐
│  Sidebar (trái, cố định)                     │
│  - Logo H3K Bakery                           │
│  - Nav links theo role                       │
├─────────────────────────────────────────────┤
│  Header (trên)                               │
│  - Tên module hiện tại                       │
│  - Avatar + tên user + nút đăng xuất         │
├─────────────────────────────────────────────┤
│  Main Content Area                           │
│  (render View tương ứng với activeModule)    │
└─────────────────────────────────────────────┘
```

### State toàn cục (tất cả dùng useState)

| State | Kiểu | Mô tả |
|---|---|---|
| `currentUser` | Object / null | Thông tin user đang đăng nhập |
| `activeModule` | string | Module đang được hiển thị |
| `currentShift` | Object / null | Ca làm việc hiện tại (Thu ngân) |
| `products` | Array | Danh sách sản phẩm |
| `categories` | Array | Danh mục sản phẩm |
| `orders` | Array | Danh sách đơn hàng |
| `inventory` | Array | Tồn kho nguyên liệu |
| `ingredients` | Array | Danh mục nguyên liệu |
| `customers` | Array | Danh sách khách hàng |
| `suppliers` | Array | Danh sách nhà cung cấp |
| `transactions` | Array | Lịch sử thu chi |
| `usersDb` | Array | Tài khoản người dùng |
| `loaithuchi` | Array | Loại thu/chi |
| `inventoryHistory` | Array | Lịch sử nhập/xuất kho |
| `showCustomOrderModal` | boolean | Hiện modal đặt bánh custom |

### Logic chính

- **Điều hướng module:** Dựa vào `currentUser.role`, lọc danh sách module được phép hiển thị trên sidebar.
- **Quản lý ca (Thu ngân):** Nếu user là Thu ngân và `currentShift === null`, tự động redirect về `OpenShiftView` để mở ca.
- **Đăng xuất:** Xóa `currentUser` và `currentShift`, trả về `AuthView`.
- **Prop drilling:** Tất cả state và setter được truyền xuống các View qua props.

---

## 5. Common Components

### 5.1 KPICard

**File:** `src/components/common/KPICard.jsx`

**Mô tả:** Card hiển thị một chỉ số kinh doanh (KPI) với icon và giá trị.

**Layout:**
```
┌─────────────────────────────┐
│  [Icon]   Tiêu đề           │
│           Giá trị lớn       │
└─────────────────────────────┘
```

**Props:**

| Prop | Kiểu | Mô tả |
|---|---|---|
| `title` | string | Tên chỉ số |
| `value` | string / number | Giá trị hiển thị |
| `icon` | ReactElement | Icon (từ Lucide) |
| `bg` | string | Class màu nền Tailwind (vd: `bg-amber-100`) |

**Styling:** Card trắng, bo góc, đổ bóng, padding đều. Icon nằm trong vùng tô màu `bg`.

---

### 5.2 Pagination

**File:** `src/components/common/Pagination.jsx`

**Mô tả:** Điều hướng giữa các trang của danh sách dữ liệu.

**Layout:**
```
← Trước  [1] [2] ... [5] [6] [7] ... [10]  Sau →
Hiển thị 1-10 trong 47 mục
```

**Props:**

| Prop | Kiểu | Mô tả |
|---|---|---|
| `currentPage` | number | Trang hiện tại |
| `totalPages` | number | Tổng số trang |
| `totalItems` | number | Tổng số mục |
| `itemsPerPage` | number | Số mục mỗi trang |
| `onPageChange` | function | Callback khi đổi trang `(page) => void` |

**Logic:**
- Luôn hiển thị trang đầu, trang cuối, trang hiện tại và trang liền kề (±1).
- Chèn dấu `...` khi có khoảng cách giữa các nhóm trang.
- Nút "Trước" và "Sau" bị vô hiệu hóa khi đang ở biên.
- Hiển thị dòng thông tin "Hiển thị X-Y trong Z mục".

---

### 5.3 StatusBadge

**File:** `src/components/common/StatusBadge.jsx`

**Mô tả:** Badge hiển thị trạng thái đơn hàng/giao dịch có màu sắc tương ứng.

**Props:**

| Prop | Kiểu | Mô tả |
|---|---|---|
| `status` | string | Tên trạng thái tiếng Việt |

**Bảng màu:**

| Trạng thái | Màu |
|---|---|
| Đã cọc, Mới đặt | Vàng (amber) |
| Đang sản xuất | Xanh dương (blue) |
| Chờ giao, Chờ khách lấy | Tím (purple) |
| Hoàn thành | Xanh lá (green) |
| Đã huỷ | Đỏ nhạt (red) |
| Mặc định | Xám (gray) |

---

## 6. View Components

### 6.1 AuthView

**File:** `src/components/views/AuthView.jsx`

**Mô tả:** Màn hình đăng nhập và đăng ký tài khoản.

**Layout:**
```
┌──────────────────────────────────────────────┐
│  Gradient nền (amber → brown)                │
│  ┌────────────────────────────────────────┐  │
│  │  Logo + Tên "H3K Bakery"              │  │
│  │  [Tab: Đăng nhập | Đăng ký]           │  │
│  │                                        │  │
│  │  Form nhập liệu                        │  │
│  │  Nút submit                            │  │
│  │  Gợi ý tài khoản test                  │  │
│  └────────────────────────────────────────┘  │
└──────────────────────────────────────────────┘
```

**State nội bộ:**

| State | Mô tả |
|---|---|
| `isLoginMode` | Toggle giữa đăng nhập / đăng ký |
| `username` | Giá trị ô tên đăng nhập |
| `password` | Giá trị ô mật khẩu |
| `fullName` | Họ tên (chỉ dùng khi đăng ký) |
| `selectedRole` | Vai trò chọn khi đăng ký |
| `adminCode` | Mã admin xác thực tạo tài khoản |
| `showPassword` | Toggle hiển thị/ẩn mật khẩu |
| `error` | Thông báo lỗi |

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `onLogin` | Callback sau khi đăng nhập thành công |
| `usersDb` | Danh sách tài khoản để xác thực |
| `onRegister` | Callback tạo tài khoản mới |

**Logic:**
- **Đăng nhập:** So sánh username/password với `usersDb`. Nếu khớp thì gọi `onLogin(user)`.
- **Đăng ký:** Kiểm tra `adminCode` hợp lệ, username chưa tồn tại, rồi gọi `onRegister(newUser)`.
- **Toggle password:** Dùng icon `Eye` / `EyeOff` của Lucide.
- Hiển thị hint tài khoản test: `admin/123`, `thungan/123`, `thobep/123`.

---

### 6.2 DashboardView

**File:** `src/components/views/DashboardView.jsx`

**Mô tả:** Bảng điều khiển tổng quan với các chỉ số kinh doanh và chức năng chốt ca.

**Layout:**
```
┌─────────────────────────────────────────────────────┐
│  [KPI] Doanh thu  [KPI] Đơn xong  [KPI] Đơn chờ    │
│  [KPI] Cảnh báo kho                                 │
├─────────────────────────────────────────────────────┤
│  Top 5 sản phẩm bán chạy         │  Thao tác nhanh  │
│  (thanh ngang dạng bar chart)    │  [Nút chốt ca]   │
└─────────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `orders` | Danh sách đơn hàng |
| `inventory` | Dữ liệu tồn kho |
| `currentShift` | Thông tin ca hiện tại |
| `onCloseShift` | Callback đóng ca |
| `currentUser` | Thông tin user |
| `transactions` | Lịch sử giao dịch |

**KPI được tính:**
- **Doanh thu hôm nay:** Tổng `finalTotal` của các đơn "Hoàn thành" trong ngày.
- **Đơn hoàn thành:** Đếm đơn trạng thái "Hoàn thành" hôm nay.
- **Đơn đang chờ:** Đếm đơn chưa hoàn thành/chưa huỷ.
- **Cảnh báo kho:** Số nguyên liệu có tồn kho < mức an toàn.

**Modal chốt ca (Reconciliation):**

Quy trình 3 bước:
1. **Bước 1 — Nhập tiền:** Thu ngân đếm tiền mặt thực tế trong ngăn kéo.
2. **Bước 2 — Xác nhận:** So sánh tiền thực tế vs tiền hệ thống. Hiển thị chênh lệch (thừa/thiếu). Nếu có lệch, yêu cầu nhập lý do.
3. **Bước 3 — Hoàn tất:** Xác nhận đóng ca, ghi nhận audit.

---

### 6.3 POSView

**File:** `src/components/views/POSView.jsx`

**Mô tả:** Màn hình bán hàng tại quầy (Point of Sale).

**Layout:**
```
┌──────────────────────────────┬──────────────────────┐
│  Danh sách sản phẩm (trái)   │  Giỏ hàng (phải)     │
│  [Ô tìm kiếm]                │  [DS sản phẩm đã chọn]│
│  [Tabs danh mục]             │  Khách hàng           │
│  [Grid sản phẩm 2-3 cột]     │  Tổng / VAT / Điểm   │
│                              │  [Thanh toán]         │
└──────────────────────────────┴──────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `products` | Danh sách sản phẩm |
| `categories` | Danh mục sản phẩm |
| `customers` | Danh sách khách hàng |
| `onAddOrder` | Callback tạo đơn hàng mới |
| `onUpdateInventory` | Callback cập nhật tồn kho |
| `onUpdateCustomer` | Callback cộng điểm khách |

**State nội bộ:**

| State | Mô tả |
|---|---|
| `cart` | Mảng `{product, quantity}` |
| `searchTerm` | Từ khoá tìm sản phẩm |
| `activeCategory` | Danh mục đang lọc |
| `customerPhone` | Số điện thoại tìm khách |
| `selectedCustomer` | Khách hàng đã chọn |
| `paymentMethod` | `"cash"` hoặc `"qr"` |
| `showReceipt` | Hiển thị hoá đơn sau thanh toán |

**Logic nghiệp vụ:**
- **Lọc sản phẩm:** Theo từ khoá tên + danh mục đang active.
- **Thêm vào giỏ:** Kiểm tra tồn kho thực tế, không cho thêm nếu hết hàng.
- **Tìm khách hàng:** Nhập SĐT → khớp trong `customers` → hiển thị tên, hạng, % giảm giá.
- **Đăng ký khách mới:** Form nhanh ngay trong POS nếu không tìm thấy khách.
- **Tính tiền:**
  - Subtotal = tổng (giá × số lượng)
  - Giảm giá = subtotal × % (từ hạng khách)
  - VAT = (subtotal - giảm giá) × 8%
  - Tổng cộng = subtotal - giảm giá + VAT
- **Điểm tích lũy:** 1 điểm / 10.000 VNĐ tổng hóa đơn.
- **Thanh toán:** Tạo đơn hàng mới trạng thái "Hoàn thành", cập nhật kho & điểm khách.

---

### 6.4 OrderKDSView

**File:** `src/components/views/OrderKDSView.jsx`

**Mô tả:** Kitchen Display System — quản lý đơn hàng sản xuất cho thợ bếp và quản lý.

**Layout — Giao diện Thợ bếp (Card Grid):**
```
┌──────────────────────────────────────────────┐
│  [Bộ lọc trạng thái]  [Bộ lọc ngày]          │
├──────┬──────┬──────┬──────────────────────────┤
│ Đơn  │ Đơn  │ Đơn  │ (grid 2-3 cột)           │
│ Card │ Card │ Card │                           │
│      │      │      │                           │
│ [▼ Chi tiết bánh custom]                      │
│ [Bắt đầu SX] [Hoàn thành]                    │
└──────────────────────────────────────────────┘
```

**Layout — Giao diện Quản lý/Thu ngân (Table):**
```
┌─────────────────────────────────────────────────────┐
│  [Lọc: Tất cả | Đang SX | Hoàn thành] [Ngày]       │
├──┬──────────┬───────────┬──────────┬────────────────┤
│# │ Khách    │ Sản phẩm  │ Trạng thái│ Thao tác       │
├──┼──────────┼───────────┼──────────┼────────────────┤
│  │          │           │          │ [Huỷ đơn]      │
└──┴──────────┴───────────┴──────────┴────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `orders` | Danh sách tất cả đơn hàng |
| `onUpdateOrder` | Callback cập nhật trạng thái đơn |
| `onCancelOrder` | Callback huỷ đơn + hoàn tiền |
| `currentUser` | Để phân biệt giao diện Baker vs Manager |
| `customers` | Thông tin khách hàng |
| `transactions` | Để ghi giao dịch hoàn tiền |

**Logic phân quyền giao diện:**
- `currentUser.role === "Thợ bếp"` → hiển thị Card Grid.
- `currentUser.role !== "Thợ bếp"` → hiển thị Table.

**Logic thợ bếp:**
- Mỗi card hiển thị: Mã đơn, tên khách, loại đơn (nội bộ/khách), mức độ ưu tiên (viền đỏ nếu urgent).
- Click vào card → mở rộng hiển thị chi tiết bánh custom (kích thước, đế, nhân, trang trí, lời nhắn).
- Nút "Bắt đầu sản xuất" → đổi trạng thái sang "Đang sản xuất".
- Nút "Hoàn thành" → đổi trạng thái sang "Hoàn thành".

**Logic quản lý — Huỷ đơn:**
1. Hiển thị modal nhập lý do huỷ.
2. Nếu đơn đã thanh toán → tự động tạo giao dịch "hoàn tiền" vào sổ chi.
3. Cập nhật trạng thái đơn sang "Đã huỷ".

---

### 6.5 InventoryView

**File:** `src/components/views/InventoryView.jsx`

**Mô tả:** Quản lý kho nguyên liệu — tồn kho, lịch sử, danh mục.

**Layout:**
```
┌─────────────────────────────────────────────────┐
│  [Tab: Tồn kho | Lịch sử | Danh mục NL]        │
│  [Nút: Nhập kho] [Xuất kho]                     │
├─────────────────────────────────────────────────┤
│  Nội dung tab tương ứng                         │
└─────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `inventory` | Tồn kho nguyên liệu |
| `ingredients` | Danh mục nguyên liệu |
| `inventoryHistory` | Lịch sử nhập/xuất |
| `suppliers` | Danh sách nhà cung cấp |
| `products` | Sản phẩm (để tính xuất theo công thức) |
| `onImport` | Callback nhập kho |
| `onExport` | Callback xuất kho |
| `onUpdateIngredients` | Callback CRUD danh mục NL |

**Tab A — Tồn Kho Hiện Tại:**

Bảng cột: Tên NL | Đơn vị | Tồn kho | Mức an toàn | Giá nhập | Trạng thái

Màu trạng thái:
- 🟢 Xanh: `tồn kho >= mức an toàn`
- 🟡 Vàng: `tồn kho >= mức an toàn × 0.5`
- 🔴 Đỏ: `tồn kho < mức an toàn × 0.5`

**Tab B — Lịch Sử Giao Dịch:**

Bộ lọc: Tìm kiếm tên NL + Loại (Nhập/Xuất)

Bảng cột: Ngày | Loại | Tên NL | Số lượng | Đơn giá | Thành tiền | Nhà cung cấp | Số lô | Ghi chú

**Tab C — Danh Mục Nguyên Liệu:**

CRUD đầy đủ: Thêm / Sửa / Xoá nguyên liệu với tên, đơn vị, mức tồn kho tối thiểu.

**Modal Nhập Kho:**
- Chọn nhà cung cấp.
- Nhập số lô hàng.
- Thêm nhiều dòng nguyên liệu: Tên NL | Số lượng | Đơn giá.
- Tự động cộng vào tồn kho sau khi xác nhận.

**Modal Xuất Kho:**

4 loại xuất:
1. **Sản xuất theo đơn** — Chọn sản phẩm + số lượng, hệ thống tính nguyên liệu cần từ recipe.
2. **Nướng trưng bày** — Xuất NL làm hàng trưng bày.
3. **Hao hụt NL** — Ghi nhận mất mát nguyên liệu.
4. **Hao hụt thành phẩm** — Ghi nhận mất thành phẩm.

Trước khi xác nhận: kiểm tra đủ tồn kho, cảnh báo nếu thiếu.

---

### 6.6 CustomerView

**File:** `src/components/views/CustomerView.jsx`

**Mô tả:** Quản lý hệ thống khách hàng thân thiết.

**Layout:**
```
┌────────────────────────────────────────────────────┐
│  [Tìm kiếm tên/SĐT]              [Thêm khách hàng]│
├────────────────────────────────────────────────────┤
│  Bảng danh sách khách hàng                         │
│  ID | Tên | SĐT | Hạng | Điểm | Giảm giá | Trạng thái│
│  [Khoá/Mở] [Sửa]                                   │
├────────────────────────────────────────────────────┤
│  [Phân trang]                                       │
└────────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `customers` | Danh sách khách hàng |
| `onUpdateCustomer` | Callback sửa thông tin |
| `onAddCustomer` | Callback thêm mới |

**Bảng hạng khách hàng (tính theo điểm tích lũy):**

| Hạng | Điểm tối thiểu | Giảm giá |
|---|---|---|
| Đồng (Bronze) | 0 | 0% |
| Bạc (Silver) | 500 | 5% |
| Vàng (Gold) | 1.000 | 10% |
| Kim cương (Diamond) | 2.000 | 15% |

**Logic:**
- **Tìm kiếm:** Lọc theo tên hoặc số điện thoại (case-insensitive).
- **Khoá tài khoản:** Toggle `status` giữa `"active"` / `"locked"`.
- **Sửa thông tin:** Mở `CustomerEditModal` với dữ liệu hiện tại.
- **Tính hạng tự động:** Khi cập nhật điểm, hạng được tính lại dựa theo bảng trên.

---

### 6.7 ProductRecipeView

**File:** `src/components/views/ProductRecipeView.jsx`

**Mô tả:** Quản lý danh mục sản phẩm và công thức nấu ăn.

**Layout:**
```
┌──────────────────────────────────────────────────┐
│  [Tab: Danh mục | Sản phẩm]                      │
├──────────────────────────────────────────────────┤
│  Tab Danh mục:                                   │
│  Bảng: Tên | Mô tả | Trạng thái | [Sửa][Bật/Tắt]│
│  [Thêm danh mục]                                 │
├──────────────────────────────────────────────────┤
│  Tab Sản phẩm:                                   │
│  Bảng: Icon | Tên | DM | Giá | Trạng thái | ...  │
│  [Xem CT] [Sửa] [Thêm CT] [Bật/Tắt]             │
└──────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `products` | Danh sách sản phẩm |
| `categories` | Danh mục sản phẩm |
| `ingredients` | Nguyên liệu (dùng trong recipe) |
| `inventory` | Tồn kho (tính số có thể làm) |
| `onUpdateProducts` | Callback cập nhật sản phẩm |
| `onUpdateCategories` | Callback cập nhật danh mục |

**Logic đặc biệt — Tính số sản phẩm có thể làm:**

```
maxProducible = min(
  floor(tồn_kho[NL] / qty_NL_trong_recipe)
  cho tất cả NL trong recipe
)
```

Hiển thị ngay trong bảng sản phẩm để biết cần nhập thêm NL không.

**Modal Công thức (RecipeModal):**
- Hiển thị danh sách NL + số lượng cần để làm 1 sản phẩm.
- Cho phép thêm/xoá/sửa các dòng nguyên liệu.
- Đơn vị tự động lấy từ danh mục NL.

---

### 6.8 HRView

**File:** `src/components/views/HRView.jsx`

**Mô tả:** Quản lý nhân sự và phân quyền tài khoản.

**Layout:**
```
┌─────────────────────────────────────────────────┐
│  [Tìm kiếm tên/username]  [Lọc trạng thái]      │
│  [Thêm nhân viên]                               │
├─────────────────────────────────────────────────┤
│  Bảng: Họ tên | Username | Vai trò | SĐT | ...  │
│  Trạng thái | [Khoá/Mở] [Sửa]                   │
└─────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `usersDb` | Danh sách tài khoản nhân viên |
| `onUpdateUser` | Callback sửa thông tin |
| `onAddUser` | Callback thêm nhân viên |
| `currentUser` | Không cho tự khoá chính mình |

**Logic:**
- **Tìm kiếm:** Theo tên đầy đủ (`fullName`) hoặc tên đăng nhập (`username`).
- **Lọc trạng thái:** Hiển thị tất cả / chỉ hoạt động / chỉ bị khoá.
- **Khoá tài khoản:** Toggle `status` giữa `"active"` / `"locked"`. Không cho khoá tài khoản đang đăng nhập.
- **Thêm/Sửa nhân viên:** Modal `HREmployeeModal` với các trường: họ tên, username, mật khẩu, vai trò, SĐT, ngày sinh.

---

### 6.9 OpenShiftView

**File:** `src/components/views/OpenShiftView.jsx`

**Mô tả:** Màn hình mở ca làm việc cho Thu ngân. Hiển thị tự động khi Thu ngân chưa có ca.

**Layout:**
```
┌───────────────────────────────────────────┐
│  Tiêu đề "Mở Ca Làm Việc"                 │
│  Chọn quầy POS: [POS-01] [POS-02] [POS-03]│
│  Tiền đầu ca: [___________] VNĐ           │
│  [Bắt đầu ca]                              │
└───────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `onOpenShift` | Callback với `{posId, initialCash, startTime}` |
| `currentUser` | Thông tin Thu ngân |

**Logic:**
- Validate: Phải chọn quầy và nhập số tiền > 0.
- Gọi `onOpenShift({ posId, initialCash: parseFloat(amount), startTime: new Date() })`.
- Sau khi mở ca thành công, App.jsx set `currentShift` và chuyển sang `DashboardView`.

---

### 6.10 AccountView

**File:** `src/components/views/AccountView.jsx`

**Mô tả:** Trang hồ sơ cá nhân và đổi mật khẩu.

**Layout:**
```
┌────────────────────────────────────────────┐
│  Avatar (chữ cái đầu)                       │
│  Họ tên + Vai trò                           │
│  ─────────────────────────────────────────  │
│  Thông tin: Username | SĐT | Ngày sinh     │
│  Trạng thái tài khoản                       │
│  ─────────────────────────────────────────  │
│  Form đổi mật khẩu:                        │
│    Mật khẩu hiện tại                        │
│    Mật khẩu mới                             │
│    Xác nhận mật khẩu mới                    │
│    [Cập nhật mật khẩu]                      │
└────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `currentUser` | Thông tin user hiện tại |
| `onUpdatePassword` | Callback cập nhật mật khẩu |
| `onLogout` | Callback để tự đăng xuất sau đổi pass |

**Logic đổi mật khẩu:**
1. Xác nhận mật khẩu hiện tại khớp với `currentUser.password`.
2. Kiểm tra "Mật khẩu mới" và "Xác nhận" phải giống nhau.
3. Kiểm tra mật khẩu mới tối thiểu 3 ký tự.
4. Gọi `onUpdatePassword(newPassword)`.
5. Hiển thị thông báo thành công, sau 2 giây gọi `onLogout()`.

---

### 6.11 CashbookView

**File:** `src/components/views/CashbookView.jsx`

**Mô tả:** Sổ thu chi — quản lý tài chính nội bộ của cửa hàng.

**Layout:**
```
┌──────────────────────────────────────────────────────┐
│  [Tab: Tổng quan | Lịch sử | Cấu hình DM]            │
│  (Tab Cấu hình chỉ hiện với Quản lý)                 │
├──────────────────────────────────────────────────────┤
│  Tab Tổng quan:                                       │
│  [KPI: Tổng thu] [KPI: Tổng chi] [KPI: Số dư]        │
│  Progress bar thu/chi                                 │
│  Biểu đồ thanh (side-by-side)                        │
├──────────────────────────────────────────────────────┤
│  Tab Lịch sử:                                        │
│  [Tìm kiếm] [Lọc loại: Thu/Chi/Tất cả]               │
│  Bảng: Ngày | Loại | DM | Mô tả | Số tiền | [Huỷ]   │
│  [Phân trang]                                        │
├──────────────────────────────────────────────────────┤
│  Tab Cấu hình:                                       │
│  Bảng danh mục thu/chi | [Thêm] [Sửa] [Bật/Tắt]    │
└──────────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `transactions` | Danh sách giao dịch thu/chi |
| `loaithuchi` | Danh mục loại thu/chi |
| `onAddTransaction` | Callback thêm giao dịch |
| `onCancelTransaction` | Callback huỷ giao dịch |
| `onUpdateLoaithuchi` | Callback cập nhật danh mục |
| `currentUser` | Phân quyền hiển thị tab |

**Logic tài chính:**
- **Tổng thu:** Sum `amount` của tất cả giao dịch loại "Thu" và trạng thái != "cancelled".
- **Tổng chi:** Sum `amount` của tất cả giao dịch loại "Chi" và trạng thái != "cancelled".
- **Số dư:** Tổng thu - Tổng chi.
- **Huỷ giao dịch:** Đặt `status = "cancelled"`, không xoá khỏi lịch sử (soft delete).
- **Danh mục:** Hỗ trợ soft delete qua `deletedAt` timestamp.

**Định dạng tiền:** Hàm nội bộ `formatCurrency(amount)` → `"1.500.000 ₫"`.

---

### 6.12 SupplierView

**File:** `src/components/views/SupplierView.jsx`

**Mô tả:** Quản lý danh sách nhà cung cấp.

**Layout:**
```
┌──────────────────────────────────────────────────┐
│  [Tìm kiếm tên/SĐT]             [Thêm NCC]      │
├──────────────────────────────────────────────────┤
│  Bảng:                                           │
│  Tên NCC | Địa chỉ | Người liên hệ | SĐT | Email│
│  Sản phẩm cung cấp | Trạng thái | [Sửa][Bật/Tắt]│
└──────────────────────────────────────────────────┘
```

**Props nhận từ App.jsx:**

| Prop | Mô tả |
|---|---|
| `suppliers` | Danh sách nhà cung cấp |
| `onUpdateSupplier` | Callback sửa |
| `onAddSupplier` | Callback thêm mới |

**Logic:**
- **Tìm kiếm:** Theo tên hoặc số điện thoại.
- **Toggle trạng thái:** "Hoạt động" ↔ "Ngừng hợp tác".
- **Thêm/Sửa:** Modal `SupplierModal` với đầy đủ thông tin liên hệ.

---

## 7. Modal Components

Tất cả modal được render tại `App.jsx` hoặc trong chính View, nhận `onClose` callback để đóng.

### Danh sách Modal

| Modal | File | Mục đích |
|---|---|---|
| CustomCakeOrderModal | `modals/CustomCakeOrderModal.jsx` | Đặt bánh kem tuỳ chỉnh (kích thước, đế, nhân, trang trí, lời nhắn, giao hàng) |
| RecipeModal | `modals/RecipeModal.jsx` | Xem chi tiết công thức sản phẩm |
| EditRecipeModal | `modals/RecipeModal.jsx` (variant) | Chỉnh sửa công thức sản phẩm |
| ProductModal | `modals/ProductModal.jsx` | Thêm / Sửa sản phẩm |
| ProductCategoryModal | `modals/ProductCategoryModal.jsx` | Thêm / Sửa danh mục sản phẩm |
| CategoryModal | `modals/CategoryModal.jsx` | Thêm / Sửa danh mục thu/chi |
| TransactionModal | `modals/TransactionModal.jsx` | Tạo giao dịch thu hoặc chi |
| IngredientModal | `modals/IngredientModal.jsx` | Thêm / Sửa danh mục nguyên liệu |
| CustomerEditModal | `modals/CustomerEditModal.jsx` | Sửa thông tin khách hàng |
| HREmployeeModal | `modals/HREmployeeModal.jsx` | Thêm / Sửa nhân viên |
| SupplierModal | `modals/SupplierModal.jsx` | Thêm / Sửa nhà cung cấp |
| ReceiptViewerModal | `modals/ReceiptViewerModal.jsx` | Hiển thị hoá đơn sau giao dịch POS |

### Cấu trúc chung của Modal

```
┌────────────────────────────────────────────┐
│  Overlay tối (backdrop)                    │
│  ┌──────────────────────────────────────┐  │
│  │  Header: Tiêu đề  [X nút đóng]       │  │
│  │  Body: Nội dung form / thông tin     │  │
│  │  Footer: [Huỷ] [Xác nhận/Lưu]       │  │
│  └──────────────────────────────────────┘  │
└────────────────────────────────────────────┘
```

### CustomCakeOrderModal — Chi tiết

**Đây là modal phức tạp nhất.** Dùng để tạo đơn bánh kem tuỳ chỉnh.

**State nội bộ:**
- `step`: `1` (Cấu hình bánh) hoặc `2` (Thông tin giao hàng)
- `selectedSize`, `selectedBase`, `selectedFilling`, `selectedDecoration`
- `message` (lời nhắn trên bánh)
- `customerPhone`, `customerName`, `deliveryType` (`"pickup"` / `"delivery"`)
- `deliveryAddress`, `deliveryDate`, `depositAmount`

**Bước 1 — Cấu hình bánh:**
- Radio group cho từng thuộc tính (kích thước, đế, nhân, trang trí).
- Textarea lời nhắn.
- Tính giá tự động theo lựa chọn.

**Bước 2 — Thông tin đơn hàng:**
- Nhập SĐT → tìm khách hàng có sẵn hoặc nhập tên mới.
- Loại nhận hàng (Tại quầy / Giao hàng).
- Ngày giao / lấy hàng.
- Số tiền cọc.

---

## 8. Dữ liệu mẫu (Mock Data)

**File:** `src/data/mockData.js`

Dữ liệu mẫu thay thế backend API. Tất cả được load vào `App.jsx` như initial state.

### MOCK_PRODUCTS (6 sản phẩm)

```js
{
  id: "P001",
  name: "Bánh Croissant",
  emoji: "🥐",
  price: 35000,
  category: "Bánh Mì",
  stock: 20,
  recipe: [
    { ingredientId: "I001", quantity: 200 },  // Bột mì 200g
    { ingredientId: "I002", quantity: 100 },  // Bơ 100g
  ],
  status: "active"
}
```

### INITIAL_CUSTOMERS (5 khách hàng)

```js
{
  id: "C001",
  name: "Nguyễn Thị An",
  phone: "0912345678",
  points: 1500,
  tier: "Gold",
  discount: 10,
  status: "active"
}
```

### INITIAL_USERS (3 tài khoản test)

| Username | Password | Vai trò |
|---|---|---|
| admin | 123 | Quản lý |
| thungan | 123 | Thu ngân |
| thobep | 123 | Thợ bếp |

### MOCK_INVENTORY (5 nguyên liệu)

```js
{
  id: "I001",
  name: "Bột mì",
  unit: "g",
  quantity: 5000,
  safeStock: 2000,
  costPrice: 15000  // VNĐ/kg
}
```

---

## 9. Luồng dữ liệu & State Management

### Nguyên tắc

- **Không dùng Redux hay Context API** — toàn bộ state tập trung tại `App.jsx`.
- **Prop drilling:** State và callback truyền thẳng từ App xuống View qua props.
- **Unidirectional data flow:** View không tự sửa state, phải gọi callback từ App.

### Sơ đồ luồng

```
App.jsx (State gốc)
    │
    ├── props + callbacks ──→ AuthView
    ├── props + callbacks ──→ DashboardView
    ├── props + callbacks ──→ POSView
    │       └── callbacks ──→ Modal (ReceiptViewer)
    ├── props + callbacks ──→ OrderKDSView
    ├── props + callbacks ──→ InventoryView
    │       └── callbacks ──→ Modal (Import/Export)
    ├── props + callbacks ──→ CustomerView
    │       └── callbacks ──→ Modal (CustomerEdit)
    ├── props + callbacks ──→ ProductRecipeView
    │       └── callbacks ──→ Modal (Recipe, Product)
    ├── props + callbacks ──→ HRView
    ├── props + callbacks ──→ CashbookView
    ├── props + callbacks ──→ SupplierView
    └── props + callbacks ──→ AccountView
```

### Pattern cập nhật dữ liệu điển hình

```js
// Trong App.jsx
const handleUpdateCustomer = (updatedCustomer) => {
  setCustomers(prev =>
    prev.map(c => c.id === updatedCustomer.id ? updatedCustomer : c)
  );
};

// Truyền xuống View
<CustomerView
  customers={customers}
  onUpdateCustomer={handleUpdateCustomer}
/>
```

---

## 10. Quy ước giao diện

### Bảng màu

| Ngữ cảnh | Màu Tailwind | Hex tương đương |
|---|---|---|
| Primary (nền sidebar, nút chính) | `amber-800`, `amber-900` | `#92400e`, `#78350f` |
| Background chính | custom `#FDFBF7` | Kem/trắng ngà |
| Thành công | `green-*` | Xanh lá |
| Cảnh báo | `yellow-*`, `amber-*` | Vàng |
| Nguy hiểm/Lỗi | `red-*` | Đỏ |
| Thông tin | `blue-*` | Xanh dương |
| Neutral | `gray-*` | Xám |

### Typography

- Tiêu đề lớn: `text-xl font-bold` hoặc `text-2xl font-bold`
- Tiêu đề nhỏ: `text-sm font-semibold text-gray-600`
- Nội dung: `text-sm text-gray-700`
- Số tiền: `font-bold text-amber-800`

### Kích thước icon (Lucide)

- Sidebar: `size={20}`
- Header/Card: `size={18}` hoặc `size={24}`
- Inline (trong text): `size={14}` hoặc `size={16}`

### Cấu trúc Card chung

```jsx
<div className="bg-white rounded-xl shadow-sm border border-gray-100 p-4">
  {/* Nội dung */}
</div>
```

### Responsive

- Layout chia cột: `grid-cols-1 sm:grid-cols-2 lg:grid-cols-4`
- Sidebar: Cố định, không ẩn trên mobile (thiết kế cho desktop/tablet)
- Bảng: `overflow-x-auto` để scroll ngang trên màn nhỏ

### Nút (Button Variants)

| Loại | Class Tailwind |
|---|---|
| Primary | `bg-amber-800 hover:bg-amber-900 text-white` |
| Danger | `bg-red-600 hover:bg-red-700 text-white` |
| Secondary | `bg-gray-100 hover:bg-gray-200 text-gray-700` |
| Success | `bg-green-600 hover:bg-green-700 text-white` |
| Ghost | `text-amber-800 hover:bg-amber-50` |

---

*Tài liệu này được tạo tự động từ phân tích mã nguồn dự án h3k-bakery. Cập nhật lại khi có thay đổi lớn về kiến trúc component.*
