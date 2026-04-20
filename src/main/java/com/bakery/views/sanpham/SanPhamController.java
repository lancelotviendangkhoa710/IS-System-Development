package com.bakery.views.sanpham;

public class SanPhamViewController implements ISanPhamView {
    @FXML private TextField txtTenSP;
    // ... các FXML khác
    
    // Nắm giữ Presenter
    private SanPhamPresenter presenter;

    @FXML
    public void initialize() {
        // Khởi tạo Presenter và truyền chính View này vào
        presenter = new SanPhamPresenter(this);
        presenter.loadData(); // Nhờ Presenter lấy data
    }

    @FXML
    private void handleThem(ActionEvent event) {
        // Chỉ làm nhiệm vụ báo cáo sự kiện cho Presenter
        presenter.onAddButtonClicked();
    }

    // --- Implement các hàm của ISanPhamView ở đây ---
    @Override
    public SanPhamDTO getSanPhamFromForm() {
        // Code lấy dữ liệu từ txtTenSP, txtGia... trả về DTO
    }

    @Override
    public void showProductList(List<SanPhamDTO> list) {
        // Code đổ list vào tvSanPham
    }
}
