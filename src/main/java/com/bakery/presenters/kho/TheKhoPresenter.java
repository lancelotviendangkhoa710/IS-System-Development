package com.bakery.presenters.kho;

import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.presenters.BasePresenter;
import com.bakery.services.kho.NguyenLieuService;
import com.bakery.services.kho.TheKhoService;
import com.bakery.views.interfaces.kho.ITheKhoView;

import java.time.LocalDate;
import java.util.List;

/**
 * Presenter điều phối màn hình Tra cứu thẻ kho nguyên liệu (UC44).
 * Tuân thủ MVP: không import JavaFX, không chứa logic nghiệp vụ.
 */
public class TheKhoPresenter extends BasePresenter<ITheKhoView> {

    private final TheKhoService    theKhoService;
    private final NguyenLieuService nguyenLieuService;

    public TheKhoPresenter(ITheKhoView view) {
        super(view);
        this.theKhoService    = new TheKhoService();
        this.nguyenLieuService = new NguyenLieuService();
    }

    // ── Khởi tạo ──────────────────────────────────────────────────────────────

    /**
     * Gọi khi initialize() — nạp danh sách nguyên liệu vào ComboBox.
     * Nếu có nguyên liệu → tự động xem thẻ kho của NL đầu tiên.
     */
    public void khoiTao() {
        runTask(
            () -> nguyenLieuService.layDanhSachNguyenLieu(),
            danhSach -> {
                view.napDanhSachNguyenLieu(danhSach);
                if (!danhSach.isEmpty()) {
                    // Tự động tìm kiếm nguyên liệu đầu tiên
                    xemTheKho(danhSach.get(0).getMaNL(), null, null);
                }
            }
        );
    }

    // ── Tra cứu thẻ kho ───────────────────────────────────────────────────────

    /**
     * Tìm kiếm biến động và tổng hợp kỳ theo NL + date range.
     * Gọi khi bấm nút "Xem thẻ kho" hoặc đổi nguyên liệu.
     */
    public void onTimKiem() {
        NguyenLieuDTO nl = view.getNguyenLieuDangChon();
        if (nl == null) {
            view.hienThiLoi("Vui lòng chọn nguyên liệu cần tra cứu.");
            return;
        }
        LocalDate tuNgay  = view.getTuNgay();
        LocalDate denNgay = view.getDenNgay();
        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            view.hienThiLoi("Từ ngày phải nhỏ hơn hoặc bằng đến ngày.");
            return;
        }
        xemTheKho(nl.getMaNL(), tuNgay, denNgay);
    }

    /**
     * Xoá bộ lọc và reset về toàn bộ lịch sử.
     */
    public void onXoaLoc() {
        NguyenLieuDTO nl = view.getNguyenLieuDangChon();
        if (nl == null) return;
        xemTheKho(nl.getMaNL(), null, null);
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void xemTheKho(int maNL, LocalDate tuNgay, LocalDate denNgay) {
        view.xoaLoi();
        runTask(
            () -> {
                List<com.bakery.model.dto.kho.TheKhoBienDongDTO> bienDong =
                        theKhoService.layBienDong(maNL, tuNgay, denNgay);
                double[] tongHop = theKhoService.layTongHop(maNL, tuNgay, denNgay);
                return new Object[]{bienDong, tongHop};
            },
            result -> {
                @SuppressWarnings("unchecked")
                List<com.bakery.model.dto.kho.TheKhoBienDongDTO> bienDong =
                        (List<com.bakery.model.dto.kho.TheKhoBienDongDTO>) result[0];
                double[] tongHop = (double[]) result[1];
                view.hienThiBienDong(bienDong);
                view.hienThiTongHop(tongHop[0], tongHop[1], tongHop[2], tongHop[3]);
                view.hienThiThanhCong("Tìm thấy " + bienDong.size() + " giao dịch.");
            }
        );
    }
}
