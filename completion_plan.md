# 🗺️ Kế Hoạch Hoàn Thiện Use-Cases — Bakery Management System

> **Cập nhật:** 2026-05-13 | **Scope:** 7 UC WIP → DONE
> **Nguyên tắc:** Mỗi task chỉ thực hiện sau khi `gitnexus_impact` đã chạy. Không sửa DB schema khi chưa xác nhận.

---

## Tổng quan UC cần hoàn thiện

| # | UC | Tên | Công việc còn lại | Độ phức tạp |
|---|---|---|---|---|
| 1 | UC45 | Truy vết nguồn gốc | Hoàn thiện UI filter + kết nối View | 🟡 Trung bình |
| ~~2~~ | ~~UC33~~ | ~~Cập nhật trạng thái công thức~~ | ~~DROPPED — CONGTHUC không có cột TRANGTHAI~~ | ❌ Dropped |
| 3 | UC42 | Lập phiếu xuất hủy | Dialog trong XuatKhoViewFXMLController | 🟡 Trung bình |
| 4 | UC44 | Tra cứu thẻ kho | Tạo Presenter + Service + kết nối KiemKeKho | 🟡 Trung bình |
| 5 | UC50 | Báo cáo lợi nhuận | Thêm method tính giá vốn thực tế vào ThongKeDAO | 🟡 Trung bình |
| 6 | UC52 | Báo cáo tồn kho | Thêm method tổng hợp đầu kỳ/cuối kỳ | 🟡 Trung bình |
| 7 | UC43 | Cảnh báo tồn kho | Alert trong DashboardController.initialize() | 🟡 Trung bình |
| 8 | UC35 | Tính số lượng bánh làm ra | UC35 **ĐÃ XONG** — cần verify thực tế | ✅ Verify |

---

## 📌 Phân tích chi tiết từng UC

---

### UC45 — Truy vết nguồn gốc *(đang active dev)*

**Hiện trạng:** Đủ 5 layer (View/Controller/Presenter/Service/DAO) — nhưng cần verify kết nối end-to-end và kiểm tra `VW_TRACUUNGUONGOC`.

**Việc còn lại:**
- [ ] Verify `VW_TRACUUNGUONGOC` (`database/06_views/vw_traceability.sql`) đã deploy chưa
- [ ] Kiểm tra `TraCuuNguonGocDAO.traTimNguonGoc()` gọi đúng view/procedure
- [ ] `TraCuuNguonGocViewFXMLController` — bind data vào TableView đúng chưa
- [ ] Test filter theo ngày, sản phẩm hoạt động đúng
- [ ] Kiểm tra `KhoViewFXMLController.chuyenTab("traceability")` đã hoạt động

**Files liên quan:**
```
fxml/kho/TraCuuNguonGocView.fxml
controllers/kho/TraCuuNguonGocViewFXMLController.java
presenters/kho/TraCuuNguonGocPresenter.java
services/kho/TraCuuNguonGocService.java
dao/kho/TraCuuNguonGocDAO.java
database/06_views/vw_traceability.sql
```

---

### ~~UC33~~ — Cập nhật trạng thái công thức *(DROPPED)*

> ❌ **DROPPED:** Bảng `CONGTHUC` không có cột `TRANGTHAI`. Không thể implement soft-disable mà không thay đổi schema DB. UC này bị loại khỏi scope.

---

### UC42 — Lập phiếu xuất hủy *(Dialog trong XuatKhoView)*

**Hiện trạng:** `PhieuXuatKhoDAO` tồn tại nhưng chưa rõ có hỗ trợ loại `XUAT_HUY`. `XuatKhoViewFXMLController` (19KB) xử lý xuất sản xuất — cần thêm button mở Dialog xuất hủy.

**Approach đã xác nhận:** ✅ **Dialog** — Button "Xuất hủy" trong `XuatKhoView.fxml` mở một Dialog riêng.

**Xuất hủy vs Xuất sản xuất:**
- Xuất sản xuất: chọn sản phẩm → tự động tính nguyên liệu theo công thức
- Xuất hủy: chọn nguyên liệu trực tiếp + số lượng + **lý do hủy** (bắt buộc)

**Việc còn lại:**
- [ ] Kiểm tra `PhieuXuatKhoDAO` — có method xuất hủy chưa (tìm `LOAIPHIEU = 'HUY'`)
- [ ] Kiểm tra DB có `PROC_XUATKHOHUY` hay cần tạo mới
- [ ] Tạo `XuatHuyDialog.fxml` — form: ComboBox nguyên liệu + TextField số lượng + TextField lý do
- [ ] Tạo `XuatHuyDialogController.java` — validate + gọi Service
- [ ] Thêm `xuatHuy(int maNL, double soLuong, String lyDo, int maNV)` vào `XuatKhoSanXuatService`
- [ ] Thêm method DAO vào `PhieuXuatKhoDAO` nếu chưa có
- [ ] Thêm Button "Xuất hủy" vào `XuatKhoView.fxml`
- [ ] Bind button mở dialog vào `XuatKhoViewFXMLController.onXuatHuy()`

**Files liên quan:**
```
fxml/kho/XuatHuyDialog.fxml                     → TẠO MỚI
controllers/kho/XuatHuyDialogController.java     → TẠO MỚI
fxml/kho/XuatKhoView.fxml                        → thêm Button Xuất hủy
controllers/kho/XuatKhoViewFXMLController.java   → thêm onXuatHuy() mở dialog
services/kho/XuatKhoSanXuatService.java          → thêm method xuatHuy
dao/kho/PhieuXuatKhoDAO.java                     → thêm/verify method
database/05_procedures/                          → PROC_XUATKHOHUY nếu chưa có
```

---

### UC44 — Tra cứu thẻ kho *(thiếu Presenter chuyên biệt)*

**Hiện trạng:** `KiemKeKhoViewFXMLController` đang gọi DAO trực tiếp (vi phạm kiến trúc MVP). Không có Presenter và Service riêng cho thẻ kho. `KiemKeKhoView` chỉ hiện snapshot tồn kho, không có lịch sử biến động.

**Phân tích:** UC44 = xem lịch sử nhập/xuất của **một nguyên liệu cụ thể**. Cần query `PHIEUNHAPKHO_CT` + `PHIEUXUATKHO_CT` theo `MANL` và khoảng thời gian.

**Việc còn lại:**
- [ ] Tạo `TheKhoDAO.java` — query lịch sử biến động theo `MANL` + date range
- [ ] Tạo `TheKhoService.java` — wrap DAO, add filter logic  
- [ ] Tạo `TheKhoPresenter.java` — orchestrate View ↔ Service
- [ ] Tạo `ITheKhoView.java` interface
- [ ] Refactor `KiemKeKhoViewFXMLController` implements `ITheKhoView`
- [ ] Cập nhật `KiemKeKhoView.fxml` — thêm filter nguyên liệu + date range picker + history table

**Files liên quan:**
```
fxml/kho/KiemKeKhoView.fxml                    → refactor, thêm filter + history table
controllers/kho/KiemKeKhoViewFXMLController.java → refactor implements ITheKhoView
presenters/kho/TheKhoPresenter.java             → TẠO MỚI
services/kho/TheKhoService.java                 → TẠO MỚI
dao/kho/TheKhoDAO.java                          → TẠO MỚI
interfaces/kho/ITheKhoView.java                 → TẠO MỚI
```

---

### UC50 — Báo cáo lợi nhuận *(công thức hardcode)*

**Hiện trạng:** `BaoCaoViewFXMLController` line 135:
```java
lblLoiNhuan.setText(String.format("%,.0fđ", doanhThu * 0.3)); // ⚠️ Hardcode 30%!
```
Lợi nhuận đang tính bằng **30% doanh thu** — không phải giá vốn thực tế từ BOM.

**Giải pháp đúng:** `Lợi nhuận = Doanh thu - Giá vốn hàng bán`. Giá vốn = `SUM(định_mức × đơn_giá_NL × số_lượng)` theo `CONGTHUC`.

**Việc còn lại:**
- [ ] Thêm method `getTongGiaVon(String loai, String giaTri)` vào `ThongKeDAO` — JOIN `HOADON` → `CTDONHANG` → `CONGTHUC` → `NGUYENLIEU`
- [ ] Thêm `getTongGiaVon()` vào `ThongKeService`
- [ ] Sửa `BaoCaoViewFXMLController.refreshData()` — gọi service thay vì hardcode
- [ ] Thêm label "Giá vốn" vào `BaoCaoView.fxml` để hiện chi tiết hơn

**Files liên quan:**
```
dao/baocao/ThongKeDAO.java          → thêm method getTongGiaVon
services/baocao/ThongKeService.java → thêm method
controllers/baocao/BaoCaoViewFXMLController.java → sửa refreshData()
fxml/baocao/BaoCaoView.fxml         → thêm label giá vốn (tuỳ chọn)
```

---

### UC52 — Báo cáo tồn kho *(chưa có method)*

**Hiện trạng:** `ThongKeDAO` không có method tổng hợp tồn kho theo kỳ. `BaoCaoView` chưa có tab/section riêng cho báo cáo tồn kho.

**Việc còn lại:**
- [ ] Thêm method `getBaoCaoTonKho(LocalDate tuNgay, LocalDate denNgay)` vào `ThongKeDAO`
  - Tồn đầu kỳ = tổng nhập - tổng xuất trước `tuNgay`
  - Nhập kỳ = `SUM` phiếu nhập trong khoảng
  - Xuất kỳ = `SUM` phiếu xuất trong khoảng  
  - Tồn cuối kỳ = Đầu kỳ + Nhập - Xuất
- [ ] Thêm method `getBaoCaoTonKho()` vào `ThongKeService`
- [ ] Thêm Tab "Tồn kho" vào `BaoCaoView.fxml` với bộ lọc kỳ báo cáo
- [ ] Thêm `TableView` với cột: Nguyên liệu | Tồn đầu kỳ | Nhập | Xuất | Tồn cuối kỳ
- [ ] Bind vào `BaoCaoViewFXMLController`

**Files liên quan:**
```
dao/baocao/ThongKeDAO.java          → thêm method
services/baocao/ThongKeService.java → thêm method
controllers/baocao/BaoCaoViewFXMLController.java → thêm tab handler
fxml/baocao/BaoCaoView.fxml         → thêm Tab Tồn kho
```

---

### UC43 — Cảnh báo tồn kho *(thiếu notification system)*

**Hiện trạng:** `KiemKeKhoViewFXMLController` đã **tính đúng** trạng thái (`⚠ Sắp hết`, `⛔ Hết hàng`) nhưng chỉ hiện khi user chủ động vào màn hình. Không có proactive alert.

**Approach khả thi (không cần background thread phức tạp):** Alert khi user đăng nhập vào Dashboard — check số lượng mặt hàng sắp hết và hiển thị badge/notification trên menu.

**Việc còn lại:**
- [ ] Thêm method `demMatHangSapHet()` vào `NguyenLieuDAO` — `COUNT(*) WHERE SOLUONGTONTONG <= MUCTOANTOANTONG`
- [ ] Thêm `layCanhBaoTonKho()` vào `NguyenLieuService` — trả `List<String>` tên NL sắp hết
- [ ] Trigger alert trong `DashboardController.initialize()` — hiện `Alert` nếu có mặt hàng cảnh báo
- [ ] (Optional) Thêm badge đỏ trên nút "Kho" trong `MainMenuView.fxml`

**Files liên quan:**
```
dao/kho/NguyenLieuDAO.java          → thêm method demMatHangSapHet
services/kho/NguyenLieuService.java → thêm method layCanhBaoTonKho
controllers/hethong/DashboardController.java → trigger alert khi init
fxml/hethong/DashboardView.fxml     → (optional) thêm badge notification
```

---

### UC35 — Tính số lượng bánh làm ra *(ĐÃ DONE — cần verify)*

**Hiện trạng thực tế:** `SanXuatViewFXMLController` (175 dòng) **đã implement đầy đủ**:
- ✅ ComboBox chọn sản phẩm → load số lượng khả dụng (`FUNC_SOLUONGKHADUNG`)
- ✅ Nhập số lượng kế hoạch → tính bảng nguyên liệu cần xuất
- ✅ Highlight đỏ dòng thiếu kho
- ✅ `CongThucService.tinhSoLuongKhaDung()` + `layCongThucVaTonKho()`

**Việc còn lại:**
- [ ] Verify `SanXuatView.fxml` có đủ FXML nodes tương ứng với các `@FXML` fields
- [ ] Verify `SanXuatView.fxml` được link đúng vào `KhoView.fxml` tab "Sản xuất"
- [ ] Test end-to-end: Chọn SP → Hiện số khả dụng → Nhập số lượng → Tính toán
- [ ] **UC35 → DONE** nếu verify pass

**Files liên quan:**
```
fxml/kho/SanXuatView.fxml                     → verify FXML nodes
controllers/kho/SanXuatViewFXMLController.java → verify (đã xong)
fxml/kho/KhoView.fxml                         → verify tab Sản xuất
```

---

## 🗓️ Sprint Plan

### Sprint 1 — Hoàn thiện đang dở *(Ưu tiên cao nhất)*

| Task | UC | Effort | Output |
|---|---|---|---|
| Verify + test UC45 end-to-end | UC45 | 2h | UC45 → DONE |
| Verify UC35 FXML + test | UC35 | 1h | UC35 → DONE |
| ~~UC33 — DROPPED~~ | ~~UC33~~ | ~~—~~ | ~~Không có TRANGTHAI~~ |

### Sprint 2 — Phiếu xuất hủy + Thẻ kho

| Task | UC | Effort | Output |
|---|---|---|---|
| Tạo XuatHuyDialog + Controller + Service | UC42 | 3h | UC42 → DONE |
| Tạo TheKhoPresenter + Service + DAO | UC44 | 4h | UC44 → DONE |

### Sprint 3 — Báo cáo + Cảnh báo

| Task | UC | Effort | Output |
|---|---|---|---|
| Fix công thức lợi nhuận (ThongKeDAO + UI) | UC50 | 3h | UC50 → DONE |
| Thêm Tab Tồn kho vào BaoCaoView | UC52 | 4h | UC52 → DONE |
| Implement alert trong DashboardController | UC43 | 2h | UC43 → DONE |

---

## 🚦 Dependency Map

```
UC45 ──────────────────────────────────── Độc lập (đang active dev)
UC35 ──────────────────────────────────── Độc lập (verify thôi)
UC33 ──── ❌ DROPPED (CONGTHUC không có cột TRANGTHAI)
UC42 ──── Dialog trong XuatKhoView — verify PROC_XUATKHOHUY trước
UC44 ──── cần tạo TheKhoDAO (query PHIEUNHAPKHO_CT + PHIEUXUATKHO_CT)
UC50 ──── cần ThongKeDAO.getTongGiaVon (JOIN HOADON→CTDONHANG→CONGTHUC→NL)
UC52 ──── cần ThongKeDAO.getBaoCaoTonKho + Tab mới trong BaoCaoView
UC43 ──── cần NguyenLieuDAO.demMatHangSapHet + DashboardController
```

---

## ✅ Definition of Done (mỗi UC)

- [ ] Đủ 5 layer MVP (View/Controller/Presenter/Service/DAO) — không có logic nghiệp vụ trong View
- [ ] `gitnexus_impact()` đã chạy trước khi sửa bất kỳ symbol nào
- [ ] Không có SQL inline trong View/Presenter
- [ ] `gitnexus_detect_changes()` chạy trước commit
- [ ] Test manual end-to-end pass
