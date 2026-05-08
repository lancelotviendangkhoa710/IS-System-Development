package com.bakery.model.dao;

import com.bakery.model.dto.AccountTokenDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO tương tác bảng ACCOUNT_TOKEN.
 * Cột DB dùng DATE → ánh xạ sang LocalDate theo quy tắc javarules.md.
 */
public class AccountTokenDAO extends BaseDAO {

    private static final String INSERT_TOKEN =
            "INSERT INTO ACCOUNT_TOKEN (MATAIKHOAN, TOKEN_VALUE, EXPIRES_AT) VALUES (?, ?, ?)";

    private static final String REVOKE_TOKEN =
            "UPDATE ACCOUNT_TOKEN SET IS_REVOKED = 'Y' WHERE TOKEN_VALUE = ?";

    private static final String FIND_BY_VALUE =
            "SELECT TOKEN_ID, MATAIKHOAN, TOKEN_VALUE, EXPIRES_AT, ISSUED_AT, IS_REVOKED " +
            "FROM ACCOUNT_TOKEN WHERE TOKEN_VALUE = ?";

    private static final String REVOKE_ALL_BY_ACCOUNT =
            "UPDATE ACCOUNT_TOKEN SET IS_REVOKED = 'Y' WHERE MATAIKHOAN = ?";

    /**
     * Lưu token mới vào DB sau khi đăng nhập thành công.
     * IS_REVOKED giữ mặc định 'N' do DB tự gán.
     */
    public boolean insertToken(AccountTokenDTO token) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_TOKEN)) {
            pstmt.setInt(1, token.getMaTaiKhoan());
            pstmt.setString(2, token.getTokenValue());
            pstmt.setDate(3, Date.valueOf(token.getExpiresAt()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("insertToken", e);
            return false;
        }
    }

    /**
     * Thu hồi token khi đăng xuất.
     * UPDATE ... IS_REVOKED = 'Y' — phải COMMIT ngay, Oracle auto-commit qua JDBC.
     */
    public boolean revokeToken(String tokenValue) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(REVOKE_TOKEN)) {
            pstmt.setString(1, tokenValue);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            handleException("revokeToken", e);
            return false;
        }
    }

    /** Tìm token theo giá trị chuỗi (dùng cho watchdog xác thực). */
    public AccountTokenDTO timTheoGiaTri(String tokenValue) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_VALUE)) {
            pstmt.setString(1, tokenValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return anhXa(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            handleException("timTheoGiaTri", e);
            return null;
        }
    }

    /** Thu hồi toàn bộ token cũ của một tài khoản (dọn trước khi tạo token mới). */
    public void thuHoiToanBoTheoTaiKhoan(int maTaiKhoan) throws Exception {
        try (Connection conn = moKetNoi();
             PreparedStatement pstmt = conn.prepareStatement(REVOKE_ALL_BY_ACCOUNT)) {
            pstmt.setInt(1, maTaiKhoan);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleException("thuHoiToanBoTheoTaiKhoan", e);
        }
    }

    private AccountTokenDTO anhXa(ResultSet rs) throws SQLException {
        AccountTokenDTO dto = new AccountTokenDTO();
        dto.setTokenId(rs.getLong("TOKEN_ID"));
        dto.setMaTaiKhoan(rs.getInt("MATAIKHOAN"));
        dto.setTokenValue(rs.getString("TOKEN_VALUE"));

        Date expiresAt = rs.getDate("EXPIRES_AT");
        if (expiresAt != null) dto.setExpiresAt(expiresAt.toLocalDate());

        Date issuedAt = rs.getDate("ISSUED_AT");
        if (issuedAt != null) dto.setIssuedAt(issuedAt.toLocalDate());

        dto.setIsRevoked(rs.getString("IS_REVOKED"));
        return dto;
    }
}
