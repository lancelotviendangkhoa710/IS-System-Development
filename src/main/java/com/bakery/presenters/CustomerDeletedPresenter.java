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
 * Điều phối logic phân trang, khôi phục dữ liệu.
 */
public class CustomerDeletedPresenter {

    private static final int PAGE_SIZE = 8;

    private final CustomerService customerService;
    private final CustomerDeletedView view;

    private List<KhachHangDTO> allDeletedCustomers;
    private int currentPage;
    private int totalPages;

    public CustomerDeletedPresenter(CustomerDeletedView view) {
        this.view = view;
        this.customerService = new CustomerService();
        this.allDeletedCustomers = List.of();
        this.currentPage = 1;
        this.totalPages = 1;
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
            currentPage = 1;
            updateView();
            view.setBusy(false);
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không tải được thùng rác.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Chuyển đến trang trước đó.
     */
    public void goToPreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateView();
        }
    }

    /**
     * Chuyển đến trang kế tiếp.
     */
    public void goToNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateView();
        }
    }

    /**
     * Chuyển đến trang được chỉ định.
     *
     * @param page số trang
     */
    public void goToPage(int page) {
        if (page < 1) {
            currentPage = 1;
        } else if (page > totalPages) {
            currentPage = totalPages;
        } else {
            currentPage = page;
        }
        updateView();
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

    private void updateView() {
        if (allDeletedCustomers.isEmpty()) {
            totalPages = 1;
            currentPage = 1;
            view.displayDeletedCustomers(List.of());
            view.updatePaginationInfo("Hiển thị 0-0 của 0 khách hàng");
            view.updatePaginationControls(currentPage, totalPages);
            return;
        }

        totalPages = (int) Math.ceil((double) allDeletedCustomers.size() / PAGE_SIZE);
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, allDeletedCustomers.size());
        List<KhachHangDTO> pageData = allDeletedCustomers.subList(fromIndex, toIndex);

        view.displayDeletedCustomers(pageData);
        view.updatePaginationInfo(String.format("Hiển thị %d-%d của %d khách hàng", fromIndex + 1, toIndex, allDeletedCustomers.size()));
        view.updatePaginationControls(currentPage, totalPages);
    }
}