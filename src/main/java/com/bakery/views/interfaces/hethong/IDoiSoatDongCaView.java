package com.bakery.views.interfaces.hethong;

import com.bakery.views.interfaces.IBaseView;
import java.math.BigDecimal;

public interface IDoiSoatDongCaView extends IBaseView {

    // ── State 0: tải thông tin ca ─────────────────────────────────────────────
    void hienThiThongTinCa(String maCa, String mayPOS, String tienDauCa, String doanhThu);

    void hienThiLoiTaiCa(String maCa, String causeMsg);

    // ── State 1 → 2: kiểm tra ────────────────────────────────────────────────
    void hienThiLoiNhapTrong();

    void hienThiLoiNhapSaiDinhDang();

    void chuyenSangSauKiemTra(BigDecimal chenhLech, String tienHienThi);

    void setNutKhoaSoEnabled(boolean enabled);

    // ── State 2 → 1: sửa lại ────────────────────────────────────────────────
    void chuyenVeNhapTien();

    // ── State 2 → 3: khóa sổ ────────────────────────────────────────────────
    void setNutKhoaSoDangXuLy(boolean dangXuLy);

    void hienThiKetQua(BigDecimal tienThucTe, BigDecimal tienHeThonh,
                       BigDecimal chenhLech, String lyDo);

    void hienThiLoiKhoaSo(String msg);

    // ── Điều hướng ────────────────────────────────────────────────────────────
    void dongDialog();

    void navigateToLogin();

    void resizeDialog();
}
