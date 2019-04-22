package com.example.restaurantes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fragment_Mapa extends Fragment {
    View rootView;

    MapView mMapView;
    private GoogleMap googleMap;
    LocationManager locationManager;
    LocationListener locationListener;


    public static Fragment_Mapa newInstance(/*TODO Recibir datos necesarios*/) {
        Fragment_Mapa fragment = new Fragment_Mapa();
        // TODO recibir datos necesarios
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_mapa, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        funcionalidadMapa();

        return rootView;
    }

    private void funcionalidadMapa() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission
                            (getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission
                                    (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Debe aceptar el permiso para ver las ubicaciones en el mapa", Toast.LENGTH_LONG).show();
                        requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1); // 1 is requestCode
                        return;
                    } else {
                        locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Usted está acá"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
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

                        LatLng location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        //googleMap.addMarker(new MarkerOptions().position(location).title("Usted está acá"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(15).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(-34, 151);
                //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                //Crear marcador onTouch
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(point));

                    }
                });

                //Poder hacer click en marcadores para nuevo restaurante o detalle de restaurante
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        return clickEnMarcadorMapa(marker);
                    }
                });
            }
        });
    }

    private boolean clickEnMarcadorMapa(final Marker marker) {
        if (marker.getTag() == "BD") {
            //TODO abrir detalle del restaurante
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Agregar nuevo restaurante")
                    .setMessage("¿Desea agregar un nueva restaurante en la ubicación actual?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Todo abrir ventana para agregar restaurtante
                            //Todo pasar ubicacion actual
                            Toast.makeText(getContext(),"- WIP - ",Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();//*/
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Permiso aceptado", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
