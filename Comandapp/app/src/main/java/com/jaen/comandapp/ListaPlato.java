package com.jaen.comandapp;

import com.jaen.comandapp.modelo.Plato;

import java.util.List;

/**
 * Created by jaenx on 23/08/2017.
 */

public class ListaPlato {

    private List<Plato> items;

    public ListaPlato(List<Plato> items) {
        this.items = items;
    }

    public void setItems(List<Plato> items) {
        this.items = items;
    }

    public List<Plato> getItems() {
        return items;
    }
}
