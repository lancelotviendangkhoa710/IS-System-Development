package com.bakery.presenters.hethong;

import com.bakery.presenters.BasePresenter;
import com.bakery.services.hethong.GiamSatCaService;
import com.bakery.views.interfaces.hethong.IGiamSatCaView;

/** Presenter cho màn hình Giám sát tiền mặt đóng ca. */
public class GiamSatCaPresenter extends BasePresenter<IGiamSatCaView> {

    private final GiamSatCaService service;

    public GiamSatCaPresenter(IGiamSatCaView view, GiamSatCaService service) {
        super(view);
        this.service = service;
    }

    public void onInitialize() {
        view.setLoading(true);
        runTask(
            () -> service.layLichSuCa(),
            danhSach -> {
                view.setLoading(false);
                view.hienThiDanhSachCa(danhSach);
            }
        );
    }

    public void onRefresh() {
        onInitialize();
    }
}
