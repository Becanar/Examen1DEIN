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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    private TableColumn<Producto, String> clCod;

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
    @FXML
    private CheckBox checkBox;

    private ObservableList<Producto> productosData;
    private Blob blob;
    /**
     * Inicializa el controlador y configura la vista de los productos.
     *
     * @param url      la URL de la ubicación del archivo FXML.
     * @param resourceBundle el paquete de recursos que contiene los datos.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tablaVista.getSelectionModel().clearSelection();
        tablaVista.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Producto productoSeleccionado = newValue;

                txtCodigo.setText(productoSeleccionado.getCodigo());
                txtNombre.setText(productoSeleccionado.getNombre());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));

                if (productoSeleccionado.getImagen() != null) {

                    Blob blob = productoSeleccionado.getImagen();
                    try {
                        InputStream imagenStream = blob.getBinaryStream();
                        img.setImage(new Image(imagenStream));
                    } catch (SQLException e) {
                        System.err.println("Error al convertir Blob a InputStream: " + e.getMessage());
                    }
                } else {
                    img.setImage(null);
                }

                checkBox.setSelected(productoSeleccionado.isDisponible());

                txtCodigo.setDisable(true);
                btActualizar.setDisable(false);
                btCrear.setDisable(true);
            }
        });


        FontIcon iconoAniadir = new FontIcon(FontAwesomeRegular.IMAGE);
        btImagen.setGraphic(iconoAniadir);
        clCod.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getCodigo()));
        colNom.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNombre()));
        colPrec.setCellValueFactory(cellData -> cellData.getValue().getPrecio() == 0 ? null : new ReadOnlyObjectWrapper<>(cellData.getValue().getPrecio()));

        colDisp.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isDisponible()));


        colDisp.setCellFactory(CheckBoxTableCell.forTableColumn(colDisp));

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
    /**
     * Borra un producto de la base de datos después de una confirmación del usuario.
     *
     * @param o objeto que puede ser utilizado para manejar el evento (en este caso no se usa).
     */
    private void borrar(Object o) {
        Producto producto = tablaVista.getSelectionModel().getSelectedItem();

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Estás seguro de que deseas eliminar el producto?");
        alerta.setContentText("El producto con código: " + producto.getCodigo() + " será eliminado.");

        ButtonType respuesta = alerta.showAndWait().orElse(ButtonType.CANCEL);
        if (respuesta == ButtonType.OK) {
            boolean exito = DaoProducto.eliminarProducto(producto);

            if (exito) {
                ArrayList<String> mensajesExito = new ArrayList<>();
                mensajesExito.add("El producto se ha eliminado correctamente.");
                confirmacion("Producto Eliminado", mensajesExito);

                cargarProductos();

                limpiar();

                txtCodigo.setDisable(false);
                btActualizar.setDisable(true);
                btCrear.setDisable(false);
            } else {
                ArrayList<String> textosError = new ArrayList<>();
                textosError.add("Ha ocurrido un error al intentar eliminar el producto.");
                alerta(textosError);
            }
        }

    }

    /**
     * Muestra la imagen del producto seleccionado en una nueva ventana.
     *
     * @param o objeto que puede ser utilizado para manejar el evento (en este caso no se usa).
     */
    private void verImg(Object o) {
        Producto producto = tablaVista.getSelectionModel().getSelectedItem();

        if (producto == null || producto.getImagen() == null) {
            ArrayList<String> mensajes = new ArrayList<>();
            mensajes.add("Este producto no tiene imagen asociada.");
            alerta(mensajes);
            return;
        }

        try {
            Blob blob = producto.getImagen();
            InputStream imagenStream = blob.getBinaryStream();
            Image image = new Image(imagenStream);

            Stage ventanaImagen = new Stage();
            ventanaImagen.setTitle("Ver Imagen");
            ventanaImagen.setResizable(false);

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);
            imageView.setFitHeight(300);

            StackPane root = new StackPane();
            root.getChildren().add(imageView);

            Scene scene = new Scene(root, 300, 300);
            ventanaImagen.setScene(scene);

            ventanaImagen.show();
        } catch (SQLException e) {
            ArrayList<String> mensajesError = new ArrayList<>();
            mensajesError.add("No se ha podido cargar la imagen del producto.");
            alerta(mensajesError);
        }
    }


    /**
     * Carga los productos desde la base de datos y los muestra en la tabla.
     */
    private void cargarProductos() {
        List<Producto> productos = DaoProducto.obtenerProductos();
        productosData = FXCollections.observableArrayList(productos);
        tablaVista.setItems(productosData);
    }

    /**
     * Muestra una ventana de información sobre la aplicación.
     *
     * @param event evento que se dispara al hacer clic en el botón de "Acerca de".
     */
    @FXML
    void acercaDe(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText("Gestión de productos 1.0");
        alert.setContentText("Autor: Beñat Cano");
        alert.showAndWait();
    }

    /**
     * Actualiza un producto en la base de datos.
     *
     * @param event evento que se dispara al hacer clic en el botón de "Actualizar".
     */
    @FXML
    void actualizar(ActionEvent event) {

        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        double precio = Double.parseDouble(txtPrecio.getText());
        boolean disponible = checkBox.isSelected();
        Blob imagen = this.blob;


        Producto producto = new Producto(codigo, nombre, precio, disponible, imagen);


        boolean exito = DaoProducto.modificarProducto(producto);

        if (exito) {

            ArrayList<String> mensajesActualizacion = new ArrayList<>();
            mensajesActualizacion.add("El producto se ha actualizado correctamente.");
            confirmacion("Producto Actualizado", mensajesActualizacion);


            cargarProductos();
            limpiar();
        } else {

            ArrayList<String> textosError = new ArrayList<>();
            textosError.add("Ha ocurrido un error al intentar modificar el producto.");
            alerta(textosError);
        }
    }

    /**
     * Crea un producto en la base de datos.
     *
     * @param event evento que se dispara al hacer clic en el botón de "Crear".
     */
    @FXML
    void crear(ActionEvent event) {
        ArrayList<String> failMessages = new ArrayList<>();


        String codigo = txtCodigo.getText();
        if (codigo.length() != 5) {
            failMessages.add("El código debe tener exactamente 5 caracteres.");
        }


        String nombre = txtNombre.getText();
        if (nombre.isEmpty()) {
            failMessages.add("El nombre es obligatorio.");
        }

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


        if (!failMessages.isEmpty()) {
            alerta(failMessages);
            return;
        }


        Producto nuevoProducto = new Producto(codigo, nombre, Double.parseDouble(precioStr), true, blob);


        boolean success = DaoProducto.guardarProducto(nuevoProducto);

        if (success) {

            ArrayList<String> mensajesActualizacion = new ArrayList<>();
            mensajesActualizacion.add("El producto se ha creado correctamente.");
            confirmacion("Producto Creado", mensajesActualizacion);


            cargarProductos();
            limpiar();
        } else {

            ArrayList<String> dbErrorMessages = new ArrayList<>();
            dbErrorMessages.add("Ha ocurrido un error al guardar el producto en la base de datos.");
            alerta(dbErrorMessages);
        }
    }
    /**
     * Limpia los campos y restablece los botones al inicio de los botones.
     */
    @FXML
    void limpiar() {
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecio.clear();
        img.setImage(null);
        blob = null;
        checkBox.setSelected(false);
        btCrear.setDisable(false);
        btActualizar.setDisable(true);
        txtCodigo.setDisable(false);
        tablaVista.getSelectionModel().clearSelection();
    }

    /**
     * Abre un cuadro de diálogo para seleccionar una imagen desde el sistema de archivos.
     * Si se selecciona una imagen, verifica su tamaño y la muestra en la interfaz.
     * Si la imagen es mayor a 64 KB, muestra un mensaje de error.
     * Además, convierte la imagen seleccionada en un objeto Blob y lo almacena para su posterior uso.
     *
     * @param event El evento que se genera al hacer clic en el botón para seleccionar la imagen.
     */
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
    /**
     * Muestra una confirmacion con los mensajes proporcionados.
     *
     * @param titulo EL titulo de la alerta
     * @param mensajes Los textos de error a mostrar en la alerta.
     */
    public void confirmacion(String titulo, ArrayList<String> mensajes) {
        String contenido = String.join("\n", mensajes);
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(titulo);
        successAlert.setHeaderText(null);
        successAlert.setContentText(contenido);
        successAlert.showAndWait();
    }
}
