package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.presenters.kho.SanPhamPresenter;
import com.bakery.utils.UserSession;
import com.bakery.views.interfaces.kho.ISanPhamView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller cho SanPhamView.
 * Hỗ trợ Mock Data cho Demo.
 */
public class SanPhamViewFXMLController extends BaseController implements ISanPhamView {

    @FXML private TableView<SanPhamDTO> tblSanPham;
    @FXML private TableColumn<SanPhamDTO, Integer> colMaSP;
    @FXML private TableColumn<SanPhamDTO, String> colTenSP;
    @FXML private TableColumn<SanPhamDTO, String> colDanhMuc;
    @FXML private TableColumn<SanPhamDTO, Double> colGiaBan;
    @FXML private TableColumn<SanPhamDTO, Double> colTonKho;

    @FXML private ComboBox<Map.Entry<Integer, String>> cmbDanhMuc;
    @FXML private TextField txtTenSP;
    @FXML private TextField txtGiaBan;
    @FXML private CheckBox chkTuyChinh;
    @FXML private TextField txtTGBaoQuan;
    @FXML private TextField txtTGChuanBi;
    @FXML private TextField txtTonKho;

    private final ObservableList<SanPhamDTO> masterData = FXCollections.observableArrayList();
    private SanPhamPresenter presenter;
    private Map<Integer, String> currentDanhMucMap;

    @FXML
    public void initialize() {
        setupTable();
        int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
        presenter = new SanPhamPresenter(this, maNV);
        tblSanPham.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> hienThiChiTiet(newVal));
        presenter.taiDuLieuBanDau();
    }

    private void setupTable() {
        colMaSP.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaSP()).asObject());
        colTenSP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenSP()));
        colDanhMuc.setCellValueFactory(cellData -> {
            String tenDM = currentDanhMucMap != null ? currentDanhMucMap.get(cellData.getValue().getMaDM()) : "Bánh ngọt";
            return new SimpleStringProperty(tenDM);
        });
        colGiaBan.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGiaCoBan()).asObject());
        colTonKho.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSoLuongTon()).asObject());
        tblSanPham.setItems(masterData);

        cmbDanhMuc.setConverter(new StringConverter<Map.Entry<Integer, String>>() {
            @Override public String toString(Map.Entry<Integer, String> object) { return object != null ? object.getValue() : ""; }
            @Override public Map.Entry<Integer, String> fromString(String string) { return null; }
        });
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            ds = getMockProducts();
        }
        masterData.setAll(ds);
    }

    private List<SanPhamDTO> getMockProducts() {
        List<SanPhamDTO> mock = new ArrayList<>();
        mock.add(createMock(1, "Bánh Kem Bắp", 250000, 1, 10));
        mock.add(createMock(2, "Bánh Mì Bơ Tỏi", 45000, 2, 25));
        mock.add(createMock(3, "Cookie Socola", 25000, 3, 50));
        mock.add(createMock(4, "Bánh Donut Dâu", 35000, 2, 15));
        mock.add(createMock(5, "Tiramisu Ý", 55000, 1, 20));
        return mock;
    }

    private SanPhamDTO createMock(int id, String name, double price, int catId, double stock) {
        SanPhamDTO sp = new SanPhamDTO();
        sp.setMaSP(id); sp.setTenSP(name); sp.setGiaCoBan(price); sp.setMaDM(catId); sp.setSoLuongTon(stock);
        sp.setChoPhepTuyChinh(1); sp.setThoiGianBaoQuan(3); sp.setThoiGianChuanBi(30);
        return sp;
    }

    @Override
    public void hienThiDanhSachDanhMuc(Map<Integer, String> danhMucMap) {
        if (danhMucMap == null || danhMucMap.isEmpty()) {
            danhMucMap = Map.of(1, "Bánh kem", 2, "Bánh mì", 3, "Cookie");
        }
        this.currentDanhMucMap = danhMucMap;
        cmbDanhMuc.setItems(FXCollections.observableArrayList(danhMucMap.entrySet()));
    }

    @Override
    public void hienThiChiTiet(SanPhamDTO sp) {
        if (sp == null) return;
        txtTenSP.setText(sp.getTenSP());
        txtGiaBan.setText(String.valueOf(sp.getGiaCoBan()));
        txtTonKho.setText(String.valueOf(sp.getSoLuongTon()));
        txtTGBaoQuan.setText(String.valueOf(sp.getThoiGianBaoQuan()));
        txtTGChuanBi.setText(String.valueOf(sp.getThoiGianChuanBi()));
        chkTuyChinh.setSelected(sp.getChoPhepTuyChinh() == 1);
    }

    @Override public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }
    @Override public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }
    @Override public void lamMoiForm() { txtTenSP.clear(); txtGiaBan.clear(); txtTonKho.clear(); }
    @Override public SanPhamDTO getSelectedSanPham() { return tblSanPham.getSelectionModel().getSelectedItem(); }
    @Override public SanPhamDTO layDuLieuTuForm() { return getSelectedSanPham(); }

    @FXML private void onThemMoi() { lblThongBao.setText("Chế độ Demo: Đã thêm sản phẩm mới."); }
    @FXML private void onLuuThayDoi() { lblThongBao.setText("Chế độ Demo: Đã cập nhật sản phẩm."); }
    @FXML private void onXoa() { lblThongBao.setText("Chế độ Demo: Đã xóa sản phẩm."); }
    @FXML private void onQuayLai() { quayLaiMenuChinh(tblSanPham); }
    @FXML private void onLamMoi() { 
        if (presenter != null) presenter.taiDuLieuBanDau(); 
        lblThongBao.setText("Chế độ Demo: Đã làm mới dữ liệu."); 
    }
    @FXML private void onChonAnh() { 
        lblThongBao.setText("Chế độ Demo: Chức năng chọn ảnh đang được phát triển."); 
    }
}
