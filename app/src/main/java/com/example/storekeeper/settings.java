package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storekeeper.DBClasses.DBHelper;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    Button show;
    AutoCompleteTextView tablenames;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        show = findViewById(R.id.show);
        tablenames = findViewById(R.id.tablenames1);


        DBHelper helper = new DBHelper(settings.this);
        ArrayList<String> tables = helper.getAllTables();
        tablenames.setAdapter(new ArrayAdapter<>(settings.this, R.layout.dropdown_row, tables));
        tablenames.setThreshold(1);


    }
}