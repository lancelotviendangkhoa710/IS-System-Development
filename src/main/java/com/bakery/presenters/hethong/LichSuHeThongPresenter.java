package com.bakery.presenters.hethong;

import com.bakery.model.dto.hethong.HoatDongNhanVienDTO;
import com.bakery.services.hethong.HoatDongNhanVienService;
import com.bakery.views.interfaces.hethong.LichSuHeThongView;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.List;

/**
 * Presenter cho màn hình Lịch sử hệ thống.
 * Tất cả truy vấn DB được chạy trong JavaFX Task để không treo UI.
 */
public class LichSuHeThongPresenter {

    private final LichSuHeThongView    view;
    private final HoatDongNhanVienService service = new HoatDongNhanVienService();

    public LichSuHeThongPresenter(LichSuHeThongView view) {
        this.view = view;
    }

    /** Tải toàn bộ dữ liệu không lọc. */
    public void taiDuLieu() {
        loc(null, null, null, null);
    }

    /**
     * Lọc hoạt động theo tiêu chí.
     *
     * @param nhom    Nhóm module (null = tất cả)
     * @param tuKhoa  Từ khóa tìm kiếm NV / hành động (null = bỏ qua)
     * @param tuNgay  Từ ngày (null = bỏ qua)
     * @param denNgay Đến ngày (null = bỏ qua)
     */
    public void loc(String nhom, String tuKhoa, LocalDate tuNgay, LocalDate denNgay) {
        view.batTatTrangThaiDangTai(true);

        Task<List<HoatDongNhanVienDTO>> task = new Task<>() {
            @Override
            protected List<HoatDongNhanVienDTO> call() {
                return service.layDanhSach(nhom, tuKhoa, tuNgay, denNgay);
            }
        };

        task.setOnSucceeded(e -> {
            view.hienThiDanhSachHoatDong(task.getValue());
            view.batTatTrangThaiDangTai(false);
        });

        task.setOnFailed(e -> {
            view.hienThiLoi("Không thể tải lịch sử hệ thống: "
                    + task.getException().getMessage());
            view.batTatTrangThaiDangTai(false);
        });

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}
