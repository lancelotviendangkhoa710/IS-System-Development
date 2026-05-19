package com.bakery.views.controllers.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dao.nhansu.VaiTroDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Controller cho PhanQuyenVaiTroView.
 * Hiển thị danh sách vai trò bên trái, cho phép chỉnh quyền từng chức năng bên phải.
 * Tuân thủ MVP: mọi thao tác DB qua DAO layer.
 */
public class PhanQuyenVaiTroViewFXMLController extends BaseController {

    @FXML private ListView<VaiTroDTO>              lstVaiTro;
    @FXML private Label                             lblTenVaiTro;
    @FXML private TableView<ChucNangDTO>            tblChucNang;
    @FXML private TableColumn<ChucNangDTO, String>  colTenChucNang;
    @FXML private TableColumn<ChucNangDTO, String>  colModule;
    @FXML private TableColumn<ChucNangDTO, Boolean> colView;
    @FXML private TableColumn<ChucNangDTO, Boolean> colAdd;
    @FXML private TableColumn<ChucNangDTO, Boolean> colEdit;
    @FXML private TableColumn<ChucNangDTO, Boolean> colDelete;
    @FXML private TableColumn<ChucNangDTO, Boolean> colDownload;
    @FXML private Button btnLuuPhanQuyen;

    private final VaiTroDAO    vaiTroDAO    = new VaiTroDAO();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();

    private final ObservableList<VaiTroDTO>   dsVaiTro   = FXCollections.observableArrayList();
    private final ObservableList<ChucNangDTO> dsChucNang = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupRoleList();
        setupPermissionTable();
        loadRoles();
    }

    // ──────────────────────────────────────────────────────────
    // Setup
    // ──────────────────────────────────────────────────────────

    private void setupRoleList() {
        lstVaiTro.setItems(dsVaiTro);
        lstVaiTro.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(VaiTroDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenVaiTro());
            }
        });
        lstVaiTro.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    if (selected != null) taiChucNangChoVaiTro(selected);
                });
    }

    private void setupPermissionTable() {
        colTenChucNang.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenChucNang()));
        colModule.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getModule() == null ? "" : c.getValue().getModule().name()));

        colView.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanView()));
        colAdd.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanAdd()));
        colEdit.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanEdit()));
        colDelete.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanDelete()));
        colDownload.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isCanDownload()));

        setupCheckboxColumn(colView,     cn -> cn.isCanView(),     (cn, v) -> cn.setCanView(v));
        setupCheckboxColumn(colAdd,      cn -> cn.isCanAdd(),      (cn, v) -> cn.setCanAdd(v));
        setupCheckboxColumn(colEdit,     cn -> cn.isCanEdit(),     (cn, v) -> cn.setCanEdit(v));
        setupCheckboxColumn(colDelete,   cn -> cn.isCanDelete(),   (cn, v) -> cn.setCanDelete(v));
        setupCheckboxColumn(colDownload, cn -> cn.isCanDownload(), (cn, v) -> cn.setCanDownload(v));

        tblChucNang.setItems(dsChucNang);
    }

    /** Tạo cột checkbox inline có thể click trực tiếp trong bảng */
    private void setupCheckboxColumn(
            TableColumn<ChucNangDTO, Boolean> col,
            java.util.function.Function<ChucNangDTO, Boolean> getter,
            java.util.function.BiConsumer<ChucNangDTO, Boolean> setter) {

        col.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox chk = new CheckBox();
            {
                chk.setOnAction(e -> {
                    ChucNangDTO item = getTableView().getItems().get(getIndex());
                    setter.accept(item, chk.isSelected());
                    // Refresh để cập nhật observable
                    getTableView().refresh();
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    chk.setSelected(item);
                    setGraphic(chk);
                }
            }
        });
    }

    // ──────────────────────────────────────────────────────────
    // Data loading
    // ──────────────────────────────────────────────────────────

    private void loadRoles() {
        Task<List<VaiTroDTO>> task = new Task<>() {
            @Override
            protected List<VaiTroDTO> call() throws Exception {
                return vaiTroDAO.layDanhSachVaiTroDangHoatDong();
            }
        };
        task.setOnSucceeded(e -> {
            dsVaiTro.setAll(task.getValue());
            if (!dsVaiTro.isEmpty()) lstVaiTro.getSelectionModel().selectFirst();
        });
        task.setOnFailed(e -> lblThongBao.setText("Lỗi tải vai trò: " + resolveError(task.getException())));
        runBackground(task);
    }

    private void taiChucNangChoVaiTro(VaiTroDTO vaiTro) {
        lblTenVaiTro.setText("Phân quyền: " + vaiTro.getTenVaiTro());
        dsChucNang.clear();
        lblThongBao.setText("Đang tải...");

        Task<PhanQuyenDAO.RolePermissionInfo> task = new Task<>() {
            @Override
            protected PhanQuyenDAO.RolePermissionInfo call() throws Exception {
                return phanQuyenDAO.layThongTinPhanQuyenTheoVaiTro(vaiTro.getMaVaiTro());
            }
        };
        task.setOnSucceeded(e -> {
            PhanQuyenDAO.RolePermissionInfo info = task.getValue();
            if (info != null && !info.getDanhSachChucNang().isEmpty()) {
                // Chỉ hiển thị dòng có ít nhất 1 quyền được cấp — ẩn dòng tất cả flags = false
                List<ChucNangDTO> coQuyen = info.getDanhSachChucNang().stream()
                        .filter(cn -> cn.isCanView() || cn.isCanAdd()
                                   || cn.isCanEdit() || cn.isCanDelete() || cn.isCanDownload())
                        .map(cn -> {
                            ChucNangDTO copy = new ChucNangDTO(
                                    cn.getMaChucNang(), cn.getTenChucNang(), cn.getMoTa(), cn.getModule());
                            copy.setCanView(cn.isCanView());
                            copy.setCanAdd(cn.isCanAdd());
                            copy.setCanEdit(cn.isCanEdit());
                            copy.setCanDelete(cn.isCanDelete());
                            copy.setCanDownload(cn.isCanDownload());
                            return copy;
                        }).toList();

                if (coQuyen.isEmpty()) {
                    lblThongBao.setText("Vai trò '" + vaiTro.getTenVaiTro() + "' chưa được cấp quyền nào.");
                } else {
                    dsChucNang.setAll(coQuyen);
                    lblThongBao.setText("Đã tải " + dsChucNang.size() + " chức năng cho vai trò " + vaiTro.getTenVaiTro());
                }
            } else {
                lblThongBao.setText("Vai trò '" + vaiTro.getTenVaiTro() + "' chưa được cấp quyền nào.");
            }
        });
        task.setOnFailed(e -> lblThongBao.setText("Lỗi tải chức năng: " + resolveError(task.getException())));
        runBackground(task);
    }


    private void taiToanBoChucNangMacDinh(VaiTroDTO vaiTro) {
        Task<List<ChucNangDTO>> task = new Task<>() {
            @Override
            protected List<ChucNangDTO> call() throws Exception {
                return phanQuyenDAO.layToanBoChucNang();
            }
        };
        task.setOnSucceeded(e -> {
            // Tất cả quyền mặc định = false
            dsChucNang.setAll(task.getValue().stream().map(cn -> {
                ChucNangDTO copy = new ChucNangDTO(cn.getMaChucNang(), cn.getTenChucNang(), cn.getMoTa(), cn.getModule());
                copy.setCanView(false); copy.setCanAdd(false);
                copy.setCanEdit(false); copy.setCanDelete(false); copy.setCanDownload(false);
                return copy;
            }).toList());
            lblThongBao.setText("Chưa có phân quyền nào. Chọn quyền và nhấn Lưu.");
        });
        task.setOnFailed(e -> lblThongBao.setText("Lỗi tải chức năng: " + resolveError(task.getException())));
        runBackground(task);
    }

    // ──────────────────────────────────────────────────────────
    // Actions
    // ──────────────────────────────────────────────────────────

    @FXML
    private void onLuuPhanQuyen() {
        VaiTroDTO selected = lstVaiTro.getSelectionModel().getSelectedItem();
        if (selected == null) { lblThongBao.setText("Vui lòng chọn vai trò."); return; }
        if (dsChucNang.isEmpty()) { lblThongBao.setText("Không có chức năng để lưu."); return; }

        btnLuuPhanQuyen.setDisable(true);
        lblThongBao.setText("Đang lưu phân quyền...");

        int maVaiTro = selected.getMaVaiTro();
        List<ChucNangDTO> snapshot = List.copyOf(dsChucNang);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (ChucNangDTO cn : snapshot) {
                    phanQuyenDAO.capNhatQuyenChiTiet(
                            maVaiTro, cn.getMaChucNang(),
                            cn.isCanView(), cn.isCanAdd(), cn.isCanEdit(),
                            cn.isCanDelete(), cn.isCanDownload());
                }
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            btnLuuPhanQuyen.setDisable(false);
            lblThongBao.setText("✅ Đã lưu phân quyền cho vai trò: " + selected.getTenVaiTro());
        });
        task.setOnFailed(e -> {
            btnLuuPhanQuyen.setDisable(false);
            lblThongBao.setText("❌ Lỗi lưu phân quyền: " + resolveError(task.getException()));
        });
        runBackground(task);
    }

    @FXML
    private void onCapTatCa() {
        dsChucNang.forEach(cn -> {
            cn.setCanView(true); cn.setCanAdd(true);
            cn.setCanEdit(true); cn.setCanDelete(true); cn.setCanDownload(true);
        });
        tblChucNang.refresh();
    }

    @FXML
    private void onThuHoiTatCa() {
        dsChucNang.forEach(cn -> {
            cn.setCanView(false); cn.setCanAdd(false);
            cn.setCanEdit(false); cn.setCanDelete(false); cn.setCanDownload(false);
        });
        tblChucNang.refresh();
    }

    @FXML
    private void onLamMoi() {
        VaiTroDTO selected = lstVaiTro.getSelectionModel().getSelectedItem();
        if (selected != null) taiChucNangChoVaiTro(selected);
    }

    // ──────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────

    private void runBackground(Task<?> task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private String resolveError(Throwable ex) {
        if (ex == null) return "Lỗi không xác định";
        Throwable cur = ex;
        while (cur.getCause() != null) cur = cur.getCause();
        return cur.getMessage() == null ? ex.toString() : cur.getMessage();
    }
}
