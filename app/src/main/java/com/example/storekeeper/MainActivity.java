package com.example.storekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    CardView cardProducts;
    CardView cardEmployees;
    CardView cardSupplier;
    CardView cardIncome;
    CardView cardCharge;
    CardView cardReturns;
    CardView cardWarranty;
    CardView cardSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardProducts  = findViewById(R.id.cardProducts);
        cardEmployees  = findViewById(R.id.cardEmployees);
        cardSupplier  = findViewById(R.id.cardSupplier);
        cardIncome  = findViewById(R.id.cardIncome);
        cardCharge  = findViewById(R.id.cardCharge);
        cardReturns  = findViewById(R.id.cardReturns);
        cardWarranty  = findViewById(R.id.cardWarranty);
        cardSettings  = findViewById(R.id.cardSettings);

        cardProducts.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, products.class);
            startActivity(intent);

        });

        cardEmployees.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, employees.class);
            startActivity(intent);

        });

        cardSupplier.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, supliers.class);
            startActivity(intent);

        });

        cardIncome.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, income.class);
            startActivity(intent);

        });

        cardCharge.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, charge.class);
            startActivity(intent);

        });

        cardReturns.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, returns.class);
            startActivity(intent);

        });

        cardWarranty.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, warranty.class);
            startActivity(intent);

        });

        cardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, settings.class);
            startActivity(intent);

        });

    }
}