package com.example.restaurantes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Registro_Restaurante extends AppCompatActivity {

    Double latitud,logitud;
    Spinner sItems;
    List<String> spinnerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_restaurante);
        Button btnRegistrar = findViewById(R.id.btnRegistrarRestaurante);

        Intent i = getIntent();
        latitud = i.getExtras().getDouble("getLatitud");
        logitud = i.getExtras().getDouble("getLongitud");

        try {
            inicializarSpinnerSeekBar();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Conexion conexion = new Conexion();
                    String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/restaurants.json", "GET"/*json_parametros.toString()*/).get();
                    JSONArray datos = new JSONArray(result);
                    EditText nombre = findViewById(R.id.txtNombreNuevoRestaurante);

                    boolean existe = false;
                    for (int i = 0; i < datos.length(); i++) {
                        JSONObject elemento = datos.getJSONObject(i);

                        if (elemento.getString("name").equals(nombre.getText())) {
                            existe = true;
                        }
                    }
                    if (existe) {
                        Toast.makeText(getApplicationContext(), "Ya existe un restaurante con el mismo nombre", Toast.LENGTH_LONG).show();
                    } else if (Registro()) {
                        Toast.makeText(getApplicationContext(), "Se ha registrado con exito", Toast.LENGTH_LONG).show();
                        finish();
                    } else
                        Toast.makeText(getApplicationContext(), "Error al registrar el restaurante", Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    private void inicializarSpinnerSeekBar() throws ExecutionException, InterruptedException, JSONException {
        //Precio
        final SeekBar costoSeekbar = findViewById(R.id.seekCosto);
        costoSeekbar.setMax(3);
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

        //Tipos comida
        Conexion conexion = new Conexion();
        String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/foods.json", "GET"/*json_parametros.toString()*/).get();
        JSONArray datos = new JSONArray(result);

        spinnerArray =  new ArrayList<String>();

        for (int i = 0; i < datos.length(); i++) {
            JSONObject elemento = datos.getJSONObject(i);
            spinnerArray.add(elemento.getString("foodtype"));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sItems = (Spinner) findViewById(R.id.spTipoComida);
        sItems.setAdapter(adapter);
    }

    private void actualizarCosto(int i) {
        TextView txtCosto= findViewById(R.id.lbCostoValor);
        if(i==1)
            txtCosto.setText("Barato");
        else if(i==2)
            txtCosto.setText("Medio");
        else if(i==3)
            txtCosto.setText("Caro");
        else
            txtCosto.setText("Error");
    }

    private boolean Registro() throws ExecutionException, InterruptedException, JSONException {
        EditText nombre= findViewById(R.id.txtNombreNuevoRestaurante);
        EditText correo= findViewById(R.id.txtCorreo);
        EditText telefono= findViewById(R.id.txtTelefono);
        EditText horario= findViewById(R.id.txtHorario);
        TextView precio= findViewById(R.id.lbCostoValor);

        String nombreRes = nombre.getText().toString();
        String correoRes = correo.getText().toString();
        String telefonoRes = telefono.getText().toString();
        String horarioRes = horario.getText().toString();
        String precioRes = precio.getText().toString();

        int i=1;
        String tipoComidaRes="";
        for(String tipo : spinnerArray ){
            if(tipo.equals(sItems.getSelectedItem().toString()))
                tipoComidaRes = String.valueOf(i);
            i++;
        }


        if(!nombreRes.isEmpty() && !correoRes.isEmpty() && !telefonoRes.isEmpty() && !horarioRes.isEmpty() && !precioRes.isEmpty()){
            Conexion conexion = new Conexion();
            JSONObject json_parametros = new JSONObject();
            json_parametros.put("name",nombreRes);
            json_parametros.put("longitude",latitud);
            json_parametros.put("latitude",logitud);
            json_parametros.put("schedules",horarioRes);
            json_parametros.put("phones_number",telefonoRes);
            json_parametros.put("email",correoRes);
            json_parametros.put("price",precioRes);
            json_parametros.put("food_id",tipoComidaRes);
            String datos="{\"restaurant\":"+json_parametros.toString()+"}";
            String  result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/restaurants","POST",datos/*json_parametros.toString()*/).get();

            if(result.equals("Created")) {
                return true;
            }
            else{
                Toast.makeText(this,result,Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }

    /*public void onBtnCambiarImagen(View view) {
    }*/

}
