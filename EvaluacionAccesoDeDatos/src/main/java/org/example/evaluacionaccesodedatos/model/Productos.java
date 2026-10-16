package org.example.evaluacionaccesodedatos.model;

public class Productos {
    private int id;
    private String codigo;
    private String Nombre;
    private String categoria;
    private Double precio;
    private int Stock;

    public Productos(){

    }

    public Productos(int id, String codigo, String nombre, String categoria, Double precio, int stock) {
        this.id = id;
        this.codigo = codigo;
        Nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        Stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }
}
