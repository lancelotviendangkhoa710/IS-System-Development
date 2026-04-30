package com.bakery.services;

import com.bakery.model.dao.LoaiThuChiDAO;
import com.bakery.model.dao.PhieuThuChiDAO;
import com.bakery.model.dto.LoaiThuChiDTO;
import com.bakery.model.dto.PhieuThuChiDTO;

import java.math.BigDecimal;
import java.util.List;

public class SoQuyService {

    private final PhieuThuChiDAO ptcDAO = new PhieuThuChiDAO();
    private final LoaiThuChiDAO  ltcDAO = new LoaiThuChiDAO();

    public List<PhieuThuChiDTO> layGiaoDich(int maCa) {
        return ptcDAO.layTheoMaCa(maCa);
    }

    public void huyGiaoDich(int maPhieuTC, String lyDo) {
        ptcDAO.huyPhieu(maPhieuTC, lyDo);
    }

    public void themGiaoDich(int maCa, int maNV, int maLoaiThuChi,
                             BigDecimal soTien, String ghiChu) {
        PhieuThuChiDTO dto = new PhieuThuChiDTO();
        dto.setMaCa(maCa);
        dto.setMaNV(maNV);
        dto.setMaLoaiThuChi(maLoaiThuChi);
        dto.setSoTien(soTien);
        dto.setGhiChu(ghiChu);
        ptcDAO.taoPhieuThuChi(dto);
    }

    /** Chỉ loại đang hoạt động — dùng cho ComboBox lập phiếu. */
    public List<LoaiThuChiDTO> layDanhSachLoai() {
        return ltcDAO.layDanhSach();
    }

    /** Tất cả loại kể cả đã khoá — dùng cho tab Cấu hình. */
    public List<LoaiThuChiDTO> layTatCaDanhSachLoai() {
        return ltcDAO.layTatCa();
    }

    public void moKhoaLoai(int ma) {
        ltcDAO.moKhoa(ma);
    }

    public void themLoai(String ten, String phanLoai) {
        ltcDAO.them(ten, phanLoai);
    }

    public void suaLoai(int ma, String ten, String phanLoai) {
        ltcDAO.sua(ma, ten, phanLoai);
    }

    public void xoaLoai(int ma) {
        ltcDAO.xoa(ma);
    }

    public BigDecimal tinhTongThu(List<PhieuThuChiDTO> ds) {
        return ds.stream()
                .filter(p -> "Thu".equals(p.getPhanLoai())
                        && !"cancelled".equals(p.getTrangThai()))
                .map(PhieuThuChiDTO::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal tinhTongChi(List<PhieuThuChiDTO> ds) {
        return ds.stream()
                .filter(p -> "Chi".equals(p.getPhanLoai())
                        && !"cancelled".equals(p.getTrangThai()))
                .map(PhieuThuChiDTO::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
