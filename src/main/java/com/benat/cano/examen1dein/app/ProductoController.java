package com.benat.cano.examen1dein.app;

import com.benat.cano.examen1dein.dao.DaoProducto;
import com.benat.cano.examen1dein.model.Producto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductoController implements Initializable {

    @FXML
    private TableView<Producto> tablaVista;

    @FXML
    private TableColumn<Producto, Integer> clCod;

    @FXML
    private TableColumn<Producto, String> colNom;

    @FXML
    private TableColumn<Producto, Double> colPrec;

    @FXML
    private TableColumn<Producto, Boolean> colDisp;

    @FXML
    private TextField txtCodigo, txtNombre, txtPrecio;

    @FXML
    private Button btCrear, btActualizar, btLimpiar, btImagen;

    private ObservableList<Producto> productosData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializa las columnas de la tabla
        clCod.setCellValueFactory(cellData -> cellData.getValue().codigoProperty().asObject());
        colNom.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colPrec.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        colDisp.setCellValueFactory(cellData -> cellData.getValue().disponibleProperty());

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
        // Lógica para mostrar la información acerca de
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
