package com.bakery.model.dto;

import java.sql.Timestamp;

/**
 * DTO ánh xạ bảng USER_SESSIONS.
 * Quản lý thông tin phiên đăng nhập dựa trên token.
 */
public class SessionDTO extends BaseDTO {
    private int sessionId;
    private int maNV;       // Khớp với cột MANV (đổi từ userId cũ)
    private String token;
    private Timestamp issuedAt;
    private Timestamp expiresAt;
    private String status;

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getMaNV() { return maNV; }
    public void setMaNV(int maNV) { this.maNV = maNV; }

    // Legacy compat — các code cũ dùng getUserId()
    public int getUserId() { return maNV; }
    public void setUserId(int userId) { this.maNV = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Timestamp getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Timestamp issuedAt) { this.issuedAt = issuedAt; }

    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
