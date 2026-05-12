package com.bakery.services.taichinh;

import com.bakery.model.dao.taichinh.LoaiThuChiDAO;
import com.bakery.model.dao.taichinh.PhieuThuChiDAO;
import com.bakery.model.dto.taichinh.LoaiThuChiDTO;
import com.bakery.model.dto.taichinh.PhieuThuChiDTO;

import java.util.List;

/**
 * Service nghiệp vụ Sổ Quỹ.
 * Điều phối LoaiThuChiDAO + PhieuThuChiDAO, áp dụng validate kinh doanh.
 */
public class SoQuyService {

    private final LoaiThuChiDAO loaiThuChiDAO   = new LoaiThuChiDAO();
    private final PhieuThuChiDAO phieuThuChiDAO = new PhieuThuChiDAO();

    // ── Loại Thu Chi ──────────────────────────────────────────────────────

    public List<LoaiThuChiDTO> layDanhSachLoaiThuChi() throws Exception {
        return loaiThuChiDAO.layDanhSachLoaiThuChi();
    }

    /**
     * Thêm loại thu chi sau khi validate.
     *
     * @param ten      tên hạng mục (không rỗng)
     * @param phanLoai 'Thu' hoặc 'Chi'
     * @return mã mới
     */
    public int themLoaiThuChi(String ten, String phanLoai) throws Exception {
        validateTenLoai(ten);
        validatePhanLoai(phanLoai);
        return loaiThuChiDAO.themLoaiThuChi(ten.trim(), phanLoai);
    }

    /**
     * Cập nhật loại thu chi sau khi validate.
     *
     * @param ma       mã loại
     * @param tenMoi   tên mới
     * @param phanLoai phân loại mới
     */
    public void suaLoaiThuChi(int ma, String tenMoi, String phanLoai) throws Exception {
        validateTenLoai(tenMoi);
        validatePhanLoai(phanLoai);
        loaiThuChiDAO.suaLoaiThuChi(ma, tenMoi.trim(), phanLoai);
    }

    /**
     * Soft-delete loại thu chi.
     *
     * @param ma         mã loại cần xóa
     * @param maNguoiXoa mã nhân viên thực hiện
     */
    public void xoaLoaiThuChi(int ma, int maNguoiXoa) throws Exception {
        loaiThuChiDAO.xoaLoaiThuChi(ma, maNguoiXoa);
    }

    // ── Phiếu Thu Chi ────────────────────────────────────────────────────

    /** Lấy danh sách phiếu theo ca. */
    public List<PhieuThuChiDTO> layDanhSachTheoCa(int maCa) throws Exception {
        return phieuThuChiDAO.layDanhSachTheoCa(maCa);
    }

    /** Lấy toàn bộ phiếu (cho tab báo cáo). */
    public List<PhieuThuChiDTO> layTatCaPhieu() throws Exception {
        return phieuThuChiDAO.layTatCaPhieu();
    }

    /**
     * Lập phiếu thu chi sau khi validate số tiền và ca mở.
     *
     * @param maLoai mã loại thu chi
     * @param soTien số tiền (> 0)
     * @param maNV   mã nhân viên
     * @param maCa   ca hiện tại (phải > 0 — ca đã mở)
     * @param ghiChu ghi chú tùy chọn
     * @return mã phiếu mới
     */
    public int lapPhieuThuChi(int maLoai, double soTien, int maNV,
                               int maCa, String ghiChu) throws Exception {
        if (maCa <= 0) {
            throw new Exception("Chưa mở ca làm việc. Vui lòng mở ca trước khi lập phiếu thu chi.");
        }
        if (soTien <= 0) {
            throw new Exception("Số tiền phải lớn hơn 0.");
        }
        return phieuThuChiDAO.taoPhieuThuChi(maLoai, soTien, maNV, maCa, ghiChu);
    }

    /**
     * Hủy phiếu thu chi (soft-delete).
     *
     * @param maPhieu mã phiếu cần hủy
     */
    public void huyPhieuThuChi(int maPhieu) throws Exception {
        phieuThuChiDAO.huyPhieuThuChi(maPhieu);
    }

    // ── Helpers validate ─────────────────────────────────────────────────

    private void validateTenLoai(String ten) throws Exception {
        if (ten == null || ten.isBlank()) {
            throw new Exception("Tên loại thu chi không được để trống.");
        }
        if (ten.trim().length() > 200) {
            throw new Exception("Tên loại thu chi không được quá 200 ký tự.");
        }
    }

    private void validatePhanLoai(String phanLoai) throws Exception {
        if (!"Thu".equals(phanLoai) && !"Chi".equals(phanLoai)) {
            throw new Exception("Phân loại phải là 'Thu' hoặc 'Chi'.");
        }
    }
}
