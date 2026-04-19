module com.bakery {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires java.sql;
    requires java.desktop;
    opens com.bakery.controllers to javafx.fxml;
    exports com.bakery.main;
}
