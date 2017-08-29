package com.jaen.comandapp.modelo;

/**
 * Created by jaen on 26/08/2017.
 */

public class Mesa {

    private String id;
    private String num;
    private String comensales;

    public Mesa(String id, String num, String comensales) {
        this.id = id;
        this.num = num;
        this.comensales = comensales;
    }

    public String getComensales() {
        return comensales;
    }

    public void setComensales(String comensales) {
        this.comensales = comensales;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
