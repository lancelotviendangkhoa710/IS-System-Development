package com.bakery.model.dto.taichinh;

/**
 * DTO cho bảng LOAITHUCHI.
 * PHANLOAI phải là 'Thu' hoặc 'Chi'.
 */
public class LoaiThuChiDTO {

    private int    maLoaiThuChi;
    private String tenLoaiThuChi;
    private String phanLoai;        // 'Thu' | 'Chi'
    // Soft-delete fields (chỉ đọc, không gửi lên form)
    private java.time.LocalDateTime thoiDiemXoa;
    private Integer maNguoiXoa;

    public LoaiThuChiDTO() {}

    public LoaiThuChiDTO(int maLoaiThuChi, String tenLoaiThuChi, String phanLoai) {
        this.maLoaiThuChi  = maLoaiThuChi;
        this.tenLoaiThuChi = tenLoaiThuChi;
        this.phanLoai      = phanLoai;
    }

    // ── Getters / Setters ────────────────────────────────────────────────

    public int getMaLoaiThuChi()                      { return maLoaiThuChi; }
    public void setMaLoaiThuChi(int maLoaiThuChi)     { this.maLoaiThuChi = maLoaiThuChi; }

    public String getTenLoaiThuChi()                  { return tenLoaiThuChi; }
    public void setTenLoaiThuChi(String v)            { this.tenLoaiThuChi = v; }

    public String getPhanLoai()                       { return phanLoai; }
    public void setPhanLoai(String v)                 { this.phanLoai = v; }

    public java.time.LocalDateTime getThoiDiemXoa()   { return thoiDiemXoa; }
    public void setThoiDiemXoa(java.time.LocalDateTime v) { this.thoiDiemXoa = v; }

    public Integer getMaNguoiXoa()                    { return maNguoiXoa; }
    public void setMaNguoiXoa(Integer v)              { this.maNguoiXoa = v; }

    /** Hiển thị trong ComboBox. */
    @Override
    public String toString() {
        return tenLoaiThuChi;
    }
}
