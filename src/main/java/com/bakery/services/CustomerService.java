package com.bakery.services;

import com.bakery.models.dao.KhachHangDAO;
import com.bakery.models.dto.KhachHangDTO;
import java.sql.SQLException;
import java.util.List;

public class CustomerService {

    private final KhachHangDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new KhachHangDAO();
    }

    // Kiem tra du lieu dau vao truoc khi goi DAO.
    public void validateCustomerInput(KhachHangDTO customer) throws SQLException {
        if (customer == null) {
            throw new SQLException("Du lieu khach hang khong hop le");
        }

        String fullName = customer.getHoTen() != null ? customer.getHoTen().trim() : "";
        String phone = customer.getSdt() != null ? customer.getSdt().trim() : "";
        String address = customer.getDiaChi() != null ? customer.getDiaChi().trim() : "";

        customer.setHoTen(fullName);
        customer.setSdt(phone);
        customer.setDiaChi(address);

        if (fullName.isEmpty()) {
            throw new SQLException("Ten khach hang khong duoc de trong");
        }

        if (fullName.length() > 100) {
            throw new SQLException("Ten khach hang toi da 100 ky tu");
        }

        if (!phone.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        if (address.length() > 255) {
            throw new SQLException("Dia chi toi da 255 ky tu");
        }
    }

    // Tim khach hang theo SDT (hoat dong hoac da xoa) de phuc vu luong tao moi.
    public KhachHangDTO findOrPrepareCustomerByPhone(String phone) throws SQLException {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        KhachHangDTO activeCustomer = customerDAO.findActiveCustomerByPhone(phone);
        if (activeCustomer != null) {
            return activeCustomer;
        }

        KhachHangDTO deletedCustomer = customerDAO.findDeletedCustomerByPhone(phone);
        if (deletedCustomer != null) {
            return deletedCustomer;
        }

        return null;
    }

    // Khoi phuc mot khach hang da xoa mem.
    public void restoreCustomer(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }
        customerDAO.restoreCustomer(customerId);
    }

    // Tao khach hang moi sau khi validate va kiem tra trung SDT.
    public int createCustomer(KhachHangDTO customer) throws SQLException {
        validateCustomerInput(customer);

        KhachHangDTO existing = customerDAO.findActiveCustomerByPhone(customer.getSdt());
        if (existing != null && existing.getThoiDiemXoa() == null) {
            throw new SQLException("SDT da ton tai trong he thong");
        }

        KhachHangDTO deletedCustomer = customerDAO.findDeletedCustomerByPhone(customer.getSdt());
        if (deletedCustomer != null) {
            throw new SQLException("SDT da ton tai trong thung rac. Hay khoi phuc khach hang cu thay vi tao moi.");
        }

        try {
            return customerDAO.createCustomer(customer);
        } catch (SQLException e) {
            throw new SQLException("Loi tao khach hang: " + e.getMessage(), e);
        }
    }

    // Cap nhat thong tin mot khach hang.
    public void updateCustomer(KhachHangDTO customer) throws SQLException {
        if (customer == null) {
            throw new SQLException("Du lieu khach hang khong hop le");
        }

        if (customer.getMaKH() <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }

        validateCustomerInput(customer);

        KhachHangDTO existing = customerDAO.findActiveCustomerById(customer.getMaKH());
        if (existing == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        KhachHangDTO duplicatePhoneCustomer = customerDAO.findActiveCustomerByPhone(customer.getSdt());
        if (duplicatePhoneCustomer != null && duplicatePhoneCustomer.getMaKH() != customer.getMaKH()) {
            throw new SQLException("SDT da ton tai trong he thong");
        }

        try {
            customerDAO.updateCustomer(customer);
        } catch (SQLException e) {
            throw new SQLException("Loi cap nhat khach hang: " + e.getMessage(), e);
        }
    }

    // Xoa mem khach hang.
    public void softDeleteCustomer(int customerId, int deletedByEmployeeId) throws SQLException {
        if (customerId <= 0 || deletedByEmployeeId <= 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        KhachHangDTO existing = customerDAO.findActiveCustomerById(customerId);
        if (existing == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        try {
            customerDAO.softDeleteCustomer(customerId, deletedByEmployeeId);
        } catch (SQLException e) {
            throw new SQLException("Loi xoa khach hang: " + e.getMessage(), e);
        }
    }

    // Lay khach hang theo ma.
    public KhachHangDTO getCustomerById(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }

        KhachHangDTO customer = customerDAO.findActiveCustomerById(customerId);
        if (customer == null) {
            throw new SQLException("Khach hang khong ton tai");
        }

        return customer;
    }

    // Lay khach hang theo SDT.
    public KhachHangDTO getCustomerByPhone(String phone) throws SQLException {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }

        return customerDAO.findActiveCustomerByPhone(phone);
    }

    // Lay khach hang theo dia chi.
    public KhachHangDTO getCustomerByAddress(String address) throws SQLException {
        if (address == null || address.trim().isEmpty()) {
            throw new SQLException("Dia chi khong hop le");
        }
        return customerDAO.findActiveCustomerByAddress(address);
    }

    // Lay danh sach khach hang dang hoat dong.
    public List<KhachHangDTO> getActiveCustomers() throws SQLException {
        return customerDAO.getAllActiveCustomers();
    }

    // Tim kiem khach hang theo tu khoa.
    public List<KhachHangDTO> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveCustomers();
        }
        return customerDAO.searchActiveCustomers(keyword);
    }

    // Lay danh sach khach hang da xoa mem.
    public List<KhachHangDTO> getDeletedCustomers() {
        return customerDAO.getAllDeletedCustomers();
    }

    // Dem tong so khach hang dang hoat dong.
    public int countActiveCustomers() {
        return customerDAO.countActiveCustomers();
    }

    // Dem khach hang moi trong thang.
    public int countNewCustomersInMonth(int year, int month) {
        return customerDAO.countNewCustomersInMonth(year, month);
    }

    // Cap nhat diem tich luy.
    public void updateCustomerPoints(int customerId, int newPoints) throws SQLException {
        if (customerId <= 0 || newPoints < 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        try {
            customerDAO.updateCustomerPoints(customerId, newPoints);
        } catch (SQLException e) {
            throw new SQLException("Loi cap nhat diem: " + e.getMessage(), e);
        }
    }

}
