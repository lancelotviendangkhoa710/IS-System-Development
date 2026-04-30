package com.bakery.presenters;

import com.bakery.services.CaLamViecService;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.IMoCaView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Presenter cho màn hình Mở ca làm việc.
 */
public class MoCaPresenter {

    private static final List<String> DANH_SACH_POS = List.of("POS-01", "POS-02", "POS-03");

    private final IMoCaView view;
    private final CaLamViecService service;

    public MoCaPresenter(IMoCaView view, CaLamViecService service) {
        this.view    = view;
        this.service = service;
    }

    public void onInitialize() {
        view.setHoTen(SessionContext.getInstance().getHoTen());
        view.setPosOptions(DANH_SACH_POS);
    }

    public void onBatDauLamViecClicked(String mayPOS, String tienText) {
        view.xoaLoi();

        if (mayPOS == null || mayPOS.isBlank()) {
            view.hienThiLoi("⚠️ Vui lòng chọn máy POS.");
            return;
        }

        BigDecimal tienDauCa;
        try {
            tienDauCa = (tienText == null || tienText.isEmpty()) ? BigDecimal.ZERO : new BigDecimal(tienText);
        } catch (NumberFormatException ex) {
            view.hienThiLoi("⚠️ Số tiền không hợp lệ.");
            return;
        }

        int maNV = SessionContext.getInstance().getMaNV();

        view.setLoading(true);

        Thread t = new Thread(() -> {
            try {
                int maCa = service.moCa(maNV, mayPOS, tienDauCa);
                SessionContext.getInstance().moCa(maCa);
                view.setLoading(false);
                view.navigateToMain();
            } catch (Exception e) {
                view.setLoading(false);
                view.hienThiLoi("⚠️ " + (e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống khi mở ca."));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onDangXuatClicked() {
        SessionContext.getInstance().dangXuat();
        view.navigateToLogin();
    }
}
