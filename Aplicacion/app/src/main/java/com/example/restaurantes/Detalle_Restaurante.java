package com.example.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;

public class Detalle_Restaurante extends AppCompatActivity {

    private FragmentTabHost mTabHost;
    String correoUsuario, NombreUsuario;
    String Nombre,Horario,Correo,Latitud,Longitud,Precio,Telefono,TipoComida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_restaurante);

        //Se reciben las variables necesarias
        Intent i=getIntent();
        NombreUsuario = i.getExtras().getString("getNombreUsuario");
        Nombre = i.getExtras().getString("getNombre");
        Horario = i.getExtras().getString("getHorario");
        Correo = i.getExtras().getString("getCorreo");
        Latitud = i.getExtras().getString("getLatitud");
        Longitud = i.getExtras().getString("getLongitud");
        Precio = i.getExtras().getString("getPrecio");
        Telefono = i.getExtras().getString("getTelefono");
        TipoComida = i.getExtras().getString("getTipoComida");


        Bundle args1 = new Bundle();
        args1.putString("getNombreUsuario", NombreUsuario);

        Bundle args2 = new Bundle();
        args2.putString("getNombre", Nombre);
        args2.putString("getHorario", Horario);
        args2.putString("getCorreo", Correo);
        args2.putString("getLatitud", Latitud);
        args2.putString("getLongitud", Longitud);
        args2.putString("getPrecio", Precio);
        args2.putString("getTelefono", Telefono);
        args2.putString("getTipoComida", TipoComida);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();

        //Poner de título
        actionBar.setTitle(Nombre);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contenido_tab);

        mTabHost.addTab(mTabHost.newTabSpec("Información").setIndicator("Información"),
                detalleRestaurante.class, args2);
        mTabHost.addTab(mTabHost.newTabSpec("Comentarios").setIndicator("Comentarios"),
                Comentarios.class, args1);
    }


    private void setFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTran = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment);
        fragmentTran.commitNow();
    }
}
