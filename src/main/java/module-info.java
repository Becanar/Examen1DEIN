module com.benat.cano.examen1dein {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.benat.cano.examen1dein to javafx.fxml;
    exports com.benat.cano.examen1dein;
}