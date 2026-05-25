package com.bakery.views.controllers.baocao;

import com.bakery.model.dto.baocao.BangDieuKhienKPIDTO;
import com.bakery.model.dto.baocao.TopSanPhamDTO;
import com.bakery.presenters.baocao.BangDieuKhienPresenter;
import com.bakery.services.baocao.BangDieuKhienService;
import com.bakery.utils.CurrencyFormatter;
import com.bakery.views.controllers.hethong.DoiSoatDongCaViewFXMLController;
import com.bakery.views.interfaces.baocao.IBangDieuKhienView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
            int rank = 0;
            for (TopSanPhamDTO sp : ds) {
                vboxTop5.getChildren().add(taoRowTop5(sp, maxBan, rank));
                rank++;
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

    private static final String[] MEDALS = {"🥇", "🥈", "🥉", "4️⃣", "5️⃣"};

    private HBox taoRowTop5(TopSanPhamDTO sp, int maxBan, int rank) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.getStyleClass().add(rank == 0 ? "top5-row-gold" : "top5-row");

        // Rank emoji (🥇 🥈 🥉 4️⃣ 5️⃣)
        Label lblRank = new Label(rank < MEDALS.length ? MEDALS[rank] : (rank + 1) + ".");
        lblRank.getStyleClass().add("top5-rank");

        Label lblTen = new Label(sp.tenSP());
        lblTen.setPrefWidth(150);
        lblTen.setMinWidth(100);
        lblTen.setMaxWidth(150);
        lblTen.getStyleClass().add("text-body");
        lblTen.setWrapText(false);

        ProgressBar bar = new ProgressBar((double) sp.tongBan() / maxBan);
        bar.getStyleClass().add("bar-top5");
        bar.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(bar, Priority.ALWAYS);

        Label lblSo = new Label(String.valueOf(sp.tongBan()));
        lblSo.setPrefWidth(36);
        lblSo.setMinWidth(36);
        lblSo.getStyleClass().add("text-body-bold");
        lblSo.setAlignment(Pos.CENTER_RIGHT);

        row.getChildren().addAll(lblRank, lblTen, bar, lblSo);
        return row;
    }
}
