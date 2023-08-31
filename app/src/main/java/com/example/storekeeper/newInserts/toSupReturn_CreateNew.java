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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class toSupReturn_CreateNew extends AppCompatActivity {
    TextInputEditText return_date, return_serialnumber, return_msg;
    ImageButton serial_btn,lock;
    AutoCompleteTextView return_supplier;
    TextInputLayout return_supplier1;
    LinearLayout container;
    CardView savebtn;
    DBHelper helper = new DBHelper(toSupReturn_CreateNew.this);
    ArrayList<String> serial_numbers;
    ArrayList<supplierModel> supplierList;
    alertDialogs dialog;
    String[] allserials;
    boolean[] checkedItems;
    int sup_code = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_sup_return_create_new);
        container = findViewById(R.id.container);
        serial_numbers = new ArrayList<>();
        return_supplier = findViewById(R.id.return_toSup_insert_supplier1);
        return_supplier1 = findViewById(R.id.return_toSup_insert_supplier);

        suppliersGetAllNames();
        return_supplier.setAdapter(new ArrayAdapter<>(toSupReturn_CreateNew.this, R.layout.dropdown_row, supplierList));
        return_supplier.setThreshold(1);
        return_supplier.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (return_supplier.isPerformingCompletion()) {
                    return_supplier.setEnabled(false);
                    return_supplier1.setEnabled(false);
                    serial_btn.setEnabled(true);
                    lock.setImageResource(R.drawable.lock2);

                    for (supplierModel e : supplierList)
                        if (e.getName().contentEquals(return_supplier.getText()))
                            sup_code = e.getCode();
                    serialsGetAllToSup(sup_code);
                } else {
                    return_supplier.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });

        lock = findViewById(R.id.return_toSup_insert_lock);
        lock.setOnLongClickListener(view -> {
            return_supplier.setEnabled(true);
            return_supplier1.setEnabled(true);
            serial_btn.setEnabled(false);
            lock.setImageResource(R.drawable.unlock);
            return true;
        });

        return_date = findViewById(R.id.return_toSup_insert_date1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        return_date.setShowSoftInputOnFocus(false);
        return_date.setOnClickListener(view -> datePicker(return_date));

        return_msg = findViewById(R.id.return_toSup_insert_msg1);


        return_serialnumber = findViewById(R.id.return_toSup_insert_sn1);
        serial_btn = findViewById(R.id.return_toSup_insert_snsearch_btn);
        serial_btn.setOnClickListener(view -> {
            if (Objects.requireNonNull(return_serialnumber.getText()).toString().equals("")) scanSerial();
            else {
                dinamicSerials(return_serialnumber.getText().toString());
                return_serialnumber.setText("");
            }
        });

        serial_btn.setOnLongClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(toSupReturn_CreateNew.this);
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

        savebtn = findViewById(R.id.return_toSup_insert_savebtn);
        savebtn.setOnClickListener(view -> {
            dialog = new alertDialogs();
            try {
                int isError = checkFields();
                if (isError == 0) {

                    helper.returnToSupAdd(sup_code, Objects.requireNonNull(return_date.getText()).toString(),serial_numbers, Objects.requireNonNull(return_msg.getText()).toString(),this,new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            dialog.launchSuccess(toSupReturn_CreateNew.this, response);
                            clear();
                        }

                        @Override
                        public void onError(String error) {
                            dialog.launchFail(toSupReturn_CreateNew.this, error);
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

    private void serialsGetAllToSup(int supCode) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(toSupReturn_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/returns/serialsGetAllToSup.php";

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
                paramV.put("supplier", String.valueOf(supCode));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    private void suppliersGetAllNames() {
        supplierList = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(toSupReturn_CreateNew.this);
        String url = "http://" + ip + "/storekeeper/suppliers/suppliersGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(toSupReturn_CreateNew.this,
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
                    return_supplier.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace);
        queue.add(request);
    }

    private int checkFields() {
        int error = 0;
        if (return_supplier.getText().toString().equals("")) {
            return_supplier.setError("Error!!!");
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
        return_supplier.setText("");
        return_supplier.clearFocus();
        return_supplier.setEnabled(true);
        return_supplier1.setEnabled(true);
        serial_btn.setEnabled(false);
        lock.setImageResource(R.drawable.unlock);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        return_date.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
        return_date.setShowSoftInputOnFocus(false);
        return_date.setText("");
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

    @SuppressLint("InflateParams")
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
            final View addView = layoutInflater.inflate(R.layout.income_insert_row,null);
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

    @SuppressLint("SetTextI18n")
    void datePicker(TextInputEditText field) {
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, date) -> field.setText(date + "/" + (month + 1) + "/" + year), mYear, mmMonth, mDay);
        datePickerDialog.show();
    }
}