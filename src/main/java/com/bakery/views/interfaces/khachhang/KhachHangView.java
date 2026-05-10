package com.bakery.views.interfaces.khachhang;

import com.bakery.model.dto.banhang.DonDatHangDTO;
import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.model.dto.khachhang.KhachHangDTO;

import java.util.List;

public interface KhachHangView {

    void hienThiDanhSachKhachHang(List<KhachHangDTO> danhSach);

    void capNhatThongTinPhanTrang(String thongTin);

    void capNhatDieuKhienPhanTrang(int trangHienTai, int tongTrang);

    void capNhatTongKhachHang(int tongKhachHang);

    void capNhatKhachHangMoiTrongThang(int soKhachMoi);

    void batTatTrangThaiBan(boolean ban);

    void hienThiLoi(String tieuDe, String noiDung);

    void hienThiThanhCong(String tieuDe, String noiDung);

    void hienThiThongTin(String tieuDe, String noiDung);

    void capNhatCheDoThungRac(boolean cheDoThungRac);

    /** Hiển thị dialog lịch sử mua hàng của khách. */
    void hienThiLichSuMuaHang(KhachHangDTO kh, List<DonDatHangDTO> dsDon);

    /** Nạp danh sách hạng thành viên để tính điểm thăng hạng và điền ComboBox lọc. */
    default void hienThiDanhSachHang(List<HangThanhVienDTO> dsHang) {}
}
