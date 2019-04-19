package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Busca extends AppCompatActivity {

    EditText busqueda;

    public void onBtnMapaClicked(View view){
        Intent intent = new Intent(this, Mapa.class);
        startActivity(intent);
    }

    public void onBtnListaClicked(View view){
        Intent intent = new Intent(this, Lista.class);
        startActivity(intent);
    }

    public void onBtnPerfilClicked(View view){
        Intent intent = new Intent(this, Perfil.class);
        startActivity(intent);
    }

    public void onBtnConfigClicked(View view){
        Intent intent = new Intent(this, Configuracion.class);
        startActivity(intent);
    }

    public void onBtnBuscarClicked(View view){
        //busqueda.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca);

        busqueda = findViewById(R.id.txtBuscado);
    }
}
