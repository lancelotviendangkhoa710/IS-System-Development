package com.bakery.presenters.hethong;

import com.bakery.services.hethong.CaLamViecService;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.hethong.IMoCaView;

import java.math.BigDecimal;
import java.util.List;

/**
 * Presenter cho màn hình Mở ca làm việc.
 */
public class MoCaPresenter extends BasePresenter<IMoCaView> {

    private static final List<String> DANH_SACH_POS = List.of("POS-01", "POS-02", "POS-03");

    private final CaLamViecService service;

    public MoCaPresenter(IMoCaView view, CaLamViecService service) {
        super(view);
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

        runTask(
            () -> service.moCa(maNV, mayPOS, tienDauCa),
            maCa -> {
                SessionContext.getInstance().moCa(maCa);
                view.navigateToMain();
            }
        );
    }

    public void onDangXuatClicked() {
        SessionContext.getInstance().dangXuat();
        view.navigateToLogin();
    }
}
