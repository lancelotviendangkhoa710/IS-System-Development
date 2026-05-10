package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.HoatDongNhanVienDTO;
import com.bakery.views.interfaces.IBaseView;

import java.util.List;

/** View interface cho màn hình Lịch sử hệ thống (Employee Activity Audit Log). */
public interface LichSuHeThongView extends IBaseView {

    /** Hiển thị danh sách hoạt động lên TableView. */
    void hienThiDanhSachHoatDong(List<HoatDongNhanVienDTO> danhSach);

    /** Hiển thị trạng thái đang tải. */
    void batTatTrangThaiDangTai(boolean dangTai);
}
