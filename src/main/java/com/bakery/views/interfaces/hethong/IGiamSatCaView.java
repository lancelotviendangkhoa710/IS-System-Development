package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.GiamSatCaDTO;
import com.bakery.views.interfaces.IBaseView;

import java.util.List;

/** View interface cho màn hình Giám sát tiền mặt đóng ca (chỉ đọc). */
public interface IGiamSatCaView extends IBaseView {
    void hienThiDanhSachCa(List<GiamSatCaDTO> danhSach);
    void hienThiLoi(String msg);
    void setLoading(boolean loading);
}
