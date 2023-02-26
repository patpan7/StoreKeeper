package com.example.storekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;


import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Models.productModel;


public class settings extends AppCompatActivity {

    EditText ip;
    CardView savebtn;
    CheckBox standalone;
    alertDialogs dialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = findViewById(R.id.settings_ip1);
        standalone = findViewById(R.id.settings_standalone);

        DBHelper helper = new DBHelper(settings.this);

        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                if (!ip.getText().equals("")) {
                    Boolean check = standalone.isChecked();
                    boolean success = helper.settingsWrite(ip.getText().toString(),check);
                    if (success) {
                        dialog.launchSuccess(this, "");
                    } else dialog.launchFail(this, "");
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }


        });

    }
}