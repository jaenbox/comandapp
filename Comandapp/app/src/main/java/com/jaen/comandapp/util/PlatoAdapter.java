package com.jaen.comandapp.util;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jaen.comandapp.MainActivity;
import com.jaen.comandapp.R;
import com.jaen.comandapp.modelo.Plato;
import com.jaen.comandapp.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Created by jaenx on 23/08/2017.
 */

public class PlatoAdapter extends ArrayAdapter {


    // Atributos
    private RequestQueue requestQueue;
    JsonObjectRequest jsArrayRequest;
    private static final String URL_BASE = "http://192.168.1.5";
    private static final String URL_PLATOS = "/webservicecomandas/obtener_platos.php";
    private static final String TAG = "PostAdapter";
    List<Plato> items;


    public PlatoAdapter(Context context) {
        super(context, 0);

        // cola de peticiones
        requestQueue = Volley.newRequestQueue(context);

        // Nueva petición JSONObject
        jsArrayRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_BASE + URL_PLATOS,
                (String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        items = parseJson(response);
                        notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error Respuesta en JSON: " + error.getMessage());

                    }
                }
        );

        // Añadir petición a la cola
        VolleySingleton.getInstance(context).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View listItemView;

        listItemView = null == convertView ? layoutInflater.inflate(
                R.layout.item_list,
                parent,
                false) : convertView;

        Plato item = items.get(position);

        // Views
        TextView tvName = (TextView) listItemView.findViewById(R.id.tvElemento);
        TextView tvPrice = (TextView) listItemView.findViewById(R.id.tvPrecio);

        // Update views
        tvName.setText(item.getName());
        tvPrice.setText(item.getPrice());

        return listItemView;
    }


    public List<Plato> parseJson(JSONObject jsonObject){
        // Variables locales
        List<Plato> platos = new ArrayList<>();
        JSONArray jsonArray= null;

        try {
            // Obtener el array del objeto
            jsonArray = jsonObject.getJSONArray("platos");

            for(int i=0; i<jsonArray.length(); i++){

                try {
                    JSONObject objeto= jsonArray.getJSONObject(i);

                    Plato plato = new Plato(
                            objeto.getString("id"),
                            objeto.getString("name"),
                            objeto.getString("price"),
                            objeto.getString("description"),
                            objeto.getString("category"));

                    platos.add(plato);

                } catch (JSONException e) {
                    Log.e(TAG, "Error de parsing: "+ e.getMessage());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return platos;
    }

    private class SimpleTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

}
