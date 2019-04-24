package com.example.restaurantes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment_Perfil extends Fragment {
    View rootView;
    static String NombreUsuario,Correo;

    public static Fragment_Perfil newInstance(String nombreUsuario,String correo) {
        Fragment_Perfil fragment = new Fragment_Perfil();
        NombreUsuario=nombreUsuario;
        Correo=correo;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_perfil, container, false);
        TextView nombre=rootView.findViewById(R.id.txtNombre);
        TextView correo=rootView.findViewById(R.id.txtCorreo);

        nombre.setText(NombreUsuario);
        correo.setText(Correo);

        return rootView;
    }
}
