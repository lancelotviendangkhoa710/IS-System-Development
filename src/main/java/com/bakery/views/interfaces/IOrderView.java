package com.bakery.views.interfaces;

import com.bakery.model.dto.CTDonHangDTO;
import com.bakery.model.dto.DonDatHangDTO;
import com.bakery.model.dto.SanPhamDTO;
import com.bakery.model.dto.KichCoBanhDTO;
import com.bakery.model.dto.CotBanhDTO;
import com.bakery.model.dto.NhanBanhDTO;
import com.bakery.model.dto.KieuTrangTriDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IOrderView {
    double getTienKhachDua();

    double getTienCoc();

    double getTongThanhToanHienTai();

    String getDiaChiGiao();

    String getSoDienThoai();

    Integer getHinhThucNhan();

    boolean isXacNhanThuTien();

    String getTrangThaiHienTaiTraCuu();

    LocalDateTime getNgayGioNhanBanh();

    void hienThiThongTinKhach(String text, boolean isVip);

    void lamMoiBaoCaoTien(double tongHang, double giamGia, double tongThanhToan, double minCoc, double conLai, double tienThua, boolean isThieuTienThua);

    void lamMoiBangGioHang(List<CTDonHangDTO> items, List<SanPhamDTO> originData);

    void batTatNutThanhToan(boolean state);

    void hienThiLoi(String msg);

    void hienThiThanhCong(String msg);

    void hienThiLoiTraCuu(String msg);

    void hienThiThongBaoTraCuu(String msg);

    void hienThiKetQuaTraCuu(String kh, String tt, double tongTien);

    void lamMoiForm();

    void hienThiDanhSachSanPham(List<SanPhamDTO> ds, Map<Integer, String> dict);

    void taiDanhSachTrangThai(List<String> list);
    
    void hienThiDuLieuTuyChinh(List<SanPhamDTO> spTuyChinh, List<KichCoBanhDTO> kichCo, List<CotBanhDTO> cotBanh, List<NhanBanhDTO> nhanBanh, List<KieuTrangTriDTO> trangTri);

    void hienThiDanhSachDonTheoDoi(List<DonDatHangDTO> dsDonTheoDoi);

    void inPhieuHoaDon(String tieuDe, Integer maDon, Integer maHoaDon, LocalDateTime ngayLapHoaDon,
                       double tongTien, double daThu, List<CTDonHangDTO> cart, List<SanPhamDTO> data, double pGiam);

    void showOrderDetails(DonDatHangDTO order);

    void showError(String msg);
}
