module BakeryManagementSystem {
    requires java.datatransfer;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires jbcrypt;
    requires java.desktop;

    opens com.bakery.views to javafx.fxml;
    opens com.bakery.views.controllers to javafx.fxml;

    exports com.bakery.main;
}