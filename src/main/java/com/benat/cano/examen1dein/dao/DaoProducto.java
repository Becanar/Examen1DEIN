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

}
