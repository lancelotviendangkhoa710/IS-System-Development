package com.bakery.model.dto.kho;

import java.time.LocalDateTime;

/**
 * DTO ánh xạ 1 dòng biến động trong thẻ kho nguyên liệu (UC44).
 * Mỗi record = 1 giao dịch nhập hoặc xuất liên quan đến 1 nguyên liệu.
 */
public class TheKhoBienDongDTO {

    private LocalDateTime ngayGiaoDich;
    /** "Nhập kho" hoặc lý do xuất từ PHIEUXUATKHO.LYDOXUAT */
    private String loaiGiaoDich;
    private int    maLo;
    private double soLuong;
    /** Dương = nhập, Âm = xuất (để tính tồn lũy tiến nếu cần). */
    private double soLuongConLai;

    public TheKhoBienDongDTO() {}

    public TheKhoBienDongDTO(LocalDateTime ngayGiaoDich, String loaiGiaoDich,
                             int maLo, double soLuong, double soLuongConLai) {
        this.ngayGiaoDich  = ngayGiaoDich;
        this.loaiGiaoDich  = loaiGiaoDich;
        this.maLo          = maLo;
        this.soLuong       = soLuong;
        this.soLuongConLai = soLuongConLai;
    }

    public LocalDateTime getNgayGiaoDich()  { return ngayGiaoDich; }
    public void setNgayGiaoDich(LocalDateTime v) { this.ngayGiaoDich = v; }

    public String getLoaiGiaoDich()         { return loaiGiaoDich; }
    public void setLoaiGiaoDich(String v)   { this.loaiGiaoDich = v; }

    public int getMaLo()                    { return maLo; }
    public void setMaLo(int v)             { this.maLo = v; }

    public double getSoLuong()              { return soLuong; }
    public void setSoLuong(double v)        { this.soLuong = v; }

    public double getSoLuongConLai()        { return soLuongConLai; }
    public void setSoLuongConLai(double v)  { this.soLuongConLai = v; }
}
