package com.bakery.views.interfaces;

public interface ILoginView {

    void clearErrors();

    void showErrorTenDangNhap(String msg);

    void showErrorMatKhau(String msg);

    void showErrorChung(String msg);

    void setLoading(boolean loading);

    void navigateToMain();

    void navigateToMoCa();
}
