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
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;

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
    @FXML
    private CheckBox checkBox;

    private ObservableList<Producto> productosData;
    private Blob blob;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tablaVista.getSelectionModel().clearSelection();
        tablaVista.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Cargar los datos del producto en el formulario
                Producto productoSeleccionado = newValue;

                // Asignar los valores al formulario
                txtCodigo.setText(productoSeleccionado.getCodigo());
                txtNombre.setText(productoSeleccionado.getNombre());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));

                // Asignar la imagen al ImageView
                if (productoSeleccionado.getImagen() != null) {
                    img.setImage(new Image("file:" + productoSeleccionado.getImagen()));
                } else {
                    img.setImage(null);
                }

                // Establecer el estado del CheckBox (disponible o no)
                // Aquí se asume que hay un método en el Producto que indica si está disponible
                checkBox.setSelected(productoSeleccionado.isDisponible());

                // Deshabilitar el campo código y habilitar el botón Actualizar
                txtCodigo.setDisable(true);
                btActualizar.setDisable(false);
                btCrear.setDisable(true);
            }
        });

        FontIcon iconoAniadir = new FontIcon(FontAwesomeRegular.IMAGE);
        btImagen.setGraphic(iconoAniadir);
        // Inicializa las columnas de la tabla
        clCod.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCodigo()));
        colNom.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNombre()));
        colPrec.setCellValueFactory(cellData -> cellData.getValue().getPrecio() == 0 ? null : new ReadOnlyObjectWrapper<>(cellData.getValue().getPrecio()));

        colDisp.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponible()));


        // Usamos CheckBoxTableCell para mostrar el CheckBox en cada fila
        colDisp.setCellFactory(CheckBoxTableCell.forTableColumn(colDisp));

        // Cargar datos desde la base de datos
        cargarProductos();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Ver imagen");
        editItem.setOnAction(event -> verImg(null));


        MenuItem deleteItem = new MenuItem("Eliminar");
        deleteItem.setOnAction(event -> borrar(null));

        contextMenu.getItems().addAll(editItem, deleteItem);

        tablaVista.setContextMenu(contextMenu);
        btActualizar.setDisable(true);
    }

    private void borrar(Object o) {
    }

    private void verImg(Object o) {
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
        ArrayList<String> failMessages = new ArrayList<>();

        // Validación de código: debe tener exactamente 5 caracteres
        String codigo = txtCodigo.getText();
        if (codigo.length() != 5) {
            failMessages.add("El código debe tener exactamente 5 caracteres.");
        }

        // Validación de nombre: campo obligatorio
        String nombre = txtNombre.getText();
        if (nombre.isEmpty()) {
            failMessages.add("El nombre es obligatorio.");
        }

        // Validación de precio: debe ser un número decimal y no vacío
        String precioStr = txtPrecio.getText();
        if (precioStr.isEmpty()) {
            failMessages.add("El precio es obligatorio.");
        } else {
            try {
                Double precio = Double.parseDouble(precioStr);
                if (precio <= 0) {
                    failMessages.add("El precio debe ser un número mayor que 0.");
                }
            } catch (NumberFormatException e) {
                failMessages.add("El precio debe ser un número decimal válido.");
            }
        }

        // Si hay errores, mostrar la alerta de error y no continuar
        if (!failMessages.isEmpty()) {
            alerta(failMessages);
            return;
        }

        // Si las validaciones son correctas, proceder a guardar el producto
        // Crear el nuevo producto
        Producto nuevoProducto = new Producto(codigo, nombre, Double.parseDouble(precioStr), true, blob);

        // Llamar al DAO para guardar el producto en la base de datos
        boolean success = DaoProducto.guardarProducto(nuevoProducto);

        if (success) {
            // Informar al usuario que todo ha ido bien
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Producto Creado");
            successAlert.setHeaderText(null);
            successAlert.setContentText("El producto se ha creado correctamente.");
            successAlert.showAndWait();

            // Volver a cargar los productos y limpiar los campos
            cargarProductos();
            limpiar();
        } else {
            // Error genérico al guardar el producto
            ArrayList<String> dbErrorMessages = new ArrayList<>();
            dbErrorMessages.add("Ha ocurrido un error al guardar el producto en la base de datos.");
            alerta(dbErrorMessages);
        }
    }
    @FXML
    void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        img.setImage(null); // Limpiar la imagen seleccionada
        blob = null; // Limpiar la imagen en Blob
        checkBox.setSelected(false);
        tablaVista.getSelectionModel().clearSelection();
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
