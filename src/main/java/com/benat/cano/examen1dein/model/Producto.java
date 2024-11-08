package com.benat.cano.examen1dein.model;

import java.sql.Blob;
import java.util.Objects;

public class Producto {
    private String codigo;
    private String nombre;
    private double precio;
    private boolean disponible;
    private Blob imagen;

    /**
     * Constructor completo de la clase Producto.
     *
     * @param codigo     El código único del producto.
     * @param nombre     El nombre del producto.
     * @param precio     El precio del producto.
     * @param disponible Estado de disponibilidad del producto (true para disponible, false para no disponible).
     * @param imagen     La imagen del producto en formato Blob.
     */
    public Producto(String codigo, String nombre, double precio, boolean disponible, Blob imagen) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
        this.imagen = imagen;
    }

    /**
     * Constructor sin imagen para casos en que no se desea incluir una imagen de producto.
     *
     * @param codigo     El código único del producto.
     * @param nombre     El nombre del producto.
     * @param precio     El precio del producto.
     * @param disponible Estado de disponibilidad del producto (true para disponible, false para no disponible).
     */
    public Producto(String codigo, String nombre, double precio, boolean disponible) {
        this(codigo, nombre, precio, disponible, null);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Blob getImagen() {
        return imagen;
    }

    public void setImagen(Blob imagen) {
        this.imagen = imagen;
    }

    /**
     * Representación en cadena del objeto Producto.
     *
     * @return Una cadena de texto que representa el producto.
     */
    @Override
    public String toString() {
        return  nombre+ "," + precio+"€";
    }
    /**
     * Compara este objeto Producto con otro objeto para determinar si son iguales.
     * Dos objetos Producto son considerados iguales si tienen el mismo valor para el codigo.
     *
     * @param o el objeto con el que se desea comparar
     * @return {@code true} si el objeto especificado es igual al objeto actual; {@code false} de lo contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return codigo == producto.codigo && Double.compare(precio, producto.precio) == 0 && disponible == producto.disponible && Objects.equals(nombre, producto.nombre) && Objects.equals(imagen, producto.imagen);
    }
    /**
     * Genera un valor hash para este objeto Producto.
     * El valor hash se calcula a partir de los campos {@code codigo}, {@code nombre}, {@code precio},
     * {@code disponible} e {@code imagen}. Este valor se utiliza para optimizar las operaciones de búsqueda
     * en estructuras de datos basadas en hash (como {@link java.util.HashSet} o {@link java.util.HashMap}).
     *
     * @return el valor hash calculado para este objeto Producto
     */
    @Override
    public int hashCode() {
        return Objects.hash(codigo, nombre, precio, disponible, imagen);
    }
}
