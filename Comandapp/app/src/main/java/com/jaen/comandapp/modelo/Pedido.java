package com.jaen.comandapp.modelo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by jaen on 23/08/2017.
 */

public class Pedido implements Serializable{

    private String id;
    private String id_mesa;
    private String fecha;
    private String pagado;
    private String estado;
    private String id_user;

    public Pedido(String id, String id_mesa, String fecha, String pagado, String estado, String id_user) {
        this.id = id;
        this.id_mesa = id_mesa;
        this.fecha = fecha;
        this.pagado = pagado;
        this.estado = estado;
        this.id_user = id_user;
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

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
