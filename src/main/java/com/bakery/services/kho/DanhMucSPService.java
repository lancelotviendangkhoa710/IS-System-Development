package com.bakery.services.kho;
import com.bakery.services.BaseService;

import com.bakery.model.dao.kho.DanhMucSPDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.DanhMucSPDTO;

import java.util.List;

/**
 * Service xử lý toàn bộ nghiệp vụ cho module Quản lý Danh mục Sản phẩm.
 * Tuân thủ SRP: chỉ chứa business-logic, không biết gì về UI hay Presenter.
 *
 * Ràng buộc nghiệp vụ:
 *  1. Thêm / Sửa → Chặn trùng tên danh mục (không phân biệt hoa thường).
 *  2. Xóa → Chặn khi danh mục đang chứa ≥ 1 sản phẩm chưa bị xóa mềm.
 */
public class DanhMucSPService extends BaseService {

    private final DanhMucSPDAO danhMucSPDAO;
    private final SanPhamDAO   sanPhamDAO;

    /** Constructor mặc định — production. */
    public DanhMucSPService() {
        this.danhMucSPDAO = new DanhMucSPDAO();
        this.sanPhamDAO   = new SanPhamDAO();
    }

    /** Constructor injection — dễ mock khi test. */
    public DanhMucSPService(DanhMucSPDAO danhMucSPDAO, SanPhamDAO sanPhamDAO) {
        this.danhMucSPDAO = danhMucSPDAO;
        this.sanPhamDAO   = sanPhamDAO;
    }

    // =========================================================
    // 1. ĐỌC DỮ LIỆU
    // =========================================================

    /**
     * Lấy danh sách tất cả danh mục đang hoạt động (chưa bị xóa mềm).
     *
     * @return danh sách DanhMucSPDTO, rỗng nếu không có dữ liệu
     */
    public List<DanhMucSPDTO> layDanhSachDanhMuc() throws Exception {
        return danhMucSPDAO.layTatCaDanhMucConHoatDong();
    }

    // =========================================================
    // 2. THÊM DANH MỤC
    // =========================================================

    /**
     * Thêm một danh mục mới vào hệ thống.
     *
     * Nghiệp vụ:
     *  - Tên không được rỗng.
     *  - Tên chưa tồn tại trong DB (case-insensitive).
     *
     * @param tenDM tên danh mục mới (chưa trim)
     * @return mã danh mục vừa được tạo (≥ 1)
     * @throws Exception khi vi phạm ràng buộc nghiệp vụ
     */
    public int themDanhMuc(String tenDM) throws Exception {
        validateString(tenDM, "Tên danh mục");
        String tenChuan = tenDM.trim();
        kiemTraTrungTenKhiThem(tenChuan);

        DanhMucSPDTO dto = new DanhMucSPDTO();
        dto.setTenDM(tenChuan);

        int maMoi = danhMucSPDAO.themDanhMuc(dto);
        if (maMoi < 1) {
            throw new Exception("Lỗi hệ thống: không thể thêm danh mục. Vui lòng thử lại.");
        }
        return maMoi;
    }

    /**
     * Tìm kiếm danh mục theo tên.
     * 
     * @param keyword từ khóa tìm kiếm
     * @return danh sách kết quả
     * @throws Exception nếu không tìm thấy kết quả nào (để View báo "Không tồn tại")
     */
    public List<DanhMucSPDTO> timKiemDanhMuc(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return layDanhSachDanhMuc();
        }
        
        List<DanhMucSPDTO> ketQua = danhMucSPDAO.timKiemDanhMuc(keyword.trim());
        if (ketQua.isEmpty()) {
            throw new Exception("Không tìm thấy danh mục nào phù hợp với: " + keyword);
        }
        return ketQua;
    }

    // =========================================================
    // 3. SỬA DANH MỤC
    // =========================================================

    /**
     * Cập nhật tên của một danh mục đã tồn tại.
     *
     * Nghiệp vụ:
     *  - Tên không được rỗng.
     *  - Tên mới chưa được danh mục KHÁC dùng (case-insensitive).
     *
     * @param maDM  mã danh mục cần sửa
     * @param tenDM tên mới (chưa trim)
     * @throws Exception khi vi phạm ràng buộc nghiệp vụ
     */
    public void suaDanhMuc(int maDM, String tenDM) throws Exception {
        validateString(tenDM, "Tên danh mục");
        String tenChuan = tenDM.trim();
        kiemTraTrungTenKhiSua(maDM, tenChuan);

        DanhMucSPDTO dto = new DanhMucSPDTO();
        dto.setMaDM(maDM);
        dto.setTenDM(tenChuan);

        boolean ok = danhMucSPDAO.capNhatDanhMuc(dto);
        if (!ok) {
            throw new Exception("Lỗi hệ thống: không thể cập nhật danh mục. Vui lòng thử lại.");
        }
    }

    // =========================================================
    // 4. XÓA DANH MỤC (XÓA MỀM)
    // =========================================================

    /**
     * Xóa mềm một danh mục khỏi hệ thống.
     *
     * Nghiệp vụ:
     *  - Không cho phép xóa nếu danh mục đang chứa ít nhất 1 sản phẩm chưa bị xóa.
     *
     * @param maDM    mã danh mục cần xóa
     * @param maNvXoa mã nhân viên thực hiện xóa (ghi vào audit log)
     * @throws Exception khi danh mục đang có sản phẩm hoặc lỗi hệ thống
     */
    public void xoaDanhMuc(int maDM, int maNvXoa) throws Exception {
        // Guard: kiểm tra sản phẩm trước khi xóa
        int soSanPham = sanPhamDAO.demSanPhamTheoDanhMuc(maDM);
        if (soSanPham > 0) {
            throw new Exception("Không thể xóa danh mục đang có sản phẩm.");
        }

        boolean ok = danhMucSPDAO.xoaDanhMuc(maDM, maNvXoa);
        if (!ok) {
            throw new Exception("Lỗi hệ thống: không thể xóa danh mục. Vui lòng thử lại.");
        }
    }

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================



    /**
     * Kiểm tra trùng tên khi THÊM MỚI (so sánh với toàn bộ danh sách hiện có).
     *
     * @throws Exception nếu tên đã tồn tại
     */
    private void kiemTraTrungTenKhiThem(String tenChuan) throws Exception {
        List<DanhMucSPDTO> dsDanhMuc = danhMucSPDAO.layTatCaDanhMucConHoatDong();
        for (DanhMucSPDTO dm : dsDanhMuc) {
            if (dm.getTenDM().equalsIgnoreCase(tenChuan)) {
                throw new Exception("Tên danh mục \"" + tenChuan + "\" đã tồn tại.");
            }
        }
    }

    /**
     * Kiểm tra trùng tên khi SỬA (bỏ qua chính nó để không báo lỗi khi giữ nguyên tên).
     *
     * @param maDMHienTai mã danh mục đang được chỉnh sửa (sẽ bị loại trừ)
     * @throws Exception nếu tên đã được danh mục khác dùng
     */
    private void kiemTraTrungTenKhiSua(int maDMHienTai, String tenChuan) throws Exception {
        List<DanhMucSPDTO> dsDanhMuc = danhMucSPDAO.layTatCaDanhMucConHoatDong();
        for (DanhMucSPDTO dm : dsDanhMuc) {
            if (dm.getMaDM() != maDMHienTai && dm.getTenDM().equalsIgnoreCase(tenChuan)) {
                throw new Exception("Tên danh mục \"" + tenChuan + "\" đã tồn tại.");
            }
        }
    }
}
