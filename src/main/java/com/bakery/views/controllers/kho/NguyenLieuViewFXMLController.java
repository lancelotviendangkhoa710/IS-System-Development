package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.NguyenLieuDAO;
import com.bakery.model.dto.kho.DonViTinhDTO;
import com.bakery.model.dto.kho.NguyenLieuDTO;
import com.bakery.model.dto.kho.NhaCungCapDTO;
import com.bakery.presenters.kho.NguyenLieuPresenter;
import com.bakery.utils.DialogHelper;
import com.bakery.views.controllers.BaseController;
import com.bakery.views.interfaces.kho.INguyenLieuView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller màn hình Quản lý Nguyên liệu (Tab Quản lý Nguyên liệu).
 * Thêm / Sửa qua dialog — không còn inline form panel.
 */
public class NguyenLieuViewFXMLController extends BaseController implements INguyenLieuView {

    // ── Table ─────────────────────────────────────────────────────────────────
    @FXML
    private TableView<NguyenLieuDTO> tblNguyenLieu;
    @FXML
    private TableColumn<NguyenLieuDTO, Double> colSoLuongTon;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colTenNL;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colXuatXu;
    @FXML
    private TableColumn<NguyenLieuDTO, String> colDVT;
    @FXML
    private TableColumn<NguyenLieuDTO, Double> colMucTon;

    @FXML
    private TextField txtTimKiem;
    @FXML
    private Button btnThemMoi;
    @FXML
    private Button btnSua;
    @FXML
    private Button btnXoa;

    // ── Cache ──────────────────────────────────────────────────────────────────
    private final ObservableList<NguyenLieuDTO> masterData = FXCollections.observableArrayList();
    private List<DonViTinhDTO> cachedDsDVT = new ArrayList<>();
    private List<NhaCungCapDTO> cachedDsNCC = new ArrayList<>();
    private NguyenLieuPresenter presenter;
    private final NguyenLieuDAO nguyenLieuDAO = new NguyenLieuDAO();

    // ── Init ───────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTable();
        presenter = new NguyenLieuPresenter(this, layMaNvHienTai());
        presenter.khoiTao();
        apDungPhanQuyenCUD();
        tblNguyenLieu.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    boolean coChon = newVal != null;
                    if (btnSua != null)
                        btnSua.setDisable(!coChon);
                    if (btnXoa != null)
                        btnXoa.setDisable(!coChon);
                });
        batDauAutoRefresh(tblNguyenLieu, () -> presenter.taiDanhSach(), 10);
    }

    private int layMaNvHienTai() {
        try {
            com.bakery.model.dto.nhansu.NhanVienDTO user = com.bakery.utils.UserSession.getCurrentUser();
            return (user != null) ? user.getMaNV() : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    // ── Table setup ────────────────────────────────────────────────────────────

    private void setupTable() {
        colSoLuongTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSoLuongTonTong() != null
                ? c.getValue().getSoLuongTonTong()
                : 0.0).asObject());
        colSoLuongTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    return;
                }
                NguyenLieuDTO nl = getTableRow() != null ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty()) ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0 ? String.valueOf((long) (double) val) + dvt : val + dvt);
            }
        });
        colTenNL.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenNL()));
        colXuatXu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getXuatXu() != null ? c.getValue().getXuatXu() : "Việt Nam"));
        colDVT.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDVT()));
        colMucTon.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getMucTonAnToan()).asObject());
        colMucTon.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    return;
                }
                NguyenLieuDTO nl = getTableRow() != null ? (NguyenLieuDTO) getTableRow().getItem() : null;
                String dvt = (nl != null && !nl.getTenDVT().isEmpty()) ? " " + nl.getTenDVT() : "";
                setText(val % 1 == 0 ? String.valueOf((long) (double) val) + dvt : val + dvt);
            }
        });
        tblNguyenLieu.setItems(masterData);
        tblNguyenLieu.setPlaceholder(new Label("Chưa có nguyên liệu nào."));
        tblNguyenLieu.setRowFactory(tv -> {
            TableRow<NguyenLieuDTO> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty())
                    onSua(row.getItem());
            });
            return row;
        });
    }

    // ── Phân quyền ────────────────────────────────────────────────────────────

    private void apDungPhanQuyenCUD() {
        com.bakery.model.dto.nhansu.NhanVienDTO user = com.bakery.utils.UserSession.getCurrentUser();
        if (user == null)
            return;
        com.bakery.services.nhansu.PhanQuyenService svc = new com.bakery.services.nhansu.PhanQuyenService();
        boolean coQuyenCUD = svc.laAdmin(user) || svc.laQuanLy(user) || svc.laThuKho(user);
        if (!coQuyenCUD) {
            if (btnThemMoi != null) {
                btnThemMoi.setVisible(false);
                btnThemMoi.setManaged(false);
            }
            if (btnSua != null) {
                btnSua.setVisible(false);
                btnSua.setManaged(false);
            }
            if (btnXoa != null) {
                btnXoa.setVisible(false);
                btnXoa.setManaged(false);
            }
        }
    }

    // ── INguyenLieuView ───────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSach(List<NguyenLieuDTO> ds) {
        if (ds == null || ds.isEmpty()) {
            masterData.clear();
            hienThiLoiLabel("Không có dữ liệu nguyên liệu.");
            return;
        }
        masterData.setAll(ds);
    }

    @Override
    public void napDanhSachDonViTinh(List<DonViTinhDTO> dsDVT) {
        if (dsDVT != null)
            cachedDsDVT = dsDVT;
    }

    @Override
    public void napDanhSachNhaCungCap(List<NhaCungCapDTO> dsNCC) {
        if (dsNCC != null)
            cachedDsNCC = dsNCC;
    }

    @Override
    public void hienThiChiTiet(NguyenLieuDTO nl) {
        /* dialog-based */ }

    @Override
    public void hienThiLoi(String msg) {
        hienThiLoiLabel(msg);
    }

    @Override
    public void hienThiThanhCong(String msg) {
        hienThiThanhCongLabel(msg);
    }

    @Override
    public void xoaLoi() {
        if (lblThongBao != null)
            lblThongBao.setText("");
    }

    @Override
    public void setLoading(boolean l) {
        tblNguyenLieu.setDisable(l);
    }

    @Override
    public void lamMoiForm() {
        tblNguyenLieu.getSelectionModel().clearSelection();
    }

    @Override
    public NguyenLieuDTO getSelectedNguyenLieu() {
        return tblNguyenLieu.getSelectionModel().getSelectedItem();
    }

    @Override
    public String getTenNLInput() {
        return "";
    }

    @Override
    public String getXuatXuInput() {
        return "";
    }

    @Override
    public double getMucTonAnToanInput() {
        return 0;
    }

    @Override
    public DonViTinhDTO getDonViTinhSelected() {
        return null;
    }

    @Override
    public String getTuKhoaTimKiemInput() {
        return txtTimKiem != null ? txtTimKiem.getText().trim() : "";
    }

    // ── FXML Actions ──────────────────────────────────────────────────────────

    @FXML
    private void onThemMoi() {
        if (presenter == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/kho/ThemNguyenLieuDialog.fxml"));
            Parent root = loader.load();
            ThemNguyenLieuDialogController dialogCtrl = loader.getController();
            dialogCtrl.khoiTao(cachedDsDVT, cachedDsNCC);
            Stage stage = new Stage();
            stage.setTitle("H3K Bakery — Thêm nguyên liệu mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblNguyenLieu.getScene().getWindow());
            stage.setResizable(false);
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("/css/bakery.css");
            if (cssUrl != null)
                scene.getStylesheets().add(cssUrl.toExternalForm());
            stage.setScene(scene);
            stage.showAndWait();
            if (dialogCtrl.isConfirmed()) {
                presenter.themNguyenLieuVaNhapKho(
                        dialogCtrl.getTenNL(), dialogCtrl.getXuatXu(), dialogCtrl.getMucTon(),
                        dialogCtrl.getDonViTinh() != null ? dialogCtrl.getDonViTinh().getMaDVT() : 0,
                        dialogCtrl.getNhaCungCap() != null ? dialogCtrl.getNhaCungCap().getMaNCC() : 0,
                        dialogCtrl.getSoLuong(), dialogCtrl.getDonGia(),
                        dialogCtrl.getNgaySanXuat(), dialogCtrl.getHanSuDung());
            }
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog thêm nguyên liệu: " + e.getMessage());
        }
    }

    private void onSua(NguyenLieuDTO nl) {
        if (nl == null)
            return;
        try {
            URL url = getClass().getResource("/fxml/kho/SuaNguyenLieuDialog.fxml");
            if (url == null)
                throw new RuntimeException("Không tìm thấy SuaNguyenLieuDialog.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            Scene scene = new Scene(loader.load());
            URL css = getClass().getResource("/css/bakery.css");
            if (css != null)
                scene.getStylesheets().add(css.toExternalForm());
            SuaNguyenLieuDialogController ctrl = loader.getController();
            ctrl.khoiTaoSua(nl, cachedDsDVT);
            Stage stage = new Stage();
            stage.setTitle("H3K Bakery — Sửa nguyên liệu");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tblNguyenLieu.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
            NguyenLieuDTO ketQua = ctrl.getKetQua();
            if (ketQua == null)
                return;
            presenter.suaNguyenLieuTuDialog(
                    ketQua.getMaNL(), ketQua.getTenNL(), ketQua.getXuatXu(),
                    ketQua.getMucTonAnToan(), ketQua.getMaDVT());
        } catch (Exception e) {
            hienThiLoiLabel("Không thể mở dialog sửa nguyên liệu: " + e.getMessage());
        }
    }

    private void onXoaHoiConfirm(NguyenLieuDTO nl) {
        if (nl == null)
            return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText("Xóa nguyên liệu: " + nl.getTenNL() + "?");
        confirm.setContentText("Dữ liệu lịch sử nhập kho liên quan vẫn được giữ nguyên.");
        DialogHelper.applyBakeryTheme(confirm);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK)
                presenter.xoaNguyenLieuTheoMa(nl.getMaNL(), nl.getTenNL());
        });
    }

    @FXML
    private void onTimKiem() {
        if (presenter != null)
            presenter.timKiem();
    }

    @FXML
    private void onLamMoi() {
        if (txtTimKiem != null)
            txtTimKiem.clear();
        if (presenter != null)
            presenter.taiDanhSach();
    }

    @FXML
    private void onSuaAction() {
        onSua(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void onXoaAction() {
        onXoaHoiConfirm(tblNguyenLieu.getSelectionModel().getSelectedItem());
    }
}

