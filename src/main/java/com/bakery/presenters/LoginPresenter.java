package com.bakery.presenters;

import com.bakery.model.dto.CaLamViecDTO;
import com.bakery.model.dto.NhanVienDTO;
import com.bakery.services.CaLamViecService;
import com.bakery.services.NhanVienService;
import com.bakery.utils.SessionContext;
import com.bakery.views.interfaces.ILoginView;

public class LoginPresenter {

    private static final int CASHIER = 45;

    private final ILoginView view;
    private final NhanVienService nhanVienService;
    private final CaLamViecService caLamViecService;

    public LoginPresenter(ILoginView view,
                          NhanVienService nhanVienService,
                          CaLamViecService caLamViecService) {
        this.view             = view;
        this.nhanVienService  = nhanVienService;
        this.caLamViecService = caLamViecService;
    }

    public void onDangNhapClicked(String tenDangNhap, String matKhau) {
        view.clearErrors();

        boolean hopLe = true;
        if (tenDangNhap.isEmpty()) {
            view.showErrorTenDangNhap("⚠ Vui lòng nhập tên đăng nhập.");
            hopLe = false;
        }
        if (matKhau.isEmpty()) {
            view.showErrorMatKhau("⚠ Vui lòng nhập mật khẩu.");
            hopLe = false;
        }
        if (!hopLe) return;

        view.setLoading(true);

        Thread t = new Thread(() -> {
            try {
                NhanVienDTO nv = nhanVienService.dangNhap(tenDangNhap, matKhau);
                SessionContext.getInstance().dangNhap(nv.getMaNV(), nv.getHoTen(), nv.getMaVaiTro());
                if (nv.getMaVaiTro() == CASHIER) {
                    kiemTraCaVaChuyenHuong(nv.getMaNV());
                } else {
                    view.setLoading(false);
                    view.navigateToMain();
                }
            } catch (Exception e) {
                view.setLoading(false);
                view.showErrorChung("⚠ " + (e.getMessage() != null ? e.getMessage() : "Lỗi không xác định."));
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void kiemTraCaVaChuyenHuong(int maNV) {
        Thread t = new Thread(() -> {
            try {
                CaLamViecDTO ca = caLamViecService.layCaHienTai(maNV);
                view.setLoading(false);
                if (ca != null) {
                    SessionContext.getInstance().moCa(ca.getMaCa());
                    view.navigateToMain();
                } else {
                    view.navigateToMoCa();
                }
            } catch (Exception e) {
                view.setLoading(false);
                view.navigateToMoCa();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
