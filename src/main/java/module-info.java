module com.bakery {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires org.apache.pdfbox;
    requires com.formdev.flatlaf.extras;

    // Mở các package để tài nguyên (ảnh, fxml) có thể được load
    opens com.bakery.views to java.desktop;
    opens com.bakery.main to java.desktop;
    
    exports com.bakery.main;
    exports com.bakery.model.dto;
}
