package com.bakery.services.nhansu;

import com.bakery.services.nhansu.XacThucService.MaLoiXacThuc;
import com.bakery.services.nhansu.XacThucService.NgoaiLeXacThuc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho logic validation thuần trong {@link XacThucService}.
 *
 * <p>Các method có thể test mà KHÔNG cần DB connection:
 * <ul>
 *   <li>validateUsername (private, expose qua dangNhap với null → ném lỗi validation)</li>
 *   <li>validatePassword (tương tự)</li>
 *   <li>{@link NgoaiLeXacThuc} — kiểm tra error code propagation</li>
 * </ul>
 *
 * Lưu ý: các test cần DB (dangNhap, doiMatKhau, taoVaGuiOtp) được đánh dấu skip.
 * Chỉ test logic validation layer không phụ thuộc DB.
 */
@DisplayName("XacThucService — Validation Logic Tests")
class XacThucServiceValidationTest {

    private XacThucService service;

    @BeforeEach
    void setUp() {
        service = new XacThucService();
    }

    // ─── NgoaiLeXacThuc — kiểm tra cấu trúc exception ───────────────────────

    @Test
    @DisplayName("NgoaiLeXacThuc chứa đúng errorCode")
    void ngoaiLeXacThuc_chuaDungErrorCode() {
        NgoaiLeXacThuc ex = new NgoaiLeXacThuc(
                MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI,
                "Test message");
        assertEquals(MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI, ex.getErrorCode());
    }

    @Test
    @DisplayName("NgoaiLeXacThuc message được truyền đúng")
    void ngoaiLeXacThuc_messageDungTruyen() {
        String msg = "Tên đăng nhập không được để trống.";
        NgoaiLeXacThuc ex = new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, msg);
        assertEquals(msg, ex.getMessage());
    }

    @Test
    @DisplayName("NgoaiLeXacThuc là subclass của Exception")
    void ngoaiLeXacThuc_laSubclassException() {
        NgoaiLeXacThuc ex = new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "err");
        assertInstanceOf(Exception.class, ex);
    }

    @Test
    @DisplayName("NgoaiLeXacThuc.getErrorCode() với OTP_SAI")
    void ngoaiLeXacThuc_otpSai() {
        NgoaiLeXacThuc ex = new NgoaiLeXacThuc(MaLoiXacThuc.OTP_SAI, "OTP sai");
        assertEquals(MaLoiXacThuc.OTP_SAI, ex.getErrorCode());
    }

    // ─── dangNhap() — validation username/password (không cần DB) ───────────

    @Test
    @DisplayName("dangNhap() với username null ném NgoaiLeXacThuc LOI_XAC_THUC_DU_LIEU")
    void dangNhap_usernameNull_nemLoiXacThucDuLieu() {
        NgoaiLeXacThuc ex = assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap(null, "matkhau123"));
        assertEquals(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, ex.getErrorCode(),
                "Username null phải ném LOI_XAC_THUC_DU_LIEU");
    }

    @Test
    @DisplayName("dangNhap() với username rỗng ném NgoaiLeXacThuc")
    void dangNhap_usernameRong_nemLoiXacThuc() {
        assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap("", "matkhau123"));
    }

    @Test
    @DisplayName("dangNhap() với username quá ngắn (< 3 ký tự) ném lỗi")
    void dangNhap_usernameQuaNgan_nemLoiXacThuc() {
        assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap("ab", "matkhau123"));
    }

    @Test
    @DisplayName("dangNhap() với username quá dài (> 50 ký tự) ném lỗi")
    void dangNhap_usernameQuaDai_nemLoiXacThuc() {
        String longUsername = "a".repeat(51);
        assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap(longUsername, "matkhau123"));
    }

    @Test
    @DisplayName("dangNhap() với password null ném NgoaiLeXacThuc")
    void dangNhap_passwordNull_nemLoiXacThuc() {
        assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap("admin", null));
    }

    @Test
    @DisplayName("dangNhap() với password rỗng ném NgoaiLeXacThuc")
    void dangNhap_passwordRong_nemLoiXacThuc() {
        assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap("admin", ""));
    }

    @Test
    @DisplayName("dangNhap() với password chứa ký tự điều khiển ném lỗi MAT_KHAU_KHONG_HOP_LE")
    void dangNhap_passwordKyTuDieuKhien_nemLoiMatKhauKhongHopLe() {
        NgoaiLeXacThuc ex = assertThrows(NgoaiLeXacThuc.class,
                () -> service.dangNhap("admin", "mat\nkhau"));
        assertEquals(MaLoiXacThuc.MAT_KHAU_KHONG_HOP_LE, ex.getErrorCode());
    }

    // ─── dangKy() — validation ───────────────────────────────────────────────

    @Test
    @DisplayName("dangKy() với hoTen rỗng ném Exception")
    void dangKy_hoTenRong_nemException() {
        assertThrows(Exception.class,
                () -> service.dangKy("", "0909000000", "newuser", "Pass123", "code", 2));
    }

    @Test
    @DisplayName("dangKy() với sdt ngắn hơn 9 ký tự ném Exception")
    void dangKy_sdtQuaNgan_nemException() {
        assertThrows(Exception.class,
                () -> service.dangKy("Nguyen Van A", "012", "newuser", "Pass123", "code", 2));
    }

    @Test
    @DisplayName("dangKy() với maVaiTro <= 0 ném Exception")
    void dangKy_maVaiTroKhongHopLe_nemException() {
        assertThrows(Exception.class,
                () -> service.dangKy("Nguyen Van A", "0909090909", "newuser", "Pass123", "code", 0));
    }

    @Test
    @DisplayName("dangKy() với maXacNhanQuanLy rỗng ném Exception")
    void dangKy_maXacNhanRong_nemException() {
        assertThrows(Exception.class,
                () -> service.dangKy("Nguyen Van A", "0909090909", "newuser", "Pass123", "", 2));
    }

    // ─── MaLoiXacThuc enum ───────────────────────────────────────────────────

    @Test
    @DisplayName("MaLoiXacThuc có đủ 11 giá trị enum")
    void maLoiXacThuc_daDu11GiaTri() {
        assertEquals(11, MaLoiXacThuc.values().length,
                "Phải có đủ 11 mã lỗi xác thực");
    }
}
