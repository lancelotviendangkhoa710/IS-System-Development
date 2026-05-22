/**
 * BakeryManagementSystem — JPMS module descriptor.
 *
 * Khai báo dạng "open module" để cho phép reflection từ JavaFX (FXML) và
 * JasperReports
 * (unnamed legacy JAR) mà không cần liệt kê từng `opens` riêng lẻ.
 */
open module BakeryManagementSystem {
    // ── Java SE ───────────────────────────────────────────────────────────────
    requires java.datatransfer;
    requires transitive java.sql;

    // ── JavaFX ────────────────────────────────────────────────────────────────
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;

    // ── Third-party named modules ─────────────────────────────────────────────
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires jbcrypt;
    requires jakarta.mail;
    requires jasperreports;

    // ── Exports ───────────────────────────────────────────────────────────────
    exports com.bakery.main;
    exports com.bakery.model.dto;
    exports com.bakery.model.dto.banhang;
    exports com.bakery.model.dto.baocao;
    exports com.bakery.model.dto.hethong;
    exports com.bakery.model.dto.khachhang;
    exports com.bakery.model.dto.kho;
    exports com.bakery.model.dto.nhansu;
    exports com.bakery.model.dto.taichinh;
    exports com.bakery.model.enums;
    exports com.bakery.model.dao;
    exports com.bakery.model.dao.banhang;
    exports com.bakery.model.dao.baocao;
    exports com.bakery.model.dao.hethong;
    exports com.bakery.model.dao.khachhang;
    exports com.bakery.model.dao.kho;
    exports com.bakery.model.dao.nhansu;
    exports com.bakery.model.dao.taichinh;
    exports com.bakery.views.interfaces;
    exports com.bakery.views.interfaces.banhang;
    exports com.bakery.views.interfaces.baocao;
    exports com.bakery.views.interfaces.hethong;
    exports com.bakery.views.interfaces.khachhang;
    exports com.bakery.views.interfaces.kho;
    exports com.bakery.views.interfaces.nhansu;
    exports com.bakery.views.controllers;
    exports com.bakery.views.controllers.banhang;
    exports com.bakery.views.controllers.baocao;
    exports com.bakery.views.controllers.hethong;
    exports com.bakery.views.controllers.khachhang;
    exports com.bakery.views.controllers.kho;
    exports com.bakery.views.controllers.bep;
    exports com.bakery.views.controllers.nhansu;
    exports com.bakery.views.controllers.taichinh;
    exports com.bakery.presenters;
    exports com.bakery.presenters.banhang;
    exports com.bakery.presenters.baocao;
    exports com.bakery.presenters.hethong;
    exports com.bakery.presenters.khachhang;
    exports com.bakery.presenters.kho;
    exports com.bakery.presenters.nhansu;
    exports com.bakery.services;
    exports com.bakery.services.banhang;
    exports com.bakery.services.baocao;
    exports com.bakery.services.hethong;
    exports com.bakery.services.khachhang;
    exports com.bakery.services.kho;
    exports com.bakery.services.nhansu;
    exports com.bakery.services.taichinh;
    exports com.bakery.utils;
}
