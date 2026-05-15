package com.bakery.services.nhansu;

import com.bakery.model.dao.AccountTokenDAO;
import com.bakery.model.dao.nhansu.NhanVienDAO;
import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dao.nhansu.VaiTroDAO;
import com.bakery.model.dto.AccountTokenDTO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.EmailService;
import com.bakery.utils.OtpUtils;
import com.bakery.utils.PasswordUtils;
import com.bakery.utils.SessionContext;
import com.bakery.utils.TokenUtils;
import com.bakery.utils.UserSession;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý các nghiệp vụ xác thực người dùng (Auth).
 * Quản lý Đăng nhập, Đăng ký, Đổi mật khẩu và Session.
 */
public class XacThucService {
    /** Token hiệu lực 12h — đủ cho một ca làm việc dài nhất. */
    private static final int TOKEN_EXPIRY_HOURS = 12;
    /** OTP prefix để phân biệt với session token trong bảng ACCOUNT_TOKEN. */
    private static final String OTP_PREFIX = "OTP_";

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    private final VaiTroDAO vaiTroDAO = new VaiTroDAO();
    private final AccountTokenDAO accountTokenDAO = new AccountTokenDAO();

    /**
     * Đăng nhập: xác thực thông tin, kiểm tra trạng thái, tạo session.
     */
    public NhanVienDTO dangNhap(String tenDangNhap, String matKhau) throws Exception {
        String tenDangNhapDaChuanHoa = validateUsername(tenDangNhap);
        String matKhauHopLe = validatePassword(matKhau, "Mật khẩu");

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoTenDangNhap(tenDangNhapDaChuanHoa);
        if (nhanVien == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI,
                    "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.TAI_KHOAN_BI_KHOA, "Nhân viên này đã ngừng làm việc.");
        }

        if (nhanVien.getTrangThaiTK() != 1) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.TAI_KHOAN_BI_KHOA, "Tài khoản đã bị khóa.");
        }

        if (!PasswordUtils.matches(matKhauHopLe, nhanVien.getMatKhau())) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI,
                    "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }

        // Mật khẩu chưa được hash → đây là lần đăng nhập đầu tiên với mật khẩu seed
        nhanVien.setCanDoiMatKhau(!PasswordUtils.isBcryptHash(nhanVien.getMatKhau()));

        // Lấy thông tin phân quyền hợp nhất từ tất cả các vai trò của nhân viên
        PhanQuyenDAO.RolePermissionInfo roleInfo = phanQuyenDAO.layPhanQuyenHopNhat(nhanVien.getDanhSachMaVaiTro());
        
        if (roleInfo == null || !roleInfo.isVaiTroHoatDong()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.VAI_TRO_KHONG_HOP_LE, "Nhân viên chưa được gán vai trò hợp lệ.");
        }

        // Lưu ý: permissionKeys có thể rỗng nếu VAITRO_CHUCNANG chưa có dữ liệu.
        // Điều này KHÔNG chặn đăng nhập — quyền module sẽ được kiểm tra riêng ở PhanQuyenService.

        Set<String> permissionKeys = new LinkedHashSet<>(roleInfo.getPermissionKeys());
        nhanVien.setDanhSachTenVaiTro(java.util.Arrays.asList(roleInfo.getTenVaiTro().split(" \\+ ")));

        // SessionContext cần được cập nhật để hỗ trợ danh sách vai trò
        // Ở đây ta lấy vai trò đầu tiên làm đại diện hoặc dùng chuỗi hợp nhất
        int primaryRole = nhanVien.getDanhSachMaVaiTro().isEmpty() ? 0 : nhanVien.getDanhSachMaVaiTro().get(0);

        SessionContext.AuthSession session = new SessionContext.AuthSession(
                nhanVien.getMaNV(),
                primaryRole,
                nhanVien.getTenDangNhap(),
                nhanVien.getHoTen(),
                roleInfo.getTenVaiTro(),
                permissionKeys);
        SessionContext.createSession(session);

        // Tạo token ACCOUNT_TOKEN — hỗ trợ force-logout và watchdog
        String token = TokenUtils.generateToken();
        LocalDate expiresAt = LocalDate.now().plusDays(1); // >= 12h, dùng DATE theo schema
        int maTaiKhoan = nhanVienDAO.layMaTaiKhoan(nhanVien.getMaNV());
        accountTokenDAO.thuHoiToanBoTheoTaiKhoan(maTaiKhoan); // Dọn token cũ trước
        AccountTokenDTO tokenDTO = new AccountTokenDTO(maTaiKhoan, token, expiresAt);
        boolean tokenSaved = accountTokenDAO.insertToken(tokenDTO);
        if (!tokenSaved) {
            throw new Exception("Không thể lưu token phiên đăng nhập. Vui lòng thử lại.");
        }
        UserSession.setCurrentToken(token);  // Lưu vào memory để watchdog dùng
        UserSession.setCurrentUser(nhanVien);

        return nhanVien;
    }

    /**
     * Đăng ký nhân viên mới.
     */
    public int dangKy(
            String hoTen,
            String soDienThoai,
            String tenDangNhap,
            String matKhau,
            String maXacNhanQuanLy,
            Integer maVaiTro) throws Exception {
        if (hoTen == null || hoTen.isBlank()) {
            throw new Exception("Họ tên không được để trống.");
        }

        if (soDienThoai == null || soDienThoai.isBlank()) {
            throw new Exception("Số điện thoại không được để trống.");
        }

        String sdt = soDienThoai.trim();
        if (sdt.length() < 9 || sdt.length() > 15) {
            throw new Exception("Số điện thoại phải từ 9 đến 15 ký tự.");
        }

        String usernameDaChuanHoa = validateUsername(tenDangNhap);
        String matKhauHopLe = validatePassword(matKhau, "Mật khẩu");

        if (maXacNhanQuanLy == null || maXacNhanQuanLy.isBlank()) {
            throw new Exception("Mã xác nhận của quản lý không được để trống.");
        }

        if (maVaiTro == null || maVaiTro <= 0) {
            throw new Exception("Mã vai trò phải lớn hơn 0.");
        }

        NhanVienDTO nhanVien = new NhanVienDTO();
        nhanVien.setHoTen(hoTen.trim());
        nhanVien.setSdt(sdt);
        nhanVien.setTenDangNhap(usernameDaChuanHoa);
        nhanVien.setMatKhau(PasswordUtils.hash(matKhauHopLe));
        nhanVien.setMaVaiTro(maVaiTro);
        nhanVien.setTrangThaiLamViec(1);

        return nhanVienDAO.themNhanVien(nhanVien);
    }

    /**
     * Đổi mật khẩu cho phiên đăng nhập hiện tại.
     */
    public void doiMatKhau(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi) throws Exception {
        doiMatKhau(matKhauHienTai, matKhauMoi, xacNhanMatKhauMoi, true);
    }

    /**
     * Đổi mật khẩu cho phiên đăng nhập hiện tại.
     * @param doDangXuat true = đăng xuất sau khi đổi (đổi thông thường);
     *                   false = giữ session (đổi bắt buộc lần đầu đăng nhập)
     */
    public void doiMatKhau(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi,
                           boolean doDangXuat) throws Exception {
        SessionContext.AuthSession session = requireActiveSession();
        // Lần đầu đăng nhập (doDangXuat=false): mật khẩu cũ là seed — không áp quy tắc độ dài
        String currentPassword = doDangXuat
                ? validatePassword(matKhauHienTai, "Mật khẩu hiện tại")
                : validatePasswordNotBlank(matKhauHienTai, "Mật khẩu hiện tại");
        String newPassword = validatePassword(matKhauMoi, "Mật khẩu mới");
        String confirmPassword = validatePassword(xacNhanMatKhauMoi, "Xác nhận mật khẩu mới");

        if (!newPassword.equals(confirmPassword)) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_XAC_NHAN_KHONG_KHOP,
                    "Mật khẩu mới và xác nhận mật khẩu mới không khớp.");
        }

        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVien == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "Không tìm thấy thông tin tài khoản hiện tại.");
        }

        if (nhanVien.getTrangThaiLamViec() != 1) {
            dangXuat();
            throw new NgoaiLeXacThuc(MaLoiXacThuc.TAI_KHOAN_BI_KHOA, "Tài khoản đã bị vô hiệu hóa.");
        }

        if (!PasswordUtils.matches(currentPassword, nhanVien.getMatKhau())) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_HIEN_TAI_SAI, "Mật khẩu hiện tại không chính xác.");
        }

        if (PasswordUtils.matches(newPassword, nhanVien.getMatKhau())) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_KHONG_HOP_LE, "Mật khẩu mới phải khác mật khẩu hiện tại.");
        }

        boolean updated = nhanVienDAO.doiMatKhau(session.getMaNhanVien(), PasswordUtils.hash(newPassword));
        if (!updated) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "Không thể cập nhật mật khẩu trong CSDL.");
        }

        if (doDangXuat) {
            dangXuat();
        }
    }

    // =========================================================================
    // OTP — Đặt lại mật khẩu qua Email
    // =========================================================================

    /**
     * Tạo OTP, lưu vào ACCOUNT_TOKEN, gửi email cho nhân viên.
     * Gọi từ background Task trong Controller — KHÔNG gọi trực tiếp từ FX thread.
     *
     * @param tenDangNhap Tên đăng nhập cần reset mật khẩu
     * @throws NgoaiLeXacThuc nếu tài khoản không tồn tại hoặc chưa có email
     */
    public void taoVaGuiOtp(String tenDangNhap) throws Exception {
        String usernameDaChuanHoa = validateUsername(tenDangNhap);

        // Lấy maTaiKhoan + email từ DB
        String[] info = nhanVienDAO.layEmailVaMaTaiKhoanTheoUsername(usernameDaChuanHoa);
        if (info == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI,
                    "Không tìm thấy tài khoản hoặc tài khoản đã bị khóa.");
        }
        int maTaiKhoan = Integer.parseInt(info[0]);
        String email = info[1];
        if (email == null || email.isBlank()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.EMAIL_KHONG_CO,
                    "Tài khoản này chưa được đăng ký email. Vui lòng liên hệ Quản lý.");
        }

        // Thu hồi OTP cũ còn tồn tại (token bắt đầu bằng OTP_PREFIX)
        accountTokenDAO.thuHoiOtpTheoTaiKhoan(maTaiKhoan);

        // Tạo và lưu OTP mới — hết hạn sau 10 phút (EXPIRES_AT dùng DATE)
        String otpCode = OtpUtils.taoOtp();
        String tokenValue = OTP_PREFIX + otpCode;
        int expireMinutes = EmailService.getInstance().getOtpExpireMinutes();
        LocalDate expiresAt = LocalDate.now().plusDays(1); // DATE column — dùng ngày, logic expire check bằng ISSUED_AT
        AccountTokenDTO otpToken = new AccountTokenDTO(maTaiKhoan, tokenValue, expiresAt);
        accountTokenDAO.insertToken(otpToken);

        // Gửi email (blocking — gọi từ background Task)
        EmailService.getInstance().guiEmailOtp(email, otpCode);
    }

    /**
     * Xác minh OTP và đặt lại mật khẩu mới.
     * Không yêu cầu session đăng nhập.
     *
     * @param tenDangNhap  Tên đăng nhập
     * @param otpCode      Mã OTP 6 số người dùng nhập
     * @param matKhauMoi   Mật khẩu mới
     * @param xacNhanMoi   Xác nhận mật khẩu mới
     */
    public void xacMinhOtpVaDoiMatKhau(String tenDangNhap, String otpCode,
                                        String matKhauMoi, String xacNhanMoi) throws Exception {
        String usernameDaChuanHoa = validateUsername(tenDangNhap);
        String matKhauMoiHopLe = validatePassword(matKhauMoi, "Mật khẩu mới");
        String xacNhanHopLe = validatePassword(xacNhanMoi, "Xác nhận mật khẩu mới");

        if (!matKhauMoiHopLe.equals(xacNhanHopLe)) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_XAC_NHAN_KHONG_KHOP,
                    "Mật khẩu mới và xác nhận không khớp.");
        }

        // Tìm tài khoản
        String[] info = nhanVienDAO.layEmailVaMaTaiKhoanTheoUsername(usernameDaChuanHoa);
        if (info == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.THONG_TIN_DANG_NHAP_SAI, "Tài khoản không tồn tại.");
        }
        int maTaiKhoan = Integer.parseInt(info[0]);

        // Verify OTP từ ACCOUNT_TOKEN
        String tokenValue = OTP_PREFIX + (otpCode == null ? "" : otpCode.trim());
        AccountTokenDTO otpToken = accountTokenDAO.timTheoGiaTri(tokenValue);
        if (otpToken == null || otpToken.getMaTaiKhoan() != maTaiKhoan) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.OTP_SAI, "Mã xác nhận không chính xác.");
        }
        if (!otpToken.conHieuLuc()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.OTP_HET_HAN, "Mã xác nhận đã hết hạn. Vui lòng yêu cầu mã mới.");
        }

        // Lấy MANV từ maTaiKhoan để gọi PROC_DOI_MATKHAU_TAIKHOAN
        NhanVienDTO nv = nhanVienDAO.timNhanVienTheoTenDangNhap(usernameDaChuanHoa);
        if (nv == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "Không tìm được thông tin nhân viên.");
        }

        // Đổi mật khẩu
        boolean updated = nhanVienDAO.doiMatKhau(nv.getMaNV(), PasswordUtils.hash(matKhauMoiHopLe));
        if (!updated) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "Không thể cập nhật mật khẩu.");
        }

        // Thu hồi OTP sau khi dùng
        accountTokenDAO.revokeToken(tokenValue);
    }

    /**
     * Cập nhật email cho tài khoản — dùng trong màn hình cài đặt cá nhân.
     */
    public void capNhatEmail(String emailMoi) throws Exception {
        SessionContext.AuthSession session = requireActiveSession();
        if (emailMoi == null || emailMoi.isBlank()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, "Email không được để trống.");
        }
        String emailChuan = emailMoi.trim();
        if (!emailChuan.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-z]{2,}$")) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, "Địa chỉ email không hợp lệ.");
        }
        nhanVienDAO.capNhatEmail(session.getMaNhanVien(), emailChuan);
        // Đồng bộ email mới vào UserSession để UI hiển thị ngay
        NhanVienDTO nhanVienMoi = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVienMoi != null) {
            UserSession.setCurrentUser(nhanVienMoi);
        }
    }

    public List<VaiTroDTO> layDanhSachVaiTroDangHoatDong() throws Exception {
        return vaiTroDAO.layDanhSachVaiTroDangHoatDong();
    }

    public List<ChucNangDTO> layQuyenPhienHienTai() throws Exception {
        requireActiveSession();
        // Lấy toàn bộ vai trò từ session thông qua NhanVienDAO để hợp nhất quyền đa vai trò
        SessionContext.AuthSession session = SessionContext.getCurrentSession();
        NhanVienDTO nhanVien = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVien == null || nhanVien.getDanhSachMaVaiTro().isEmpty()) return List.of();
        PhanQuyenDAO.RolePermissionInfo info = phanQuyenDAO.layPhanQuyenHopNhat(nhanVien.getDanhSachMaVaiTro());
        return info != null ? info.getDanhSachChucNang() : List.of();
    }

    public SessionContext.AuthSession layPhienHienTai() {
        return SessionContext.getCurrentSession();
    }

    public void dangXuat() {
        // Thu hồi ACCOUNT_TOKEN trong DB → watchdog thiết bị khác phát hiện phiên kết thúc
        String token = UserSession.getCurrentToken();
        if (token != null) {
            try {
                accountTokenDAO.revokeToken(token);
            } catch (Exception e) {
                System.err.println("[XacThucService] Lỗi revoke token khi đăng xuất: " + e.getMessage());
            }
        }
        UserSession.clear();
        SessionContext.clear();
    }

    /**
     * Cập nhật thông tin cá nhân và đổi mật khẩu mới cho phiên hiện tại.
     * Luồng này yêu cầu nhập mật khẩu mới để xác nhận lưu thay đổi.
     */
    public void capNhatThongTinCaNhan(String hoTen, String soDienThoai,
                                      String matKhauMoi, String xacNhanMatKhauMoi) throws Exception {
        SessionContext.AuthSession session = requireActiveSession();

        if (hoTen == null || hoTen.isBlank()) {
            throw new Exception("Họ tên không được để trống.");
        }

        if (soDienThoai == null || soDienThoai.isBlank()) {
            throw new Exception("Số điện thoại không được để trống.");
        }
        String sdt = soDienThoai.trim();
        if (sdt.length() < 9 || sdt.length() > 15) {
            throw new Exception("Số điện thoại phải từ 9 đến 15 ký tự.");
        }

        // Đổi mật khẩu chỉ khi người dùng thực sự nhập mật khẩu mới (không bắt buộc)
        boolean doiMatKhau = matKhauMoi != null && !matKhauMoi.isBlank();
        String matKhauMoiHopLe = null;
        if (doiMatKhau) {
            matKhauMoiHopLe = validatePassword(matKhauMoi, "Mật khẩu mới");
            String xacNhanHopLe = validatePassword(xacNhanMatKhauMoi, "Xác nhận mật khẩu mới");
            if (!matKhauMoiHopLe.equals(xacNhanHopLe)) {
                throw new Exception("Mật khẩu mới và xác nhận mật khẩu mới không khớp.");
            }
        }

        boolean updatedInfo = nhanVienDAO.capNhatThongTinCaNhan(
                session.getMaNhanVien(),
                hoTen.trim(),
                sdt
        );
        if (!updatedInfo) {
            throw new Exception("Không thể cập nhật thông tin cá nhân.");
        }

        if (doiMatKhau) {
            boolean updatedPassword = nhanVienDAO.doiMatKhau(
                    session.getMaNhanVien(),
                    PasswordUtils.hash(matKhauMoiHopLe)
            );
            if (!updatedPassword) {
                throw new Exception("Không thể cập nhật mật khẩu mới.");
            }
        }

        // Đồng bộ cache session/user sau khi lưu DB.
        NhanVienDTO nhanVienMoi = nhanVienDAO.timNhanVienTheoMa(session.getMaNhanVien());
        if (nhanVienMoi != null) {
            UserSession.setCurrentUser(nhanVienMoi);
            SessionContext.AuthSession oldSession = SessionContext.getCurrentSession();
            if (oldSession != null) {
                SessionContext.createSession(new SessionContext.AuthSession(
                        oldSession.getMaNhanVien(),
                        oldSession.getMaVaiTro(),
                        oldSession.getTenDangNhap(),
                        nhanVienMoi.getHoTen(),
                        oldSession.getTenVaiTro(),
                        oldSession.getQuyen()
                ));
            }
        }
    }

    private SessionContext.AuthSession requireActiveSession() throws Exception {
        SessionContext.AuthSession session = SessionContext.getCurrentSession();
        if (session == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_HE_THONG, "Không có phiên đăng nhập hợp lệ.");
        }
        return session;
    }

    private String validateUsername(String username) throws Exception {
        if (username == null) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, "Tên đăng nhập không được để trống.");
        }

        String normalized = username.trim();
        if (normalized.isEmpty()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, "Tên đăng nhập không được để trống.");
        }

        if (normalized.length() < 3 || normalized.length() > 50) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.LOI_XAC_THUC_DU_LIEU, "Tên đăng nhập phải từ 3 đến 50 ký tự.");
        }
        return normalized;
    }

    private String validatePassword(String password, String fieldName) throws Exception {
        if (password == null || password.isEmpty()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_KHONG_HOP_LE, fieldName + " không được để trống.");
        }

        for (int i = 0; i < password.length(); i++) {
            if (Character.isISOControl(password.charAt(i))) {
                throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_KHONG_HOP_LE,
                        fieldName + " không được chứa ký tự điều khiển.");
            }
        }
        return password;
    }

    /** Chỉ check không rỗng — dùng cho mật khẩu seed (lần đầu đăng nhập). */
    private String validatePasswordNotBlank(String password, String fieldName) throws Exception {
        if (password == null || password.isBlank()) {
            throw new NgoaiLeXacThuc(MaLoiXacThuc.MAT_KHAU_KHONG_HOP_LE, fieldName + " không được để trống.");
        }
        return password;
    }

    public enum MaLoiXacThuc {
        LOI_XAC_THUC_DU_LIEU,
        THONG_TIN_DANG_NHAP_SAI,
        TAI_KHOAN_BI_KHOA,
        VAI_TRO_KHONG_HOP_LE,
        MAT_KHAU_KHONG_HOP_LE,
        MAT_KHAU_HIEN_TAI_SAI,
        MAT_KHAU_XAC_NHAN_KHONG_KHOP,
        EMAIL_KHONG_CO,
        OTP_SAI,
        OTP_HET_HAN,
        LOI_HE_THONG
    }

    public static class NgoaiLeXacThuc extends Exception {
        private final MaLoiXacThuc errorCode;

        public NgoaiLeXacThuc(MaLoiXacThuc errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public MaLoiXacThuc getErrorCode() {
            return errorCode;
        }
    }
}
