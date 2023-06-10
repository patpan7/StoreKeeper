package com.example.storekeeper.newInserts;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.serialModel;
import com.example.storekeeper.Models.supplierModel;
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
import java.util.Locale;
import java.util.Objects;

public class income_CreateNew extends AppCompatActivity {

    TextInputEditText income_date, income_serialnumber;
    TextInputLayout income_products1, income_suppliers1;
    ImageButton barcode_btn, serial_btn, lock;
    AutoCompleteTextView income_suppliers, income_products;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(income_CreateNew.this);
    ArrayList<String> serial_numbers;
    ArrayList<String> serial_numbers_return;
    ArrayList<supplierModel> supplierList;
    ArrayList<productModel> productList;
    ArrayList<serialModel> serialsList;
    alertDialogs dialog;
    int prod_code;
    int supp_code;
    int warranty;
    int successes = 0;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_create_new);
        container = findViewById(R.id.container);
        lock = findViewById(R.id.income_insert_lock);
        lock.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                income_products.setEnabled(true);
                income_products1.setEnabled(true);
                barcode_btn.setEnabled(true);
                income_suppliers.setEnabled(true);
                income_suppliers1.setEnabled(true);
                lock.setImageResource(R.drawable.unlock);
                prod_code = 0;
                supp_code = 0;
                warranty = 0;
                return true;
            }
        });
        serial_numbers = new ArrayList<>();
        serial_numbers_return = new ArrayList<>();
        income_suppliers1 = findViewById(R.id.income_insert_supplier);
        income_suppliers = findViewById(R.id.income_insert_supplier1);
        suppliersGetAllNames();

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

        income_products1 = findViewById(R.id.income_insert_product);
        income_products = findViewById(R.id.income_insert_product1);
        productsGetAllNames();
        serialsGetAll();
        income_products.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (income_products.isPerformingCompletion()) {
                    income_products.setEnabled(false);
                    income_products1.setEnabled(false);
                    barcode_btn.setEnabled(false);
                    income_suppliers.setEnabled(false);
                    income_suppliers1.setEnabled(false);
                    lock.setImageResource(R.drawable.lock2);
                    for (productModel p : productList){
                        if (p.getName().contentEquals(income_products.getText())) {
                            prod_code = p.getCode();
                            warranty = p.getWarranty();
                        }
                    }
                    for (supplierModel s : supplierList){
                        if (s.getName().contentEquals(income_suppliers.getText()))
                            supp_code = s.getCode();
                    }
                } else {
                    income_products.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });


        barcode_btn = findViewById(R.id.income_insert_prodsearch_btn);
        barcode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (income_serialnumber.getText().toString().equals("")) scanCode();
            }
        });

        income_serialnumber = findViewById(R.id.income_insert_sn1);
        serial_btn = findViewById(R.id.income_insert_snsearch_btn);
        serial_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (income_serialnumber.getText().toString().equals("")) scanSerial();
                else {
                    dynamicSerials(income_serialnumber.getText().toString());
                    income_serialnumber.setText("");
                }
            }
        });

        savebtn = findViewById(R.id.income_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFields();
                if (isError == 0) {
                    //int prod_code = helper.productGetCode(income_products.getText().toString());
                    //int supp_code = helper.supplierGetCode(income_suppliers.getText().toString());
                    //int warranty = helper.productGetWarranty(prod_code);

                    for (int i = 0; i < serial_numbers.size(); i++) {
                        //Toast.makeText(getApplicationContext()," ok ",Toast.LENGTH_LONG).show();
                        helper.serialAdd(serial_numbers.get(i), prod_code, Objects.requireNonNull(income_date.getText()).toString(), supp_code, warranty,this, new DBHelper.MyCallback(){
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                successes += 1;
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            }
                        });
                        helper.incomeAdd(supp_code, income_date.getText().toString(), serial_numbers.get(i), this, new DBHelper.MyCallback(){
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                successes += 1;
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            }
                        });

                    }
                    for (int i = 0; i < serial_numbers_return.size(); i++) {
                        //Toast.makeText(getApplicationContext()," ok ",Toast.LENGTH_LONG).show();
                        helper.serialUpdateAvailable(serial_numbers_return.get(i), 1,this, new DBHelper.MyCallback(){
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                successes += 1;
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            }
                        });
                        helper.incomeAdd(supp_code, Objects.requireNonNull(income_date.getText()).toString(), serial_numbers_return.get(i), this, new DBHelper.MyCallback(){
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                successes += 1;
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            }
                        });
                    }
                    if (successes == (serial_numbers.size() + serial_numbers_return.size())) {
                        dialog.launchSuccess(this, "");
                        AlertDialog.Builder aler = new AlertDialog.Builder(this);
                        aler.setMessage("Συνέχεια παραλαβής?");
                        aler.setPositiveButton("Συνέχεια", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                income_products.setEnabled(true);
                                income_products1.setEnabled(true);
                                barcode_btn.setEnabled(true);
                                lock.setImageResource(R.drawable.unlock);
                                income_products.setText("");
                                income_serialnumber.setText("");
                                container.removeAllViews();
                                serial_numbers.clear();
                                serial_numbers_return.clear();
                            }
                        });
                        aler.setNegativeButton("Νέα παραλαβή", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clear();
                            }
                        });
                        aler.create().show();
                    } else dialog.launchFail(this, "");
                } else {
                    dialog.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }


        });
    }

    private void suppliersGetAllNames() {
        supplierList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/suppliers/suppliersGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(income_CreateNew.this,
                            R.layout.dropdown_row);
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        int code = productObject.getInt("code");
                        String name = productObject.getString("name");
                        supplierModel supplier = new supplierModel(code, name);
                        supplierList.add(supplier);
                        adapter.add(name);
                    }


                    // Σύνδεση του ArrayAdapter με το AutoCompleteTextView
                    income_suppliers.setAdapter(adapter);
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

    private void productsGetAllNames() {
        productList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/products/productsGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(income_CreateNew.this,
                            R.layout.dropdown_row);
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        int code = productObject.getInt("code");
                        String name = productObject.getString("name");
                        String barcode = productObject.getString("barcode");
                        int warranty = productObject.getInt("warranty");
                        productModel product = new productModel(code, name, barcode, warranty);
                        productList.add(product);
                        adapter.add(name);
                    }


                    // Σύνδεση του ArrayAdapter με το AutoCompleteTextView
                    income_products.setAdapter(adapter);
                    income_products.setThreshold(1);
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
        serialsList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/incomes/serialsGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        String serialnumber = productObject.getString("serialnumber");
                        int prod_code = productObject.getInt("prod_code");
                        int available = productObject.getInt("available");
                        serialModel serial = new serialModel(serialnumber, prod_code, available);
                        serialsList.add(serial);
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
        if (income_suppliers.getText().toString().equals("")) {
            income_suppliers.setError("Error!!!");
            error += 1;
        }

        if (income_date.getText().toString().equals("")) {
            income_date.setError("Error!!!");
            error += 1;
        }
        if (income_products.getText().toString().equals("")) {
            income_products.setError("Error!!!");
            error += 1;
        }
        if (serial_numbers.size() <= 0 && serial_numbers_return.size() <= 0) {
            income_serialnumber.setError("Error!!!");
            error += 1;
        }
        return error;
    }

    private void clear() {
        income_suppliers.setText("");
        income_suppliers.setEnabled(true);
        income_suppliers1.setEnabled(true);
        income_suppliers.clearFocus();
        income_date.setText("");
        income_products.setText("");
        income_products.setEnabled(true);
        income_products1.setEnabled(true);
        income_products.clearFocus();
        barcode_btn.setEnabled(true);
        income_serialnumber.setText("");
        income_serialnumber.clearFocus();
        container.removeAllViews();
        serial_numbers.clear();
        serial_numbers_return.clear();
        lock.setImageResource(R.drawable.unlock);
        prod_code = 0;
        supp_code = 0;
        warranty = 0;
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
        if (result.getContents() != null) {
            String name = "";
            for (int i = 0; i < productList.size(); i++) {
                if (productList.get(i).getBarcode().equals(result.getContents()))
                    income_products.setText(productList.get(i).getName());
            }
        }
        //income_products.setText(helper.productGetName(result.getContents()));
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
        if (result.getContents() != null) dynamicSerials(result.getContents());
    });

    void dynamicSerials(String sn) {
//        boolean isThisProd = helper.checkSerialNumberProd(sn, prod_code);
//        boolean isStock = helper.checkSerialNumberStock(sn);
//        boolean isReturn = helper.checkSerialNumberOnReturn(sn);
        boolean isStock = false;
        boolean isReturn = false;
        for (serialModel s : serialsList) {
            if (s.getSerialnumber() != null && s.getSerialnumber().equals(sn) && s.getProd_code() == prod_code) {
                isStock = true;
                isReturn = s.getAvailable() == -1;
                break;
            }
        }


        if (isStock || serial_numbers.contains(sn)) {
            Toast.makeText(getApplicationContext(), "Το serial number: " + sn + " υπάρχει ή δεν ανήκει σε αυτό το προϊόν!!!", Toast.LENGTH_LONG).show();
        } else {


            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_insert_row, null);
            TextView textOut = addView.findViewById(R.id.textout);
            textOut.setText(sn);
            if (isReturn) {
                serial_numbers_return.add(sn);
                ImageButton buttonRemove = addView.findViewById(R.id.remove);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        serial_numbers_return.remove(textOut.getText());
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });
            } else {

                serial_numbers.add(sn);
                ImageButton buttonRemove = addView.findViewById(R.id.remove);
                buttonRemove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        serial_numbers.remove(textOut.getText());
                        ((LinearLayout) addView.getParent()).removeView(addView);
                    }
                });
            }
            container.addView(addView);
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