module az.hemsoft.terminaljx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.google.gson;
    requires java.sql;
    requires org.mariadb.jdbc;
    requires atlantafx.base;
    requires com.jfoenix;
    requires static lombok;
    requires org.yaml.snakeyaml;
    requires io.javalin;
    requires org.slf4j;

    opens az.hemsoft.terminaljx to javafx.fxml;
    opens az.hemsoft.terminaljx.ui.controller to javafx.fxml;
    opens az.hemsoft.terminaljx.business.restaurant.model to com.google.gson, org.yaml.snakeyaml;
    opens az.hemsoft.terminaljx.business.config to org.yaml.snakeyaml;

    exports az.hemsoft.terminaljx;
    exports az.hemsoft.terminaljx.ui.controller;
    exports az.hemsoft.terminaljx.business.restaurant.model;
    exports az.hemsoft.terminaljx.business.restaurant.api;
    exports az.hemsoft.terminaljx.business.restaurant.service;
    exports az.hemsoft.terminaljx.business.config;
    exports az.hemsoft.terminaljx.ui;
    opens az.hemsoft.terminaljx.ui to javafx.fxml;
}