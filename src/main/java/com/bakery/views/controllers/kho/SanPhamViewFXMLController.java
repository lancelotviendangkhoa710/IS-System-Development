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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SanPhamViewFXMLController extends BaseController implements ISanPhamView {

    /** Đường dẫn ảnh đang được chọn trong form (chưa lưu). */
    private String selectedImagePath = null;

    @FXML
    private TableView<SanPhamDTO> tblSanPham;
    @FXML
    private TableColumn<SanPhamDTO, Integer> colMaSP;
    @FXML
    private TableColumn<SanPhamDTO, String> colTenSP;
    @FXML
    private TableColumn<SanPhamDTO, String> colDanhMuc;
    @FXML
    private TableColumn<SanPhamDTO, Double> colGiaBan;
    @FXML
    private TableColumn<SanPhamDTO, Double> colTonKho;

    @FXML
    private ComboBox<Map.Entry<Integer, String>> cmbDanhMuc;
    @FXML
    private TextField txtTenSP;
    @FXML
    private TextField txtGiaBan;
    @FXML
    private CheckBox chkTuyChinh;
    @FXML
    private TextField txtTGBaoQuan;
    @FXML
    private TextField txtTGChuanBi;
    @FXML
    private TextField txtTonKho;
    @FXML
    private ImageView imgSanPham;

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
            String tenDM = currentDanhMucMap != null ? currentDanhMucMap.get(cellData.getValue().getMaDM())
                    : "Bánh ngọt";
            return new SimpleStringProperty(tenDM);
        });
        colGiaBan.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getGiaCoBan()).asObject());
        colTonKho.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getSoLuongTon()).asObject());
        tblSanPham.setItems(masterData);

        cmbDanhMuc.setConverter(new StringConverter<Map.Entry<Integer, String>>() {
            @Override
            public String toString(Map.Entry<Integer, String> object) {
                return object != null ? object.getValue() : "";
            }

            @Override
            public Map.Entry<Integer, String> fromString(String string) {
                return null;
            }
        });
    }

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            masterData.clear();
            hienThiLoi("Không có dữ liệu sản phẩm.");
            return;
        }
        masterData.setAll(ds);
    }

    @Override
    public void hienThiDanhSachDanhMuc(Map<Integer, String> danhMucMap) {
        if (danhMucMap == null || danhMucMap.isEmpty()) {
            return;
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
        // Hiển thị ảnh sản phẩm
        selectedImagePath = sp.getHinhAnh();
        hienThiAnh(selectedImagePath);
    }

    @Override
    public void hienThiLoi(String msg) {
        hienThiLoiLabel(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        hienThiThanhCongLabel(msg);
    }

    @Override
    public void lamMoiForm() {
        txtTenSP.clear();
        txtGiaBan.clear();
        txtTonKho.clear();
        txtTGBaoQuan.clear();
        txtTGChuanBi.clear();
        selectedImagePath = null;
        xoaAnhPreview();
    }

    @Override
    public SanPhamDTO getSelectedSanPham() {
        return tblSanPham.getSelectionModel().getSelectedItem();
    }

    @Override
    public SanPhamDTO layDuLieuTuForm() {
        SanPhamDTO sp = getSelectedSanPham();
        if (sp == null) sp = new SanPhamDTO();
        sp.setTenSP(txtTenSP.getText().trim());
        try { sp.setGiaCoBan(Double.parseDouble(txtGiaBan.getText().trim())); } catch (NumberFormatException ignored) {}
        try { sp.setSoLuongTon(Double.parseDouble(txtTonKho.getText().trim())); } catch (NumberFormatException ignored) {}
        try { sp.setThoiGianBaoQuan(Integer.parseInt(txtTGBaoQuan.getText().trim())); } catch (NumberFormatException ignored) {}
        try { sp.setThoiGianChuanBi(Integer.parseInt(txtTGChuanBi.getText().trim())); } catch (NumberFormatException ignored) {}
        sp.setChoPhepTuyChinh(chkTuyChinh.isSelected() ? 1 : 0);
        if (cmbDanhMuc.getValue() != null) sp.setMaDM(cmbDanhMuc.getValue().getKey());
        // Gắn đường dẫn ảnh vào DTO
        if (selectedImagePath != null) sp.setHinhAnh(selectedImagePath);
        return sp;
    }

    @FXML
    private void onThemMoi() {
        if (presenter != null) presenter.themSanPham();
    }

    @FXML
    private void onLuuThayDoi() {
        if (presenter != null) presenter.suaSanPham();
    }

    @FXML
    private void onXoa() {
        if (presenter != null) presenter.xoaSanPham();
    }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblSanPham);
    }

    @FXML
    private void onLamMoi() {
        if (presenter != null) presenter.taiDanhSachSanPham();
    }

    @FXML
    private void onChonAnh() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh sản phẩm");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Ảnh (*.png, *.jpg, *.jpeg, *.gif, *.webp)",
                        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp")
        );
        // Mở từ thư mục cuối cùng hoặc home
        if (selectedImagePath != null) {
            File lastDir = new File(selectedImagePath).getParentFile();
            if (lastDir != null && lastDir.exists()) fc.setInitialDirectory(lastDir);
        }

        File chosen = fc.showOpenDialog(
                imgSanPham != null ? imgSanPham.getScene().getWindow() : null);

        if (chosen != null) {
            selectedImagePath = chosen.getAbsolutePath();
            hienThiAnh(selectedImagePath);
            hienThiThanhCongLabel("Đã chọn ảnh: " + chosen.getName());
        }
    }

    // ── Helpers ảnh ───────────────────────────────────────────────────────

    /**
     * Load và hiển thị ảnh vào ImageView từ đường dẫn file tuyệt đối.
     * Hỗ trợ cả file:// và classpath resource.
     */
    private void hienThiAnh(String path) {
        if (imgSanPham == null) return;
        if (path == null || path.isBlank()) {
            xoaAnhPreview();
            return;
        }
        try {
            Image img;
            File f = new File(path);
            if (f.exists()) {
                img = new Image(f.toURI().toString(), true); // background loading
            } else {
                // Thử classpath (VD: /images/sanpham/banhkem.png)
                var res = getClass().getResourceAsStream(path);
                if (res == null) { xoaAnhPreview(); return; }
                img = new Image(res); // InputStream variant không hỗ trợ backgroundLoading
            }
            imgSanPham.setImage(img);
            imgSanPham.setFitWidth(120);
            imgSanPham.setFitHeight(120);
            imgSanPham.setPreserveRatio(true);
            // Clip tròn nhẹ qua style
            imgSanPham.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);");
        } catch (Exception e) {
            xoaAnhPreview();
            hienThiLoiLabel("Không thể tải ảnh: " + e.getMessage());
        }
    }

    private void xoaAnhPreview() {
        if (imgSanPham != null) imgSanPham.setImage(null);
    }
}
