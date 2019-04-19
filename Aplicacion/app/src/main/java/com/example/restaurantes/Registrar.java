package com.example.restaurantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity {

    Spinner edad;
    EditText user;
    EditText pass;
    EditText passConf;
    EditText correo;
    ImageView img;
    Pattern pattern;

    public void onBtnNextClicked(View view){
        if(user.getText().toString().matches("") || pass.getText().toString().matches("")
            || passConf.getText().toString().matches("") || correo.getText().toString().matches(""))
            Toast.makeText(this, "No ha ingresado alguno de los datos correspondientes.", Toast.LENGTH_SHORT).show();
        else if(!pass.getText().toString().matches(passConf.getText().toString()))
            Toast.makeText(this, "Las contraseñas dadas son diferentes.", Toast.LENGTH_SHORT).show();
        else if(!pattern.matcher(correo.getText().toString()).matches())
            Toast.makeText(this, "El correo dado no es válido.", Toast.LENGTH_SHORT).show();
        else{
            //
            // Aquí se mete en la BD al nuevo usuario
            //
            this.finish();
        }
    }

    public void llenarSpnEdad(){
        edad = (Spinner) findViewById(R.id.spnEdad);
        List<Integer> list = new ArrayList<Integer>();
        for(int i=15; i<100; i++){
            list.add(i);
        }
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edad.setAdapter(dataAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        pattern = Patterns.EMAIL_ADDRESS;
        img = findViewById(R.id.imagen);
        llenarSpnEdad();
        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPassword);
        passConf = findViewById(R.id.txtPasswordConf);
        correo = findViewById(R.id.txtCorreo);
    }
}
