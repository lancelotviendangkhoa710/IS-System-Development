package com.bakery.views.interfaces;

import com.bakery.model.dto.DonViTinhDTO;
import com.bakery.model.dto.NguyenLieuDTO;

import java.util.List;

/**
 * Contract MVP giữa NguyenLieuPresenter và View (FXML Controller).
 * Presenter giao tiếp qua interface này — KHÔNG import bất kỳ class JavaFX nào.
 */
public interface INguyenLieuView {

    // ─── DANH SÁCH ─────────────────────────────────────────────────────

    /** Đẩy danh sách nguyên liệu lên TableView. */
    void hienThiDanhSach(List<NguyenLieuDTO> ds);

    /** Nạp danh sách đơn vị tính vào ComboBox. */
    void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT);

    // ─── FORM CHI TIẾT ─────────────────────────────────────────────────

    /** Điền thông tin nguyên liệu vào form khi người dùng chọn hàng. */
    void hienThiChiTiet(NguyenLieuDTO nl);

    // ─── THÔNG BÁO ─────────────────────────────────────────────────────

    /** Hiển thị thông báo lỗi nghiệp vụ. */
    void hienThiLoi(String msg);

    /** Hiển thị thông báo thao tác thành công. */
    void hienThiThanhCong(String msg);

    // ─── FORM ──────────────────────────────────────────────────────────

    /** Làm trống form, bỏ chọn bảng, reset trạng thái nút. */
    void lamMoiForm();

    // ─── GETTERS ───────────────────────────────────────────────────────

    /** @return nguyên liệu đang chọn trên TableView; null nếu chưa chọn. */
    NguyenLieuDTO getSelectedNguyenLieu();

    /** @return tên nhập trong TextField, đã trim(), KHÔNG BAO GIỜ null. */
    String getTenNLInput();

    /** @return xuất xứ nhập trong TextField, đã trim(). */
    String getXuatXuInput();

    /** @return mức tồn an toàn nhập trong TextField; -1 nếu không hợp lệ. */
    double getMucTonAnToanInput();

    /** @return Đơn vị tính được chọn trong ComboBox; null nếu chưa chọn. */
    DonViTinhDTO getDonViTinhSelected();

    /** @return từ khóa tìm kiếm, đã trim(). */
    String getTuKhoaTimKiemInput();
}
