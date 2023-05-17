package com.example.storekeeper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(view ->
                {
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);

                });
    }
}