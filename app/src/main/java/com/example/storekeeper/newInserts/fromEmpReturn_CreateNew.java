package com.example.storekeeper.newInserts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionBarPolicy;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class fromEmpReturn_CreateNew extends AppCompatActivity {

    TextInputEditText return_date, return_serialnumber, return_msg;
    ImageButton serial_btn, lock;
    TextInputLayout return_employee1;
    AutoCompleteTextView return_employee;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(fromEmpReturn_CreateNew.this);
    ArrayList<String> serial_numbers;
    alertDialogs dialog;
    String[] allserials;
    boolean[] checkedItems;
    ArrayList<Integer> employeeSerials;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_emp_return_create_new);
        container = findViewById(R.id.container);
        serial_numbers = new ArrayList<>();
        return_employee = findViewById(R.id.return_fromEmp_insert_employee1);
        return_employee1 = findViewById(R.id.return_fromEmp_insert_employee);
        ArrayList<String> employeeList = helper.employeesGetAllNames();
        return_employee.setAdapter(new ArrayAdapter<>(fromEmpReturn_CreateNew.this, R.layout.dropdown_row, employeeList));
        return_employee.setThreshold(1);
        return_employee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (return_employee.isPerformingCompletion()) {
                    return_employee.setEnabled(false);
                    return_employee1.setEnabled(false);
                    serial_btn.setEnabled(true);
                    lock.setImageResource(R.drawable.lock2);
                    int emp_code = helper.employeeGetCode(return_employee.getText().toString());
                    allserials = helper.serialGetAllFromEmp(emp_code).toArray(new String[0]);
                    checkedItems = new boolean[allserials.length];
                    employeeSerials = new ArrayList<>();
                } else {
                    return_employee.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        lock = findViewById(R.id.return_fromEmp_insert_lock);
        lock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return_employee.setEnabled(true);
                return_employee1.setEnabled(true);
                serial_btn.setEnabled(false);
                lock.setImageResource(R.drawable.unlock);
                return true;
            }
        });

        return_date = findViewById(R.id.return_fromEmp_insert_date1);
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

        return_msg = findViewById(R.id.return_fromEmp_insert_msg1);


        return_serialnumber = findViewById(R.id.return_fromEmp_insert_sn1);
        serial_btn = findViewById(R.id.return_fromEmp_insert_snsearch_btn);
        serial_btn.setEnabled(false);
        serial_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (return_serialnumber.getText().toString().equals("")) scanSerial();
                else {
                    dinamicSerials(return_serialnumber.getText().toString(), return_employee.getText().toString());
                    return_serialnumber.setText("");
                }
            }
        });

        serial_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(fromEmpReturn_CreateNew.this);
                // set the title for the alert dialog
                builder.setTitle("Επιλογή Serial Number");

                // set the icon for the alert dialog
                builder.setIcon(R.drawable.check);

                // now this is the function which sets the alert dialog for multiple item selection ready
                builder.setMultiChoiceItems(allserials,
                        checkedItems,
                        (DialogInterface.OnMultiChoiceClickListener) (dialogInterface, pos, isChecked) -> {
                            if (isChecked) {
                                if (!employeeSerials.contains(pos))
                                    employeeSerials.add(pos);
                                else
                                    employeeSerials.remove(pos);
                            }
                        });

                // alert dialog shouldn't be cancellable
                builder.setCancelable(false);

                // handle the positive button of the dialog
                builder.setPositiveButton("Επιλογή", (dialog, which) -> {
                    for (int i = 0; i < employeeSerials.size(); i++)
                        if (!serial_numbers.contains(allserials[employeeSerials.get(i)]))
                            dinamicSerials(allserials[employeeSerials.get(i)], return_employee.getText().toString());
                });
                builder.setNegativeButton("Άκυρο", (dialog, which) -> {
                });

                // handle the negative button of the alert dialog

                // handle the neutral button of the dialog to clear the selected items boolean checkedItem
                builder.setNeutralButton("Καθαρισμός", (dialog, which) -> {
                });

                // create the builder
                builder.create();

                // create the alert dialog with the alert dialog builder instance
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        savebtn = findViewById(R.id.return_fromEmp_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFileds();
                if (isError == 0) {
                    boolean success2 = false;
                    int successes = 0;
                    for (int i = 0; i < serial_numbers.size(); i++) {
                        //Toast.makeText(getApplicationContext()," ok ",Toast.LENGTH_LONG).show();
                        success2 = helper.serialUpdateEmployee(serial_numbers.get(i), "", 0, 1);
                        helper.returnFromEmpAdd(return_employee.getText().toString(), return_date.getText().toString(), serial_numbers.get(i), return_msg.getText().toString());
                        if (success2)
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
        if (return_employee.getText().toString().equals("")) {
            return_employee.setError("Error!!!");
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
        return_employee.setText("");
        return_employee.clearFocus();
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
            dinamicSerials(result.getContents(), return_employee.getText().toString());
    });

    void dinamicSerials(String sn, String employee_name) {

        boolean isOld = helper.checkSerialNumber(sn);
        boolean isAvailable = helper.checkSerialNumberAvailable(sn);
        int emp_code = helper.employeeGetCode(employee_name);
        boolean isChargedtoEmp = helper.checkSerialNumberisCharged(sn, emp_code);
        if (isOld || serial_numbers.contains(sn)) {
            if (!isAvailable) {
                if (isChargedtoEmp) {
                    LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View addView = layoutInflater.inflate(R.layout.income_insert_row, null);
                    TextView textOut = addView.findViewById(R.id.textout);
                    textOut.setText(sn);
                    serial_numbers.add(sn);
                    int pos = -1;
                    for (int i=0;i<allserials.length;i++) {
                        if (allserials[i].equals(sn)) {
                            pos = i;
                            break;
                        }
                    }
                    employeeSerials.add(pos);
                    checkedItems[pos] = true;
                    ImageButton buttonRemove = addView.findViewById(R.id.remove);
                    buttonRemove.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            serial_numbers.remove(textOut.getText());
                            int pos = -1;
                            for (int i=0;i<allserials.length;i++) {
                                if (allserials[i].equals(textOut.getText().toString())) {
                                    pos = i;
                                    break;
                                }
                            }
                            employeeSerials.remove(pos);
                            checkedItems[pos] = false;
                            ((LinearLayout) addView.getParent()).removeView(addView);
                        }
                    });
                    container.addView(addView);
                } else
                    Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " είναι χρεωμένο σε διαφορετιό υπάλληλο!!!", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " δεν είναι χρεωμένο!!!", Toast.LENGTH_LONG).show();
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