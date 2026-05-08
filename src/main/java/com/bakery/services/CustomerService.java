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

    public java.util.Map<String, String> validateCustomerData(String hoTen, String sdt, String diaChi) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        
        if (hoTen == null || hoTen.trim().isEmpty()) {
            errors.put("fullName", "Tên khách hàng không được để trống");
        } else if (hoTen.length() > 100) {
            errors.put("fullName", "Tên khách hàng tối đa 100 ký tự");
        }

        if (sdt == null || !sdt.matches("^\\d{10}$")) {
            errors.put("phone", "Số điện thoại phải là 10 chữ số");
        }

        if (diaChi == null || diaChi.trim().isEmpty()) {
            errors.put("address", "Địa chỉ không được để trống");
        } else if (diaChi.length() > 255) {
            errors.put("address", "Địa chỉ tối đa 255 ký tự");
        }

        return errors;
    }

    // Kiểm tra dữ liệu đầu vào trước khi gọi DAO.
    public void validateCustomerInput(KhachHangDTO customer) throws SQLException {
        if (customer == null) {
            throw new SQLException("Dữ liệu khách hàng không hợp lệ");
        }

        String fullName = customer.getHoTen() != null ? customer.getHoTen().trim() : "";
        String phone = customer.getSdt() != null ? customer.getSdt().trim() : "";
        String address = customer.getDiaChi() != null ? customer.getDiaChi().trim() : "";

        customer.setHoTen(fullName);
        customer.setSdt(phone);
        customer.setDiaChi(address);

        java.util.Map<String, String> errors = validateCustomerData(fullName, phone, address);
        if (!errors.isEmpty()) {
            throw new SQLException(errors.values().iterator().next());
        }
    }

    // Tìm khách hàng theo SĐT (hoạt động hoặc đã xóa) để phục vụ luồng tạo mới.
    public KhachHangDTO findOrPrepareCustomerByPhone(String phone) throws SQLException {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new SQLException("Số điện thoại phải là 10 chữ số");
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

    // Khôi phục một khách hàng đã xóa mềm.
    public void restoreCustomer(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Mã khách hàng không hợp lệ");
        }
        customerDAO.restoreCustomer(customerId);
    }

    public int createCustomer(KhachHangDTO customer) throws SQLException {
        validateCustomerInput(customer);

        KhachHangDTO existing = customerDAO.findActiveCustomerByPhone(customer.getSdt());
        if (existing != null && existing.getThoiDiemXoa() == null) {
            throw new SQLException("Số điện thoại đã tồn tại trong hệ thống");
        }

        KhachHangDTO deletedCustomer = customerDAO.findDeletedCustomerByPhone(customer.getSdt());
        if (deletedCustomer != null) {
            throw new SQLException("Số điện thoại đã tồn tại trong thùng rác. Hãy khôi phục khách hàng cũ thay vì tạo mới.");
        }

        // Tự động gán hạng thành viên khởi điểm dựa trên điểm tích luỹ (thường là 0)
        CustomerTierService tierService = new CustomerTierService();
        com.bakery.models.dto.HangThanhVienDTO appropriateTier = tierService.getTierByPoints(customer.getDiemTichLuy());
        if (appropriateTier != null) {
            customer.setMaHang(appropriateTier.getMaHang());
        }

        try {
            return customerDAO.createCustomer(customer);
        } catch (SQLException e) {
            throw new SQLException("Lỗi tạo khách hàng: " + e.getMessage(), e);
        }
    }

    // Cập nhật thông tin một khách hàng.
    public void updateCustomer(KhachHangDTO customer) throws SQLException {
        if (customer == null) {
            throw new SQLException("Dữ liệu khách hàng không hợp lệ");
        }

        if (customer.getMaKH() <= 0) {
            throw new SQLException("Mã khách hàng không hợp lệ");
        }

        validateCustomerInput(customer);

        KhachHangDTO existing = customerDAO.findActiveCustomerById(customer.getMaKH());
        if (existing == null) {
            throw new SQLException("Khách hàng không tồn tại");
        }

        KhachHangDTO duplicatePhoneCustomer = customerDAO.findActiveCustomerByPhone(customer.getSdt());
        if (duplicatePhoneCustomer != null && duplicatePhoneCustomer.getMaKH() != customer.getMaKH()) {
            throw new SQLException("Số điện thoại đã tồn tại trong hệ thống");
        }

        try {
            customerDAO.updateCustomer(customer);
        } catch (SQLException e) {
            throw new SQLException("Lỗi cập nhật khách hàng: " + e.getMessage(), e);
        }
    }

    // Xóa mềm khách hàng.
    public void softDeleteCustomer(int customerId, int deletedByEmployeeId) throws SQLException {
        if (customerId <= 0 || deletedByEmployeeId <= 0) {
            throw new SQLException("Dữ liệu không hợp lệ");
        }

        KhachHangDTO existing = customerDAO.findActiveCustomerById(customerId);
        if (existing == null) {
            throw new SQLException("Khách hàng không tồn tại");
        }

        try {
            customerDAO.softDeleteCustomer(customerId, deletedByEmployeeId);
        } catch (SQLException e) {
            throw new SQLException("Lỗi xóa khách hàng: " + e.getMessage(), e);
        }
    }

    // Lấy khách hàng theo mã.
    public KhachHangDTO getCustomerById(int customerId) throws SQLException {
        if (customerId <= 0) {
            throw new SQLException("Mã khách hàng không hợp lệ");
        }

        KhachHangDTO customer = customerDAO.findActiveCustomerById(customerId);
        if (customer == null) {
            throw new SQLException("Khách hàng không tồn tại");
        }

        return customer;
    }

    // Lấy khách hàng theo SĐT.
    public KhachHangDTO getCustomerByPhone(String phone) throws SQLException {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new SQLException("Số điện thoại phải là 10 chữ số");
        }

        return customerDAO.findActiveCustomerByPhone(phone);
    }

    // Lấy khách hàng theo địa chỉ.
    public KhachHangDTO getCustomerByAddress(String address) throws SQLException {
        if (address == null || address.trim().isEmpty()) {
            throw new SQLException("Địa chỉ không hợp lệ");
        }
        return customerDAO.findActiveCustomerByAddress(address);
    }

    // Lấy danh sách khách hàng đang hoạt động.
    public List<KhachHangDTO> getActiveCustomers() throws SQLException {
        return customerDAO.getAllActiveCustomers();
    }

    // Tìm kiếm khách hàng theo từ khóa.
    public List<KhachHangDTO> searchCustomers(String keyword) throws SQLException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getActiveCustomers();
        }
        return customerDAO.searchActiveCustomers(keyword);
    }

    // Lấy danh sách khách hàng đã xóa mềm.
    public List<KhachHangDTO> getDeletedCustomers() {
        return customerDAO.getAllDeletedCustomers();
    }

    // Đếm tổng số khách hàng đang hoạt động.
    public int countActiveCustomers() {
        return customerDAO.countActiveCustomers();
    }

    // Đếm khách hàng mới trong tháng.
    public int countNewCustomersInMonth(int year, int month) {
        return customerDAO.countNewCustomersInMonth(year, month);
    }

    // Cập nhật điểm tích lũy.
    public void updateCustomerPoints(int customerId, int newPoints) throws SQLException {
        if (customerId <= 0 || newPoints < 0) {
            throw new SQLException("Dữ liệu không hợp lệ!");
        }

        try {
            // Update points
            customerDAO.updateCustomerPoints(customerId, newPoints);
            
            // Auto calculate tier
            CustomerTierService tierService = new CustomerTierService();
            com.bakery.models.dto.HangThanhVienDTO appropriateTier = tierService.getTierByPoints(newPoints);
            
            if (appropriateTier != null) {
                KhachHangDTO customer = customerDAO.findActiveCustomerById(customerId);
                if (customer != null && customer.getMaHang() != appropriateTier.getMaHang()) {
                    customer.setMaHang(appropriateTier.getMaHang());
                    customerDAO.updateCustomer(customer);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Lỗi cập nhật điểm: " + e.getMessage(), e);
        }
    }

}
