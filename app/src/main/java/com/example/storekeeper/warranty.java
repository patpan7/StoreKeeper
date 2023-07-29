package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class warranty extends AppCompatActivity {

    TextInputEditText warranty_serial;
    ImageButton serial_btn;
    LinearLayout container;
    CardView warranty_check;
    DBHelper helper = new DBHelper(warranty.this);
    alertDialogs dialog;
    ProgressDialog progress;

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
            if (warranty_serial.getText().toString().equals(""))
                warranty_serial.setError("Error!!!");
            else
                try {
                    showLoading();
                    dynamicSerials(warranty_serial.getText().toString(),this);
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
    void dynamicSerials(String sn, Context c) throws ParseException {

        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(warranty.this);
        String url = "http://" + ip + "/storekeeper/warranty/warrantyCheck.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String product = null;
                String income_date = null;
                String supplier = null;
                int warranty_months = 0;
                String warranty_end_date = null;
                String employee = null;
                String charge_date = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        JSONArray message = jsonObject.getJSONArray("message");
                        for (int i = 0; i < message.length(); i++) {
                            JSONObject productObject = message.getJSONObject(i);
                            product = productObject.getString("productName");
                            income_date = DBHelper.formatDateForAndroid(productObject.getString("incomeDate"));
                            supplier = productObject.getString("supplierName");
                            warranty_months = Integer.parseInt(productObject.getString("warrantyMonths"));
                            warranty_end_date = DBHelper.formatDateForAndroid(productObject.getString("warrantyDate"));
                            employee = productObject.getString("employeeName");
                            charge_date = productObject.getString("charge_date");
                            LayoutInflater layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                            Date enddate = null;
                            try {
                                enddate = sdf.parse(warranty_end_date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
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
                            dismissLoading();
                        }
                    }else {
                        dismissLoading();
                        dialog = new alertDialogs();
                        dialog.launchFail(c, "Το serial number " + sn + " δεν υπάρχει!");
                        warranty_serial.setText("");
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
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("serialnumber", sn);
                return paramV;
            }
        };
        queue.add(request);

    }

    private void showLoading() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(dialogInterface -> {
            onBackPressed();
        });
        progress.show();
    }

    private void dismissLoading() {
        progress.dismiss();
    }
}