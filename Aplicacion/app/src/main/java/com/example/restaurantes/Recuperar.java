package com.example.restaurantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Recuperar extends AppCompatActivity {

    EditText user;
    EditText correo;
    Pattern pattern;

    public boolean verificar(){
        //
        // Revisa si el correo y el usuario son el mismo
        //
        return true;
    }

    public void onBtnNextClicked(View view){
        if(user.getText().toString().matches("") || correo.getText().toString().matches(""))
            Toast.makeText(this, "No ha ingresado el usuario y/o el correo.", Toast.LENGTH_SHORT).show();
        else if(!pattern.matcher(correo.getText().toString()).matches())
            Toast.makeText(this, "El correo dado no es válido.", Toast.LENGTH_SHORT).show();
        else if(verificar()){
            //
            // Mandar correo
            //
            this.finish();
        }
        else
            Toast.makeText(this, "Ese correo no pertenece a ese usuario.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        pattern = Patterns.EMAIL_ADDRESS;
    }
}
