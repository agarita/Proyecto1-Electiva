package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Perfil extends AppCompatActivity {

    public void onBtnMapaClicked(View view){
        Intent intent = new Intent(this, Mapa.class);
        startActivity(intent);
    }

    public void onBtnListaClicked(View view){
        Intent intent = new Intent(this, Lista.class);
        startActivity(intent);
    }

    public void onBtnBuscaClicked(View view){
        Intent intent = new Intent(this, Busca.class);
        startActivity(intent);
    }

    public void onBtnConfigClicked(View view){
        Intent intent = new Intent(this, Configuracion.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
    }
}
