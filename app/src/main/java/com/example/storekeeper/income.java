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
import android.widget.DatePicker;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.Adapters.income_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.income_RVInterface;
import com.example.storekeeper.Models.incomeModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.income_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class income extends AppCompatActivity implements income_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    income_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<incomeModel> incomeModelsFiltered = new ArrayList<>();
    ArrayList<incomeModel> incomeModel = new ArrayList<>();
    ArrayList<incomeModel> dbIncomes = new ArrayList<>();
    ArrayList<productModel> products;
    ArrayList<String> serials;
    DBHelper helper = new DBHelper(income.this);
    ProgressDialog progress;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);
        recyclerView = findViewById(R.id.incomeRV);
        refreshLayout = findViewById(R.id.income_refresh);
        SearchView searchView = findViewById(R.id.income_searchView);
        floatingActionButton = findViewById(R.id.income_fab);
        date_start = findViewById(R.id.date_start1);
        date_end = findViewById(R.id.date_end1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        date_start.setText(1 + "/" + (mmMonth + 1) + "/" + mYear);
        date_start.setShowSoftInputOnFocus(false);
        date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(date_start);
            }
        });
        date_end.setText(mDay + "/" + (mmMonth + 1) + "/" + mYear);
        date_end.setShowSoftInputOnFocus(false);
        date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(date_end);
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(income.this, income_CreateNew.class);
                startActivity(intent);
            }
        });

        try {
            showLoading();
            setUpIncomes(date_start.getText().toString(), date_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                try {
                    showLoading();
                    setUpIncomes(date_start.getText().toString(), date_end.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
        ArrayList<incomeModel> filteredList = new ArrayList<>();
        incomeModelsFiltered.clear();
        for (incomeModel income : incomeModel) {
            if (income.getSupplier().toUpperCase().contains(s.toUpperCase())) {
                filteredList.add(income);
                incomeModelsFiltered.add(income);
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
        progress.setOnCancelListener(dialogInterface -> {
            onBackPressed();
        });
        progress.show();
    }

    private void dismissLoading() {
        progress.dismiss();
    }

    private void setUpIncomes(String start, String end) throws ParseException {
//        ArrayList<incomeModel> dbIncomes = helper.incomeGetAll(start, end);
//        incomeModel.clear();
//        incomeModel.addAll(dbIncomes);
//        adapter = new income_RVAdapter(this, dbIncomes, this);
//        recyclerView.setAdapter(adapter);
        incomeModelsFiltered.clear();
        dbIncomes.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income.this);
        String url = "http://" + ip + "/storekeeper/incomes/incomeGetAll.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    JSONArray message = jsonObject.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        String supplier = productObject.getString("name");
                        String date = formatDateForAndroid(productObject.getString("income_date"));
                        incomeModel newIncome = new incomeModel(date, supplier);
                        dbIncomes.add(newIncome);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbIncomes);
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
                paramV.put("start", formatDateForSQL(start));
                paramV.put("end", formatDateForSQL(end));
                return paramV;
            }
        };
        queue.add(request);

    }

    private void setuprecyclerview(ArrayList<incomeModel> dbIncomes) {
        incomeModel.clear();
        incomeModel.addAll(dbIncomes);
        adapter = new income_RVAdapter(this, dbIncomes, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
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

    @Override
    public void onItemClick(int position) {
        if (incomeModelsFiltered.isEmpty())
            incomeDialog(position);
        else {
            String supname1 = incomeModelsFiltered.get(position).getSupplier();
            for (int i = 0; i < incomeModel.size(); i++)
                if (incomeModel.get(i).getSupplier().equals(supname1))
                    incomeDialog(i);
        }

    }

    public void incomeDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View incomePopupView = getLayoutInflater().inflate(R.layout.income_popup, null);
        TextInputEditText income_popup_supplier = incomePopupView.findViewById(R.id.income_popup_supplier1);
        TextInputEditText income_popup_date = incomePopupView.findViewById(R.id.income_popup_date1);
        LinearLayout container = incomePopupView.findViewById(R.id.container);
        income_popup_supplier.setText(incomeModel.get(pos).getSupplier());
        income_popup_date.setText(incomeModel.get(pos).getDate());

        productsGetAllNamesIncome(incomeModel.get(pos).getSupplier(), incomeModel.get(pos).getDate());
        for (int i = 0; i < products.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
            TextView productName = addView.findViewById(R.id.income_popup_row_product);
            LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
            productName.setText(products.get(i).getName());
            int prod_code = products.get(i).getCode();

            serialGetAllIncome(incomeModel.get(pos).getSupplier(), incomeModel.get(pos).getDate(),prod_code);

            for (int j = 0; j<serials.size();j++){
                LayoutInflater layoutInflaterSN = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                serialnumber.setText(serials.get(j));
                containerSN.addView(addViewSN);
            }
            container.addView(addView);
        }


        dialogBuilder.setView(incomePopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }

    private void serialGetAllIncome(String supplier, String date, int prod_code) {
        serials = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income.this);
        String url = "http://" + ip + "/storekeeper/incomes/serialGetAllIncome.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        serials.add(productObject.getString("serial_number"));
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
                paramV.put("date", formatDateForSQL(date));
                paramV.put("supplier", supplier);
                paramV.put("prod_code", String.valueOf(prod_code));
                return paramV;
            }
        };
        queue.add(request);
    }

    public void productsGetAllNamesIncome(String supplier, String date){
        products = new ArrayList<>();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(income.this);
        String url = "http://" + ip + "/storekeeper/incomes/productsGetAllNamesIncome.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp = new JSONObject(response);
                    String status = resp.getString("status");
                    JSONArray message = resp.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        int code = productObject.getInt("code");
                        String name = productObject.getString("name");
                        productModel newProduct = new productModel(code, name);
                        products.add(newProduct);
                    }
                    Log.e("products",products.size()+"");
                } catch (JSONException ignored) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("date", formatDateForSQL(date));
                paramV.put("supplier", supplier);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    public static String formatDateForSQL(String inDate) {
        SimpleDateFormat inSDF = new SimpleDateFormat("dd/mm/yyyy");
        SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }

    public static String formatDateForAndroid(String inDate) {
        SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat outSDF = new SimpleDateFormat("dd/mm/yyyy");
        String outDate = "";
        if (inDate != null) {
            try {
                Date date = inSDF.parse(inDate);
                outDate = outSDF.format(date);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return outDate;
    }
}