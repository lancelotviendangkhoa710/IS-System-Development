package com.bakery.services;

import com.bakery.model.dao.DonDatHangDAO;
import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.CTDonTuyChinhDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.TrangThaiDonDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.model.dto.YeuCauTaoDonHangDTO;

import java.sql.SQLException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service duy nhất chịu trách nhiệm quản lý vòng đời Đơn hàng:
 * Tạo mới, chuyển trạng thái, hủy đơn, tra cứu thông tin đơn.
 * (Áp dụng SRP – Single Responsibility Principle)
 */
public class DonHangService {

    private final DonDatHangDAO donDatHangDAO;

    public DonHangService() {
        this.donDatHangDAO = new DonDatHangDAO();
    }

    public DonHangService(DonDatHangDAO donDatHangDAO) {
        this.donDatHangDAO = donDatHangDAO;
    }

    // =========================================================
    // 1. TẠO ĐƠN HÀNG MỚI
    // =========================================================

    /**
     * Validate, map DTO rồi gọi DAO tạo đơn hàng.
     * Trả về mã đơn mới được sinh ra bởi DB.
     */
    public int taoDonHang(YeuCauTaoDonHangDTO request) throws Exception {
        kiemTraYeuCauDonHang(request);

        int maTrangThai = request.getMaTrangThai();
        if (maTrangThai <= 0) {
            maTrangThai = request.getTienDaCoc() > 0
                    ? layMaTrangThaiDaCoc()
                    : layMaTrangThaiMoiDat();
        }

        DonDatHangDTO donDatHang = chuyenSangDonDatHangDTO(request, maTrangThai);
        List<CTDonHangDTO> dsCtDonHang = new ArrayList<>();
        List<CTDonTuyChinhDTO> dsCtTuyChinh = new ArrayList<>();
        chuyenDoiChiTietDonHang(request.getItems(), dsCtDonHang, dsCtTuyChinh);

        try {
            int maDonMoi = donDatHangDAO.taoDonHang(donDatHang, dsCtDonHang, dsCtTuyChinh);
            boolean tonTai = donDatHangDAO.tonTaiDonHang(maDonMoi);
            if (!tonTai) {
                throw new Exception("Tạo đơn thất bại: không tìm thấy đơn hàng vừa tạo trong CSDL.");
            }
            return maDonMoi;
        } catch (SQLException e) {
            throw new Exception("Không tạo được đơn hàng: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // 2. CHUYỂN TRẠNG THÁI ĐƠN HÀNG
    // =========================================================

    /**
     * Chuyển trạng thái đơn hàng sau khi validate logic nghiệp vụ.
     * Nếu chuyển sang HOÀN_THÀNH sẽ trả về đơn hàng để tầng trên tạo hóa đơn.
     */
    public DonDatHangDTO chuyenTrangThaiDon(int maDon, int maTrangThaiMoi, int maNvCapNhat,
            Integer hinhThucNhan, String tenTrangThaiHienTai, String tenTrangThaiMoi) throws Exception {
        if (maDon <= 0)
            throw new IllegalArgumentException("Mã đơn sai định dạng.");
        if (maTrangThaiMoi <= 0)
            throw new IllegalArgumentException("Mã trạng thái mới sai định dạng.");
        if (maNvCapNhat <= 0)
            throw new IllegalArgumentException("Mã nhân viên cập nhật sai định dạng.");
        if (tenTrangThaiHienTai == null || tenTrangThaiHienTai.trim().isEmpty())
            throw new IllegalArgumentException("Trạng thái hiện tại chưa được nhập.");
        if (tenTrangThaiMoi == null || tenTrangThaiMoi.trim().isEmpty())
            throw new IllegalArgumentException("Trạng thái mới chưa được nhập.");

        String trangThaiHienTai = chuanHoaTrangThai(tenTrangThaiHienTai);
        String trangThaiMoi = chuanHoaTrangThai(tenTrangThaiMoi);

        if ("HOAN_THANH".equals(trangThaiHienTai)) {
            throw new IllegalStateException("Đơn hàng đã hoàn thành, không thể cập nhật thêm.");
        }
        if (trangThaiHienTai.equals(trangThaiMoi)) {
            throw new IllegalArgumentException("Trạng thái mới không được trùng với trạng thái hiện tại.");
        }

        try {
            donDatHangDAO.chuyenTrangThaiDon(maDon, maTrangThaiMoi, maNvCapNhat, hinhThucNhan);

            // Nếu chuyển sang HOÀN_THÀNH → trả về đơn hàng để ThanhToanService tạo hóa đơn
            if ("HOAN_THANH".equals(trangThaiMoi)) {
                return donDatHangDAO.layTomTatDonHang(maDon);
            }
            return null;
        } catch (SQLException e) {
            throw new Exception("Chuyển trạng thái thất bại: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // 3. HỦY ĐƠN HÀNG
    // =========================================================

    /** Hủy đơn và hoàn kho sau khi kiểm tra trạng thái cho phép hủy. */
    public void huyDonVaHoanCoc(int maDon, String lyDoHuy, int maNvCapNhat,
            String tenTrangThaiHienTai) throws Exception {
        if (maDon <= 0)
            throw new IllegalArgumentException("Mã đơn hủy sai định dạng.");
        if (maNvCapNhat <= 0)
            throw new IllegalArgumentException("Mã nhân viên cập nhật sai định dạng.");
        if (lyDoHuy == null || lyDoHuy.trim().isEmpty())
            throw new IllegalArgumentException("Lý do hủy đơn chưa được nhập.");
        if (kiemTraTrangThaiCamHuy(tenTrangThaiHienTai))
            throw new IllegalStateException("Đơn hàng đang ở trạng thái không cho phép hủy.");

        try {
            donDatHangDAO.huyDonVaHoanKho(maDon, lyDoHuy.trim(), maNvCapNhat);
        } catch (SQLException e) {
            throw new Exception("Hủy đơn thất bại: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // 4. TRA CỨU THÔNG TIN ĐƠN HÀNG
    // =========================================================

    /** Lấy bản tóm tắt đơn hàng theo mã. Ném Exception nếu không tìm thấy. */
    public DonDatHangDTO layTomTatDonHang(int maDon) throws Exception {
        if (maDon <= 0)
            throw new IllegalArgumentException("Mã đơn theo dõi không hợp lệ.");
        try {
            DonDatHangDTO tomTat = donDatHangDAO.layTomTatDonHang(maDon);
            if (tomTat == null)
                throw new Exception("Không tìm thấy đơn hàng với mã " + maDon + ".");
            return tomTat;
        } catch (SQLException e) {
            throw new Exception("Không thể lấy thông tin đơn hàng: " + e.getMessage(), e);
        }
    }

    /** Lấy tên trạng thái hiện tại của đơn hàng. */
    public String theoDoiDonHang(int maDon) throws Exception {
        if (maDon <= 0)
            throw new IllegalArgumentException("Mã đơn theo dõi không hợp lệ.");
        try {
            String tenTrangThai = donDatHangDAO.layTenTrangThaiDon(maDon);
            if (tenTrangThai == null || tenTrangThai.trim().isEmpty())
                throw new Exception("Không tìm thấy đơn hàng với mã " + maDon + ".");
            return tenTrangThai;
        } catch (SQLException e) {
            throw new Exception("Không thể theo dõi đơn hàng: " + e.getMessage(), e);
        }
    }

    /** Lấy danh sách chi tiết sản phẩm của đơn hàng. */
    public List<CTDonHangDTO> layChiTietDonHang(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn không hợp lệ.");
        try {
            return donDatHangDAO.layChiTietDonHang(maDon);
        } catch (SQLException e) {
            throw new Exception("Không thể lấy chi tiết đơn hàng: " + e.getMessage(), e);
        }
    }

    /** Lấy danh sách chi tiết tùy chỉnh (bánh custom) của đơn hàng. */
    public List<CTDonTuyChinhDTO> layChiTietTuyChinh(int maDon) throws Exception {
        if (maDon <= 0) throw new IllegalArgumentException("Mã đơn không hợp lệ.");
        try {
            return donDatHangDAO.layChiTietTuyChinh(maDon);
        } catch (SQLException e) {
            throw new Exception("Không thể lấy chi tiết tùy chỉnh: " + e.getMessage(), e);
        }
    }

    /** Lấy danh sách tất cả trạng thái đơn hàng từ DB. */
    public List<TrangThaiDonDTO> layDanhSachTrangThaiDon() throws Exception {
        try {
            return donDatHangDAO.layDanhSachTrangThaiDon();
        } catch (SQLException e) {
            throw new Exception("Không thể lấy danh sách trạng thái đơn: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    private int layMaTrangThaiDaCoc() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO tt : dsTrangThai) {
            if ("DA_COC".equals(chuanHoaTrangThai(tt.getTenTrangThai())))
                return tt.getMaTrangThai();
        }
        return layMaTrangThaiMoiDat();
    }

    private int layMaTrangThaiMoiDat() throws Exception {
        List<TrangThaiDonDTO> dsTrangThai = layDanhSachTrangThaiDon();
        for (TrangThaiDonDTO tt : dsTrangThai) {
            String normalized = chuanHoaTrangThai(tt.getTenTrangThai());
            if ("MOI_DAT".equals(normalized) || "CHO_XU_LY".equals(normalized))
                return tt.getMaTrangThai();
        }
        throw new Exception("Không tìm thấy trạng thái mặc định MOI_DAT/CHO_XU_LY.");
    }

    private void kiemTraYeuCauDonHang(YeuCauTaoDonHangDTO request) {
        if (request == null)
            throw new IllegalArgumentException("Yêu cầu tạo đơn hàng bị trống.");
        if (request.getMaNVLap() <= 0)
            throw new IllegalArgumentException("Mã nhân viên lập đơn không hợp lệ.");
        if (request.getTienDaCoc() < 0)
            throw new IllegalArgumentException("Tiền đặt cọc không được âm.");
        if (request.getHinhThucNhan() == null
                || (request.getHinhThucNhan() != 1 && request.getHinhThucNhan() != 2))
            throw new IllegalArgumentException("Hình thức nhận chỉ được là Trực tiếp (1) hoặc Đặt hàng (2).");
        if (request.getHinhThucNhan() == 2
                && (request.getDiaChiGiao() == null || request.getDiaChiGiao().trim().isEmpty()))
            throw new IllegalArgumentException("Đơn đặt hàng bắt buộc nhập địa chỉ giao.");
        if (request.getHinhThucNhan() == 1)
            request.setDiaChiGiao(null);
        if (request.getNgayGioNhanBanh() == null)
            throw new IllegalArgumentException("Ngày giờ nhận bánh bắt buộc nhập.");
        if (request.getNgayGioNhanBanh().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("Ngày giờ nhận bánh không được nằm trong quá khứ.");

        List<YeuCauChiTietDonHangDTO> items = request.getItems();
        if (items == null || items.isEmpty())
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 sản phẩm.");
    }

    private DonDatHangDTO chuyenSangDonDatHangDTO(YeuCauTaoDonHangDTO request, int maTrangThai) {
        DonDatHangDTO donDatHang = new DonDatHangDTO();
        donDatHang.setNgayGioNhanBanh(request.getNgayGioNhanBanh());
        donDatHang.setMaKH(request.getMaKH());
        donDatHang.setMaNVLap(request.getMaNVLap());
        donDatHang.setMaTrangThai(maTrangThai);
        donDatHang.setTienDaCoc(java.math.BigDecimal.valueOf(request.getTienDaCoc()));
        donDatHang.setHinhThucNhan(request.getHinhThucNhan());
        donDatHang.setDiaChiGiao(request.getDiaChiGiao());
        return donDatHang;
    }

    private void chuyenDoiChiTietDonHang(List<YeuCauChiTietDonHangDTO> items,
            List<CTDonHangDTO> dsCtDonHang, List<CTDonTuyChinhDTO> dsCtTuyChinh) {
        for (YeuCauChiTietDonHangDTO item : items) {
            if (item.isCustom() && item instanceof YeuCauChiTietDonTuyChinhDTO) {
                YeuCauChiTietDonTuyChinhDTO customItem = (YeuCauChiTietDonTuyChinhDTO) item;
                CTDonTuyChinhDTO ct = new CTDonTuyChinhDTO();
                ct.setMaSP(customItem.getMaSP());
                ct.setSoLuong(customItem.getSoLuong());
                ct.setDonGia(java.math.BigDecimal.valueOf(customItem.getDonGia()));
                ct.setLoiChucTrenBanh(customItem.getLoiChucTrenBanh());
                ct.setGhiChuThoBanh(customItem.getGhiChuThoBanh());
                ct.setMaKC(customItem.getMaKC());
                ct.setMaCot(customItem.getMaCot());
                ct.setMaNhan(customItem.getMaNhan());
                ct.setMaTrangTri(customItem.getMaTrangTri());
                dsCtTuyChinh.add(ct);
            } else if (item.isCustom()) {
                // Fallback: bánh tùy chỉnh nhưng không cast được
                CTDonTuyChinhDTO ct = new CTDonTuyChinhDTO();
                ct.setMaSP(item.getMaSP());
                ct.setSoLuong(item.getSoLuong());
                ct.setDonGia(java.math.BigDecimal.valueOf(item.getDonGia()));
                ct.setLoiChucTrenBanh(item.getGhiChu());
                ct.setGhiChuThoBanh(item.getPhuKien());
                dsCtTuyChinh.add(ct);
            } else {
                CTDonHangDTO ct = new CTDonHangDTO();
                ct.setMaSP(item.getMaSP());
                ct.setSoLuong(item.getSoLuong());
                ct.setDonGia(java.math.BigDecimal.valueOf(item.getDonGia()));
                dsCtDonHang.add(ct);
            }
        }
    }

    private boolean kiemTraTrangThaiCamHuy(String tenTrangThaiHienTai) {
        String current = chuanHoaTrangThai(tenTrangThaiHienTai);
        return !("MOI_DAT".equals(current) || "DA_COC".equals(current) || "CHO_XU_LY".equals(current));
    }

    /** Chuẩn hóa tên trạng thái: bỏ dấu, uppercase, thay khoảng trắng bằng '_'. */
    private String chuanHoaTrangThai(String rawStatus) {
        if (rawStatus == null)
            return "";
        String normalized = Normalizer.normalize(rawStatus.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("đ", "d").replace("Đ", "D")
                .toUpperCase().replace(' ', '_');
        if (normalized.contains("KHACH") && normalized.contains("LAY"))
            return "CHO_KHACH_LAY";
        return normalized;
    }
}
