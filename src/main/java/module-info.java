module com.ppm.gitppm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.eclipse.jgit;
    requires org.slf4j;
    requires java.desktop;

    opens com.ppm.gitppm to javafx.fxml;
    opens com.ppm.gitppm.controller to javafx.fxml;
    exports com.ppm.gitppm;
    exports com.ppm.gitppm.controller;
    exports com.ppm.gitppm.model;
    exports com.ppm.gitppm.service;
}
