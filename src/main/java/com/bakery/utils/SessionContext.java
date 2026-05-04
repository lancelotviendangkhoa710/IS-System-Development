package com.bakery.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class SessionContext {
    private static AuthSession currentSession;
    

    private static final SessionContext INSTANCE = new SessionContext();

    private int maCa;
    private boolean caoDangMo;

    private SessionContext() {
    }

    public static synchronized void createSession(AuthSession session) {
        currentSession = Objects.requireNonNull(session, "session");
    }

    public static synchronized AuthSession getCurrentSession() {
        return currentSession;
    }

    public static synchronized void clear() {
        currentSession = null;
        INSTANCE.dongCa();
    }

    public void dangXuat() {
        clear();
    }

    public static final class AuthSession {
        private final int maNhanVien;
        private final int maVaiTro;
        private final String tenDangNhap;
        private final String hoTen;
        private final String tenVaiTro;
        private final Set<String> quyen;

        public AuthSession(
                int maNhanVien,
                int maVaiTro,
                String tenDangNhap,
                String hoTen,
                String tenVaiTro,
                Set<String> quyen
        ) {
            this.maNhanVien = maNhanVien;
            this.maVaiTro = maVaiTro;
            this.tenDangNhap = tenDangNhap;
            this.hoTen = hoTen;
            this.tenVaiTro = tenVaiTro;
            this.quyen = Collections.unmodifiableSet(new LinkedHashSet<>(quyen));
        }

        public int getMaNhanVien() { return maNhanVien; }
        public int getMaVaiTro() { return maVaiTro; }
        public String getTenDangNhap() { return tenDangNhap; }
        public String getHoTen() { return hoTen; }
        public String getTenVaiTro() { return tenVaiTro; }
        public Set<String> getQuyen() { return quyen; }
    }

    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public void moCa(int maCa) {
        this.maCa = maCa;
        this.caoDangMo = true;
    }

    public void dongCa() {
        this.maCa = 0;
        this.caoDangMo = false;
    }

    public int getMaNV() { return currentSession != null ? currentSession.getMaNhanVien() : 0; }
    public String getHoTen() { return currentSession != null ? currentSession.getHoTen() : null; }
    public int getMaVaiTro() { return currentSession != null ? currentSession.getMaVaiTro() : 0; }
    public int getMaCa() { return maCa; }
    public boolean isCaoDangMo() { return caoDangMo; }
}
