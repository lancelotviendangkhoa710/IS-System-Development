package com.bakery.views.interfaces;

import com.bakery.model.dto.DashboardKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;

import java.util.List;

public interface IDashboardView {

    void setCardDongCaVisible(boolean visible);

    void hienThiKPI(DashboardKPIDTO kpi);

    void hienThiLoiKPI();

    void setTop5Loading(boolean loading);

    void hienThiTop5(List<TopSanPhamDTO> ds);

    void hienThiLoiTop5();

    void hienThiDialogDongCa();
}
