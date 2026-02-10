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
    requires transitive java.sql;
    requires org.mariadb.jdbc;
    requires atlantafx.base;
    requires com.jfoenix;
    requires static lombok;
    requires org.yaml.snakeyaml;
    requires transitive io.javalin;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires net.bytebuddy;
    requires jdk.jdi;
    requires com.sun.jna.platform;
    requires jakarta.persistence;
    requires org.checkerframework.checker.qual;
    requires annotations;

    opens az.hemsoft.terminaljx to javafx.fxml;
    opens az.hemsoft.terminaljx.ui.controller to javafx.fxml;
    opens az.hemsoft.terminaljx.business.core.annotation
            to com.google.gson, org.yaml.snakeyaml, com.fasterxml.jackson.databind;
    opens az.hemsoft.terminaljx.business.core.repository to com.google.gson, com.fasterxml.jackson.databind;
    opens az.hemsoft.terminaljx.business.core.mapper to com.google.gson, com.fasterxml.jackson.databind;
    opens az.hemsoft.terminaljx.business.core.schema to com.google.gson, com.fasterxml.jackson.databind;
    opens az.hemsoft.terminaljx.api to io.javalin, com.fasterxml.jackson.databind;
    opens az.hemsoft.terminaljx.business.warehouse.model to com.fasterxml.jackson.databind, com.google.gson;
    opens az.hemsoft.terminaljx.business.warehouse.controller to com.fasterxml.jackson.databind, com.google.gson;

    exports az.hemsoft.terminaljx;
    exports az.hemsoft.terminaljx.ui.controller;
    exports az.hemsoft.terminaljx.api;
    exports az.hemsoft.terminaljx.ui;
    exports az.hemsoft.terminaljx.business.core.annotation;
    exports az.hemsoft.terminaljx.business.core.repository;
    exports az.hemsoft.terminaljx.business.core.mapper;
    exports az.hemsoft.terminaljx.business.core.schema;
    exports az.hemsoft.terminaljx.business.core.service;

    exports az.hemsoft.terminaljx.business.warehouse.model;
    exports az.hemsoft.terminaljx.business.warehouse.service;
    exports az.hemsoft.terminaljx.business.warehouse.controller;

    opens az.hemsoft.terminaljx.ui to javafx.fxml;

    exports az.hemsoft.terminaljx.config;

    opens az.hemsoft.terminaljx.config to com.fasterxml.jackson.databind, com.google.gson, org.yaml.snakeyaml;

    exports az.hemsoft.terminaljx.business.user;

    opens az.hemsoft.terminaljx.business.user to com.fasterxml.jackson.databind, com.google.gson, org.yaml.snakeyaml;
}