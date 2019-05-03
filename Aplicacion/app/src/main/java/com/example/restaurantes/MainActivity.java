package com.example.restaurantes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Fragment fragment_nuevo;
    String CorreoUsuario,NombreUsuario,IdUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i=getIntent();
        CorreoUsuario = i.getExtras().getString("Correo");
        IdUsuario = i.getExtras().getString("idUsuario");
        NombreUsuario="";

        Conexion conexion = new Conexion();
        try {
            String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();
            NombreUsuario=User(result,CorreoUsuario);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        funcionalidadMenu();
    }

    private String User(String jsonDatos, String correo) throws Exception {
        JSONArray datos = new JSONArray(jsonDatos);
        Crypto crypto=new Crypto();

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("email").equals(correo)){
                return elemento.getString("name");
            }
        }
        return "";
    }


    public void llamarMapaUbicacion(double Latitud, double Longitud, String IdUsuario, String NombreUsuario){
        BottomNavigationView mainNav=findViewById(R.id.main_nav);

        mainNav.setItemBackgroundResource(R.color.colorMapa);
        fragment_nuevo = new Fragment_Mapa().newInstance(Latitud,Longitud,NombreUsuario,IdUsuario);
        setFragment(fragment_nuevo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
        getSupportActionBar().setTitle("Mapa Restaurantes");
    }


    private void funcionalidadMenu() {
        final BottomNavigationView mainNav=findViewById(R.id.main_nav);

        fragment_nuevo = new Fragment_Mapa().newInstance(NombreUsuario,IdUsuario);
        setFragment(fragment_nuevo);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_mapa:
                        mainNav.setItemBackgroundResource(R.color.colorMapa);
                        fragment_nuevo = new Fragment_Mapa().newInstance(NombreUsuario,IdUsuario);
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorMapa)));
                        getSupportActionBar().setTitle("Mapa Restaurantes");
                        return true;

                    case R.id.nav_lista:
                        mainNav.setItemBackgroundResource(R.color.colorLista);
                        fragment_nuevo = new Fragment_Lista().newInstance(NombreUsuario,IdUsuario);
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorLista)));
                        getSupportActionBar().setTitle("Lista Restaurantes");
                        return true;

                    case R.id.nav_busca:
                        mainNav.setItemBackgroundResource(R.color.colorBusqueda);
                       Ubicacion ubicacion=new Ubicacion();
                       Location location=ubicacion.obtenerUbicacionActual();
                        fragment_nuevo = new Fragment_Busca().newInstance(NombreUsuario,IdUsuario,location.getLatitude(),location.getLongitude());
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBusqueda)));
                        getSupportActionBar().setTitle("Búsqueda Restaurantes");
                        return true;

                    case R.id.nav_perfil:
                        mainNav.setItemBackgroundResource(R.color.colorPerfil);
                        fragment_nuevo = new Fragment_Perfil().newInstance(NombreUsuario,CorreoUsuario);
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setTitle("Perfil");
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPerfil)));

                        return true;
                    case R.id.nav_acerca_de:
                        mainNav.setItemBackgroundResource(R.color.colorAcercaDe);
                        fragment_nuevo = new Fragment_Acerca_De();
                        setFragment(fragment_nuevo);
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorAcercaDe)));
                        getSupportActionBar().setTitle("Acerca de");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private class Ubicacion implements LocationListener {
        public Ubicacion() {
            super();
        }

        public Location obtenerUbicacionActual() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission
                        (getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        &&
                        ActivityCompat.checkSelfPermission
                                (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Debe aceptar el permiso para poder utilizar todas las funcionalidades", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    }, 1); // 1 is requestCode
                    return null;
                } else {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            return null;
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {

                case 1:
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permiso aceptado", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void setFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTran = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment);
        fragmentTran.commitNow();
    }
}
