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

public class DaoProducto {


    /**
     * Obtiene una lista de productos desde la base de datos.
     *
     * @return Lista de productos.
     */
    public static List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        String query = "SELECT codigo, nombre, precio, disponible, imagen FROM productos";
        ConectorDB conn;
        try {
            conn=new ConectorDB();
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
     * Convierte un archivo de tipo File a un objeto Blob para ser almacenado en la base de datos.
     *
     * @param file el archivo a convertir a Blob
     * @return el Blob generado a partir del archivo
     * @throws SQLException en caso de errores al trabajar con la base de datos
     * @throws IOException  en caso de errores al leer el archivo
     */
    public static Blob convertFileToBlob(File file) throws SQLException, IOException {
        ConectorDB connection = new ConectorDB();
        // Open a connection to the database
        try (Connection conn = connection.getConnection();
             FileInputStream inputStream = new FileInputStream(file)) {

            // Create Blob
            Blob blob = conn.createBlob();
            // Write the file's bytes to the Blob
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

    public static boolean guardarProducto(Producto producto) {
        String consulta = "INSERT INTO productos (codigo, nombre, precio, disponible,imagen) VALUES (?, ?, ?, ?, ?)";
        ConectorDB conn;
        try {
            conn=new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);
            stmt.setString(1, producto.getCodigo());
            stmt.setString(2, producto.getNombre());
            stmt.setDouble(3, producto.getPrecio());
            stmt.setInt(4, producto.isDisponible() ? 1 : 0);

            // Guardar la imagen como Blob
            if (producto.getImagen() != null) {
                stmt.setBlob(5, producto.getImagen());
            } else {
                stmt.setNull(5, Types.BLOB);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Si la inserción fue exitosa, se devuelven más de 0 filas afectadas
        } catch (SQLException e) {
            System.err.println("Error al guardar el producto: " + e.getMessage());
            return false;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean modificarProducto(Producto producto) {
        String consulta = "UPDATE productos SET nombre = ?, precio = ?, disponible = ?, imagen = ? WHERE codigo = ?";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);

            // Establecer los valores de los parámetros en la consulta
            stmt.setString(1, producto.getNombre());
            stmt.setDouble(2, producto.getPrecio());
            stmt.setInt(3, producto.isDisponible() ? 1 : 0);

            // Guardar la imagen como Blob (si existe)
            if (producto.getImagen() != null) {
                stmt.setBlob(4, producto.getImagen());
            } else {
                stmt.setNull(4, Types.BLOB); // Si no hay imagen, establecemos NULL en la columna de imagen
            }

            stmt.setString(5, producto.getCodigo()); // El código se usa para identificar el producto a modificar

            // Ejecutar la consulta
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Si la actualización fue exitosa, devuelve true
        } catch (SQLException | FileNotFoundException e) {
            System.err.println("Error al modificar el producto: " + e.getMessage());
            return false; // Si hay un error, devolvemos false
        }
    }

    public static boolean eliminarProducto(Producto producto) {
        String consulta = "DELETE FROM productos WHERE codigo = ?";
        ConectorDB conn;
        try {
            conn = new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(consulta);

            // Establecer el valor del parámetro (código del producto a eliminar)
            stmt.setString(1, producto.getCodigo());

            // Ejecutar la consulta
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0; // Si la eliminación fue exitosa, devuelve true
        } catch (SQLException | FileNotFoundException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            return false; // Si ocurre un error, devolvemos false
        }
    }


}
