package com.example.restaurantes;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment_Acerca_De extends Fragment {
    View rootView;

    public static Fragment_Acerca_De newInstance() {
        Fragment_Acerca_De fragment = new Fragment_Acerca_De();
        // TODO recibir datos necesarios
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_acerca_de, container, false);

        return rootView;
    }
}
