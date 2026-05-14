package com.bakery.services.hethong;

import com.bakery.model.dao.hethong.KhoiPhucDuLieuDAO;
import com.bakery.model.dto.hethong.KhoiPhucDuLieuDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.utils.UserSession;

import java.util.List;

/**
 * Service nghiệp vụ cho chức năng khôi phục / tự xóa dữ liệu (UC60).
 * Không chứa SQL — ủy quyền hoàn toàn cho KhoiPhucDuLieuDAO.
 */
public class KhoiPhucDuLieuService {

    /** Ngưỡng ngày xóa vĩnh viễn theo quy định nghiệp vụ. */
    public static final int NGUONG_NGAY_XOA = 120;

    private final KhoiPhucDuLieuDAO dao = new KhoiPhucDuLieuDAO();

    /**
     * Lấy danh sách bản ghi đã xóa mềm, lọc theo loại.
     *
     * @param loaiDoiTuong null/"Tất cả" = tất cả bảng; khác = lọc bảng cụ thể
     */
    public List<KhoiPhucDuLieuDTO> layDanhSachDaXoa(String loaiDoiTuong) throws Exception {
        String boLoc = (loaiDoiTuong == null || loaiDoiTuong.equalsIgnoreCase("Tất cả"))
                ? null : loaiDoiTuong;
        return dao.layDanhSachDaXoa(boLoc);
    }

    /**
     * Khôi phục một bản ghi cụ thể (xóa mềm → khôi phục).
     * Ghi audit log qua procedure với mã NV hiện tại.
     *
     * @param dto DTO chứa thông tin bảng + PK cần khôi phục
     */
    public void khoiPhuc(KhoiPhucDuLieuDTO dto) throws Exception {
        if (dto == null || dto.getMaDoiTuong() == null || dto.getTenBang() == null) {
            throw new Exception("Thông tin bản ghi không hợp lệ.");
        }
        dao.khoiPhuc(dto.getTenBang(), dto.getTenCotXoa(), dto.getMaDoiTuong(), layMaNvHienTai());
    }

    /**
     * Xóa vĩnh viễn tất cả bản ghi đã soft-delete quá {@link #NGUONG_NGAY_XOA} ngày.
     * Ghi audit log qua procedure với mã NV hiện tại.
     *
     * @return Thông báo kết quả (số bản ghi đã purge)
     */
    public String xoaVinhVienQuaHan() throws Exception {
        int tong = dao.xoaVinhVienQuaHan(NGUONG_NGAY_XOA, layMaNvHienTai());
        if (tong == 0) {
            return "Không có bản ghi nào vượt quá " + NGUONG_NGAY_XOA + " ngày để xóa.";
        }
        return "Đã xóa vĩnh viễn " + tong + " bản ghi quá " + NGUONG_NGAY_XOA + " ngày.";
    }

    /**
     * Lấy danh sách loại đối tượng để hiển thị ComboBox lọc.
     */
    public List<String> layDanhSachLoai() {
        return dao.layDanhSachLoai();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /** Lấy mã NV đang đăng nhập từ session. */
    private int layMaNvHienTai() {
        NhanVienDTO user = UserSession.getCurrentUser();
        return (user != null) ? user.getMaNV() : 0;
    }
}
