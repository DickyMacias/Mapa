package com.castaeda.mapa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.castaeda.mapa.Utilerias.Configuracion;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class subirActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static LatLng selected_pos;

    EditText txtTitulo, txtSnippet;
    String myUrl;
    Double myCurrentLocation_Latitud;
    Double myCurrentLocation_Longitud;
    Button anunciarButton;
    Toolbar toolbar;

    private static int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vender);

        final Button seleccionar = (Button) findViewById(R.id.seleccionar);
        seleccionar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), mapaSeleccionar.class);
                startActivity(intent);
            }
        });

        if (checkGooglePlayServices()) {
            buildGoogleApiClient();

            //prepare connection request
            createLocationRequest();
        }

        anunciarButton = (Button) findViewById(R.id.btn_anunciar_venta);
        registerForContextMenu(anunciarButton );
        anunciarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {

                if(selected_pos!=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(subirActivity.this);
                    builder.setCancelable(false);
                    builder.setTitle("Deseas anunciar?");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            openContextMenu(v);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    Toast.makeText(getBaseContext(), "Favor de selecionar la posicion de la casa en el mapa.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void enviarAnuncio(){
        try {
            txtTitulo = (EditText) findViewById(R.id.txtTitulo);
            txtSnippet = (EditText) findViewById(R.id.txtSnippet);

            StringBuilder url = new StringBuilder();
            url.append(Configuracion.APP_URL_BASE + "anuncios.new.php?")
                    .append("titulo=").append(URLEncoder.encode(txtTitulo.getText().toString(), "utf-8")).append("&")
                    .append("snippet=").append(URLEncoder.encode(txtSnippet.getText().toString(), "utf-8")).append("&")
                    .append("gm_lat=").append(URLEncoder.encode(String.valueOf(selected_pos.latitude), "utf-8")).append("&")
                    .append("gm_lng=").append(URLEncoder.encode(String.valueOf(selected_pos.longitude), "utf-8"));

            myUrl=Configuracion.APP_URL_BASE
                    +"anuncios.new.php?"
                    +"titulo="+(URLEncoder.encode(txtTitulo.getText().toString(), "utf-8"))
                    +"&snippet="+(URLEncoder.encode(txtSnippet.getText().toString(), "utf-8"))
                    +"&gm_lat="+String.valueOf(selected_pos.latitude)
                    +"&gm_lng="+String.valueOf(selected_pos.longitude);


            UploadTask upload = new UploadTask();
            upload.execute(myUrl);
            Log.e("Rip", myUrl);

            Toast.makeText(getBaseContext(), "Anuncio creado", Toast.LENGTH_SHORT).show();
            selected_pos=null;
            finish();
        } catch(Exception e){
            Log.e("Vender",e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds options to the action bar if it is present.
        //getMenuInflat er().inflate(R.menu.main, menu);
        return true;
    }

    private class UploadTask extends AsyncTask<String, Integer, String> {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try {
                uploadProducto();
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    StringBuilder sb = new StringBuilder();

    private void uploadProducto() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("titulo",txtTitulo.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("snippet",txtSnippet.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("gm_lat",String.valueOf(selected_pos.latitude)));
            nameValuePairs.add(new BasicNameValuePair("gm_lng",String.valueOf(selected_pos.longitude)));


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Configuracion.APP_URL_BASE+"/anuncios.new.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            try {
                BufferedReader reader =new BufferedReader(new InputStreamReader(entity.getContent()), 65728);
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);

                }
            }
            catch (IOException e) { e.printStackTrace(); }
            catch (Exception e) { e.printStackTrace(); }

            Log.e("SB MESSAGE:", sb.toString()+" URL: "+Configuracion.APP_URL_BASE);


        } catch (Exception e) {
            Log.e("Error:", e.toString());
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(20000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        myCurrentLocation_Latitud = mLastLocation.getLatitude();
        myCurrentLocation_Longitud = mLastLocation.getLongitude();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean checkGooglePlayServices() {

        int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(checkGooglePlayServices,
                    this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();

            return false;
        }

        return true;

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Qué desea hacer?");
        menu.add(0, v.getId(), 0, "Crear otro anuncio");
        menu.add(0, v.getId(), 0, "Ir a la pantalla de inicio");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Crear otro anuncio") {
            enviarAnuncio();
        }
        else if (item.getTitle() == "Ir a la pantalla de inicio") {
            enviarAnuncio();
            Intent intent = new Intent();
            intent.setClass(getBaseContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        else {
            return false;
        }
        return true;
    }
}

