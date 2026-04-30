package com.bakery.presenters;

import com.bakery.model.dto.DoiSoatInfoDTO;
import com.bakery.services.DoiSoatService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.IDoiSoatDongCaView;

import java.math.BigDecimal;

public class DoiSoatDongCaPresenter extends BasePresenter<IDoiSoatDongCaView> {

    private final DoiSoatService service;

    private DoiSoatInfoDTO info;
    private BigDecimal tienThucTeDem;
    private BigDecimal chenhLech;

    public DoiSoatDongCaPresenter(IDoiSoatDongCaView view, DoiSoatService service) {
        super(view);
        this.service = service;
    }

    public void onInitialize() {
        int maCa = SessionContext.getInstance().getMaCa();
        int maNV = SessionContext.getInstance().getMaNV();

        runTask(
            () -> service.layThongTinDoiSoat(maCa, maNV),
            infoResult -> {
                this.info = infoResult;
                String maCaHienThi = "CA_" + String.format("%06d", info.maCa());
                view.hienThiThongTinCa(
                        maCaHienThi,
                        info.maMayPOS(),
                        CurrencyFormatter.format(info.tienKhaiBaoDauCa()),
                        CurrencyFormatter.format(info.doanhThuPhatSinh())
                );
                view.resizeDialog();
            }
        );
    }

    public void onKiemTraClicked(String input) {
        if (input.isEmpty()) {
            view.hienThiLoiNhapTrong();
            return;
        }

        try {
            tienThucTeDem = CurrencyFormatter.parse(input);
        } catch (NumberFormatException ex) {
            view.hienThiLoiNhapSaiDinhDang();
            return;
        }

        chenhLech = service.tinhChenhLech(tienThucTeDem);
        boolean khop = chenhLech.compareTo(BigDecimal.ZERO) == 0;

        view.chuyenSangSauKiemTra(khop, tienThucTeDem.toPlainString());

        if (!khop) {
            view.setNutKhoaSoEnabled(false);
        } else {
            view.setNutKhoaSoEnabled(true);
        }

        view.resizeDialog();
    }

    public void onLyDoChanged(String lyDo) {
        view.setNutKhoaSoEnabled(!lyDo.isBlank());
    }

    public void onSuaLaiClicked() {
        view.chuyenVeNhapTien();
        view.resizeDialog();
    }

    public void onKhoaSoClicked(String lyDoInput) {
        String lyDo = (chenhLech.compareTo(BigDecimal.ZERO) != 0) ? lyDoInput : null;
        int maCa = SessionContext.getInstance().getMaCa();

        runTask(
            () -> {
                service.dongCaDoiSoat(maCa, tienThucTeDem, lyDo);
                return info.tienKhaiBaoDauCa().add(info.doanhThuPhatSinh());
            },
            tienHeThong -> {
                view.hienThiKetQua(tienThucTeDem, tienHeThong, chenhLech, lyDo);
                view.resizeDialog();
            }
        );
    }

    public void onHuyBoClicked() {
        view.dongDialog();
    }

    public void onHoanTatClicked() {
        view.dongDialog();
        view.navigateToLogin();
    }
}
