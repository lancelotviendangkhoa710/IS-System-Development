# Giải thích kỹ thuật – Hệ thống Bakery Management

---

## 1. Workflow lập hóa đơn bán lẻ (Thanh toán ngay tại quầy)

### Tổng quan luồng

```
[User bấm nút] 
   → View (DonHangViewFXMLController)
   → Presenter (DonHangPresenter)
   → Service (DonHangService → ThanhToanService)
   → DAO (DonHangDAO / HoaDonDAO)
   → Oracle DB (PROC_TAODONHANG / PROC_TAOHOADON / PROC_THANHTOANVATHANGHANG)
```

---

### Bước 0 – Khởi tạo màn hình

**File:** [DonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/DonHangViewFXMLController.java#L92-L103)

```java
// initialize() — gọi khi FXML load xong
presenter = new DonHangPresenter(this, new DonHangService(), dialogFactory);
presenter.taiDuLieuBanDau();
```

**Presenter gọi Service:**
```
taiDuLieuBanDau()
  → orderService.layDanhSachSanPhamPOS()   // nạp sản phẩm lên grid
  → orderService.layMapDanhMucSanPham()    // nạp danh mục cho ComboBox
  → orderService.layDanhSachTrangThaiDon() // nạp trạng thái đơn
  → view.hienThiDanhSachSanPham(...)
  → view.hienThiDuLieuTuyChinh(...)
```

---

### Bước 1 – Nhân viên chọn sản phẩm & thêm vào giỏ

**File View** → `taoCardSanPham()` → Nút "Thêm" → `presenter.themSanPhamVaoGio(sanPham)`

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L134-L156)

```java
// Presenter.themSanPhamVaoGio()
if (existed != null) {
    existed.setSoLuong(existed.getSoLuong() + 1); // tăng SL nếu đã có
} else {
    gioHangItems.add(newItem); // thêm mới vào danh sách nội bộ
}
capNhatGioHangVaTien(); // sync lại View
```

**`capNhatGioHangVaTien()`** [line 268]:
```
orderService.tinhTienHoaDon(request)   → ThanhToanService.tinhTienHoaDon()
  → subtotal * (1 + 0.085)             // cộng thuế VAT 8.5%
→ view.lamMoiBaoCaoTien(...)           // cập nhật lblTongTienHang, lblTongThanhToan
→ view.lamMoiBangGioHang(...)          // refresh TableView
→ view.batTatNutThanhToan(...)         // enable/disable nút Thanh toán
```

> **Rule chi phối:** Presenter là orchestrator — chỉ Presenter biết gọi Service, View chỉ hiển thị kết quả.

---

### Bước 2 – Nhấn "Thanh toán"

**View** → `@FXML onThanhToan()` → `presenter.moDialogTaoDon()`

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L286-L414)

```java
// moDialogTaoDon() — chuỗi kiểm tra Fail-Fast
1. Kiểm tra ca làm việc (SessionContext.getMaCa() > 0)
   → Nếu chưa mở ca → view.yeuCauMoCa() (hiện dialog Mở ca, blocking)
2. Kiểm tra giỏ hàng rỗng
3. Kiểm tra tồn kho thực tế:
   → orderService.kiemTraTonKhoGioHang(gioHangItems)
4. Mở dialog TaoDonHang qua dialogFactory.showCreateOrderDialog(tongTienPhaiTra, lookup)
   → Người dùng điền: tên KH, SĐT, hình thức thanh toán (TM/CK), tiền đưa
5. Kiểm tra loại đơn:
   - IMMEDIATE → xuLyThanhToanNgay()
   - PRE_ORDER  → xuLyDatTruoc()
```

---

### Bước 3 – Xử lý thanh toán ngay (`xuLyThanhToanNgay`)

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L416-L447)

```java
private void xuLyThanhToanNgay(req, tongTien) throws Exception {
    // Dựng YeuCauTaoDonHangDTO
    request.setMaNVLap(getCurrentUserId());
    request.setHinhThucNhan(1);        // trực tiếp
    request.setNgayGioNhanBanh(now + 30s); // đệm tránh lỗi "quá khứ"
    request.setPhanTramGiamGia(phanTramGiamGia);
    
    int maPTTT = orderService.layMaPTTTTheoHinhThuc(req.hinhThucThanhToan());
    HoaDonDTO hd = orderService.thanhToanTrucTiep(request, req.soTienKhachDua());
    
    view.hienThiThanhCong("Đã thanh toán! Mã HĐ: #" + hd.getMaHD());
    view.inPhieuHoaDon("HÓA ĐƠN BÁN LẺ", hd, null, ...);
}
```

---

### Bước 4 – Service xử lý nghiệp vụ

**File:** [ThanhToanService.java](file:///D:/Clone/src/main/java/com/bakery/services/banhang/ThanhToanService.java#L72-L130)

```java
public HoaDonDTO thanhToanTrucTiep(request, soTienKhachDua) throws Exception {
    1. Kiểm tra ca làm việc (FK_HD_CA yêu cầu MACA)
    2. tinhTienHoaDon(request)          → subtotal * 1.085
    3. Áp dụng giảm giá (phanTramGiamGia)
    4. Validate soTienKhachDua >= soTienSauGiam
    5. layMaTrangThaiHoanThanh()        → SELECT động từ TRANGTHAIDON
    6. quanLyDonHangService.taoDonHang(request)
                                        → DonHangDAO.taoDonHang()
                                        → CALL PROC_TAODONHANG(...)
    7. taoHoaDonDTO(maDon, ...)         → build HoaDonDTO
    8. hoaDonDAO.themHoaDonMoi(hd)      → CALL PROC_TAOHOADON(...)
    9. hoaDonDAO.thanhToanVaThangHang() → CALL PROC_THANHTOANVATHANGHANG(...)
    10. taoPhieuThuChiTuHoaDon()        → ghi sổ quỹ (chỉ khi tiền mặt)
    11. return hoaDonDAO.layHoaDonTheoMa(maHD)
}
```

---

### Bước 5 – DAO gọi Oracle Stored Procedure

**File:** [DonHangDAO.java](file:///D:/Clone/src/main/java/com/bakery/model/dao/banhang/DonHangDAO.java#L29-L80)

```java
// taoDonHangWithConn()
String sql = "{CALL PROC_TAODONHANG(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
CallableStatement cstmt = conn.prepareCall(sql);
cstmt.setString(8, taoJsonChiTiet(dsCtDonHang, dsCtTuyChinh)); // truyền JSON danh sách
cstmt.registerOutParameter(9, Types.NUMERIC);  // P_MADON_OUT
cstmt.execute();
return cstmt.getInt(9); // nhận mã đơn mới
```

**File:** [HoaDonDAO.java](file:///D:/Clone/src/main/java/com/bakery/model/dao/banhang/HoaDonDAO.java#L70-L85)

```java
// themHoaDonMoiWithConn()
String sql = "{CALL PROC_TAOHOADON(?, ?, ?, ?, ?, ?, ?, ?)}";
cstmt.registerOutParameter(8, Types.NUMERIC); // P_MAHD_OUT
cstmt.execute();
int maHD = cstmt.getInt(8);
```

```java
// thanhToanVaThangHangWithConn()
String sql = "{CALL PROC_THANHTOANVATHANGHANG(?, ?, ?)}";
// → Procedure cộng điểm thành viên, nâng hạng nếu đủ điều kiện
```

---

### Bước 6 – In hóa đơn

**View** nhận lệnh từ Presenter qua interface:

```java
// IDonHangView.inPhieuHoaDon() → DonHangViewFXMLController
FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/banhang/HoaDonView.fxml"));
HoaDonViewFXMLController ctrl = loader.getController();
ctrl.setReceiptData("HÓA ĐƠN BÁN LẺ", hd, null, cart, ...);
Stage stage = new Stage();
stage.show(); // Hiện cửa sổ in
```

---

### Sơ đồ tóm tắt call graph

```
[View] onThanhToan()
  │
  └─► [Presenter] moDialogTaoDon()
        ├─ Fail-Fast: kiểm tra ca, tồn kho
        ├─ dialogFactory.showCreateOrderDialog()  ← hiện UI dialog
        └─► xuLyThanhToanNgay()
              └─► [Service] DonHangService.thanhToanTrucTiep()
                    └─► [Service] ThanhToanService.thanhToanTrucTiep()
                          ├─► [Service] QuanLyDonHangService.taoDonHang()
                          │     └─► [DAO] DonHangDAO.taoDonHang()
                          │           └─► DB: PROC_TAODONHANG
                          ├─► [DAO] HoaDonDAO.themHoaDonMoi()
                          │     └─► DB: PROC_TAOHOADON
                          ├─► [DAO] HoaDonDAO.thanhToanVaThangHang()
                          │     └─► DB: PROC_THANHTOANVATHANGHANG
                          └─► [DAO] PhieuThuChiDAO.taoPhieuThuChi() (nếu tiền mặt)
              └─► [View] inPhieuHoaDon()   ← Presenter gọi View qua interface
```

---

---

## 2. Tại sao dùng MVP thay vì MVC?

### Câu trả lời ngắn gọn khi thầy hỏi

> **"MVP phù hợp hơn MVC cho JavaFX vì trong JavaFX không có khái niệm Router/URL — View và Controller gắn chặt qua FXML. Nếu dùng MVC truyền thống, Controller sẽ bị nhét cả logic nghiệp vụ lẫn điều khiển UI, vi phạm Single Responsibility. MVP tách rõ: View chỉ hiển thị, Presenter xử lý tất cả quyết định nghiệp vụ và giao tiếp với View qua Interface — dễ test, dễ bảo trì."**

---

### So sánh chi tiết MVC vs MVP trong ngữ cảnh JavaFX

| Tiêu chí | MVC (truyền thống) | MVP (dự án này) |
|---|---|---|
| **View ↔ Controller** | Controller biết trực tiếp View | View chỉ biết interface (IDonHangView) |
| **Ai quyết định logic?** | Controller xử lý + cập nhật View trực tiếp | Presenter quyết định, gọi View qua interface |
| **Testability** | Khó test — Controller phụ thuộc JavaFX | Presenter test được — không import JavaFX |
| **Dependency** | Controller → Model, Controller → View | Presenter → Service, Presenter → IView (abstract) |
| **Trong JavaFX** | FXML Controller = Controller + View trộn lẫn | FXML Controller = View thuần, Presenter riêng |

---

### Lý do cụ thể trong dự án này

**1. JavaFX không có URL Routing**

Trong web (Spring MVC), Controller nhận HTTP request từ Router — độc lập với View. Trong JavaFX, FXML Controller *là* View (gắn với layout). Nếu nhồi logic vào đây → vi phạm SRP.

**2. Interface IDonHangView tách biệt hoàn toàn**

```java
// Presenter chỉ thấy interface — không biết JavaFX tồn tại
private final IDonHangView view;

view.hienThiDanhSachSanPham(ds, dict);  // Presenter gọi qua interface
view.lamMoiBaoCaoTien(tongHang, giam, tongThanhToan, ...);
view.batTatNutThanhToan(true);
```

→ Presenter có thể test mà không cần mở màn hình JavaFX (dùng mock IDonHangView).

**3. Presenter không import JavaFX (đúng rule core)**

```java
// DonHangPresenter.java — import list
import com.bakery.model.dto.*;
import com.bakery.services.banhang.DonHangService;
import com.bakery.views.interfaces.banhang.IDonHangView; // chỉ interface
// KHÔNG có: import javafx.scene.*, import javafx.fxml.*
```

> Ngoại lệ được phép: `Platform.runLater()` trong Presenter khi cần callback sau Task background.

**4. View thuần — không chứa logic**

```java
// DonHangViewFXMLController — @FXML event
@FXML private void onThanhToan() {
    if (presenter != null) presenter.moDialogTaoDon(); // 1 dòng, delegate hết
}
```

---

---

## 3. Đa luồng trong dự án

### Có làm đa luồng không?

**CÓ.** Dự án dùng đa luồng ở 2 chỗ quan trọng: **JavaFX Task** cho tác vụ DB nặng, và **Platform.runLater()** để cập nhật UI từ thread phụ.

---

### Đa luồng ở đâu?

#### 3.1 – Hủy đơn hàng: `javafx.concurrent.Task`

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L624-L648)

```java
// huyDonHang() — tác vụ DB chạy trên thread riêng
javafx.concurrent.Task<Void> taskHuy = new javafx.concurrent.Task<>() {
    @Override
    protected Void call() throws Exception {
        // Thread phụ: gọi DB (có thể mất vài giây)
        orderService.huyDonVaHoanCoc(maDon, lyDoHuy, maNvHienTai, ...);
        return null;
    }
};

// Callback khi thành công — PHẢI dùng Platform.runLater để update UI
taskHuy.setOnSucceeded(event -> Platform.runLater(() -> {
    view.hienThiThongBaoTraCuu("Đã hủy đơn #" + maDon + "...");
    timKiemDonTheoDoi(...); // refresh danh sách
}));

taskHuy.setOnFailed(event -> Platform.runLater(() -> {
    view.hienThiLoiTraCuu("Hủy đơn thất bại: " + taskHuy.getException().getMessage());
}));

new Thread(taskHuy, "thread-huy-don-" + maDon).start(); // đặt tên thread để debug
```

#### 3.2 – Hủy hóa đơn bán lẻ: `javafx.concurrent.Task`

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L678-L698)

```java
javafx.concurrent.Task<Void> taskHuy = new javafx.concurrent.Task<>() {
    @Override
    protected Void call() throws Exception {
        orderService.huyHoaDonBanLe(maDon, lyDo, maNv);
        return null;
    }
};
// ...
new Thread(taskHuy, "thread-huy-hd-" + maDon).start();
```

#### 3.3 – Auto-refresh danh sách sản phẩm: `BaseController.batDauAutoRefresh()`

**File:** [DonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/DonHangViewFXMLController.java#L430-L441)

```java
batDauAutoRefresh(tabTaoDon, () -> {
    if (presenter != null) presenter.lamMoiDanhSachSanPham(); // gọi mỗi 10s
}, 10);
```

→ BaseController dùng `ScheduledExecutorService` hoặc `javafx.animation.Timeline` để auto-refresh định kỳ.

#### 3.4 – `Platform.runLater()` khi update UI từ thread phụ

**File:** [DonHangViewFXMLController.java](file:///D:/Clone/src/main/java/com/bakery/views/controllers/banhang/DonHangViewFXMLController.java#L213)

```java
// hienThiDanhSachSanPham() — gọi từ Presenter (có thể ở thread phụ)
Platform.runLater(this::capNhatComboLocDanhMuc);
```

**File:** [DonHangPresenter.java](file:///D:/Clone/src/main/java/com/bakery/presenters/banhang/DonHangPresenter.java#L382-L389)

```java
// moDialogTaoDon() — sau khi xảy ra lỗi từ DB thread
Platform.runLater(() -> {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle("⚠ Xưởng đã kín đơn");
    alert.showAndWait();
});
```

---

### Tại sao dùng đa luồng?

#### Lý do 1: Tránh UI bị đơ (Freeze)

JavaFX chạy toàn bộ UI trên **JavaFX Application Thread** (FX Thread). Nếu gọi DB trực tiếp trên FX Thread:
- Trong lúc chờ Oracle respond → FX Thread bị block → **màn hình đứng hình**, không render, không nhận click.
- Người dùng tưởng app bị crash → trải nghiệm tệ.

**Giải pháp:** Tác vụ nặng (gọi DB, network) → chạy trên **thread phụ**, kết quả trả về FX Thread qua `Platform.runLater()`.

#### Lý do 2: Tác vụ hủy đơn có thể chậm

`PROC_HUYDON_HOANCOC` phải:
- Kiểm tra trạng thái đơn
- Hoàn kho (`UPDATE SANPHAM`)
- Ghi phiếu hoàn tiền
- `COMMIT`

Nếu DB Oracle đang busy hoặc có lock, thao tác có thể mất 1-3 giây → **bắt buộc** phải chạy background.

#### Lý do 3: Rule kỹ thuật cốt lõi

> **Core rule:** "Tác vụ nặng → `Task`/`Service` JavaFX. Update UI từ thread phụ → `Platform.runLater()`."

#### Lý do 4: Thread naming giúp debug

```java
new Thread(taskHuy, "thread-huy-don-" + maDon).start();
// → Thread dump sẽ thấy: "thread-huy-don-42" thay vì "Thread-7"
```

---

### Quy tắc vàng về đa luồng trong dự án

| Quy tắc | Lý do |
|---|---|
| Tác vụ DB nặng → `javafx.concurrent.Task` | Không đơ UI |
| Update UI → chỉ trên FX Thread | JavaFX không thread-safe |
| Dùng `Platform.runLater()` cho UI callback | Bridge từ thread phụ về FX Thread |
| Capture biến trước lambda (xem `maCa`) | Tránh race condition |
| Đặt tên Thread | Dễ debug khi Thread dump |

---

### Minh họa luồng đa luồng khi hủy đơn

```
[FX Thread]                          [Thread phụ: thread-huy-don-42]
     │                                         │
     ├─ showCancelOrderDialog()                │
     │  (blocking, đúng — trên FX Thread)      │
     │                                         │
     ├─ new Thread(taskHuy).start() ──────────►│
     │                                         ├─ orderService.huyDonVaHoanCoc()
     │  [FX Thread tiếp tục render UI]         ├─   → DonHangDAO.huyDonVaHoanKho()
     │                                         ├─   → PROC_HUYDON_HOANCOC (Oracle)
     │                                         ├─ return (done)
     │◄── Platform.runLater(callback) ─────────┤
     │                                         │
     ├─ view.hienThiThongBaoTraCuu(...)        │
     └─ timKiemDonTheoDoi(...)                 │
```
