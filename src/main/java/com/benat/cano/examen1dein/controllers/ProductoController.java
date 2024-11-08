package com.benat.cano.examen1dein.controllers;

import com.benat.cano.examen1dein.dao.DaoProducto;
import com.benat.cano.examen1dein.model.Producto;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {

    @FXML
    private TableView<Producto> tablaVista;
    @FXML
    private ImageView img;

    @FXML
    private TableColumn<Producto, String> clCod; // Columna de Código

    @FXML
    private TableColumn<Producto, String> colNom; // Columna de Nombre

    @FXML
    private TableColumn<Producto, Double> colPrec; // Columna de Precio

    @FXML
    private TableColumn<Producto, Boolean> colDisp; // Columna de Disponible (con CheckBox)

    @FXML
    private TextField txtCodigo, txtNombre, txtPrecio;

    @FXML
    private Button btCrear, btActualizar, btLimpiar, btImagen;

    private ObservableList<Producto> productosData;
    private Blob blob;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa las columnas de la tabla
        clCod.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCodigo()));
        colNom.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNombre()));
        colPrec.setCellValueFactory(cellData -> cellData.getValue().getPrecio() == 0 ? null : new ReadOnlyObjectWrapper<>(cellData.getValue().getPrecio()));

        colDisp.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponible()));


        // Usamos CheckBoxTableCell para mostrar el CheckBox en cada fila
        colDisp.setCellFactory(CheckBoxTableCell.forTableColumn(colDisp));

        // Cargar datos desde la base de datos
        cargarProductos();
        btActualizar.setDisable(true);
    }

    /**
     * Carga los productos desde la base de datos y los muestra en la tabla.
     */
    private void cargarProductos() {
        List<Producto> productos = DaoProducto.obtenerProductos();
        productosData = FXCollections.observableArrayList(productos);
        tablaVista.setItems(productosData);
    }


    @FXML
    void acercaDe(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Gestión de productos 1.0");
        alert.setContentText("Autor: Beñat Cano");
        alert.showAndWait();
    }


    @FXML
    void actualizar(ActionEvent event) {
        // Lógica para actualizar un producto existente
    }

    @FXML
    void crear(ActionEvent event) {
        // Lógica para crear un nuevo producto
    }

    @FXML
    void limpiar(ActionEvent event) {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
    }

    @FXML
    void seleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona una imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(null);
        try {
            double kbs = (double) file.length() / 1024;
            if (kbs > 64) {
                ArrayList<String> failMessages = new ArrayList<>();
                failMessages.add("La imagen es demasiado grande.");
                alerta(failMessages);
            } else {
                InputStream imagen = new FileInputStream(file);
                Blob blob = DaoProducto.convertFileToBlob(file);
                this.blob = blob;
                img.setImage(new Image(imagen));
            }
        } catch (IOException | NullPointerException e) {
            System.out.println("Imagen no seleccionada");
        } catch (SQLException e) {
            ArrayList<String> failMessages = new ArrayList<>();
            failMessages.add("No se ha podido seleccionar la imagen.");
            alerta(failMessages);
        }
    }
    /**
     * Muestra una alerta con los mensajes de error proporcionados.
     *
     * @param textos Los textos de error a mostrar en la alerta.
     */
    public void alerta(ArrayList<String> textos) {
        String contenido = String.join("\n", textos);
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setTitle("Error");
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
