package com.example.restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class detalleRestaurante extends Fragment {
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_restaurante, container, false);

        String Nombre=new String();
        String NombreUsuario=new String();
        String Horario=new String();
        String Correo=new String();
        Double Latitud=new Double(0);
        Double Longitud=new Double(0);
        String Precio=new String();
        String Telefono=new String();
        String TipoComida=new String();
        String IdUsuario=new String();
        String IdRestaurante=new String();

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

        //Button btnAgregarImg=rootView.findViewById(R.id.btnAgregarImg);
        Button btnVerMapa=rootView.findViewById(R.id.btnVerMapa);

        lbNombre.setText(Nombre);
        String[] result = Horario.split(",");
        String horario="";
        for(String line : result){
            horario=horario+line+"\n";
        }
        lbHorario.setText(horario);
        lbCorreo.setText(Correo);
        lbPrecio.setText(Precio);
        lbTelefono.setText(Telefono);

        String tipoComida="";
        String calificacion="";
        try {
            tipoComida=ObtenerTipoComida(TipoComida);
            calificacion=ObtenerCalificacion(IdUsuario,IdRestaurante);
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
                MostrarRestauranteMapa(finalLatitud,finalLongitud, finalIdUsuario, finalNombreUsuario);
            }
        });

        return rootView;
    }

    private void MostrarRestauranteMapa(double Latitud, double Longitud, String IdUsuario, String NombreUsuario){
        Intent mapa= new Intent(getContext(),RestauranteMapa.class);
        mapa.putExtra("getLatitud",Latitud);
        mapa.putExtra("getLongitud",Longitud);
        startActivity(mapa);
    }

    private String ObtenerCalificacion(String IdUsuario, String IdRestaurante) throws JSONException, ExecutionException, InterruptedException {
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



}
