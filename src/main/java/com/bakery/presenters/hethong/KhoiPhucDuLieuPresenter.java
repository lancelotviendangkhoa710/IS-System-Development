package com.bakery.presenters.hethong;

import com.bakery.model.dto.hethong.KhoiPhucDuLieuDTO;
import com.bakery.services.hethong.KhoiPhucDuLieuService;
import com.bakery.views.interfaces.hethong.IKhoiPhucDuLieuView;
import javafx.concurrent.Task;

import java.util.List;

/**
 * Presenter cho màn hình Khôi phục dữ liệu (UC60).
 * Tất cả tác vụ DB chạy trong JavaFX Task để không treo UI.
 */
public class KhoiPhucDuLieuPresenter {

    private final IKhoiPhucDuLieuView view;
    private final KhoiPhucDuLieuService service = new KhoiPhucDuLieuService();

    public KhoiPhucDuLieuPresenter(IKhoiPhucDuLieuView view) {
        this.view = view;
    }

    /** Tải toàn bộ bản ghi đã xóa mềm (không lọc). */
    public void taiDuLieu() {
        loc(null);
    }

    /**
     * Lọc theo loại đối tượng.
     *
     * @param loaiDoiTuong "Tất cả" hoặc tên loại cụ thể
     */
    public void loc(String loaiDoiTuong) {
        view.setLoading(true);

        Task<List<KhoiPhucDuLieuDTO>> task = new Task<>() {
            @Override
            protected List<KhoiPhucDuLieuDTO> call() throws Exception {
                return service.layDanhSachDaXoa(loaiDoiTuong);
            }
        };

        task.setOnSucceeded(e -> {
            view.hienThiDanhSach(task.getValue());
            view.setLoading(false);
        });

        task.setOnFailed(e -> {
            view.hienThiLoi("Không thể tải dữ liệu: " + task.getException().getMessage());
            view.setLoading(false);
        });

        chayTask(task);
    }

    /**
     * Khôi phục bản ghi được chọn.
     *
     * @param dto     Bản ghi cần khôi phục
     * @param loaiBoLoc Loại đang lọc hiện tại (để reload đúng bộ lọc sau khi xong)
     */
    public void khoiPhuc(KhoiPhucDuLieuDTO dto, String loaiBoLoc) {
        if (dto == null) {
            view.hienThiLoi("Vui lòng chọn bản ghi cần khôi phục.");
            return;
        }
        view.setLoading(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                service.khoiPhuc(dto);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            view.hienThiThanhCong("✅ Đã khôi phục \"" + dto.getTenDoiTuong() + "\" thành công.");
            loc(loaiBoLoc); // reload danh sách sau khi khôi phục
        });

        task.setOnFailed(e -> {
            view.hienThiLoi("❌ Không thể khôi phục: " + task.getException().getMessage());
            view.setLoading(false);
        });

        chayTask(task);
    }

    /**
     * Xóa vĩnh viễn tất cả bản ghi quá 120 ngày.
     *
     * @param loaiBoLoc Loại đang lọc hiện tại (để reload đúng bộ lọc sau khi xong)
     */
    public void xoaVinhVienQuaHan(String loaiBoLoc) {
        view.setLoading(true);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return service.xoaVinhVienQuaHan();
            }
        };

        task.setOnSucceeded(e -> {
            view.hienThiThanhCong("🗑️ " + task.getValue());
            loc(loaiBoLoc); // reload sau khi purge
        });

        task.setOnFailed(e -> {
            view.hienThiLoi("❌ Lỗi xóa vĩnh viễn: " + task.getException().getMessage());
            view.setLoading(false);
        });

        chayTask(task);
    }

    /**
     * Tự động xóa vĩnh viễn bản ghi quá hạn — chạy hoàn toàn ngầm,
     * không hiện confirm, không thay đổi trạng thái loading.
     * Chỉ thông báo nếu thực sự có bản ghi bị xóa.
     *
     * @param loaiBoLoc Loại đang lọc (để reload sau khi purge)
     */
    public void tuDongXoaQuaHan(String loaiBoLoc) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return service.xoaVinhVienQuaHan();
            }
        };

        task.setOnSucceeded(e -> {
            String ketQua = task.getValue();
            // Chỉ thông báo khi thực sự có bản ghi bị xóa (không làm phiền nếu không có gì)
            if (ketQua != null && !ketQua.contains("Không có")) {
                view.hienThiThanhCong("🗑️ Tự động: " + ketQua);
            }
            loc(loaiBoLoc); // reload để UI luôn đồng bộ với DB
        });

        task.setOnFailed(e -> {
            // Lỗi auto-purge chỉ log, không làm phiền UI
            System.err.println("[KhoiPhucDuLieuPresenter] Lỗi tự động purge: "
                    + task.getException().getMessage());
        });

        chayTask(task);
    }

    private void chayTask(Task<?> task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /** Trả về danh sách loại đối tượng để populate ComboBox lọc. */
    public List<String> layDanhSachLoai() {
        return service.layDanhSachLoai();
    }
}
