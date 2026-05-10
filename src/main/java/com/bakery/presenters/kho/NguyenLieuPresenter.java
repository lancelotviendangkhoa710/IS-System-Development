package com.bakery.presenters.kho;

import com.bakery.presenters.BasePresenter;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.services.kho.NguyenLieuService;
import com.bakery.views.interfaces.kho.INguyenLieuView;

import java.time.LocalDate;

/**
 * Presenter điều phối màn hình Quản lý Nguyên liệu.
 * Tuân thủ MVP: không chứa logic nghiệp vụ, không biết JavaFX.
 */
public class NguyenLieuPresenter extends BasePresenter<INguyenLieuView> {

    private final NguyenLieuService nguyenLieuService;
    private final int maNvHienTai;

    public NguyenLieuPresenter(INguyenLieuView view, int maNvHienTai) {
        super(view);
        this.nguyenLieuService = new NguyenLieuService();
        this.maNvHienTai = maNvHienTai;
    }

    public NguyenLieuPresenter(INguyenLieuView view, NguyenLieuService nguyenLieuService, int maNvHienTai) {
        super(view);
        this.nguyenLieuService = nguyenLieuService;
        this.maNvHienTai = maNvHienTai;
    }

    // ── Khởi tạo ──────────────────────────────────────────────────────────────

    /** Gọi khi initialize(): nạp ComboBox DVT + NCC rồi tải bảng. */
    public void khoiTao() {
        runTask(
            () -> nguyenLieuService.layDanhSachDonViTinh(),
            dsDVT -> {
                view.napDanhSachDonViTinh(dsDVT);
                runTask(
                    () -> nguyenLieuService.layDanhSachNhaCungCap(),
                    dsNCC -> {
                        view.napDanhSachNhaCungCap(dsNCC);
                        taiDanhSach();
                    }
                );
            }
        );
    }

    // ── Tải danh sách ─────────────────────────────────────────────────────────

    public void taiDanhSach() {
        runTask(
            () -> nguyenLieuService.layDanhSachNguyenLieu(),
            ds -> view.hienThiDanhSach(ds)
        );
    }

    // ── Thêm ──────────────────────────────────────────────────────────────────

    /** Thêm nguyên liệu kèm nhập kho lần đầu (atomic). */
    public void themNguyenLieuVaNhapKho(
            String tenNL, String xuatXu, double mucTon, int maDVT,
            int maNCC, double soLuong, double donGia,
            LocalDate ngaySanXuat, LocalDate hanSuDung) {
        runTask(
            () -> nguyenLieuService.themNguyenLieuVaNhapKho(
                    tenNL, xuatXu, mucTon, maDVT, maNCC, maNvHienTai,
                    soLuong, donGia, ngaySanXuat, hanSuDung),
            result -> {
                view.hienThiThanhCong("Thêm nguyên liệu '" + tenNL + "' thành công (Mã NL: "
                        + result[0] + ", Phiếu nhập: #" + result[1] + ").");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
    }

    // ── Sửa ───────────────────────────────────────────────────────────────────

    public void suaNguyenLieu() {
        NguyenLieuDTO selected = view.getSelectedNguyenLieu();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn nguyên liệu cần sửa.");
            return;
        }
        DonViTinhDTO dvt = view.getDonViTinhSelected();
        int maDVT = dvt != null ? dvt.getMaDVT() : 0;

        runTask(
            () -> nguyenLieuService.suaNguyenLieu(
                    selected.getMaNL(),
                    view.getTenNLInput(),
                    view.getXuatXuInput(),
                    view.getMucTonAnToanInput(),
                    maDVT),
            () -> {
                view.hienThiThanhCong("Cập nhật nguyên liệu thành công.");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
    }

    // ── Xóa ───────────────────────────────────────────────────────────────────

    public void xoaNguyenLieu() {
        NguyenLieuDTO selected = view.getSelectedNguyenLieu();
        if (selected == null) {
            view.hienThiLoi("Vui lòng chọn nguyên liệu cần xóa.");
            return;
        }

        runTask(
            () -> nguyenLieuService.xoaNguyenLieu(selected.getMaNL(), maNvHienTai),
            () -> {
                view.hienThiThanhCong("Xóa nguyên liệu thành công.");
                view.lamMoiForm();
                taiDanhSach();
            }
        );
    }

    // ── Tìm kiếm ──────────────────────────────────────────────────────────────

    public void timKiem() {
        String keyword = view.getTuKhoaTimKiemInput();
        runTask(
            () -> nguyenLieuService.timKiemNguyenLieu(keyword),
            ds -> {
                view.hienThiDanhSach(ds);
                view.hienThiThanhCong("Tìm thấy " + ds.size() + " nguyên liệu.");
            }
        );
    }

    // ── Chọn hàng ─────────────────────────────────────────────────────────────

    public void onChonNguyenLieu(NguyenLieuDTO selected) {
        view.hienThiChiTiet(selected);
    }
}
