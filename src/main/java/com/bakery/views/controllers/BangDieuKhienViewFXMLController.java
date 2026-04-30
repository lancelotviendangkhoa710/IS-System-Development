package com.bakery.views.controllers;

import com.bakery.model.dto.BangDieuKhienKPIDTO;
import com.bakery.model.dto.TopSanPhamDTO;
import com.bakery.presenters.BangDieuKhienPresenter;
import com.bakery.services.BangDieuKhienService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.interfaces.IBangDieuKhienView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class BangDieuKhienViewFXMLController implements IBangDieuKhienView {

    @FXML private Label     lblDoanhThu;
    @FXML private Label     lblDonHoanThanh;
    @FXML private Label     lblDonDangXuLy;
    @FXML private Label     lblCanhBaoTonKho;
    @FXML private VBox      cardDongCa;
    @FXML private VBox      vboxTop5;
    @FXML private StackPane paneLoadingTop5;

    private final BangDieuKhienPresenter presenter = new BangDieuKhienPresenter(this, new BangDieuKhienService());

    @FXML
    public void initialize() {
        presenter.onInitialize();
    }

    @FXML
    private void onThucHienDongCa() {
        presenter.onThucHienDongCa();
    }

    // ── IDashboardView ────────────────────────────────────────────────────────

    @Override
    public void setCardDongCaVisible(boolean visible) {
        Platform.runLater(() -> {
            cardDongCa.setVisible(visible);
            cardDongCa.setManaged(visible);
        });
    }

    @Override
    public void hienThiKPI(BangDieuKhienKPIDTO kpi) {
        Platform.runLater(() -> {
            lblDoanhThu.setText(CurrencyFormatter.format(kpi.doanhThuHomNay()));
            lblDonHoanThanh.setText(kpi.soHoaDonHomNay() + " đơn");
            lblDonDangXuLy.setText(kpi.donDangXuLy() + " đơn");

            int canhBao = kpi.canhBaoTonKho();
            lblCanhBaoTonKho.setText(canhBao + " mặt hàng");
            if (canhBao > 0)
                lblCanhBaoTonKho.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 26px; "
                        + "-fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        });
    }

    @Override
    public void hienThiLoiKPI() {
        Platform.runLater(() -> {
            lblDoanhThu.setText("—");
            lblDonHoanThanh.setText("—");
            lblDonDangXuLy.setText("—");
            lblCanhBaoTonKho.setText("—");
        });
    }

    @Override
    public void setTop5Loading(boolean loading) {
        Platform.runLater(() -> {
            paneLoadingTop5.setVisible(loading);
            paneLoadingTop5.setManaged(loading);
            vboxTop5.setVisible(!loading);
            vboxTop5.setManaged(!loading);
        });
    }

    @Override
    public void hienThiTop5(List<TopSanPhamDTO> ds) {
        Platform.runLater(() -> {
            vboxTop5.getChildren().clear();
            if (ds.isEmpty()) {
                Label lblTrong = new Label("Chưa có dữ liệu trong tháng này.");
                lblTrong.getStyleClass().add("text-secondary");
                vboxTop5.getChildren().add(lblTrong);
                return;
            }
            int maxBan = ds.stream().mapToInt(TopSanPhamDTO::tongBan).max().orElse(1);
            for (TopSanPhamDTO sp : ds) {
                vboxTop5.getChildren().add(taoRowTop5(sp, maxBan));
            }
        });
    }

    @Override
    public void hienThiLoiTop5() {
        Platform.runLater(() -> {
            vboxTop5.getChildren().clear();
            Label lblLoi = new Label("Không thể tải dữ liệu.");
            lblLoi.getStyleClass().add("text-error");
            vboxTop5.getChildren().add(lblLoi);
        });
    }

    @Override
    public void hienThiDialogDongCa() {
        Platform.runLater(DoiSoatDongCaViewFXMLController::hienThi);
    }

    // ── Builder UI ────────────────────────────────────────────────────────────

    private HBox taoRowTop5(TopSanPhamDTO sp, int maxBan) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblTen = new Label(sp.tenSP());
        lblTen.setPrefWidth(160);
        lblTen.setMinWidth(160);
        lblTen.setMaxWidth(160);
        lblTen.getStyleClass().add("text-body");
        lblTen.setWrapText(false);

        ProgressBar bar = new ProgressBar((double) sp.tongBan() / maxBan);
        bar.getStyleClass().add("bar-top5");
        bar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bar, Priority.ALWAYS);

        Label lblSo = new Label(String.valueOf(sp.tongBan()));
        lblSo.setPrefWidth(40);
        lblSo.setMinWidth(40);
        lblSo.getStyleClass().add("text-body-bold");
        lblSo.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(lblTen, bar, lblSo);
        return row;
    }
}
