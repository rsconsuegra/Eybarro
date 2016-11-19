package org.underpressureapps.eybarro;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity {

    EditText usuario;
    EditText pass;
    public static String TAG = "AppFirebase";
    public static final String LOGIN_URL = "https://pomelo.uninorte.edu.co/pls/prod/twbkwbis.P_ValLogin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    CookieManager cookieManager = new CookieManager();

    public void onClickIniciar(View view) {
        /*Handle HttpRequest. if login successful change activity:*/

        usuario   = (EditText)findViewById(R.id.edusuario);
        pass      = (EditText)findViewById(R.id.edcontraseña);
        final MainActivity context =  this;

        Uri base_addres =Uri.parse("https://pomelo.uninorte.edu.co");
        new Thread(new Runnable() {
            public void run() {


                URL url = null;
                CookieHandler.setDefault(cookieManager);


                String user     =usuario.getText().toString();
                String password =pass.getText().toString();
                user="randyc";
                password="270295randy";
                //Save cokies

                try {
                    url = new URL(LOGIN_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpsURLConnection conn = null;

                try {
                    conn = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:42.0) Gecko/20100101 Firefox/42.0");
                    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    conn.setRequestProperty("Connection", "keep-alive");
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", "TESTID=set");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("sid", user)
                        .appendQueryParameter("PIN", password);
                String query = builder.build().getEncodedQuery();
                //System.out.println(query);

                OutputStream os = null;
                try {
                    os = conn.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedWriter writer = null;

                writer = new BufferedWriter(
                        new OutputStreamWriter(os));

                try {
                    writer.write(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    conn.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                InputStream in = null;
                try {
                    in = conn.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String encoding = conn.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = null;
                try {
                    body = IOUtils.toString(in, encoding);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(!body.contains("Bienvenido")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Acceso: Usuario/Contraseña Incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                            System.out.println(cookieManager.getCookieStore().getCookies().get(0).getValue());
                            Intent i = new Intent(MainActivity.this,UploadActivity.class);
                            startActivity(i);
                        }
                    });
                }

                //Log.d("Tag",body);
            }
        }).start();

    }
}
