package com.benat.cano.examen1dein.dao;

import com.benat.cano.examen1dein.db.ConectorDB;
import com.benat.cano.examen1dein.model.Producto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de realizar operaciones de acceso a la base de datos
 * relacionadas con los productos, como obtener, guardar, modificar y eliminar productos.
 */
public class DaoProducto {

    /**
     * Obtiene la lista de productos desde la base de datos.
     *
     * @return Una lista de objetos {@link Producto} que contienen los datos de los productos.
     */
    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT codigo, nombre, precio, disponible, imagen FROM productos";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                boolean disponible = rs.getInt("disponible") == 1;
                Blob imagen = rs.getBlob("imagen");

                Producto producto = new Producto(codigo, nombre, precio, disponible, imagen);
                productos.add(producto);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        conn.closeConexion();
        return productos;
    }

    /**
     * Convierte un archivo de imagen a un objeto {@link Blob} para almacenarlo en la base de datos.
     *
     * @param file El archivo de imagen a convertir.
     * @return El objeto {@link Blob} que representa la imagen.
     * @throws SQLException Si ocurre un error al crear el Blob.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        ConectorDB connection = new ConectorDB();
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            Blob blob = conn.createBlob();
            byte[] buffer = new byte[1024];
            int bytesRead;

            try (var outputStream = blob.setBinaryStream(1)) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return blob;
        }
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     *
     * @param producto El producto que se va a guardar.
     * @return {@code true} si el producto se guardó correctamente, {@code false} si hubo un error.
     */
    public static boolean guardarProducto(Producto producto) {
        String consulta = "INSERT INTO productos (codigo, nombre, precio, disponible, imagen) VALUES (?, ?, ?, ?, ?)";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.isDisponible() ? 1 : 0);

            if (producto.getImagen() != null) {
                stmt.setBlob(5, producto.getImagen());
            } else {
                stmt.setNull(5, Types.BLOB);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar el producto: " + e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifica un producto existente en la base de datos.
     *
     * @param producto El producto con los nuevos datos.
     * @return {@code true} si el producto se actualizó correctamente, {@code false} si hubo un error.
     */
    public static boolean modificarProducto(Producto producto) {
        String consulta = "UPDATE productos SET nombre = ?, precio = ?, disponible = ?, imagen = ? WHERE codigo = ?";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);

            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.isDisponible() ? 1 : 0);

            if (producto.getImagen() != null) {
                stmt.setBlob(4, producto.getImagen());
            } else {
                stmt.setNull(4, Types.BLOB);
            }

            stmt.setString(5, producto.getCodigo());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | FileNotFoundException e) {
            System.err.println("Error al modificar el producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto de la base de datos.
     *
     * @param producto El producto que se va a eliminar.
     * @return {@code true} si el producto se eliminó correctamente, {@code false} si hubo un error.
     */
    public static boolean eliminarProducto(Producto producto) {
        String consulta = "DELETE FROM productos WHERE codigo = ?";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);

            stmt.setString(1, producto.getCodigo());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | FileNotFoundException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }
}
