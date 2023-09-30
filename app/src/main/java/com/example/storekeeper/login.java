package com.example.storekeeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.HelperClasses.DBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    Button login;
    CardView cardSettings;
    EditText username, password;
    ProgressBar progressBar;
    String usernameAPI, apiKey;
    String usernameData, passwordData;

    SharedPreferences sharedPreferences;
    DBHelper helper = new DBHelper(login.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyAppName", MODE_PRIVATE);
        getApiKey(sharedPreferences.getString("username", "false"));

        username = findViewById(R.id.login_username1);
        password = findViewById(R.id.login_password1);
        progressBar = findViewById(R.id.loading);

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String ip = helper.getSettingsIP();
            usernameData = String.valueOf(username.getText());
            passwordData = String.valueOf(password.getText());
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://" + ip + "/storekeeper/login.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    if (status.equals("success")) {
                        usernameAPI = jsonObject.getString("username");
                        apiKey = jsonObject.getString("apiKey");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("logged", "true");
                        editor.putString("username", usernameAPI);
                        editor.putString("apiKey", apiKey);
                        editor.apply();
                        Intent intent = new Intent(login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        username.setError("Error!!!");
                        password.setError("Error!!!");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    //throw new RuntimeException(e);
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> paramV = new HashMap<>();
                    paramV.put("username", usernameData);
                    paramV.put("password", passwordData);
                    return paramV;
                }
            };
            Log.e("Tag",stringRequest.toString());
            queue.add(stringRequest);
        });

        login.setOnLongClickListener(view -> {
          register(username.getText().toString(),password.getText().toString());
            return false;
        });

        cardSettings = findViewById(R.id.cardSettings);
        cardSettings.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, settings.class);
            startActivity(intent);
        });

    }

    private void register(String username, String password) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + ip + "/storekeeper/register.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            progressBar.setVisibility(View.VISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                //throw new RuntimeException(e);
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("username", username);
                paramV.put("password",password);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private void getApiKey(String username) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://" + ip + "/storekeeper/getApiKey.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("message");
                if (status.equals("success")) {
                    apiKey = jsonObject.getString("message");
                    //Toast.makeText(getApplicationContext(), "message", Toast.LENGTH_LONG).show();
                    if (sharedPreferences.getString("logged", "false").equals("true") && apiKey.equals(sharedPreferences.getString("apiKey","false"))) {
                        Intent intent = new Intent(login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("logged", "false");
                        editor.putString("username", usernameAPI);
                        editor.putString("apiKey", "null");
                        editor.apply();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                //throw new RuntimeException(e);
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("username", username);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
}