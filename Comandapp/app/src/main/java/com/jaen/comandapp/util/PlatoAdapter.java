package com.jaen.comandapp.util;

import android.content.Context;
import android.os.AsyncTask;
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
import com.jaen.comandapp.R;
import com.jaen.comandapp.modelo.Plato;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaen on 23/08/2017.
 */

public class PlatoAdapter extends ArrayAdapter {

    // Atributos
    JsonObjectRequest jsArrayRequest;
    private static final String URL_BASE = "http://";
    private static final String URL_PLATOS = "/api/v1/obtener_platos.php";


    List<Plato> items;
    String ip_servidor;

    public PlatoAdapter(Context context, String ip_servidor) {
        super(context, 0);
        this.ip_servidor = ip_servidor;
        new GetPlatos().execute();  // se lanza metodo Asyntask

    }

    @Override
    public int getCount() {
        return items != null ? items.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View listItemView;

        listItemView = null == convertView ? layoutInflater.inflate(
                R.layout.item_plato_list,
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

    /* Clase AsynkTask para cargar los platos. */
    private class GetPlatos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // Nueva petición JSONObject
            jsArrayRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL_BASE + ip_servidor + URL_PLATOS,
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
                            Log.d("Plato Adapter", "Error Respuesta en JSON: " + error.getMessage());

                        }
                    }
            );

            // Añadir petición a la cola
            VolleySingleton.getInstance(PlatoAdapter.this.getContext()).addToRequestQueue(jsArrayRequest);

            return null;
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
                        Log.e("Plato Adapter", "Error de parsing: "+ e.getMessage());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return platos;
        }
    }

}
