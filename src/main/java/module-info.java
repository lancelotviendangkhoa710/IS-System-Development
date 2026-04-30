module BakeryManagementSystem {
    requires java.datatransfer;
    requires transitive java.sql;
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires jbcrypt;

    // Mở gói cho JavaFX reflection (FXML binding, Platform.runLater, Scene graph)
    opens com.bakery.main to javafx.graphics;
    opens com.bakery.views.controllers to javafx.fxml, javafx.graphics, javafx.controls;
    opens com.bakery.model.dto to javafx.base, javafx.fxml;
    opens com.bakery.presenters to javafx.fxml, javafx.graphics;
    opens com.bakery.utils to jbcrypt;

    exports com.bakery.main;
    exports com.bakery.model.dto;
    exports com.bakery.model.enums;
    exports com.bakery.model.dao;
    exports com.bakery.views.interfaces;
    exports com.bakery.views.controllers;
    exports com.bakery.presenters;
    exports com.bakery.services;
    exports com.bakery.utils;
}
