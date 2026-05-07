package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.services.kho.NhaCungCapService;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller cho QuanLyNhaCungCap.
 * Hỗ trợ Mock Data cho Demo.
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
            if (list == null || list.isEmpty()) {
                list = getMockSuppliers();
            }
            nccList.setAll(list);
        } catch (Exception e) {
            nccList.setAll(getMockSuppliers());
        }
    }

    private List<NhaCungCapDTO> getMockSuppliers() {
        List<NhaCungCapDTO> mock = new ArrayList<>();
        mock.add(createMock(1, "Bột mì Biên Hòa", "02838111222", "KCN Biên Hòa, Đồng Nai"));
        mock.add(createMock(2, "Sữa tươi Vinamilk", "19001559", "Quận 7, TP.HCM"));
        mock.add(createMock(3, "Trứng gà CP", "02436668888", "KCN Phú Nghĩa, Hà Nội"));
        mock.add(createMock(4, "Bơ lạt Anchor (New Zealand)", "0909123456", "Phân phối bởi Fonterra VN"));
        mock.add(createMock(5, "Đường sắt Biên Hòa", "02513836141", "Đồng Nai"));
        return mock;
    }

    private NhaCungCapDTO createMock(int id, String name, String sdt, String address) {
        NhaCungCapDTO ncc = new NhaCungCapDTO();
        ncc.setMaNCC(id); ncc.setTenNCC(name); ncc.setSdt(sdt); ncc.setDiaChi(address);
        return ncc;
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
