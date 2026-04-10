module com.bakery {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires com.github.librepdf.openpdf;
    requires com.oracle.database.jdbc;

    opens com.bakery.controllers to javafx.fxml;
    exports com.bakery.main;
}
