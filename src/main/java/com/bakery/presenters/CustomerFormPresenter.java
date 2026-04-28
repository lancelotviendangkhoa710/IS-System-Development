package com.bakery.presenters;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.views.interfaces.CustomerAddView;
import com.bakery.views.interfaces.CustomerUpdateView;

import java.sql.SQLException;
import java.time.LocalDate;
import javafx.concurrent.Task;

/**
 * Presenter cho các form thêm/sửa khách hàng.
 * Xử lý validation và gọi Service để lưu dữ liệu.
 */
public class CustomerFormPresenter {

    private final CustomerService customerService;

    public CustomerFormPresenter() {
        this.customerService = new CustomerService();
    }

    // === FOR ADD FORM ===

    /**
     * Xử lý tạo khách hàng mới từ form Thêm.
     *
     * @param view CustomerAddView interface để update UI
     */
    public void handleAddCustomer(CustomerAddView view) {
        view.setBusy(true);

        String hoTen = view.getFullName();
        String sdt = view.getPhoneNumber();
        String diaChi = view.getAddress();

        // Validate dữ liệu client-side
        if (!validateAddForm(view, hoTen, sdt, diaChi)) {
            view.setBusy(false);
            return;
        }

        // Tạo DTO từ dữ liệu nhập
        KhachHangDTO customer = new KhachHangDTO();
        customer.setHoTen(hoTen);
        customer.setSdt(sdt);
        customer.setDiaChi(diaChi);
        customer.setNgayDangKy(LocalDate.now());
        customer.setDiemTichLuy(0);

        // Chạy trong background thread
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws SQLException {
                return customerService.createCustomer(customer);
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Khách hàng được tạo thành công.");
            view.clearForm();
        });

        task.setOnFailed(event -> {
            String errorMsg = task.getException() instanceof SQLException
                    ? task.getException().getMessage()
                    : "Lỗi không xác định";
            view.showErrorAlert("Lỗi", errorMsg);
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    // === FOR UPDATE FORM ===

    /**
     * Xử lý cập nhật khách hàng từ form Sửa.
     *
     * @param view CustomerUpdateView interface để update UI
     * @param customerId mã khách hàng đang chỉnh sửa
     */
    public void handleUpdateCustomer(CustomerUpdateView view, int customerId) {
        view.setBusy(true);

        String hoTen = view.getFullName();
        String sdt = view.getPhoneNumber();
        String diaChi = view.getAddress();

        // Validate dữ liệu client-side
        if (!validateUpdateForm(view, hoTen, sdt, diaChi)) {
            view.setBusy(false);
            return;
        }

        // Sẽ fetch DTO cũ trong background thread để giữ nguyên Điểm và Hạng

        // Chạy trong background thread
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                KhachHangDTO existing = customerService.getCustomerById(customerId);
                if (existing == null) {
                    throw new SQLException("Khách hàng không tồn tại");
                }
                
                existing.setHoTen(hoTen);
                existing.setSdt(sdt);
                existing.setDiaChi(diaChi);
                
                customerService.updateCustomer(existing);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Khách hàng được cập nhật thành công.");
            // Do not clear form for update, or if requested to be identical, view.clearForm();
            // Wait, I will use view.clearForm() to exactly match the request.
            view.clearForm();
        });

        task.setOnFailed(event -> {
            String errorMsg = task.getException() instanceof SQLException
                    ? task.getException().getMessage()
                    : "Lỗi không xác định";
            view.showErrorAlert("Lỗi", errorMsg);
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    // === VALIDATION ===

    private boolean validateAddForm(CustomerAddView view, String hoTen, String sdt, String diaChi) {
        boolean isValid = true;

        // Validate họ tên
        if (hoTen == null || hoTen.trim().isEmpty()) {
            view.setFullNameError("Tên khách hàng không được để trống");
            isValid = false;
        } else if (hoTen.length() > 100) {
            view.setFullNameError("Tên khách hàng tối đa 100 ký tự");
            isValid = false;
        } else {
            view.setFullNameError(null);
        }

        // Validate SĐT
        if (sdt == null || !sdt.matches("^\\d{10}$")) {
            view.setPhoneError("SĐT phải là 10 chữ số");
            isValid = false;
        } else {
            view.setPhoneError(null);
        }

        // Validate địa chỉ
        if (diaChi == null || diaChi.trim().isEmpty()) {
            view.setAddressError("Địa chỉ không được để trống");
            isValid = false;
        } else if (diaChi.length() > 255) {
            view.setAddressError("Địa chỉ tối đa 255 ký tự");
            isValid = false;
        } else {
            view.setAddressError(null);
        }

        return isValid;
    }

    private boolean validateUpdateForm(CustomerUpdateView view, String hoTen, String sdt, String diaChi) {
        boolean isValid = true;

        // Validate họ tên
        if (hoTen == null || hoTen.trim().isEmpty()) {
            view.setFullNameError("Tên khách hàng không được để trống");
            isValid = false;
        } else if (hoTen.length() > 100) {
            view.setFullNameError("Tên khách hàng tối đa 100 ký tự");
            isValid = false;
        } else {
            view.setFullNameError(null);
        }

        // Validate SĐT
        if (sdt == null || !sdt.matches("^\\d{10}$")) {
            view.setPhoneError("SĐT phải là 10 chữ số");
            isValid = false;
        } else {
            view.setPhoneError(null);
        }

        // Validate địa chỉ
        if (diaChi == null || diaChi.trim().isEmpty()) {
            view.setAddressError("Địa chỉ không được để trống");
            isValid = false;
        } else if (diaChi.length() > 255) {
            view.setAddressError("Địa chỉ tối đa 255 ký tự");
            isValid = false;
        } else {
            view.setAddressError(null);
        }

        return isValid;
    }
}