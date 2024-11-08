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

                // Establecer el estado del CheckBox (disponible o no)
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
        // Verificamos si el objeto recibido es una instancia de Producto
        Producto producto = tablaVista.getSelectionModel().getSelectedItem();

        // Mostrar alerta de confirmación
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Estás seguro de que deseas eliminar el producto?");
        alerta.setContentText("El producto con código: " + producto.getCodigo() + " será eliminado.");

        // Si el usuario hace clic en "OK" (confirmar)
        ButtonType respuesta = alerta.showAndWait().orElse(ButtonType.CANCEL);
        if (respuesta == ButtonType.OK) {
            // Llamamos al método para eliminar el producto de la base de datos
            boolean exito = DaoProducto.eliminarProducto(producto);

            if (exito) {
                // Si la eliminación fue exitosa, mostramos una alerta de éxito
                ArrayList<String> mensajesExito = new ArrayList<>();
                mensajesExito.add("El producto se ha eliminado correctamente.");
                confirmacion("Producto Eliminado", mensajesExito);

                // Recargar la tabla de productos
                cargarProductos();

                // Limpiar el formulario
                limpiar();

                // Restaurar el estado de los botones
                txtCodigo.setDisable(false);
                btActualizar.setDisable(true);
                btCrear.setDisable(false);
            } else {
                // Si ocurrió un error, mostramos una alerta de error
                ArrayList<String> textosError = new ArrayList<>();
                textosError.add("Ha ocurrido un error al intentar eliminar el producto.");
                alerta(textosError);
            }
        }

    }


    private void verImg(Object o) {
        // Obtener el producto seleccionado de la tabla
        Producto producto = tablaVista.getSelectionModel().getSelectedItem();

        if (producto == null || producto.getImagen() == null) {
            // Si no hay imagen, mostramos una alerta
            ArrayList<String> mensajes = new ArrayList<>();
            mensajes.add("Este producto no tiene imagen asociada.");
            alerta(mensajes);  // Usar el método alerta para mostrar el mensaje
            return;
        }

        // Si existe una imagen, creamos una ventana modal para mostrarla
        try {
            // Convertir el Blob de la imagen en un InputStream
            Blob blob = producto.getImagen();
            InputStream imagenStream = blob.getBinaryStream();
            Image image = new Image(imagenStream);

            // Crear una nueva ventana para mostrar la imagen
            Stage ventanaImagen = new Stage();
            ventanaImagen.setTitle("Ver Imagen");
            ventanaImagen.setResizable(false);  // Evitar que la ventana se redimensione

            // Crear un ImageView para mostrar la imagen
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(300);  // Establecer tamaño de la imagen
            imageView.setFitHeight(300);

            // Crear un layout para agregar el ImageView
            StackPane root = new StackPane();
            root.getChildren().add(imageView);

            // Crear la escena y asignarla a la ventana
            Scene scene = new Scene(root, 300, 300);
            ventanaImagen.setScene(scene);

            // Mostrar la ventana modal
            ventanaImagen.show();
        } catch (SQLException e) {
            // Si ocurre un error al obtener la imagen, mostrar una alerta de error
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
        // Obtener los datos del formulario (asumiendo que ya están en los campos de texto)
        String codigo = txtCodigo.getText();
        String nombre = txtNombre.getText();
        double precio = Double.parseDouble(txtPrecio.getText());
        boolean disponible = checkBox.isSelected(); // Se asume que hay un CheckBox llamado cbDisponible
        Blob imagen = this.blob; // Asumimos que ya tienes la imagen cargada

        // Crear el objeto Producto con los datos del formulario
        Producto producto = new Producto(codigo, nombre, precio, disponible, imagen);

        // Llamar al metodo modificarProducto para actualizar en la base de datos
        boolean exito = DaoProducto.modificarProducto(producto);

        // Si la actualización fue exitosa
        if (exito) {
            // Crear lista de textos para la alerta de éxito
            ArrayList<String> mensajesActualizacion = new ArrayList<>();
            mensajesActualizacion.add("El producto se ha actualizado correctamente.");
            confirmacion("Producto Actualizado", mensajesActualizacion);


            cargarProductos(); // Recargar los productos en la tabla
            limpiar(); // Limpiar los campos
        } else {
            // Si ocurrió un error, crear lista de textos para la alerta de error
            ArrayList<String> textosError = new ArrayList<>();
            textosError.add("Ha ocurrido un error al intentar modificar el producto.");
            alerta(textosError);
        }
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
            ArrayList<String> mensajesActualizacion = new ArrayList<>();
            mensajesActualizacion.add("El producto se ha creado correctamente.");
            confirmacion("Producto Creado", mensajesActualizacion);


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
        btCrear.setDisable(false);
        btActualizar.setDisable(true);
        txtCodigo.setDisable(false);
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

    public void confirmacion(String titulo, ArrayList<String> mensajes) {
        String contenido = String.join("\n", mensajes);  // Unir todos los mensajes en un solo string
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle(titulo);
        successAlert.setHeaderText(null);
        successAlert.setContentText(contenido);
        successAlert.showAndWait();
    }
}
