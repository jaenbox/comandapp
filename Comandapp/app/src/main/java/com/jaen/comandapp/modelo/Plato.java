package com.jaen.comandapp.modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jaenx on 23/08/2017.
 */

public class Plato {
    private String id;
    private String name;
    private String price;
    private String description;
    private String category;

    public Plato() {}

    public Plato(String id, String name, String price, String description, String category) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
    }

    /**
     * Constructor jsonObject
     * @param jsonObject
     */
    public Plato(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getString("id");
            this.name = jsonObject.getString("name");
            this.price = jsonObject.getString("price");
            this.description = jsonObject.getString("description");
            this.category = jsonObject.getString("category");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
