package com.bakery.views.controllers.banhang;

import com.bakery.main.App;
import com.bakery.model.dto.nhansu.NhanVienDTO;
import com.bakery.services.nhansu.XacThucService;
import com.bakery.utils.DialogHelper;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import com.bakery.views.controllers.nhansu.DangNhapViewFXMLController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ThuNganViewFXMLController {
    private final XacThucService xacThucService = new XacThucService();

    @FXML private Label lblCashierName;
    @FXML private Label lblShiftStatus;
    @FXML private Label lblHeaderTitle;
    @FXML private Label lblClock;

    @FXML private Button btnMenuOverview;
    @FXML private Button btnMenuPOS;
    @FXML private Button btnMenuCustomOrders;
    @FXML private Button btnMenuCustomers;
    @FXML private Button btnMenuHistory;

    @FXML private VBox paneOverview;
    @FXML private VBox panePlaceholder;
    @FXML private Label lblPlaceholderMessage;

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTotalInvoices;
    @FXML private Label lblTotalCustomOrders;

    private Timeline clockTimeline;

    @FXML
    private void initialize() {
        startClock();
        loadSessionInfo(UserSession.getCurrentUser());
        handleNavOverview();
    }

    public void khoiTaoDashboard(NhanVienDTO nhanVien) {
        UserSession.setCurrentUser(nhanVien);
        loadSessionInfo(nhanVien);
        handleNavOverview();
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        clockTimeline = new Timeline(new KeyFrame(Duration.ZERO, e ->
                lblClock.setText(LocalDateTime.now().format(formatter))), new KeyFrame(Duration.seconds(1)));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void loadSessionInfo(NhanVienDTO nhanVien) {
        SessionContext.AuthSession session = xacThucService.layPhienHienTai();
        if (session == null) {
            return;
        }
        String hoTen = nhanVien != null && nhanVien.getHoTen() != null ? nhanVien.getHoTen() : session.getHoTen();
        lblCashierName.setText(hoTen);
        String maCa = SessionContext.getInstance().isCaoDangMo()
                ? "CA" + SessionContext.getInstance().getMaCa()
                : "Chua mo ca";
        lblShiftStatus.setText("Ca lam viec: " + maCa);
    }

    private void resetMenuStyles() {
        List<Button> buttons = Arrays.asList(btnMenuOverview, btnMenuPOS, btnMenuCustomOrders, btnMenuCustomers, btnMenuHistory);
        for (Button btn : buttons) {
            btn.getStyleClass().remove("active");
        }
    }

    @FXML
    private void handleNavOverview() {
        resetMenuStyles();
        btnMenuOverview.getStyleClass().add("active");
        lblHeaderTitle.setText("Tong quan giao dich");

        paneOverview.setVisible(true);
        paneOverview.setManaged(true);
        panePlaceholder.setVisible(false);
        panePlaceholder.setManaged(false);

        lblTotalRevenue.setText("0 đ");
        lblTotalInvoices.setText("0");
        lblTotalCustomOrders.setText("0");
    }

    @FXML
    private void handleNavPOS() {
        moScene(btnMenuPOS, "/fxml/banhang/DonHangView.fxml", "H3K Bakery - Ban hang POS", 1280, 720);
    }

    @FXML
    private void handleNavCustomOrders() {
        moScene(btnMenuCustomOrders, "/fxml/banhang/TheoDoiDonHangView.fxml", "H3K Bakery - Theo doi don hang", 1366, 768);
    }

    @FXML
    private void handleNavCustomers() {
        moScene(btnMenuCustomers, "/fxml/khachhang/KhachHangView.fxml", "H3K Bakery - Khach hang", 1280, 720);
    }

    @FXML
    private void handleNavHistory() {
        moScene(btnMenuHistory, "/fxml/baocao/BaoCaoView.fxml", "H3K Bakery - Bao cao giao dich", 1280, 720);
    }

    private void navigateToModule(Button activeBtn, String title, String placeholderMsg) {
        resetMenuStyles();
        activeBtn.getStyleClass().add("active");
        lblHeaderTitle.setText(title);

        paneOverview.setVisible(false);
        paneOverview.setManaged(false);
        panePlaceholder.setVisible(true);
        panePlaceholder.setManaged(true);
        lblPlaceholderMessage.setText(placeholderMsg);
    }

    @FXML
    private void handleShiftReconciliation() {
        showAlert(Alert.AlertType.INFORMATION, "Doi soat", "Mo nghiep vu doi soat cuoi ca theo UC17.");
    }

    @FXML
    private void handleChangePassword() {
        navigateToModule(btnMenuOverview, "Doi mat khau", "Chuc nang doi mat khau se duoc bo sung o phase tiep theo.");
    }

    @FXML
    private void handleLogout() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        xacThucService.dangXuat();
        UserSession.clear();
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(App.LOGIN_VIEW));
            Parent root = loader.load();
            DangNhapViewFXMLController controller = loader.getController();
            controller.setLoginInfo("Da dang xuat tai khoan Thu ngan.");

            Stage stage = (Stage) lblCashierName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Loi dang xuat", ex.getMessage());
        }
    }

    private void moScene(Node source, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            java.net.URL cssUrl = App.class.getResource("/css/bakery.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) source.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Loi dieu huong", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            DialogHelper.applyBakeryTheme(alert);
            alert.showAndWait();
        });
    }
}
