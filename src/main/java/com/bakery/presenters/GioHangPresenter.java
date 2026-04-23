package com.bakery.presenters;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.YeuCauChiTietDonHangDTO;
import com.bakery.model.dto.YeuCauChiTietDonTuyChinhDTO;
import com.bakery.services.SanPhamService;
import com.bakery.views.interfaces.IOrderView;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter duy nhất chịu trách nhiệm quản lý Giỏ hàng POS:
 * Thêm, xóa, thay đổi số lượng sản phẩm trong giỏ và cập nhật giao diện.
 * (SRP – Single Responsibility Principle)
 */
public class GioHangPresenter {

    private final IOrderView view;
    private final SanPhamService sanPhamService;

    // Trạng thái nội bộ giỏ hàng
    private final List<YeuCauChiTietDonHangDTO> gioHangItems = new ArrayList<>();
    private List<SanPhamDTO> tatCaSanPham = new ArrayList<>();
    private double phanTramGiamGia = 0.0;

    public GioHangPresenter(IOrderView view, SanPhamService sanPhamService) {
        this.view = view;
        this.sanPhamService = sanPhamService;
    }

    // =========================================================
    // 1. THÊM SẢN PHẨM VÀO GIỎ HÀNG
    // =========================================================

    /** Thêm sản phẩm tiêu chuẩn vào giỏ. Nếu đã có thì tăng số lượng. */
    public void themSanPhamVaoGio(SanPhamDTO sp) {
        YeuCauChiTietDonHangDTO existed = gioHangItems.stream()
                .filter(i -> i.getMaSP() == sp.getMaSP() && !i.isCustom())
                .findFirst().orElse(null);

        if (existed != null) {
            existed.setSoLuong(existed.getSoLuong() + 1);
        } else {
            YeuCauChiTietDonHangDTO newItem = new YeuCauChiTietDonHangDTO();
            newItem.setMaSP(sp.getMaSP());
            newItem.setSoLuong(1);
            newItem.setDonGia(sp.getGiaCoBan());
            newItem.setCustom(false);
            gioHangItems.add(newItem);
        }
        capNhatGioHangVaTien();
    }

    /** Thêm bánh tùy chỉnh (có phụ phí) vào giỏ hàng. */
    public void themBanhTuyChinhVaoGio(SanPhamDTO sp, int soLuong, double donGia,
            Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri,
            String loiChuc, String ghiChu) {
        YeuCauChiTietDonTuyChinhDTO newItem = new YeuCauChiTietDonTuyChinhDTO(
                sp.getMaSP(), soLuong, donGia, "", "",
                maKC, maCot, maNhan, maTrangTri, loiChuc, ghiChu);
        gioHangItems.add(newItem);
        capNhatGioHangVaTien();
    }

    // =========================================================
    // 2. THAY ĐỔI SỐ LƯỢNG / XÓA MÓN
    // =========================================================

    /**
     * Thay đổi số lượng món theo chỉ số trong danh sách.
     * change = 0: xóa hẳn; change > 0: tăng; change < 0: giảm.
     */
    public void thayDoiSoLuongMon(int index, int change) {
        if (index < 0 || index >= gioHangItems.size())
            return;

        YeuCauChiTietDonHangDTO item = gioHangItems.get(index);
        if (change == 0) {
            gioHangItems.remove(index);
        } else {
            item.setSoLuong(item.getSoLuong() + change);
            if (item.getSoLuong() <= 0) {
                gioHangItems.remove(index);
            }
        }
        capNhatGioHangVaTien();
    }

    // =========================================================
    // 3. CẬP NHẬT GIAO DIỆN GIỎ HÀNG VÀ SỐ TIỀN
    // =========================================================

    /** Tính lại toàn bộ tổng tiền và đẩy lên View. */
    public void capNhatGioHangVaTien() {
        double tongTienHang = gioHangItems.stream()
                .mapToDouble(i -> i.getDonGia() * i.getSoLuong()).sum();
        double tienGiamGia = tongTienHang * phanTramGiamGia;
        double tongTienPhaiTra = tongTienHang - tienGiamGia;
        double tienKhachDua = view.getTienKhachDua();
        double tienCocNhapVao = view.getTienCoc();

        double soTienGhiNhan = (tienCocNhapVao > 0)
                ? tienCocNhapVao
                : Math.min(tongTienPhaiTra, tienKhachDua);
        double conLai = Math.max(0, tongTienPhaiTra - soTienGhiNhan);
        double tienThua = Math.max(0, tienKhachDua - soTienGhiNhan);

        view.lamMoiBaoCaoTien(tongTienHang, tienGiamGia, tongTienPhaiTra,
                tongTienPhaiTra * 0.5, conLai, tienThua, tienKhachDua < soTienGhiNhan);
        view.lamMoiBangGioHang(convertToCTDonHangList(gioHangItems), tatCaSanPham);
        view.batTatNutThanhToan(!gioHangItems.isEmpty());
    }

    // =========================================================
    // 4. TÍNH GIÁ BÁNH TÙY CHỈNH (Delegate xuống Service)
    // =========================================================

    public double tinhGiaBanhTuyChinh(int maSP, Integer maKC, Integer maCot,
            Integer maNhan, Integer maTrangTri) {
        return sanPhamService.tinhGiaBanhTuyChinh(maSP, maKC, maCot, maNhan, maTrangTri);
    }

    // =========================================================
    // GETTER / SETTER dùng bởi các Presenter khác
    // =========================================================

    public List<YeuCauChiTietDonHangDTO> getGioHangItems() {
        return new ArrayList<>(gioHangItems);
    }

    public void setTatCaSanPham(List<SanPhamDTO> tatCaSanPham) {
        this.tatCaSanPham = tatCaSanPham;
    }

    public void setPhanTramGiamGia(double phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    /** Xóa toàn bộ giỏ hàng sau khi thanh toán xong. */
    public void xoaGioHang() {
        gioHangItems.clear();
    }

    // =========================================================
    // PRIVATE – HỖ TRỢ NỘI BỘ
    // =========================================================

    private List<CTDonHangDTO> convertToCTDonHangList(List<YeuCauChiTietDonHangDTO> items) {
        List<CTDonHangDTO> list = new ArrayList<>();
        for (YeuCauChiTietDonHangDTO item : items) {
            CTDonHangDTO dto = new CTDonHangDTO();
            dto.setMaSP(item.getMaSP());
            dto.setSoLuong(item.getSoLuong());
            dto.setDonGia(item.getDonGia());
            list.add(dto);
        }
        return list;
    }
}
