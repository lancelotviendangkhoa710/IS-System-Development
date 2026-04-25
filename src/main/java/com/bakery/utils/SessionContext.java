package com.bakery.utils;

/**
 * Lưu thông tin phiên làm việc hiện tại của nhân viên đang đăng nhập.
 * Dùng Singleton — toàn bộ ứng dụng dùng chung một instance.
 */
public class SessionContext {

    private static final SessionContext INSTANCE = new SessionContext();

    private int maNV;
    private String hoTen;
    private int maVaiTro;
    private int maCa;
    private boolean caoDangMo;

    private SessionContext() {}

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    /** Gọi sau khi đăng nhập thành công. */
    public void dangNhap(int maNV, String hoTen, int maVaiTro) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.maVaiTro = maVaiTro;
        this.caoDangMo = false;
    }

    /** Gọi sau khi mở ca thành công để lưu maCa vào session. */
    public void moCa(int maCa) {
        this.maCa = maCa;
        this.caoDangMo = true;
    }

    /** Gọi sau khi đóng ca thành công để xóa thông tin ca. */
    public void dongCa() {
        this.maCa = 0;
        this.caoDangMo = false;
    }

    /** Gọi khi đăng xuất — xóa toàn bộ thông tin phiên. */
    public void dangXuat() {
        this.maNV = 0;
        this.hoTen = null;
        this.maVaiTro = 0;
        dongCa();
    }

    public int getMaNV() { return maNV; }
    public String getHoTen() { return hoTen; }
    public int getMaVaiTro() { return maVaiTro; }
    public int getMaCa() { return maCa; }
    public boolean isCaoDangMo() { return caoDangMo; }
}
