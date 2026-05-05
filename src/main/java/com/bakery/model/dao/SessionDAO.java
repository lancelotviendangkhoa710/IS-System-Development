package com.bakery.model.dao;

import com.bakery.model.dto.SessionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SessionDAO extends BaseDAO {

    public boolean insertSession(String token, int userId, Timestamp expiresAt) throws Exception {
        String sql = "INSERT INTO USER_SESSIONS (USER_ID, TOKEN, ISSUED_AT, EXPIRES_AT, STATUS) VALUES (?, ?, SYSTIMESTAMP, ?, 'ACTIVE')";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, token);
            pstmt.setTimestamp(3, expiresAt);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("insertSession", e);
            return false;
        }
    }

    public SessionDTO findSessionByToken(String token) throws Exception {
        String sql = "SELECT SESSION_ID, USER_ID, TOKEN, ISSUED_AT, EXPIRES_AT, STATUS FROM USER_SESSIONS WHERE TOKEN = ?";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public boolean updateSessionStatus(String token, String status) throws Exception {
        String sql = "UPDATE USER_SESSIONS SET STATUS = ? WHERE TOKEN = ?";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, token);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("updateSessionStatus", e);
            return false;
        }
    }

    public boolean deleteSession(String token) throws Exception {
        String sql = "DELETE FROM USER_SESSIONS WHERE TOKEN = ?";
        try (Connection conn = moKetNoi();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, token);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("deleteSession", e);
            return false;
        }
    }

    public boolean revokeSession(String token) throws Exception {
        return updateSessionStatus(token, "REVOKED");
    }

    private SessionDTO mapSession(ResultSet rs) throws SQLException {
        SessionDTO session = new SessionDTO();
        session.setSessionId(rs.getInt("SESSION_ID"));
        session.setUserId(rs.getInt("USER_ID"));
        session.setToken(rs.getString("TOKEN"));
        session.setIssuedAt(rs.getTimestamp("ISSUED_AT"));
        session.setExpiresAt(rs.getTimestamp("EXPIRES_AT"));
        session.setStatus(rs.getString("STATUS"));
        return session;
    }
}
