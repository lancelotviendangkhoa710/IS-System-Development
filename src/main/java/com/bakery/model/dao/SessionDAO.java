package com.bakery.model.dao;

import com.bakery.model.dto.SessionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * DAO tương tác bảng USER_SESSIONS.
 * Dùng cột MANV (đã đồng bộ với schema hiện tại).
 */
public class SessionDAO extends BaseDAO {

    private static final String INSERT_SESSION =
            "INSERT INTO USER_SESSIONS (MANV, TOKEN, ISSUED_AT, EXPIRES_AT, STATUS) " +
            "VALUES (?, ?, SYSTIMESTAMP, ?, 'ACTIVE')";

    private static final String FIND_BY_TOKEN =
            "SELECT SESSION_ID, MANV, TOKEN, ISSUED_AT, EXPIRES_AT, STATUS " +
            "FROM USER_SESSIONS WHERE TOKEN = ?";

    private static final String UPDATE_STATUS =
            "UPDATE USER_SESSIONS SET STATUS = ? WHERE TOKEN = ?";

    private static final String DELETE_SESSION =
            "DELETE FROM USER_SESSIONS WHERE TOKEN = ?";

    private static final String DELETE_ALL_BY_MANV =
            "DELETE FROM USER_SESSIONS WHERE MANV = ?";

    /**
     * Lưu phiên đăng nhập mới vào DB.
     *
     * @param token     Token duy nhất
     * @param maNV      Mã nhân viên
     * @param expiresAt Thời điểm hết hạn
     * @return true nếu thành công
     */
    public boolean insertSession(String token, int maNV, Timestamp expiresAt) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SESSION)) {
            pstmt.setInt(1, maNV);
            pstmt.setString(2, token);
            pstmt.setTimestamp(3, expiresAt);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("insertSession", e);
            return false;
        }
    }

    /**
     * Tìm phiên đăng nhập theo token.
     *
     * @param token Token cần tìm
     * @return SessionDTO nếu tìm thấy, null nếu không
     */
    public SessionDTO findSessionByToken(String token) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_TOKEN)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapSession(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            handleException("findSessionByToken", e);
            return null;
        }
    }

    /**
     * Cập nhật trạng thái phiên (ACTIVE / EXPIRED / REVOKED).
     */
    public boolean updateSessionStatus(String token, String status) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_STATUS)) {
            pstmt.setString(1, status);
            pstmt.setString(2, token);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("updateSessionStatus", e);
            return false;
        }
    }

    /**
     * Xóa hẳn một phiên (khi admin force-logout hoặc đăng xuất sạch).
     */
    public boolean deleteSession(String token) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SESSION)) {
            pstmt.setString(1, token);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("deleteSession", e);
            return false;
        }
    }

    /**
     * Revoke phiên — đặt STATUS = 'REVOKED' (giữ lại lịch sử audit).
     */
    public boolean revokeSession(String token) throws Exception {
        return updateSessionStatus(token, "REVOKED");
    }

    /**
     * Xóa toàn bộ phiên cũ của một nhân viên (dọn trước khi tạo phiên mới).
     */
    public void xoaToanBoPhienCuaUser(int maNV) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_ALL_BY_MANV)) {
            pstmt.setInt(1, maNV);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("xoaToanBoPhienCuaUser", e);
        }
    }

    private SessionDTO mapSession(ResultSet rs) throws SQLException {
        SessionDTO session = new SessionDTO();
        session.setSessionId(rs.getInt("SESSION_ID"));
        session.setMaNV(rs.getInt("MANV"));
        session.setToken(rs.getString("TOKEN"));
        session.setIssuedAt(rs.getTimestamp("ISSUED_AT"));
        session.setExpiresAt(rs.getTimestamp("EXPIRES_AT"));
        session.setStatus(rs.getString("STATUS"));
        return session;
    }
}
