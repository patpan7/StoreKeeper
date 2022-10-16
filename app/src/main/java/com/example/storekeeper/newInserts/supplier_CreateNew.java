package com.example.storekeeper.newInserts;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;

public class supplier_CreateNew extends AppCompatActivity {

    EditText code, name, phone, mobile, mail, afm;
    CardView savebtn;
    supplierModel supplier;
    alertDialogs dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_create_new);

        code = findViewById(R.id.suppliers_insert_code1);
        name = findViewById(R.id.suppliers_insert_name1);
        phone = findViewById(R.id.suppliers_insert_phone1);
        mobile = findViewById(R.id.suppliers_insert_mobile1);
        mail = findViewById(R.id.suppliers_insert_mail1);
        afm = findViewById(R.id.suppliers_insert_afm1);
        savebtn = findViewById(R.id.suppliers_insert_savebtn);

        DBHelper helper = new DBHelper(supplier_CreateNew.this);
        code.setText(String.valueOf(helper.supplierNextID()));
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFileds();
                if (isError == 0) {
                    supplier = new supplierModel(-1, name.getText().toString().trim(), phone.getText().toString().trim(), mobile.getText().toString().trim(), mail.getText().toString().trim(), afm.getText().toString().trim());
                    boolean success = helper.supplierAdd(supplier);
                    if (success) {
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

    private void clear() {
        DBHelper helper = new DBHelper(supplier_CreateNew.this);
        code.setText(String.valueOf(helper.employeeNextID()));
        name.setText("");
        name.clearFocus();
        phone.setText("");
        phone.clearFocus();
        mobile.setText("");
        mobile.clearFocus();
        mail.setText("");
        mail.clearFocus();
        afm.setText("");
        afm.clearFocus();
    }

    int checkFileds() {
        int error = 0;
        if (name.getText().toString().equals("")) {
            name.setError("Error!!!");
            error += 1;
        }
        if (phone.getText().toString().equals("") || phone.length() != 10) {
            phone.setError("Error!!!");
            error += 1;
        }
        if (mobile.getText().toString().equals("") || mobile.length() != 10) {
            mobile.setError("Error!!!");
            error += 1;
        }
        if (mail.getText().toString().equals("")) {
            mail.setError("Error!!!");
            error += 1;
        }
        if (afm.getText().toString().equals("") || afm.length()!=9) {
            afm.setError("Error!!!");
            error += 1;
        }
        return error;
    }
}