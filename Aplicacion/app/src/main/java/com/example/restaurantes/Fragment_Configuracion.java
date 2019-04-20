package com.example.restaurantes;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Configuracion extends Fragment {
    View rootView;

    public static Fragment_Configuracion newInstance() {
        Fragment_Configuracion fragment = new Fragment_Configuracion();
        // TODO recibir datos necesarios
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_configuracion, container, false);

        return rootView;
    }
}
