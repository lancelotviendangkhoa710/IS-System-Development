package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.CongThucDTO;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.model.dto.kho.SanPhamDTO;

import java.util.List;

/** Interface View cho màn hình Quản lý Công thức nguyên liệu (Tab 3). */
public interface ICongThucView {
    void hienThiDanhSachCongThuc(List<CongThucDTO> ds);
    void hienThiDanhSachNguyenLieu(List<NguyenLieuDTO> dsNL);
    void hienThiDanhSachSanPham(List<SanPhamDTO> dsSP);
    void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT);
    void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC);
    void hienThiChiTiet(CongThucDTO ct);
    void hienThiLoi(String thongBao);
    void hienThiThanhCong(String thongBao);
    void lamMoiForm();
    CongThucDTO getSelectedCongThuc();
}
