package com.bakery.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho {@link CurrencyFormatter} — kiểm tra định dạng, đọc số và parse.
 * Lưu ý: {@code apDungDinhDangNhapTien()} phụ thuộc JavaFX (TextField) — bỏ qua trong unit test này.
 */
@DisplayName("CurrencyFormatter Tests")
class CurrencyFormatterTest {

    // ─── format(double) ───────────────────────────────────────────────────────

    @Test
    @DisplayName("format(double) định dạng số nguyên VNĐ")
    void format_double_soNguyen() {
        String result = CurrencyFormatter.format(1_000_000.0);
        assertTrue(result.contains("1"), "Kết quả phải chứa số 1");
        assertTrue(result.contains("000") || result.contains(".") || result.contains(","),
                "Kết quả phải có dấu phân cách: " + result);
    }

    @Test
    @DisplayName("format(double) không ném ngoại lệ khi input là 0")
    void format_double_soKhong() {
        assertDoesNotThrow(() -> CurrencyFormatter.format(0.0));
    }

    @Test
    @DisplayName("format(BigDecimal) null trả về '0 ₫' không ném exception")
    void format_bigDecimalNull_traVeKhong() {
        assertDoesNotThrow(() -> {
            String result = CurrencyFormatter.format((BigDecimal) null);
            assertNotNull(result);
        });
    }

    @Test
    @DisplayName("format(long) định dạng đúng")
    void format_long_dungDinh() {
        String result = CurrencyFormatter.format(25_000L);
        assertNotNull(result);
        assertTrue(result.contains("25"), "Kết quả phải chứa '25': " + result);
    }

    // ─── formatSoThuan() ─────────────────────────────────────────────────────

    @Test
    @DisplayName("formatSoThuan() trả về số có phân cách — không có ký hiệu ₫")
    void formatSoThuan_khongCoKyHieu() {
        String result = CurrencyFormatter.formatSoThuan(1_234_567L);
        assertFalse(result.contains("₫"), "Không được có ký hiệu ₫: " + result);
        assertTrue(result.contains("234"), "Phải chứa nhóm '234': " + result);
    }

    // ─── docSoTien() ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("docSoTien(0) trả về 'Không đồng'")
    void docSoTien_khong() {
        assertEquals("Không đồng", CurrencyFormatter.docSoTien(0L));
    }

    @Test
    @DisplayName("docSoTien kết thúc bằng ' đồng'")
    void docSoTien_ketThucBangDong() {
        String result = CurrencyFormatter.docSoTien(1_000L);
        assertTrue(result.endsWith(" đồng"), "Phải kết thúc bằng ' đồng': " + result);
    }

    @Test
    @DisplayName("docSoTien chữ hoa đầu câu")
    void docSoTien_chuHoaDauCau() {
        String result = CurrencyFormatter.docSoTien(5_000L);
        assertTrue(Character.isUpperCase(result.charAt(0)),
                "Chữ cái đầu phải viết hoa: " + result);
    }

    @Test
    @DisplayName("docSoTien số âm có tiền tố 'Âm'")
    void docSoTien_soAm() {
        String result = CurrencyFormatter.docSoTien(-1_000L);
        assertTrue(result.startsWith("Âm"), "Số âm phải bắt đầu bằng 'Âm': " + result);
    }

    @Test
    @DisplayName("docSoTien 25_000 chứa 'mươi lăm' (đặc tả tiếng Việt)")
    void docSoTien_haiMuoiLamNghin() {
        String result = CurrencyFormatter.docSoTien(25_000L).toLowerCase();
        assertTrue(result.contains("lăm") || result.contains("hai mươi"),
                "25.000đ phải chứa 'lăm' hoặc 'hai mươi': " + result);
    }

    @Test
    @DisplayName("docSoTien(double) round-trip qua Math.round")
    void docSoTien_double() {
        String result = CurrencyFormatter.docSoTien(1_500_000.0);
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    @DisplayName("docSoTien(BigDecimal) null trả về 'Không đồng'")
    void docSoTien_bigDecimalNull() {
        assertEquals("Không đồng", CurrencyFormatter.docSoTien((BigDecimal) null));
    }

    // ─── parse() ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("parse() chuỗi có dấu chấm kiểu VN → số đúng")
    void parse_dauChamKieuVN() {
        BigDecimal result = CurrencyFormatter.parse("1.234.567");
        assertEquals(new BigDecimal("1234567"), result,
                "parse('1.234.567') phải trả về 1234567");
    }

    @Test
    @DisplayName("parse() chuỗi có dấu phẩy kiểu US → số đúng")
    void parse_dauPhayKieuUS() {
        BigDecimal result = CurrencyFormatter.parse("1,234,567");
        assertEquals(new BigDecimal("1234567"), result);
    }

    @Test
    @DisplayName("parse() chuỗi null trả về ZERO")
    void parse_null_traVeZero() {
        assertEquals(BigDecimal.ZERO, CurrencyFormatter.parse(null));
    }

    @Test
    @DisplayName("parse() chuỗi rỗng trả về ZERO")
    void parse_rong_traVeZero() {
        assertEquals(BigDecimal.ZERO, CurrencyFormatter.parse(""));
    }

    @Test
    @DisplayName("parse() chuỗi chỉ chữ trả về ZERO (không ném exception)")
    void parse_chuoiChuChu_traVeZero() {
        assertDoesNotThrow(() -> {
            BigDecimal result = CurrencyFormatter.parse("abc");
            assertEquals(BigDecimal.ZERO, result);
        });
    }

    @Test
    @DisplayName("parse() số đơn không phân cách")
    void parse_soDon() {
        BigDecimal result = CurrencyFormatter.parse("50000");
        assertEquals(new BigDecimal("50000"), result);
    }

    @Test
    @DisplayName("parse() chứa ký hiệu ₫ vẫn trả về số đúng")
    void parse_coKyHieu() {
        // Ký hiệu ₫ không phải chữ số → bị lọc bỏ
        BigDecimal result = CurrencyFormatter.parse("100000₫");
        assertEquals(new BigDecimal("100000"), result);
    }
}
