package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;

import java.util.List;

/**
 * Interface INguyenLieuView định nghĩa các phương thức giao tiếp giữa Presenter và View
 * cho màn hình quản lý Nguyên Liệu theo kiến trúc MVP.
 * 
 * Tuân thủ quy tắc: Presenter giao tiếp qua interface này và không phụ thuộc vào JavaFX.
 */
public interface INguyenLieuView extends IBaseView {

    // ——— HIỂN THỊ DANH SÁCH ———

    /** 
     * Đẩy danh sách nguyên liệu lên TableView để hiển thị.
     */
    void hienThiDanhSach(List<NguyenLieuDTO> ds);

    /** 
     * Nạp danh sách đơn vị tính (DVT) vào ComboBox cho người dùng chọn.
     */
    void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT);

    // ——— FORM CHI TIẾT ———

    /** 
     * Điền thông tin nguyên liệu cụ thể vào form khi người dùng chọn một hàng trong bảng.
     */
    void hienThiChiTiet(NguyenLieuDTO nl);


    // ——— QUẢN LÝ FORM ———

    /** 
     * Làm trống (reset) form nhập liệu, bỏ chọn hàng trên bảng và reset trạng thái nút.
     */
    void lamMoiForm();

    // ——— TRUY XUẤT DỮ LIỆU NHẬP (GETTERS) ———

    /** 
     * Lấy đối tượng nguyên liệu đang được chọn trên TableView.
     * @return NguyenLieuDTO hoặc null nếu chưa chọn.
     */
    NguyenLieuDTO getSelectedNguyenLieu();

    /** 
     * Lấy tên nguyên liệu từ TextField (đã được cắt khoảng trắng thừa).
     * @return Chuỗi văn bản, không bao giờ null.
     */
    String getTenNLInput();

    /** 
     * Lấy xuất xứ nguyên liệu từ TextField.
     */
    String getXuatXuInput();

    /** 
     * Lấy mức tồn an toàn nhập từ giao diện.
     * @return Giá trị số thực; trả về -1 nếu dữ liệu không hợp lệ.
     */
    double getMucTonAnToanInput();

    /** 
     * Lấy đơn vị tính đang được chọn trong ComboBox.
     */
    DonViTinhDTO getDonViTinhSelected();

    /** 
     * Lấy từ khóa tìm kiếm người dùng nhập.
     */
    String getTuKhoaTimKiemInput();
}
