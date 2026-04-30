package com.bakery.model.dto.banhang;

public class YeuCauChiTietDonTuyChinhDTO extends YeuCauChiTietDonHangDTO {
    private Integer maKC;
    private Integer maCot;
    private Integer maNhan;
    private Integer maTrangTri;
    private String loiChucTrenBanh;
    private String ghiChuThoBanh;

    public YeuCauChiTietDonTuyChinhDTO() {
        super();
        this.setCustom(true);
    }

    public YeuCauChiTietDonTuyChinhDTO(int maSP, int soLuong, double donGia, String ghiChu, String phuKien, 
                                       Integer maKC, Integer maCot, Integer maNhan, Integer maTrangTri, 
                                       String loiChucTrenBanh, String ghiChuThoBanh) {
        super(maSP, soLuong, donGia, true, ghiChu, phuKien);
        this.maKC = maKC;
        this.maCot = maCot;
        this.maNhan = maNhan;
        this.maTrangTri = maTrangTri;
        this.loiChucTrenBanh = loiChucTrenBanh;
        this.ghiChuThoBanh = ghiChuThoBanh;
    }

    public Integer getMaKC() {
        return maKC;
    }

    public void setMaKC(Integer maKC) {
        this.maKC = maKC;
    }

    public Integer getMaCot() {
        return maCot;
    }

    public void setMaCot(Integer maCot) {
        this.maCot = maCot;
    }

    public Integer getMaNhan() {
        return maNhan;
    }

    public void setMaNhan(Integer maNhan) {
        this.maNhan = maNhan;
    }

    public Integer getMaTrangTri() {
        return maTrangTri;
    }

    public void setMaTrangTri(Integer maTrangTri) {
        this.maTrangTri = maTrangTri;
    }

    public String getLoiChucTrenBanh() {
        return loiChucTrenBanh;
    }

    public void setLoiChucTrenBanh(String loiChucTrenBanh) {
        this.loiChucTrenBanh = loiChucTrenBanh;
    }

    public String getGhiChuThoBanh() {
        return ghiChuThoBanh;
    }

    public void setGhiChuThoBanh(String ghiChuThoBanh) {
        this.ghiChuThoBanh = ghiChuThoBanh;
    }
}
