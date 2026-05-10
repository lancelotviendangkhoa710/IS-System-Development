package com.bakery.model.dto.hethong;

import java.time.LocalDateTime;

/** DTO ánh xạ từ VW_HoatDongNhanVien — dùng cho màn hình Lịch sử hệ thống. */
public class HoatDongNhanVienDTO {

    private int    maHoatDong;
    private int    maNV;
    private String tenNhanVien;
    private String chucVu;
    private String nhom;
    private String hanhDong;
    private Integer entityId;
    private LocalDateTime thoiGian;

    public HoatDongNhanVienDTO() {}

    public HoatDongNhanVienDTO(int maHoatDong, int maNV, String tenNhanVien,
                                String chucVu, String nhom, String hanhDong,
                                Integer entityId, LocalDateTime thoiGian) {
        this.maHoatDong  = maHoatDong;
        this.maNV        = maNV;
        this.tenNhanVien = tenNhanVien;
        this.chucVu      = chucVu;
        this.nhom        = nhom;
        this.hanhDong    = hanhDong;
        this.entityId    = entityId;
        this.thoiGian    = thoiGian;
    }

    public int     getMaHoatDong()  { return maHoatDong; }
    public int     getMaNV()        { return maNV; }
    public String  getTenNhanVien() { return tenNhanVien; }
    public String  getChucVu()      { return chucVu; }
    public String  getNhom()        { return nhom; }
    public String  getHanhDong()    { return hanhDong; }
    public Integer getEntityId()    { return entityId; }
    public LocalDateTime getThoiGian() { return thoiGian; }

    public void setMaHoatDong(int maHoatDong)    { this.maHoatDong = maHoatDong; }
    public void setMaNV(int maNV)                { this.maNV = maNV; }
    public void setTenNhanVien(String v)         { this.tenNhanVien = v; }
    public void setChucVu(String v)              { this.chucVu = v; }
    public void setNhom(String v)                { this.nhom = v; }
    public void setHanhDong(String v)            { this.hanhDong = v; }
    public void setEntityId(Integer v)           { this.entityId = v; }
    public void setThoiGian(LocalDateTime v)     { this.thoiGian = v; }
}
