package com.benat.cano.examen1dein.dao;

import com.benat.cano.examen1dein.db.ConectorDB;
import com.benat.cano.examen1dein.model.Producto;

import java.io.FileNotFoundException;
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
        String query = "SELECT codigo, nombre, precio, disponible, imagen FROM Producto";
        ConectorDB conn;
        try {
            conn=new ConectorDB();
            PreparedStatement stmt = conn.getConnection().prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int codigo = rs.getInt("codigo");
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
}
