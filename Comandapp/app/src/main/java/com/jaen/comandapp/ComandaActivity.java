package com.jaen.comandapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jaen.comandapp.modelo.Mesa;
import com.jaen.comandapp.modelo.Plato;
import com.jaen.comandapp.util.PlatoAdapter;
import com.jaen.comandapp.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import static java.lang.Thread.sleep;

public class ComandaActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    // Atributos
    private static final String URL_BASE = "http://";
    private static final String URL_MESAS = "/api/v1/obtener_mesas.php";
    private static final String URL_INSERTAR = "/api/v1/insertar_pedido.php";
    private static final String URL_PEDIDO = "/api/v1/last_pedido.php?user=";
    private ArrayList<String> mesas;
    private JSONArray jsonArray;

    Mesa mesa;
    String id_camarero = "";
    String ip_servidor = "";
    ListView listView;
    ArrayAdapter adapter;
    Spinner spMesas;
    EditText etComanda;
    EditText etObservaciones;
    ArrayList<Plato> platos;
    ArrayList<String> pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);
        //Botón atrás.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Id del usuario logueado en app.
        SharedPreferences sharedPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_camarero = sharedPref.getString("id_camarero", "");
        ip_servidor = sharedPref.getString("ip_servidor", "");

        // Inicializar el ArrayList que rellena el spinner
        mesas = new ArrayList<String>();
        platos = new ArrayList<Plato>();
        pedido = new ArrayList<>();

        etComanda = (EditText) findViewById(R.id.etComanda);
        spMesas = (Spinner) findViewById(R.id.spMesa);
        etObservaciones = (EditText) findViewById(R.id.etObservaciones);

        spMesas.setOnItemSelectedListener(this);
        listView = (ListView) findViewById(R.id.lvPlatos);

        // Recoger datos para spinner
        getData();

        // Adapter
        adapter = new PlatoAdapter(this, ip_servidor);
        listView.setAdapter(adapter);

        /*Cada elemento seleccionado se almacena en el campo etComanda*/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Plato seleccionado.
                Plato plato = (Plato) adapter.getItem(position);
                etComanda.append(plato.getName()+ Html.fromHtml("<br />"));
                platos.add(plato);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Segun el "num" de mesa seleccionado se almacena en el objeto mesa
        mesa = new Mesa(getId(i), getNum(i), getComensales(i));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // no lo implementamos
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.comanda_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                etComanda.setText("");// limpiamos el campo comanda
                platos.clear(); // borramos elementos de arraylist.
                return true;
            case R.id.action_send:
                /* Se ejecuta en segundo plano el envio del pedido*/
                Log.d("Pedido ", "sendPedido");

                sendPedido send = new sendPedido();
                send.execute(etObservaciones.getText().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getData(){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(URL_BASE+ ip_servidor + URL_MESAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            jsonArray = j.getJSONArray("mesas");
                            //Calling method getStudents to get the students from the JSON Array
                            getMesas(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Llamamos al método singleton Volley y añadimos a la cola la nueva petición
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getMesas(JSONArray j){
        //Traversing through all the items in the json array
        for(int i=0;i<j.length();i++){
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                mesas.add(json.getString("num"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spMesas.setAdapter(new ArrayAdapter<String>(ComandaActivity.this, android.R.layout.simple_spinner_dropdown_item, mesas));
    }

    private void getLastPedido(){
        Log.d("Pedido ", " IN getLastPedido()");

        //Creating a string request
        StringRequest stringRequest = new StringRequest(URL_BASE + ip_servidor + URL_PEDIDO+id_camarero,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            jsonArray = j.getJSONArray("pedido");
                            //Calling method getStudents to get the students from the JSON Array
                            getPedido(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Llamamos al método singleton Volley y añadimos a la cola la nueva petición
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getPedido(JSONArray j){
        Log.d("Pedido ", "IN getPedido()");
        //Traversing through all the items in the json array

        try {
            //Getting json object
            JSONObject json = j.getJSONObject(0);
            Log.d("Pedido id ", j.getJSONObject(0).getString("id_pedido"));
            Log.d("Pedido mesa ", j.getJSONObject(0).getString("id_mesa"));
            Log.d("Pedido fecha ", j.getJSONObject(0).getString("fecha"));
            //Adding the name to array list
            pedido.add(json.getString("id_pedido"));
            pedido.add(json.getString("id_mesa"));
            pedido.add(json.getString("fecha"));
            pedido.add(json.getString("id_user"));
            pedido.add(json.getString("name"));
            pedido.add(json.getString("price"));

        } catch (JSONException e) {
                e.printStackTrace();
        }
    }

    private String getId(int position){
        String name="";
        try {
            //Getting object of given index
            JSONObject json = jsonArray.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }

    private String getNum(int position){
        String name="";
        try {
            //Getting object of given index
            JSONObject json = jsonArray.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }

    private String getComensales(int position){
        String name="";
        try {
            //Getting object of given index
            JSONObject json = jsonArray.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("num");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }

    /**
     * Envio del Pedido a WebService
     */
    private class sendPedido extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            // Recogemos todos los valores.
            String id = null;
            String id_mesa = mesa.getId();
            String fecha = " ";
            String pagado = "0";
            String estado = "cocina";
            // HashMap para crear el objeto Json a enviar.
            HashMap<String, String> map = new HashMap<>();
            // Enviamos los datos para generar el pedido 'pedido' y las lineas de pedido 'comandas'
            map.put("id", id);
            map.put("id_user", id_camarero);
            map.put("estado", estado);
            map.put("pagado", pagado);
            map.put("fecha", fecha);
            map.put("id_mesa", id_mesa);
            // Almacenamos todos los platos y los añadimos al HashMap con nomenglatura 'platoX : id_plato'
            for(int i=0; i<platos.size(); i++) {
                map.put("plato"+i, platos.get(i).getId());
            }
            map.put("num_platos", String.valueOf(platos.size()));
            map.put("observaciones", strings[0]);
            map.put("cantidad", "1");
            Log.d("LOG ", "COMANDA ACTYIVITY");

            // Objeto JSON
            JSONObject jobject = new JSONObject(map);

            // Actualizar datos en el servidor
            VolleySingleton.getInstance(ComandaActivity.this).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.POST,
                            URL_BASE + ip_servidor + URL_INSERTAR,
                            jobject,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // Procesar la respuesta del servidor
                                    procesarRespuesta(response);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Comanda Activity ", "Error Volley: " + error.getMessage());
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json; charset=utf-8");
                            headers.put("Accept", "application/json");
                            return headers;
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8" + getParamsEncoding();
                        }
                    }
            );

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    private void procesarRespuesta(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            ComandaActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    // Enviar código de éxito
                    ComandaActivity.this.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    ComandaActivity.this.finish();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            ComandaActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    ComandaActivity.this.setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    ComandaActivity.this.finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}