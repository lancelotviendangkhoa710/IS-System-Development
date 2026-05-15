# Implementation Plan — Task 1–5

> **Ràng buộc áp dụng:** MVP pattern (V/P/S/D/DTO), Amber Palette, CẤM inline style, CẤM SQL trong View/Presenter, naming camelCase tiếng Việt không dấu, PreparedStatement/CallableStatement, CẤM tự tạo Procedure/Function khi chưa được User xác nhận.

---

## Task 1 — Dialog Xuất Kho: Cải thiện UI

### Hiện trạng
- [XuatKhoView.fxml](file:///d:/Clone/src/main/resources/fxml/kho/XuatKhoView.fxml) — FXML đã tồn tại nhưng thiếu `styleClass="bg-app"` root
- [XuatKhoViewFXMLController.java](file:///d:/Clone/src/main/java/com/bakery/views/controllers/kho/XuatKhoViewFXMLController.java) — Dialog dùng `javafx.scene.control.Dialog` mặc định, không styling, không CSS class

### Phạm vi sửa
1. **XuatKhoView.fxml** — Thêm `styleClass="bg-app"`, chuẩn padding `30/40/30/40`, sửa `lblTitle` thành `lbl-title-screen`
2. **XuatKhoViewFXMLController.java** — Thêm CSS vào mỗi dialog (`dialog.getDialogPane().getStylesheets()`), apply CSS classes `lbl-title-dialog`, `text-field`, `combo-box`, `btn-primary` cho các component trong dialog
3. **bakery.css** — Thêm CSS class `.dialog-pane-styled` nếu cần (hoặc reuse existing)

### Risk: LOW
- Dialog code chỉ thay đổi UI, không đụng logic nghiệp vụ

---

## Task 2 — Xuất kho làm bánh: Hiển thị giới hạn ngày + logic "9/10"

### Hiện trạng
- `moDialogLamBanh()` (line 173–238) đã tính `tinhSoLuongKhaDung` (tối đa NL có thể làm) nhưng **CHƯA** kiểm tra giới hạn `NANGLUCSANXUAT.GIOIHANSOBANH` theo ngày
- Bảng `NANGLUCSANXUAT` (PK = `NGAYSANXUAT`) có `GIOIHANSOBANH` và `SOBANHDANHAN`
- [CauHinhGioiHanDAO](file:///d:/Clone/src/main/java/com/bakery/model/dao/hethong/CauHinhGioiHanDAO.java) đã có `layTheoNgay(LocalDate)` và `capNhat()`

### Phạm vi sửa
1. **XuatKhoSanXuatService.java** — Thêm method `layGioiHanVaDaLam(LocalDate)` gọi `CauHinhGioiHanDAO.layTheoNgay()` trả về `{gioiHan, daDung}`
2. **XuatKhoViewFXMLController.java** — Trong `moDialogLamBanh()`:
   - Thêm label "Giới hạn hôm nay: 9/10" bên cạnh label "Tối đa khả dụng"
   - Validate: `soLuongCanLam ≤ Math.min(khaDung, gioiHan - daDung)`
   - Khi chuyển sản phẩm, tính lại `khaDung` nhưng giới hạn ngày giữ nguyên
3. **SanXuatViewFXMLController.java** — Tương tự, hiển thị giới hạn ngày trong UI

### Risk: MEDIUM
- Cần đọc thêm `CauHinhGioiHanDAO` để đảm bảo không tạo procedure mới

---

## Task 3 — Phân quyền: Block Thợ Bếp vào Bán Hàng

### Hiện trạng
- [MainMenuViewFXMLController.java](file:///d:/Clone/src/main/java/com/bakery/views/controllers/hethong/MainMenuViewFXMLController.java) line 146–148: `onMoBanHang()` kiểm tra `coQuyen(BAN_HANG_POS)` → đúng logic
- **Nhưng:** `capNhatTrangThaiNut(btnBanHang, coQuyenBanHangTong)` chỉ set `setDisable(!duocCapQuyen)` + `setVisible(duocCapQuyen)` → nút **ẩn** khi không có quyền, **ĐÚNG**
- **Bug thực tế:** `onMoBanHang()` (line 225–235) kiểm tra `coQuyenPos` và `coQuyenTheoDoi` → nếu không có quyền thì set lblThongBao và return. Logic đã đúng nhưng **có thể bug phát sinh từ `PhanQuyenService` cấp nhầm quyền cho Thợ Bếp**

### Phân tích root cause
- [PhanQuyenService.java](file:///d:/Clone/src/main/java/com/bakery/services/nhansu/PhanQuyenService.java) line 143–157: Module `NHA_BEP` cấp quyền `THEO_DOI_DON_HANG` (line 148) → Thợ Bếp có quyền **Theo dõi đơn hàng**
- Line 146–148: `coQuyenBanHangTong = coQuyen(BAN_HANG_POS) || coQuyen(THEO_DOI_DON_HANG)` → **TRUE** vì Bếp có `THEO_DOI_DON_HANG`
- Kết quả: `btnBanHang` hiển thị → nhấn vào → `onMoBanHang()` → `coQuyenPos=false, coQuyenTheoDoi=true` → mở `TheoDoiDonHangView.fxml` (không phải POS)

> **Root cause:** Nút "Bán hàng" trên sidebar **gộp** cả POS lẫn Theo dõi đơn. Thợ Bếp có quyền Theo dõi đơn → nút Bán hàng hiện lên. Khi nhấn → mở Theo dõi đơn (đúng logic) nhưng **tên nút gây hiểu nhầm**.

### Phạm vi sửa
1. **MainMenuViewFXMLController.java** — Tách logic: nếu chỉ có `THEO_DOI_DON_HANG` mà không có `BAN_HANG_POS`, **đổi text nút** thành "Theo dõi đơn hàng" thay vì "Bán hàng". Hoặc ẩn hẳn nút "Bán hàng" cho Thợ Bếp — chỉ hiện nút "Theo dõi đơn" riêng (dùng `btnTheoDoiDon` thay vì ẩn nó).
2. Thay vì ẩn `btnTheoDoiDon` (hiện đang bị `anNutDieuHuong(btnTheoDoiDon)` ẩn), **hiển thị nó** cho Thợ Bếp, và ẩn `btnBanHang`.

### Risk: LOW
- Chỉ thay đổi visibility logic, không đụng RBAC backend

---

## Task 4 — Bug Email không lưu

### Hiện trạng
- [MainMenuViewFXMLController.java](file:///d:/Clone/src/main/java/com/bakery/views/controllers/hethong/MainMenuViewFXMLController.java) line 464–508:
  - Gọi `capNhatThongTinCaNhan(hoTen, sdt, matKhauMoi, xacNhan)` — **yêu cầu mật khẩu mới**
  - Sau đó gọi `capNhatEmail(emailNhap)` — **chỉ khi email không rỗng**
  
- **Bug:** `capNhatThongTinCaNhan()` **yêu cầu mật khẩu mới** (line 371: `validatePassword`). Nếu user **chỉ muốn sửa email** mà không đổi mật khẩu → exception → email cũng không được lưu.
- **Bug 2:** `capNhatEmail()` dùng `new XacThucService()` (instance mới) nhưng session vẫn OK vì đọc từ `SessionContext` static.
- **Bug 3:** Sau khi `capNhatEmail` thành công, `UserSession.getCurrentUser()` không được refresh lại email → UI vẫn hiện null.

### Phạm vi sửa
1. **XacThucService.java** — Tách `capNhatThongTinCaNhan()` để cho phép cập nhật **mà không bắt buộc đổi mật khẩu**. Mật khẩu mới chỉ cần khi có nhập.
2. **MainMenuViewFXMLController.java** — Gọi `capNhatEmail()` **trước** hoặc **song song** với `capNhatThongTinCaNhan()`. Refresh `UserSession` sau khi cập nhật email.
3. **NhanVienDAO.java** — Kiểm tra `mapNhanVien()` đã có `nv.setEmail(rs.getString("EMAIL"))` ✅ — OK.

### Risk: MEDIUM
- Thay đổi signature/logic của `capNhatThongTinCaNhan()` → cần kiểm tra caller.

---

## Task 5 — Chấm công (Attendance)

### Hiện trạng
- **Chưa có** code chấm công (grep tìm `chamCong`, `checkIn`, `checkOut`, `attendance` → 0 results)
- Bảng `CALAMVIEC` hiện tại dùng cho **thu ngân** (có `MAMAYPOS`, `TRANGTHAI`, `TIENDAUCA` v.v.)
- Yêu cầu: Tất cả nhân viên (trừ thu ngân) phải chấm công khi đăng nhập, tự check-out khi đăng xuất

### Thiết kế
1. **Reuse bảng `CALAMVIEC`** — Thêm column hoặc tạo bảng mới `CHAMCONG` (cần User xác nhận)
2. **Logic:**
   - Đăng nhập → kiểm tra vai trò, nếu không phải thu ngân → tự động INSERT `CALAMVIEC` (MAMAYPOS = null, không có tiền đầu ca)
   - Hiển thị thông báo "Đã chấm công lúc HH:mm"
   - Đăng xuất → UPDATE `THOIGIANDONGCA` = NOW cho record đang mở

### Phạm vi sửa (cần User xác nhận)

> [!WARNING]
> Task 5 **cần tạo mới Procedure và có thể cần bảng mới** → cần User xác nhận trước khi triển khai.

1. **Database** — Option A: Reuse `CALAMVIEC` (MAMAYPOS nullable, bỏ tiền) hoặc Option B: Tạo bảng `CHAMCONG` riêng
2. **ChamCongDAO** (mới) — `checkIn(int maNV)`, `checkOut(int maNV)`
3. **ChamCongService** (mới) — Validate + gọi DAO
4. **DangNhapViewFXMLController** — Sau login thành công: gọi `chamCongService.checkIn()` → hiện thông báo
5. **MainMenuViewFXMLController** / **ThoBepDashboardViewFXMLController** / **ThuKhoDashboardViewFXMLController** — Khi đăng xuất: gọi `chamCongService.checkOut()`

### Risk: HIGH
- Tạo mới DAO, Service, có thể Procedure DB → cần User confirm

---

## Thứ tự triển khai

| Ưu tiên | Task | Lý do |
|---------|------|-------|
| 1 | Task 3 — Phân quyền Bếp | Fix nhanh, chỉ sửa visibility logic |
| 2 | Task 4 — Bug Email | Fix bug hiện tại, scope nhỏ |
| 3 | Task 1 — UI Dialog Xuất kho | Polish UI, không ảnh hưởng logic |
| 4 | Task 2 — Giới hạn ngày xuất kho | Cần thêm logic mới, scope vừa |
| 5 | Task 5 — Chấm công | Scope lớn nhất, cần confirm DB |
