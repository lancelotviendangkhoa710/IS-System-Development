package com.bakery.views.interfaces;

import com.bakery.model.dto.DanhMucSPDTO;

import java.util.List;

/**
 * Contract MVP giữa DanhMucSPPresenter và View (FXML Controller).
 * Tuân thủ parallel_agents.md: Presenter KHÔNG import bất kỳ class UI JavaFX nào.
 * UI Agent implement interface này để nhận lệnh từ Presenter.
 *
 * Quy ước:
 *  - Method "hiển thị dữ liệu" → nhận DTO, View tự map lên ObservableList / Label.
 *  - Method "getXxx" → View đọc state từ TextField / ComboBox trả về kiểu nguyên thủy.
 *  - Không có tham chiếu đến Node/Scene/Stage ở đây.
 */
public interface IDanhMucSPView {

    // ─── DANH SÁCH ─────────────────────────────────────────────────────

    /**
     * Đẩy danh sách danh mục lên TableView.
     * Controller cập nhật ObservableList và TableView sẽ tự refresh.
     *
     * @param dsDanhMuc danh sách danh mục đang hoạt động (chưa bị xóa mềm)
     */
    void hienThiDanhSach(List<DanhMucSPDTO> dsDanhMuc);

    // ─── FORM CHI TIẾT ─────────────────────────────────────────────────

    /**
     * Điền thông tin của một danh mục vào form khi người dùng chọn hàng.
     *
     * @param dm đối tượng danh mục được chọn; null = xóa chọn / reset
     */
    void hienThiChiTiet(DanhMucSPDTO dm);

    // ─── THÔNG BÁO ─────────────────────────────────────────────────────

    /**
     * Hiển thị thông báo lỗi nghiệp vụ (trùng tên, đang có sản phẩm…).
     *
     * @param msg nội dung lỗi rõ ràng, thân thiện người dùng
     */
    void hienThiLoi(String msg);

    /**
     * Hiển thị thông báo thao tác thành công.
     *
     * @param msg ví dụ: "Thêm danh mục thành công."
     */
    void hienThiThanhCong(String msg);

    // ─── FORM NHẬP LIỆU ────────────────────────────────────────────────

    /**
     * Làm trống form (xóa TextField, bỏ chọn TableView, reset trạng thái nút).
     * Gọi sau khi Thêm / Sửa / Xóa thành công.
     */
    void lamMoiForm();

    // ─── GETTERS (Presenter đọc state từ View) ─────────────────────────

    /**
     * @return danh mục đang được người dùng chọn trên TableView;
     *         null nếu chưa chọn hàng nào.
     */
    DanhMucSPDTO getSelectedCategory();

    /**
     * @return giá trị đã nhập trong TextField Tên Danh Mục (đã trim());
     *         trả về chuỗi rỗng nếu chưa nhập, KHÔNG BAO GIỜ trả null.
     */
    String getTenDanhMucInput();

    /**
     * @return từ khóa tìm kiếm người dùng nhập trong ô Search.
     */
    String getTuKhoaTimKiemInput();
}
