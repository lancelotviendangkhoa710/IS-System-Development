package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.DanhMucSPDTO;

import java.util.List;

/**
 * Interface IDanhMucSPView định nghĩa các phương thức giao tiếp giữa Presenter và View (FXML Controller)
 * cho chức năng quản lý Danh mục sản phẩm.
 * 
 * Tuân thủ kiến trúc MVP: Presenter điều khiển View thông qua interface này, đảm bảo không phụ thuộc vào thư viện UI.
 */
public interface IDanhMucSPView {

    // ——— HIỂN THỊ DANH SÁCH ———

    /**
     * Hiển thị danh sách các danh mục sản phẩm lên bảng (TableView).
     * @param dsDanhMuc Danh sách các đối tượng DTO danh mục.
     */
    void hienThiDanhSach(List<DanhMucSPDTO> dsDanhMuc);

    // ——— FORM CHI TIẾT ———

    /**
     * Điền dữ liệu của một danh mục vào các trường nhập liệu khi người dùng chọn dòng trên bảng.
     * @param dm Đối tượng danh mục được chọn; null để xóa trắng các trường.
     */
    void hienThiChiTiet(DanhMucSPDTO dm);

    // ——— THÔNG BÁO ———

    /**
     * Hiển thị thông báo lỗi (ví dụ: Tên danh mục đã tồn tại, danh mục đang chứa sản phẩm, v.v.).
     */
    void hienThiLoi(String msg);

    /**
     * Hiển thị thông báo khi thực hiện thao tác thành công.
     */
    void hienThiThanhCong(String msg);

    // ——— QUẢN LÝ FORM ———

    /**
     * Làm mới (reset) các trường nhập liệu và trạng thái của các nút bấm trên giao diện.
     */
    void lamMoiForm();

    // ——— TRUY XUẤT TRẠNG THÁI (GETTERS) ———

    /**
     * @return Đối tượng danh mục đang được chọn trên bảng; null nếu chưa chọn.
     */
    DanhMucSPDTO getSelectedCategory();

    /**
     * @return Tên danh mục người dùng đã nhập (đã xử lý cắt khoảng trắng thừa).
     */
    String getTenDanhMucInput();

    /**
     * @return Từ khóa tìm kiếm người dùng nhập vào.
     */
    String getTuKhoaTimKiemInput();
}
