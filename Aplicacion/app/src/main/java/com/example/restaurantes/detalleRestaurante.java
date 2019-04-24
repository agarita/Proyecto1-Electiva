package com.example.restaurantes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class detalleRestaurante extends Fragment {
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_restaurante, container, false);

        String Nombre=new String();
        String Genero=new String();
        String Director=new String();
        String Anno=new String();
        String Sipnosis=new String();
        String Actores=new String();
        String Portada=new String();
        String Calificacion=new String();

        if (getArguments() != null) {

            Nombre = getArguments().getString("Nombre");
            Genero = getArguments().getString("Genero");
            Director = getArguments().getString("Director");
            Anno = getArguments().getString("Anno");
            Sipnosis = getArguments().getString("Sipnosis");
            Actores = getArguments().getString("Actores");
            Portada = getArguments().getString("Portada");
            Calificacion = getArguments().getString("Calificacion");

        }
        return rootView;
    }


}
