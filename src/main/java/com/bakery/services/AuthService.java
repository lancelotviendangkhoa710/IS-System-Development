package com.bakery.services;

import com.bakery.model.dao.SessionDAO;
import com.bakery.model.dao.nhansu.NhanVienDAO;
import com.bakery.model.dto.SessionDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.utils.TokenUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AuthService extends BaseService {
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final SessionDAO sessionDAO = new SessionDAO();

    private static final long TOKEN_EXPIRY_MINUTES = 30;

    /**
     * Đăng nhập và tạo token mới.
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Token nếu đăng nhập thành công, null nếu thất bại
     * @throws Exception nếu có lỗi hệ thống
     */
    public String login(String username, String password) throws Exception {
        validateString(username, "Tên đăng nhập");
        validateString(password, "Mật khẩu");

        NhanVienDTO nhanVien = nhanVienDAO.kiemTraDangNhap(username, password);
        String token = TokenUtils.generateToken();
        Timestamp expiresAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));

        boolean success = sessionDAO.insertSession(token, nhanVien.getMaNV(), expiresAt);
        if (!success) {
            throw new Exception("Không thể tạo phiên đăng nhập. Vui lòng thử lại.");
        }
        return token;
    }

    /**
     * Kiểm tra token có hợp lệ không.
     * 
     * @param token Token cần kiểm tra
     * @return true nếu token hợp lệ và chưa hết hạn
     * @throws Exception nếu có lỗi hệ thống
     */
    public boolean validateToken(String token) throws Exception {
        if (!TokenUtils.isValidTokenFormat(token)) {
            return false;
        }

        SessionDTO session = sessionDAO.findSessionByToken(token);
        if (session == null) {
            return false;
        }

        if (!"ACTIVE".equals(session.getStatus())) {
            return false;
        }

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        if (session.getExpiresAt().before(now)) {
            // Tự động cập nhật trạng thái thành EXPIRED
            sessionDAO.updateSessionStatus(token, "EXPIRED");
            return false;
        }

        return true;
    }

    /**
     * Lấy thông tin nhân viên từ token.
     * 
     * @param token Token của phiên
     * @return NhanVienDTO nếu token hợp lệ, null nếu không
     * @throws Exception nếu có lỗi hệ thống
     */
    public NhanVienDTO getUserFromToken(String token) throws Exception {
        if (!validateToken(token)) {
            return null;
        }

        SessionDTO session = sessionDAO.findSessionByToken(token);
        if (session == null) {
            return null;
        }

        return nhanVienDAO.timNhanVienTheoMa(session.getUserId());
    }

    /**
     * Đăng xuất - thu hồi token.
     * 
     * @param token Token cần thu hồi
     * @return true nếu thành công
     * @throws Exception nếu có lỗi hệ thống
     */
    public boolean logout(String token) throws Exception {
        if (!TokenUtils.isValidTokenFormat(token)) {
            return false;
        }

        return sessionDAO.revokeSession(token);
    }

    /**
     * Gia hạn token (nếu cần).
     * 
     * @param token Token cần gia hạn
     * @return Token mới nếu gia hạn thành công, null nếu thất bại
     * @throws Exception nếu có lỗi hệ thống
     */
    public String refreshToken(String token) throws Exception {
        if (!validateToken(token)) {
            return null;
        }

        String newToken = TokenUtils.generateToken();
        Timestamp newExpiresAt = Timestamp.valueOf(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));

        SessionDTO oldSession = sessionDAO.findSessionByToken(token);
        if (oldSession == null) {
            return null;
        }
        sessionDAO.revokeSession(token);

        boolean success = sessionDAO.insertSession(newToken, oldSession.getUserId(), newExpiresAt);
        if (!success) {
            throw new Exception("Không thể gia hạn phiên đăng nhập.");
        }

        return newToken;
    }
}