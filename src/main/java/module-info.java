module com.bakery {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;


    // Mở các package để tài nguyên (ảnh, fxml) có thể được load
    opens com.bakery.main to javafx.graphics;
    opens com.bakery.views.controllers to javafx.fxml;
    
    exports com.bakery.main;
    exports com.bakery.model.dto;
}
