package com.bakery.views.controllers.kho;

import com.bakery.model.dao.kho.PhieuXuatKhoDAO;
import com.bakery.model.dao.kho.SanPhamDAO;
import com.bakery.model.dto.kho.PhieuXuatKhoDTO;
import com.bakery.model.dto.kho.SanPhamDTO;
import com.bakery.utils.SessionContext;
import com.bakery.views.controllers.BaseController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller Quản lý Xuất Kho.
 * Hiển thị lịch sử phiếu xuất và dialog xuất hủy thành phẩm.
 * Gọi PROC_XUATHUYBANH qua PhieuXuatKhoDAO.
 */
public class XuatKhoViewFXMLController extends BaseController {

    @FXML private Label lblTitle;
    @FXML private TableView<PhieuXuatKhoDTO> tblData;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colDate;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colUser;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colContent;
    @FXML private TableColumn<PhieuXuatKhoDTO, String> colStatus;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PhieuXuatKhoDAO xuatKhoDAO = new PhieuXuatKhoDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final ObservableList<PhieuXuatKhoDTO> danhSach = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        lblTitle.setText("QUẢN LÝ XUẤT KHO");
        setupTable();
        taiDuLieu();
    }

    private void setupTable() {
        colDate.setCellValueFactory(c -> {
            String text = c.getValue().getNgayXuat() != null ? c.getValue().getNgayXuat().format(FMT) : "—";
            return new SimpleStringProperty(text);
        });
        colUser.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getTenNhanVien())));
        colContent.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getLyDoXuat())));
        colStatus.setCellValueFactory(c ->
                new SimpleStringProperty("Phiếu #" + c.getValue().getMaPX()));
        tblData.setItems(danhSach);
        tblData.setPlaceholder(new Label("Chưa có phiếu xuất nào."));
    }

    private void taiDuLieu() {
        Thread t = new Thread(() -> {
            try {
                List<PhieuXuatKhoDTO> ds = xuatKhoDAO.layDanhSachPhieuXuat();
                javafx.application.Platform.runLater(() -> danhSach.setAll(ds));
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        hienThiLoiLabel("Lỗi tải danh sách phiếu xuất: " + e.getMessage()));
            }
        }, "xuat-kho-tai-du-lieu");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onAction() {
        moDialogXuatHuy();
    }

    @FXML
    private void onBack() {
        quayLaiMenuChinh(lblTitle);
    }

    // ── Dialog xuất hủy thành phẩm ────────────────────────────────────

    private void moDialogXuatHuy() {
        List<SanPhamDTO> dsSP;
        try {
            dsSP = sanPhamDAO.layTatCaSanPhamQuanLy();
        } catch (Exception e) {
            hienThiLoiLabel("Không thể tải danh sách sản phẩm: " + e.getMessage());
            return;
        }
        if (dsSP.isEmpty()) {
            hienThiLoiLabel("Chưa có sản phẩm nào trong hệ thống.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Xuất Hủy Thành Phẩm");
        dialog.setHeaderText("Chọn sản phẩm cần hủy và nhập số lượng");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Combo sản phẩm
        ComboBox<SanPhamDTO> cbSP = new ComboBox<>(FXCollections.observableArrayList(dsSP));
        cbSP.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(SanPhamDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTenSP() + " (Tồn: " + (int)item.getSoLuongTon() + ")");
            }
        });
        cbSP.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(SanPhamDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "— Chọn sản phẩm —" : item.getTenSP());
            }
        });
        cbSP.getSelectionModel().selectFirst();
        cbSP.setMaxWidth(Double.MAX_VALUE);

        TextField txtSoLuong = new TextField("1");
        TextField txtLyDo = new TextField("Hủy hàng hỏng");
        txtLyDo.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("Sản phẩm:"), 0, 0);
        grid.add(cbSP, 1, 0);
        grid.add(new Label("Số lượng hủy:"), 0, 1);
        grid.add(txtSoLuong, 1, 1);
        grid.add(new Label("Lý do:"), 0, 2);
        grid.add(txtLyDo, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(480);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                SanPhamDTO sp = cbSP.getValue();
                if (sp == null) { hienThiLoiLabel("Vui lòng chọn sản phẩm."); return; }
                double soLuong;
                try {
                    soLuong = Double.parseDouble(txtSoLuong.getText().trim());
                    if (soLuong <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    hienThiLoiLabel("Số lượng không hợp lệ."); return;
                }
                String lyDo = txtLyDo.getText().trim();
                if (lyDo.isEmpty()) lyDo = "Xuất hủy";
                int maNV = SessionContext.getInstance().getMaNV();
                final String lyDoFinal = lyDo;
                final double slFinal = soLuong;

                Thread t = new Thread(() -> {
                    try {
                        xuatKhoDAO.xuatHuyBanh(sp.getMaSP(), slFinal, lyDoFinal, maNV);
                        javafx.application.Platform.runLater(() -> {
                            hienThiThanhCongLabel("Đã xuất hủy " + (int)slFinal + " " + sp.getTenSP() + ".");
                            taiDuLieu();
                        });
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() ->
                                hienThiLoiLabel("Lỗi xuất hủy: " + e.getMessage()));
                    }
                }, "xuat-huy-banh");
                t.setDaemon(true);
                t.start();
            }
        });
    }

    private static String nvl(String s) { return s != null ? s : "—"; }
}
