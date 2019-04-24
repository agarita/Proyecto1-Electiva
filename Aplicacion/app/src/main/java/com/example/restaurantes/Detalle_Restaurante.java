package com.example.restaurantes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Detalle_Restaurante extends AppCompatActivity {

    private FragmentTabHost mTabHost;
    String correoUsuario, NombreUsuario, IdUsuario;
    String Nombre,Horario,Correo,Precio,Telefono,TipoComida,IdRestaurante;
    Double Latitud,Longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_restaurante);

        //Se reciben las variables necesarias
        Intent i=getIntent();
        NombreUsuario = i.getExtras().getString("getNombreUsuario");
        IdUsuario = i.getExtras().getString("getidUsuario");
        Nombre = i.getExtras().getString("getNombre");
        Horario = i.getExtras().getString("getHorario");
        Correo = i.getExtras().getString("getCorreo");
        Latitud = i.getExtras().getDouble("getLatitud");
        Longitud = i.getExtras().getDouble("getLongitud");
        Precio = i.getExtras().getString("getPrecio");
        Telefono = i.getExtras().getString("getTelefono");
        TipoComida = i.getExtras().getString("getTipoComida");
        IdRestaurante = i.getExtras().getString("getIdRestaurante");


        Bundle args1 = new Bundle();
        args1.putString("getidUsuario", IdUsuario);
        args1.putString("getIdRestaurante", IdRestaurante);

        Bundle args2 = new Bundle();
        args2.putString("getIdUsuario", IdUsuario);
        args1.putString("getNombreUsuario", NombreUsuario);
        args2.putString("getIdRestaurante", IdRestaurante);
        args2.putString("getNombre", Nombre);
        args2.putString("getHorario", Horario);
        args2.putString("getCorreo", Correo);
        args2.putDouble("getLatitud", Latitud);
        args2.putDouble("getLongitud", Longitud);
        args2.putString("getPrecio", Precio);
        args2.putString("getTelefono", Telefono);
        args2.putString("getTipoComida", TipoComida);

        android.support.v7.app.ActionBar actionBar = this.getSupportActionBar();

        //Poner de título
        actionBar.setTitle(Nombre);

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contenido_tab);

        mTabHost.addTab(mTabHost.newTabSpec("Información").setIndicator("Información"),
                detalleRestaurante.class, args2);
        mTabHost.addTab(mTabHost.newTabSpec("Comentarios").setIndicator("Comentarios"),
                Comentarios.class, args1);
    }


    private void Calificar(CharSequence Calificacion)throws JSONException, ExecutionException, InterruptedException {
        int calificacion=Integer.parseInt(Calificacion.toString());



        JSONObject json_parametros = new JSONObject();
        json_parametros.put("star", calificacion);
        json_parametros.put("restaurant_id",IdRestaurante );
        json_parametros.put("user_id", IdUsuario);

        String datos="{\"rating\":"+json_parametros.toString()+"}";

        Conexion conexion = new Conexion();
        String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/ratings.json", "POST", datos).get();

        if(result.equals("Created")) {
            Toast.makeText(this, "Se califico exitosamente el restaurante.", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Ocurrió un error inesperado."+result.toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(this, json_parametros.toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.agregarCalificacion: {
                final CharSequence calificaciones[] = new CharSequence[]{"1", "2", "3", "4", "5"};
                try {
                    Conexion conexion = new Conexion();
                    String resultBD = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/ratings.json", "GET").get();
                    JSONArray datos = new JSONArray(resultBD);

                    boolean existe=false;

                    for(int i = 0; i < datos.length(); i++){
                        JSONObject elemento = datos.getJSONObject(i);
                        if(elemento.getString("restaurant_id").equals(IdRestaurante)&& elemento.getString("user_id").equals(IdUsuario)){
                            existe=true;
                        }
                    }

                    if(existe)
                        Toast.makeText(getApplicationContext(),"Ya se ha registrado la calificación para este restaurante",Toast.LENGTH_LONG).show();
                    else
                        new AlertDialog.Builder(this)
                                .setItems(calificaciones, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // el usuario selecciona calificaciones[which]
                                        try {
                                            Calificar(calificaciones[which]);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .show();//*/

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case android.R.id.home: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
