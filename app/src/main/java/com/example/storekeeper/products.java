package com.example.storekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class products extends AppCompatActivity {

    ArrayList<productModel> productModels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        RecyclerView recyclerView = findViewById(R.id.productsRV);
        setUpProductModels();

        Products_RVAdapter adapter = new Products_RVAdapter(this,productModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpProductModels() {
        String[] productNames = getResources().getStringArray(R.array.products_full_text);

        for (String productName : productNames) {
            productModels.add(new productModel(productName));
        }
    }
}