package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Ingresar extends AppCompatActivity {

    EditText user;
    EditText pass;

    public boolean verificar(){
        //TODO
        return true;
    }

    public void onBtnIngresarClicked(View view){
        if(user.getText().toString().matches("") || pass.getText().toString().matches(""))
            Toast.makeText(this, "No ha ingresado un usuario y/o contraseña.", Toast.LENGTH_SHORT).show();
        else if(verificar()){
            //
            // TODO Revisa que la información sea correcta
            //
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this, "La información ingresada no corresponde a ningún usuario.", Toast.LENGTH_SHORT).show();
    }

    public void onBtnIngresarFBClicked(View view){

    }

    public void onBtnRegistrarseClicked(View view){
        Intent intent = new Intent(this, Registrar.class);
        startActivity(intent);
    }

    public void onBtnRecuperarPassClicked(View view){
        Intent intent = new Intent(this, Recuperar.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);

        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPassword);
    }
}
