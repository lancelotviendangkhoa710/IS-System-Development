package com.bakery.services.hethong;
import com.bakery.services.BaseService;
import com.bakery.utils.SessionContext;

import com.bakery.model.dao.hethong.LoaiThuChiDAO;
import com.bakery.model.dao.hethong.PhieuThuChiDAO;
import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;

import java.math.BigDecimal;
import java.util.List;

public class SoQuyService extends BaseService {

    private final PhieuThuChiDAO ptcDAO = new PhieuThuChiDAO();
    private final LoaiThuChiDAO  ltcDAO = new LoaiThuChiDAO();

    public List<PhieuThuChiDTO> layGiaoDich(int maCa) throws Exception {
        List<PhieuThuChiDTO> data = ptcDAO.layTheoMaCa(maCa);
        return data != null ? data : java.util.List.of();
    }

    /** Lấy toàn bộ phiếu thu chi — không giới hạn ca. */
    public List<PhieuThuChiDTO> layTatCaGiaoDich() throws Exception {
        List<PhieuThuChiDTO> data = ptcDAO.layTatCa();
        return data != null ? data : java.util.List.of();
    }

    public void huyGiaoDich(int maPhieuTC, String lyDo) throws Exception {
        ptcDAO.huyPhieu(maPhieuTC, lyDo);
    }

    public void themGiaoDich(int maCa, int maNV, int maLoaiThuChi,
                             BigDecimal soTien, String ghiChu) throws Exception {
        PhieuThuChiDTO dto = new PhieuThuChiDTO();
        dto.setMaCa(maCa);
        dto.setMaNV(maNV);
        dto.setMaLoaiThuChi(maLoaiThuChi);
        dto.setSoTien(soTien);
        dto.setGhiChu(ghiChu);
        ptcDAO.taoPhieuThuChi(dto);
    }

    /** Chỉ loại đang hoạt động — dùng cho ComboBox lập phiếu. */
    public List<LoaiThuChiDTO> layDanhSachLoai() throws Exception {
        List<LoaiThuChiDTO> data = ltcDAO.layDanhSach();
        return data != null ? data : java.util.List.of();
    }

    /** Tất cả loại kể cả đã khoá — dùng cho tab Cấu hình. */
    public List<LoaiThuChiDTO> layTatCaDanhSachLoai() throws Exception {
        List<LoaiThuChiDTO> data = ltcDAO.layTatCa();
        return data != null ? data : java.util.List.of();
    }

    public void moKhoaLoai(int ma) throws Exception {
        ltcDAO.moKhoa(ma);
    }

    public void themLoai(String ten, String phanLoai) throws Exception {
        ltcDAO.them(ten, phanLoai);
    }

    public void suaLoai(int ma, String ten, String phanLoai) throws Exception {
        ltcDAO.sua(ma, ten, phanLoai);
    }

    /**
     * Xóa mềm loại thu chi — lấy maNV từ SessionContext.
     * Backward-compat cho SoQuyPresenter và các controller cũ.
     */
    public void xoaLoai(int ma) throws Exception {
        int maNV = SessionContext.getInstance().getMaNV();
        ltcDAO.xoa(ma, maNV);
    }

    /**
     * Xóa mềm loại thu chi — truyền maNV tường minh.
     * Dùng cho controller mới biết maNV trước khi gọi.
     */
    public void xoaLoai(int ma, int maNV) throws Exception {
        ltcDAO.xoa(ma, maNV);
    }

    /**
     * Tính tổng thu từ danh sách đã load — duy trì backward compat với Presenter.
     * Ưu tiên dùng tinhTongThuChiTheoMaCa() cho hiệu năng tốt hơn.
     */
    public java.math.BigDecimal tinhTongThu(java.util.List<com.bakery.model.dto.hethong.PhieuThuChiDTO> ds) {
        return ds.stream()
                .filter(p -> "Thu".equals(p.getPhanLoai())
                        && !"cancelled".equalsIgnoreCase(p.getTrangThai()))
                .map(PhieuThuChiDTO::getSoTien)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Tính tổng chi từ danh sách đã load — duy trì backward compat với Presenter.
     * Ưu tiên dùng tinhTongThuChiTheoMaCa() cho hiệu năng tốt hơn.
     */
    public java.math.BigDecimal tinhTongChi(java.util.List<com.bakery.model.dto.hethong.PhieuThuChiDTO> ds) {
        return ds.stream()
                .filter(p -> "Chi".equals(p.getPhanLoai())
                        && !"cancelled".equalsIgnoreCase(p.getTrangThai()))
                .map(PhieuThuChiDTO::getSoTien)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * Task 2.3: Aggregate tại DB — không cần load danh sách phíu lên Java.
     * Dùng cho các trường hợp chỉ cần số tổng (Dashboard, Đối soát).
     *
     * @param maCa mã ca (0 = tất cả ca)
     * @return BigDecimal[2] = {tongThu, tongChi}
     */
    public java.math.BigDecimal[] tinhTongThuChiTheoMaCa(int maCa) throws Exception {
        return ptcDAO.tinhTongThuChi(maCa);
    }
}
