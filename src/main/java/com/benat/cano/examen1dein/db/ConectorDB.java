package com.benat.cano.examen1dein.db;

import javafx.scene.control.Alert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que establece la conexión a la base de datos MySQL.
 * Proporciona métodos para obtener la conexión y cerrarla.
 */
public class ConectorDB {
    private final Connection connection; ///< Conexión a la base de datos.

    /**
     * Constructor de la clase ConectorDB.
     * Establece la conexión a la base de datos con las credenciales especificadas.
     *
     * @throws SQLException Si hay un error al establecer la conexión a la base de datos.
     */
    public ConectorDB() throws SQLException, FileNotFoundException {
        Properties props = loadProperties();
        String url = props.getProperty("dburl");
        connection = DriverManager.getConnection(url, props);

        DatabaseMetaData databaseMetaData = connection.getMetaData();
/*
        System.out.println();
        System.out.println("--- Datos de conexión ------------------------------------------");
        System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
        System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
        System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
        System.out.println("----------------------------------------------------------------");
        System.out.println();
        connection.setAutoCommit(true);*/

    }

    /**
     * Obtiene la conexión a la base de datos.
     *
     * @return La conexión a la base de datos.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos.
     * Muestra una alerta si hay un error al cerrar la conexión.
     */
    public void closeConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close(); // Cierra la conexión si está abierta.
            }
        } catch (SQLException e) {
            mostrarAlertaErrorCierreConexion(); // Llama a una alerta personalizada.
        }
    }

   /* public static void main(String[] args) throws SQLException, FileNotFoundException {
        ConectorDB c = new ConectorDB();
        c.getConnection(); // Obtiene la conexión a la base de datos.
    }*/

    public static Properties loadProperties() {
        try (FileInputStream fs = new FileInputStream("db.properties")) {
            Properties props = new Properties();
            props.load(fs);
            return props;
        } catch (FileNotFoundException e) {
            mostrarAlertaArchivoNoEncontrado();
        } catch (IOException e) {

            mostrarAlertaErrorEntradaSalida();
        }
        return null;
    }
    /**
     * Muestra una alerta cuando el archivo especificado no se encuentra.
     */
    private static void mostrarAlertaArchivoNoEncontrado() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Archivo");
        alerta.setHeaderText("Archivo No Encontrado");
        alerta.setContentText("El archivo especificado no pudo ser encontrado. Por favor, verifica la ruta o el nombre del archivo.");
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta cuando ocurre un error de E/S.
     */
    private static void mostrarAlertaErrorEntradaSalida() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Entrada/Salida");
        alerta.setHeaderText("Error de E/S");
        alerta.setContentText("Ocurrió un error durante la operación de entrada/salida. Por favor, intente nuevamente o contacte al soporte.");
        alerta.showAndWait();
    }
    /**
     * Muestra una alerta cuando hay un error al cerrar la conexión a la base de datos.
     */
    private void mostrarAlertaErrorCierreConexion() {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de Base de Datos");
        alerta.setHeaderText("Error al Cerrar Conexión");
        alerta.setContentText("No se pudo cerrar la conexión a la base de datos. Por favor, intente nuevamente o contacte al soporte.");
        alerta.showAndWait();
    }

}