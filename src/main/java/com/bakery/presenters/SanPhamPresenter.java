package com.bakery.presenters;

import com.bakery.views.sanpham.SanPhamView;
import com.bakery.models.dao.SanPhamDAO;
import com.bakery.models.dto.SanPhamDTO;
import java.util.List;

public class SanPhamPresenter {
    private SanPhamView view;
    private SanPhamDAO dao; // Hoặc SanPhamService

    public SanPhamPresenter(SanPhamView view) {
        this.view = view;
        this.dao = new SanPhamDAO();
    }

    public void loadData() {
        try {
            List<SanPhamDTO> list = dao.layTatCaSanPham();
            view.showProductList(list);
        } catch (Exception e) {
            view.showMessage("Lỗi", "Không thể tải dữ liệu", true);
        }
    }

    public void onAddButtonClicked() {
        try {
            // 1. Xin View dữ liệu người dùng vừa nhập
            SanPhamDTO sp = view.getSanPhamFromForm();
            
            // 2. (Thêm logic validate ở đây nếu cần)
            
            // 3. Gọi DB để lưu
            int idMoi = dao.themSanPham(sp);
            
            // 4. Ra lệnh cho View cập nhật UI
            view.showMessage("Thành công", "Đã thêm SP: " + idMoi, false);
            view.clearForm();
            loadData();
        } catch (Exception e) {
            view.showMessage("Lỗi", e.getMessage(), true);
        }
    }
}