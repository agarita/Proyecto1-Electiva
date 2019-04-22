package com.example.restaurantes;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Fragment_Busca extends Fragment {
    View rootView;

    public static Fragment_Busca newInstance() {
        Fragment_Busca fragment = new Fragment_Busca();
        // TODO recibir datos necesarios
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_busca, container, false);

        inicializarSeekBars();

        ImageView imgBusqueda = rootView.findViewById(R.id.btnBuscar);
        imgBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });


        return rootView;
    }

    public void inicializarSeekBars(){
        //Distancia de busqueda
        final SeekBar distaciaSeekbar = rootView.findViewById(R.id.seekDistanciaBusqueda);
        distaciaSeekbar.setMax(10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            distaciaSeekbar.setMin(1);
        }
        distaciaSeekbar.setProgress(1);


        distaciaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                actualizarDistancia(i);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

        });


        //Costo de busqueda
        final SeekBar costoSeekbar = rootView.findViewById(R.id.seekCosto);
        costoSeekbar.setMax(3);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            distaciaSeekbar.setMin(1);
        }
        distaciaSeekbar.setProgress(1);


        costoSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                actualizarCosto(i);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

        });
    }

    private void actualizarCosto(int i) {
        TextView txtCosto= rootView.findViewById(R.id.lbCostoValor);
        if(i==0)
            txtCosto.setText("Todos los precios");
        else if(i==1)
            txtCosto.setText("Barato");
        else if(i==2)
            txtCosto.setText("Medio");
        else if(i==3)
            txtCosto.setText("Caro");
        else
            txtCosto.setText("Error");
    }

    private void actualizarDistancia(int i){
        TextView txtDistacia= rootView.findViewById(R.id.lbDistaciaValor);
        txtDistacia.setText(Integer.toString(i) + " KM");
    }

    public void buscar(){
        Toast.makeText(getContext(),"- WIP -",Toast.LENGTH_LONG).show();
    }

}
