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
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
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
    private FilteredList<SanPhamDTO> filteredData;
    private SanPhamPresenter presenter;
    private Map<Integer, String> currentDanhMucMap;

    @FXML
    private TextField txtTimKiem;

    @FXML
    public void initialize() {
        setupTable();
        int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
        presenter = new SanPhamPresenter(this, maNV);
        tblSanPham.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> presenter.onChonSanPham(newVal));
        txtTonKho.setEditable(false);
        presenter.taiDuLieuBanDau();
    }

    /** Lọc bảng theo từ khóa (tên hoặc mã SP). Gọi từ onTimKiem. */
    @FXML
    private void onTimKiem() {
        String keyword = txtTimKiem != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        if (filteredData != null) {
            filteredData.setPredicate(sp -> {
                if (keyword.isEmpty())
                    return true;
                return sp.getTenSP().toLowerCase().contains(keyword)
                        || String.valueOf(sp.getMaSP()).contains(keyword);
            });
        }
    }

    private void setupTable() {
        colMaSP.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMaSP()).asObject());
        colTenSP.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenSP()));
        colDanhMuc.setCellValueFactory(cellData -> {
            String tenDM = currentDanhMucMap != null ? currentDanhMucMap.get(cellData.getValue().getMaDM()) : "";
            return new SimpleStringProperty(tenDM != null ? tenDM : "");
        });
        colGiaBan.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getGiaBan()).asObject());
        colTonKho.setCellValueFactory(
                cellData -> new SimpleDoubleProperty(cellData.getValue().getSoLuongTon()).asObject());
        // FilteredList wrap masterData để hỗ trợ AutoComplete search
        filteredData = new FilteredList<>(masterData, sp -> true);
        tblSanPham.setItems(filteredData);

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
        if (sp == null)
            return;
        txtTenSP.setText(sp.getTenSP());
        txtGiaBan.setText(String.valueOf(sp.getGiaBan()));
        txtTonKho.setText(String.valueOf(sp.getSoLuongTon()));
        txtTGBaoQuan.setText(String.valueOf(sp.getThoiGianBaoQuan()));
        txtTGChuanBi.setText(String.valueOf(sp.getThoiGianChuanBi()));
        chkTuyChinh.setSelected(sp.getChoPhepTuyChinh() == 1);
        // Bug fix: đồng bộ ComboBox danh mục theo sản phẩm đang chọn
        if (cmbDanhMuc.getItems() != null) {
            cmbDanhMuc.getItems().stream()
                    .filter(e -> e.getKey() == sp.getMaDM())
                    .findFirst()
                    .ifPresent(cmbDanhMuc::setValue);
        }
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

    /** Xóa filter tìm kiếm và hiển thị lại toàn bộ dữ liệu. */
    private void xoaFilter() {
        if (filteredData != null)
            filteredData.setPredicate(sp -> true);
        if (txtTimKiem != null)
            txtTimKiem.clear();
    }

    @Override
    public SanPhamDTO layDuLieuTuForm() {
        SanPhamDTO selected = getSelectedSanPham();
        // Bug fix: tạo DTO mới, KHÔNG mutate item đang có trong ObservableList
        SanPhamDTO sp = new SanPhamDTO();
        if (selected != null) {
            sp.setMaSP(selected.getMaSP());
            sp.setGiaVon(selected.getGiaVon()); // giữ giaVon để service tái sử dụng
            sp.setSoLuongTon(selected.getSoLuongTon()); // tồn kho không sửa được ở đây
        }
        sp.setTenSP(txtTenSP.getText().trim());
        try {
            sp.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim()));
        } catch (NumberFormatException ignored) {
        }
        try {
            sp.setThoiGianBaoQuan(Integer.parseInt(txtTGBaoQuan.getText().trim()));
        } catch (NumberFormatException ignored) {
        }
        try {
            sp.setThoiGianChuanBi(Integer.parseInt(txtTGChuanBi.getText().trim()));
        } catch (NumberFormatException ignored) {
        }
        sp.setChoPhepTuyChinh(chkTuyChinh.isSelected() ? 1 : 0);
        if (cmbDanhMuc.getValue() != null) {
            sp.setMaDM(cmbDanhMuc.getValue().getKey());
        } else if (selected != null) {
            sp.setMaDM(selected.getMaDM()); // fallback nếu user không đổi danh mục
        }
        // Giữ ảnh cũ nếu user chưa chọn ảnh mới
        sp.setHinhAnh(selectedImagePath != null ? selectedImagePath
                : (selected != null ? selected.getHinhAnh() : null));
        return sp;
    }

    @FXML
    private void onThemMoi() {
        if (presenter == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/ThemSanPhamDialog.fxml"));
            Parent root = loader.load();

            ThemSanPhamDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTaoDanhMuc(currentDanhMucMap);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm sản phẩm mới");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tblSanPham.getScene().getWindow());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            SanPhamDTO ketQua = dialogCtrl.getKetQua();
            if (ketQua != null) {
                presenter.themSanPham(ketQua, this::chuyenSangTabCongThuc);
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm sản phẩm: " + e.getMessage());
        }
    }

    @FXML
    private void onLuuThayDoi() {
        if (presenter != null)
            presenter.suaSanPham();
    }

    @FXML
    private void onXoa() {
        if (presenter != null)
            presenter.xoaSanPham();
    }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblSanPham);
    }

    @FXML
    private void onLamMoi() {
        xoaFilter();
        if (presenter != null)
            presenter.taiDanhSachSanPham();
    }

    @FXML
    private void onChonAnh() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh sản phẩm (PNG)");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Ảnh PNG (*.png)", "*.png"));
        // Mở từ thư mục cuối cùng hoặc home
        if (selectedImagePath != null) {
            File lastDir = new File(selectedImagePath).getParentFile();
            if (lastDir != null && lastDir.exists())
                fc.setInitialDirectory(lastDir);
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
        if (imgSanPham == null)
            return;
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
                if (res == null) {
                    xoaAnhPreview();
                    return;
                }
                img = new Image(res); // InputStream variant không hỗ trợ backgroundLoading
            }
            imgSanPham.setImage(img);
            imgSanPham.setFitWidth(120);
            imgSanPham.setFitHeight(120);
            imgSanPham.setPreserveRatio(true);
        } catch (Exception e) {
            xoaAnhPreview();
            hienThiLoiLabel("Không thể tải ảnh: " + e.getMessage());
        }
    }

    private void xoaAnhPreview() {
        if (imgSanPham != null)
            imgSanPham.setImage(null);
    }

    /**
     * Implement ISanPhamView: chuyển sang Tab Công thức sau khi tạo SP mới.
     * Tìm TabPane cha (QuanLySanPhamView) qua scene graph và select tabCongThuc.
     */
    @Override
    public void chuyenSangTabCongThuc(int maSP) {
        javafx.application.Platform.runLater(() -> {
            // Tìm TabPane cha trong scene graph
            javafx.scene.Node node = tblSanPham;
            while (node != null && !(node instanceof TabPane)) {
                // leo lên cha 2 cấp: VBox (tab content) → Tab → TabPane
                node = node.getParent();
            }
            if (node instanceof TabPane tabPane) {
                // Tự chọn SP mới trong bảng để Tab Công thức nhận đúng SP
                masterData.stream()
                        .filter(sp -> sp.getMaSP() == maSP)
                        .findFirst()
                        .ifPresent(sp -> tblSanPham.getSelectionModel().select(sp));

                // Chuyển sang tab "tabCongThuc" (index 2 trong QuanLySanPhamView)
                tabPane.getTabs().stream()
                        .filter(t -> "tabCongThuc".equals(t.getId()))
                        .findFirst()
                        .ifPresent(t -> tabPane.getSelectionModel().select(t));
            }
        });
    }
}
