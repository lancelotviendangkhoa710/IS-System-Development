package com.bakery.presenters.hethong;

import com.bakery.model.dto.hethong.CauHinhGioiHanDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.services.hethong.CauHinhGioiHanService;
import com.bakery.services.kho.SanPhamService;
import com.bakery.views.interfaces.hethong.ICauHinhGioiHanView;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.List;

/** Orchestrator cho màn hình cấu hình giới hạn nhận đơn. */
public class CauHinhGioiHanPresenter {

    private final ICauHinhGioiHanView view;
    private final CauHinhGioiHanService service;
    private final SanPhamService sanPhamService;

    public CauHinhGioiHanPresenter(ICauHinhGioiHanView view) {
        this.view = view;
        this.service = new CauHinhGioiHanService();
        this.sanPhamService = new SanPhamService();
    }

    public void taiDuLieu() {
        // --- Tải danh sách cấu hình ---
        Task<List<CauHinhGioiHanDTO>> taskCauHinh = new Task<>() {
            @Override
            protected List<CauHinhGioiHanDTO> call() throws Exception {
                return service.layDanhSachCauHinh();
            }
        };
        taskCauHinh.setOnSucceeded(e -> view.hienThiDanhSachCauHinh(taskCauHinh.getValue()));
        taskCauHinh.setOnFailed(e -> {
            Throwable ex = taskCauHinh.getException();
            view.hienThiLoi("Lỗi tải dữ liệu: " + (ex != null ? ex.getMessage() : "Không xác định"));
        });
        new Thread(taskCauHinh, "cau-hinh-tai-du-lieu").start();

        // --- Task 5: Tải danh sách sản phẩm bán lẻ (có sẵn) ---
        Task<List<SanPhamDTO>> taskSanPham = new Task<>() {
            @Override
            protected List<SanPhamDTO> call() throws Exception {
                return sanPhamService.layDanhSachSanPhamPOS();
            }
        };
        taskSanPham.setOnSucceeded(e -> {
            // Bug 2 fix: null guard tránh NPE tại FXCollections.observableArrayList(null)
            List<SanPhamDTO> dsSP = taskSanPham.getValue();
            if (dsSP != null) {
                view.napDanhSachSanPhamBanLe(dsSP);
            }
        });
        taskSanPham.setOnFailed(e -> {
            Throwable ex = taskSanPham.getException();
            System.err.println("[CauHinhGioiHanPresenter] Không load được danh sách SP: "
                    + (ex != null ? ex.getMessage() : "?"));
        });
        // Bug 1 fix: setDaemon(true) để thread không block JVM shutdown
        Thread tSP = new Thread(taskSanPham, "cau-hinh-tai-sanpham");
        tSP.setDaemon(true);
        tSP.start();
    }

    /**
     * Lưu cấu hình giới hạn tùy chỉnh (custom cake) cho ngày hôm nay.
     * Task 6: Tách riêng path Tùy chỉnh.
     */
    public void luuCauHinhTuyChinh(String gioiHanStr) {
        luuCauHinh(LocalDate.now(), gioiHanStr, "giới hạn bánh tùy chỉnh");
    }

    /**
     * Lưu cấu hình giới hạn cho sản phẩm bán lẻ (bánh có sẵn) hôm nay.
     * Task 6: Tách riêng path Có sẵn.
     * Lưu ý: DB hiện tại (NANGLUCSANXUAT) áp dụng 1 giới hạn global/ngày;
     * khi schema hỗ trợ per-product (thêm cột MASP), cập nhật thêm ở đây.
     */
    public void luuCauHinhSanPhamBanLe(int maSP, String gioiHanStr) {
        luuCauHinh(LocalDate.now(), gioiHanStr, "giới hạn bánh có sẵn (mã SP: " + maSP + ")");
    }

    /**
     * Lưu cấu hình giới hạn cho ngày và giá trị cụ thể.
     */
    public void luuCauHinh(LocalDate ngay, String gioiHanStr, String loai) {
        int gioiHan;
        try {
            gioiHan = Integer.parseInt(gioiHanStr.trim());
            if (gioiHan <= 0)
                throw new NumberFormatException();
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
