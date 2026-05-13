# 🔄 HANDOFF CONTEXT — Bakery Management System

> **Conversation cũ:** `bd847433-df61-45f8-9ead-af054249ce14`
> **Thời điểm:** 2026-05-13 22:13 ICT
> **Mục đích:** Chuyển context sang conversation mới để tiếp tục implement WIP Use-Cases

---

## 1. DỰ ÁN

**Tên:** Bakery Management System (H3K Bakery)
**Repo:** `D:\Clone` — corpus: `lancelotviendangkhoa710/IS-System-Development`
**GitNexus index:** `IS-System-Development` — 5868 symbols, 17445 relationships, 300 flows

### Stack
- Java 21 / JavaFX 25 / Oracle 12c+ / Maven
- Kiến trúc **MVP bắt buộc**: View → Presenter → Service → DAO → DB
- Naming: camelCase tiếng Việt, class suffix chuẩn (`DAO`, `Service`, `Presenter`, `ViewFXMLController`)
- UI: AMBER palette (`#D85A30` primary, `#185FA5` secondary, `#F1EFE8` bg) — CẤM inline style

---

## 2. TRẠNG THÁI USE-CASE (2026-05-13)

**Tổng: 57 UC | 46 DONE (80.7%) | 7 WIP | UC33 DROPPED**

### WIP — Cần hoàn thiện (theo thứ tự ưu tiên):

| UC | Tên | Trạng thái chi tiết |
|---|---|---|
| **UC45** | Truy vết nguồn gốc | 🔥 **ACTIVE DEV** — đủ 5 layer, đang test end-to-end |
| **UC35** | Tính số lượng bánh làm ra | ✅ Code xong rồi — chỉ cần verify FXML + test |
| **UC42** | Lập phiếu xuất hủy | Cần tạo `XuatHuyDialog.fxml` + Controller (approach: **Dialog**) |
| **UC44** | Tra cứu thẻ kho | Cần tạo TheKhoDAO + Service + Presenter (KiemKeKho đang gọi DAO trực tiếp) |
| **UC50** | Báo cáo lợi nhuận | `lblLoiNhuan = doanhThu * 0.3` — hardcode, cần tính giá vốn thực từ BOM |
| **UC52** | Báo cáo tồn kho | Chưa có Tab Tồn kho trong BaoCaoView + chưa có method ThongKeDAO |
| **UC43** | Cảnh báo tồn kho | Cần trigger alert trong DashboardController.initialize() |

### DROPPED:
- **UC33** — Cập nhật trạng thái công thức: bảng `CONGTHUC` **không có cột TRANGTHAI** → loại khỏi scope

---

## 3. UC45 — TRUY VẾT NGUỒN GỐC (ACTIVE DEV)

### Trạng thái hiện tại: **ĐỦ 5 LAYER — cần test end-to-end**

#### Files đã có:
```
database/06_views/vw_traceability.sql              ✅ SQL View đã viết
dao/kho/TraCuuNguonGocDAO.java                     ✅ DAO có
services/kho/TraCuuNguonGocService.java            ✅ Service có (layDanhSachMe + layChiTietNguonGoc)
presenters/kho/TraCuuNguonGocPresenter.java        ✅ Presenter có
interfaces/kho/ITraCuuNguonGocView.java            ✅ Interface có
controllers/kho/TraCuuNguonGocViewFXMLController.java ✅ Controller đầy đủ (161 dòng)
fxml/kho/TraCuuNguonGocView.fxml                   ✅ FXML có
```

#### Kiến trúc màn hình (2 bảng split):
- **Trái:** `tblMe` — danh sách mẻ sản xuất (`MeSanXuatDTO`)
- **Phải:** `tblChiTiet` — chi tiết lô NL của mẻ đang chọn (`TraCuuNguonGocDTO`)
- **Filter:** `txtTimKiem` + `dpTuNgay` + `dpDenNgay`

#### SQL View đã có (`VW_TRACUUNGUONGOC`):
```sql
MESANXUAT → SANPHAM → PHIEUXUATKHO → CTPHIEUXUAT_NL → CTPHIEUNHAP
         → NGUYENLIEU → PHIEUNHAPKHO → NHACUNGCAP
```
Join chuỗi: mẻ sản xuất → phiếu xuất kho (sản xuất) → chi tiết xuất (lô NL) → chi tiết nhập → NCC

#### Việc cần làm để UC45 → DONE:
1. **Verify** `VW_TRACUUNGUONGOC` đã deploy lên DB Oracle chưa (chạy `SELECT * FROM VW_TRACUUNGUONGOC FETCH FIRST 5 ROWS ONLY`)
2. **Verify** `TraCuuNguonGocDAO` query đúng view
3. **Verify** `KhoViewFXMLController.chuyenTab("traceability")` hoạt động — Tab "Truy vết" hiện ra trong KhoView
4. **Test** flow: Vào Tab Truy vết → Bảng mẻ load → Chọn mẻ → Bảng NL hiện chi tiết
5. **Test** filter: Nhập từ khóa SP + chọn date range → bấm Tìm kiếm

#### DTOs liên quan:
- `MeSanXuatDTO`: `maMe`, `tenSP`, `soLuongSanXuat`, `ngaySanXuat`, `tenNhanVien`
- `TraCuuNguonGocDTO`: `tenNguyenLieu`, `soLuongDaDung`, `maLo`, `maVachLo`, `nsxNguyenLieu`, `hanSuDung`, `tenNCC`, `sdtNCC`

---

## 4. KẾ HOẠCH 3 SPRINT (CÒN LẠI)

### Sprint 1 — Ưu tiên cao (~3h)
| Task | UC | Effort |
|---|---|---|
| Verify + test UC45 end-to-end | UC45 | 2h |
| Verify SanXuatView.fxml + test | UC35 | 1h |

### Sprint 2 — Lõi kho (~7h)
| Task | UC | Effort |
|---|---|---|
| Tạo `XuatHuyDialog.fxml` + `XuatHuyDialogController` + service method | UC42 | 3h |
| Tạo `TheKhoDAO` + `TheKhoService` + `TheKhoPresenter` + refactor KiemKeKho | UC44 | 4h |

### Sprint 3 — Báo cáo + Cảnh báo (~9h)
| Task | UC | Effort |
|---|---|---|
| Fix `ThongKeDAO.getTongGiaVon()` + sửa `refreshData()` bỏ hardcode 30% | UC50 | 3h |
| Thêm Tab Tồn kho + `ThongKeDAO.getBaoCaoTonKho()` | UC52 | 4h |
| `NguyenLieuDAO.demMatHangSapHet()` + alert trong `DashboardController` | UC43 | 2h |

---

## 5. CHI TIẾT UC CÒN LẠI

### UC42 — Xuất hủy (Dialog approach đã xác nhận)
**Approach:** Button "Xuất hủy" trong `XuatKhoView.fxml` → mở `XuatHuyDialog.fxml`
```
TẠO MỚI:
  fxml/kho/XuatHuyDialog.fxml                   — ComboBox NL + TextField số lượng + TextField lý do
  controllers/kho/XuatHuyDialogController.java   — validate + gọi Service

SỬA:
  fxml/kho/XuatKhoView.fxml                      — thêm Button "Xuất hủy"
  controllers/kho/XuatKhoViewFXMLController.java — thêm onXuatHuy()
  services/kho/XuatKhoSanXuatService.java        — thêm xuatHuy(maNL, soLuong, lyDo, maNV)
  dao/kho/PhieuXuatKhoDAO.java                   — verify/thêm method
```
> Cần kiểm tra DB có `PROC_XUATKHOHUY` chưa trước khi code

### UC44 — Thẻ kho (thiếu MVP layer)
**Vấn đề:** `KiemKeKhoViewFXMLController` gọi DAO trực tiếp → vi phạm MVP
**Giải pháp:** Tạo đủ Presenter + Service + DAO, thêm UI lịch sử biến động
```
TẠO MỚI:
  dao/kho/TheKhoDAO.java                         — query lịch sử NL theo MANL + date range
  services/kho/TheKhoService.java                — wrap DAO
  presenters/kho/TheKhoPresenter.java            — orchestrate
  interfaces/kho/ITheKhoView.java               — contract

REFACTOR:
  controllers/kho/KiemKeKhoViewFXMLController.java — implements ITheKhoView, bỏ gọi DAO trực tiếp
  fxml/kho/KiemKeKhoView.fxml                    — thêm filter + history table
```

### UC50 — Lợi nhuận (bug hardcode)
**Bug:** `BaoCaoViewFXMLController.java:135` → `doanhThu * 0.3` (sai hoàn toàn)
**Fix:** Thêm `ThongKeDAO.getTongGiaVon(loai, giaTri)` JOIN: `HOADON → CTDONHANG → CONGTHUC → NGUYENLIEU`

### UC52 — Báo cáo tồn kho
Chưa có method trong `ThongKeDAO`. Cần thêm Tab mới "Tồn kho" vào `BaoCaoView.fxml`
Columns: Nguyên liệu | Tồn đầu kỳ | Nhập kỳ | Xuất kỳ | Tồn cuối kỳ

### UC43 — Cảnh báo tồn kho
Thêm trong `DashboardController.initialize()`:
```java
// Kiểm tra NL sắp hết khi vào Dashboard
List<String> canhBao = nguyenLieuService.layCanhBaoTonKho();
if (!canhBao.isEmpty()) { Alert warning... }
```
Cần thêm `NguyenLieuDAO.demMatHangSapHet()` và `NguyenLieuService.layCanhBaoTonKho()`

---

## 6. RULES BẮT BUỘC (nhắc lại)

```
TRƯỚC KHI SỬA BẤT KỲ SYMBOL:
  → gitnexus_impact({target: "symbolName", direction: "upstream"})
  → Báo blast radius cho user

TRƯỚC KHI COMMIT:
  → gitnexus_detect_changes()

HIGH/CRITICAL impact → DỪNG, cảnh báo user

KHÔNG BAO GIỜ:
  → SQL trong View/Presenter
  → Rename bằng find-and-replace (dùng gitnexus_rename)
  → Hardcode CSS inline trong FXML
  → Commit khi chưa detect_changes
```

---

## 7. FILES ĐANG MỞ (context hiện tại)

```
D:\Clone\src\main\resources\fxml\kho\KhoView.fxml                    ← đang active
D:\Clone\src\main\java\com\bakery\views\controllers\kho\KhoViewFXMLController.java
D:\Clone\src\main\java\com\bakery\views\controllers\kho\TraCuuNguonGocViewFXMLController.java
D:\Clone\src\main\resources\fxml\kho\TraCuuNguonGocView.fxml
D:\Clone\database\06_views\vw_traceability.sql
D:\Clone\src\main\java\com\bakery\views\interfaces\kho\ITraCuuNguonGocView.java
D:\Clone\src\main\java\com\bakery\services\kho\TraCuuNguonGocService.java
```

---

## 8. LỆNH BẮT ĐẦU CHO CONVERSATION MỚI

Paste đoạn này vào đầu conversation mới:

```
Đọc file AGENTS.md tại D:\Clone\.agents\ theo đúng quy trình startup.

Context handoff từ conversation bd847433: Tôi đang làm Bakery Management System.
Đã hoàn thiện 46/57 Use-Case. Còn 7 UC đang WIP theo plan ở file:
C:\Users\Acer\.gemini\antigravity\brain\bd847433-df61-45f8-9ead-af054249ce14\completion_plan.md

Task ưu tiên ngay bây giờ: HOÀN THIỆN UC45 (Truy vết nguồn gốc).
Đủ 5 layer MVP rồi. Cần verify kết nối end-to-end và test.

Sau đó tiếp tục theo Sprint Plan trong completion_plan.md.
```
