package com.example.restaurantes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RestauranteMapa extends AppCompatActivity {

    MapView mMapView;
    Double Latitud,Longitud;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurante_mapa);


        mMapView = (MapView) findViewById(R.id.mapView2);
        mMapView.onCreate(savedInstanceState);

        Intent i=getIntent();
        Latitud = i.getExtras().getDouble("getLatitud");
        Longitud = i.getExtras().getDouble("getLongitud");


        funcionalidadMapa();
    }


    private void funcionalidadMapa() {
     final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



        mMapView.onResume(); // needed to get the map to display immediately

        try {
        MapsInitializer.initialize(this);
    } catch (Exception e) {
        e.printStackTrace();
    }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (googleMap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission
                                (getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                &&
                                ActivityCompat.checkSelfPermission
                                        (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Debe aceptar el permiso para ver las ubicaciones en el mapa", Toast.LENGTH_LONG).show();
                            requestPermissions(new String[]{
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            }, 1); // 1 is requestCode
                            return;
                        } else {
                             LocationListener locationListener = new LocationListener() {
                                @Override
                                public void onLocationChanged(Location location) {
                                }

                                @Override
                                public void onStatusChanged(String s, int i, Bundle bundle) {
                                }

                                @Override
                                public void onProviderEnabled(String s) {
                                }

                                @Override
                                public void onProviderDisabled(String s) {
                                }
                            };

                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            googleMap.clear();

                            // For showing a move to my location button
                            googleMap.setMyLocationEnabled(true);




                            LatLng location;

                            location = new LatLng(Latitud, Longitud);

                            googleMap.addMarker(new MarkerOptions().position(location).title("Restaurante"));


                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }
                }
            }
        });
    }
}



