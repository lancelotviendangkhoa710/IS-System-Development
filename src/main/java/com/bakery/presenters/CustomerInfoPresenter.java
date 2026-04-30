package com.bakery.presenters;

import com.bakery.models.dto.KhachHangDTO;
import com.bakery.services.CustomerService;
import com.bakery.views.interfaces.CustomerInfoView;
import com.bakery.views.interfaces.ViewFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.concurrent.Task;

/**
 * Presenter cho màn hình Danh sách Khách hàng.
 * Điều phối logic nghiệp vụ và giao tiếp với View qua interface.
 */
public class CustomerInfoPresenter {

    private static final int ROWS_PER_PAGE = 10;

    private final CustomerService customerService;
    private final CustomerInfoView view;
    private final ViewFactory viewFactory;

    private List<KhachHangDTO> originalData;
    private List<KhachHangDTO> searchResultData;
    private List<KhachHangDTO> filteredData;
    private String currentKeyword;
    private LocalDate filterFromDate;
    private LocalDate filterToDate;
    private String filterTier;
    private int currentPage;
    private int totalPages;

    public CustomerInfoPresenter(CustomerInfoView view, ViewFactory viewFactory) {
        this.view = view;
        this.viewFactory = viewFactory;
        this.customerService = new CustomerService();
        this.originalData = List.of();
        this.searchResultData = List.of();
        this.filteredData = List.of();
        this.currentKeyword = "";
        this.currentPage = 1;
        this.totalPages = 1;
    }

    /**
     * Tải lại danh sách khách hàng từ Service.
     */
    public void refreshCustomers() {
        view.setBusy(true);

        Task<List<KhachHangDTO>> task = new Task<List<KhachHangDTO>>() {
            @Override
            protected List<KhachHangDTO> call() throws SQLException {
                return new ArrayList<>(customerService.getActiveCustomers());
            }
        };

        task.setOnSucceeded(event -> {
            originalData = task.getValue();
            if (currentKeyword == null || currentKeyword.isEmpty()) {
                searchResultData = new ArrayList<>(originalData);
            }
            currentPage = 1;
            applyLocalFilters();
            view.setBusy(false);
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không tải được danh sách khách hàng.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Tìm kiếm khách hàng theo từ khóa.
     *
     * @param keyword từ khóa tìm kiếm
     */
    public void searchCustomers(String keyword) {
        currentKeyword = keyword == null ? "" : keyword.trim();
        currentPage = 1;

        if (currentKeyword.isEmpty()) {
            searchResultData = new ArrayList<>(originalData);
            applyLocalFilters();
        } else {
            view.setBusy(true);

            Task<List<KhachHangDTO>> task = new Task<List<KhachHangDTO>>() {
                @Override
                protected List<KhachHangDTO> call() throws SQLException {
                    return new ArrayList<>(customerService.searchCustomers(currentKeyword));
                }
            };

            task.setOnSucceeded(event -> {
                searchResultData = task.getValue();
                applyLocalFilters();
                view.setBusy(false);
            });

            task.setOnFailed(event -> {
                view.showErrorAlert("Lỗi", "Không thể tìm kiếm.\n" + task.getException().getMessage());
                view.setBusy(false);
            });

            new Thread(task).start();
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
        updateViewWithCurrentData();
    }

    /**
     * Xóa mềm khách hàng và tải lại danh sách.
     *
     * @param customerId mã khách hàng
     * @param employeeId mã nhân viên thực hiện xóa
     */
    public void deleteCustomerAndReload(int customerId, int employeeId) {
        view.setBusy(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                customerService.softDeleteCustomer(customerId, employeeId);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Xóa khách hàng thành công.");
            refreshCustomers();
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không thể xóa khách hàng.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Xuất danh sách khách hàng hiện tại ra Excel.
     *
     * @param file file Excel
     */
    public void exportCustomersToExcel(File file) {
        if (filteredData.isEmpty()) {
            view.showInfoAlert("Thông báo", "Không có dữ liệu để xuất.");
            return;
        }

        view.setBusy(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws IOException {
                DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
                    Sheet sheet = workbook.createSheet("KhachHang");

                    String[] headers = {"Mã KH", "Họ tên", "SĐT", "Địa chỉ", "Ngày đăng ký", "Điểm", "Hạng"};
                    Row headerRow = sheet.createRow(0);
                    for (int i = 0; i < headers.length; i++) {
                        Cell cell = headerRow.createCell(i);
                        cell.setCellValue(headers[i]);
                    }

                    int rowIdx = 1;
                    for (KhachHangDTO kh : filteredData) {
                        Row row = sheet.createRow(rowIdx++);
                        row.createCell(0).setCellValue(kh.getMaKH());
                        row.createCell(1).setCellValue(kh.getHoTen() == null ? "" : kh.getHoTen());
                        row.createCell(2).setCellValue(kh.getSdt() == null ? "" : kh.getSdt());
                        row.createCell(3).setCellValue(kh.getDiaChi() == null ? "" : kh.getDiaChi());
                        row.createCell(4).setCellValue(kh.getNgayDangKy() == null ? "" : kh.getNgayDangKy().format(dateFmt));
                        row.createCell(5).setCellValue(kh.getDiemTichLuy());
                        row.createCell(6).setCellValue(kh.getTenHang() == null ? "-" : kh.getTenHang());
                    }

                    for (int i = 0; i < headers.length; i++) {
                        sheet.autoSizeColumn(i);
                    }

                    workbook.write(out);
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Xuất Excel thành công.");
            view.setBusy(false);
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không thể xuất Excel.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Mở dialog thêm khách hàng mới.
     */
    public void openAddCustomerDialog() {
        viewFactory.openAddCustomerDialog(() -> refreshCustomers());
    }

    /**
     * Mở dialog cập nhật khách hàng.
     *
     * @param customer khách hàng cần cập nhật
     */
    public void openUpdateCustomerDialog(KhachHangDTO customer) {
        viewFactory.openUpdateCustomerDialog(customer, () -> refreshCustomers());
    }

    /**
     * Mở dialog thùng rác khách hàng.
     */
    public void openDeletedCustomersDialog() {
        viewFactory.openDeletedCustomersDialog(() -> refreshCustomers());
    }

    public void filterCustomers(LocalDate fromDate, LocalDate toDate, String tier) {
        this.filterFromDate = fromDate;
        this.filterToDate = toDate;
        this.filterTier = tier;
        this.currentPage = 1;
        applyLocalFilters();
    }

    private void applyLocalFilters() {
        if (searchResultData == null) {
            filteredData = new ArrayList<>();
        } else {
            filteredData = searchResultData.stream().filter(kh -> {
                boolean match = true;
                if (filterFromDate != null && kh.getNgayDangKy() != null) {
                    if (kh.getNgayDangKy().isBefore(filterFromDate)) match = false;
                }
                if (filterToDate != null && kh.getNgayDangKy() != null) {
                    if (kh.getNgayDangKy().isAfter(filterToDate)) match = false;
                }
                if (filterTier != null && !filterTier.isEmpty()) {
                    String tierName = kh.getTenHang() == null ? "" : kh.getTenHang();
                    if (!tierName.equalsIgnoreCase(filterTier)) match = false;
                }
                return match;
            }).collect(java.util.stream.Collectors.toList());
        }
        updateViewWithCurrentData();
    }

    // === PRIVATE HELPERS ===

    private void updateViewWithCurrentData() {
        if (filteredData.isEmpty()) {
            totalPages = 1;
            currentPage = 1;
            view.displayCustomers(List.of());
            view.updatePaginationInfo("Hiển thị 0-0 của 0");
            view.updateTotalCustomersCount(countActiveCustomers());
            view.updateNewCustomersThisMonth(countNewCustomersInMonth());
            view.updatePaginationControls(currentPage, totalPages);
            return;
        }

        totalPages = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        if (currentPage < 1) {
            currentPage = 1;
        }
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int fromIndex = (currentPage - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        List<KhachHangDTO> pageData = List.copyOf(filteredData.subList(fromIndex, toIndex));

        view.displayCustomers(pageData);
        view.updatePaginationInfo(String.format("Hiển thị %d-%d của %d", fromIndex + 1, toIndex, filteredData.size()));
        view.updateTotalCustomersCount(countActiveCustomers());
        view.updateNewCustomersThisMonth(countNewCustomersInMonth());
        view.updatePaginationControls(currentPage, totalPages);
    }

    private int countActiveCustomers() {
        try {
            return customerService.countActiveCustomers();
        } catch (Exception e) {
            System.err.println("Lỗi đếm khách hàng hoạt động: " + e.getMessage());
            return 0;
        }
    }

    private int countNewCustomersInMonth() {
        try {
            LocalDate now = LocalDate.now();
            return customerService.countNewCustomersInMonth(now.getYear(), now.getMonthValue());
        } catch (Exception e) {
            System.err.println("Lỗi đếm khách hàng mới: " + e.getMessage());
            return 0;
        }
    }
}