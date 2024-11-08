module com.benat.cano.examen1dein {
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires javafx.controls;


    opens com.benat.cano.examen1dein to javafx.fxml;
    exports com.benat.cano.examen1dein;
    exports com.benat.cano.examen1dein.controllers;
    opens com.benat.cano.examen1dein.controllers to javafx.fxml;
}