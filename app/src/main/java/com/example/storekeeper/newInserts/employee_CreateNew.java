package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.R;

public class employee_CreateNew extends AppCompatActivity {

    EditText code, name, surname, phone, mobile, mail, work, id;
    CardView savebtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_create_new);

        code = findViewById(R.id.employees_insert_code1);
        name = findViewById(R.id.employees_insert_name1);
        surname = findViewById(R.id.employees_insert_surname1);
        phone = findViewById(R.id.employees_insert_phone1);
        mobile = findViewById(R.id.employees_insert_mobile1);
        mail = findViewById(R.id.employees_insert_mail1);
        work = findViewById(R.id.employees_insert_work1);
        id = findViewById(R.id.employees_insert_id1);
        savebtn = findViewById(R.id.employees_insert_savebtn);

    }
}