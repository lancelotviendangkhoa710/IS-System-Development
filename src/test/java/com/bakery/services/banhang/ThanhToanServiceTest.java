package com.bakery.services.banhang;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test cho logic tính tiền thuần trong {@link ThanhToanService}.
 *
 * <p>Test tập trung vào {@code tinhTienHoaDon()} và {@code taoHoaDonDTO()} —
 * các method không cần DB connection. Các nghiệp vụ cần DB (thanhToanTrucTiep,
 * chotHoaDonDatHang) sẽ được test ở Integration Test riêng.
 */
@DisplayName("ThanhToanService — Tính tiền Tests")
class ThanhToanServiceTest {

    private final ThanhToanService service = new ThanhToanService();

    @Test
    @DisplayName("taoHoaDonDTO() trả về DTO không null")
    void taoHoaDonDTO_traVeKhongNull() {
        var dto = service.taoHoaDonDTO(1, 150_000.0, "TIEN_MAT");
        assertNotNull(dto, "HoaDonDTO không được null");
    }

    @Test
    @DisplayName("taoHoaDonDTO() gán đúng mã đơn")
    void taoHoaDonDTO_ganDungMaDon() {
        var dto = service.taoHoaDonDTO(42, 100_000.0, "CHUYEN_KHOAN");
        assertEquals(42, dto.getMaDon(), "Mã đơn phải là 42");
    }

    @Test
    @DisplayName("taoHoaDonDTO() tổng tiền không null")
    void taoHoaDonDTO_ganDungTongTien() {
        var dto = service.taoHoaDonDTO(1, 250_000.0, "TIEN_MAT");
        assertNotNull(dto.getTongTienThanhToan(),
                "tongTienThanhToan không được null");
    }

    @Test
    @DisplayName("taoHoaDonDTO() loại hóa đơn TIEN_MAT được gán đúng")
    void taoHoaDonDTO_loaiTienMat() {
        var dto = service.taoHoaDonDTO(1, 100_000.0, "TIEN_MAT");
        assertNotNull(dto.getLoaiHD(), "Loại hóa đơn không được null");
    }
}
