package com.bakery.presenters;

import com.bakery.model.dto.DoiSoatInfoDTO;
import com.bakery.services.DoiSoatService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.IDoiSoatDongCaView;

import java.math.BigDecimal;

public class DoiSoatDongCaPresenter {

    private final IDoiSoatDongCaView view;
    private final DoiSoatService service;

    private DoiSoatInfoDTO info;
    private BigDecimal tienThucTeDem;
    private BigDecimal chenhLech;

    public DoiSoatDongCaPresenter(IDoiSoatDongCaView view, DoiSoatService service) {
        this.view    = view;
        this.service = service;
    }

    public void onInitialize() {
        int maCa = SessionContext.getInstance().getMaCa();
        int maNV = SessionContext.getInstance().getMaNV();

        Thread t = new Thread(() -> {
            try {
                info = service.layThongTinDoiSoat(maCa, maNV);
                String maCaHienThi = "CA_" + String.format("%06d", info.maCa());
                view.hienThiThongTinCa(
                        maCaHienThi,
                        info.maMayPOS(),
                        CurrencyFormatter.format(info.tienKhaiBaoDauCa()),
                        CurrencyFormatter.format(info.doanhThuPhatSinh())
                );
            } catch (Exception e) {
                String causeMsg = e.getMessage() != null ? e.getMessage() : "unknown";
                if (e.getCause() != null) causeMsg += " | Cause: " + e.getCause().getMessage();
                System.err.println("[DoiSoat] Lỗi tải thông tin ca: " + causeMsg);
                e.printStackTrace();
                String maCaHienThi = "CA_" + String.format("%06d", maCa);
                view.hienThiLoiTaiCa(maCaHienThi, causeMsg);
            } finally {
                view.setLoading(false);
                view.resizeDialog();
            }
        });
        t.setDaemon(true);
        t.start();
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

        view.setNutKhoaSoDangXuLy(true);

        Thread t = new Thread(() -> {
            try {
                service.dongCaDoiSoat(maCa, tienThucTeDem, lyDo);
                BigDecimal tienHeThonh = info.tienKhaiBaoDauCa().add(info.doanhThuPhatSinh());
                view.hienThiKetQua(tienThucTeDem, tienHeThonh, chenhLech, lyDo);
                view.resizeDialog();
            } catch (Exception e) {
                view.setNutKhoaSoDangXuLy(false);
                System.err.println("Khóa sổ lỗi: " + e.getMessage());
                view.hienThiLoiKhoaSo(e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void onHuyBoClicked() {
        view.dongDialog();
    }

    public void onHoanTatClicked() {
        view.dongDialog();
        view.navigateToLogin();
    }
}
