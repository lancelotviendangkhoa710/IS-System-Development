package com.bakery.model.dto;

public class DoiSoatDTO {
    private int maCa;
    private double tienKhaiBaoDauCa;
    private double tongTienHeThong;
    private double tienThucTeDem;
    private double chenhLech;
    private String lyDoChenhLech;

    public DoiSoatDTO() {}

    public int getMaCa() { return maCa; }
    public void setMaCa(int maCa) { this.maCa = maCa; }

    public double getTienKhaiBaoDauCa() { return tienKhaiBaoDauCa; }
    public void setTienKhaiBaoDauCa(double tienKhaiBaoDauCa) { this.tienKhaiBaoDauCa = tienKhaiBaoDauCa; }

    public double getTongTienHeThong() { return tongTienHeThong; }
    public void setTongTienHeThong(double tongTienHeThong) { this.tongTienHeThong = tongTienHeThong; }

    public double getTienThucTeDem() { return tienThucTeDem; }
    public void setTienThucTeDem(double tienThucTeDem) { this.tienThucTeDem = tienThucTeDem; }

    public double getChenhLech() { return chenhLech; }
    public void setChenhLech(double chenhLech) { this.chenhLech = chenhLech; }

    public String getLyDoChenhLech() { return lyDoChenhLech; }
    public void setLyDoChenhLech(String lyDoChenhLech) { this.lyDoChenhLech = lyDoChenhLech; }
}