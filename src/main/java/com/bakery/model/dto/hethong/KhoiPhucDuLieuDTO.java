package com.bakery.model.dto.hethong;

import java.time.LocalDateTime;

/**
 * DTO đại diện cho một bản ghi đã bị xóa mềm (soft-delete) trong hệ thống.
 * Dùng cho màn hình Khôi phục dữ liệu (UC60).
 */
public class KhoiPhucDuLieuDTO {

    /** Mã định danh của bản ghi (cột Primary Key). */
    private String maDoiTuong;

    /** Tên hiển thị của bản ghi (tên sản phẩm, tên NL...). */
    private String tenDoiTuong;

    /** Loại đối tượng (dùng để hiển thị nhóm). */
    private String loaiDoiTuong;

    /** Tên bảng Oracle (dùng nội bộ cho Procedure). */
    private String tenBang;

    /** Tên cột PK trong bảng Oracle (dùng nội bộ cho Procedure). */
    private String tenCotXoa;

    /** Thời điểm bị xóa mềm. */
    private LocalDateTime thoiDiemXoa;

    /** Tên nhân viên đã xóa (lấy qua JOIN NX). */
    private String tenNhanVienXoa;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public KhoiPhucDuLieuDTO() {}

    public KhoiPhucDuLieuDTO(String maDoiTuong, String tenDoiTuong, String loaiDoiTuong,
                              String tenBang, String tenCotXoa,
                              LocalDateTime thoiDiemXoa, String tenNhanVienXoa) {
        this.maDoiTuong = maDoiTuong;
        this.tenDoiTuong = tenDoiTuong;
        this.loaiDoiTuong = loaiDoiTuong;
        this.tenBang = tenBang;
        this.tenCotXoa = tenCotXoa;
        this.thoiDiemXoa = thoiDiemXoa;
        this.tenNhanVienXoa = tenNhanVienXoa;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getMaDoiTuong() { return maDoiTuong; }
    public void setMaDoiTuong(String maDoiTuong) { this.maDoiTuong = maDoiTuong; }

    public String getTenDoiTuong() { return tenDoiTuong; }
    public void setTenDoiTuong(String tenDoiTuong) { this.tenDoiTuong = tenDoiTuong; }

    public String getLoaiDoiTuong() { return loaiDoiTuong; }
    public void setLoaiDoiTuong(String loaiDoiTuong) { this.loaiDoiTuong = loaiDoiTuong; }

    public String getTenBang() { return tenBang; }
    public void setTenBang(String tenBang) { this.tenBang = tenBang; }

    public String getTenCotXoa() { return tenCotXoa; }
    public void setTenCotXoa(String tenCotXoa) { this.tenCotXoa = tenCotXoa; }

    public LocalDateTime getThoiDiemXoa() { return thoiDiemXoa; }
    public void setThoiDiemXoa(LocalDateTime thoiDiemXoa) { this.thoiDiemXoa = thoiDiemXoa; }

    public String getTenNhanVienXoa() { return tenNhanVienXoa; }
    public void setTenNhanVienXoa(String tenNhanVienXoa) { this.tenNhanVienXoa = tenNhanVienXoa; }
}
