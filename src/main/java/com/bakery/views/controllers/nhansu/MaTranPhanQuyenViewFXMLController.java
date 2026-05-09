package com.bakery.views.controllers.nhansu;

import com.bakery.model.dao.nhansu.PhanQuyenDAO;
import com.bakery.model.dto.nhansu.ChucNangDTO;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.model.dto.nhansu.VaiTroDTO;
import com.bakery.services.nhansu.NhanVienService;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.*;

/**
 * Controller cho MaTranPhanQuyenView.
 * Tab 1: Gán vai trò cho từng nhân viên (ma trận checkbox NV × VaiTro).
 * Tab 2: Chọn vai trò → xem/sửa quyền chi tiết
 * CAN_VIEW/ADD/EDIT/DELETE/DOWNLOAD.
 * Mọi dữ liệu đọc/ghi từ DB. Không có Mock Data.
 */
public class MaTranPhanQuyenViewFXMLController extends BaseController {

    // ── Tab 1: Nhân viên & Vai trò ────────────────────────────────────────
    @FXML
    private TabPane tabPane;
    @FXML
    private ScrollPane scrollMatrix;
    @FXML
    private TextField txtTimNhanVien;
    @FXML
    private Label lblStatusVaiTro;

    // ── Tab 2: Quyền chức năng ────────────────────────────────────────────
    @FXML
    private ComboBox<VaiTroDTO> cbVaiTro;
    @FXML
    private TableView<ChucNangRow> tblChucNang;
    @FXML
    private TableColumn<ChucNangRow, String> colTenCN;
    @FXML
    private TableColumn<ChucNangRow, String> colModule;
    @FXML
    private TableColumn<ChucNangRow, Boolean> colView;
    @FXML
    private TableColumn<ChucNangRow, Boolean> colAdd;
    @FXML
    private TableColumn<ChucNangRow, Boolean> colEdit;
    @FXML
    private TableColumn<ChucNangRow, Boolean> colDel;
    @FXML
    private TableColumn<ChucNangRow, Boolean> colDown;
    @FXML
    private Label lblStatusChucNang;

    // ── State ────────────────────────────────────────────────────────────
    private final NhanVienService nhanVienService = new NhanVienService();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();

    private List<NhanVienDTO> cachedNhanVien = new ArrayList<>();
    private List<VaiTroDTO> cachedVaiTro = new ArrayList<>();
    // matrixMap: maNV → list of CheckBox theo thứ tự cachedVaiTro
    private final Map<Integer, List<CheckBox>> matrixMap = new LinkedHashMap<>();

    private final ObservableList<ChucNangRow> chucNangRows = FXCollections.observableArrayList();

    // ── Wrapper row cho Tab 2 ─────────────────────────────────────────────
    public static class ChucNangRow {
        final ChucNangDTO dto;
        final SimpleBooleanProperty canView;
        final SimpleBooleanProperty canAdd;
        final SimpleBooleanProperty canEdit;
        final SimpleBooleanProperty canDel;
        final SimpleBooleanProperty canDown;

        ChucNangRow(ChucNangDTO dto) {
            this.dto = dto;
            this.canView = new SimpleBooleanProperty(dto.isCanView());
            this.canAdd = new SimpleBooleanProperty(dto.isCanAdd());
            this.canEdit = new SimpleBooleanProperty(dto.isCanEdit());
            this.canDel = new SimpleBooleanProperty(dto.isCanDelete());
            this.canDown = new SimpleBooleanProperty(dto.isCanDownload());
        }
    }

    // ── Initialize ────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        setupTab2Table();
        taiDuLieuNenTang();
    }

    private void taiDuLieuNenTang() {
        lblStatusVaiTro.setText("Đang tải dữ liệu...");
        Thread t = new Thread(() -> {
            try {
                List<NhanVienDTO> dsNV = nhanVienService.layTatCaNhanVien();
                Map<Integer, String> roleMap = nhanVienService.layDanhSachVaiTro();
                List<VaiTroDTO> dsVT = new ArrayList<>();
                for (Map.Entry<Integer, String> e : roleMap.entrySet()) {
                    VaiTroDTO vt = new VaiTroDTO();
                    vt.setMaVaiTro(e.getKey());
                    vt.setTenVaiTro(e.getValue());
                    dsVT.add(vt);
                }
                Platform.runLater(() -> {
                    cachedNhanVien = dsNV;
                    cachedVaiTro = dsVT;
                    xayDungMatrixVaiTro(dsNV, dsVT);
                    // Tab 2: fill ComboBox
                    cbVaiTro.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(VaiTroDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? null : item.getTenVaiTro());
                        }
                    });
                    cbVaiTro.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(VaiTroDTO item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(empty || item == null ? "-- Chọn vai trò --" : item.getTenVaiTro());
                        }
                    });
                    cbVaiTro.setItems(FXCollections.observableArrayList(dsVT));
                    lblStatusVaiTro
                            .setText("Đã tải " + dsNV.size() + " nhân viên · " + dsVT.size() + " vai trò từ DB.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusVaiTro.setText("Lỗi tải dữ liệu: " + e.getMessage()));
            }
        }, "phan-quyen-init");
        t.setDaemon(true);
        t.start();
    }

    // ── Tab 1: Xây dựng lưới NV × VaiTro ────────────────────────────────

    private void xayDungMatrixVaiTro(List<NhanVienDTO> dsNV, List<VaiTroDTO> dsVT) {
        matrixMap.clear();

        GridPane grid = new GridPane();
        grid.setHgap(24);
        grid.setVgap(12);
        grid.setPadding(new Insets(16));
        grid.setStyle("-fx-background-color: white;");

        // Header: tên vai trò
        Label lblHeaderNV = new Label("NHÂN VIÊN");
        lblHeaderNV.setStyle("-fx-font-weight: bold; -fx-text-fill: #92400E; -fx-font-size: 13px;");
        grid.add(lblHeaderNV, 0, 0);

        for (int c = 0; c < dsVT.size(); c++) {
            Label lblVT = new Label(dsVT.get(c).getTenVaiTro());
            lblVT.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2937; -fx-min-width: 90; -fx-alignment: CENTER;");
            grid.add(lblVT, c + 1, 0);
        }

        // Rows: nhân viên
        for (int row = 0; row < dsNV.size(); row++) {
            NhanVienDTO nv = dsNV.get(row);
            Label lblNV = new Label(nv.getHoTen() + "\n(" + nv.getTenDangNhap() + ")");
            lblNV.setStyle("-fx-text-fill: #374151; -fx-font-size: 12px;");
            grid.add(lblNV, 0, row + 1);

            List<CheckBox> cbs = new ArrayList<>();
            for (int c = 0; c < dsVT.size(); c++) {
                int maVT = dsVT.get(c).getMaVaiTro();
                CheckBox cb = new CheckBox();
                cb.setSelected(nv.getDanhSachMaVaiTro() != null && nv.getDanhSachMaVaiTro().contains(maVT));

                VBox cell = new VBox(cb);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-padding: 4;");
                grid.add(cell, c + 1, row + 1);
                cbs.add(cb);
            }
            matrixMap.put(nv.getMaNV(), cbs);
        }

        scrollMatrix.setContent(grid);
    }

    @FXML
    private void onTimKiemNhanVien() {
        String kw = txtTimNhanVien.getText().trim().toLowerCase();
        List<NhanVienDTO> filtered = kw.isEmpty() ? cachedNhanVien
                : cachedNhanVien.stream()
                        .filter(nv -> (nv.getHoTen() != null && nv.getHoTen().toLowerCase().contains(kw))
                                || (nv.getTenDangNhap() != null && nv.getTenDangNhap().toLowerCase().contains(kw)))
                        .toList();
        xayDungMatrixVaiTro(filtered, cachedVaiTro);
        lblStatusVaiTro.setText("Hiển thị " + filtered.size() + " / " + cachedNhanVien.size() + " nhân viên.");
    }

    @FXML
    private void onLuuPhanVaiTro() {
        lblStatusVaiTro.setText("Đang lưu...");
        Thread t = new Thread(() -> {
            try {
                int count = 0;
                for (NhanVienDTO nv : cachedNhanVien) {
                    List<CheckBox> cbs = matrixMap.get(nv.getMaNV());
                    if (cbs == null)
                        continue;
                    List<Integer> selectedIds = new ArrayList<>();
                    for (int i = 0; i < cachedVaiTro.size(); i++) {
                        if (cbs.get(i).isSelected()) {
                            selectedIds.add(cachedVaiTro.get(i).getMaVaiTro());
                        }
                    }
                    nhanVienService.capNhatVaiTro(nv.getMaNV(), selectedIds);
                    count++;
                }
                final int saved = count;
                Platform.runLater(
                        () -> lblStatusVaiTro.setText(" Đã lưu phân vai trò cho " + saved + " nhân viên vào DB."));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusVaiTro.setText("Lỗi lưu: " + e.getMessage()));
            }
        }, "phan-quyen-luu-vaitro");
        t.setDaemon(true);
        t.start();
    }

    // ── Tab 2: Ma trận quyền chức năng ───────────────────────────────────

    @SuppressWarnings("unchecked")
    private void setupTab2Table() {
        tblChucNang.setEditable(true);
        tblChucNang.setItems(chucNangRows);
        tblChucNang.setPlaceholder(new Label("Chọn vai trò để xem quyền."));

        colTenCN.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dto.getTenChucNang()));
        colModule.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().dto.getModule() != null ? c.getValue().dto.getModule().name() : "—"));

        // Checkbox columns (editable)
        colView.setCellValueFactory(c -> c.getValue().canView);
        colView.setCellFactory(CheckBoxTableCell.forTableColumn(colView));
        colView.setEditable(true);

        colAdd.setCellValueFactory(c -> c.getValue().canAdd);
        colAdd.setCellFactory(CheckBoxTableCell.forTableColumn(colAdd));
        colAdd.setEditable(true);

        colEdit.setCellValueFactory(c -> c.getValue().canEdit);
        colEdit.setCellFactory(CheckBoxTableCell.forTableColumn(colEdit));
        colEdit.setEditable(true);

        colDel.setCellValueFactory(c -> c.getValue().canDel);
        colDel.setCellFactory(CheckBoxTableCell.forTableColumn(colDel));
        colDel.setEditable(true);

        colDown.setCellValueFactory(c -> c.getValue().canDown);
        colDown.setCellFactory(CheckBoxTableCell.forTableColumn(colDown));
        colDown.setEditable(true);
    }

    @FXML
    private void onTaiQuyenVaiTro() {
        VaiTroDTO selected = cbVaiTro.getValue();
        if (selected == null) {
            lblStatusChucNang.setText("⚠ Vui lòng chọn vai trò.");
            return;
        }
        lblStatusChucNang.setText("Đang tải quyền...");
        Thread t = new Thread(() -> {
            try {
                PhanQuyenDAO.RolePermissionInfo info = phanQuyenDAO
                        .layThongTinPhanQuyenTheoVaiTro(selected.getMaVaiTro());

                // Lấy TOÀN BỘ chức năng, sau đó merge với quyền đã có
                List<ChucNangDTO> tatCaCN = phanQuyenDAO.layToanBoChucNang();
                Map<Integer, ChucNangDTO> quyenHienCo = new LinkedHashMap<>();
                if (info != null) {
                    for (ChucNangDTO cn : info.getDanhSachChucNang()) {
                        quyenHienCo.put(cn.getMaChucNang(), cn);
                    }
                }

                List<ChucNangRow> rows = new ArrayList<>();
                for (ChucNangDTO cn : tatCaCN) {
                    ChucNangDTO merged = quyenHienCo.getOrDefault(cn.getMaChucNang(), cn);
                    rows.add(new ChucNangRow(merged));
                }

                Platform.runLater(() -> {
                    chucNangRows.setAll(rows);
                    lblStatusChucNang.setText("Vai trò: " + selected.getTenVaiTro()
                            + " — " + rows.size() + " chức năng. Checkbox có thể chỉnh sửa.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusChucNang.setText("❌ Lỗi: " + e.getMessage()));
            }
        }, "phan-quyen-tai-chuc-nang");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onLuuQuyenChucNang() {
        VaiTroDTO selected = cbVaiTro.getValue();
        if (selected == null) {
            lblStatusChucNang.setText("⚠ Chưa chọn vai trò.");
            return;
        }
        if (chucNangRows.isEmpty()) {
            lblStatusChucNang.setText("⚠ Không có dữ liệu để lưu.");
            return;
        }

        lblStatusChucNang.setText("Đang lưu quyền...");
        List<ChucNangRow> snapshot = new ArrayList<>(chucNangRows);
        int maVT = selected.getMaVaiTro();

        Thread t = new Thread(() -> {
            try {
                for (ChucNangRow row : snapshot) {
                    phanQuyenDAO.capNhatQuyenChiTiet(
                            maVT, row.dto.getMaChucNang(),
                            row.canView.get(), row.canAdd.get(),
                            row.canEdit.get(), row.canDel.get(), row.canDown.get());
                }
                Platform.runLater(() -> lblStatusChucNang.setText(
                        "✅ Đã lưu quyền " + snapshot.size() + " chức năng cho vai trò "
                                + selected.getTenVaiTro() + " vào DB."));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatusChucNang.setText("❌ Lỗi lưu: " + e.getMessage()));
            }
        }, "phan-quyen-luu-chuc-nang");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onRefresh() {
        chucNangRows.clear();
        lblStatusChucNang.setText("Chọn vai trò để xem quyền.");
        taiDuLieuNenTang();
    }
}
