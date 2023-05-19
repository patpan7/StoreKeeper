package com.example.storekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.DBClasses.DBHelper;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CardView cardProducts;
    CardView cardEmployees;
    CardView cardSupplier;
    CardView cardIncome;
    CardView cardCharge;
    CardView cardReturns;
    CardView cardWarranty;
    CardView cardLogout;

    TextView username;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        if (sharedPreferences.getString("logged", "false").equals("false")) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        }
        username = findViewById(R.id.main_username);
        username.append(sharedPreferences.getString("username", ""));

        cardProducts = findViewById(R.id.cardProducts);
        cardEmployees = findViewById(R.id.cardEmployees);
        cardSupplier = findViewById(R.id.cardSupplier);
        cardIncome = findViewById(R.id.cardIncome);
        cardCharge = findViewById(R.id.cardCharge);
        cardReturns = findViewById(R.id.cardReturns);
        cardWarranty = findViewById(R.id.cardWarranty);
        cardLogout = findViewById(R.id.cardLogout);

        cardProducts.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, products.class);
            startActivity(intent);

        });

        cardEmployees.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, employees.class);
            startActivity(intent);

        });

        cardSupplier.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, suppliers.class);
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

        cardLogout.setOnClickListener(view -> {
            logout();
        });

    }

    void logout() {
        DBHelper helper = new DBHelper(MainActivity.this);
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + ip + "/storekeeper/logout.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("logged", "");
                    editor.putString("username", "");
                    editor.putString("apiKey", "");
                    editor.apply();
                    Intent intent = new Intent(MainActivity.this, login.class);
                    startActivity(intent);
                    finish();
                } else Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("username", sharedPreferences.getString("username", ""));
                paramV.put("apiKey", sharedPreferences.getString("apiKey", ""));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

}