package com.jaen.comandapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jaen.comandapp.modelo.Pedido;

public class PedidoActivity extends AppCompatActivity {

    private TextView id;
    private TextView fecha;
    private TextView pagado;
    private TextView estado;
    private TextView id_camarero;
    private TextView id_mesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Recogemos el objeto pasado.
        Pedido pedido = (Pedido) getIntent().getExtras().getSerializable("pedido");

        id = (TextView)findViewById(R.id.tvPedido);
        fecha = (TextView)findViewById(R.id.tvFecha);
        pagado = (TextView)findViewById(R.id.tvPagado);
        estado = (TextView)findViewById(R.id.tvEstado);
        id_camarero = (TextView)findViewById(R.id.tvIdCamarero);
        id_mesa = (TextView)findViewById(R.id.tvIdMesa);

        id.setText(pedido.getId());
        fecha.setText(pedido.getFecha());
        if(pedido.getPagado().equals("0")) {
            pagado.setText("sin pagar");
        } else {
            pagado.setText("pagado");
        }
        estado.setText(pedido.getEstado());
        id_camarero.setText(pedido.getId_user());
        id_mesa.setText(pedido.getId_mesa());
    }
}
