package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.amrdeveloper.lottiedialog.LottieDialog;
import com.example.storekeeper.CaptureAct;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class Product_CreateNew extends AppCompatActivity {

    EditText code, name, barcode, warranty;
    CardView savebtn;
    ImageButton scanbtn;
    productModel product;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create_new);

        code = findViewById(R.id.product_insert_code1);
        name = findViewById(R.id.product_insert_name1);
        barcode = findViewById(R.id.product_insert_barcode1);
        warranty = findViewById(R.id.product_insert_warranty1);
        savebtn = findViewById(R.id.product_insert_savebtn);
        scanbtn = findViewById(R.id.product_insert_scanbtn);

        scanbtn.setOnClickListener(view -> scanCode());

        savebtn.setOnClickListener(view -> {
            product = new productModel(-1, name.getText().toString(), barcode.getText().toString(), Integer.parseInt(warranty.getText().toString()));
            DBHelper helper = new DBHelper(Product_CreateNew.this);
            boolean success = helper.addOne(product);
            launchSuccessLottieDialog();


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

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null)
            barcode.setText(result.getContents());
    });

    private void launchSuccessLottieDialog() {
        LottieDialog dialog = new LottieDialog(this)
                .setAnimation(R.raw.success)
                .setAnimationRepeatCount(1)
                .setAutoPlayAnimation(true)
                .setDialogBackground(Color.TRANSPARENT)
                .setMessage("Task is Done :D");

        dialog.show();
    }

}