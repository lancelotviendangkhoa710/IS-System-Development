package com.bakery.services.banhang;

import com.bakery.model.dao.banhang.HoaDonDAO;
import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.banhang.HoaDonDTO;
import com.bakery.model.dto.banhang.YeuCauTaoDonHangDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;
import com.bakery.model.dao.hethong.PhieuThuChiDAO;
import com.bakery.utils.DBConnect;
import com.bakery.utils.SessionContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Service duy nhất chịu trách nhiệm xử lý Thanh toán.
 *
 * <p><b>Distributed Transaction:</b> Mỗi nghiệp vụ thanh toán dùng 1 Connection
 * duy nhất cho toàn bộ chuỗi DAO → đảm bảo tính ATOMICITY (ACID).
 * Nếu bất kỳ bước nào thất bại, toàn bộ giao dịch sẽ được ROLLBACK.</p>
 */
public class ThanhToanService {

    private final HoaDonDAO hoaDonDAO;
    private final QuanLyDonHangService quanLyDonHangService;
    private final PhieuThuChiDAO phieuThuChiDAO;

    public ThanhToanService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.quanLyDonHangService = new QuanLyDonHangService();
        this.phieuThuChiDAO = new PhieuThuChiDAO();
    }

    public ThanhToanService(HoaDonDAO hoaDonDAO, QuanLyDonHangService quanLyDonHangService, PhieuThuChiDAO phieuThuChiDAO) {
        this.hoaDonDAO = hoaDonDAO;
        this.quanLyDonHangService = quanLyDonHangService;
        this.phieuThuChiDAO = phieuThuChiDAO;
    }

    public double tinhTienHoaDon(YeuCauTaoDonHangDTO request) {
        if (request == null || request.getItems() == null) return 0.0;
        double subtotal = request.getItems().stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong())
                .sum();
        return subtotal * 1.085; // Mặc định tính thêm 8.5% (Thuế/Phụ phí)
    }

    /**
     * Tạo đơn hàng HOÀN_THÀNH và xuất hóa đơn bán lẻ ngay.
     * Dùng cho luồng thanh toán trực tiếp tại quầy.
     *
     * <p><b>ATOMICITY:</b> Toàn bộ luồng (taoDonHang → themHoaDon → thanhToan → phieuThu)
     * chạy trên 1 Connection duy nhất. Nếu bất kỳ bước nào thất bại → ROLLBACK toàn bộ.</p>
     */
    public HoaDonDTO thanhToanTrucTiep(YeuCauTaoDonHangDTO request, double soTienKhachDua) throws Exception {
        // 1. Tính tổng tiền từ giỏ hàng
        double tongTien = tinhTienHoaDon(request);

        // 2. Validate số tiền khách đưa
        if (soTienKhachDua < tongTien)
            throw new IllegalArgumentException("Số tiền khách đưa không đủ để thanh toán.");

        // 3. Đặt trạng thái HOÀN_THÀNH và ghi nhận toàn bộ tiền là đã cọc
        int maTrangThaiHoanThanh = layMaTrangThaiHoanThanh();
        request.setMaTrangThai(maTrangThaiHoanThanh);
        request.setTienDaCoc(tongTien);

        // 4. Thực hiện toàn bộ giao dịch trong 1 Connection — đảm bảo ATOMICITY
        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) throw new Exception("Không thể kết nối CSDL.");
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // 4a. Tạo đơn hàng (PROC_TAODONHANG tự COMMIT nội bộ — bỏ qua, ta sẽ COMMIT sau)
            int maDon = quanLyDonHangService.taoDonHang(conn, request);
            if (maDon <= 0) throw new Exception("Tạo đơn hàng thất bại.");

            // 4b. Tạo hóa đơn bán lẻ
            HoaDonDTO hd = taoHoaDonDTO(maDon, tongTien, "BAN_LE");
            int maHD = hoaDonDAO.themHoaDonMoi(conn, hd);
            if (maHD <= 0)
                throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + maDon + ".");

            // 4c. Chốt hóa đơn & cộng điểm thành viên
            hoaDonDAO.thanhToanVaThangHang(conn, maHD, request.getMaKH(), tongTien);

            // 4d. Tạo phiếu thu đi kèm (Sổ quỹ)
            taoPhieuThuChiTuHoaDon(conn, maHD, tongTien, "Thu tiền bán lẻ HD" + maHD);

            conn.commit(); // COMMIT DUY NHẤT — toàn bộ thành công

            // 5. Trả về hóa đơn đầy đủ để in (dùng connection riêng sau khi commit xong)
            HoaDonDTO hoaDonVuaTao = hoaDonDAO.layHoaDonTheoMa(maHD);
            if (hoaDonVuaTao == null)
                throw new Exception("Không thể tải thông tin hóa đơn vừa tạo.");
            return hoaDonVuaTao;

        } catch (Exception e) {
            // ROLLBACK TOÀN BỘ nếu bất kỳ bước nào thất bại
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    System.err.println("[ThanhToanService] Lỗi rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {
                    System.err.println("[ThanhToanService] Lỗi đóng connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Tạo và chốt hóa đơn khi đơn đặt hàng chuyển sang HOÀN_THÀNH.
     * Được gọi bởi DonHangService sau khi chuyển trạng thái.
     *
     * <p><b>ATOMICITY:</b> themHoaDon + thanhToan + phieuThu chạy trên 1 Connection.</p>
     *
     * @throws Exception nếu bất kỳ bước nào thất bại (không trả về null)
     */
    public HoaDonDTO chotHoaDonDatHang(DonDatHangDTO donHang) throws Exception {
        if (donHang == null)
            throw new IllegalArgumentException("Thông tin đơn hàng bị trống.");

        double tongTien = donHang.getTongTienHDBan() != null ? donHang.getTongTienHDBan().doubleValue() : 0.0;
        double tienCoc = donHang.getTienDaCoc() != null ? donHang.getTienDaCoc().doubleValue() : 0.0;
        double tongTienConLai = Math.max(0, tongTien - tienCoc);

        HoaDonDTO hd = taoHoaDonDTO(donHang.getMaDon(), tongTienConLai, "DAT_HANG");
        hd.setMaDon(donHang.getMaDon());

        Connection conn = null;
        try {
            conn = DBConnect.getConnection();
            if (conn == null) throw new Exception("Không thể kết nối CSDL.");
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            int maHD = hoaDonDAO.themHoaDonMoi(conn, hd);
            if (maHD <= 0)
                throw new Exception("Không thể tạo hóa đơn cho đơn hàng " + donHang.getMaDon() + ".");

            hoaDonDAO.thanhToanVaThangHang(conn, maHD, donHang.getMaKH(), tongTien);

            // Tạo phiếu thu đi kèm (Sổ quỹ)
            taoPhieuThuChiTuHoaDon(conn, maHD, tongTienConLai, "Thu tiền đơn hàng HD" + maHD);

            conn.commit(); // COMMIT DUY NHẤT

            HoaDonDTO ketQua = hoaDonDAO.layHoaDonTheoMa(maHD);
            if (ketQua == null)
                throw new Exception("Không thể tải hóa đơn vừa tạo MAHD=" + maHD + ".");
            return ketQua;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {
                    System.err.println("[ThanhToanService] Lỗi rollback chotHoaDon: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) {
                    System.err.println("[ThanhToanService] Lỗi đóng connection: " + ex.getMessage());
                }
            }
        }
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    /**
     * Tạo một đối tượng HoaDonDTO cơ bản.
     */
    public HoaDonDTO taoHoaDonDTO(int maDon, double soTien, String loaiHD) {
        HoaDonDTO hd = new HoaDonDTO();
        hd.setMaDon(maDon);
        hd.setMaCa(SessionContext.getInstance().getMaCa());
        hd.setThueVAT(8.5); // Mặc định 8.5%
        hd.setTongTienThanhToan(java.math.BigDecimal.valueOf(soTien));
        hd.setMaPTTT(1); // Mặc định Tiền mặt
        hd.setLoaiHD(loaiHD);
        return hd;
    }

    /**
     * Tạo phiếu thu chi liên kết với hóa đơn (trong distributed transaction).
     */
    private void taoPhieuThuChiTuHoaDon(Connection conn, int maHD, double soTien, String ghiChu) throws Exception {
        int maLoaiThu = phieuThuChiDAO.layMaLoaiTheoTen(conn, "Bán hàng");
        if (maLoaiThu == -1) maLoaiThu = 1; // Fallback

        PhieuThuChiDTO ptc = new PhieuThuChiDTO();
        ptc.setMaLoaiThuChi(maLoaiThu);
        ptc.setSoTien(BigDecimal.valueOf(soTien));
        ptc.setMaNV(SessionContext.getInstance().getMaNV());
        ptc.setMaHD(maHD);
        ptc.setMaCa(SessionContext.getInstance().getMaCa());
        ptc.setGhiChu(ghiChu);

        phieuThuChiDAO.taoPhieuThuChi(conn, ptc);
    }

    private int layMaTrangThaiHoanThanh() throws Exception {
        return quanLyDonHangService.layDanhSachTrangThaiDon().stream()
                .filter(tt -> "HOAN_THANH".equals(com.bakery.utils.StringUtil.chuanHoa(tt.getTenTrangThai())))
                .mapToInt(tt -> tt.getMaTrangThai())
                .findFirst()
                .orElseThrow(() -> new Exception("Không tìm thấy trạng thái HOÀN_THÀNH."));
    }
}
