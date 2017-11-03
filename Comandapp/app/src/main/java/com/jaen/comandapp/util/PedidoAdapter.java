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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jaen.comandapp.R;
import com.jaen.comandapp.modelo.Pedido;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaen on 23/08/2017.
 */

public class PedidoAdapter extends ArrayAdapter {

    // Atributos
    JsonObjectRequest jsArrayRequest;
    private static final String URL_BASE = "http://";
    private String URL_PEDIDOS = "/api/v1/obtener_pedidos.php?user=";

    List<Pedido> items;
    String id_camarero;
    String ip_servidor;

    public PedidoAdapter(Context context, String id_camarero, String ip_servidor) {
        super(context, 0);
        // Pasamos al adapter el usuario logueado.
        this.id_camarero = id_camarero;
        this.ip_servidor = ip_servidor;

        URL_PEDIDOS +=id_camarero;

        new GetPedidos().execute();
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
                R.layout.item_pedido_list,
                parent,
                false) : convertView;

        Pedido item = items.get(position);

        // Views
        TextView tvPedido = (TextView) listItemView.findViewById(R.id.tvPedido);
        TextView tvFecha = (TextView) listItemView.findViewById(R.id.tvFecha);
        TextView tvMesa = (TextView) listItemView.findViewById(R.id.tvMesa);

        // Update views
        tvPedido.setText(item.getId());
        tvFecha.setText(item.getFecha());
        tvMesa.setText(item.getId_mesa());

        return listItemView;
    }

    /* Clase AsynkTask para cargar los platos. */
    private class GetPedidos extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // Nueva petición JSONObject
            jsArrayRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL_BASE + ip_servidor + URL_PEDIDOS,
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
                            Log.d("PedidoAdapter: ", "Error Respuesta en JSON: " + error.getMessage());

                        }
                    }
            );

            // Añadir petición a la cola
            VolleySingleton.getInstance(PedidoAdapter.this.getContext()).addToRequestQueue(jsArrayRequest);

            return null;
        }

        public List<Pedido> parseJson(JSONObject jsonObject){
            // Variables locales
            List<Pedido> platos = new ArrayList<>();
            JSONArray jsonArray= null;

            try {
                // Obtener el array del objeto
                jsonArray = jsonObject.getJSONArray("pedidos");

                for(int i=0; i<jsonArray.length(); i++){

                    try {
                        JSONObject objeto= jsonArray.getJSONObject(i);

                        Pedido plato = new Pedido(
                                objeto.getString("id"),
                                objeto.getString("id_mesa"),
                                objeto.getString("fecha"),
                                objeto.getString("pagado"),
                                objeto.getString("estado"),
                                objeto.getString("id_user"));

                        platos.add(plato);

                    } catch (JSONException e) {
                        Log.e("PlatoAdapter: ", "Error de parsing: "+ e.getMessage());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return platos;
        }
    }
}
