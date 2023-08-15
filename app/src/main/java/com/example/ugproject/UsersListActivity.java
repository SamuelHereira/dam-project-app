package com.example.ugproject;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ugproject.adapters.UsuarioAdapter;
import com.example.ugproject.api.CustomGetAsyncTask;
import com.example.ugproject.models.Usuario;
import com.example.ugproject.utils.Alerts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class UsersListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UsuarioAdapter userAdapter;

    private EditText txtSearch;
    private Button btnSearch;
    private ArrayList<Usuario> listaUsuarios = null;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_users_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaUsuarios = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);

        // Habilitar el botón de regreso en la Toolbar
        //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Manejar el evento del botón de regreso
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });

        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = txtSearch.getText().toString();
                if (search.isEmpty()) {
                    Alerts.showAlertDialogWithText(context, "Error", "Debe ingresar un texto para buscar");
                    return;
                }
                createAndExecuteGetRequest(search);
            }
        });

        createAndExecuteGetRequest("");

    }


    private void createAndExecuteGetRequest(String search) {

        AlertDialog loadingDialog = Alerts.showLoadingDialog(this);


        String url = "http://192.168.100.23:3000/usuarios";

        try {
            CustomGetAsyncTask customGetAsyncTask = new CustomGetAsyncTask(this,
                    url, search, new CustomGetAsyncTask.OnResultListener() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Integer statusCode = jsonObject.getInt("statusCode");

                        if (statusCode != 200) {
                            Alerts.showAlertDialogWithText(UsersListActivity.this, "Error", "Error al obtener usuarios");
                            return;
                        }

                        listaUsuarios.clear();

                        JSONArray usuariosArray = jsonObject.getJSONArray("usuarios");

                        String urlFile = "http://192.168.100.23:3000/archivo/";

                        for (int i = 0; i < usuariosArray.length(); i++) {
                            JSONObject usuarioObject = usuariosArray.getJSONObject(i);
                            Usuario usuario = new Usuario();
                            usuario.setId(usuarioObject.getInt("id"));
                            usuario.setCedula(usuarioObject.getString("cedula"));
                            usuario.setNombre(usuarioObject.getString("nombre"));
                            usuario.setApellido(usuarioObject.getString("apellido"));
                            usuario.setCorreo(usuarioObject.getString("correo"));
                            usuario.setCelular(usuarioObject.getString("celular"));
                            usuario.setDireccion(usuarioObject.getString("direccion"));
                            usuario.setCarrera(usuarioObject.getString("carrera"));
                            usuario.setSemestre(usuarioObject.getInt("semestre"));
                            usuario.setTitulo(urlFile + usuarioObject.getString("titulo"));
                            usuario.setFoto(urlFile + usuarioObject.getString("foto"));
                            usuario.setSaludo(usuarioObject.getString("saludo"));
                            usuario.setLatitud_gps(usuarioObject.getString("latitud_gps"));
                            usuario.setLongitud_gps(usuarioObject.getString("longitud_gps"));

                            Log.println(Log.INFO, "Usuario Foto", usuario.getFoto());


                            listaUsuarios.add(usuario);
                        }

                        userAdapter = new UsuarioAdapter(listaUsuarios);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(userAdapter);

                        if (listaUsuarios.size() == 0)
                            Alerts.showAlertDialogWithText(UsersListActivity.this, "Error", "No se encontraron usuarios");


                        loadingDialog.dismiss();

                    } catch (JSONException e) {
                        loadingDialog.dismiss();
                        Alerts.showAlertDialogWithText(UsersListActivity.this, "Error", "Error al obtener usuarios");

                    }


                }

                @Override
                public void onError(String result) {
                    loadingDialog.dismiss();
                    Alerts.showAlertDialogWithText(UsersListActivity.this, "Error", result);
                }
            });

            customGetAsyncTask.execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}