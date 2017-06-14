package com.castaeda.mapa.Utilerias;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Anuncio implements Serializable {

    private int id;
    private String titulo;
    private String snippet;
    private Double gmLatitud;
    private Double gmLongitud;


    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitulo(){
        return this.titulo;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }

    public String getSnippet(){
        return this.snippet;
    }

    public void setSnippet(String snippet){
        this.snippet = snippet;
    }

    public Double getGmLatitud(){
        return this.gmLatitud;
    }

    public void setGmLatitud(Double latitud){
        this.gmLatitud = latitud;
    }

    public Double getGmLongitud(){
        return this.gmLongitud;
    }

    public void setGmLongitud(Double longitud){
        this.gmLongitud = longitud;
    }

}
