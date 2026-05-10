package com.bakery.services.kho;
import com.bakery.services.BaseService;

import com.bakery.model.dao.kho.DonViTinhDAO;
import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dao.kho.NhaCungCapDAO;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý toàn bộ nghiệp vụ cho module Quản lý Nguyên liệu.
 * Tuân thủ SRP: chỉ chứa business-logic, không biết gì về UI hay Presenter.
 *
 * Ràng buộc nghiệp vụ:
 * 1. Thêm / Sửa → Chặn trùng tên nguyên liệu (case-insensitive).
 * 2. Thêm / Sửa → Tên và Đơn vị tính không được rỗng.
 * 3. Xóa → Procedure tự xử lý (xóa mềm nếu đã có lịch sử nhập kho).
 */
public class NguyenLieuService extends BaseService {

    private final NguyenLieuDAO nguyenLieuDAO;
    private final DonViTinhDAO donViTinhDAO;
    private final NhaCungCapDAO nhaCungCapDAO;

    /** Constructor production. */
    public NguyenLieuService() {
        this.nguyenLieuDAO = new NguyenLieuDAO();
        this.donViTinhDAO = new DonViTinhDAO();
        this.nhaCungCapDAO = new NhaCungCapDAO();
    }

    /** Constructor injection — dùng cho unit test. */
    public NguyenLieuService(NguyenLieuDAO nguyenLieuDAO, DonViTinhDAO donViTinhDAO, NhaCungCapDAO nhaCungCapDAO) {
        this.nguyenLieuDAO = nguyenLieuDAO;
        this.donViTinhDAO = donViTinhDAO;
        this.nhaCungCapDAO = nhaCungCapDAO;
    }

    // =========================================================
    // 1. ĐỌC DỮ LIỆU
    // =========================================================

    /** Lấy danh sách nguyên liệu đang hoạt động. */
    public List<NguyenLieuDTO> layDanhSachNguyenLieu() throws Exception {
        return nguyenLieuDAO.layTatCaNguyenLieu();
    }

    /** Lấy danh sách đơn vị tính để nạp vào ComboBox. */
    public List<DonViTinhDTO> layDanhSachDonViTinh() throws Exception {
        return donViTinhDAO.layTatCaDonViTinh();
    }

    /** Lấy danh sách nhà cung cấp để nạp vào ComboBox dialog. */
    public List<NhaCungCapDTO> layDanhSachNhaCungCap() throws Exception {
        return nhaCungCapDAO.layDanhSachNhaCungCap();
    }

    /**
     * Tìm kiếm nguyên liệu theo tên.
     *
     * @throws Exception nếu không tìm thấy kết quả nào
     */
    public List<NguyenLieuDTO> timKiemNguyenLieu(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layDanhSachNguyenLieu();
        }
        List<NguyenLieuDTO> ketQua = nguyenLieuDAO.timKiemNguyenLieu(keyword.trim());
        if (ketQua.isEmpty()) {
            throw new Exception("Không tìm thấy nguyên liệu nào phù hợp với: \"" + keyword + "\".");
        }
        return ketQua;
    }

    // =========================================================
    // 2. THÊM NGUYÊN LIỆU
    // =========================================================

    /**
     * Thêm nguyên liệu mới.
     *
     * @param tenNL        tên nguyên liệu (chưa trim)
     * @param xuatXu       xuất xứ (có thể rỗng)
     * @param mucTonAnToan mức tồn an toàn (>= 0)
     * @param maDVT        mã đơn vị tính (> 0)
     * @param maNv         mã nhân viên thực hiện
     * @return mã nguyên liệu vừa tạo
     * @throws Exception khi vi phạm ràng buộc nghiệp vụ
     */
    public int themNguyenLieu(String tenNL, String xuatXu,
            double mucTonAnToan, int maDVT, int maNv) throws Exception {
        String tenChuan = validateTen(tenNL);
        validateDVT(maDVT);
        kiemTraTrungTenKhiThem(tenChuan);

        NguyenLieuDTO dto = new NguyenLieuDTO();
        dto.setTenNL(tenChuan);
        dto.setXuatXu(xuatXu == null ? "" : xuatXu.trim());
        dto.setMucTonAnToan(Math.max(0, mucTonAnToan));
        dto.setMaDVT(maDVT);

        int maMoi = nguyenLieuDAO.themNguyenLieu(dto, maNv);
        if (maMoi < 1) {
            throw new Exception("Lỗi hệ thống: không thể thêm nguyên liệu. Vui lòng thử lại.");
        }
        return maMoi;
    }

    // =========================================================
    // 2b. THÊM NGUYÊN LIỆU + NHẬP KHO LẦN ĐẦU (ATOMIC)
    // =========================================================

    /**
     * Thêm nguyên liệu mới đồng thời tạo phiếu nhập đầu tiên.
     * Đảm bảo GIAVONTRUNGBINH được tính ngay sau khi tạo.
     */
    public int[] themNguyenLieuVaNhapKho(
            String tenNL, String xuatXu, double mucTonAnToan, int maDVT,
            int maNCC, int maNV,
            double soLuong, double donGia,
            LocalDate ngaySanXuat, LocalDate hanSuDung) throws Exception {

        String tenChuan = validateTen(tenNL);
        validateDVT(maDVT);
        if (maNCC <= 0) throw new Exception("Vui lòng chọn nhà cung cấp.");
        if (soLuong <= 0) throw new Exception("Số lượng nhập phải lớn hơn 0.");
        if (donGia <= 0) throw new Exception("Đơn giá phải lớn hơn 0.");
        kiemTraTrungTenKhiThem(tenChuan);

        NguyenLieuDTO dto = new NguyenLieuDTO();
        dto.setTenNL(tenChuan);
        dto.setXuatXu(xuatXu == null ? "" : xuatXu.trim());
        dto.setMucTonAnToan(Math.max(0, mucTonAnToan));
        dto.setMaDVT(maDVT);

        Date sqlNSX = ngaySanXuat != null ? Date.valueOf(ngaySanXuat) : null;
        Date sqlHSD = hanSuDung  != null ? Date.valueOf(hanSuDung)  : null;

        int[] result = nguyenLieuDAO.themNguyenLieuVaNhapKho(dto, maNCC, maNV, soLuong, donGia, sqlNSX, sqlHSD);
        if (result[0] < 1) throw new Exception("Lỗi hệ thống: không thể tạo nguyên liệu. Vui lòng thử lại.");
        return result;
    }

    // =========================================================
    // 3. SỬA NGUYÊN LIỆU
    // =========================================================

    /**
     * Cập nhật thông tin nguyên liệu.
     *
     * @throws Exception khi vi phạm ràng buộc nghiệp vụ
     */
    public void suaNguyenLieu(int maNL, String tenNL, String xuatXu,
            double mucTonAnToan, int maDVT) throws Exception {
        String tenChuan = validateTen(tenNL);
        validateDVT(maDVT);
        kiemTraTrungTenKhiSua(maNL, tenChuan);

        NguyenLieuDTO dto = new NguyenLieuDTO();
        dto.setMaNL(maNL);
        dto.setTenNL(tenChuan);
        dto.setXuatXu(xuatXu == null ? "" : xuatXu.trim());
        dto.setMucTonAnToan(Math.max(0, mucTonAnToan));
        dto.setMaDVT(maDVT);

        boolean ok = nguyenLieuDAO.capNhatNguyenLieu(dto);
        if (!ok) {
            throw new Exception("Lỗi hệ thống: không thể cập nhật nguyên liệu. Vui lòng thử lại.");
        }
    }

    // =========================================================
    // 4. XÓA NGUYÊN LIỆU
    // =========================================================

    /**
     * Xóa nguyên liệu.
     * Procedure xử lý logic: xóa mềm nếu đã có lịch sử nhập kho,
     * xóa cứng nếu chưa từng nhập kho.
     *
     * @throws Exception khi lỗi hệ thống
     */
    public void xoaNguyenLieu(int maNL, int maNv) throws Exception {
        boolean ok = nguyenLieuDAO.xoaNguyenLieu(maNL, maNv);
        if (!ok) {
            throw new Exception("Lỗi hệ thống: không thể xóa nguyên liệu. Vui lòng thử lại.");
        }
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    private String validateTen(String ten) throws Exception {
        if (ten == null || ten.trim().isEmpty()) {
            throw new Exception("Tên nguyên liệu không được để trống.");
        }
        return ten.trim();
    }

    private void validateDVT(int maDVT) throws Exception {
        if (maDVT <= 0) {
            throw new Exception("Vui lòng chọn đơn vị tính.");
        }
    }

    private void kiemTraTrungTenKhiThem(String tenChuan) throws Exception {
        for (NguyenLieuDTO nl : nguyenLieuDAO.layTatCaNguyenLieu()) {
            if (nl.getTenNL().equalsIgnoreCase(tenChuan)) {
                throw new Exception("Tên nguyên liệu \"" + tenChuan + "\" đã tồn tại.");
            }
        }
    }

    private void kiemTraTrungTenKhiSua(int maNLHienTai, String tenChuan) throws Exception {
        for (NguyenLieuDTO nl : nguyenLieuDAO.layTatCaNguyenLieu()) {
            if (nl.getMaNL() != maNLHienTai && nl.getTenNL().equalsIgnoreCase(tenChuan)) {
                throw new Exception("Tên nguyên liệu \"" + tenChuan + "\" đã tồn tại.");
            }
        }
    }
}
