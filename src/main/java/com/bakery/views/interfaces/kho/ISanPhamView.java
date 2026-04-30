package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import java.util.List;
import java.util.Map;

public interface ISanPhamView {
    void hienThiDanhSachSanPham(List<SanPhamDTO> dsSanPham);
    void hienThiDanhSachDanhMuc(Map<Integer, String> danhMucMap);
    void hienThiChiTiet(SanPhamDTO sp);
    void hienThiLoi(String thongBao);
    void hienThiThanhCong(String thongBao);
    void lamMoiForm();
    
    SanPhamDTO getSelectedSanPham();
    SanPhamDTO layDuLieuTuForm();
}
