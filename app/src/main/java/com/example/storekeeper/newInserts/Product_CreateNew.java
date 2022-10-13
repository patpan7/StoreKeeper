package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.CaptureAct;
import com.example.storekeeper.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class Product_CreateNew extends AppCompatActivity {

    EditText code, name, barcode;
    CardView savebtn;
    ImageButton scanbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_new);

        code = findViewById(R.id.product_insert_code1);
        name = findViewById(R.id.product_insert_name1);
        barcode = findViewById(R.id.product_insert_barcode1);
        savebtn = findViewById(R.id.product_insert_savebtn);
        scanbtn = findViewById(R.id.product_insert_scanbtn);

        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(),result -> {
       if (result.getContents() != null)
           barcode.setText(result.getContents());
    });

}