package com.example.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Ingresar extends AppCompatActivity {

    CallbackManager callbackManager;
    EditText user;
    EditText pass;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        callbackManager = CallbackManager.Factory.create();
        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPassword);

        final LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken().getToken();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getData(object);
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.i("login", "El usuario cancelo el ingreso");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("login", "Ha habido un error en el ingreso");
                Log.e("login", error.toString());
            }
        });
        if(AccessToken.getCurrentAccessToken() != null){
            Log.i("login", "Ha hecho login exitosamente.");
        }
    }

    public void onBtnIngresarClicked(View view) throws Exception {
        progressBar.setVisibility(View.VISIBLE);// To Show ProgressBar
        if(user.getText().toString().isEmpty() || pass.getText().toString().isEmpty())
            Toast.makeText(this, "No ha ingresado un usuario y/o contraseña.", Toast.LENGTH_SHORT).show();
        else {
            Conexion conexion = new Conexion();
            String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();

            String idUsuario=UserExist(result,user.getText().toString(),pass.getText().toString());
            if(idUsuario!=null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("Correo",user.getText().toString());
                intent.putExtra("idUsuario",idUsuario);
                progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                startActivity(intent);
            }
            else{
                progressBar.setVisibility(View.INVISIBLE); //To Hide ProgressBar
                Toast.makeText(this, "El correo o contraseña son incorrectas.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private String UserExist(String jsonDatos, String correo, String password) throws Exception {
        JSONArray datos = new JSONArray(jsonDatos);
        Crypto crypto=new Crypto();

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("email").equals(correo) && /*crypto.decrypt(*/elemento.getString("password").equals(password)){
                return elemento.getString("id");
            }
        }
        return null;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }



    private void getData(JSONObject object) {
        //Aquí se guarda la info del facebook
        //Foto perfil, correo y fecha nacimiento.
        try {
            Log.i("login", object.getString("email"));
            Log.i("login", object.getString("user_birthday"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
