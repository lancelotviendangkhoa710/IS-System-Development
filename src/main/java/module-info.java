module com.bakery {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    
    opens com.bakery.controllers to javafx.fxml;
    exports com.bakery.main;
}
