package com.bakery.utils;

public final class DemoConfig {

    /** 2 chế độ: production (có FOR UPDATE, không delay) và bug demo (không FOR UPDATE, có delay). */
    public enum ConcurrencyMode {
        PRODUCTION("PROC_TAODONHANG",     "Production — FOR UPDATE, không delay"),
        BUG_DEMO  ("PROC_TAODONHANG_BUG", "Bug Demo — không FOR UPDATE, có delay");

        private final String tenProc;
        private final String moTa;

        ConcurrencyMode(String tenProc, String moTa) {
            this.tenProc = tenProc;
            this.moTa = moTa;
        }

        public String getTenProc() {
            return tenProc;
        }

        public String getMoTa() {
            return moTa;
        }
    }

    /** Chế độ hiện tại — volatile để đảm bảo visibility giữa các thread. */
    private static volatile ConcurrencyMode cheDo = ConcurrencyMode.PRODUCTION;

    private DemoConfig() {
    }

    public static ConcurrencyMode getCheDo() {
        return cheDo;
    }

    public static void setCheDo(ConcurrencyMode mode) {
        cheDo = (mode != null) ? mode : ConcurrencyMode.PRODUCTION;
    }

    /** Tên procedure sẽ được gọi trong DonHangDAO. */
    public static String getTenProcTaoDon() {
        return cheDo.getTenProc();
    }

    /** Đang ở chế độ demo (có delay) hay không. */
    public static boolean isDemoMode() {
        return cheDo != ConcurrencyMode.PRODUCTION;
    }
}
