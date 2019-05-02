package com.example.restaurantes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Fragment_Perfil extends Fragment {
    View rootView;
    static String NombreUsuario,Correo;

    public static Fragment_Perfil newInstance(String nombreUsuario,String correo) {
        Fragment_Perfil fragment = new Fragment_Perfil();
        NombreUsuario=nombreUsuario;
        Correo=correo;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_perfil, container, false);
        TextView nombre=rootView.findViewById(R.id.txtNombre);
        TextView correo=rootView.findViewById(R.id.txtCorreo);

        Conexion conexion = new Conexion();
        String resultRestaurantes = null;
        JSONArray datos = null;
        String path_imagen="";
        try {
            resultRestaurantes = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();
            datos = new JSONArray(resultRestaurantes);

            for(int i = 0; i < datos.length(); i++){
                JSONObject elemento = datos.getJSONObject(i);

                if(elemento.getString("email").equals(Correo))
                    path_imagen=elemento.getString("url_imagen");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView fotoUsuario=rootView.findViewById(R.id.imagenUsuario);
        if(!path_imagen.isEmpty())
            Picasso.with(this.getContext()).load(path_imagen).into(fotoUsuario);

        nombre.setText(NombreUsuario);
        correo.setText(Correo);

        return rootView;
    }
}
