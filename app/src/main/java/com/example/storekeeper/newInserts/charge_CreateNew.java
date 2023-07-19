package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.R;
import com.example.storekeeper.alertDialogs;
import com.example.storekeeper.captureAct;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class charge_CreateNew extends AppCompatActivity {

    TextInputEditText charge_date, charge_serialnumber;
    ImageButton serial_btn;
    AutoCompleteTextView charge_employee;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(charge_CreateNew.this);
    ArrayList<String> serial_numbers;
    ArrayList<employeesModel> employeeList;
    ArrayList<String> serialsList = new ArrayList<>();
    alertDialogs dialog;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_create_new);
        container = findViewById(R.id.container);

        serial_numbers = new ArrayList<>();
        charge_employee = findViewById(R.id.charge_insert_employee1);
        //ArrayList<String> employeeList = helper.employeesGetAllNames();
        employeesGetAllNames();
        charge_employee.setAdapter(new ArrayAdapter<>(charge_CreateNew.this, R.layout.dropdown_row, employeeList));
        charge_employee.setThreshold(1);

        charge_date = findViewById(R.id.charge_insert_date1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        charge_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        charge_date.setShowSoftInputOnFocus(false);
        charge_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(charge_date);
            }
        });
        serialsGetAll();
        charge_serialnumber = findViewById(R.id.charge_insert_sn1);
        serial_btn = findViewById(R.id.charge_insert_snsearch_btn);
        serial_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (charge_serialnumber.getText().toString().equals("")) scanSerial();
                else {
                    dinamicSerials(charge_serialnumber.getText().toString());
                    charge_serialnumber.setText("");
                }
            }
        });

        savebtn = findViewById(R.id.charge_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            int emp_code = 0;
            try {
                int isError = checkFields();
                if (isError == 0) {
                    for (employeesModel e : employeeList)
                        if (e.getName().contentEquals(charge_employee.getText()))
                            emp_code = e.getCode();
//                    boolean success2 = false;
//                    int successes = 0;
//                    for (int i = 0; i <= serial_numbers.size()-1; i++) {
//                        //Toast.makeText(getApplicationContext()," ok ",Toast.LENGTH_LONG).show();
//                        success2 = helper.serialUpdateEmployee(serial_numbers.get(i), Objects.requireNonNull(charge_date.getText()).toString(), emp_code,0);
//                        helper.chargeAdd(charge_employee.getText().toString(),charge_date.getText().toString(),serial_numbers.get(i));
//                        if (success2)
//                            successes +=1;
//                    }
//                    if (successes == serial_numbers.size()) {
//                        dialog.launchSuccess(this, "");
//                        //helper.chargeAdd(charge_employee.getText().toString(),charge_date.getText().toString(),serial_numbers.get(i));
//                        clear();
//                    } else dialog.launchFail(this, "");
                    helper.chargeAdd(serial_numbers, charge_date.getText().toString(), emp_code, this, new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            dialog.launchSuccess(charge_CreateNew.this, response);
                            clear();
                        }

                        @Override
                        public void onError(String error) {
                            dialog.launchFail(charge_CreateNew.this, error);
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

    private void employeesGetAllNames() {
        employeeList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(charge_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/employees/employeesGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(charge_CreateNew.this,
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
                    charge_employee.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    private void serialsGetAll() {
        serialsList.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(charge_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/charges/serialsGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        String serialnumber = productObject.getString("serialnumber");
                        serialsList.add(serialnumber);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    private int checkFields() {
        int error = 0;
        if (charge_employee.getText().toString().equals("")) {
            charge_employee.setError("Error!!!");
            error += 1;
        }

        if (charge_date.getText().toString().equals("")) {
            charge_date.setError("Error!!!");
            error += 1;
        }
        if (serial_numbers.size() <= 0) {
            charge_serialnumber.setError("Error!!!");
            error += 1;
        }
        return error;
    }

    private void clear() {
        charge_employee.setText("");
        charge_employee.clearFocus();
        charge_serialnumber.setText("");
        charge_serialnumber.clearFocus();
        container.removeAllViews();
        serial_numbers.clear();
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        charge_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
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
        if (result.getContents() != null) dinamicSerials(result.getContents());
    });

    void dinamicSerials(String sn) {
        if (serialsList.contains(sn) && !serial_numbers.contains(sn)){
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
        } else {
            Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " δεν υπάρχει ή είναι χρεωμένο!!!", Toast.LENGTH_LONG).show();
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