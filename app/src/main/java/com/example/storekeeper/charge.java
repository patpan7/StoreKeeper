package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.Adapters.charge_RVAdapter;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Interfaces.charge_RVInterface;
import com.example.storekeeper.Models.chargeModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.charge_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class charge extends AppCompatActivity implements charge_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    charge_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<chargeModel> chargeModel = new ArrayList<>();
    ArrayList<chargeModel> chargeModelFiltered = new ArrayList<>();
    ArrayList<chargeModel> dbCharges = new ArrayList<>();
    ArrayList<productModel> products = new ArrayList<>();
    ArrayList<String> serials = new ArrayList<>();
    DBHelper helper = new DBHelper(charge.this);
    ProgressDialog progress;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);
        recyclerView = findViewById(R.id.chargeRV);
        refreshLayout = findViewById(R.id.charge_refresh);
        SearchView searchView = findViewById(R.id.charge_searchView);
        floatingActionButton = findViewById(R.id.charge_fab);
        date_start = findViewById(R.id.date_start1);
        date_end = findViewById(R.id.date_end1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        date_start.setText(1 + "/" + (mmMonth + 1) + "/" + mYear);
        date_start.setShowSoftInputOnFocus(false);
        date_start.setOnClickListener(view -> datePicker(date_start));
        date_end.setText(mDay + "/" + (mmMonth + 1) + "/" + mYear);
        date_end.setShowSoftInputOnFocus(false);
        date_end.setOnClickListener(view -> datePicker(date_end));
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(charge.this, charge_CreateNew.class);
            startActivity(intent);
        });
        try {
            showLoading();
            setUpCharges(Objects.requireNonNull(date_start.getText()).toString(), Objects.requireNonNull(date_end.getText()).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            try {
                showLoading();
                setUpCharges(date_start.getText().toString(), Objects.requireNonNull(date_end.getText()).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    floatingActionButton.hide();
                } else {
                    floatingActionButton.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                filterList(s);
                return true;
            }
        });

    }

    private void filterList(String s) {
        ArrayList<chargeModel> filteredList = new ArrayList<>();
        chargeModelFiltered.clear();
        for (chargeModel charge : chargeModel) {
            if (charge.getName().toUpperCase().contains(s.toUpperCase())) {
                filteredList.add(charge);
                chargeModelFiltered.add(charge);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No data", Toast.LENGTH_LONG).show();
        } else {
            adapter.setFilteredList(filteredList);
        }
    }
    private void showLoading() {
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(dialogInterface -> onBackPressed());
        progress.show();
    }

    private void dismissLoading() {
        progress.dismiss();
    }

    private void setUpCharges(String start, String end) throws ParseException {
        chargeModelFiltered.clear();
        dbCharges.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(charge.this);
        String url = "http://" + ip + "/storekeeper/charges/chargeGetAll.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    JSONArray message = jsonObject.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        String employee = productObject.getString("name");
                        String date = DBHelper.formatDateForAndroid(productObject.getString("charge_date"));
                        chargeModel newCharge = new chargeModel(date, employee);
                        dbCharges.add(newCharge);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbCharges);
            }
        }, Throwable::printStackTrace){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("start", DBHelper.formatDateForSQL(start));
                paramV.put("end", DBHelper.formatDateForSQL(end));
                return paramV;
            }
        };
        queue.add(request);
    }

    private void setuprecyclerview(ArrayList<chargeModel> dbCharges) {
        chargeModel.clear();
        chargeModel.addAll(dbCharges);
        adapter = new charge_RVAdapter(this, dbCharges, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
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

    @Override
    public void onItemClick(int position) {
        if (chargeModelFiltered.isEmpty())
            chargeDialog(position);
        else {
            String name1 = chargeModelFiltered.get(position).getName();
            for (int i = 0; i < chargeModel.size(); i++)
                if (chargeModel.get(i).getName().equals(name1))
                    chargeDialog(i);
        }

    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    public void chargeDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View chargePopupView = getLayoutInflater().inflate(R.layout.charge_popup, null);
        TextInputEditText charge_popup_employee = chargePopupView.findViewById(R.id.charge_popup_employee1);
        TextInputEditText charge_popup_date = chargePopupView.findViewById(R.id.charge_popup_date1);
        LinearLayout container = chargePopupView.findViewById(R.id.container);
        charge_popup_employee.setText(chargeModel.get(pos).getName());
        charge_popup_date.setText(chargeModel.get(pos).getDate());

        productsGetAllNamesCharge(chargeModel.get(pos).getName(), chargeModel.get(pos).getDate(), container,pos,chargePopupView);

    }

    @SuppressLint("InflateParams")
    private void productsGetAllNamesCharge(String name, String date, LinearLayout container, int pos, View chargePopupView) {
        products.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(charge.this);
        String url = "http://" + ip + "/storekeeper/charges/productsGetAllNamesCharge.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject resp = new JSONObject(response);
                String status = resp.getString("status");
                JSONArray message = resp.getJSONArray("message");
                if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                    JSONObject productObject = message.getJSONObject(i);
                    int code = productObject.getInt("code");
                    String name1 = productObject.getString("name");
                    productModel newProduct = new productModel(code, name1);
                    products.add(newProduct);
                }


                for (int i = 0; i < products.size(); i++) {
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
                    TextView productName = addView.findViewById(R.id.income_popup_row_product);
                    LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
                    productName.setText(products.get(i).getName());
                    int prod_code = products.get(i).getCode();

                    serialGetAllcharge(chargeModel.get(pos).getName(), chargeModel.get(pos).getDate(),prod_code,containerSN,container,addView);
                }


                dialogBuilder.setView(chargePopupView);
                dialog = dialogBuilder.create();
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.show();

            } catch (JSONException ignored) {
            }
        }, error -> {
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("date", DBHelper.formatDateForSQL(date));
                paramV.put("employee", name);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    @SuppressLint("InflateParams")
    private void serialGetAllcharge(String name, String date, int prod_code, LinearLayout containerSN, LinearLayout container, View addView) {

        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(charge.this);
        String url = "http://" + ip + "/storekeeper/charges/serialGetAllCharges.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    serials.clear();
                    JSONObject resp = new JSONObject(response);
                    String status = resp.getString("status");
                    JSONArray message = resp.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        serials.add(productObject.getString("serial_number"));
                    }
                    //containerSN.removeAllViews();
                    Log.e("Serials size",serials.size()+"");
                    for (int j = 0; j<serials.size();j++){
                        LayoutInflater layoutInflaterSN = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                        TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                        serialnumber.setText(serials.get(j));
                        containerSN.addView(addViewSN);
                    }
                    container.addView(addView);
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, Throwable::printStackTrace){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("date", DBHelper.formatDateForSQL(date));
                paramV.put("employee", name);
                paramV.put("prod_code", String.valueOf(prod_code));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }
}