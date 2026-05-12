package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.model.dto.kho.SanPhamDTO;

import java.util.List;

/** View interface cho màn hình Cấu hình giới hạn nhận đơn. */
public interface ICauHinhGioiHanView {
    void hienThiDanhSachCauHinh(List<CauHinhGioiHanDTO> dsCauHinh);
    void hienThiThongBao(String msg);
    void hienThiLoi(String msg);
    void lamMoiForm();

    /** Task 5: Nhận danh sách sản phẩm bán lẻ để populate ComboBox. */
    void napDanhSachSanPhamBanLe(List<SanPhamDTO> dsSanPham);
}
