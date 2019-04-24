package com.example.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Fragment_Lista extends Fragment {
    View rootView;
    ArrayList<DatosRestaurante> restaurantesObtenidos=new ArrayList<>();

    static String NombreUsuario,IdUsuario;

    public static Fragment_Lista newInstance(String nombreUsuario,String idUsuario) {
        Fragment_Lista fragment = new Fragment_Lista();
        NombreUsuario=nombreUsuario;
        IdUsuario=idUsuario;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_lista, container, false);

        try {
            restaurantesObtenidos=ObtenerDatosRestaurantes();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(restaurantesObtenidos!=null) {
            poblarListViewRestaurantes(restaurantesObtenidos);
        }

        return rootView;
    }

    private void poblarListViewRestaurantes(final ArrayList<DatosRestaurante> restaurantesObtenidos) {

        ListView listaRestaurantes=rootView.findViewById(R.id.lstRestaurantes);

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, restaurantesObtenidos) {
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
