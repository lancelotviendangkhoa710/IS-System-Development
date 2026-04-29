package com.bakery.views.interfaces;

import java.util.List;

public interface IMoCaView {

    void setHoTen(String hoTen);

    void setPosOptions(List<String> options);

    void hienThiLoi(String msg);

    void xoaLoi();

    void setLoading(boolean loading);

    void navigateToMain();

    void navigateToLogin();
}
