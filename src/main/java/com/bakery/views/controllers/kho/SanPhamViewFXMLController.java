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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class SanPhamViewFXMLController extends BaseController implements ISanPhamView {

    @FXML private TableView<SanPhamDTO> tblSanPham;
    @FXML private TableColumn<SanPhamDTO, Integer> colMaSP;
    @FXML private TableColumn<SanPhamDTO, String>  colTenSP;
    @FXML private TableColumn<SanPhamDTO, String>  colDanhMuc;
    @FXML private TableColumn<SanPhamDTO, Double>  colGiaVon;
    @FXML private TableColumn<SanPhamDTO, Double>  colGiaBan;
    @FXML private TableColumn<SanPhamDTO, Double>  colTonKho;
    @FXML private TextField txtTimKiem;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final ObservableList<SanPhamDTO> masterData = FXCollections.observableArrayList();
    private FilteredList<SanPhamDTO> filteredData;
    private SanPhamPresenter presenter;
    private Map<Integer, String> currentDanhMucMap;

    /** DTO nhận từ dialog Sửa — dùng cho layDuLieuTuForm(). */
    private SanPhamDTO dtoDialogKetQua = null;

    /** Ref sang CongThucController — nhận từ QuanLySanPhamViewFXMLController sau initialize. */
    private CongThucViewFXMLController congThucController;

    public void setCongThucController(CongThucViewFXMLController ctrl) {
        this.congThucController = ctrl;
    }

    @FXML
    public void initialize() {
        setupTable();
        int maNV = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getMaNV() : 1;
        presenter = new SanPhamPresenter(this, maNV);
        tblSanPham.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> presenter.onChonSanPham(newVal));
        presenter.taiDuLieuBanDau();
    }

    /** Lọc bảng theo từ khóa (tên hoặc mã SP). */
    @FXML
    private void onTimKiem() {
        String keyword = txtTimKiem != null ? txtTimKiem.getText().trim().toLowerCase() : "";
        if (filteredData != null) {
            filteredData.setPredicate(sp -> {
                if (keyword.isEmpty()) return true;
                return sp.getTenSP().toLowerCase().contains(keyword)
                        || String.valueOf(sp.getMaSP()).contains(keyword);
            });
        }
    }

    private void setupTable() {
        colMaSP.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaSP()).asObject());
        colTenSP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenSP()));
        colDanhMuc.setCellValueFactory(c -> {
            String ten = currentDanhMucMap != null ? currentDanhMucMap.get(c.getValue().getMaDM()) : "";
            return new SimpleStringProperty(ten != null ? ten : "");
        });
        colGiaVon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGiaVon()).asObject());
        colGiaVon.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null
                        : java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN")).format(val) + " đ");
            }
        });
        colGiaBan.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGiaBan()).asObject());
        colGiaBan.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null
                        : java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN")).format(val) + " đ");
            }
        });
        colTonKho.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTon()).asObject());

        filteredData = new FilteredList<>(masterData, sp -> true);
        tblSanPham.setItems(filteredData);
    }

    // ── ISanPhamView ──────────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSachSanPham(List<SanPhamDTO> ds) {
        masterData.setAll(ds != null ? ds : List.of());
        if (ds == null || ds.isEmpty()) hienThiLoi("Không có dữ liệu sản phẩm.");
    }

    @Override
    public void hienThiDanhSachDanhMuc(Map<Integer, String> danhMucMap) {
        if (danhMucMap != null) this.currentDanhMucMap = danhMucMap;
    }

    @Override
    public void hienThiChiTiet(SanPhamDTO sp) {
        // Kích hoạt nút Sửa / Xóa khi có SP được chọn
        btnSua.setDisable(sp == null);
        btnXoa.setDisable(sp == null);
    }

    @Override
    public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }

    @Override
    public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }

    @Override
    public void lamMoiForm() {
        // Vô hiệu hóa nút Sửa / Xóa khi bỏ chọn
        btnSua.setDisable(true);
        btnXoa.setDisable(true);
        dtoDialogKetQua = null;
    }

    @Override
    public SanPhamDTO getSelectedSanPham() {
        return tblSanPham.getSelectionModel().getSelectedItem();
    }

    /** Trả về DTO đã được user chỉnh sửa qua dialog. */
    @Override
    public SanPhamDTO layDuLieuTuForm() {
        return dtoDialogKetQua;
    }

    // ── Event Handlers ────────────────────────────────────────────────────────

    /** Mở dialog Thêm sản phẩm mới. */
    @FXML
    private void onThemMoi() {
        if (presenter == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/kho/ThemSanPhamDialog.fxml"));
            Parent root = loader.load();

            ThemSanPhamDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTaoDanhMuc(currentDanhMucMap);

            Stage stage = new Stage();
            stage.setTitle("Thêm sản phẩm mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblSanPham.getScene().getWindow());
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            SanPhamDTO ketQua = dialogCtrl.getKetQua();
            if (ketQua != null) presenter.themSanPham(ketQua, this::chuyenSangTabCongThuc);
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm sản phẩm: " + e.getMessage());
        }
    }

    /** Mở dialog Sửa sản phẩm. Kết quả được lưu vào dtoDialogKetQua rồi gọi presenter. */
    @FXML
    private void onSuaSanPham() {
        SanPhamDTO selected = getSelectedSanPham();
        if (selected == null) {
            hienThiLoiLabel("Vui lòng chọn một sản phẩm để sửa.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/kho/SuaSanPhamDialog.fxml"));
            Parent root = loader.load();

            SuaSanPhamDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(selected, currentDanhMucMap);

            Stage stage = new Stage();
            stage.setTitle("Sửa sản phẩm");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblSanPham.getScene().getWindow());
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();

            dtoDialogKetQua = dialogCtrl.getKetQua();
            if (dtoDialogKetQua != null) presenter.suaSanPham();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog sửa sản phẩm: " + e.getMessage());
        }
    }

    @FXML
    private void onXoa() {
        if (presenter != null) presenter.xoaSanPham();
    }

    @FXML
    private void onQuayLai() { quayLaiMenuChinh(tblSanPham); }

    @FXML
    private void onLamMoi() {
        xoaFilter();
        if (presenter != null) presenter.taiDanhSachSanPham();
    }

    private void xoaFilter() {
        if (filteredData != null) filteredData.setPredicate(sp -> true);
        if (txtTimKiem != null) txtTimKiem.clear();
    }

    /** Reload danh sách SP — gọi từ QuanLySanPhamViewFXMLController khi switch về tab này để giá vốn luôn fresh. */
    public void lamMoiDanhSach() {
        if (presenter != null) presenter.taiDanhSachSanPham();
    }

    // ── chuyenSangTabCongThuc ─────────────────────────────────────────────────

    @Override
    public void chuyenSangTabCongThuc(int maSP) {
        javafx.application.Platform.runLater(() -> {
            // 1. Tìm và select SP mới trong bảng
            masterData.stream().filter(sp -> sp.getMaSP() == maSP).findFirst()
                    .ifPresent(sp -> tblSanPham.getSelectionModel().select(sp));

            // 2. Navigate sang Tab Công thức
            javafx.scene.Node node = tblSanPham;
            while (node != null && !(node instanceof TabPane)) node = node.getParent();
            if (node instanceof TabPane tabPane) {
                tabPane.getTabs().stream()
                        .filter(t -> "tabCongThuc".equals(t.getId()))
                        .findFirst()
                        .ifPresent(t -> tabPane.getSelectionModel().select(t));
            }

            // 3. Gọi thẳng vào CongThucController để tránh race condition với tab-listener
            if (congThucController != null) {
                SanPhamDTO sp = masterData.stream()
                        .filter(s -> s.getMaSP() == maSP).findFirst().orElse(null);
                congThucController.capNhatSanPhamDangChon(sp);
            }
        });
    }
}
