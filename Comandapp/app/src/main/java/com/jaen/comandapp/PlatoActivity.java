package com.jaen.comandapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.jaen.comandapp.util.Peticion;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jaen.comandapp.util.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import static java.lang.Thread.sleep;

/**
 * Created by jaenx on 04/11/2017.
 */

public class PlatoActivity extends AppCompatActivity {

    // Atributos
    private static final String URL_BASE = "http://";
    private static final String URL_INSERTAR = "/api/v1/insertar_plato.php";
    private static final String URL_IMG="/api/v1/insert_image.php";
    String ip_servidor = "";

    Spinner spCategory;
    Button btnCamera;
    Button btnSendPlato;
    EditText etName;
    EditText etPrice;
    String path = "jpeg";
    EditText etDescription;
    String imgBase64;
    Boolean photo = false;

    String name = "";
    String price = "";
    String description = "";
    String category = "";

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_plato);

        SharedPreferences sharedPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
        ip_servidor = sharedPref.getString("ip_servidor", "");

        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etDescription = (EditText) findViewById(R.id.etDescription);
        spCategory = (Spinner) findViewById(R.id.spCategory);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnSendPlato = (Button) findViewById(R.id.btnSendPlato);

        /* Spinner */
        ArrayAdapter<CharSequence> spAdapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        spCategory.setAdapter(spAdapter);

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                category = "al";
            }
        });

        /* Button camera*/
        btnCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        /* Button de envio de datos */
        btnSendPlato.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Recoger todos los valores y pasarlos a la api REST.
                name = etName.getText().toString();
                price = etPrice.getText().toString();
                description = etDescription.getText().toString();
                /*
                Log.d("name ", name);
                Log.d("price ", price);
                Log.d("description ", description);
                Log.d("category ", description);
                Log.d("Imagen ", imgBase64);
                */
                if(name=="" | price=="" | description=="" | category=="" | photo== true) {
                    sendPlato send = new sendPlato();
                    send.execute();

                    // Enviar la foto.
                    if(photo) {

                        new Upload().execute();
                    }

                    finish(); // se cierra la activity.
                } else {
                    Toast.makeText(PlatoActivity.this, "Es necesario rellenar los campos", Toast.LENGTH_LONG).show();
                }

            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(), "Tu dispositivo no dispone de camara", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            photo = true;
            // Recogemos la imagen realizada.
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            imgBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // PHP // file_put_contents('img.png', base64_decode($_POST['imagen_base64']));
        } else {
            Toast.makeText(PlatoActivity.this, "No se ha recogido imagen",Toast.LENGTH_LONG).show();
            photo = false;
        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class Upload extends AsyncTask<Void, Void, String> {
        private Bitmap image;
        private String name= "img";

        @Override
        protected String doInBackground(Void... voids) {

            HashMap<String, String> detail = new HashMap<>();
            detail.put("name", name);
            detail.put("image", imgBase64);
            try {
                String dataToSend = hashMapToUrl(detail);
                String response = Peticion.post(URL_BASE+ip_servidor+URL_IMG, dataToSend);
                return response;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            //show image uploaded
            Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
        }
    }

    // Enviar Imágen a api REST.
/*
    public String enviarPost(String imgBase64) {

        String param = "";
        HttpURLConnection connection = null;
        String request = "";

        try{
            // Conectar y envio de datos
            URL url = new URL(URL_BASE + ip_servidor + URL_IMG);
            Log.v("conexion lanzada a ",URL_BASE + ip_servidor + URL_IMG);
            connection=(HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", ""+Integer.toString(param.getBytes().length));

            // Salida de datos
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(param);
            wr.close();

            // Almacena respuesta.
            Scanner scanner = new Scanner(connection.getInputStream());
            Log.v("Recepcion ",scanner.toString());
            while(scanner.hasNext()) {
                request += ( scanner.nextLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }
*/
    // Alnmacenar datos del plato.
    private class sendPlato extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            String id = null;
            String path = "jpeg";

            // HashMap para crear el objeto Json a enviar.
            HashMap<String, String> map = new HashMap<>();

            map.put("id", id);
            map.put("name", name);
            map.put("price", price);
            map.put("path", path);
            map.put("description", description);
            map.put("category", category);

            // Recorremos el hashMap y mostramos por pantalla el par valor y clave
            /*
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();
                Log.d(e.getKey().toString(), e.getValue().toString());
            }
            */
            // Objeto JSON
            JSONObject jobject = new JSONObject(map);

            // Actualizar datos en el servidor
            VolleySingleton.getInstance(PlatoActivity.this).addToRequestQueue(
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
                                    Log.d("Plato Activity--> ", "Error Volley: " + error.getMessage());
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
                            PlatoActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG).show();

                    // Enviar código de éxito
                    PlatoActivity.this.setResult(Activity.RESULT_OK);
                    // Terminar actividad
                    PlatoActivity.this.finish();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            PlatoActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    PlatoActivity.this.setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    PlatoActivity.this.finish();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

}

