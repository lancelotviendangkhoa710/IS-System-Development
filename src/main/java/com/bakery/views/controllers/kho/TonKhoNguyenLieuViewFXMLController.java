package com.bakery.views.controllers.kho;

import com.bakery.services.baocao.ThongKeService;
import com.bakery.views.controllers.BaseController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller cho màn hình Tồn Kho Nguyên Liệu (di chuyển từ Báo cáo).
 * Cập nhật định kỳ lượng tồn kho thực tế của các loại nguyên liệu.
 */
public class TonKhoNguyenLieuViewFXMLController extends BaseController {

    @FXML private DatePicker dpTonKhoTuNgay;
    @FXML private DatePicker dpTonKhoDenNgay;
    @FXML private Button btnXemTonKho;
    @FXML private Label lblTonKhoHetHang;
    @FXML private Label lblTonKhoSapHet;
    @FXML private Label lblTonKhoDuHang;
    @FXML private TableView<String[]> tableTonKho;

    private final ThongKeService thongKeService = new ThongKeService();

    @FXML
    public void initialize() {
        setupTonKhoTableColumns();
        onXemBaoCaoTonKho();
        // Tự động làm mới dữ liệu sau mỗi 10 giây khi màn hình hoạt động
        batDauAutoRefresh(tableTonKho, this::onXemBaoCaoTonKhoAmThang, 10);
    }

    @SuppressWarnings("unchecked")
    private void setupTonKhoTableColumns() {
        if (tableTonKho == null) return;
        // 6 cột tương ứng String[]{tenNL, dvt, tonDau, nhap, xuat, tonCuoi}
        for (int i = 0; i < tableTonKho.getColumns().size(); i++) {
            final int idx = i;
            ((TableColumn<String[], String>) tableTonKho.getColumns().get(i))
                .setCellValueFactory(c -> new SimpleStringProperty(
                    c.getValue() != null && c.getValue().length > idx
                        ? c.getValue()[idx] : ""));
        }
        // Tô đỏ dòng tồn cuối kỳ <= 0
        tableTonKho.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(String[] item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.length < 6) {
                    getStyleClass().removeAll("row-danger", "row-warning");
                } else {
                    try {
                        double tonCuoi = Double.parseDouble(item[5].replace(",", "."));
                        getStyleClass().removeAll("row-danger", "row-warning");
                        if (tonCuoi <= 0) {
                            getStyleClass().add("row-danger");
                        }
                    } catch (NumberFormatException ignored) { }
                }
            }
        });
        // Mặc định kỳ = tháng hiện tại
        dpTonKhoTuNgay.setValue(LocalDate.now().withDayOfMonth(1));
        dpTonKhoDenNgay.setValue(LocalDate.now());
    }

    @FXML
    private void onXemBaoCaoTonKho() {
        if (btnXemTonKho != null) btnXemTonKho.setDisable(true);
        LocalDate tu  = dpTonKhoTuNgay  != null ? dpTonKhoTuNgay.getValue()  : null;
        LocalDate den = dpTonKhoDenNgay != null ? dpTonKhoDenNgay.getValue() : null;

        new Thread(() -> {
            try {
                Map<String, Long> tongHop = thongKeService.getTonKhoTongHop();
                List<String[]> rows = thongKeService.getBaoCaoTonKho(tu, den);

                Platform.runLater(() -> {
                    if (lblTonKhoHetHang != null)
                        lblTonKhoHetHang.setText(String.valueOf(tongHop.getOrDefault("HET_HANG", 0L)));
                    if (lblTonKhoSapHet != null)
                        lblTonKhoSapHet.setText(String.valueOf(tongHop.getOrDefault("SAP_HET", 0L)));
                    if (lblTonKhoDuHang != null)
                        lblTonKhoDuHang.setText(String.valueOf(tongHop.getOrDefault("DU_HANG", 0L)));
                    if (tableTonKho != null)
                        tableTonKho.setItems(FXCollections.observableArrayList(rows));
                    if (btnXemTonKho != null) btnXemTonKho.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    hienThiCanhBao("Lỗi tải tồn kho", e.getMessage());
                    if (btnXemTonKho != null) btnXemTonKho.setDisable(false);
                });
            }
        }, "bao-cao-ton-kho-view").start();
    }

    /**
     * Tải dữ liệu tồn kho ngầm không hiển thị thông báo lỗi Alert (dùng cho auto-refresh tránh phiền user).
     */
    private void onXemBaoCaoTonKhoAmThang() {
        LocalDate tu  = dpTonKhoTuNgay  != null ? dpTonKhoTuNgay.getValue()  : null;
        LocalDate den = dpTonKhoDenNgay != null ? dpTonKhoDenNgay.getValue() : null;

        new Thread(() -> {
            try {
                Map<String, Long> tongHop = thongKeService.getTonKhoTongHop();
                List<String[]> rows = thongKeService.getBaoCaoTonKho(tu, den);

                Platform.runLater(() -> {
                    if (lblTonKhoHetHang != null)
                        lblTonKhoHetHang.setText(String.valueOf(tongHop.getOrDefault("HET_HANG", 0L)));
                    if (lblTonKhoSapHet != null)
                        lblTonKhoSapHet.setText(String.valueOf(tongHop.getOrDefault("SAP_HET", 0L)));
                    if (lblTonKhoDuHang != null)
                        lblTonKhoDuHang.setText(String.valueOf(tongHop.getOrDefault("DU_HANG", 0L)));
                    if (tableTonKho != null)
                        tableTonKho.setItems(FXCollections.observableArrayList(rows));
                });
            } catch (Exception ignored) { }
        }, "bao-cao-ton-kho-silent").start();
    }
}
