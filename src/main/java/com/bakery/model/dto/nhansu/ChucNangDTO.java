package com.bakery.model.dto.nhansu;

import com.bakery.model.enums.SystemModule;

public class ChucNangDTO {
    private int maChucNang;
    private String tenChucNang;
    private String moTa;
    private SystemModule module;

    // Chi tiết quyền (Flags)
    private boolean canView;
    private boolean canAdd;
    private boolean canEdit;
    private boolean canDelete;
    private boolean canDownload;

    public ChucNangDTO() {}

    public ChucNangDTO(int maChucNang, String tenChucNang, String moTa, SystemModule module) {
        this.maChucNang = maChucNang;
        this.tenChucNang = tenChucNang;
        this.moTa = moTa;
        this.module = module;
    }

    public int getMaChucNang() { return maChucNang; }
    public void setMaChucNang(int maChucNang) { this.maChucNang = maChucNang; }

    public String getTenChucNang() { return tenChucNang; }
    public void setTenChucNang(String tenChucNang) { this.tenChucNang = tenChucNang; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public SystemModule getModule() { return module; }
    public void setModule(SystemModule module) { this.module = module; }

    public boolean isCanView() { return canView; }
    public void setCanView(boolean canView) { this.canView = canView; }

    public boolean isCanAdd() { return canAdd; }
    public void setCanAdd(boolean canAdd) { this.canAdd = canAdd; }

    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }

    public boolean isCanDelete() { return canDelete; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }

    public boolean isCanDownload() { return canDownload; }
    public void setCanDownload(boolean canDownload) { this.canDownload = canDownload; }
}
