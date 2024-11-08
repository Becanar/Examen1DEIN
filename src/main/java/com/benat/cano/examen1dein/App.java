package com.benat.cano.examen1dein;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    /**
     * Metodo start, que inicializa y muestra la ventana principal de la aplicación.
     *
     * @param stage El escenario principal en el que se mostrará la interfaz.
     * @throws IOException Si hay un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/benat/cano/examen1dein/fxml/Productos.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinWidth(700);
        stage.setMinHeight(750);
        stage.setMaxWidth(1100);
        stage.setMaxHeight(800);
        try {
            Image img = new Image(getClass().getResource("/com/benat/cano/examen1dein/images/carrito.png").toString());
            stage.getIcons().add(img);
        } catch (Exception e) {
            System.out.println("Error al cargar la imagen: " + e.getMessage());
        }
        scene.getStylesheets().add(getClass().getResource("/com/benat/cano/examen1dein/estilo/style.css").toExternalForm());
        stage.setTitle("PRODUCTOS");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Metodo main que lanza la aplicación.a
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }
}