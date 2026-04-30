package com.bakery.views.interfaces;

import java.util.List;

public interface IMoCaView extends IBaseView {

    void setHoTen(String hoTen);

    void setPosOptions(List<String> options);


    void navigateToMain();

    void navigateToLogin();
}
