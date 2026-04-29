module BakeryManagementSystem {
    requires java.datatransfer;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires jbcrypt;

    // Mở các package để tài nguyên (ảnh, fxml) có thể được load
    opens com.bakery.main to javafx.graphics;
    opens com.bakery.views to javafx.fxml;
    opens com.bakery.views.controllers to javafx.fxml;

    exports com.bakery.main;
    exports com.bakery.model.dto;
}
