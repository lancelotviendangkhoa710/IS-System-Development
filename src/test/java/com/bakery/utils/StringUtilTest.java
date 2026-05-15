package com.bakery.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho {@link StringUtil} — kiểm tra chuẩn hóa chuỗi trạng thái.
 */
@DisplayName("StringUtil Tests")
class StringUtilTest {

    @Test
    @DisplayName("chuanHoa() loại bỏ dấu tiếng Việt và viết hoa")
    void chuanHoa_loaiBoDauVietHoa() {
        String result = StringUtil.chuanHoa("hoàn thành");
        assertEquals("HOAN_THANH", result);
    }

    @Test
    @DisplayName("chuanHoa() chuyển đ → d")
    void chuanHoa_chuyenD() {
        String result = StringUtil.chuanHoa("đặt hàng");
        assertEquals("DAT_HANG", result);
    }

    @Test
    @DisplayName("chuanHoa() null trả về chuỗi rỗng")
    void chuanHoa_null_traVeRong() {
        assertEquals("", StringUtil.chuanHoa(null));
    }

    @Test
    @DisplayName("chuanHoa() chuỗi rỗng trả về chuỗi rỗng")
    void chuanHoa_rong_traVeRong() {
        assertEquals("", StringUtil.chuanHoa(""));
    }

    @Test
    @DisplayName("chuanHoa() trim khoảng trắng đầu cuối")
    void chuanHoa_trimKhoangTrang() {
        String result = StringUtil.chuanHoa("  hủy  ");
        assertEquals("HUY", result);
    }

    @Test
    @DisplayName("chuanHoa() khoảng cách giữa từ → dấu gạch dưới")
    void chuanHoa_khoangCachThanhGachDuoi() {
        String result = StringUtil.chuanHoa("chờ xác nhận");
        assertTrue(result.contains("_"), "Khoảng cách phải thành '_': " + result);
    }

    @Test
    @DisplayName("chuanHoa() 'chờ khách lấy' → CHO_KHACH_LAY (đặc tả cứng)")
    void chuanHoa_choKhachLay_dacTaCung() {
        String result = StringUtil.chuanHoa("chờ khách lấy");
        assertEquals("CHO_KHACH_LAY", result);
    }

    @Test
    @DisplayName("chuanHoa() 'Chờ Khách Lấy' viết hoa đầu từ → CHO_KHACH_LAY")
    void chuanHoa_choKhachLayHoaDau() {
        String result = StringUtil.chuanHoa("Chờ Khách Lấy");
        assertEquals("CHO_KHACH_LAY", result);
    }
}
