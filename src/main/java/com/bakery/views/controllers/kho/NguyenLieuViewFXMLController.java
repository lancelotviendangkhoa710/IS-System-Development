package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.presenters.kho.NguyenLieuPresenter;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.kho.INguyenLieuView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Tooltip;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NguyenLieuViewFXMLController extends BaseController implements INguyenLieuView {

    @FXML private TableView<NguyenLieuDTO> tblNguyenLieu;
    @FXML private TableColumn<NguyenLieuDTO, Integer> colMaNL;
    @FXML private TableColumn<NguyenLieuDTO, String> colTenNL;
    @FXML private TableColumn<NguyenLieuDTO, String> colXuatXu;
    @FXML private TableColumn<NguyenLieuDTO, Double> colMucTon;

    @FXML private TextField txtTimKiem;
    @FXML private TextField txtTenNL;
    @FXML private TextField txtXuatXu;
    @FXML private TextField txtMucTonAnToan;
    @FXML private ComboBox<DonViTinhDTO> cmbDonViTinh;
    @FXML private VBox vboxChiTiet;

    private final ObservableList<NguyenLieuDTO> masterData = FXCollections.observableArrayList();
    private NguyenLieuPresenter presenter;
    private List<DonViTinhDTO> cachedDsDVT = new ArrayList<>();
    private List<NhaCungCapDTO> cachedDsNCC = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, 1);
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> hienThiChiTiet(newVal));
        presenter.khoiTao();
        // Task 8: Thủ kho không được sửa mức tồn kho an toàn
        apDungPhanQuyenTonKhoAnToan();
        // Auto-refresh: mỗi 10s tự query DB, nếu có NL mới sẽ hiện lên
        batDauAutoRefresh(tblNguyenLieu, () -> presenter.taiDanhSach(), 10);
    }

    /**
     * Task 8: Kiểm tra vai trò hiện tại.
     * Chỉ Quản lý / Admin mới được phép chỉnh sửa mức tồn kho an toàn.
     * Thủ kho và các vai trò khác: field chỉ đọc + tooltip giải thích.
     */
    private void apDungPhanQuyenTonKhoAnToan() {
        com.bakery.model.dto.nhansu.NhanVienDTO user =
                com.bakery.utils.UserSession.getCurrentUser();
        if (user == null) return;

        boolean coQuyenSua = user.getDanhSachTenVaiTro().stream().anyMatch(r -> {
            String rNorm = r.toLowerCase();
            return rNorm.contains("quản lý")
                || rNorm.contains("quan ly")
                || rNorm.contains("admin");
        });

        if (!coQuyenSua) {
            txtMucTonAnToan.setEditable(false);
            txtMucTonAnToan.setFocusTraversable(false);
            txtMucTonAnToan.getStyleClass().add("field-readonly");
            Tooltip.install(txtMucTonAnToan,
                new Tooltip("Chỉ Quản lý mới có quyền thay đổi mức tồn kho an toàn."));
        }
    }

    private void setupTable() {
        colMaNL.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaNL()).asObject());
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getXuatXu() != null ? c.getValue().getXuatXu() : "Việt Nam"));
        colMucTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        tblNguyenLieu.setItems(masterData);
    }

    // ── INguyenLieuView ───────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSach(List<NguyenLieuDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            masterData.clear();
            hienThiLoi("Không có dữ liệu nguyên liệu.");
            return;
        }
        masterData.setAll(ds);
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        if (dsDVT == null || dsDVT.isEmpty()) return;
        cachedDsDVT = dsDVT;
        cmbDonViTinh.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(DonViTinhDTO d) { return d != null ? d.getTenDVT() : ""; }
            @Override public DonViTinhDTO fromString(String s) { return null; }
        });
        cmbDonViTinh.setItems(FXCollections.observableArrayList(dsDVT));
    }

    @Override
    public void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC) {
        if (dsNCC != null) cachedDsNCC = dsNCC;
    }

    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) {
        if (nl == null) return;
        // Hiện panel chi tiết khi có record được chọn
        vboxChiTiet.setVisible(true);
        vboxChiTiet.setManaged(true);
        txtTenNL.setText(nl.getTenNL());
        txtXuatXu.setText(nl.getXuatXu() != null ? nl.getXuatXu() : "");
        txtMucTonAnToan.setText(String.valueOf(nl.getMucTonAnToan()));
        // Đồng bộ DVT trong ComboBox form sửa
        if (nl.getMaDVT() > 0) {
            cmbDonViTinh.getItems().stream()
                    .filter(d -> d.getMaDVT() == nl.getMaDVT())
                    .findFirst()
                    .ifPresent(cmbDonViTinh::setValue);
        }
    }

    @Override public void hienThiLoi(String msg)       { hienThiLoiLabel(msg); }
    @Override public void hienThiThanhCong(String msg)  { hienThiThanhCongLabel(msg); }
    @Override public void xoaLoi()                      { if (lblThongBao != null) lblThongBao.setText(""); }
    @Override public void setLoading(boolean l)         { tblNguyenLieu.setDisable(l); }

    @Override
    public void lamMoiForm() {
        txtTenNL.clear();
        txtXuatXu.clear();
        txtMucTonAnToan.clear();
        cmbDonViTinh.setValue(null);
        tblNguyenLieu.getSelectionModel().clearSelection();
        // Ẩn panel chi tiết sau khi reset
        vboxChiTiet.setVisible(false);
        vboxChiTiet.setManaged(false);
    }

    @Override public NguyenLieuDTO getSelectedNguyenLieu() { return tblNguyenLieu.getSelectionModel().getSelectedItem(); }
    @Override public String getTenNLInput()                { return txtTenNL.getText().trim(); }
    @Override public String getXuatXuInput()               { return txtXuatXu.getText().trim(); }
    @Override public DonViTinhDTO getDonViTinhSelected()   { return cmbDonViTinh.getValue(); }
    @Override public String getTuKhoaTimKiemInput()        { return txtTimKiem.getText().trim(); }

    @Override
    public double getMucTonAnToanInput() {
        try { return Double.parseDouble(txtMucTonAnToan.getText().trim()); }
        catch (Exception e) { return 0; }
    }

    // ── FXML Actions ─────────────────────────────────────────────────────────

    @FXML
    private void onThemMoi() {
        if (presenter == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ThemNguyenLieuDialog.fxml"));
            Parent root = loader.load();

            ThemNguyenLieuDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(cachedDsDVT, cachedDsNCC);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Thêm nguyên liệu mới");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tblNguyenLieu.getScene().getWindow());

            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            if (dialogCtrl.isConfirmed()) {
                presenter.themNguyenLieuVaNhapKho(
                        dialogCtrl.getTenNL(),
                        dialogCtrl.getXuatXu(),
                        dialogCtrl.getMucTon(),
                        dialogCtrl.getDonViTinh() != null ? dialogCtrl.getDonViTinh().getMaDVT() : 0,
                        dialogCtrl.getNhaCungCap() != null ? dialogCtrl.getNhaCungCap().getMaNCC() : 0,
                        dialogCtrl.getSoLuong(),
                        dialogCtrl.getDonGia(),
                        dialogCtrl.getNgaySanXuat(),
                        dialogCtrl.getHanSuDung());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm nguyên liệu: " + e.getMessage());
        }
    }

    @FXML private void onLuuThayDoi() { if (presenter != null) presenter.suaNguyenLieu(); }
    @FXML private void onXoa()        { if (presenter != null) presenter.xoaNguyenLieu(); }
    @FXML private void onTimKiem()    { if (presenter != null) presenter.timKiem(); }

    @FXML
    private void onLamMoi() {
        if (presenter != null) presenter.taiDanhSach();
    }

    @FXML
    private void onQuayLai() {
        quayLaiMenuChinh(tblNguyenLieu);
    }
}
