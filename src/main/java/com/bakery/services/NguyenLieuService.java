package com.bakery.services;

import com.bakery.model.dao.DonViTinhDAO;
import com.bakery.model.dao.NguyenLieuDAO;
import com.bakery.model.dto.DonViTinhDTO;
import com.bakery.model.dto.NguyenLieuDTO;

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

    /** Constructor production. */
    public NguyenLieuService() {
        this.nguyenLieuDAO = new NguyenLieuDAO();
        this.donViTinhDAO = new DonViTinhDAO();
    }

    /** Constructor injection — dùng cho unit test. */
    public NguyenLieuService(NguyenLieuDAO nguyenLieuDAO, DonViTinhDAO donViTinhDAO) {
        this.nguyenLieuDAO = nguyenLieuDAO;
        this.donViTinhDAO = donViTinhDAO;
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
