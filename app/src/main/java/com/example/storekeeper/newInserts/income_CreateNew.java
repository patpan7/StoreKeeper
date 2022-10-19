package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.R;
import com.example.storekeeper.captureAct;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class income_CreateNew extends AppCompatActivity {

    TextInputEditText income_date, income_bacode;
    ImageButton barcode_btn, serial_btn;
    AutoCompleteTextView income_suppliers, income_products;
    LinearLayout container;
    DBHelper helper = new DBHelper(income_CreateNew.this);


    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_create_new);
        container = findViewById(R.id.container);

        income_suppliers = findViewById(R.id.income_insert_supplier1);
        ArrayList<String> supplierList = helper.suppliersGetAllNames();
        income_suppliers.setAdapter(new ArrayAdapter<>(income_CreateNew.this, R.layout.dropdown_row, supplierList));
        income_suppliers.setThreshold(1);


        income_date = findViewById(R.id.income_insert_date1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        income_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        income_date.setShowSoftInputOnFocus(false);
        income_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(income_date);
            }
        });

        income_products = findViewById(R.id.income_insert_product1);
        ArrayList<String> productList = helper.productsGetAllNames();
        income_products.setAdapter(new ArrayAdapter<>(income_CreateNew.this, R.layout.dropdown_row, productList));
        income_products.setThreshold(1);

        barcode_btn = findViewById(R.id.income_insert_prodsearch_btn);
        barcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (income_products.getText().toString().equals("")) {
                    scanCode();
                }
            }
        });
        income_bacode = findViewById(R.id.income_insert_sn1);
        serial_btn = findViewById(R.id.income_insert_snsearch_btn);
        serial_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                scanSerial();
            }
        });


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
        if (result.getContents() != null)
            income_products.setText(helper.productGetName(result.getContents()));
    });

    private void scanSerial() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(captureAct.class);
        barLauncher2.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher2 = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_insert_row, null);
            TextView textOut = addView.findViewById(R.id.textout);
            textOut.setText(result.getContents());
            ImageButton buttonRemove = addView.findViewById(R.id.remove);
            buttonRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((LinearLayout) addView.getParent()).removeView(addView);
                }
            });
            container.addView(addView);
        }
    });

    void datePicker(TextInputEditText field) {
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                field.setText(date + "/" + (month + 1) + "/" + year);
            }
        }, mYear, mmMonth, mDay);
        datePickerDialog.show();
    }
}