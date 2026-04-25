# TÀI LIỆU TỔNG HỢP Ý TƯỞNG ĐỒ ÁN: HỆ THỐNG QUẢN LÝ TIỆM BÁNH

> **Mục đích file này:** Cung cấp toàn bộ ngữ cảnh nghiệp vụ, cấu trúc dữ liệu, quy trình kỹ thuật và các giả định đã được xác lập cho đồ án môn học. AI trợ lý đọc file này để hiểu đúng ý tưởng trước khi hỗ trợ viết code, SQL, hoặc tài liệu.

---

## I. TỔNG QUAN DỰ ÁN

**Tên hệ thống:** H3K Bakery — Hệ thống Quản lý Tiệm Bánh  
**Công nghệ:** Java Swing (giao diện) + Oracle Database (PL/SQL)  
**Thư viện tích hợp:** JasperReports (in hóa đơn/phiếu hẹn), JFreeChart (biểu đồ), Apache POI (xuất Excel)  
**Kiến trúc:** MVP — Model / View / Service / Presenter / Utils  
**Phạm vi:** Ứng dụng desktop nội bộ cho tiệm bánh vừa và nhỏ

### Cấu trúc thư mục dự án

```
BakeryManagementSystem/
├── .git/                               # Thư mục ẩn do Git tự sinh ra để theo dõi lịch sử
├── .gitignore                          # RẤT QUAN TRỌNG: Khai báo các file KHÔNG đẩy lên GitHub
├── README.md                           # File giới thiệu dự án, hướng dẫn cài đặt cho người mới
├── pom.xml                             # File quản lý thư viện Maven (FlatLaf, ojdbc8, JasperReports, MigLayout,...)
│
├── 	database_scripts/                   # THƯ MỤC SQL CHÍNH: Quản lý toàn bộ cấu trúc Oracle
│   ├── 01_tables/                      # CREATE TABLE, CONSTRAINT. CẤM viết comment trong thư mục này
│   │   └── (vd: nhanvien.sql, ...)
│   │
│   ├── 02_data/                        # INSERT dữ liệu mẫu (mock data) để chạy demo
│   │   └── (vd: insert_danhmuc.sql, ...)
│   │
│   ├── 03_packages/                    # Package PL/SQL dùng chung toàn hệ thống
│   │   └── package_error_codes.sql     # PKG_ERROR_CODES: Toàn bộ mã lỗi chuẩn hóa
│   │
│   ├── 04_functions/                   # Các Function SQL (tiền tố file: func_)
│   │   └── (vd: func_calc_price.sql, ...)
│   │
│   ├── 05_procedures/                  # Các Stored Procedure (tiền tố file: proc_)
│   │   └── (vd: proc_place_order.sql, ...)
│   │
│   ├── 06_triggers/                    # Các Trigger (tiền tố file: trg_)
│   │   └── (vd: trg_check_stock.sql, ...)
│   │
│   └── 07_views/                       # Các View phục vụ JasperReports và báo cáo (tiền tố: VW_)
│       └── (vd: vw_hoadon_in.sql, vw_phieuhen.sql, ...)
│
└── src/
    └── main/
        ├── java/
        │   └── com/bakery/
        │       │
        │       ├── App.java                            # Điểm khởi chạy: cài FlatLaf theme, mở LoginPanel
        │       │
        │       ├── model/                              # Tầng dữ liệu
        │       │   ├── dto/                            # Class vật chứa dữ liệu (chỉ thuộc tính + Get/Set)
        │       │   │   └── (vd: SanPhamDTO.java, DonHangDTO.java, ...)
        │       │   │
        │       │   └── dao/                            # Gọi Stored Procedure / Truy vấn SQL thuần
        │       │       └── (vd: SanPhamDAO.java, DonHangDAO.java, ...)
        │       │
        │       ├── services/                           # Xử lý logic nghiệp vụ, điều phối DAO
        │       │   └── (vd: DonHangService.java, KhoService.java, ThanhToanService.java, ...)
        │       │
        │       ├── presenters/                         # Kết nối View ↔ Service (không chứa logic nghiệp vụ)
        │       │   └── (vd: POSPresenter.java, OrderPresenter.java, InventoryPresenter.java, ...)
        │       │
        │       ├── views/                              # Toàn bộ giao diện Swing
        │       │   ├── interfaces/                     # Contract (interface) giữa View và Presenter
        │       │   │   └── (vd: IPOSView.java, IOrderView.java, IInventoryView.java, ...)
        │       │   │
        │       │   ├── MainFrame.java                  # Cửa sổ chính: chứa Sidebar + Content Area
        │       │   ├── LoginPanel.java                 # Màn hình đăng nhập
        │       │   ├── DashboardPanel.java             # Màn hình tổng quan & KPI
        │       │   ├── POSPanel.java                   # Màn hình bán hàng tại quầy
        │       │   ├── OrderPanel.java                 # Màn hình quản lý đơn hàng & KDS bếp
        │       │   ├── InventoryPanel.java             # Màn hình kho nguyên liệu
        │       │   ├── CustomerPanel.java              # Màn hình khách hàng thành viên
        │       │   ├── ReportPanel.java                # Màn hình báo cáo thống kê
        │       │   │
        │       │   └── dialogs/                        # Các cửa sổ popup (JDialog)
        │       │       ├── CustomCakeDialog.java       # Popup đặt bánh tùy chỉnh (4 bước)
        │       │       ├── PaymentDialog.java          # Popup xác nhận thanh toán
        │       │       ├── ReconciliationDialog.java   # Popup đối soát đóng ca
        │       │       └── ReceiptDialog.java          # Popup xem trước hóa đơn nhiệt 80mm
        │       │
        │       └── utils/
        │           ├── DBConnect.java                  # Quản lý kết nối Oracle (Singleton)
        │           ├── AlertHelper.java                # Hiển thị JOptionPane chuẩn hóa toàn app
        │           ├── CurrencyFormatter.java          # Format tiền tệ VNĐ (1000 → 1.000đ)
        │           └── Config.java                     # Đọc cấu hình (chuỗi kết nối - bị Git ignore)
        │
        └── resources/
            ├── reports/                                # Template JasperReports (.jrxml)
            │   ├── HoaDon.jrxml                        # Hóa đơn thanh toán khổ 80mm
            │   └── PhieuHen.jrxml                      # Phiếu hẹn lấy bánh tùy chỉnh khổ 80mm
            │
            ├── fonts/                                  # Font chữ tùy chỉnh (nếu có)
            │
            └── images/
                └── logo.png
```
---

## II. VAI TRÒ NGƯỜI DÙNG & PHÂN QUYỀN

| Vai trò | Quyền truy cập chính |
|---|---|
| **Quản lý** | Toàn bộ hệ thống: báo cáo, nhân sự, kho, sản phẩm, đơn hàng, đối soát |
| **Thu ngân** | POS bán hàng, lập đơn tùy chỉnh, đối soát ca, quản lý đơn, khách hàng |
| **Thợ bếp** | Màn hình KDS, cập nhật trạng thái sản xuất, quản lý kho nguyên liệu |

**Cơ chế phân quyền động (Java Swing):** Sau khi đăng nhập, Java truy vấn danh sách `MACHUCNANG` của tài khoản. Với mỗi nút/menu không có quyền, dùng `btn.setVisible(false)` để ẩn hoàn toàn, hoặc `btn.setEnabled(false)` để làm xám.

---

## III. CÁC PHÂN HỆ CHỨC NĂNG (USE-CASE)

### Phân hệ I — Quản trị & Nhân sự
UC01 Đăng nhập/Đăng xuất | UC02 Đổi mật khẩu | UC03 Thêm/Xóa/Sửa nhân sự | UC04 Tra cứu nhân sự | UC05 Phân quyền tài khoản | UC06 Xem nhật ký hoạt động | UC07 Khôi phục dữ liệu (xóa mềm)

### Phân hệ II — Khách hàng & Thành viên
UC08 Thêm/Xóa/Sửa khách hàng | UC09 Tra cứu khách hàng | UC10 Lịch sử mua hàng | UC11 Cấu hình hạng thành viên

### Phân hệ III — Bán hàng & Đơn hàng
UC12 Lập hóa đơn bán lẻ (POS) | UC13 Lập đơn bánh tùy chỉnh | UC14 Cập nhật trạng thái đơn | UC15 Tra cứu danh sách đơn | UC16 Hủy đơn & hoàn cọc | UC17 Đối soát tiền cuối ca

### Phân hệ IV — Danh mục Sản phẩm & Công thức
UC18 CRUD danh mục sản phẩm | UC19 CRUD sản phẩm | UC20 Tra cứu sản phẩm | UC21 CRUD công thức | UC22 Tra cứu công thức | UC23 Tính số lượng bánh khả dụng từ kho | UC24 Cấu hình giới hạn nhận đơn ngày | UC25 CRUD nguyên liệu

### Phân hệ V — Quản lý Kho
UC26 Lập phiếu nhập kho | UC27 Lập phiếu xuất kho sản xuất | UC28 Lập phiếu xuất hủy | UC29 Cảnh báo tồn kho | UC30 Tra cứu thẻ kho | UC31 Truy vết nguồn gốc lô hàng | UC32 CRUD nhà cung cấp | UC33 Tra cứu nhà cung cấp

### Phân hệ VI — Thống kê & Báo cáo
UC34 Báo cáo lợi nhuận | UC35 Báo cáo doanh thu | UC36 Báo cáo tồn kho

---

## IV. CẤU TRÚC CƠ SỞ DỮ LIỆU (ORACLE)

### Nhóm 1 — Nhân sự & Khách hàng
| Bảng | Mô tả |
|---|---|
| `VAITRO` | Vai trò hệ thống (Quản lý, Thu ngân, Thợ bếp) |
| `NHANVIEN` | Hồ sơ nhân viên, liên kết vai trò |
| `CALAMVIEC` | Phiên làm việc, gắn với máy POS |
| `CHUCNANG` | Danh mục chức năng hệ thống |
| `VAITRO_CHUCNANG` | Bảng giao vai trò — chức năng (many-to-many) |
| `HANGTHANHVIEN` | Cấu hình hạng VIP (Đồng/Bạc/Vàng, điểm tối thiểu, % giảm) |
| `KHACHHANG` | Hồ sơ khách, điểm tích lũy, hạng thẻ |

### Nhóm 2 — Sản phẩm & Tùy chỉnh bánh
| Bảng | Mô tả |
|---|---|
| `DANHMUCSP` | Danh mục sản phẩm (Bánh Mì, Bánh Lạnh, ...) |
| `SANPHAM` | Sản phẩm: giá, tồn kho, thời gian chuẩn bị, cho phép tùy chỉnh |
| `KICHCOBANH` | Kích cỡ bánh + phụ phí (15cm, 20cm, 2 tầng) |
| `COTBANH` | Cốt bánh + phụ phí (Vani, Socola, Trà Xanh) |
| `NHANBANH` | Loại nhân + phụ phí (Mứt Dâu, Phô Mai) |
| `KIEUTRANGTRI` | Kiểu trang trí + phụ phí (Viết chữ, Vẽ 2D, Labubu) |

### Nhóm 3 — Kho & Công thức
| Bảng | Mô tả |
|---|---|
| `DONVITINH` | Đơn vị tính nguyên liệu |
| `NGUYENLIEU` | Nguyên liệu: tồn kho, giá vốn trung bình, mức an toàn |
| `CONGTHUC` | Định lượng nguyên liệu cho từng sản phẩm |
| `NHACUNGCAP` | Thông tin nhà cung cấp |
| `PHIEUNHAPKHO` | Chứng từ nhập kho |
| `CTPHIEUNHAP` | Chi tiết lô hàng: số lượng, đơn giá, HSD, mã vạch lô |
| `PHIEUXUATKHO` | Chứng từ xuất kho (sản xuất / xuất hủy) |
| `CTPHIEUXUAT_NL` | Chi tiết xuất nguyên liệu (trừ theo lô) |
| `CTPHIEUXUAT_TP` | Chi tiết xuất thành phẩm (bánh hỏng/hủy) |

### Nhóm 4 — Đặt hàng & Tiến độ
| Bảng | Mô tả |
|---|---|
| `TRANGTHAIDON` | Danh sách trạng thái (Mới đặt → Đã cọc → Đang SX → Chờ giao → Hoàn thành / Hủy) |
| `NANGLUCSANXUAT` | Giới hạn số bánh tối đa theo từng ngày sản xuất |
| `DONDATHANG` | Đơn hàng: khách, nhân viên lập, trạng thái, tiền cọc, hình thức nhận |
| `CTDONHANG` | Chi tiết đơn bánh bán sẵn |
| `CTDONTUYCHINH` | Chi tiết đơn bánh tùy chỉnh: kích cỡ, cốt, nhân, trang trí, lời chúc |
| `LICHSUDONHANG` | Audit log toàn bộ thay đổi trạng thái đơn |

### Nhóm 5 — Tài chính & Sổ quỹ
| Bảng | Mô tả |
|---|---|
| `PHUONGTHUCTT` | Phương thức thanh toán (Tiền mặt, Chuyển khoản, MoMo) |
| `HOADON` | Hóa đơn thanh toán: VAT, tổng tiền, liên kết ca làm việc |
| `LOAITHUCHI` | Danh mục loại thu/chi (Thu bán hàng, Thu cọc, Chi hoàn cọc, Chi nhập kho...) |
| `PHIEUTHUCHI` | Sổ quỹ: mọi dòng tiền vào/ra trong ca, liên kết với hóa đơn hoặc đơn hàng |
| `DOISOAT` | Kết quả đối soát cuối ca: tiền hệ thống, tiền đếm thực tế, chênh lệch, lý do |
| `CAUHINH_DIEM` | *(Bổ sung)* Tỷ lệ tích điểm và quy đổi điểm theo thời gian hiệu lực |

> **Lưu ý bảng PHIEUTHUCHI:** Có cả cột `MAHD` (liên kết hóa đơn) và `MADON` (liên kết đơn hàng). Cột `MADON` cần thiết cho trường hợp hoàn tiền cọc khi hủy đơn — xảy ra trước khi có hóa đơn.

---

## V. CÁC QUY TRÌNH NGHIỆP VỤ CHÍNH

### QT-01: Bán hàng tại quầy (POS)

```
1. Thu ngân quét/chọn sản phẩm → Java truy vấn GiaCoBan từ SANPHAM → hiển thị giỏ hàng
2. Bấm [Thanh Toán] → Java tính tổng (trừ TienDaCoc nếu có) + VAT → hiện Popup xác nhận
3. Chọn phương thức thanh toán → gọi PROC_ThanhToanVaThangHang
4. Procedure thực hiện trong 1 transaction:
   - INSERT HOADON
   - Cộng dồn DIEMTICHLUY cho khách (dùng tỷ lệ từ CAUHINH_DIEM)
   - Quét HANGTHANHVIEN → nâng hạng nếu đủ điểm
   - COMMIT
5. Java bắt MaHD vừa tạo → gọi JasperReports → hiện Preview hóa đơn 80mm
```

**Trigger bảo vệ:**
- `BEFORE INSERT` trên `CTDONHANG`: gọi `FUNC_TinhGiaBanhTuyChinh` để gán cứng `DonGia` và `DonGiaVon` — chặn gian lận từ UI
- `AFTER INSERT` trên `CTDONHANG`: trừ lùi `SANPHAM.SoLuongTon`
- `INSTEAD OF DELETE` trên `HOADON`: chặn xóa vật lý chứng từ kế toán

---

### QT-02: Lập đơn bánh tùy chỉnh & Thu tiền cọc

```
1. Thu ngân nhập thông tin khách + cấu hình bánh
   → Java liên tục tính tổng: GiaCoBan + PhuPhi(KichCo) + PhuPhi(Cot) + PhuPhi(Nhan) + PhuPhi(TrangTri)
   → Hiển thị Tổng tiền realtime

2. [Validate - Fail-Fast trên Java Swing]
   - Bọc TextField trong try-catch(NumberFormatException) → chặn nhập chữ
   - Quy tắc 50%: TienCocToiThieu = TongTien * 0.5
   - Nếu SoTienCoc < TienCocToiThieu → JOptionPane cảnh báo, chặn luồng
   - Nếu chọn "Giao tận nơi" → bắt buộc nhập địa chỉ

3. [Xử lý bất đồng bộ - SwingWorker]
   - Đóng gói lệnh gọi Procedure vào SwingWorker ngầm
   - UI hiện GlassPane loading "Đang xử lý..."
   - Luồng chính (EDT) không bị đơ

4. PROC_NhanTienCoc (p_MaDon, p_SoTienCoc, p_MaCa, p_MaNV):
   - Chống xung đột: UPDATE TienDaCoc = NVL(TienDaCoc,0) + p_SoTienCoc (cộng dồn, KHÔNG gán thẳng)
   - Cập nhật MaTrangThai = 'Đã cọc' trong cùng 1 câu UPDATE
   - INSERT PHIEUTHUCHI (loại 'Thu - Tiền cọc') → liên kết sổ quỹ
   - INSERT LICHSUDONHANG → audit trail
   - COMMIT

5. Khi SwingWorker báo thành công → gọi JasperReports in Phiếu Hẹn 80mm
   (Data từ VIEW VW_PhieuHenLayBanh: tên khách, mã đơn, tiền đã cọc, tiền cần thu thêm, giờ hẹn)
```

**Trigger bổ sung:**
- `BEFORE INSERT` trên `CTDONTUYCHINH`: tự động lấy `ThoiGianChuanBi` từ `SANPHAM` nếu giá trị truyền xuống rỗng
- `AFTER INSERT` trên `CTDONTUYCHINH`: kiểm tra năng lực sản xuất (xem QT-04)

---

### QT-03: Quy trình sản xuất bánh (KDS)

```
1. Màn hình bếp (KDS):
   - Hiển thị lưới đơn hàng, ưu tiên theo NgaySanXuatThucTe = NgayGioNhanBanh - ThoiGianChuanBi
   - Đơn khẩn cấp (urgent) viền đỏ, ưu tiên trên cùng

2. Thợ bếp bấm [Bắt đầu làm]:
   → Java gọi PROC_ChuyenTrangThaiDon: "Mới đặt"/"Đã cọc" → "Đang sản xuất"

3. Xuất kho nguyên liệu (PROC_XuatKhoSanXuat):
   - Pessimistic Locking (UPDLOCK) khóa dòng nguyên liệu
   - Kiểm tra đủ hàng không, nếu đủ → tạo PHIEUXUATKHO
   - TRIGGER AFTER INSERT trên CTPHIEUXUAT_NL → trừ tồn kho nguyên liệu
   - Thuật toán FEFO: FUNC_XacDinhPhieuNhapFEFO dùng ORDER BY HanSuDung ASC

4. Nướng xong → đóng gói:
   - Java tính HSD: LocalDate.now() + ThoiGianBaoQuan → in tem JasperReports
   - Nếu thiếu nguyên liệu: Toast Message cảnh báo màu đỏ, trượt lên góc phải 3 giây

5. Thợ bếp bấm [Hoàn thành]:
   → Java kiểm tra HinhThucNhan:
     - "Giao tận nơi" → trạng thái "Chờ giao"
     - "Tại quầy"     → trạng thái "Chờ khách lấy"
```

---

### QT-04: Kiểm tra năng lực sản xuất

```
TRIGGER AFTER INSERT trên CTDONTUYCHINH:
  NgaySanXuatThucTe = NgayGioNhanBanh - ThoiGianChuanBi
  
  SELECT SUM(SOLUONG) INTO v_TongBanhTrongNgay
  FROM CTDONTUYCHINH ct JOIN DONDATHANG d ON ct.MADON = d.MADON
  WHERE TRUNC(d.NGAYGIONHANBANH - ct.THOIGIANCHUANBI / 24) = TRUNC(NgaySanXuatThucTe)
  AND MaTrangThai NOT IN ('Hủy');
  
  IF v_TongBanhTrongNgay > GIOIHANSOBANH THEN
    RAISE_APPLICATION_ERROR(-20001, 'Vượt năng lực sản xuất ngày này!');
  END IF;
```

> **Giả định đã xác lập:** Năng lực đo bằng số lượng bánh (không phải giờ-công). Mỗi đơn vị bánh được coi là tương đương nhau. Hướng mở rộng: thay `GIOIHANSOBANH` bằng `GIOIHANTONGGIO` và SUM theo `THOIGIANCHUANBI * SOLUONG`.

---

### QT-05: Hủy đơn & Hoàn tiền cọc

```
PROC_HuyDonVaHoanKho (p_MaDon, p_MaNV, p_MaCa, p_LyDoHuy):

  1. Lấy: TienDaCoc, MaTrangThai, TongTienHDBan từ DONDATHANG

  2. Tính tiền hoàn theo trạng thái hiện tại:
     IF TrangThai IN ('Mới đặt', 'Đã cọc'):
       SoTienHoan = TienDaCoc  -- Hoàn 100%
     ELSE:  -- Đã sản xuất xong
       PhiThietHai = TongTienHDBan * 0.30  -- Giữ 30%
       SoTienHoan  = GREATEST(TienDaCoc - PhiThietHai, 0)

  3. Hoàn kho bánh:
     UPDATE SANPHAM SET SoLuongTon = SoLuongTon + SoLuong
     (dựa vào CTDONHANG và CTDONTUYCHINH của đơn)

  4. Nếu SoTienHoan > 0:
     INSERT PHIEUTHUCHI (loại 'Chi - Hoàn cọc', MADON = p_MaDon)
     -- Dùng MADON (không phải MAHD) vì chưa có hóa đơn

  5. Cập nhật trạng thái đơn → 'Hủy'
  6. INSERT LICHSUDONHANG
  7. COMMIT
```

> **Lưu ý schema:** Bảng `PHIEUTHUCHI` có cột `MADON` (bổ sung ngoài `MAHD`) để liên kết phiếu hoàn cọc với đơn hàng thay vì hóa đơn.

---

### QT-06: Đối soát & Đóng ca

```
FUNC_TinhTienMatLyTuong (p_MaCa, p_TienKhaiBaoDauCa):
  = TienKhaiBaoDauCa
  + SUM(HoaDon tiền mặt trong ca)
  + SUM(PhieuThuChi loại Thu trong ca)
  - SUM(PhieuThuChi loại Chi trong ca)
  → Trả về 1 số, Java lưu vào biến tạm (KHÔNG hiển thị lên màn hình)

Logic đối soát mù (Java Swing):
  - Thu ngân tự đếm tiền → nhập TienThucTeDem
  - Java tính: ChenhLech = TienThucTeDem - TongTienHeThong
  - Nếu ChenhLech = 0  → cho phép đóng ca ngay
  - Nếu ChenhLech ≠ 0  → khóa nút, bắt buộc nhập LyDoChenhLech

PROC_DongCaDoiSoat (p_MaCa, p_TienThucTeDem, p_ChenhLech, p_LyDo):
  - INSERT DOISOAT (gọi lại FUNC_TinhTienMatLyTuong lần 2 để cập nhật bill mới nhất)
  - UPDATE CALAMVIEC: ThoiGianDongCa = SYSDATE, TrangThai = 'Đã đóng'
  - COMMIT
```

---

### QT-07: Nhập kho nguyên liệu

```
PROC_NhapKho (danh sách lô hàng):
  - INSERT PHIEUNHAPKHO
  - Vòng lặp INSERT CTPHIEUNHAP từng dòng
  (Trigger tự động xử lý phần còn lại)

Chuỗi Trigger:
  T1: BEFORE INSERT trên CTPHIEUNHAP
      → Nếu DatChuanVSATTP = 0: RAISE_APPLICATION_ERROR → Rollback

  T2: AFTER INSERT/UPDATE/DELETE trên CTPHIEUNHAP
      → SUM cộng dồn TongTienNhap vào PHIEUNHAPKHO

  T3: AFTER INSERT trên CTPHIEUNHAP
      → Cập nhật SoLuongTonTong += SoLuong
      → Tính giá vốn trung bình:
         GiaVonMoi = [(TonCu * GiaCu) + (SoLuongNhap * DonGia)] / TongSLMoi
      → UPDATE cả 2 giá trị vào NGUYENLIEU trong 1 câu lệnh

  T4: AFTER UPDATE trên CTPHIEUNHAP (cột HanSuDung)
      → Nếu HanSuDung_moi > HanSuDung_cu: RAISE_APPLICATION_ERROR → chống gian lận
```

---

### QT-08: Quy trình chuyển đổi trạng thái đơn hàng

```
PROC_ChuyenTrangThaiDon (p_MaDon, p_TrangThaiMoi, p_MaNV):

Luồng trạng thái hợp lệ:
  NULL         → Mới đặt / Đã cọc     (khi tạo đơn)
  Mới đặt      → Đã cọc               (khi thu thêm cọc)
  Mới đặt      → Đang sản xuất        (bếp bấm bắt đầu)
  Đã cọc       → Đang sản xuất        (bếp bấm bắt đầu)
  Đang SX      → Chờ giao             (bếp xong, giao đi)
  Đang SX      → Chờ khách lấy        (bếp xong, lấy tại quầy)
  Chờ giao     → Hoàn thành           (đã giao)
  Chờ KH lấy  → Hoàn thành           (KH đã lấy)
  Mọi TT       → Hủy                  (quản lý/thu ngân hủy)

Mỗi lần gọi:
  - UPDATE DONDATHANG SET MaTrangThai = p_TrangThaiMoi
  - INSERT LICHSUDONHANG (audit trail)
  - COMMIT
```

---

## VI. CÁC HÀM (FUNCTION) SQL QUAN TRỌNG

| Tên Function | Đầu vào | Đầu ra | Mục đích |
|---|---|---|---|
| `FUNC_TinhGiaBanhTuyChinh` | MaKC, MaCot, MaNhan, MaTrangTri, MaSP | Tổng giá bán, giá vốn | Tính giá bánh custom realtime và trong Trigger |
| `FUNC_TinhDiemKhaDung` | NgaySanXuat | Số bánh còn nhận được | Kiểm tra năng lực trước khi lập đơn |
| `FUNC_XacDinhPhieuNhapFEFO` | MaNL | MaLo cần xuất trước | Tìm lô nguyên liệu gần hết HSD nhất |
| `FUNC_TinhGiaVonDong` | MaSP, SoLuong | Giá vốn tại thời điểm SX | Chốt giá vốn thành phẩm khi nướng xong |
| `FUNC_ChuyenDoiTyLeCongThuc` | MaSP, SoLuong | Map<MaNL, SoLuongCan> | Scale công thức theo số lượng cần làm |
| `FUNC_SoLuongKhaDung` | MaSP | Số bánh có thể làm | Dự báo SX dựa trên tồn kho hiện tại |
| `FUNC_TinhLoiNhuanGop` | TuNgay, DenNgay | Tổng lợi nhuận | (Giá bán - Giá vốn đóng băng) × SL |
| `FUNC_KiemTraTonKhoToiThieu` | — | List nguyên liệu sắp hết | Cảnh báo dashboard |
| `FUNC_TopSanPhamBanChay` | TuNgay, DenNgay, Top N | List SP + SL bán | Biểu đồ cột dashboard |
| `FUNC_DoanhThuTheoPhuongThucTT` | TuNgay, DenNgay | Map<PhuongThuc, SoTien> | Biểu đồ pie dashboard |
| `FUNC_TinhTienMatLyTuong` | MaCa, TienKhaiBaoDauCa | Số tiền mặt lý tưởng trong két | Đối soát ca |

---

## VII. TÍCH HỢP IN ẤN — JASPERREPORTS

### Hóa đơn thanh toán (VW_ChiTietHoaDonIn)
JOIN: `HOADON` + `CALAMVIEC` + `NHANVIEN` + `CTDONHANG` + `SANPHAM`  
UNION ALL thêm `CTDONTUYCHINH` nếu có bánh tùy chỉnh  
Kết quả: bảng phẳng đủ dữ liệu cho 1 tờ bill  
Template: `HoaDon.jrxml` — khổ 80mm, chiều cao tự động giãn  
Gọi: `JasperViewer.viewReport(print, false)` — hiện Preview PDF trên màn hình

### Phiếu hẹn lấy bánh (VW_PhieuHenLayBanh)
Cột quan trọng: Mã đơn, Tên khách, SĐT, Tiền đã cọc, Tiền cần thu thêm, **NgayGioNhanBanh** (in đậm)  
Template: `PhieuHen.jrxml` — khổ 80mm

### Tem hạn sử dụng
Java tính: `HSD = LocalDate.now() + ThoiGianBaoQuan`  
In ngay sau khi thợ bếp bấm hoàn thành mẻ bánh

---

## VIII. XỬ LÝ ĐA LUỒNG & CHỐNG XUNG ĐỘT (JAVA)

### Bất đồng bộ UI (SwingWorker)
Mọi thao tác gọi SQL nặng (thanh toán, xuất kho, đối soát) bắt buộc bọc trong `SwingWorker<Void, Void>`:
- UI hiện `GlassPane` phủ mờ + vòng loading "Đang xử lý..."
- Chạy ngầm trong `doInBackground()` — không chặn Event Dispatch Thread (EDT)
- Chỉ khi `done()` kích hoạt mới tắt loading và hiện `JOptionPane` thông báo kết quả

### Optimistic Locking (Cập nhật sản phẩm/công thức)
Java gửi kèm `currentVersion`. Nếu `executeUpdate()` trả về 0 → `JOptionPane`: *"Dữ liệu đã bị thay đổi bởi người khác. Vui lòng tải lại!"*

### Pessimistic Locking (Xuất kho sản xuất)
`PROC_XuatKhoSanXuat` dùng `SELECT ... FOR UPDATE` khóa dòng nguyên liệu trước khi kiểm tra và trừ số lượng.

### Chống xung đột tiền cọc
`PROC_NhanTienCoc` dùng `NVL(TienDaCoc, 0) + p_SoTienCoc` — Oracle xếp hàng các lệnh UPDATE, đảm bảo 2 nhân viên cùng thu cọc 1 đơn không bị ghi đè.

---

## IX. GIẢ ĐỊNH ĐÃ ĐƯỢC XÁC LẬP (GHI VÀO BÁO CÁO)

### GA-01: Tích lũy điểm thành viên
Tỷ lệ: **1 điểm = 1.000đ** thanh toán (lưu trong bảng `CAUHINH_DIEM`, có cột `HIEULUC_TU` để thay đổi theo chính sách). `PROC_ThanhToanVaThangHang` truy vấn tỷ lệ hiện hành thay vì hard-code.

### GA-02: Năng lực sản xuất
Đo bằng **số lượng bánh** thay vì giờ-công. Giả định mỗi đơn vị bánh tương đương nhau trong ngày. `GIOIHANSOBANH` do quản lý cài đặt thủ công. Hướng mở rộng: dùng `GIOIHANTONGGIO` và SUM(`THOIGIANCHUANBI × SOLUONG`).

### GA-03: Phí thiệt hại khi hủy muộn
Khi khách hủy sau khi bánh đã hoàn thành sản xuất: tiệm giữ lại **30% tổng giá trị đơn**. Phần còn lại của tiền cọc được hoàn trả và ghi tự động vào `PHIEUTHUCHI`.

### GA-04: Giao hàng bên thứ ba
Hệ thống **không tích hợp API shipper**. Đặt shipper thực hiện thủ công ngoài hệ thống. Phí ship do khách thanh toán trực tiếp cho đơn vị vận chuyển, không đi qua sổ quỹ tiệm.

### GA-05: Không in hóa đơn vật lý
Hệ thống xuất bill dưới dạng **Preview PDF (JasperViewer)** trên màn hình — không cần máy in phần cứng, phục vụ mục đích demo đồ án.

---

## X. DANH SÁCH PROCEDURE & TRIGGER THAM CHIẾU

### Procedures
| Tên | Chức năng |
|---|---|
| `PROC_ThanhToanVaThangHang` | Chốt hóa đơn, cộng điểm, nâng hạng thẻ VIP |
| `PROC_NhanTienCoc` | Thu tiền cọc đơn tùy chỉnh, phát sinh phiếu thu |
| `PROC_XuatKhoSanXuat` | Xuất kho nguyên liệu theo FEFO, có Pessimistic Locking |
| `PROC_XuatHuyBanh` | Xuất hủy thành phẩm hỏng/hết HSD |
| `PROC_NhapKho` | Tạo phiếu nhập kho và chi tiết lô hàng |
| `PROC_KiemKeKho` | Kiểm kê định kỳ, tự phát sinh phiếu bù trừ |
| `PROC_HuyPhieuNhapKho` | Trả hàng NCC, chặn nếu đã lấy dùng một phần |
| `PROC_HuyDonVaHoanKho` | Hủy đơn, hoàn kho bánh, hoàn tiền cọc, audit |
| `PROC_KhoiPhucDuLieu` | Khôi phục bản ghi xóa mềm (Deleted_At = NULL) |
| `PROC_DongCaDoiSoat` | Ghi kết quả đối soát, đóng ca làm việc |
| `PROC_ChuyenTrangThaiDon` | Cập nhật trạng thái đơn + ghi LICHSUDONHANG |

### Triggers quan trọng
| Tên | Bảng | Thời điểm | Chức năng |
|---|---|---|---|
| TRG_GanGiaChiTietDon | CTDONHANG | BEFORE INSERT | Gán cứng DonGia, DonGiaVon từ Function |
| TRG_GanGiaTuyChinhDon | CTDONTUYCHINH | BEFORE INSERT | Gán giá + tự điền ThoiGianChuanBi |
| TRG_KiemTraNangLucSX | CTDONTUYCHINH | AFTER INSERT | Chặn nếu vượt giới hạn ngày SX |
| TRG_TruKhoThanhPham | CTDONHANG | AFTER INSERT | Trừ SANPHAM.SoLuongTon |
| TRG_CamXoaHoaDon | HOADON | INSTEAD OF DELETE | Chặn xóa vật lý chứng từ kế toán |
| TRG_KiemTraVSATTP | CTPHIEUNHAP | BEFORE INSERT | Chặn nguyên liệu chưa đạt chuẩn |
| TRG_TongTienPhieuNhap | CTPHIEUNHAP | AFTER I/U/D | Cập nhật TongTienNhap tự động |
| TRG_CapNhatKhoVaGiaVon | CTPHIEUNHAP | AFTER INSERT | Cộng tồn kho + tính giá vốn TB |
| TRG_ChongSuaHSD | CTPHIEUNHAP | AFTER UPDATE | Chặn dời HSD xa hơn thực tế |
| TRG_TruNguyenLieu | CTPHIEUXUAT_NL | AFTER INSERT | Trừ tồn kho nguyên liệu |
| TRG_GanGiaVonXuatHuy | CTPHIEUXUAT_TP | BEFORE INSERT | Gán giá vốn cho phiếu xuất hủy |

---

## XI. VIEWS PHỤC VỤ JASPERREPORTS

| Tên View | Mục đích |
|---|---|
| `VW_ChiTietHoaDonIn` | Dữ liệu in hóa đơn thanh toán (JOIN + UNION ALL) |
| `VW_PhieuHenLayBanh` | Dữ liệu in phiếu hẹn đặt bánh tùy chỉnh |

---

*Tài liệu được tổng hợp từ: Quy trình nghiệp vụ, Database Schema, Danh sách Use-case và Đề xuất bổ sung của nhóm.*
