package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;

public class employee_CreateNew extends AppCompatActivity {

    EditText code, name, phone, mobile, mail, work, id;
    CardView savebtn;
    employeesModel employee;
    alertDialogs dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_create_new);

        code = findViewById(R.id.employees_insert_code1);
        name = findViewById(R.id.employees_insert_name1);
        phone = findViewById(R.id.employees_insert_phone1);
        mobile = findViewById(R.id.employees_insert_mobile1);
        mail = findViewById(R.id.employees_insert_mail1);
        work = findViewById(R.id.employees_insert_work1);
        id = findViewById(R.id.employees_insert_id1);
        savebtn = findViewById(R.id.employees_insert_savebtn);

        DBHelper helper = new DBHelper(employee_CreateNew.this);
        helper.employeeNextID(employee_CreateNew.this, code);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFields();
                if (isError == 0) {
                    employee = new employeesModel(-1,
                            name.getText().toString().trim(),
                            phone.getText().toString().trim(),
                            mobile.getText().toString().trim(),
                            mail.getText().toString().trim(),
                            work.getText().toString().trim(),
                            id.getText().toString().trim());
                    helper.employeeAdd(employee, this, new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                            dialog.launchSuccess(employee_CreateNew.this, response);
                            clear();
                        }

                        @Override
                        public void onError(String error) {
                            // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            dialog.launchFail(employee_CreateNew.this, error);
                        }
                    });

                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                Log.e(getClass().toString(),e.toString());
            }


        });
    }

    private void clear() {
        DBHelper helper = new DBHelper(employee_CreateNew.this);
        helper.employeeNextID(employee_CreateNew.this,code);
        name.setText("");
        name.clearFocus();
        phone.setText("");
        phone.clearFocus();
        mobile.setText("");
        mobile.clearFocus();
        mail.setText("");
        mail.clearFocus();
        work.setText("");
        work.clearFocus();
        id.setText("");
        id.clearFocus();

    }

    int checkFields() {
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
        if (work.getText().toString().equals("")) {
            work.setError("Error!!!");
            error += 1;
        }
        if (id.getText().toString().equals("")) {
            id.setError("Error!!!");
            error += 1;
        }
        return error;
    }
}