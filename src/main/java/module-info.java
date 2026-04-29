module BakeryManagementSystem {
    requires java.datatransfer;
    requires transitive java.sql; // Sử dụng transitive để clients có thể dùng Connection, ResultSet...
    requires javafx.base;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires jbcrypt;

    opens com.bakery.main to javafx.graphics;
    opens com.bakery.views.controllers to javafx.fxml;
    opens com.bakery.model.dto to javafx.base;

    exports com.bakery.main;
    exports com.bakery.model.dto;
    exports com.bakery.model.enums;
    exports com.bakery.model.dao; // Export DAO để Service ở module khác (nếu có) hoặc reflect có thể dùng
    exports com.bakery.views.interfaces;
    exports com.bakery.presenters;
    exports com.bakery.services;
    exports com.bakery.utils;
}
