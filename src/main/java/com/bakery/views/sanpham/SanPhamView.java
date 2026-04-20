package com.bakery.views.sanpham;

public interface SanPhamView {
    SanPhamDTO getSanPhamFromForm();
    String getSelectedProductId();
    
    // Các hàm Cập nhật giao diện
    void showProductList(List<SanPhamDTO> list);
    void showMessage(String title, String message, boolean isError);
    void clearForm();
}
