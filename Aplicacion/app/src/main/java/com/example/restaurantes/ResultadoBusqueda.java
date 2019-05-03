package com.example.restaurantes;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ResultadoBusqueda extends AppCompatActivity {

    ArrayList<DatosRestaurante> restaurantesObtenidos=new ArrayList<>();

    String NombreUsuario,IdUsuario;
    String Distancia="",Precio="",ClaveBusqueda="",Calificacion="", TipoComida="";
    Double LatitudUsuario=null, LongitudUsuario=null;
    Location ubicacionActual=new Location("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lista);

        Intent i=getIntent();
        NombreUsuario = i.getExtras().getString("getNombreUsuario");
        IdUsuario = i.getExtras().getString("getidUsuario");
        TipoComida = i.getExtras().getString("getIdTipoComida");
        ClaveBusqueda = i.getExtras().getString("getClaveBusqueda");
        Distancia = i.getExtras().getString("getDistacia");
        Calificacion = i.getExtras().getString("getEstrellas");
        Precio = i.getExtras().getString("getPrecio");
        LatitudUsuario = i.getExtras().getDouble("getLatitud");
        LongitudUsuario = i.getExtras().getDouble("getLongitud");

        if(LatitudUsuario!= null && LongitudUsuario!=null) {
            ubicacionActual.setLatitude(LatitudUsuario);
            ubicacionActual.setLongitude(LongitudUsuario);
        }
        else
            if(Distancia!=null) {
                Toast.makeText(this, "No se ha podido acceder a la ubicación actual", Toast.LENGTH_LONG).show();
                Distancia=null;
            }

        Log.i("Info", String.format("TC: %s\n CB: %s\n D: %s\n C: %s\nP: %s\n",
                TipoComida, ClaveBusqueda, Distancia, Calificacion, Precio));

        try {
            restaurantesObtenidos=ObtenerDatosRestaurantes();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        restaurantesObtenidos=filtrarRestaurantes();


        if(restaurantesObtenidos!=null) {
            poblarListViewRestaurantes(restaurantesObtenidos);
        }


    }

    private ArrayList<DatosRestaurante> filtrarRestaurantes() {
        ArrayList<DatosRestaurante> restaurantes=new ArrayList<>();
        for(DatosRestaurante restaurante : restaurantesObtenidos){
            try {
                Location ubicacionRestaurante=new Location("");
                ubicacionRestaurante.setLatitude(restaurante.getLatitud());
                ubicacionRestaurante.setLongitude(restaurante.getLongitud());
                int distanciaActual=Math.round(ubicacionActual.distanceTo(ubicacionRestaurante))/1000;
                if(ClaveBusqueda==null || (restaurante.getNombre().toLowerCase().contains(ClaveBusqueda.toLowerCase()))) {
                    if (TipoComida == null || restaurante.getTipoComida().contains((TipoComida))) {
                        if(Precio == null || restaurante.getPrecio().contains(Precio)) {
                            if(Calificacion == null || ObtenerCalificacion(restaurante.getId()).contains(Calificacion)){
                                if(Distancia == null || Integer.valueOf(Distancia)>=distanciaActual)
                                    restaurantes.add(restaurante);

                            }
                        }
                    }
                }
            } catch (JSONException|ExecutionException|InterruptedException  e) {
                e.printStackTrace();
            }
        }

        return restaurantes;
    }

    private String ObtenerCalificacion(String IdRestaurante) throws JSONException, ExecutionException, InterruptedException {
        Conexion conexion = new Conexion();
        String resultBD = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/ratings.json", "GET").get();

        JSONArray datos = new JSONArray(resultBD);

        int cont=0;
        int promedio=0;

        for(int i = 0; i < datos.length(); i++){
            JSONObject elemento = datos.getJSONObject(i);
            if(elemento.getString("restaurant_id").equals(IdRestaurante)){
                promedio=promedio+elemento.getInt("star");
                cont=cont+1;
            }
        }

        if(cont!=0)
            promedio=promedio/cont;

        return String.valueOf(Math.round(promedio));
    }

    private void poblarListViewRestaurantes(final ArrayList<DatosRestaurante> restaurantesObtenidos) {

        ListView listaRestaurantes=findViewById(R.id.lstRestaurantes);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, restaurantesObtenidos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(restaurantesObtenidos.get(position).getNombre());
                text2.setText("Precio: "+restaurantesObtenidos.get(position).getPrecio());
                view.setTag(restaurantesObtenidos.get(position).getNombre());
                return view;
            }
        };
        listaRestaurantes.setAdapter(adapter);

        listaRestaurantes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(DatosRestaurante restaurante : restaurantesObtenidos){
                    if(view.getTag()==restaurante.getNombre()){
                        Intent detalleRestaurante= new Intent(getApplicationContext(), DetalleRestaurante.class);
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
                        Bundle bundle=new Bundle();
                        startActivity(detalleRestaurante);
                    }
                }
            }
        });
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
}
