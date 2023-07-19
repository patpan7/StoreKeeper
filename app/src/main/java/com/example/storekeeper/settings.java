package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.HelperClasses.DBHelper;


public class settings extends AppCompatActivity {

    EditText ip;
    CardView savebtn;
    alertDialogs dialog;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = findViewById(R.id.settings_ip1);

        DBHelper helper = new DBHelper(settings.this);

        ip.setText(helper.getSettingsIP());

        savebtn = findViewById(R.id.settings_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                if (ip.getText().length() != 0) {
                    boolean success = helper.settingsWrite(ip.getText().toString());
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