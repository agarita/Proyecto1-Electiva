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
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Ingresar extends AppCompatActivity {

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    EditText user;
    EditText pass;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPassword);

        callbackManager = CallbackManager.Factory.create();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken currentToken) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        LoginButton loginButton = findViewById(R.id.login_button);
        FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("login", "El usuario ha ingresado con Facebook");
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
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
        };

        loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile"));
        loginButton.registerCallback(callbackManager, callback);

        if (AccessToken.getCurrentAccessToken() != null) {
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

    /* Cosas de Facebook */
    @Override
    protected void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        nextActivity(profile);
    }

    @Override
    protected void onPause(){ super.onPause(); }

    protected void onStop(){
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void nextActivity(Profile profile) {
        if(profile != null){
            Log.i("login",profile.getFirstName());
            Log.i("login",profile.getLastName());
            Log.i("login",profile.getProfilePictureUri(200,200).toString());
        }
    }
}
