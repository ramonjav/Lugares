package com.example.lugares.utilidades;

import com.example.lugares.datos.Lugares;

import java.util.ArrayList;

public class listDatos {

    public static ArrayList<Lugares> listLugares = new ArrayList<>();

    public static ArrayList<Lugares> getListLugares() {
        return listLugares;
    }

    public static void setListLugares(ArrayList<Lugares> listLugares) {
        listDatos.listLugares = listLugares;
    }
}
