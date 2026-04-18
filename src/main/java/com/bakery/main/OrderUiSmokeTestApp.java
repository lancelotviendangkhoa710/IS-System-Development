package com.bakery.main;

import com.bakery.controllers.OrderController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class OrderUiSmokeTestApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(OrderUiSmokeTestApp.class.getResource("/views/Order.fxml"));
        Parent root = loader.load();

        OrderController controller = loader.getController();
        if (controller == null) {
            throw new IllegalStateException("Khong khoi tao duoc OrderController tu FXML.");
        }

        Scene scene = new Scene(root, 1000, 820);
        stage.setScene(scene);
        stage.setTitle("UI Smoke Test - Order Module");
        stage.show();

        inHuongDanTestManual();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void inHuongDanTestManual() {
        System.out.println("=== UI SMOKE TEST: ORDER MODULE ===");
        System.out.println("1. Kiem tra man hinh mo duoc, khong vang loi FXML.");
        System.out.println("2. Test tao don hop le:");
        System.out.println("   - Chon ngay + gio nhan banh.");
        System.out.println("   - Chon hinh thuc nhan 'Truc tiep' hoac 'Dat hang'.");
        System.out.println("   - Neu 'Dat hang' thi nhap dia chi giao.");
        System.out.println("   - Click card san pham de them vao gio.");
        System.out.println("   - Nhan 'Tao don hang' -> mong doi thong bao xanh.");
        System.out.println("3. Test validate du lieu sai:");
        System.out.println("   - Chon 'Dat hang' va de trong dia chi giao -> mong doi bao do.");
        System.out.println("   - De trong ngay/gio nhan banh -> mong doi bao do.");
        System.out.println("4. Test thanh toan:");
        System.out.println("   - Nhap ma hoa don + tien khach dua nho hon tong tien -> mong doi bi chan.");
        System.out.println("5. Test tab theo doi/cap nhat:");
        System.out.println("   - Nhap ma don, tra cuu -> cac field thong tin la read-only.");
        System.out.println("   - Chon trang thai moi roi luu cap nhat.");
        System.out.println("=== KET THUC HUONG DAN ===");
        System.out.println();
        System.out.println("Danh sach quick test case: " + List.of(
                "UI-01 Load FXML",
                "UI-02 Tao don hop le",
                "UI-03 Bat buoc dia chi giao khi Dat hang",
                "UI-04 Bat buoc ngay gio nhan banh",
                "UI-05 Thanh toan thieu tien",
                "UI-06 Tra cuu don read-only",
                "UI-07 Cap nhat trang thai qua combobox"));
    }
}
