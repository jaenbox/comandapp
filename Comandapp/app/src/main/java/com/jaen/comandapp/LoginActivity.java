package com.jaen.comandapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText etUser;
    private EditText etPassword;
    private Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        etUser = (AutoCompleteTextView) findViewById(R.id.etUser);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignin = (Button) findViewById(R.id.btnSignin);

        btnSignin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        final String res = enviarPost(etUser.getText().toString(), etPassword.getText().toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Comprobar la respuesta del WebService
                                if(isNumeric(res)) {
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                                    // Alamcenar en preferencias el id_camarero.
                                    SharedPreferences sharedPref = getSharedPreferences("datos", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("id_camarero", res);
                                    editor.commit();

                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Usuario o password incorrectos", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                };
                thread.start();
            }
        });

    }

    /**
     * Envio de parámetros al WebService
     * @param correo
     * @param passwd
     * @return
     */
    public String enviarPost(String correo, String passwd) {
        // Parámetros a pasar
        String param = "username="+correo+"&passwd="+passwd;

        HttpURLConnection connection = null;
        String request = "";

        try{
            // Conectar y envio de datos
            URL url = new URL("http://192.168.1.5/WebServiceComandas/signin.php");
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

            while(scanner.hasNext()) {
                request += ( scanner.nextLine());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return request.toString();
    }

    /**
     * Comprueba si el WebService nos devuelve un Id(número) o String
     * @param s
     * @return
     */
    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}

