package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.DBClasses.DBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class warranty extends AppCompatActivity {

    TextInputEditText warranty_serial;
    ImageButton serial_btn;
    LinearLayout container;
    CardView warranty_check;
    DBHelper helper = new DBHelper(warranty.this);
    alertDialogs dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warranty);
        container = findViewById(R.id.container);
        warranty_serial = findViewById(R.id.warranty_sn1);
        serial_btn = findViewById(R.id.warranty_snsearch_btn);
        serial_btn.setOnClickListener(view -> {
            if (warranty_serial.getText().toString().equals("")) scanSerial();
        });

        warranty_check = findViewById(R.id.warranty_checkbtn);
        warranty_check.setOnClickListener(view -> {
            try {
                dynamicSerials(warranty_serial.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

    }

    private void scanSerial() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(captureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null)
            warranty_serial.setText(result.getContents());
    });

    @SuppressLint({"InflateParams", "SimpleDateFormat"})
    void dynamicSerials(String sn) throws ParseException {

        boolean isOld = helper.checkSerialNumberStock(sn);
        if (isOld) {
            //Toast.makeText(getApplicationContext(),"Το serial number: "+sn+" υπάρχει!!!",Toast.LENGTH_LONG).show();
            String product = helper.productGetNameFromSerial(sn);
            String income_date = helper.warrantyGetIncomeDate(sn);
            String supplier = helper.supplierGetName(sn);
            int warranty_months = helper.warrantyGetMonths(sn);
            String warranty_end_date = helper.warrantyGetEndDate(sn);
            String employee = helper.employeeGetName(sn);
            String charge_date = helper.chargeDateGet(sn);

            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.warranty_row, null);
            TextView productName = addView.findViewById(R.id.warranty_product);
            TextView serialNumber = addView.findViewById(R.id.warranty_serial_number);
            TextView incomeDate = addView.findViewById(R.id.warranty_income_date);
            TextView supplierName = addView.findViewById(R.id.warranty_supplier);
            TextView warrantyMonths = addView.findViewById(R.id.warranty_months);
            TextView warrantyEnd = addView.findViewById(R.id.warranty_end_date);
            TextView employeeName = addView.findViewById(R.id.warranty_employee);
            TextView chargeDate = addView.findViewById(R.id.warranty_employee_charge);
            ImageView warrantyIcon = addView.findViewById(R.id.warranty_icon);
            productName.setText(product);
            serialNumber.append(sn);
            incomeDate.append(income_date);
            supplierName.append(supplier);
            warrantyMonths.append(warranty_months + "");
            warrantyEnd.append(warranty_end_date);
            employeeName.append(employee);
            chargeDate.append(charge_date);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date enddate = sdf.parse(warranty_end_date);
            Calendar today = Calendar.getInstance();
            Calendar enddateCal = Calendar.getInstance();
            enddateCal.setTime(enddate);
            Calendar warning = Calendar.getInstance();
            warning.setTime(enddate);
            warning.add(Calendar.MONTH, -1);

            if (today.before(enddateCal))
                if (today.before(warning))
                    warrantyIcon.setImageResource(R.drawable.approval);
                else
                    warrantyIcon.setImageResource(R.drawable.warring);
            else
                warrantyIcon.setImageResource(R.drawable.reject);
            container.addView(addView);
            warranty_serial.setText("");
        } else {
            dialog = new alertDialogs();
            dialog.launchFail(this, "Το serial number " + sn + " δεν υπάρχει!");
        }
    }
}