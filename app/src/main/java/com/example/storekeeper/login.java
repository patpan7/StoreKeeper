package com.example.storekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class login extends AppCompatActivity {
    Button login;
    CardView cardSettings;
    EditText username, password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.login_username1);
        password = findViewById(R.id.login_password1);
        progressBar = findViewById(R.id.loading);

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(view -> {
//            Intent intent = new Intent(login.this, MainActivity.class);
//            startActivity(intent);

        });

        cardSettings = findViewById(R.id.cardSettings);
        cardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, settings.class);
            startActivity(intent);
        });

    }
}