package com.jaen.comandapp.modelo;

/**
 * Created by jaenx on 18/09/2017.
 */

public class Impresion {
    private String id;
    private String id_mesa;
    private String fecha;
    private String id_user;
    private String name;
    private String price;
    private String observaciones;

    public Impresion(String id, String id_mesa, String fecha, String id_user, String name, String price, String observaciones) {
        this.id = id;
        this.id_mesa = id_mesa;
        this.fecha = fecha;
        this.id_user = id_user;
        this.name = name;
        this.price = price;
        this.observaciones = observaciones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_mesa() {
        return id_mesa;
    }

    public void setId_mesa(String id_mesa) {
        this.id_mesa = id_mesa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
