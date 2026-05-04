package com.bakery.views.interfaces.hethong;

import com.bakery.views.interfaces.IBaseView;
import java.util.List;

public interface IMoCaView extends IBaseView {

    void setHoTen(String hoTen);

    void setPosOptions(List<String> options);


    void navigateToMain();

    void navigateToLogin();
}
