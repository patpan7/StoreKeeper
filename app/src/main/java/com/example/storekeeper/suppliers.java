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

import com.example.storekeeper.Adapters.suppliers_RVAdapter;
import com.example.storekeeper.Interfaces.suppliers_RVInterface;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.newInserts.supplier_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class suppliers extends AppCompatActivity implements suppliers_RVInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    suppliers_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText supplier_popup_code;
    CardView supplier_popup_savebtn;
    ImageView supplier_popup_editbtn;
    ArrayList<supplierModel> supplierModels = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        recyclerView = findViewById(R.id.suppliersRV);
        searchView = findViewById(R.id.suppliers_searchView);
        floatingActionButton = findViewById(R.id.suppliers_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(suppliers.this, supplier_CreateNew.class);
                startActivity(intent);
            }
        });
        setUpSuppliersModels();
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

    private void setUpSuppliersModels() {
        String[] suppliersNames = getResources().getStringArray(R.array.suppliers_full_text);

        for (String supplierName : suppliersNames) {
            supplierModels.add(new supplierModel(supplierName));
        }

        adapter = new suppliers_RVAdapter(this,supplierModels,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        productDialog();
    }

    @SuppressLint("MissingInflatedId")
    public void productDialog(){
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View productPopupView = getLayoutInflater().inflate(R.layout.suppliers_popup, null);
        supplier_popup_code = productPopupView.findViewById(R.id.suppliers_popup_code1);

        supplier_popup_code.setText("000001");


        dialogBuilder.setView(productPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        supplier_popup_savebtn = productPopupView.findViewById(R.id.suppliers_popup_savebtn);
        supplier_popup_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        supplier_popup_editbtn = productPopupView.findViewById(R.id.suppliers_popup_editbtn);
        supplier_popup_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supplier_popup_code.setEnabled(true);

            }
        });

    }
}