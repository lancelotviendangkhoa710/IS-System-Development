package org.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Xe {
    private String tenChuXe;
    private String loaiXe;
    private double triGiaXe;
    private double dungTichXiLanh;

    public Xe() {
        this.tenChuXe = "";
        this.loaiXe = "";
        this.triGiaXe = 0;
        this.dungTichXiLanh = 0;
        System.out.println("Khởi tạo thành công đối tượng xe với các thông số mặc định");
    };

    public Xe(String tenChuXe, String loaiXe, double triGiaXe, double dungTichXiLanh)
    {
        this.tenChuXe = tenChuXe;
        this.loaiXe = loaiXe;
        this.triGiaXe = triGiaXe;
        this.dungTichXiLanh = dungTichXiLanh;
    };

    public String getTenChuXe() { return tenChuXe; };
    public String getLoaiXe() { return loaiXe; };
    public double getTriGiaXe() { return triGiaXe; };
    public double getDungTichXiLanh() { return dungTichXiLanh; };

    public void setTenChuXe(String tenCX) { this.tenChuXe = tenCX; };
    public void setLoaiXe(String loaiXe) { this.loaiXe = loaiXe; };
    public void setTriGiaXe(double triGiaXe) { this.triGiaXe = triGiaXe; };
    public void setDungTichXiLanh(double dungTichXiLanh) { this.dungTichXiLanh = dungTichXiLanh; };

    public double tinhMucThue() {
        if (dungTichXiLanh < 100)
            return triGiaXe * 0.01;
        else if (dungTichXiLanh <= 175)
            return triGiaXe * 0.03;
        else
            return triGiaXe * 0.05;
    }

    public void xuatThongTin() {
        System.out.println("Tên chủ xe: " + tenChuXe);
        System.out.println("Loại xe: " + loaiXe);
        DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.getDefault()));
        System.out.println("Trị giá xe: " + df.format(triGiaXe));
        System.out.println("Dung tích xi lanh: " + dungTichXiLanh);
        System.out.println("Mức thuế phải đóng: " + tinhMucThue());
    };

    public void xuatThueXe() {
        DecimalFormat df = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.getDefault()));
        System.out.println("Mức thuế phải đóng của chủ xe " + tenChuXe +
                " với loại xe " + loaiXe + " là: " + tinhMucThue());
    };

}
