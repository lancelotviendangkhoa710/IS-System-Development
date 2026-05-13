package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.MeSanXuatDTO;
import com.bakery.model.dto.kho.TraCuuNguonGocDTO;
import com.bakery.views.interfaces.IBaseView;

import java.util.List;

/**
 * Interface View cho màn hình Truy xuất nguồn gốc nguyên liệu.
 * Tuân thủ MVP: Presenter chỉ gọi các method này, không biết JavaFX.
 */
public interface ITraCuuNguonGocView extends IBaseView {

    /** Hiển thị danh sách mẻ sản xuất lên bảng trái. */
    void hienThiDanhSachMe(List<MeSanXuatDTO> danhSach);

    /** Hiển thị chi tiết lô nguyên liệu của mẻ được chọn lên bảng phải. */
    void hienThiChiTietNguonGoc(List<TraCuuNguonGocDTO> chiTiet);

    /** Hiển thị thông báo thành công / thông tin. */
    void hienThiThongBao(String thongBao);

    /** Hiển thị thông báo lỗi. */
    void hienThiLoi(String loiMessage);

    /** Lấy từ khóa tìm kiếm tên sản phẩm từ ô input. */
    String getTuKhoaInput();
}
