package com.bakery.utils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class SessionContext {
    private static AuthSession currentSession;
    
    // Singleton pattern from Dev-C for backward compatibility with shift logic
    private static final SessionContext INSTANCE = new SessionContext();

    private int maNV;
    private String hoTen;
    private int maVaiTro;
    private int maCa;
    private boolean caoDangMo;

    private SessionContext() {
    }

    // --- HEAD Branch Methods ---
    public static synchronized void createSession(AuthSession session) {
        currentSession = Objects.requireNonNull(session, "session");
        // Sync with Dev-C instance
        INSTANCE.dangNhap(session.getMaNhanVien(), session.getHoTen(), session.getMaVaiTro());
    }

    public static synchronized AuthSession getCurrentSession() {
        return currentSession;
    }

    public static synchronized void clear() {
        currentSession = null;
        INSTANCE.dangXuat();
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

    // --- Dev-C Branch Methods ---
    public static SessionContext getInstance() {
        return INSTANCE;
    }

    public void dangNhap(int maNV, String hoTen, int maVaiTro) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.maVaiTro = maVaiTro;
        this.caoDangMo = false;
    }

    public void moCa(int maCa) {
        this.maCa = maCa;
        this.caoDangMo = true;
    }

    public void dongCa() {
        this.maCa = 0;
        this.caoDangMo = false;
    }

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
