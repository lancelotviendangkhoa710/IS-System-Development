package com.bakery.services.khachhang;

import com.bakery.model.dao.khachhang.KhachHangDAO;
import com.bakery.model.dto.khachhang.KhachHangDTO;

import java.sql.SQLException;
import java.util.List;

public class KhachHangService {

    private final KhachHangDAO customerDAO;

    public KhachHangService() {
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

        try {
            KhachHangDTO activeCustomer = customerDAO.findActiveCustomerByPhone(phone);
            if (activeCustomer != null) {
                return activeCustomer;
            }

            KhachHangDTO deletedCustomer = customerDAO.findDeletedCustomerByPhone(phone);
            if (deletedCustomer != null) {
                return deletedCustomer;
            }

            return null;
        } catch (Exception e) {
            throw taoLoiDichVu("Loi tim khach hang theo SDT", e);
        }
    }

    // Khoi phuc mot khach hang da xoa mem.
    public void restoreCustomer(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }
        try {
            customerDAO.restoreCustomer(customerId);
        } catch (Exception e) {
            throw taoLoiDichVu("Loi khoi phuc khach hang", e);
        }
    }

    public int createCustomer(KhachHangDTO customer) throws SQLException {
        validateCustomerInput(customer);

        try {
            KhachHangDTO existing = customerDAO.findActiveCustomerByPhone(customer.getSdt());
            if (existing != null && existing.getThoiDiemXoa() == null) {
                throw new SQLException("SDT da ton tai trong he thong");
            }

            KhachHangDTO deletedCustomer = customerDAO.findDeletedCustomerByPhone(customer.getSdt());
            if (deletedCustomer != null) {
                throw new SQLException("SDT da ton tai trong thung rac. Hay khoi phuc khach hang cu thay vi tao moi.");
            }

            HangThanhVienService tierService = new HangThanhVienService();
            com.bakery.model.dto.HangThanhVienDTO appropriateTier = tierService.getTierByPoints(customer.getDiemTichLuy());
            if (appropriateTier != null) {
                customer.setMaHang(appropriateTier.getMaHang());
            }

            return customerDAO.createCustomer(customer);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw taoLoiDichVu("Loi tao khach hang", e);
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

        try {
            KhachHangDTO existing = customerDAO.findActiveCustomerById(customer.getMaKH());
            if (existing == null) {
                throw new SQLException("Khach hang khong ton tai");
            }

            KhachHangDTO duplicatePhoneCustomer = customerDAO.findActiveCustomerByPhone(customer.getSdt());
            if (duplicatePhoneCustomer != null && duplicatePhoneCustomer.getMaKH() != customer.getMaKH()) {
                throw new SQLException("SDT da ton tai trong he thong");
            }

            customerDAO.updateCustomer(customer);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw taoLoiDichVu("Loi cap nhat khach hang", e);
        }
    }

    // Xoa mem khach hang.
    public void softDeleteCustomer(int customerId, int deletedByEmployeeId) throws SQLException {
        if (customerId <= 0 || deletedByEmployeeId <= 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        try {
            KhachHangDTO existing = customerDAO.findActiveCustomerById(customerId);
            if (existing == null) {
                throw new SQLException("Khach hang khong ton tai");
            }

            customerDAO.softDeleteCustomer(customerId, deletedByEmployeeId);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw taoLoiDichVu("Loi xoa khach hang", e);
        }
    }

    // Lay khach hang theo ma.
    public KhachHangDTO getCustomerById(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Ma khach hang khong hop le");
        }

        try {
            KhachHangDTO customer = customerDAO.findActiveCustomerById(customerId);
            if (customer == null) {
                throw new SQLException("Khach hang khong ton tai");
            }
            return customer;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw taoLoiDichVu("Loi lay khach hang theo ma", e);
        }
    }

    // Lay khach hang theo SDT.
    public KhachHangDTO getCustomerByPhone(String phone) throws SQLException {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new SQLException("SDT phai la 10 chu so");
        }
        try {
            return customerDAO.findActiveCustomerByPhone(phone);
        } catch (Exception e) {
            throw taoLoiDichVu("Loi lay khach hang theo SDT", e);
        }
    }

    // Lay khach hang theo dia chi.
    public KhachHangDTO getCustomerByAddress(String address) throws SQLException {
        if (address == null || address.trim().isEmpty()) {
            throw new SQLException("Dia chi khong hop le");
        }
        try {
            return customerDAO.findActiveCustomerByAddress(address);
        } catch (Exception e) {
            throw taoLoiDichVu("Loi lay khach hang theo dia chi", e);
        }
    }

    // Lay danh sach khach hang dang hoat dong.
    public List<KhachHangDTO> getActiveCustomers() throws SQLException {
        try {
            return customerDAO.getAllActiveCustomers();
        } catch (Exception e) {
            throw taoLoiDichVu("Loi lay danh sach khach hang", e);
        }
    }

    // Tim kiem khach hang theo tu khoa.
    public List<KhachHangDTO> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveCustomers();
        }
        try {
            return customerDAO.searchActiveCustomers(keyword);
        } catch (Exception e) {
            throw taoLoiDichVu("Loi tim kiem khach hang", e);
        }
    }

    // Lay danh sach khach hang da xoa mem.
    public List<KhachHangDTO> getDeletedCustomers() throws SQLException {
        try {
            return customerDAO.getAllDeletedCustomers();
        } catch (Exception e) {
            throw taoLoiDichVu("Loi lay danh sach khach hang da xoa", e);
        }
    }

    // Dem tong so khach hang dang hoat dong.
    public int countActiveCustomers() throws SQLException {
        try {
            return customerDAO.countActiveCustomers();
        } catch (Exception e) {
            throw taoLoiDichVu("Loi dem khach hang dang hoat dong", e);
        }
    }

    // Dem khach hang moi trong thang.
    public int countNewCustomersInMonth(int year, int month) throws SQLException {
        try {
            return customerDAO.countNewCustomersInMonth(year, month);
        } catch (Exception e) {
            throw taoLoiDichVu("Loi dem khach hang moi trong thang", e);
        }
    }

    // Cap nhat diem tich luy.
    public void updateCustomerPoints(int customerId, int newPoints) throws SQLException {
        if (customerId <= 0 || newPoints < 0) {
            throw new SQLException("Du lieu khong hop le");
        }

        try {
            customerDAO.updateCustomerPoints(customerId, newPoints);

            HangThanhVienService tierService = new HangThanhVienService();
            com.bakery.model.dto.HangThanhVienDTO appropriateTier = tierService.getTierByPoints(newPoints);

            if (appropriateTier != null) {
                KhachHangDTO customer = customerDAO.findActiveCustomerById(customerId);
                if (customer != null && customer.getMaHang() != appropriateTier.getMaHang()) {
                    customer.setMaHang(appropriateTier.getMaHang());
                    customerDAO.updateCustomer(customer);
                }
            }
        } catch (Exception e) {
            throw taoLoiDichVu("Loi cap nhat diem", e);
        }
    }

    private SQLException taoLoiDichVu(String thongDiep, Exception e) {
        return new SQLException(thongDiep + ": " + e.getMessage(), e);
    }
}
