package com.bakery.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho {@link PasswordUtils} — kiểm tra mã hóa BCrypt và xác minh mật khẩu.
 */
@DisplayName("PasswordUtils Tests")
class PasswordUtilsTest {

    // ─── hash() ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("hash() trả về chuỗi BCrypt hợp lệ")
    void hash_traVeHashBCrypt() {
        String hash = PasswordUtils.hash("matkhau123");
        assertNotNull(hash);
        assertTrue(PasswordUtils.isBcryptHash(hash),
                "Kết quả hash phải bắt đầu bằng $2a$, $2b$ hoặc $2y$");
    }

    @Test
    @DisplayName("hash() hai lần cho cùng mật khẩu phải khác nhau (salt ngẫu nhiên)")
    void hash_haiLanKhacNhau() {
        String hash1 = PasswordUtils.hash("secret");
        String hash2 = PasswordUtils.hash("secret");
        assertNotEquals(hash1, hash2, "Hai lần hash cùng mật khẩu phải khác nhau do salt");
    }

    // ─── matches() ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("matches() trả về true khi mật khẩu đúng với BCrypt hash")
    void matches_dungMatKhau_traVeTrue() {
        String plain = "BakeryH3K!2025";
        String hash  = PasswordUtils.hash(plain);
        assertTrue(PasswordUtils.matches(plain, hash));
    }

    @Test
    @DisplayName("matches() trả về false khi mật khẩu sai")
    void matches_saiMatKhau_traVeFalse() {
        String hash = PasswordUtils.hash("dungMatKhau");
        assertFalse(PasswordUtils.matches("saiMatKhau", hash));
    }

    @Test
    @DisplayName("matches() trả về false khi plain null")
    void matches_plainNull_traVeFalse() {
        String hash = PasswordUtils.hash("abc123");
        assertFalse(PasswordUtils.matches(null, hash));
    }

    @Test
    @DisplayName("matches() trả về false khi stored null")
    void matches_storedNull_traVeFalse() {
        assertFalse(PasswordUtils.matches("somePassword", null));
    }

    @Test
    @DisplayName("matches() trả về false khi stored là chuỗi rỗng")
    void matches_storedRong_traVeFalse() {
        assertFalse(PasswordUtils.matches("somePassword", ""));
    }

    @Test
    @DisplayName("matches() hỗ trợ mật khẩu plain-text (seed lần đầu chưa hash)")
    void matches_plainTextSeed_traVeTrue() {
        // Seed mật khẩu ban đầu chưa qua BCrypt — so sánh plain-text
        assertFalse(PasswordUtils.isBcryptHash("1"),
                "Mật khẩu seed '1' không phải BCrypt");
        assertTrue(PasswordUtils.matches("1", "1"),
                "matches() phải so sánh plain-text khi stored không phải BCrypt");
    }

    @Test
    @DisplayName("matches() phân biệt hoa thường")
    void matches_phanBietHoaThuong() {
        String hash = PasswordUtils.hash("Password123");
        assertFalse(PasswordUtils.matches("password123", hash));
    }

    // ─── isBcryptHash() ──────────────────────────────────────────────────────

    @Test
    @DisplayName("isBcryptHash() nhận dạng đúng hash $2a$")
    void isBcryptHash_prefix2a_traVeTrue() {
        assertTrue(PasswordUtils.isBcryptHash("$2a$12$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"));
    }

    @Test
    @DisplayName("isBcryptHash() nhận dạng đúng hash $2b$")
    void isBcryptHash_prefix2b_traVeTrue() {
        assertTrue(PasswordUtils.isBcryptHash("$2b$10$abcdefghijklmnopqrstuvwxyz012345678901234567890123"));
    }

    @Test
    @DisplayName("isBcryptHash() trả về false cho chuỗi thường")
    void isBcryptHash_chuoiThuong_traVeFalse() {
        assertFalse(PasswordUtils.isBcryptHash("plaintext"));
    }

    @Test
    @DisplayName("isBcryptHash() trả về false cho chuỗi rỗng")
    void isBcryptHash_rong_traVeFalse() {
        assertFalse(PasswordUtils.isBcryptHash(""));
    }

    @Test
    @DisplayName("hash() + matches() round-trip toàn vẹn")
    void roundTrip_hashRoiMatches() {
        String[] passwords = {"123456", "P@ssw0rd!", "  space  ", "tiengViet123"};
        for (String pwd : passwords) {
            String hash = PasswordUtils.hash(pwd);
            assertTrue(PasswordUtils.matches(pwd, hash),
                    "Round-trip thất bại cho mật khẩu: " + pwd);
        }
    }
}
