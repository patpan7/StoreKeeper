package com.example.storekeeper.newInserts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.widget.Toast;

import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;
import com.example.storekeeper.captureAct;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class toSupReturn_CreateNew extends AppCompatActivity {
    TextInputEditText return_date, return_serialnumber, return_msg;
    ImageButton serial_btn;
    AutoCompleteTextView return_supplier;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(toSupReturn_CreateNew.this);
    ArrayList<String> serial_numbers;
    alertDialogs dialog;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_sup_return_create_new);
        container = findViewById(R.id.container);
        serial_numbers = new ArrayList<>();
        return_supplier = findViewById(R.id.return_toSup_insert_supplier1);
        ArrayList<String> employeeList = helper.suppliersGetAllNames();
        return_supplier.setAdapter(new ArrayAdapter<>(toSupReturn_CreateNew.this, R.layout.dropdown_row, employeeList));
        return_supplier.setThreshold(1);

        return_date = findViewById(R.id.return_toSup_insert_date1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        return_date.setShowSoftInputOnFocus(false);
        return_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(return_date);
            }
        });

        return_msg = findViewById(R.id.return_toSup_insert_msg1);


        return_serialnumber = findViewById(R.id.return_toSup_insert_sn1);
        serial_btn = findViewById(R.id.return_toSup_insert_snsearch_btn);
        serial_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (return_serialnumber.getText().toString().equals("")) scanSerial();
                else {
                    dinamicSerials(return_serialnumber.getText().toString(), return_supplier.getText().toString());
                    return_serialnumber.setText("");
                }
            }
        });

        savebtn = findViewById(R.id.return_toSup_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFileds();
                if (isError == 0) {
                    boolean success2 = false;
                    boolean success3 = false;
                    int successes = 0;
                    for (int i = 0; i <= serial_numbers.size() - 1; i++) {
                        //Toast.makeText(getApplicationContext()," ok ",Toast.LENGTH_LONG).show();
                        success2 =helper.serialDelete(serial_numbers.get(i));
                        success3 = helper.returnToSupAdd(return_supplier.getText().toString(), return_date.getText().toString(), serial_numbers.get(i), return_msg.getText().toString());
                        if (success2 && success3)
                            successes += 1;
                    }
                    if (successes == serial_numbers.size()) {
                        dialog.launchSuccess(this, "");
                        clear();
                    } else dialog.launchFail(this, "");
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }


        });
    }

    private int checkFileds() {
        int error = 0;
        if (return_supplier.getText().toString().equals("")) {
            return_supplier.setError("Error!!!");
            error += 1;
        }
        if (return_date.getText().toString().equals("")) {
            return_date.setError("Error!!!");
            error += 1;
        }
        if (serial_numbers.size() <= 0) {
            return_serialnumber.setError("Error!!!");
            error += 1;
        }
        if (return_msg.getText().toString().equals("")) {
            return_msg.setError("Error!!!");
            error += 1;
        }
        return error;
    }

    private void clear() {
        return_supplier.setText("");
        return_supplier.clearFocus();
        return_date.setText("");
        return_msg.setText("");
        return_msg.clearFocus();
        return_serialnumber.setText("");
        return_serialnumber.clearFocus();
        container.removeAllViews();
        serial_numbers.clear();
    }

    private void scanSerial() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(captureAct.class);
        barLauncher2.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher2 = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null)
            dinamicSerials(result.getContents(), return_supplier.getText().toString());
    });

    void dinamicSerials(String sn, String supplier_name) {

        boolean isOld = helper.checkSerialNumber(sn);
        boolean isAvailable = helper.checkSerialNumberAvailable(sn);
        int sup_code = helper.supplierGetCode(supplier_name);
        Boolean isFromSup = helper.checkSerialNumberisfromSup(sup_code,sn);
        if (isOld || serial_numbers.contains(sn)) {
            if (isAvailable) {
                if (isFromSup) {
                    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View addView = layoutInflater.inflate(R.layout.income_insert_row, null);
                    TextView textOut = addView.findViewById(R.id.textout);
                    textOut.setText(sn);
                    serial_numbers.add(sn);
                    ImageButton buttonRemove = addView.findViewById(R.id.remove);
                    buttonRemove.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            serial_numbers.remove(textOut.getText());
                            ((LinearLayout) addView.getParent()).removeView(addView);
                        }
                    });
                    container.addView(addView);
                } else
                    Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " είναι από άλλο προμηθευτή!!!", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " είναι χρεωμένο σε υπάλληλο!!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " δεν υπάρχει!!!", Toast.LENGTH_LONG).show();
        }
    }

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