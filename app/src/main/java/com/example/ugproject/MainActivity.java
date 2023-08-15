package com.example.ugproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public TextView registerButton;
    public EditText txtUser, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtUser = findViewById(R.id.txtuser);
        txtPassword = findViewById(R.id.txtPassword);

//        validate txtUser only 10 characters
        txtUser.setOnKeyListener((v, keyCode, event) -> {
            if (txtUser.getText().length() > 10) {
                txtUser.setError("Max. 10 caracteres");
            }
            return false;
        });

    }

    public void handleRegister(View view) {
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void handleLogin(View view) {
        Intent i = new Intent(this,UsersListActivity.class);
        startActivity(i);
    }
}