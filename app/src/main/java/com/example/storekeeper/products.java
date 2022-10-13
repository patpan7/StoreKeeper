package com.example.storekeeper;

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

import com.example.storekeeper.Adapters.Products_RVAdapter;
import com.example.storekeeper.Interfaces.Products_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.Product_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class products extends AppCompatActivity implements Products_RVInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    Products_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText product_popup_code, product_popup_name, product_popup_barcode, product_popup_balance;
    CardView product_popup_savebtn;
    ImageView product_popup_editbtn;
    ArrayList<productModel> productModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        recyclerView = findViewById(R.id.productsRV);
        searchView = findViewById(R.id.product_searchView);
        floatingActionButton = findViewById(R.id.product_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(products.this, Product_CreateNew.class);
                startActivity(intent);
            }
        });
        setUpProductModels();
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

    private void setUpProductModels() {
        String[] productNames = getResources().getStringArray(R.array.products_full_text);

        for (String productName : productNames) {
            productModels.add(new productModel(productName));
        }

        adapter = new Products_RVAdapter(this,productModels,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        productDialog();
        //Toast.makeText(this, "Successfully.", Toast.LENGTH_SHORT).show();
    }

    public void productDialog(){
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View productPopupView = getLayoutInflater().inflate(R.layout.product_popup, null);
        product_popup_code = productPopupView.findViewById(R.id.product_popup_code1);
        product_popup_name = productPopupView.findViewById(R.id.product_popup_name1);
        product_popup_barcode = productPopupView.findViewById(R.id.product_popup_barcode1);
        product_popup_balance = productPopupView.findViewById(R.id.product_popup_balance1);

        product_popup_code.setText("000001");
        product_popup_name.setText("Υπολογιστής");
        product_popup_barcode.setText("1234567890");
        product_popup_balance.setText("5");

        dialogBuilder.setView(productPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        product_popup_savebtn = productPopupView.findViewById(R.id.product_popup_savebtn);
        product_popup_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        product_popup_editbtn = productPopupView.findViewById(R.id.product_popup_editbtn);
        product_popup_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //product_popup_code.setEnabled(true);
                product_popup_name.setEnabled(true);
                product_popup_barcode.setEnabled(true);
                //product_popup_balance.setEnabled(true);
            }
        });

    }
}