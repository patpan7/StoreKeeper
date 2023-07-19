package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
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
import com.example.storekeeper.Adapters.suppliers_RVAdapter;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Interfaces.suppliers_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.newInserts.supplier_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class suppliers extends AppCompatActivity implements suppliers_RVInterface {

    SearchView searchView;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    suppliers_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText supplier_popup_code, supplier_popup_name, supplier_popup_phone, supplier_popup_mobile, supplier_popup_mail, supplier_popup_afm, supplier_popup_incomeProd;
    CardView supplier_popup_savebtn;
    ImageView supplier_popup_editbtn;
    ImageButton income_plus;
    LinearLayout container;
    ArrayList<supplierModel> supplierModelsFiltered = new ArrayList<>();
    ArrayList<supplierModel> supplierModels = new ArrayList<>();
    ArrayList<supplierModel> dbSuppliers = new ArrayList<>();
    ArrayList<productModel> products = new ArrayList<>();
    ArrayList<String> serials = new ArrayList<>();
    alertDialogs dialogAlert;
    DBHelper helper = new DBHelper(suppliers.this);
    ProgressDialog progress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        recyclerView = findViewById(R.id.suppliersRV);
        refreshLayout = findViewById(R.id.suppliers_refresh);
        searchView = findViewById(R.id.suppliers_searchView);
        floatingActionButton = findViewById(R.id.suppliers_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(suppliers.this, supplier_CreateNew.class);
                startActivity(intent);
            }
        });
        showLoading();
        setUpSuppliersModels();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                showLoading();
                setUpSuppliersModels();
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
                //adapter.getFilter().filter(s);
                filterList(s);
                return true;
            }
        });
    }
    private void filterList(String s) {
        ArrayList<supplierModel> filteredList = new ArrayList<>();
        supplierModelsFiltered.clear();
        for (supplierModel supplier : supplierModels) {
            if (supplier.getName().toUpperCase().contains(s.toUpperCase())) {
                filteredList.add(supplier);
                supplierModelsFiltered.add(supplier);
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
    private void setUpSuppliersModels() {
//        ArrayList<supplierModel> dbSuppliers = helper.suppliersGetAll(suppliers.this);
//        supplierModels.clear();
//        supplierModels.addAll(dbSuppliers);
//        adapter = new suppliers_RVAdapter(this, dbSuppliers, this);
//        recyclerView.setAdapter(adapter);
        supplierModelsFiltered.clear();
        dbSuppliers.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(suppliers.this);
        String url = "http://" + ip + "/storekeeper/suppliers/suppliersGetAll.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String status = response.getString("status");
                    JSONArray message = response.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        int code = productObject.getInt("code");
                        String name = productObject.getString("name");
                        String phone = productObject.getString("phone");
                        String mobile = productObject.getString("mobile");
                        String mail = productObject.getString("mail");
                        String afm = productObject.getString("afm");
                        supplierModel newSupplier = new supplierModel(code, name, phone, mobile, mail, afm);
                        dbSuppliers.add(newSupplier);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbSuppliers);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);

    }

    private void setuprecyclerview(ArrayList<supplierModel> dbSuppliers) {
        supplierModels.clear();
        supplierModels.addAll(dbSuppliers);
        adapter = new suppliers_RVAdapter(this, dbSuppliers, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
    }

    @Override
    public void onItemClick(int position) {
        if (supplierModelsFiltered.isEmpty())
            supplierDialog(position);
        else {
            int code1 = supplierModelsFiltered.get(position).getCode();
            for (int i = 0; i < supplierModels.size(); i++)
                if (supplierModels.get(i).getCode() == code1)
                    supplierDialog(i);
        }
    }

    @SuppressLint("SetTextI18n")
    public void supplierDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View supplierPopupView = getLayoutInflater().inflate(R.layout.suppliers_popup, null);
        supplier_popup_code = supplierPopupView.findViewById(R.id.suppliers_popup_code1);
        supplier_popup_name = supplierPopupView.findViewById(R.id.suppliers_popup_name1);
        supplier_popup_phone = supplierPopupView.findViewById(R.id.suppliers_popup_phone1);
        supplier_popup_mobile = supplierPopupView.findViewById(R.id.suppliers_popup_mobile1);
        supplier_popup_mail = supplierPopupView.findViewById(R.id.suppliers_popup_mail1);
        supplier_popup_afm = supplierPopupView.findViewById(R.id.suppliers_popup_afm1);
        supplier_popup_incomeProd = supplierPopupView.findViewById(R.id.suppliers_popup_incomeProd1);
        income_plus = supplierPopupView.findViewById(R.id.income_plus);
        container = supplierPopupView.findViewById(R.id.container);

        supplier_popup_code.setText(String.valueOf(supplierModels.get(pos).getCode()));
        supplier_popup_name.setText(supplierModels.get(pos).getName());
        supplier_popup_phone.setText(supplierModels.get(pos).getPhone());
        supplier_popup_mobile.setText(supplierModels.get(pos).getMobile());
        supplier_popup_mail.setText(supplierModels.get(pos).getMail());
        supplier_popup_afm.setText(supplierModels.get(pos).getAfm());
        //supplier_popup_incomeProd.setText(helper.supplierGetAllIncomeProd(supplierModels.get(pos).getCode()) + "");
        helper.supplierGetAllIncomeProd(suppliers.this,supplierModels.get(pos).getCode(),supplier_popup_incomeProd);
        final boolean[] incomeClicked = {false};
        income_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (incomeClicked[0]) {
                    incomeClicked[0] = false;
                    income_plus.setImageResource(R.drawable.down);
                    container.removeAllViews();

                } else {
                    incomeClicked[0] = true;
                    income_plus.setImageResource(R.drawable.up);
                    container.removeAllViews();
                    //ArrayList<String> products = helper.productsGetAllNamesIncome(supplierModels.get(pos).getCode());
                    productsGetAllNamesIncome(supplierModels.get(pos).getCode(),pos);

                }
            }
        });

        dialogBuilder.setView(supplierPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        supplier_popup_savebtn = supplierPopupView.findViewById(R.id.suppliers_popup_savebtn);
        supplier_popup_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert = new alertDialogs();
                try {
                    int isError = checkFields();
                    if (isError == 0) {
                        int code = Integer.parseInt(supplier_popup_code.getText().toString());
                        String name = supplier_popup_name.getText().toString().trim();
                        String phone = supplier_popup_phone.getText().toString().trim();
                        String mobile = supplier_popup_mobile.getText().toString().trim();
                        String mail = supplier_popup_mail.getText().toString().trim();
                        String afm = supplier_popup_afm.getText().toString().trim();
                        supplierModel supplier = new supplierModel(code, name, phone, mobile, mail, afm);

                        DBHelper helper = new DBHelper(suppliers.this);
                        helper.supplierUpdate(supplier, getApplicationContext(), new DBHelper.MyCallback() {
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                dialogAlert.launchSuccess(suppliers.this, "Επιτυχής ενημέρωση");
                                dialog.dismiss();
                                setUpSuppliersModels();
                                if (!searchView.getQuery().equals(""))
                                    adapter.getFilter().filter(searchView.getQuery());
                                else setUpSuppliersModels();
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                                dialogAlert.launchFail(suppliers.this, error);
                            }
                        });
                    } else {
                        dialogAlert.launchFail(suppliers.this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                    }
                } catch (Exception e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        });

        supplier_popup_editbtn = supplierPopupView.findViewById(R.id.suppliers_popup_editbtn);
        supplier_popup_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supplier_popup_name.setEnabled(true);
                supplier_popup_phone.setEnabled(true);
                supplier_popup_mobile.setEnabled(true);
                supplier_popup_mail.setEnabled(true);
                supplier_popup_afm.setEnabled(true);
            }
        });
    }

    private void productsGetAllNamesIncome(int code, int pos) {
        products.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(suppliers.this);
        String url = "http://" + ip + "/storekeeper/suppliers/productsGetAllNamesIncome.php";

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
                    for (int i = 0; i < products.size(); i++) {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
                        TextView productName = addView.findViewById(R.id.income_popup_row_product);
                        LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
                        productName.setText(products.get(i).getName());
                        int prod_code = products.get(i).getCode();
                        serialGetAllIncome(prod_code, supplierModels.get(pos).getCode(),container,containerSN,addView);
                    }
                    Log.e("products",products.size()+"");
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })

        {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("supplier", String.valueOf(code));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    void serialGetAllIncome(int prod_code, int code, LinearLayout container, LinearLayout containerSN, View addView){
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(suppliers.this);
        String url = "http://" + ip + "/storekeeper/suppliers/serialGetAllIncome.php";

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
                    for (int j = 0; j < serials.size(); j++) {
                        LayoutInflater layoutInflaterSN = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                        TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                        serialnumber.setText(serials.get(j));
                        containerSN.addView(addViewSN);
                    }
                    container.addView(addView);
                    //containerSN.removeAllViews();
                    Log.e("Serials size",serials.size()+"");

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
                paramV.put("supplier", String.valueOf(code));
                paramV.put("prod_code", String.valueOf(prod_code));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    int checkFields() {
        int error = 0;
        if (supplier_popup_name.getText().toString().equals("")) {
            supplier_popup_name.setError("Error!!!");
            error += 1;
        }
        if (supplier_popup_phone.getText().toString().equals("") || supplier_popup_phone.length() != 10) {
            supplier_popup_phone.setError("Error!!!");
            error += 1;
        }
        if (supplier_popup_mobile.getText().toString().equals("") || supplier_popup_mobile.length() != 10) {
            supplier_popup_mobile.setError("Error!!!");
            error += 1;
        }
        if (supplier_popup_mail.getText().toString().equals("")) {
            supplier_popup_mail.setError("Error!!!");
            error += 1;
        }
        if (supplier_popup_afm.getText().toString().equals("") || supplier_popup_afm.length() != 9) {
            supplier_popup_afm.setError("Error!!!");
            error += 1;
        }
        return error;
    }
}