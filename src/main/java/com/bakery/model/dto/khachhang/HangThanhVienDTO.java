package com.bakery.model.dto.khachhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HangThanhVienDTO {
    private int maHang;
    private String tenHang;
    private Integer diemToiThieu;
    private BigDecimal phanTramGiamGia;
    private LocalDateTime thoiDiemXoa;
    private Integer maNX;

    public HangThanhVienDTO() {}

    public HangThanhVienDTO(int maHang, String tenHang, Integer diemToiThieu, BigDecimal phanTramGiamGia, LocalDateTime thoiDiemXoa, Integer maNX) {
        this.maHang = maHang;
        this.tenHang = tenHang;
        this.diemToiThieu = diemToiThieu;
        this.phanTramGiamGia = phanTramGiamGia;
        this.thoiDiemXoa = thoiDiemXoa;
        this.maNX = maNX;
    }

    public int getMaHang() { return maHang; }
    public void setMaHang(int maHang) { this.maHang = maHang; }

    public String getTenHang() { return tenHang; }
    public void setTenHang(String tenHang) { this.tenHang = tenHang; }

    public Integer getDiemToiThieu() { return diemToiThieu; }
    public void setDiemToiThieu(Integer diemToiThieu) { this.diemToiThieu = diemToiThieu; }

    public BigDecimal getPhanTramGiamGia() { return phanTramGiamGia; }
    public void setPhanTramGiamGia(BigDecimal phanTramGiamGia) { this.phanTramGiamGia = phanTramGiamGia; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public Integer getMaNX() { return maNX; }
    public void setMaNX(Integer maNX) { this.maNX = maNX; }
}
