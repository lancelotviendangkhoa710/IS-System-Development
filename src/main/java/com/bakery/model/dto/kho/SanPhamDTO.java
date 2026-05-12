package com.bakery.model.dto.kho;

import com.bakery.model.dto.BaseDTO;
import java.time.LocalDateTime;

public class SanPhamDTO extends BaseDTO {
    private int maSP;
    private int maDM;
    private String tenSP;
    private double giaVon;
    private double giaBan;
    private String hinhAnh;
    private int choPhepTuyChinh;
    private int thoiGianBaoQuan;
    private double soLuongTon;
    private int phienBan;
    private LocalDateTime thoiDiemXoa;
    private int thoiGianChuanBi;
    private Integer maNX;
    /** Chuỗi tên nguyên liệu LISTAGG từ DB — dùng cho Tooltip POS (không lưu DB). */
    private String thanhPhan;

    public SanPhamDTO() {
    }

    public SanPhamDTO(int maSP, int maDM, String tenSP, double giaVon, double giaBan,
            String hinhAnh, int choPhepTuyChinh, int thoiGianBaoQuan,
            double soLuongTon, int phienBan, LocalDateTime thoiDiemXoa,
            int thoiGianChuanBi, Integer maNX) {
        this.maSP = maSP;
        this.maDM = maDM;
        this.tenSP = tenSP;
        this.giaVon = giaVon;
        this.giaBan = giaBan;
        this.hinhAnh = hinhAnh;
        this.choPhepTuyChinh = choPhepTuyChinh;
        this.thoiGianBaoQuan = thoiGianBaoQuan;
        this.soLuongTon = soLuongTon;
        this.phienBan = phienBan;
        this.thoiDiemXoa = thoiDiemXoa;
        this.thoiGianChuanBi = thoiGianChuanBi;
        this.maNX = maNX;
    }

    public int getMaSP() {
        return maSP;
    }

    public void setMaSP(int maSP) {
        this.maSP = maSP;
    }

    public int getMaDM() {
        return maDM;
    }

    public void setMaDM(int maDM) {
        this.maDM = maDM;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGiaVon() {
        return giaVon;
    }

    public void setGiaVon(double giaVon) {
        this.giaVon = giaVon;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public int getChoPhepTuyChinh() {
        return choPhepTuyChinh;
    }

    public void setChoPhepTuyChinh(int choPhepTuyChinh) {
        this.choPhepTuyChinh = choPhepTuyChinh;
    }

    public int getThoiGianBaoQuan() {
        return thoiGianBaoQuan;
    }

    public void setThoiGianBaoQuan(int thoiGianBaoQuan) {
        this.thoiGianBaoQuan = thoiGianBaoQuan;
    }

    public double getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(double soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public int getPhienBan() {
        return phienBan;
    }

    public void setPhienBan(int phienBan) {
        this.phienBan = phienBan;
    }

    public LocalDateTime getThoiDiemXoa() {
        return thoiDiemXoa;
    }

    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) {
        this.thoiDiemXoa = thoiDiemXoa;
    }

    public int getThoiGianChuanBi() {
        return thoiGianChuanBi;
    }

    public void setThoiGianChuanBi(int thoiGianChuanBi) {
        this.thoiGianChuanBi = thoiGianChuanBi;
    }

    public Integer getMaNX() {
        return maNX;
    }

    public void setMaNX(Integer maNX) {
        this.maNX = maNX;
    }

    public String getThanhPhan() {
        return thanhPhan;
    }

    public void setThanhPhan(String thanhPhan) {
        this.thanhPhan = thanhPhan;
    }
}
