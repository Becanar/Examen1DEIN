package com.benat.cano.examen1dein;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
/**
 * Clase principal que lanza la aplicacion de gestion de productos.
 *
 * Esta clase extiende de {@link Application} y se encarga de cargar la interfaz de usuario,
 * establecer la configuracion inicial del escenario y aplicar los estilos necesarios.
 */
public class App extends Application {
    /**
     * Metodo que se ejecuta al iniciar la aplicacion.
     * Carga el archivo FXML, establece las dimensiones del escenario,
     * agrega un icono al escenario y aplica el archivo CSS de estilos.
     *
     * @param stage El escenario principal de la aplicacion.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
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
     * Metodo principal que lanza la aplicacion.
     *
     * @param args Los argumentos de la aplicacion.
     */
    public static void main(String[] args) {
        launch();
    }
}