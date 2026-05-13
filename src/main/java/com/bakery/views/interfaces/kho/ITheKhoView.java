package com.bakery.views.interfaces.kho;

import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.TheKhoBienDongDTO;
import com.bakery.views.interfaces.IBaseView;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface View cho màn hình Tra cứu thẻ kho nguyên liệu (UC44).
 * Tuân thủ MVP: Presenter chỉ gọi các method này, không biết JavaFX.
 */
public interface ITheKhoView extends IBaseView {

    /** Hiển thị danh sách biến động nhập/xuất lên bảng lịch sử. */
    void hienThiBienDong(List<TheKhoBienDongDTO> bienDong);

    /** Cập nhật 4 ô KPI tổng hợp kỳ. */
    void hienThiTongHop(double tonDauKy, double nhapKy, double xuatKy, double tonCuoiKy);

    /** Nạp danh sách nguyên liệu vào ComboBox bộ lọc. */
    void napDanhSachNguyenLieu(List<NguyenLieuDTO> danhSach);

    /** Lấy nguyên liệu đang được chọn trong ComboBox. */
    NguyenLieuDTO getNguyenLieuDangChon();

    /** Lấy giá trị từ ngày từ DatePicker. */
    LocalDate getTuNgay();

    /** Lấy giá trị đến ngày từ DatePicker. */
    LocalDate getDenNgay();
}
