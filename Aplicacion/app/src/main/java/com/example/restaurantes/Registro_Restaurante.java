package com.example.restaurantes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static com.example.restaurantes.Registrar.getFilePath;

public class Registro_Restaurante extends AppCompatActivity {

    Double latitud,logitud;
    Spinner sItems;
    List<String> spinnerArray;

    List<String> path_imagenes = new ArrayList<>();
    ArrayList<Uri> imagenesUri = new ArrayList<>();

    private static final int SELECT_PICTURE = 3513;
    private static final int PERMISSION_REQUEST_CODE = 5468;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_restaurante);
        Button btnRegistrar = findViewById(R.id.btnRegistrarRestaurante);

        Intent i = getIntent();
        latitud = i.getExtras().getDouble("getLatitud");
        logitud = i.getExtras().getDouble("getLongitud");

        try {
            inicializarSpinnerSeekBar();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Conexion conexion = new Conexion();
                    String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/restaurants.json", "GET"/*json_parametros.toString()*/).get();
                    JSONArray datos = new JSONArray(result);
                    EditText nombre = findViewById(R.id.txtNombreNuevoRestaurante);

                    boolean existe = false;
                    for (int i = 0; i < datos.length(); i++) {
                        JSONObject elemento = datos.getJSONObject(i);

                        if (elemento.getString("name").equals(nombre.getText())) {
                            existe = true;
                        }
                    }
                    if (existe) {
                        Toast.makeText(getApplicationContext(), "Ya existe un restaurante con el mismo nombre", Toast.LENGTH_LONG).show();
                    } else if (Registro()) {
                        Toast.makeText(getApplicationContext(), "Se ha registrado con exito", Toast.LENGTH_LONG).show();
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Error al registrar el restaurante", Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    private void inicializarSpinnerSeekBar() throws ExecutionException, InterruptedException, JSONException {
        //Precio
        final SeekBar costoSeekbar = findViewById(R.id.seekCosto);
        costoSeekbar.setMax(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            costoSeekbar.setMin(1);
        }
        costoSeekbar.setProgress(1);

        costoSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                actualizarCosto(i);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
        });

        //Tipos comida
        Conexion conexion = new Conexion();
        String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/foods.json", "GET"/*json_parametros.toString()*/).get();
        JSONArray datos = new JSONArray(result);

        spinnerArray =  new ArrayList<String>();

        for (int i = 0; i < datos.length(); i++) {
            JSONObject elemento = datos.getJSONObject(i);
            spinnerArray.add(elemento.getString("foodtype"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spTipoComida);
        sItems.setAdapter(adapter);
    }

    private void actualizarCosto(int i) {
        TextView txtCosto= findViewById(R.id.lbCostoValor);
        if(i==1)
            txtCosto.setText("Barato");
        else if(i==2)
            txtCosto.setText("Medio");
        else if(i==3)
            txtCosto.setText("Caro");
        else
            txtCosto.setText("Error");
    }

    private boolean Registro() throws ExecutionException, InterruptedException, JSONException {
        EditText nombre= findViewById(R.id.txtNombreNuevoRestaurante);
        EditText correo= findViewById(R.id.txtCorreo);
        EditText telefono= findViewById(R.id.txtTelefono);
        EditText horario= findViewById(R.id.txtHorario);
        TextView precio= findViewById(R.id.lbCostoValor);

        String nombreRes = nombre.getText().toString();
        String correoRes = correo.getText().toString();
        String telefonoRes = telefono.getText().toString();
        String horarioRes = horario.getText().toString();
        String precioRes = precio.getText().toString();

        int i=1;
        String tipoComidaRes="";
        for(String tipo : spinnerArray ){
            if(tipo.equals(sItems.getSelectedItem().toString()))
                tipoComidaRes = String.valueOf(i);
            i++;
        }


        if(!nombreRes.isEmpty() && !correoRes.isEmpty() && !telefonoRes.isEmpty() && !horarioRes.isEmpty() && !precioRes.isEmpty()){
            Conexion conexion = new Conexion();
            JSONObject json_parametros = new JSONObject();
            json_parametros.put("name",nombreRes);
            json_parametros.put("longitude",latitud);
            json_parametros.put("latitude",logitud);
            json_parametros.put("schedules",horarioRes);
            json_parametros.put("phones_number",telefonoRes);
            json_parametros.put("email",correoRes);
            json_parametros.put("price",precioRes);
            json_parametros.put("food_id",tipoComidaRes);
            String datos="{\"restaurant\":"+json_parametros.toString()+"}";
            String  result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/restaurants","POST",datos/*json_parametros.toString()*/).get();

            if(result.equals("Created")) {
                return true;
            }
            else{
                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    public void onBtnCambiarImagen(View view) {
        agregarImagen();
    }

    public void agregarImagen(){
        if(Build.VERSION.SDK_INT >=23) {
            if (checkPermission()){
                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

            }else{
                requestPermission();
            }
        }else{
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

        }
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
                    agregarImagen();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            Toast.makeText(this, "No se puede acceder a la imagenes sin permisos", Toast.LENGTH_SHORT).show();
                        } else {
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
            case 564645/*MY_CAMERA_PERMISSION_CODE*/: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TomarFoto();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                            Toast.makeText(this, "No se puede tomar fotos sin permisos", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(this)
                                    .setTitle("Acceso denegado")
                                    .setMessage("No se puede tomar fotos sin permisos. Por favor active los permisos en la configuración de la aplicación.")
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri filePath = data.getData();
                ClipData imagenes=data.getClipData();
                if (null != filePath) {
                    try {
                        path_imagenes.add(getFilePath(this, filePath));
                        imagenesUri.add(filePath);
                        actualizarImagenesSlider(imagenesUri);
                        Log.d("PATH", filePath.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                if (null != imagenes) {
                    try {
                        List<String> pathimagenes=new ArrayList<>();
                        for(int i=0; i<imagenes.getItemCount();i++){
                            ClipData.Item imagen=imagenes.getItemAt(i);
                            Uri uri=imagen.getUri();
                            pathimagenes.add(getFilePath(this,uri));
                            imagenesUri.add(uri);

                        }

                        path_imagenes = pathimagenes;

                        actualizarImagenesSlider(imagenesUri);

                        //ImageView imgUsuario = findViewById(R.id.);
                        //imgUsuario.setImageURI(filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == 6416546/*TAKE_PICTURE*/) {
                if (resultCode != 0) {
                    Bundle extras = data.getExtras();
                    if (null != extras) {
                        //ImageView fotoUsuario = findViewById(R.id.imagenUsuario);
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        //path_portada = getFilePath(this, );
                        //fotoUsuario.setImageBitmap(imageBitmap);
                    }
                }
            }
        }
    }

    private void actualizarImagenesSlider(ArrayList<Uri> imagenesUri) {
        final ViewPager mPager;
        final int[] currentPage = {0};
        final int NUM_PAGES = imagenesUri.size();
        mPager = (ViewPager) findViewById(R.id.pager);


        mPager.setAdapter(new CustomSliderAdapter(Registro_Restaurante.this,imagenesUri));


        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage[0] == NUM_PAGES) {
                    currentPage[0] = 0;
                }
                mPager.setCurrentItem(currentPage[0]++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage[0] = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

}
