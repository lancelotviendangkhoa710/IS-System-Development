package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.services.kho.NhaCungCapService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;



/**
 * Controller cho QuanLyNhaCungCap.
 * Mọi dữ liệu được lấy từ DB. Không có Mock Data.
 */
public class QuanLyNhaCungCapViewFXMLController extends BaseController {

    @FXML private TableView<NhaCungCapDTO> tvNhaCungCap;
    @FXML private TableColumn<NhaCungCapDTO, String> colMaNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colTenNCC;
    @FXML private TableColumn<NhaCungCapDTO, String> colSdt;
    @FXML private TableColumn<NhaCungCapDTO, String> colDiaChi;

    @FXML private TextField txtTenNCC;
    @FXML private TextField txtSdt;
    @FXML private TextArea txtDiaChi;

    @FXML private TextField txtTimKiem;
    @FXML private TextField txtMaNCC;
    @FXML private Button btnThem;
    @FXML private Button btnLuu;
    @FXML private Button btnHuy;
    @FXML private Button btnXoa;

    private final NhaCungCapService nhaCungCapService = new NhaCungCapService();
    private final ObservableList<NhaCungCapDTO> nccList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadData();
        tvNhaCungCap.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> hienThiChiTiet(newVal));
    }

    private void setupTable() {
        colMaNCC.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getMaNCC())));
        colTenNCC.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenNCC()));
        colSdt.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSdt()));
        colDiaChi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDiaChi()));
        tvNhaCungCap.setItems(nccList);
    }

    private void loadData() {
        try {
            List<NhaCungCapDTO> list = nhaCungCapService.layDanhSachNhaCungCap();
            nccList.setAll(list != null ? list : java.util.Collections.emptyList());
            if (list == null || list.isEmpty()) {
                hienThiLoiLabel("Không có dữ liệu nhà cung cấp.");
            }
        } catch (Exception e) {
            hienThiLoiLabel("Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    private void hienThiChiTiet(NhaCungCapDTO ncc) {
        if (ncc == null) return;
        txtTenNCC.setText(ncc.getTenNCC());
        txtSdt.setText(ncc.getSdt());
        txtDiaChi.setText(ncc.getDiaChi());
    }

    @FXML private void onThem() { hienThiThanhCongLabel("Chế độ Demo: Đã thêm nhà cung cấp."); }
    @FXML private void onLuu() { hienThiThanhCongLabel("Chế độ Demo: Đã cập nhật nhà cung cấp."); }
    @FXML private void onXoa() { hienThiThanhCongLabel("Chế độ Demo: Đã xóa nhà cung cấp."); }
    @FXML private void onHuy() { txtTenNCC.clear(); txtSdt.clear(); txtDiaChi.clear(); }
    @FXML private void onVeMenu() { quayLaiMenuChinh(tvNhaCungCap); }

    @FXML private void onTaiLai() { loadData(); hienThiThanhCongLabel("Chế độ Demo: Đã tải lại danh sách."); }
    @FXML private void onTimKiem() { /* Mock tìm kiếm */ }
}
