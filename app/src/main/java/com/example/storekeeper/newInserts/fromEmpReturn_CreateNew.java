package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;
import com.example.storekeeper.captureAct;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.M)
public class fromEmpReturn_CreateNew extends AppCompatActivity {

    TextInputEditText return_date, return_serialnumber, return_msg;
    ImageButton serial_btn, lock;
    TextInputLayout return_employee1;
    AutoCompleteTextView return_employee;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(fromEmpReturn_CreateNew.this);
    ArrayList<String> serial_numbers;
    ArrayList<employeesModel> employeeList;
    alertDialogs dialog;
    String[] allserials;
    boolean[] checkedItems;
    int emp_code = 0;

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

        employeesGetAllNames();
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

                    for (employeesModel e : employeeList)
                        if (e.getName().contentEquals(return_employee.getText()))
                            emp_code = e.getCode();
                    serialsGetAllFromEmp(emp_code);

                } else {
                    return_employee.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        lock = findViewById(R.id.return_fromEmp_insert_lock);
        lock.setOnLongClickListener(view -> {
            return_employee.setEnabled(true);
            return_employee1.setEnabled(true);
            serial_btn.setEnabled(false);
            lock.setImageResource(R.drawable.unlock);
            return true;
        });

        return_date = findViewById(R.id.return_fromEmp_insert_date1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        return_date.setShowSoftInputOnFocus(false);
        return_date.setOnClickListener(view -> datePicker(return_date));

        return_msg = findViewById(R.id.return_fromEmp_insert_msg1);


        return_serialnumber = findViewById(R.id.return_fromEmp_insert_sn1);
        serial_btn = findViewById(R.id.return_fromEmp_insert_snsearch_btn);
        serial_btn.setEnabled(false);
        serial_btn.setOnClickListener(view -> {
            if (Objects.requireNonNull(return_serialnumber.getText()).toString().equals("")) scanSerial();
            else {
                dinamicSerials(return_serialnumber.getText().toString());
                return_serialnumber.setText("");
            }
        });

        serial_btn.setOnLongClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(fromEmpReturn_CreateNew.this);
            // set the title for the alert dialog
            builder.setTitle("Επιλογή Serial Number");
            // set the icon for the alert dialog
            builder.setIcon(R.drawable.check);

            // now this is the function which sets the alert dialog for multiple item selection ready
            builder.setMultiChoiceItems(allserials,
                    checkedItems,
                    (dialogInterface, pos, isChecked) -> {
                    });

            // alert dialog shouldn't be cancellable
            builder.setCancelable(false);

            // handle the positive button of the dialog
            builder.setPositiveButton("Επιλογή", (dialogInterface, i) -> {
                for (int j = 0; j < checkedItems.length; j++) {
                    if (checkedItems[j])
                        if (!serial_numbers.contains(allserials[j]))
                            dinamicSerials(allserials[j]);
                }
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
        });

        savebtn = findViewById(R.id.return_fromEmp_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFields();
                if (isError == 0) {
                    helper.returnFromEmpAdd(emp_code, Objects.requireNonNull(return_date.getText()).toString(), serial_numbers, Objects.requireNonNull(return_msg.getText()).toString(), this, new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            dialog.launchSuccess(fromEmpReturn_CreateNew.this, response);
                            clear();
                        }

                        @Override
                        public void onError(String error) {
                            dialog.launchFail(fromEmpReturn_CreateNew.this, error);
                        }
                    });
                    //helper.serialUpdateEmployee(serial_numbers, "", 0, 1);
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }


        });
    }

    private void serialsGetAllFromEmp(int empCode) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(fromEmpReturn_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/returns/serialsGetAllFromEmp.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject resp = new JSONObject(response);
                    String status = resp.getString("status");
                    JSONArray message = resp.getJSONArray("message");
                    allserials = new String[message.length()];
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        //allserials.add(productObject.getString("serial_number"));
                        allserials[i]=productObject.getString("serial_number");
                    }
                    checkedItems = new boolean[allserials.length];
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("employee", String.valueOf(empCode));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    private void employeesGetAllNames() {
        employeeList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(fromEmpReturn_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/employees/employeesGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(fromEmpReturn_CreateNew.this,
                            R.layout.dropdown_row);
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        int code = productObject.getInt("code");
                        String name = productObject.getString("name");
                        employeesModel employee = new employeesModel(code, name);
                        employeeList.add(employee);
                        adapter.add(name);
                    }


                    // Σύνδεση του ArrayAdapter με το AutoCompleteTextView
                    return_employee.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace);
        queue.add(request);
    }

    private int checkFields() {
        int error = 0;
        if (return_employee.getText().toString().equals("")) {
            return_employee.setError("Error!!!");
            error += 1;
        }
        if (Objects.requireNonNull(return_date.getText()).toString().equals("")) {
            return_date.setError("Error!!!");
            error += 1;
        }
        if (serial_numbers.size() == 0) {
            return_serialnumber.setError("Error!!!");
            error += 1;
        }
        if (Objects.requireNonNull(return_msg.getText()).toString().equals("")) {
            return_msg.setError("Error!!!");
            error += 1;
        }
        return error;
    }

    @SuppressLint("SetTextI18n")
    private void clear() {
        return_employee.setText("");
        return_employee.setEnabled(true);
        return_employee.clearFocus();
        return_employee1.setEnabled(true);
        serial_btn.setEnabled(false);
        lock.setImageResource(R.drawable.unlock);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        return_date.setShowSoftInputOnFocus(false);
        return_msg.setText("");
        return_msg.clearFocus();
        return_serialnumber.setText("");
        return_serialnumber.clearFocus();
        container.removeAllViews();
        serial_numbers.clear();
        allserials = new String[0];
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
            dinamicSerials(result.getContents());
    });

    @RequiresApi(api = Build.VERSION_CODES.M)
    void dinamicSerials(String sn) {
        boolean isOk = false;
        for (String allserial : allserials) {
            if (allserial.equals(sn)) {
                isOk = true;
                break;
            }
        }
        if (isOk){
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") final View addView = layoutInflater.inflate(R.layout.income_insert_row,null);
                    TextView textOut = addView.findViewById(R.id.textout);
                    textOut.setText(sn);
                    serial_numbers.add(sn);
                    int pos = -1;
                    for (int i = 0; i <= allserials.length; i++) {
                        if (allserials[i].equals(sn)) {
                            pos = i;
                            break;
                        }
                    }
                    checkedItems[pos] = true;
                    ImageButton buttonRemove = addView.findViewById(R.id.remove);
                    buttonRemove.setOnClickListener(v -> {
                        serial_numbers.remove(textOut.getText());
                        int pos1 = -1;
                        for (int i = 0; i < allserials.length; i++) {
                            if (allserials[i].equals(textOut.getText().toString())) {
                                pos1 = i;
                                break;
                            }
                        }
                        checkedItems[pos1] = false;
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    });
                    container.addView(addView);
        } else
            Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " δεν είναι χρεωμένο!!!", Toast.LENGTH_LONG).show();
    }

    void datePicker(TextInputEditText field) {
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, date) -> field.setText(date + "/" + (month + 1) + "/" + year), mYear, mmMonth, mDay);
        datePickerDialog.show();
    }
}