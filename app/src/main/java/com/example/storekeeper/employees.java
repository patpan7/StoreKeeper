package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storekeeper.Adapters.employees_RVAdapter;
import com.example.storekeeper.Interfaces.employees_RVInterface;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.newInserts.employee_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class employees extends AppCompatActivity implements employees_RVInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    employees_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText employee_popup_code;
    CardView employee_popup_savebtn;
    ImageView employee_popup_editbtn;
    ArrayList<employeesModel> employeeModels = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        recyclerView = findViewById(R.id.employeesRV);
        searchView = findViewById(R.id.employees_searchView);
        floatingActionButton = findViewById(R.id.employees_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(employees.this, employee_CreateNew.class);
                startActivity(intent);
            }
        });
        setUpEmployeesModels();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy >0){
                    floatingActionButton.hide();
                } else{
                    floatingActionButton.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private void setUpEmployeesModels() {
        String[] employeesNames = getResources().getStringArray(R.array.employees_full_text);

        for (String employeeName : employeesNames) {
            employeeModels.add(new employeesModel(employeeName));
        }

        adapter = new employees_RVAdapter(this,employeeModels,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        productDialog();
    }

    public void productDialog(){
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View productPopupView = getLayoutInflater().inflate(R.layout.employees_popup, null);
        employee_popup_code = productPopupView.findViewById(R.id.employees_popup_code1);

        employee_popup_code.setText("000001");


        dialogBuilder.setView(productPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        employee_popup_savebtn = productPopupView.findViewById(R.id.employees_popup_savebtn);
        employee_popup_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        employee_popup_editbtn = productPopupView.findViewById(R.id.employees_popup_editbtn);
        employee_popup_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employee_popup_code.setEnabled(true);

            }
        });

    }
}