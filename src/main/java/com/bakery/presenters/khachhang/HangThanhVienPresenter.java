package com.bakery.presenters.khachhang;

import com.bakery.model.dto.khachhang.HangThanhVienDTO;
import com.bakery.services.khachhang.HangThanhVienService;
import com.bakery.views.interfaces.khachhang.HangThanhVienView;

import java.sql.SQLException;
import java.util.List;
import javafx.concurrent.Task;

/**
 * Presenter cho màn hình Quản lý Hạng Thành viên.
 * Xử lý tải danh sách hạng và cập nhật hạng.
 */
public class HangThanhVienPresenter {

    private final HangThanhVienService tierService;
    private final HangThanhVienView view;

    public HangThanhVienPresenter(HangThanhVienView view) {
        this.view = view;
        this.tierService = new HangThanhVienService();
    }

    /**
     * Tải danh sách hạng thành viên từ Service.
     */
    public void loadTiers() {
        view.setBusy(true);

        Task<List<HangThanhVienDTO>> task = new Task<List<HangThanhVienDTO>>() {
            @Override
            protected List<HangThanhVienDTO> call() throws SQLException {
                return tierService.getAllTiers();
            }
        };

        task.setOnSucceeded(event -> {
            view.displayTiers(task.getValue());
            view.setBusy(false);
        });

        task.setOnFailed(event -> {
            view.showErrorAlert("Lỗi", "Không tải được danh sách hạng.\n" + task.getException().getMessage());
            view.setBusy(false);
        });

        new Thread(task).start();
    }

    /**
     * Cập nhật thông tin hạng thành viên.
     *
     * @param tier thông tin hạng cần cập nhật
     */
    public void updateTier(HangThanhVienDTO tier) {
        view.setBusy(true);

        // Validate dữ liệu
        String tenHang = tier.getTenHang();
        int diemToiThieu = tier.getDiemToiThieu();
        double phanTramGiamGia = tier.getPhanTramGiamGia().doubleValue();

        if (!validateTierForm(tenHang, diemToiThieu, phanTramGiamGia)) {
            view.setBusy(false);
            return;
        }

        // Chạy trong background thread
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws SQLException {
                tierService.updateTier(tier);
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            view.showSuccessAlert("Thành công", "Hạng thành viên được cập nhật thành công.");
            loadTiers();
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

    private boolean validateTierForm(String tenHang, int diemToiThieu, double phanTramGiamGia) {
        boolean isValid = true;

        // Validate tên hạng
        if (tenHang == null || tenHang.trim().isEmpty()) {
            view.setTierNameError("Tên hạng không được để trống");
            isValid = false;
        } else {
            view.setTierNameError(null);
        }

        // Validate điểm tối thiểu
        if (diemToiThieu < 0) {
            view.setMinPointsError("Điểm tối thiểu phải >= 0");
            isValid = false;
        } else {
            view.setMinPointsError(null);
        }

        // Validate phần trăm giảm giá
        if (phanTramGiamGia < 0 || phanTramGiamGia > 100) {
            view.setDiscountError("Phần trăm giảm giá phải từ 0 đến 100");
            isValid = false;
        } else {
            view.setDiscountError(null);
        }

        return isValid;
    }
}
