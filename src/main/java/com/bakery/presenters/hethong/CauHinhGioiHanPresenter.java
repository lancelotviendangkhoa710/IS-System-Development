package com.bakery.presenters.hethong;

import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.services.hethong.CauHinhGioiHanService;
import com.bakery.views.interfaces.hethong.ICauHinhGioiHanView;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.List;

/** Orchestrator cho màn hình cấu hình giới hạn nhận đơn. */
public class CauHinhGioiHanPresenter {

    private final ICauHinhGioiHanView view;
    private final CauHinhGioiHanService service;

    public CauHinhGioiHanPresenter(ICauHinhGioiHanView view) {
        this.view = view;
        this.service = new CauHinhGioiHanService();
    }

    /** Tải danh sách cấu hình hiện tại từ DB và đẩy lên View. */
    public void taiDuLieu() {
        Task<List<CauHinhGioiHanDTO>> task = new Task<>() {
            @Override
            protected List<CauHinhGioiHanDTO> call() throws Exception {
                return service.layDanhSachCauHinh();
            }
        };

        task.setOnSucceeded(e -> view.hienThiDanhSachCauHinh(task.getValue()));
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            view.hienThiLoi("Lỗi tải dữ liệu: " + (ex != null ? ex.getMessage() : "Không xác định"));
        });

        Thread t = new Thread(task, "cau-hinh-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    /**
     * Lưu cấu hình giới hạn tùy chỉnh (custom cake) cho ngày hôm nay.
     * @param gioiHanStr chuỗi nhập từ TextField
     */
    public void luuCauHinhTuyChinh(String gioiHanStr) {
        luuCauHinh(LocalDate.now(), gioiHanStr, "giới hạn bánh tùy chỉnh");
    }

    /**
     * Lưu cấu hình giới hạn cho ngày và giá trị cụ thể.
     * @param ngay ngày áp dụng
     * @param gioiHanStr chuỗi số nguyên dương từ UI
     * @param loai tên loại (dùng cho thông báo lỗi)
     */
    public void luuCauHinh(LocalDate ngay, String gioiHanStr, String loai) {
        int gioiHan;
        try {
            gioiHan = Integer.parseInt(gioiHanStr.trim());
            if (gioiHan <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            view.hienThiLoi("Giới hạn " + loai + " phải là số nguyên dương.");
            return;
        }

        final int gioiHanFinal = gioiHan;
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.luuCauHinh(ngay, gioiHanFinal);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            view.hienThiThongBao("Đã lưu cấu hình thành công.");
            view.lamMoiForm();
            taiDuLieu();
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            view.hienThiLoi("Lỗi lưu cấu hình: " + (ex != null ? ex.getMessage() : "Không xác định"));
        });

        Thread t = new Thread(task, "cau-hinh-luu");
        t.setDaemon(true);
        t.start();
    }
}
