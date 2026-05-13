package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.MeSanXuatDTO;
import com.bakery.presenters.BasePresenter;
import com.bakery.services.kho.TraCuuNguonGocService;
import com.bakery.views.interfaces.kho.ITraCuuNguonGocView;

import java.time.LocalDate;

/**
 * Presenter điều phối màn hình Truy xuất nguồn gốc nguyên liệu.
 * Tuân thủ MVP: không import JavaFX, không chứa logic nghiệp vụ.
 */
public class TraCuuNguonGocPresenter extends BasePresenter<ITraCuuNguonGocView> {

    private final TraCuuNguonGocService service;

    public TraCuuNguonGocPresenter(ITraCuuNguonGocView view) {
        super(view);
        this.service = new TraCuuNguonGocService();
    }

    // ── Khởi tạo ──────────────────────────────────────────────────────────────

    /** Gọi khi initialize() — nạp toàn bộ danh sách mẻ sản xuất ban đầu. */
    public void khoiTao() {
        timKiem(null, null, null);
    }

    // ── Tìm kiếm / lọc mẻ ─────────────────────────────────────────────────────

    /**
     * Lọc danh sách mẻ sản xuất theo từ khóa và khoảng ngày.
     *
     * @param tuKhoa  tên sản phẩm (null = không lọc)
     * @param tuNgay  từ ngày (null = không giới hạn)
     * @param denNgay đến ngày (null = không giới hạn)
     */
    public void timKiem(String tuKhoa, LocalDate tuNgay, LocalDate denNgay) {
        runTask(
            () -> service.layDanhSachMe(tuKhoa, tuNgay, denNgay),
            ds -> {
                view.hienThiDanhSachMe(ds);
                view.hienThiThongBao("Tìm thấy " + ds.size() + " mẻ sản xuất.");
            }
        );
    }

    // ── Chọn mẻ → nạp chi tiết NL ──────────────────────────────────────────────

    /** Gọi khi người dùng chọn 1 hàng trên bảng mẻ sản xuất. */
    public void onChonMe(MeSanXuatDTO selected) {
        if (selected == null) return;
        runTask(
            () -> service.layChiTietNguonGoc(selected.getMaMe()),
            chiTiet -> {
                view.hienThiChiTietNguonGoc(chiTiet);
                view.hienThiThongBao(
                    "Mẻ #" + selected.getMaMe() + " — " + selected.getTenSP()
                    + " — " + chiTiet.size() + " lô nguyên liệu."
                );
            }
        );
    }

    // ── Nút tìm kiếm từ View ───────────────────────────────────────────────────

    /** Nút 🔍 — lấy từ khóa từ View rồi tìm kiếm (không lọc ngày). */
    public void onTimKiem(LocalDate tuNgay, LocalDate denNgay) {
        String tuKhoa = view.getTuKhoaInput();
        timKiem(tuKhoa, tuNgay, denNgay);
    }
}
