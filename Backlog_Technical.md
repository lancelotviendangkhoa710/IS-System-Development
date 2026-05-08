# Backlog Technical & Review CUD

## 1. Kết quả kiểm tra CUD Operations (Create - Update - Delete)
Qua việc rà soát kiến trúc Java gọi xuống CSDL (Oracle DB) thông qua JDBC `CallableStatement` và `PreparedStatement`:
- **Luồng dữ liệu CUD:** Các thao tác Thêm, Sửa, Xóa đều được module hóa thành các Stored Procedure ở database (vd: `PROC_THEM_...`, `PROC_SUA_...`, `PROC_XOA_...`). Việc này đảm bảo logic nghiệp vụ (Business Logic) và tính nhất quán dữ liệu (Data Integrity) được xử lý tập trung dưới DB. Lớp Java DAO gọi đúng Procedure với tham số (`?`) được bind an toàn, chống SQL Injection.
- **Xử lý Exception (Exception Handling):** 
  - Tại `BaseDAO.java`, hàm `handleException()` đang gom các `SQLException` và truyền thông báo lỗi lên tầng trên. 
  - Các Trigger và Procedure dưới DB dùng `RAISE_APPLICATION_ERROR` (Mã ORA-20000 trở lên) để báo lỗi vi phạm Ràng buộc (Constraint) hoặc Hết hàng. 
  - **Lưu ý Cải thiện (Status/Note):** Hiện tại thông báo lỗi từ Oracle trả về sẽ dính cả chuỗi mã lỗi (ví dụ: `ORA-20001: Sản phẩm không đủ tồn kho...`). Nên bổ sung thêm 1 hàm regex trong `BaseDAO` để cắt bỏ tiền tố `ORA-XXXXX:`, giúp UI hiển thị thông báo lỗi thân thiện hơn với người dùng cuối. 
  - Vấn đề Leak Connection: Java DAO đã áp dụng tốt chuẩn `try-with-resources` để tự động đóng `Connection`, `PreparedStatement` và `ResultSet`. Không ghi nhận rủi ro rò rỉ kết nối.

---

## 2. Bảng ánh xạ Database Objects & Java Mapping

### A. Thủ tục lưu trữ (Stored Procedures)
Xử lý các tác vụ ghi/xóa/cập nhật dữ liệu phức tạp.

| Database Object (Oracle) | Chức năng Kỹ thuật | Lớp & Phương thức gọi (Java) |
| --- | --- | --- |
| `PROC_THEM_SANPHAM`, `PROC_SUA_SANPHAM`, `PROC_XOA_SANPHAM` | Quản lý vòng đời Sản phẩm (CUD). Kèm xử lý Soft Delete qua `MANX`. | `SanPhamDAO.java` (`themSanPham`, `capNhatSanPham`, `xoaSanPham`) |
| `PROC_THEM_NHANVIEN`, `PROC_GAN_VAITRO_NHANVIEN` | Tạo nhân viên mới kèm phân quyền N-N (nhiều vai trò). | `NhanVienDAO.java` (`themNhanVien`, `capNhatVaiTroChoNhanVien`) |
| `PROC_THEM_KHACHHANG`, `PROC_SUA_KHACHHANG`, `PROC_XOA_KHACHHANG` | Quản lý thẻ khách hàng và điểm tích luỹ. | `KhachHangDAO.java` (`themKhachHang`, `suaKhachHang`, `xoaKhachHang`) |
| `PROC_TAODONHANG`, `PROC_CHUYENTRANGTHAIDON`, `PROC_HUYDON_HOANCOC` | Workflow xử lý đơn hàng POS. Tự động tính cọc, luân chuyển trạng thái, hoàn cọc. | `DonHangDAO.java` (`submitNewOrder`, `chuyenTrangThaiDon`, `huyDonVaHoanCoc`) |
| `PROC_TAOPHIEUNHAPKHO`, `PROC_HUYPHIEUNHAPKHO` | Xử lý nhập kho nguyên liệu (FEFO - lô date). | `PhieuNhapKhoDAO.java` (`taoPhieuNhapKho`, `huyPhieuNhapKho`) |
| `PROC_XUATHUYBANH` | Tự động xuất kho huỷ các lô thành phẩm hết date. | `PhieuXuatKhoDAO.java` (`xuatHuyThanhPham`) |
| `PROC_THANHTOANVATHANGHANG` | Hoàn thành thanh toán hóa đơn POS & tính dặm thăng hạng KH. | `HoaDonDAO.java` (`hoanThanhThanhToan`) |
| `PROC_DONGCADOISOAT` | Chốt sổ quỹ cuối ngày, chênh lệch thực tế & hệ thống. | `DoiSoatDAO.java` (`dongCaVaDoiSoat`) |

### B. Hàm (Functions)
Tính toán và trả về kết quả số liệu tức thời.

| Database Object (Oracle) | Chức năng Kỹ thuật | Lớp & Phương thức gọi (Java) |
| --- | --- | --- |
| `FUNC_GIABANHTUYCHINH` | Tự động cộng dồn giá base + phụ phí (Size, Cốt, Nhân, Trang trí). | `SanPhamDAO.java` (`tinhGiaBanhTuyChinh`) |
| `FUNC_TINHTIENMATLYTUONG` | Truy xuất công thức: Tiền đầu ca + Tổng thu TM - Tổng chi TM. | `DoiSoatDAO.java` (`layTienMatLyTuongDauCa`) |
| `FUNC_FEFO` / `FUNC_STOCK_ALERT` | Tìm kiếm lô nguyên liệu cận date nhất để xuất trước / Cảnh báo tồn. | *[Ẩn dưới DB]* (Được gọi gián tiếp thông qua các view hoặc trigger) |

### C. Triggers
Đảm bảo tính toàn vẹn và Tự động hóa ở cấp độ thấp (Tầng Java không cần can thiệp).

| Database Object (Oracle) | Chức năng Kỹ thuật | Trạng thái (Status/Ghi chú) |
| --- | --- | --- |
| `trg_assign_price_custom` / `standard` | Tự động áp giá tại thời điểm chốt đơn để tránh trượt giá tương lai. | Hoạt động ngầm ổn định khi `DonHangDAO` thêm CTHD. |
| `trg_update_inventory_stock` | Tự động trừ/cộng tồn kho Sản phẩm/Nguyên liệu tương ứng. | Hoạt động ngầm. Không cần Java DAO can thiệp thủ công. |
| `trg_control_capacity_custom` | Cấm nhận quá số lượng bánh giới hạn thiết lập trong `NANGLUCSANXUAT`. | Sẽ ném Exception cấm nhập, Java DAO catch và báo về UI. |
| `trg_prohibit_delete_invoice` | Ngăn cấm xoá cứng (hard-delete) hóa đơn tài chính (Tuân thủ kiểm toán). | Ràng buộc bảo mật. Đã Active. |

---

## 3. Tổng kết Tình trạng (Status & Notes)
- **Tình trạng:** Toàn bộ chức năng cốt lõi (Core Domain) đã được chuyển từ mô hình Mock Database lên kiến trúc Service-DAO-DB hoàn chỉnh. Các procedure Oracle được mapping 1-1 với lớp Repository.
- **Hiệu suất (Performance):** 
  - Các Trigger xử lý tồn kho phức tạp (trừ FEFO) thực hiện ở cấp CSDL cho hiệu suất cao hơn nhiều so với việc tải danh sách Object lên RAM ở tầng Java để tính toán (tránh lỗi N+1 Query).
  - Tối ưu I/O bằng cách dùng `CallableStatement` gom logic nhiều bảng vào 1 vòng Call duy nhất (Vd: `PROC_TAODONHANG` insert cả Hóa đơn + Chi tiết).
- **Lưu ý bảo trì (Maintainability):** Mọi sự thay đổi về công thức giá bánh hay luồng duyệt đơn (workflow) nay chỉ cần cập nhật ở cấp độ DB Procedure/Function (không cần recompile hoặc deploy lại JAR Java). Đây là điểm sáng của kiến trúc này.
