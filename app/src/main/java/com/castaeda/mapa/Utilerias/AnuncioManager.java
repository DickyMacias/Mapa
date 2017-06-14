package com.castaeda.mapa.Utilerias;

import android.util.Log;

import com.castaeda.mapa.Utilerias.Anuncio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnuncioManager {

    public static List<Anuncio> parseJSonToObject(String json){
        ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                Anuncio anuncio = new Anuncio();
                anuncio.setId(jsonObj.getInt("id"));
                anuncio.setTitulo(jsonObj.getString("titulo"));
                anuncio.setSnippet(jsonObj.getString("snippet"));
                anuncio.setGmLatitud(jsonObj.getDouble("gm_lat"));
                anuncio.setGmLongitud(jsonObj.getDouble("gm_lng"));
                anuncios.add(anuncio);
            }
        } catch (JSONException e) {
            Log.e("AnuncioManager", "Error procesando el JSON");
        }
        return anuncios;
    }

}
