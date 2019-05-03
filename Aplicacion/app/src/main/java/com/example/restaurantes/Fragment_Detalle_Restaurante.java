package com.example.restaurantes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


public class Fragment_Detalle_Restaurante extends Fragment {
    private View rootView;

    ArrayList<String> path_imagenes = new ArrayList<>();
    ArrayList<Uri> uri_imagenes = new ArrayList<Uri>();

    private static final int SELECT_PICTURE = 3513;
    private static final int PERMISSION_REQUEST_CODE = 5468;

    String IdRestaurante;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_restaurante, container, false);

        AWSMobileClient.getInstance().initialize(rootView.getContext()).execute();

        String Nombre = new String();
        String NombreUsuario = new String();
        String Horario = new String();
        String Correo = new String();
        Double Latitud = new Double(0);
        Double Longitud = new Double(0);
        String Precio = new String();
        String Telefono = new String();
        String TipoComida = new String();
        String IdUsuario = new String();
        IdRestaurante = new String();

        if (getArguments() != null) {

            Nombre = getArguments().getString("getNombre");
            Horario = getArguments().getString("getHorario");
            Correo = getArguments().getString("getCorreo");
            Latitud = getArguments().getDouble("getLatitud");
            Longitud = getArguments().getDouble("getLongitud");
            Precio = getArguments().getString("getPrecio");
            Telefono = getArguments().getString("getTelefono");
            TipoComida = getArguments().getString("getTipoComida");
            IdUsuario = getArguments().getString("getIdUsuario");
            IdRestaurante = getArguments().getString("getIdRestaurante");
            NombreUsuario = getArguments().getString("getNombreUsuario");

        }

        //ImageView imgFoto = rootView.findViewById(R.id.imgRestaurante);
        TextView lbNombre = rootView.findViewById(R.id.lblNombreRest);
        TextView lbHorario = rootView.findViewById(R.id.txtHorario);
        TextView lbCorreo = rootView.findViewById(R.id.txtCorreo);
        TextView lbPrecio = rootView.findViewById(R.id.txtPrecio);
        TextView lbTelefono = rootView.findViewById(R.id.txtTelefono);
        TextView lbTipoComida = rootView.findViewById(R.id.txtTipoComida);
        TextView lbCalificacion = rootView.findViewById(R.id.txtCalificacion);

        Button btnCambiarImg = rootView.findViewById(R.id.btnCambiarImg);
        Button btnVerMapa = rootView.findViewById(R.id.btnVerMapa);

        lbNombre.setText(Nombre);
        String[] result = Horario.split(",");
        String horario = "";
        for (String line : result) {
            horario = horario + line + "\n";
        }
        lbHorario.setText(horario);
        lbCorreo.setText(Correo);
        lbPrecio.setText(Precio);
        lbTelefono.setText(Telefono);

        String tipoComida = "";
        String calificacion = "";
        try {
            tipoComida = ObtenerTipoComida(TipoComida);
            calificacion = ObtenerCalificacion(IdRestaurante);
            descargarImagenesRestaurante(IdRestaurante);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lbTipoComida.setText(tipoComida);
        lbCalificacion.setText(calificacion);


        final Double finalLatitud = Latitud;
        final Double finalLongitud = Longitud;
        final String finalNombreUsuario = NombreUsuario;
        final String finalIdUsuario = IdUsuario;
        btnVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MostrarRestauranteMapa(finalLatitud, finalLongitud, finalIdUsuario, finalNombreUsuario);
            }
        });

        btnCambiarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarImagen();
            }
        });

        return rootView;
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
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE );
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
                            Toast.makeText(getContext(), "No se puede acceder a la imagenes sin permisos", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(getContext())
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
                            Toast.makeText(getContext(), "No se puede tomar fotos sin permisos", Toast.LENGTH_SHORT).show();
                        } else {
                            new AlertDialog.Builder(getContext())
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
                        path_imagenes.add(getFilePath(getContext(), filePath));
                        uri_imagenes.add(filePath);
                        //actualizarImagenesSlider(uri_imagenes,null);
                        subirImagenes();
                        Log.d("PATH", filePath.getPath());
                        descargarImagenesRestaurante(IdRestaurante);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                if (null != imagenes) {
                    try {
                        for(int i=0; i<imagenes.getItemCount();i++){
                            ClipData.Item imagen=imagenes.getItemAt(i);
                            Uri uri=imagen.getUri();
                            path_imagenes.add(getFilePath(getContext(),uri));
                            uri_imagenes.add(uri);

                        }
                        subirImagenes();

                        //actualizarImagenesSlider(uri_imagenes,null);
                        descargarImagenesRestaurante(IdRestaurante);

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

    private void subirImagenes() throws JSONException, ExecutionException, InterruptedException {
        Toast.makeText(getContext(),"Subiendo imagenes...",Toast.LENGTH_LONG).show();

        boolean exito=true;
        int cont=0;
        for(String path : path_imagenes) {
            Conexion conexion = new Conexion();
            JSONObject json_parametros = new JSONObject();

            String nombreFoto = getYear() + getMonth() + getDay() + getHour() + getMinute() + getSecond()+cont;
            uploadImageS3(nombreFoto.replaceAll("\\s", ""),path);
            String urlImagen = "https://s3.us-east-2.amazonaws.com/apprestaurantes-userfiles-mobilehub-1898934645/restaurantes/" + nombreFoto.replaceAll("\\s", "") + ".jpg";

            json_parametros.put("url_resimage", urlImagen);
            json_parametros.put("restaurant_id", IdRestaurante);
            String datos1 = "{\"resimage\":" + json_parametros.toString() + "}";
            String result1 = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/resimages", "POST", datos1/*json_parametros.toString()*/).get();

            cont++;

            if(!result1.equals("Created"))
                exito=false;
        }

        path_imagenes=new ArrayList<String>();

        if(exito)
            Toast.makeText(getContext(),"Imagenes guardadas",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(),"Error al guardar imagenes ",Toast.LENGTH_LONG).show();
    }

    private void descargarImagenesRestaurante(String idRestaurante) throws ExecutionException, InterruptedException, JSONException {
        Conexion conexion = new Conexion();
        String resultRestaurantes = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/resimages.json", "GET").get();
        JSONArray datos = new JSONArray(resultRestaurantes);

        ArrayList<String> urls=new ArrayList<>();
        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("restaurant_id").equals(idRestaurante))
                urls.add(elemento.getString("url_resimage"));
        }

        actualizarImagenesSlider(null,urls);
    }

    private void actualizarImagenesSlider(@Nullable ArrayList<Uri> imagenesUri, @Nullable ArrayList<String> pathImagenes) {
        final ViewPager mPager;
        final int[] currentPage = {0};
        final int NUM_PAGES;
        if(pathImagenes!=null)
            NUM_PAGES = pathImagenes.size();
        else
            NUM_PAGES = imagenesUri.size();
        mPager = (ViewPager) rootView.findViewById(R.id.pager);


        mPager.setAdapter(new CustomSliderAdapter(getContext(),imagenesUri,pathImagenes));


        CirclePageIndicator indicator = (CirclePageIndicator)
                rootView.findViewById(R.id.indicator);

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

    private void MostrarRestauranteMapa(double Latitud, double Longitud, String IdUsuario, String NombreUsuario){
        Intent mapa= new Intent(getContext(),RestauranteMapa.class);
        mapa.putExtra("getLatitud",Latitud);
        mapa.putExtra("getLongitud",Longitud);
        startActivity(mapa);
    }

    private String ObtenerCalificacion(String IdRestaurante) throws JSONException, ExecutionException, InterruptedException {
        Conexion conexion = new Conexion();
        String resultBD = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/ratings.json", "GET").get();

        JSONArray datos = new JSONArray(resultBD);

        int cont=0;
        float promedio=0;

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("restaurant_id").equals(IdRestaurante)){
                promedio=promedio+elemento.getInt("star");
                cont=cont+1;
            }
        }

        if(cont!=0)
            promedio=promedio/cont;

        return String.valueOf(promedio);
    }



    private String ObtenerTipoComida(String tipoComida) throws ExecutionException, InterruptedException, JSONException {
        Conexion conexion = new Conexion();
        String resultBD = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/foods.json", "GET").get();

        JSONArray datos = new JSONArray(resultBD);

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("id").equals(tipoComida)){
                return elemento.getString("foodtype");
            }
        }
        return "";
    }

    //------- Agregar menu de opciones --------
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_opciones_informacion_restaurante, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            }  else if (isMediaDocument(uri)) {
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


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    private void uploadImageS3(String nombreImagen,String path_imagen){
        //Agregar el keypublico y local cuando se vaya a correr, borrarlo cuando se vaya a subir a github
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAZAY5RRIHXJIW5TPL","eQIJ+0e+2DKFKaEUIAy8XyL2Op8z1Uii7SbzZWhZ");
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver uploadObserver =
                transferUtility.upload("restaurantes/" + nombreImagen + ".jpg", new File(path_imagen));

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.progress_bar);
        dialog.setTitle("State");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        final ProgressBar progressBar= dialog.findViewById(R.id.progreso_img);

        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

        //dialog.show();

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    //dialog.dismiss();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                //progressBar.setProgress(percentDone);
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(getContext(),"Error al subir la imagen",Toast.LENGTH_LONG).show();
            }

        });

        // If your upload does not trigger the onStateChanged method inside your
        // TransferListener, you can directly check the transfer state as shown here.
        if (TransferState.COMPLETED == uploadObserver.getState()) {
            // Handle a completed upload.
        }
    }


    //---------------------- Obtener Fecha ---------------------------
    private String getHour(){
        Calendar c = Calendar.getInstance();
        String Hora = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        return Hora;
    }
    private String getMinute(){
        Calendar c = Calendar.getInstance();
        String Minuto = String.valueOf(c.get(Calendar.MINUTE));
        return Minuto;
    }
    private String getDay(){
        Calendar c = Calendar.getInstance();
        String Dia = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        return Dia;
    }
    private String getMonth(){
        Calendar c = Calendar.getInstance();
        String Mes = String.valueOf(c.get(Calendar.MONTH)+1);
        return Mes;
    }
    private String getYear(){
        Calendar c = Calendar.getInstance();
        String Año =  String.valueOf(c.get(Calendar.YEAR));
        return Año;
    }
    private String getSecond(){
        Calendar c = Calendar.getInstance();
        String Segundo = String.valueOf(c.get(Calendar.SECOND));
        return Segundo;
    }


}
