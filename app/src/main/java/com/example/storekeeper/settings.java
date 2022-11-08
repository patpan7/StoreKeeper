package com.example.storekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.newInserts.income_CreateNew;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class settings extends AppCompatActivity {

    Button show;
    AutoCompleteTextView tablenames;
    TableLayout sqliteTable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        show = findViewById(R.id.show);
        tablenames = findViewById(R.id.tablenames1);
        sqliteTable = findViewById(R.id.sqliteTable);

        DBHelper helper = new DBHelper(settings.this);
        ArrayList<String> tables = helper.getAllTables();
        tablenames.setAdapter(new ArrayAdapter<>(settings.this, R.layout.dropdown_row, tables));
        tablenames.setThreshold(1);


    }
}