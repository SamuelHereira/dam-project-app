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
import com.example.ugproject.db.DbController;
import com.example.ugproject.models.Usuario;
import com.example.ugproject.utils.Alerts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UsersListActivity extends AppCompatActivity {
    private DbController db = new DbController(this);
    private RecyclerView recyclerView;
    private UsuarioAdapter userAdapter;

    private EditText txtSearch, txtSearchId;
    private Button btnSearch;
    private List<Usuario> listaUsuarios = null;

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

        txtSearch = findViewById(R.id.txtSearch2);
        btnSearch = findViewById(R.id.btnSearch);
        Button btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = txtSearch.getText().toString();
//                if (search.isEmpty()) {
//                    Alerts.showAlertDialogWithText(context, "Error", "Debe ingresar un texto para buscar");
//                    return;
//                }
                createAndExecuteGetRequest(search);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String search = txtSearch.getText().toString();
//                if (search.isEmpty()) {
//                    Alerts.showAlertDialogWithText(context, "Error", "Debe ingresar un texto para buscar");
//                    return;
//                }
                createAndExecuteGetRequest(search);
            }
        });

        createAndExecuteGetRequest("");

    }


    private void createAndExecuteGetRequest(String search) {

        AlertDialog loadingDialog = Alerts.showLoadingDialog(this);

        if (search.isEmpty()) {

            listaUsuarios = DbController.obtenerEstudiantes(this);
            Log.println(Log.ASSERT, "users", listaUsuarios.get(0).getId().toString());

        } else {
            String[] columnasBusqueda = {
                    DbController.ColNombre,
                    DbController.ColApellido,
                    DbController.ColCedula,
                    DbController.ColCarrera,
                    DbController.ColSemestre
            };

            listaUsuarios = DbController.buscarEstudiantes(
                    this, columnasBusqueda, search.toString(), null
            );
        }


        userAdapter = new UsuarioAdapter(listaUsuarios, context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(userAdapter);

        if (listaUsuarios.size() == 0)
            Alerts.showAlertDialogWithText(UsersListActivity.this, "Error", "No se encontraron usuarios");

        loadingDialog.dismiss();


    }


}