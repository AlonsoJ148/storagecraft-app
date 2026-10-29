package com.storagecraft.model;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Immutable // Le indica a Hibernate que es una vista de lectura
@Table(name = "vw_items_inventario")
public class Item {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "cofre_ubicacion")
    private String cofreUbicacion;

    public Item() {}

    // Getters y Setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getCofreUbicacion() { return cofreUbicacion; }
    public void setCofreUbicacion(String cofreUbicacion) { this.cofreUbicacion = cofreUbicacion; }
}