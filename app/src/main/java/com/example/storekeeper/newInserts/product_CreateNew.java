package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;
import com.example.storekeeper.captureAct;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class product_CreateNew extends AppCompatActivity {

    EditText code, name, barcode, warranty;
    CardView savebtn;
    ImageButton scanbtn;
    productModel product;
    alertDialogs dialog;

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

        DBHelper helper = new DBHelper(this);
        code.setText(String.valueOf(helper.productNextID(this)));
        scanbtn.setOnClickListener(view -> scanCode());

        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFileds();
                if (isError == 0) {
                    product = new productModel(-1, name.getText().toString().trim(), barcode.getText().toString(), Integer.parseInt(warranty.getText().toString()));
                    helper.productAdd(product, this, new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                            dialog.launchSuccess(product_CreateNew.this, response);
                            clear();
                        }

                        @Override
                        public void onError(String error) {
                            // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            dialog.launchFail(product_CreateNew.this, error);
                        }
                    });
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }


        });
    }

    private void clear() {
        DBHelper helper = new DBHelper(product_CreateNew.this);
        code.setText(String.valueOf(helper.productNextID(this)));
        name.setText("");
        name.clearFocus();
        barcode.setText("");
        barcode.clearFocus();
        warranty.setText("");
        warranty.clearFocus();
    }


    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(captureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) barcode.setText(result.getContents());
    });

    int checkFileds() {
        int error = 0;
        if (name.getText().toString().equals("")) {
            name.setError("Error!!!");
            error += 1;
        }

        if (barcode.getText().toString().equals("")) {
            barcode.setError("Error!!!");
            error += 1;
        }
        if (warranty.getText().toString().equals("")) {
            warranty.setError("Error!!!");
            error += 1;
        }
        return error;
    }

}