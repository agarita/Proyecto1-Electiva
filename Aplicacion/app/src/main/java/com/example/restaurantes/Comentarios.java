package com.example.restaurantes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class Comentarios extends Fragment {
    private View rootView;
    String id_usuario, id_restaurante;
    JSONArray TodosComentarios;
    JSONArray TodosUsuarios;
    EditText Comentario;
    TextView labelNoComentarios;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_comentarios, container, false);

        if (getArguments() != null) {
            id_usuario=getArguments().getString("getidUsuario");
            id_restaurante=getArguments().getString("getIdRestaurante");
        }

        Button btnAgregarComentario=rootView.findViewById(R.id.btnEnviar);
        Comentario=rootView.findViewById(R.id.txtComentario);
        labelNoComentarios=rootView.findViewById(R.id.lbNoComentarios);

        btnAgregarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AgregarComentario();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ActualizarComentarios();
        return rootView;
    }

    private void Actualizar_Datos() {
        try {
            Conexion comentariosConexion = new Conexion();
            String result = comentariosConexion.execute("https://shrouded-savannah-17544.herokuapp.com/feedbacks.json", "GET").get();
            TodosComentarios = new JSONArray(result);

            Conexion user_extendeds = new Conexion();
            String result1 = user_extendeds.execute("https://shrouded-savannah-17544.herokuapp.com/users.json", "GET").get();
            TodosUsuarios = new JSONArray(result1);
        } catch (InterruptedException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ActualizarComentarios() {
        Actualizar_Datos();

        JSONArray datosComentarios = TodosComentarios;
        JSONArray datosUsuarios = TodosUsuarios;

        JSONArray comentariosFiltrados = new JSONArray();

        try {
            if (datosComentarios !=null) {
                if (datosComentarios.length() > 0) {

                    for (int i = 0; i < datosComentarios.length(); i++) {
                        JSONObject elemento = datosComentarios.getJSONObject(i);
                        if (elemento.getString("restaurant_id").equals(id_restaurante)) {
                            comentariosFiltrados.put(elemento);
                        }
                    }

                    if (comentariosFiltrados != null) {
                        if (comentariosFiltrados.length() > 0) {
                            labelNoComentarios.setVisibility(View.INVISIBLE);

                            final ArrayList<String> nicks = new ArrayList<>();
                            final ArrayList<String> comentarios = new ArrayList<>();

                            JSONObject elemento;
                            for (int i = 0; i < comentariosFiltrados.length(); i++) {
                                elemento = comentariosFiltrados.getJSONObject(i);

                                JSONObject usuario;
                                for (int k = 0; k < datosUsuarios.length(); k++) {
                                    usuario = datosUsuarios.getJSONObject(k);
                                    if (elemento.get("user_id").equals(usuario.get("id")))
                                        nicks.add(usuario.getString("name"));
                                }

                                comentarios.add(elemento.getString("feedbackuser"));
                            }





                            ListView listaRestaurantes=rootView.findViewById(R.id.listaComentarios);

                            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, nicks) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                    text1.setText(nicks.get(position));
                                    text2.setText(comentarios.get(position));
                                    return view;
                                }
                            };
                            listaRestaurantes.setAdapter(adapter);
                        } else
                            labelNoComentarios.setVisibility(View.VISIBLE);
                    } else
                        labelNoComentarios.setVisibility(View.VISIBLE);
                } else
                    labelNoComentarios.setVisibility(View.VISIBLE);
            } else
                labelNoComentarios.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            Toast.makeText(this.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void AgregarComentario() throws JSONException, ExecutionException, InterruptedException {
        EditText comentario = rootView.findViewById(R.id.txtComentario);
        String strComentario = comentario.getText().toString();

        if (!strComentario.isEmpty()) {
                Conexion conexion = new Conexion();

                JSONObject json_parametros = new JSONObject();
                json_parametros.put("user_id", id_usuario);
                json_parametros.put("feedbackuser", strComentario);
                json_parametros.put("restaurant_id", id_restaurante);
                String result = conexion.execute("https://shrouded-savannah-17544.herokuapp.com/feedbacks.json", "POST", json_parametros.toString()).get();

                if (result.equals("Created")) {
                    Toast.makeText(rootView.getContext(), "Se publicó exitosamente el comentario.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(rootView.getContext(), "Ocurrió un error inesperado." + result.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(rootView.getContext(), json_parametros.toString(), Toast.LENGTH_LONG).show();
                }
        } else {
            Toast.makeText(rootView.getContext(), "El comentario no puede estar vacío", Toast.LENGTH_LONG).show();
        }
    }
}
