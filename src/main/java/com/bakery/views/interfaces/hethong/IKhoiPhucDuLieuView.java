package com.bakery.views.interfaces.hethong;

import com.bakery.model.dto.hethong.KhoiPhucDuLieuDTO;
import com.bakery.views.interfaces.IBaseView;

import java.util.List;

/** View interface cho màn hình Khôi phục dữ liệu (UC60). */
public interface IKhoiPhucDuLieuView extends IBaseView {

    /** Hiển thị danh sách bản ghi đã xóa lên TableView. */
    void hienThiDanhSach(List<KhoiPhucDuLieuDTO> danhSach);

    /** Bật/tắt trạng thái loading. */
    void setLoading(boolean loading);
}
