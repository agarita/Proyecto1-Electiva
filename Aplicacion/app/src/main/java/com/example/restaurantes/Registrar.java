package com.example.restaurantes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity {

    Spinner edad;
    EditText user;
    EditText pass;
    EditText passConf;
    EditText correo;
    Pattern pattern;
    String path_portada;

    private static final int SELECT_PICTURE = 3513;
    private static final int PERMISSION_REQUEST_CODE = 5468;

    public void onBtnNextClicked(View view) throws Exception {
        if (user.getText().toString().matches("") || pass.getText().toString().matches("")
                || passConf.getText().toString().matches("") || correo.getText().toString().matches(""))
            Toast.makeText(this, "No ha ingresado alguno de los datos correspondientes.", Toast.LENGTH_SHORT).show();
        else if (!pass.getText().toString().matches(passConf.getText().toString()))
            Toast.makeText(this, "Las contraseñas dadas son diferentes.", Toast.LENGTH_SHORT).show();
        else if (!pattern.matcher(correo.getText().toString()).matches())
            Toast.makeText(this, "El correo dado no es válido.", Toast.LENGTH_SHORT).show();
        else if (pass.length()<6)
            Toast.makeText(this, "La contraseña debe contener 6 digitos o más.", Toast.LENGTH_SHORT).show();
        else {
            Conexion conexion = new Conexion();
            String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();
            String correo = this.correo.getText().toString().trim();

            if (!UserExist(result, correo)) {
                //Crear cuenta
                if (RegistrarUsuarioBD()) {
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_LONG).show();
                    this.finish();
                } else
                    Toast.makeText(this, "Error no se pudo registrar el usuario", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: Ya existe un correo igual registrado", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean UserExist(String jsonDatos,String correo) throws JSONException {
        JSONArray datos = new JSONArray(jsonDatos);

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("email").equals(correo)){
                return true;
            }
        }
        return false;
    }



    private boolean RegistrarUsuarioBD() throws Exception {
        String mail = this.correo.getText().toString();
        String nick = this.user.getText().toString();
        String password = this.pass.getText().toString();
        String password_confirmation = this.passConf.getText().toString();
        if(!mail.isEmpty() && !nick.isEmpty() && !password.isEmpty() && !password_confirmation.isEmpty() && password.equals(password_confirmation)){
            Conexion conexion = new Conexion();
            Crypto crypto=new Crypto();
            JSONObject json_parametros = new JSONObject();
            json_parametros.put("name",nick);
            json_parametros.put("email",mail);
            json_parametros.put("password",crypto.encrypt(password));
            //json_parametros.put("password_confirmation",password_confirmation);
            json_parametros.put("url_imagen","");
            String datos="{\"user\":"+json_parametros.toString()+"}";
            String  result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users","POST",datos/*json_parametros.toString()*/).get();

            if(result.equals("Created")) {
               return true;
            }
        }
        return false;
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
        llenarSpnEdad();
        user = findViewById(R.id.txtUser);
        pass = findViewById(R.id.txtPassword);
        passConf = findViewById(R.id.txtPasswordConf);
        correo = findViewById(R.id.txtCorreo);
    }


    // ------------------ Elegir imagen de usuario para mostrar ----------
    public void ElegirImagen(){
        if(Build.VERSION.SDK_INT >=23) {
            if (checkPermission()){
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

            }else{
                requestPermission();
            }
        }else{
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri filePath = data.getData();
                if (null != filePath) {
                    try {
                        ImageView imgUsuario=findViewById(R.id.imagenUsuario);
                        imgUsuario.setImageURI(filePath);
                        path_portada = getFilePath(this,filePath);
                        Log.d("PATH", filePath.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE );
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ElegirImagen();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(this, "No se puede acceder a la imagenes sin permisos", Toast.LENGTH_SHORT).show();
                        }else{
                            new AlertDialog.Builder(this)
                                    .setTitle("Acceso denegado")
                                    .setMessage("No se puede acceder a la imagenes sin permisos. Por favor active los permisos en la configuración de la aplicación.")
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do nothing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();//*/
                        }
                    }
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            }
        }
    }

    public void onBtnCambiarImagen(View view) {
        ElegirImagen();
    }
}
