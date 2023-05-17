package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.DBClasses.DBHelper;


public class settings extends AppCompatActivity {

    EditText ip, port;
    CardView savebtn;
    CheckBox standalone;
    alertDialogs dialog;
    Button clean,clean2;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = findViewById(R.id.settings_ip1);
        port = findViewById(R.id.settings_port1);
        standalone = findViewById(R.id.settings_standalone);

        DBHelper helper = new DBHelper(settings.this);

        ip.setText(helper.getSettingsIP());
        port.setText(helper.getSettingPort());
        standalone.setChecked(helper.getSettingsStandalone());

        savebtn = findViewById(R.id.settings_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                if (ip.getText().length() != 0 && port.getText().length() != 0) {
                    Boolean check = standalone.isChecked();
                    boolean success = helper.settingsWrite(ip.getText().toString(),port.getText().toString(),check);
                    if (success) {
                        dialog.launchSuccess(this, "");
                    } else dialog.launchFail(this, "");
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                Log.d("EXCEPTION",e.toString());
            }
        });
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}