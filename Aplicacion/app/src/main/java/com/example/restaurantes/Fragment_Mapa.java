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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Fragment_Mapa extends Fragment {
    View rootView;

    MapView mMapView;
    private GoogleMap googleMap;
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<DatosRestaurante> restaurantesObtenidos;
    ArrayList<Marker> marcadoresMapa = new ArrayList<>();

    static String NombreUsuario,IdUsuario;

    static Boolean LugarEspecifico=false;
    static Double Lat,Long;


    public static Fragment_Mapa newInstance(String nombreUsuario,String idUsuario) {
        Fragment_Mapa fragment = new Fragment_Mapa();
        NombreUsuario=nombreUsuario;
        IdUsuario=idUsuario;
        return fragment;
    }

    public static Fragment_Mapa newInstance(Double Latitud, Double Longitud, String nombreUsuario,String idUsuario) {
        Fragment_Mapa fragment = new Fragment_Mapa();
        NombreUsuario=nombreUsuario;
        IdUsuario=idUsuario;
        Lat=Latitud;
        Long=Longitud;
        LugarEspecifico=true;
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
                if (googleMap != null) {
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


                            //googleMap.addMarker(new MarkerOptions().position(location).title("Usted está acá"));

                            LatLng location;
                            if(LugarEspecifico)
                                location = new LatLng(Lat, Long);
                            else
                                location = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                            // For zooming automatically to the location of the marker
                            CameraPosition cameraPosition= new CameraPosition.Builder().target(location).zoom(15).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    }

                    try {
                        if (!AgregarMarcadoresRestaurantes(googleMap)) {
                            Toast.makeText(getContext(), "Error al agregar los marcadores al mapa", Toast.LENGTH_LONG).show();
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Crear marcador onTouch
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng point) {
                            googleMap.clear();
                            try {
                                AgregarMarcadoresRestaurantes(googleMap);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

                    /*googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            for(Marker marker : marcadoresMapa) {
                                if(Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.02 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.02) {
                                    marker.showInfoWindow();
                                    break;
                                }
                            }

                        }
                    });*/
                }
                else
                    Toast.makeText(getContext(), "Error no se pudo inicializar el mapa", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean AgregarMarcadoresRestaurantes(GoogleMap googleMap) throws ExecutionException, InterruptedException, JSONException {

        if(restaurantesObtenidos==null)
            restaurantesObtenidos = ObtenerDatosRestaurantes();

        if(restaurantesObtenidos==null) {
            Toast.makeText(getContext(), "Error al obtener los restaurantes", Toast.LENGTH_LONG).show();
            return false;
        }
        else {
            for (int i = 0; i < restaurantesObtenidos.size(); i++) {
                createMarker(restaurantesObtenidos.get(i).getLatitud(), restaurantesObtenidos.get(i).getLongitud(), restaurantesObtenidos.get(i).getNombre(), "Horario: " + restaurantesObtenidos.get(i).getHorario(), "BD");
            }
        }
        return true;
    }

    private ArrayList<DatosRestaurante> ObtenerDatosRestaurantes() throws ExecutionException, InterruptedException, JSONException {
        Conexion conexion = new Conexion();
        String resultRestaurantes = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/restaurants.json", "GET").get();
        JSONArray datos = new JSONArray(resultRestaurantes);

        ArrayList<DatosRestaurante> restaurantesObtenidos = new ArrayList<DatosRestaurante>();
        DatosRestaurante restaurante;

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            restaurante=new DatosRestaurante();

            restaurante.setId(elemento.getString("id"));
            restaurante.setNombre(elemento.getString("name"));
            restaurante.setLatitud(Double.valueOf(elemento.getString("latitude")));
            restaurante.setLongitud(Double.valueOf(elemento.getString("longitude")));
            restaurante.setHorario(elemento.getString("schedules"));
            restaurante.setTelefono(elemento.getString("phones_number"));
            restaurante.setCorreo(elemento.getString("email"));
            restaurante.setPrecio(elemento.getString("price"));
            restaurante.setTipoComida(elemento.getString("food_id"));
            restaurantesObtenidos.add(restaurante);
        }
        return restaurantesObtenidos;
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet, String tag) {
        Marker m= googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet));
        m.setTag(tag);
        //marcadoresMapa.add(m);
        return m;
    }

    private boolean clickEnMarcadorMapa(final Marker marker) {
        if (marker.getTag() == "BD") {
            for(DatosRestaurante restaurante : restaurantesObtenidos){
                if(marker.getPosition().latitude==restaurante.getLatitud() && marker.getPosition().longitude==restaurante.getLongitud()){
                    Intent detalleRestaurante= new Intent(getContext(),Detalle_Restaurante.class);
                    detalleRestaurante.putExtra("getNombreUsuario",NombreUsuario);
                    detalleRestaurante.putExtra("getidUsuario",IdUsuario);
                    detalleRestaurante.putExtra("getIdRestaurante",restaurante.getId());
                    detalleRestaurante.putExtra("getNombre",restaurante.getNombre());
                    detalleRestaurante.putExtra("getHorario",restaurante.getHorario());
                    detalleRestaurante.putExtra("getCorreo",restaurante.getCorreo());
                    detalleRestaurante.putExtra("getLatitud",restaurante.getLatitud());
                    detalleRestaurante.putExtra("getLongitud",restaurante.getLongitud());
                    detalleRestaurante.putExtra("getPrecio",restaurante.getPrecio());
                    detalleRestaurante.putExtra("getTelefono",restaurante.getTelefono());
                    detalleRestaurante.putExtra("getTipoComida",restaurante.getTipoComida());
                    startActivity(detalleRestaurante);
                    return true;
                }
            }
            return false;
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("Agregar nuevo restaurante")
                    .setMessage("¿Desea agregar un nueva restaurante en la ubicación actual?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i= new Intent(getContext(),Registro_Restaurante.class);
                            i.putExtra("getLatitud",marker.getPosition().latitude);
                            i.putExtra("getLongitud",marker.getPosition().longitude);
                            startActivity(i);
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
