package com.bakery.views.controllers.kho;

import com.bakery.model.dto.kho.DanhMucSPDTO;
import com.bakery.presenters.kho.DanhMucSPPresenter;
import com.bakery.views.interfaces.kho.IDanhMucSPView;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class DanhMucSPViewFXMLController extends BaseController implements IDanhMucSPView {

    @FXML private TableView<DanhMucSPDTO> tblDanhMuc;
    @FXML private TableColumn<DanhMucSPDTO, Integer> colMaDM;
    @FXML private TableColumn<DanhMucSPDTO, String> colTenDM;

    @FXML private TextField txtTimKiem;

    @FXML private Button btnThemMoi;
    @FXML private Button btnSua;
    @FXML private Button btnXoa;

    private final ObservableList<DanhMucSPDTO> masterData = FXCollections.observableArrayList();
    private DanhMucSPPresenter presenter;
    private String tempTenDanhMucInput = "";

    @FXML
    public void initialize() {
        setupTable();
        presenter = new DanhMucSPPresenter(this, 1);
        setupSelectionListener();
        lamMoiForm();
        presenter.taiDanhSach();
        // Auto-refresh: mỗi 30s tự query DB — danh mục ít thay đổi, interval dài hơn các module nghiệp vụ
        batDauAutoRefresh(tblDanhMuc, () -> presenter.taiDanhSach(), 30);
    }

    private void setupTable() {
        colMaDM.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getMaDM()).asObject());
        colTenDM.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenDM()));
        tblDanhMuc.setItems(masterData);
    }

    private void setupSelectionListener() {
        tblDanhMuc.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, newVal) -> {
                    presenter.onChonDanhMuc(newVal);
                    boolean coChon = newVal != null;
                    if (btnSua != null) btnSua.setDisable(!coChon);
                    if (btnXoa != null) btnXoa.setDisable(!coChon);
                });
    }

    // ─── Thực thi IDanhMucSPView ────────────────────────────────────────

    @Override public void hienThiDanhSach(List<DanhMucSPDTO> ds) { masterData.setAll(ds); }

    @Override
    public void hienThiChiTiet(DanhMucSPDTO dm) {
        // No-op vì không còn form inline bên phải, SelectionListener tự xử lý bật/tắt nút
    }

    @Override public void hienThiLoi(String msg) { hienThiLoiLabel(msg); }
    @Override public void hienThiThanhCong(String msg) { hienThiThanhCongLabel(msg); }

    @Override
    public void lamMoiForm() {
        tblDanhMuc.getSelectionModel().clearSelection();
        if (btnSua != null) btnSua.setDisable(true);
        if (btnXoa != null) btnXoa.setDisable(true);
        lblThongBao.setText("");
    }

    @Override public DanhMucSPDTO getSelectedCategory() { return tblDanhMuc.getSelectionModel().getSelectedItem(); }
    @Override public String getTenDanhMucInput() { return tempTenDanhMucInput; }
    @Override public String getTuKhoaTimKiemInput() { return txtTimKiem.getText().trim(); }

    // ─── FXML Actions ────────────────────────────────────────────────────

    @FXML
    private void onThemMoi() {
        // Mở dialog nhập tên thay vì đọc form trực tiếp
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm danh mục mới");
        dialog.setHeaderText("Nhập tên danh mục sản phẩm");
        dialog.setContentText("Tên danh mục:");
        try {
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/bakery.css").toExternalForm());
        } catch (Exception ignored) {}
        dialog.showAndWait().ifPresent(ten -> {
            if (!ten.trim().isEmpty()) {
                presenter.themDanhMuc(ten.trim());
            } else {
                hienThiLoiLabel("⚠ Tên danh mục không được để trống.");
            }
        });
    }

    @FXML
    private void onSuaAction() {
        DanhMucSPDTO selected = getSelectedCategory();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getTenDM());
        dialog.setTitle("Sửa danh mục");
        dialog.setHeaderText("Chỉnh sửa tên danh mục sản phẩm");
        dialog.setContentText("Tên danh mục:");
        try {
            dialog.getDialogPane().getStylesheets()
                    .add(getClass().getResource("/css/bakery.css").toExternalForm());
        } catch (Exception ignored) {}

        dialog.showAndWait().ifPresent(ten -> {
            if (!ten.trim().isEmpty()) {
                tempTenDanhMucInput = ten.trim();
                presenter.suaDanhMuc();
            } else {
                hienThiLoiLabel("⚠ Tên danh mục không được để trống.");
            }
        });
    }

    @FXML private void onXoaAction() { presenter.xoaDanhMuc(); }
    @FXML private void onTimKiem() { presenter.timKiem(); }
    @FXML private void onLamMoi() { lamMoiForm(); presenter.taiDanhSach(); }
}
