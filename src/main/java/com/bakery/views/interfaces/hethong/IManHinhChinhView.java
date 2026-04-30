package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.ModuleDef;

import java.util.List;

public interface IManHinhChinhView {

    void setHoTen(String hoTen);

    void setVaiTro(String tenVaiTro);

    void setAvatar(String kyTu);

    void buildMenu(List<ModuleDef> modules);

    void navigateToLogin();
}
