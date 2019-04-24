package com.example.restaurantes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Fragment_Busca extends Fragment {
    View rootView;

    static String NombreUsuario,IdUsuario;

    String Distancia=null,Precio=null,ClaveBusqueda=null,Calificacion=null, TipoComida=null;

    public static Fragment_Busca newInstance(String nombreUsuario,String idUsuario) {
        Fragment_Busca fragment = new Fragment_Busca();
        NombreUsuario=nombreUsuario;
        IdUsuario=idUsuario;
        return fragment;
    }

    Spinner sItems;
    List<String> spinnerArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_busca, container, false);

        inicializarSeekBars();
        try {
            inicializarSpinnerTiposComidas();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        inicializarTxtBusqueda();

        ImageView imgBusqueda = rootView.findViewById(R.id.btnBuscar);
        imgBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });


        return rootView;
    }

    private void inicializarTxtBusqueda() {
        EditText busqueda=rootView.findViewById(R.id.txtBuscar);
        busqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    buscar();
                    return true;
                }
                return false;
            }
        });
    }

    private void inicializarSpinnerTiposComidas() throws JSONException, ExecutionException, InterruptedException {
        //Tipos comida
        Conexion conexion = new Conexion();
        String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/foods.json", "GET"/*json_parametros.toString()*/).get();
        JSONArray datos = new JSONArray(result);

        spinnerArray =  new ArrayList<String>();

        spinnerArray.add("Todos los tipos");

        for (int i = 0; i < datos.length(); i++) {
            JSONObject elemento = datos.getJSONObject(i);
            spinnerArray.add(elemento.getString("foodtype"));
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) rootView.findViewById(R.id.spTipoComida);
        sItems.setAdapter(adapter);


        //TODO cambiar tipo comida
        //TipoComida
    }

    public void inicializarSeekBars(){
        //Distancia de busqueda
        final SeekBar distaciaSeekbar = rootView.findViewById(R.id.seekDistanciaBusqueda);
        distaciaSeekbar.setMax(10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            distaciaSeekbar.setMin(1);
        }
        distaciaSeekbar.setProgress(10);
        actualizarDistancia(10);

        distaciaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                actualizarDistancia(i);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
        });


        //Costo de busqueda
        final SeekBar costoSeekbar = rootView.findViewById(R.id.seekCosto);
        costoSeekbar.setMax(4);
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

        //Costo de estrellas
        final SeekBar estrellasSeekbar = rootView.findViewById(R.id.seekEstrellas);
        estrellasSeekbar.setMax(6);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            estrellasSeekbar.setMin(1);
        }
        estrellasSeekbar.setProgress(6);
        actualizarEstrellas(6);

        estrellasSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                actualizarEstrellas(i);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
        });
    }

    private void actualizarEstrellas(int i) {
        TextView txtEstrellas = rootView.findViewById(R.id.lbEstrellaValor);
        if(i==6)
            txtEstrellas.setText("Todas");
        else
            txtEstrellas.setText(Integer.toString(i));
    }

    private void actualizarCosto(int i) {
        TextView txtCosto= rootView.findViewById(R.id.lbCostoValor);
        if(i==1)
            txtCosto.setText("Todos los precios");
        else if(i==2)
            txtCosto.setText("Barato");
        else if(i==3)
            txtCosto.setText("Medio");
        else if(i==4)
            txtCosto.setText("Caro");
        else
            txtCosto.setText("Error");
    }

    private void actualizarDistancia(int i){
        TextView txtDistacia= rootView.findViewById(R.id.lbDistaciaValor);
        if(i==10)
            txtDistacia.setText("Todo el mundo");
        else
            txtDistacia.setText(Integer.toString(i) + " KM");
    }

    public void buscar(){
        Intent i =new Intent(getContext(),ResultadoBusqueda.class);
        i.putExtra("getNombreUsuario",NombreUsuario);
        i.putExtra("getidUsuario",IdUsuario);
        i.putExtra("getIdTipoComida",TipoComida);
        i.putExtra("getClaveBusqueda",ClaveBusqueda);
        i.putExtra("getDistacia",Distancia);
        i.putExtra("getEstrellas",Calificacion);
        i.putExtra("getPrecio",Precio);
        startActivity(i);
    }

}
