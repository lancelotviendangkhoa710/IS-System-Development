package com.bakery.presenters;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.views.interfaces.CustomerDeletedView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;

/**
 * Presenter cho màn hình Thùng rác khách hàng.
 * Điều phối logic tìm kiếm, khôi phục dữ liệu.
 */
public class CustomerDeletedPresenter {

    private final CustomerService customerService;
    private final CustomerDeletedView view;

    private List<KhachHangDTO> allDeletedCustomers;
    private String currentKeyword;

    public CustomerDeletedPresenter(CustomerDeletedView view) {
        this.view = view;
        this.customerService = new CustomerService();
        this.allDeletedCustomers = List.of();
        this.currentKeyword = "";
    }

    /**
     * Tải lại danh sách khách hàng đã xóa.
     */
    public void refreshDeletedCustomers() {
        view.setBusy(true);

        Task<List<KhachHangDTO>> task = new Task<List<KhachHangDTO>>() {
            @Override
            protected List<KhachHangDTO> call() throws SQLException {
                return new ArrayList<>(customerService.getDeletedCustomers());
            }
        };

        task.setOnSucceeded(event -> {
            allDeletedCustomers = task.getValue();
            currentKeyword = "";
            updateViewWithCurrentData();
            view.setBusy(false);
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không tải được thùng rác.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Tìm kiếm khách hàng đã xóa theo từ khóa.
     *
     * @param keyword từ khóa tìm kiếm
     */
    public void searchDeletedCustomers(String keyword) {
        this.currentKeyword = keyword == null ? "" : keyword.trim();
        updateViewWithCurrentData();
    }

    /**
     * Khôi phục khách hàng đã xóa.
     *
     * @param customerId mã khách hàng
     */
    public void restoreCustomer(int customerId) {
        view.setBusy(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                customerService.restoreCustomer(customerId);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Khôi phục khách hàng thành công.");
            refreshDeletedCustomers();
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không thể khôi phục khách hàng.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    // === PRIVATE HELPERS ===

    private void updateViewWithCurrentData() {
        if (allDeletedCustomers.isEmpty()) {
            view.displayDeletedCustomers(List.of());
            view.updatePaginationInfo("Hiển thị 0 khách hàng");
            return;
        }

        if (currentKeyword.isEmpty()) {
            view.displayDeletedCustomers(allDeletedCustomers);
            view.updatePaginationInfo("Hiển thị " + allDeletedCustomers.size() + " khách hàng");
        } else {
            String lowerKeyword = currentKeyword.toLowerCase();
            List<KhachHangDTO> filtered = allDeletedCustomers.stream().filter(kh -> 
                (kh.getHoTen() != null && kh.getHoTen().toLowerCase().contains(lowerKeyword)) ||
                (kh.getSdt() != null && kh.getSdt().contains(lowerKeyword)) ||
                (String.valueOf(kh.getMaKH()).contains(lowerKeyword)) ||
                (kh.getDiaChi() != null && kh.getDiaChi().toLowerCase().contains(lowerKeyword))
            ).toList();
            view.displayDeletedCustomers(filtered);
            view.updatePaginationInfo("Tìm thấy " + filtered.size() + " khách hàng");
        }
    }
}