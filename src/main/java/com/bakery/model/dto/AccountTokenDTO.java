package com.bakery.model.dto;

import java.time.LocalDate;

/** DTO ánh xạ bảng ACCOUNT_TOKEN. */
public class AccountTokenDTO extends BaseDTO {
    private long tokenId;
    private int maTaiKhoan;
    private String tokenValue;
    private LocalDate expiresAt;
    private LocalDate issuedAt;
    private String isRevoked; // 'Y' hoặc 'N'

    public AccountTokenDTO() {}

    public AccountTokenDTO(int maTaiKhoan, String tokenValue, LocalDate expiresAt) {
        this.maTaiKhoan = maTaiKhoan;
        this.tokenValue = tokenValue;
        this.expiresAt = expiresAt;
        this.isRevoked = "N";
    }

    public long getTokenId() { return tokenId; }
    public void setTokenId(long tokenId) { this.tokenId = tokenId; }

    public int getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(int maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

    public String getTokenValue() { return tokenValue; }
    public void setTokenValue(String tokenValue) { this.tokenValue = tokenValue; }

    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }

    public LocalDate getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDate issuedAt) { this.issuedAt = issuedAt; }

    public String getIsRevoked() { return isRevoked; }
    public void setIsRevoked(String isRevoked) { this.isRevoked = isRevoked; }

    /** Kiện tra token còn hiệu lực không. */
    public boolean conHieuLuc() {
        return "N".equals(isRevoked)
                && expiresAt != null
                && !LocalDate.now().isAfter(expiresAt);
    }
}
