package com.bakery.views.interfaces.baocao;

import com.bakery.model.dto.baocao.BangDieuKhienKPIDTO;
import com.bakery.model.dto.baocao.TopSanPhamDTO;

import java.util.List;

public interface IBangDieuKhienView {

    void setCardDongCaVisible(boolean visible);

    void hienThiKPI(BangDieuKhienKPIDTO kpi);

    void hienThiLoiKPI();

    void setTop5Loading(boolean loading);

    void hienThiTop5(List<TopSanPhamDTO> ds);

    void hienThiLoiTop5();

    void hienThiDialogDongCa();
}
