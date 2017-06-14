package com.castaeda.mapa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.castaeda.mapa.Utilerias.Anuncio;
import com.castaeda.mapa.Utilerias.AnuncioManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.castaeda.mapa.Utilerias.Configuracion;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    Toolbar toolbar;
    public static List<Anuncio> anuncios;
    GoogleMap mMap;

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        intent.setClass(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_vender);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Espacios disponibles");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mMap = mapFragment.getMap();

    }



    private void initMap() {
        if(mMap == null){
            SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mf.getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }

        LatLng CUU = new LatLng(28.630842, -106.069083);
        mMap.addMarker(new MarkerOptions().position(CUU).title("Chihuahua").visible(false));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CUU, 12));

        if(mMap != null) {
            mMap.clear();
            new MarkerTask().execute();

        }
    }

    private class MarkerTask extends AsyncTask<Void, Void, String> {

        private static final String LOG_TAG = "ExampleApp";

        private final String SERVICE_URL = Configuracion.APP_URL_BASE;


        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(Void... args) {

            HttpURLConnection conn = null;
            final StringBuilder json = new StringBuilder();
            try {
                // Connect to the web service
                URL url = new URL(SERVICE_URL);
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Read the JSON data into the StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    json.append(buff, 0, read);
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error connecting to service", e);
                //throw new IOException("Error connecting to service", e); //uncaught
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return json.toString();
        }


        @Override
        protected void onPostExecute(String json) {

            Log.e("No funciona",Configuracion.APP_URL_BASE);
            try {
                int icon_tipo_anuncio = 0;
                anuncios = AnuncioManager.parseJSonToObject(json);

                for (Anuncio anuncio:anuncios) {
                    LatLng latLng = new LatLng(anuncio.getGmLatitud(),anuncio.getGmLongitud());
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(icon_tipo_anuncio))
                            .title(anuncio.getTitulo())
                            .snippet(anuncio.getSnippet())
                            .position(latLng));
                }


            } catch (Exception e) {
                Log.e(LOG_TAG, "Error generando marcadores de espacios", e);
            }

        }
    }

}