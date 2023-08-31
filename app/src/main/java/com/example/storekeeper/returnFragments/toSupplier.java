package com.example.storekeeper.returnFragments;

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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.storekeeper.Adapters.return_toSupAdapter;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Interfaces.return_toSupInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.toSupReturnModel;
import com.example.storekeeper.R;
import com.example.storekeeper.newInserts.toSupReturn_CreateNew;
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

public class toSupplier extends Fragment implements return_toSupInterface {
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    return_toSupAdapter adapter;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    ArrayList<toSupReturnModel> toSupReturnModels = new ArrayList<>();
    ArrayList<toSupReturnModel> toSupReturnModelsFiltered = new ArrayList<>();
    ArrayList<toSupReturnModel> dbToSup = new ArrayList<>();
    ArrayList<productModel> products = new ArrayList<>();
    ArrayList<String> serials = new ArrayList<>();
    ProgressDialog progress;
    DBHelper helper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new DBHelper(requireActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_supplier, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.toSupplierRV);
        refreshLayout = view.findViewById(R.id.toSupplier_refresh);
        SearchView searchView = view.findViewById(R.id.toSupplier_searchView);
        floatingActionButton = view.findViewById(R.id.toSupplier_fab);
        date_start = view.findViewById(R.id.date_start1);
        date_end = view.findViewById(R.id.date_end1);
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);

        date_start.setText(1 + "/" + (mmMonth + 1) + "/" + mYear);
        date_start.setShowSoftInputOnFocus(false);
        date_start.setOnClickListener(view1 -> datePicker(date_start));
        date_end.setText(mDay + "/" + (mmMonth + 1) + "/" + mYear);
        date_end.setShowSoftInputOnFocus(false);
        date_end.setOnClickListener(view12 -> datePicker(date_end));
        floatingActionButton.setOnClickListener(view13 -> {
            Intent intent = new Intent(toSupplier.this.getContext(), toSupReturn_CreateNew.class);
            startActivity(intent);
        });
        try {
            showLoading();
            setUpReturnToSup(Objects.requireNonNull(date_start.getText()).toString(), Objects.requireNonNull(date_end.getText()).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            try {
                showLoading();
                setUpReturnToSup(date_start.getText().toString(), Objects.requireNonNull(date_end.getText()).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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
        ArrayList<toSupReturnModel> filteredList = new ArrayList<>();
        toSupReturnModelsFiltered.clear();
        for (toSupReturnModel toSup : toSupReturnModels) {
            if (toSup.getName().toUpperCase().contains(s.toUpperCase())) {
                filteredList.add(toSup);
                toSupReturnModelsFiltered.add(toSup);
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_LONG).show();
        } else {
            adapter.setFilteredList(filteredList);
        }
    }

    private void showLoading() {
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progress.setCanceledOnTouchOutside(false);
        progress.setOnCancelListener(dialogInterface -> {
            //onBackPressed();
        });
        progress.show();
    }

    private void dismissLoading() {
        progress.dismiss();
    }



    private void setUpReturnToSup(String start, String end) throws ParseException {
        toSupReturnModelsFiltered.clear();
        dbToSup.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = "http://" + ip + "/storekeeper/returns/returnToSupGetAll.php";

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
                        String date = DBHelper.formatDateForAndroid(productObject.getString("return_date"));
                        String msg = productObject.getString("msg");
                        toSupReturnModel newToSup = new toSupReturnModel(supplier, date, msg);
                        dbToSup.add(newToSup);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbToSup);
            }
        }, Throwable::printStackTrace) {
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

    private void setuprecyclerview(ArrayList<toSupReturnModel> dbToSup) {
        toSupReturnModels.clear();
        toSupReturnModels.addAll(dbToSup);
        adapter = new return_toSupAdapter(this.getContext(), dbToSup, this);
        //adapter = new income_RVAdapter(this, dbIncomes, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
    }

    void datePicker(TextInputEditText field) {
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this.requireContext(), (datePicker, year, month, date) -> field.setText(date + "/" + (month + 1) + "/" + year), mYear, mmMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onItemClick(int position) {

        if (toSupReturnModelsFiltered.isEmpty())
            returnDialog(position);
        else {
            String name1 = toSupReturnModelsFiltered.get(position).getName();
            for (int i = 0; i < toSupReturnModels.size(); i++)
                if (toSupReturnModels.get(i).getName().equals(name1))
                    returnDialog(i);
        }
    }

    @SuppressLint("MissingInflatedId")
    private void returnDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this.requireContext());
        @SuppressLint("InflateParams") final View supReturnPopupView = getLayoutInflater().inflate(R.layout.return_to_sup_popup, null);
        TextInputEditText returnToSup_popup_employee = supReturnPopupView.findViewById(R.id.returnToSup_popup_employee1);
        TextInputEditText returnToSup_popup_date = supReturnPopupView.findViewById(R.id.returnToSup_popup_date1);
        TextInputEditText return_msg = supReturnPopupView.findViewById(R.id.returnToSup_popup_msg1);
        LinearLayout container = supReturnPopupView.findViewById(R.id.container);
        returnToSup_popup_employee.setText(toSupReturnModels.get(pos).getName());
        returnToSup_popup_date.setText(toSupReturnModels.get(pos).getDate());
        return_msg.setText(toSupReturnModels.get(pos).getMsg());


        //int employeeCode = helper.employeeGetCode(fromEmpReturnModels.get(pos).getName());
       productsGetAllNamesRerunSup(toSupReturnModels.get(pos).getName(), toSupReturnModels.get(pos).getDate(),container, pos, supReturnPopupView);
    }

    private void productsGetAllNamesRerunSup(String name, String date, LinearLayout container, int pos, View supReturnPopupView) {
        products.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = "http://" + ip + "/storekeeper/returns/productsGetAllNamesRerunSup.php";

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
                    LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    @SuppressLint("InflateParams") final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
                    TextView productName = addView.findViewById(R.id.income_popup_row_product);
                    LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
                    productName.setText(products.get(i).getName());
                    int prod_code = products.get(i).getCode();
                    serialGetAllReturnSup(toSupReturnModels.get(pos).getName(), toSupReturnModels.get(pos).getDate(), prod_code, containerSN, container, addView);
                }
                dialogBuilder.setView(supReturnPopupView);
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
                paramV.put("supplier", name);
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private void serialGetAllReturnSup(String name, String date, int prodCode, LinearLayout containerSN, LinearLayout container, View addView) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = "http://" + ip + "/storekeeper/returns/serialGetAllReturnSup.php";

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
                    for (int j = 0; j < serials.size(); j++) {
                        LayoutInflater layoutInflaterSN = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("InflateParams") final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
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
                paramV.put("supplier", name);
                paramV.put("prod_code", String.valueOf(prodCode));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }
}