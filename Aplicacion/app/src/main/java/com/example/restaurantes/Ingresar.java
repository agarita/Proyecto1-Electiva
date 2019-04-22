package com.example.restaurantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Ingresar extends AppCompatActivity {

    EditText user;
    EditText pass;

    public boolean verificar(){
        //TODO
        return true;
    }

    public void onBtnIngresarClicked(View view) throws Exception {
        if(user.getText().toString().isEmpty() || pass.getText().toString().isEmpty())
            Toast.makeText(this, "No ha ingresado un usuario y/o contraseña.", Toast.LENGTH_SHORT).show();
        else if(verificar()){
            Conexion conexion = new Conexion();
            String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();
            if(UserExist(result,user.getText().toString(),pass.getText().toString())) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "El correo o contraseña son incorrectas.", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(this, "La información ingresada no corresponde a ningún usuario.", Toast.LENGTH_SHORT).show();
    }

    private boolean UserExist(String jsonDatos, String correo, String password) throws Exception {
        JSONArray datos = new JSONArray(jsonDatos);
        Crypto crypto=new Crypto();

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("email").equals(correo) && crypto.decrypt(elemento.getString("password")).equals(password)){
                return true;
            }
        }
        return false;
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
