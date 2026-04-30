package com.bakery.presenters;

import com.bakery.model.dto.BangDieuKhienKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;
import com.bakery.services.BangDieuKhienService;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.IBangDieuKhienView;

import java.util.List;

public class BangDieuKhienPresenter {

    private static final int CASHIER = 45;

    private final IBangDieuKhienView view;
    private final BangDieuKhienService service;

    public BangDieuKhienPresenter(IBangDieuKhienView view, BangDieuKhienService service) {
        this.view    = view;
        this.service = service;
    }

    public void onInitialize() {
        boolean laCashier = SessionContext.getInstance().getMaVaiTro() == CASHIER;
        view.setCardDongCaVisible(laCashier);
        taiKPI();
        taiTop5();
    }

    private void taiKPI() {
        Thread t = new Thread(() -> {
            try {
                BangDieuKhienKPIDTO kpi = service.layKPI();
                view.hienThiKPI(kpi);
            } catch (Exception e) {
                System.err.println("Dashboard KPI error: " + e.getMessage());
                view.hienThiLoiKPI();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void taiTop5() {
        Thread t = new Thread(() -> {
            try {
                List<TopSanPhamDTO> ds = service.layTop5SanPhamThang();
                view.setTop5Loading(false);
                view.hienThiTop5(ds);
            } catch (Exception e) {
                System.err.println("Dashboard Top5 error: " + e.getMessage());
                view.setTop5Loading(false);
                view.hienThiLoiTop5();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onThucHienDongCa() {
        view.hienThiDialogDongCa();
    }
}
