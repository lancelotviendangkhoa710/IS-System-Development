package com.bakery.services;

import com.bakery.model.dto.KhachHangDTO;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerServiceValidationTest {

    private final CustomerService service = new CustomerService();

    @Test
    void validateCustomerInput_rejectsInvalidPhone() {
        KhachHangDTO customer = taoKhachHang("Nguyen Van A", "12345", "Quan 1");

        assertThrows(SQLException.class, () -> service.validateCustomerInput(customer));
    }

    @Test
    void validateCustomerInput_rejectsEmptyName() {
        KhachHangDTO customer = taoKhachHang("   ", "0901234567", "Quan 1");

        assertThrows(SQLException.class, () -> service.validateCustomerInput(customer));
    }

    @Test
    void validateCustomerInput_trimsInput() throws SQLException {
        KhachHangDTO customer = taoKhachHang("  Nguyen Van B  ", "0901234567", "  Quan 3 ");

        assertDoesNotThrow(() -> service.validateCustomerInput(customer));
        assertEquals("Nguyen Van B", customer.getHoTen());
        assertEquals("Quan 3", customer.getDiaChi());
    }

    private KhachHangDTO taoKhachHang(String hoTen, String sdt, String diaChi) {
        KhachHangDTO customer = new KhachHangDTO();
        customer.setHoTen(hoTen);
        customer.setSdt(sdt);
        customer.setDiaChi(diaChi);
        return customer;
    }
}
