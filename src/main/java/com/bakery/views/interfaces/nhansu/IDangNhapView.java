package com.bakery.views.interfaces.nhansu;

public interface IDangNhapView {

    void clearErrors();

    void showErrorTenDangNhap(String msg);

    void showErrorMatKhau(String msg);

    void showErrorChung(String msg);

    void setLoading(boolean loading);

    void navigateToMain();

    void navigateToMoCa();
}
