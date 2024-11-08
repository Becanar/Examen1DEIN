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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {

    @FXML
    private TableView<Producto> tablaVista;

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
        // Lógica para seleccionar y cargar una imagen para el producto
    }
}
