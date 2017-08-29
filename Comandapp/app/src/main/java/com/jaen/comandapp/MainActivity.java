package com.jaen.comandapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.jaen.comandapp.modelo.Pedido;
import com.jaen.comandapp.util.PedidoAdapter;


public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter adapter;
    FloatingActionButton fabAddComanda;
    String id_camarero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                startActivity(intent);
            }
        });

        // Adapter
        adapter = new PedidoAdapter(this, id_camarero);
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
                startActivityForResult(intent, 0);
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
        adapter = new PedidoAdapter(this, id_camarero);
        listView.setAdapter(adapter);
    }


}
