# QUY TẮC ĐẶT TÊN FILE & CLASS (NAMING CONVENTIONS)

> Tất cả tên file Java trong dự án **ưu tiên đặt bằng tiếng Việt không dấu** để dễ nhận diện nghiệp vụ.
> Áp dụng cho toàn bộ tầng `views/`, `presenters/`, `services/`, `model/dao/`, `model/dto/`.

---

## 1. QUY TẮC ĐẶT TÊN THEO TẦNG

| Tầng | Hậu tố bắt buộc | Ví dụ |
|---|---|---|
| `views/` (JPanel màn hình) | `Panel` | `BanHangPanel.java` |
| `views/dialogs/` (JDialog popup) | `Dialog` | `DatBanhTuyChinhDialog.java` |
| `views/interfaces/` (Interface contract) | `IView` | `IBanHangView.java` |
| `presenters/` | `Presenter` | `BanHangPresenter.java` |
| `services/` | `Service` | `DonHangService.java` |
| `model/dao/` | `DAO` | `DonHangDAO.java` |
| `model/dto/` | `DTO` | `DonHangDTO.java` |

---

## 2. BẢNG ĐỐI CHIẾU TÊN FILE THEO TỪNG MÀN HÌNH

### Màn hình chính & đăng nhập

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Khung chính (sidebar + content) | `MainFrame.java` | — | — | — | — |
| Đăng nhập | `DangNhapPanel.java` | `DangNhapPresenter.java` | — | `NhanVienDAO.java` | `NhanVienDTO.java` |

---

### Phân hệ Bán hàng & Đơn hàng

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| POS — Bán hàng tại quầy | `BanHangPanel.java` | `BanHangPresenter.java` | `DonHangService.java` | `DonHangDAO.java` | `DonHangDTO.java` |
| Quản lý đơn hàng & KDS bếp | `QuanLyDonHangPanel.java` | `QuanLyDonHangPresenter.java` | `DonHangService.java` | `DonHangDAO.java` | `DonHangDTO.java` |
| Popup đặt bánh tùy chỉnh | `DatBanhTuyChinhDialog.java` | `DatBanhTuyChinhPresenter.java` | `DonHangService.java` | `DonHangDAO.java` | `ChiTietTuyChinhDTO.java` |
| Popup xác nhận thanh toán | `ThanhToanDialog.java` | `ThanhToanPresenter.java` | `ThanhToanService.java` | `HoaDonDAO.java` | `HoaDonDTO.java` |
| Popup xem trước hóa đơn 80mm | `XemHoaDonDialog.java` | — | — | — | — |
| Popup đối soát đóng ca | `DoiSoatDongCaDialog.java` | `DoiSoatPresenter.java` | `DoiSoatService.java` | `CaLamViecDAO.java` | `DoiSoatDTO.java` |

---

### Phân hệ Kho & Nguyên liệu

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Quản lý kho nguyên liệu | `QuanLyKhoPanel.java` | `QuanLyKhoPresenter.java` | `KhoService.java` | `NguyenLieuDAO.java` | `NguyenLieuDTO.java` |
| Popup lập phiếu nhập kho | `NhapKhoDialog.java` | `NhapKhoPresenter.java` | `KhoService.java` | `PhieuNhapKhoDAO.java` | `PhieuNhapKhoDTO.java` |
| Popup lập phiếu xuất kho | `XuatKhoDialog.java` | `XuatKhoPresenter.java` | `KhoService.java` | `PhieuXuatKhoDAO.java` | `PhieuXuatKhoDTO.java` |

---

### Phân hệ Khách hàng & Thành viên

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Danh sách khách hàng | `KhachHangPanel.java` | `KhachHangPresenter.java` | `KhachHangService.java` | `KhachHangDAO.java` | `KhachHangDTO.java` |

---

### Phân hệ Dashboard & Báo cáo

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Dashboard tổng quan & KPI | `DashboardPanel.java` | `DashboardPresenter.java` | `BaoCaoService.java` | `BaoCaoDAO.java` | `KpiDTO.java` |
| Báo cáo thống kê | `BaoCaoPanel.java` | `BaoCaoPresenter.java` | `BaoCaoService.java` | `BaoCaoDAO.java` | `BaoCaoDTO.java` |

---

### Phân hệ Quản lý Sản phẩm & Công thức

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Danh mục & Sản phẩm | `SanPhamPanel.java` | `SanPhamPresenter.java` | `SanPhamService.java` | `SanPhamDAO.java` | `SanPhamDTO.java` |

---

### Phân hệ Quản trị & Nhân sự *(chỉ Quản lý)*

| Màn hình | View | Presenter | Service | DAO | DTO |
|---|---|---|---|---|---|
| Quản lý nhân viên | `NhanVienPanel.java` | `NhanVienPresenter.java` | `NhanVienService.java` | `NhanVienDAO.java` | `NhanVienDTO.java` |

---

## 3. QUY TẮC ĐẶT TÊN BIẾN & PHƯƠNG THỨC

| Loại | Quy tắc | Ví dụ |
|---|---|---|
| Biến / hàm | `camelCase` | `layDanhSachDonHang()`, `maNhanVien` |
| Class / Interface | `PascalCase` | `DonHangService`, `IBanHangView` |
| Hằng số | `UPPER_SNAKE_CASE` | `MAX_DEPOSIT_PERCENT`, `VAT_RATE` |
| Tham số hàm | `camelCase`, tiếng Việt không dấu | `maDon`, `soTienCoc`, `ngayNhan` |

---

## 4. QUY TẮC ĐẶT TÊN PHƯƠNG THỨC THEO HÀNH ĐỘNG

| Hành động | Tiền tố | Ví dụ |
|---|---|---|
| Lấy danh sách | `layDanhSach...` | `layDanhSachSanPham()` |
| Lấy 1 bản ghi | `layTheoMa...` | `layTheoMaDonHang(int maDon)` |
| Thêm mới | `them...` | `themKhachHang(KhachHangDTO kh)` |
| Cập nhật | `capNhat...` | `capNhatTrangThai(int maDon, String trangThai)` |
| Xóa | `xoa...` | `xoaNhanVien(int maNV)` |
| Kiểm tra / validate | `kiemTra...` | `kiemTraTienCoc(BigDecimal tienCoc, BigDecimal tongTien)` |
| Tính toán | `tinh...` | `tinhGiaBanhTuyChinhDTO(ChiTietTuyChinhDTO ct)` |
| Hiển thị lên View | `hienThi...` | `hienThiDanhSachDon(List<DonHangDTO> ds)` |
| Xử lý sự kiện nút | `xuLyNhan...` | `xuLyNhanThanhToan()`, `xuLyNhanLuuDon()` |
