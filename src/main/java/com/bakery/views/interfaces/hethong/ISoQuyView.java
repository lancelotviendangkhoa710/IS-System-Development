package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.LoaiThuChiDTO;
import com.bakery.model.dto.hethong.PhieuThuChiDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ISoQuyView {

    // ── Tab Tổng quan ─────────────────────────────────────────────────────────
    void hienThiTongQuan(BigDecimal tongThu, BigDecimal tongChi, BigDecimal soDu);
    void hienThiTyTrong(double tyLeThu, double tyLeChi);

    // ── Tab Lịch sử ──────────────────────────────────────────────────────────
    void setLoadingLichSu(boolean loading);
    void setLoaiOptions(List<LoaiThuChiDTO> options);
    void hienThiDanhSachGiaoDich(List<PhieuThuChiDTO> ds);
    void hienThiFooterGiaoDich(int tu, int den, int tong);
    void setNutPhanTrangEnabled(boolean truoc, boolean sau);
    void hienThiDialogThemGiaoDich(List<LoaiThuChiDTO> cacLoai);

    // ── Tab Cấu hình (Manager only) ───────────────────────────────────────────
    void setTabCauHinhVisible(boolean visible);
    void setLoadingCauHinh(boolean loading);
    void hienThiDanhSachLoai(List<LoaiThuChiDTO> ds);
    void hienThiFooterLoai(int tu, int den, int tong);
    void setNutPhanTrangLoaiEnabled(boolean truoc, boolean sau);

    // ── Dialog xác nhận ───────────────────────────────────────────────────────
    Optional<String> hienThiDialogXacNhanHuy(String tenPhieu);

    // ── Thông báo ─────────────────────────────────────────────────────────────
    void hienThiThongBao(String msg);
    void hienThiLoi(String msg);
}
