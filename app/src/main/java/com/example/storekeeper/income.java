package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storekeeper.Adapters.income_RVAdapter;
import com.example.storekeeper.Interfaces.income_RVInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class income extends AppCompatActivity implements income_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    Button income_datebtn;
    income_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<com.example.storekeeper.Models.incomeModel> incomeModel = new ArrayList<>();
    alertDialogs dialogAlert;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        recyclerView = findViewById(R.id.incomeRV);
        refreshLayout = findViewById(R.id.income_refresh);
        SearchView searchView = findViewById(R.id.income_searchView);
        floatingActionButton = findViewById(R.id.income_fab);
        income_datebtn = findViewById(R.id.income_datebtn);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        calendar.clear();
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        income_datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(income.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        income_datebtn.setText(month);
                    }
                }, mYear, mmMonth, mDay);
                datePickerDialog.show();
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(employees.this, employee_CreateNew.class);
                //startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}