package com.bakery.views.controllers.hethong;

import com.bakery.model.dto.hethong.GiamSatCaDTO;
import com.bakery.presenters.hethong.GiamSatCaPresenter;
import com.bakery.services.hethong.GiamSatCaService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.hethong.IGiamSatCaView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller cho màn hình "Giám sát tiền mặt đóng ca".
 * Chỉ đọc — không có CUD nào.
 * Chỉ Quản lý được truy cập (kiểm tra tại MainMenuViewFXMLController).
 */
public class GiamSatCaViewFXMLController extends BaseController implements IGiamSatCaView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private StackPane paneLoading;
    @FXML private TableView<GiamSatCaDTO> tblCa;
    @FXML private TableColumn<GiamSatCaDTO, String> colMaCa;
    @FXML private TableColumn<GiamSatCaDTO, String> colNhanVien;
    @FXML private TableColumn<GiamSatCaDTO, String> colMayPOS;
    @FXML private TableColumn<GiamSatCaDTO, String> colMoCa;
    @FXML private TableColumn<GiamSatCaDTO, String> colDongCa;
    @FXML private TableColumn<GiamSatCaDTO, String> colTrangThai;
    @FXML private TableColumn<GiamSatCaDTO, String> colTienDau;
    @FXML private TableColumn<GiamSatCaDTO, String> colHeThong;
    @FXML private TableColumn<GiamSatCaDTO, String> colThucTe;
    @FXML private TableColumn<GiamSatCaDTO, String> colChenhLech;
    @FXML private TableColumn<GiamSatCaDTO, String> colLyDo;

    private final GiamSatCaPresenter presenter =
            new GiamSatCaPresenter(this, new GiamSatCaService());

    @FXML
    public void initialize() {
        khoiTaoCot();
        // Tô màu dòng: đỏ nếu âm tiền
        tblCa.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(GiamSatCaDTO item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-danger", "row-success");
                if (!empty && item != null && item.isAmTien()) {
                    getStyleClass().add("row-danger");
                }
            }
        });
        presenter.onInitialize();
    }

    private void khoiTaoCot() {
        colMaCa.setCellValueFactory(c ->
                new SimpleStringProperty("CA_" + String.format("%06d", c.getValue().getMaCa())));
        colNhanVien.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getHoTenNV()));
        colMayPOS.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getMaMayPOS())));
        colMoCa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getThoiGianMoCa() != null
                        ? c.getValue().getThoiGianMoCa().format(FMT) : "—"));
        colDongCa.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getThoiGianDongCa() != null
                        ? c.getValue().getThoiGianDongCa().format(FMT) : "Đang mở"));
        colTrangThai.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getTrangThai())));
        colTienDau.setCellValueFactory(c ->
                new SimpleStringProperty(fmt(c.getValue().getTienKhaiBaoDauCa())));
        colHeThong.setCellValueFactory(c ->
                new SimpleStringProperty(fmt(c.getValue().getTongTienHeThong())));
        colThucTe.setCellValueFactory(c ->
                new SimpleStringProperty(fmt(c.getValue().getTienThucTeDem())));
        colChenhLech.setCellValueFactory(c ->
                new SimpleStringProperty(fmt(c.getValue().getChenhLech())));
        // Tô màu ô chênh lệch
        colChenhLech.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty ? null : val);
                getStyleClass().removeAll("lbl-danger", "lbl-success");
                if (!empty && getTableRow() != null && getTableRow().getItem() != null) {
                    GiamSatCaDTO item = (GiamSatCaDTO) getTableRow().getItem();
                    if (item.getChenhLech() != null) {
                        getStyleClass().add(item.isAmTien() ? "lbl-danger" : "lbl-success");
                    }
                }
            }
        });
        colLyDo.setCellValueFactory(c ->
                new SimpleStringProperty(nvl(c.getValue().getLyDoChenhLech())));
    }

    @FXML
    private void onRefresh() {
        presenter.onRefresh();
    }

    // ── IGiamSatCaView ────────────────────────────────────────────────────────

    @Override
    public void hienThiDanhSachCa(List<GiamSatCaDTO> danhSach) {
        Platform.runLater(() -> {
            tblCa.setItems(FXCollections.observableArrayList(danhSach));
            setVisible(paneLoading, false);
        });
    }

    @Override
    public void hienThiLoi(String msg) {
        Platform.runLater(() -> hienThiLoiLabel(msg));
    }

    @Override
    public void xoaLoi() {
        Platform.runLater(() -> { if (lblThongBao != null) lblThongBao.setText(""); });
    }

    @Override
    public void setLoading(boolean loading) {
        Platform.runLater(() -> setVisible(paneLoading, loading));
    }

    // ── Tiện ích ──────────────────────────────────────────────────────────────

    private static String fmt(java.math.BigDecimal v) {
        return v != null ? CurrencyFormatter.format(v) : "—";
    }

    private static String nvl(String s) {
        return (s != null && !s.isBlank()) ? s : "—";
    }

    private static void setVisible(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
