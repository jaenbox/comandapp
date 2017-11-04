package com.jaen.comandapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.print.PrintManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jaen.comandapp.modelo.Impresion;
import com.jaen.comandapp.modelo.Pedido;
import com.jaen.comandapp.util.MyPrintDocumentAdapter;
import com.jaen.comandapp.util.PedidoAdapter;
import com.jaen.comandapp.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {

    private static final String URL_BASE = "http://";
    private static final String URL_PEDIDO = "/api/v1/last_pedido.php?user=";

    static final int REQUEST = 1;

    ListView listView;
    ArrayAdapter adapter;
    FloatingActionButton fabAddComanda;
    String id_camarero;
    String ip_servidor;

    ArrayList<String> pedido;
    JsonObjectRequest jsArrayRequest;

    List<Impresion> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Id del usuario logueado en app.
        SharedPreferences sharedPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_camarero = sharedPref.getString("id_camarero", "");
        ip_servidor = sharedPref.getString("ip_servidor", "");

        fabAddComanda = (FloatingActionButton) findViewById(R.id.fabAddComanda);
        listView = (ListView) findViewById(R.id.listView);

        pedido = new ArrayList<>();

        // Listener listView mostrar pedido.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Recogemos el objeto seleccionado en listView y lo pasamos con intent a PedidoActivity
                Pedido pedido = (Pedido) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, PedidoActivity.class );
                intent.putExtra("pedido", pedido);
                startActivity(intent);
            }
        });

        // Listener fab
        fabAddComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ComandaActivity.class);
                startActivity(intent);
            }
        });

        // Adapter
        adapter = new PedidoAdapter(this, id_camarero, ip_servidor);
        listView.setAdapter(adapter);


    }

    @Override
    public void onResume(){
        super.onResume();

        setContentView(R.layout.activity_main);

        // Id del usuario logueado en app.
        SharedPreferences sharedPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
        id_camarero = sharedPref.getString("id_camarero", "");

        fabAddComanda = (FloatingActionButton) findViewById(R.id.fabAddComanda);
        listView = (ListView) findViewById(R.id.listView);

        // Listener listView mostrar pedido.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Recogemos el objeto seleccionado en listView y lo pasamos con intent a PedidoActivity
                Pedido pedido = (Pedido) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, PedidoActivity.class );
                intent.putExtra("pedido", pedido);
                startActivity(intent);
            }
        });

        // Listener fab
        fabAddComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ComandaActivity.class);
                startActivityForResult(intent, REQUEST);
            }
        });

        // Adapter
        adapter = new PedidoAdapter(this, id_camarero, ip_servidor);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d("MainActivity", "Pedido tramitado correctamente");
                new getLastPedido().execute();

                doPrint print = new doPrint();
                print.execute();
            }
        } else {
            Log.d("MainActivity", "Pedido cancelado correctamente");

        }
    }

    private class getLastPedido extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Nueva petición JSONObject
            jsArrayRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    URL_BASE+ ip_servidor + URL_PEDIDO + id_camarero,
                    (String)null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            items = parseJson(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("MainActivity: ", "Error Respuesta en JSON: " + error.getMessage());

                        }
                    }
            );

            // Añadir petición a la cola
            VolleySingleton.getInstance(MainActivity.this).addToRequestQueue(jsArrayRequest);

            return null;
        }
    }

    public List<Impresion> parseJson(JSONObject jsonObject){

        // Variables locales
        List<Impresion> impresiones = new ArrayList<>();
        JSONArray jsonArray= null;

        try {
            // Obtener el array del objeto
            jsonArray = jsonObject.getJSONArray("pedido");

            for(int i=0; i<jsonArray.length(); i++){

                try {
                    JSONObject objeto= jsonArray.getJSONObject(i);

                    Impresion impresion = new Impresion(
                            objeto.getString("id"),
                            objeto.getString("id_mesa"),
                            objeto.getString("fecha"),
                            objeto.getString("id_user"),
                            objeto.getString("name"),
                            objeto.getString("price"),
                            objeto.getString("observaciones"));

                    impresiones.add(impresion);

                } catch (JSONException e) {
                    Log.e("MainActivity: ", "Error de parsing: "+ e.getMessage());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return impresiones;
    }

    private class doPrint extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            // Necesario para que la tarea Asincrona getLastPedido rellene la List<Impresion> items
            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                // Get a PrintManager instance
                PrintManager printManager = (PrintManager)MainActivity.this.getSystemService(Context.PRINT_SERVICE);

                // Set job name, which will be displayed in the print queue
                String jobName = MainActivity.this.getString(R.string.app_name) + " Document";

                // Start a print job, passing in a PrintDocumentAdapter implementation
                // to handle the generation of a print document
                printManager.print(jobName, new MyPrintDocumentAdapter(MainActivity.this, items), null);


            } catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_new:
                Intent intent = new Intent(MainActivity.this, PlatoActivity.class );
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
