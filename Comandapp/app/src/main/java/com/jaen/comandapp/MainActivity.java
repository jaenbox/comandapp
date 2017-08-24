package com.jaen.comandapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jaen.comandapp.modelo.Plato;
import com.jaen.comandapp.util.PlatoAdapter;
import com.jaen.comandapp.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    FloatingActionButton fabAddComanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabAddComanda = (FloatingActionButton) findViewById(R.id.fabAddComanda);
        listView = (ListView) findViewById(R.id.listView);

        // Listener listView mostrar pedido.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int position = i;
                Log.d("Valor itemValue ", String.valueOf(position));
            }
        });

        // Listener fab
        fabAddComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("MainActivity: ", "Se presiono el fabAddComanda");
            }
        });

        // Adapter
        adapter = new PlatoAdapter(this);
        listView.setAdapter(adapter);

    }





}
