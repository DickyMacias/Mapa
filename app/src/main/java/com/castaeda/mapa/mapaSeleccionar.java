package com.castaeda.mapa;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.castaeda.mapa.Utilerias.Anuncio;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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


public class mapaSeleccionar extends FragmentActivity {

    Toolbar toolbar;
    GoogleMap mMap;
    LatLng position_map;
    Builder builder;

    @Override
    public void onBackPressed(){
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_seleccionar);

        builder = new Builder(mapaSeleccionar.this);
        builder.setTitle("Selecionar");
        builder.setMessage("¿Deseas selecionar esta posicion para la casa?");


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Seleccione el lugar de anuncio");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        });
        initMap();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = mapFragment.getMap();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                mMap.clear();
                MarkerOptions options = new MarkerOptions()
                        .title("Lugar selecionado")
                        .position(new LatLng(latLng.latitude,
                                latLng.longitude));
                mMap.addMarker(options);

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        position_map=latLng;
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMap.clear();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

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

        }
    }