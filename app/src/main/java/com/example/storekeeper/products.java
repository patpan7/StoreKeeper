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
import com.example.storekeeper.Adapters.products_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.products_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.product_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class products extends AppCompatActivity implements products_RVInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    products_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText product_popup_code, product_popup_name, product_popup_barcode, product_popup_warranty, product_popup_incomeSum, product_popup_available, product_popup_charged;
    CardView product_popup_savebtn;
    ImageView product_popup_editbtn;
    ImageButton available_plus, charged_plus;
    LinearLayout container;
    ArrayList<productModel> productModels = new ArrayList<>();
    ArrayList<productModel> productModelsFiltered = new ArrayList<>();
    ArrayList<productModel> dbProducts = new ArrayList<>();
    ArrayList<String> productSerials = new ArrayList<>();
    ArrayList<String> employees = new ArrayList<>();
    alertDialogs dialogAlert;
    DBHelper helper = new DBHelper(products.this);
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        recyclerView = findViewById(R.id.productsRV);
        refreshLayout = findViewById(R.id.product_refresh);
        searchView = findViewById(R.id.product_searchView);
        floatingActionButton = findViewById(R.id.product_fab);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(products.this, product_CreateNew.class);
            startActivity(intent);
        });
        showLoading();
        setUpProductModels();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                showLoading();
                setUpProductModels();
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
        ArrayList<productModel> filteredList = new ArrayList<>();
        productModelsFiltered.clear();
        for (productModel product : productModels) {
            if (product.getName().toUpperCase().contains(s.toUpperCase()) || String.valueOf(product.getCode()).contains(s.toUpperCase()) || String.valueOf(product.getBarcode()).contains(s.toUpperCase())) {
                filteredList.add(product);
                productModelsFiltered.add(product);
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

    private void setUpProductModels() {
        productModelsFiltered.clear();
        dbProducts.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(products.this);
        String url = "http://" + ip + "/storekeeper/products/productsGetAll.php";

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
                        String barcode = productObject.getString("barcode");
                        int warranty = productObject.getInt("warranty");
                        productModel newProduct = new productModel(code, name, barcode, warranty);
                        dbProducts.add(newProduct);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbProducts);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);

    }

    private void setuprecyclerview(ArrayList<productModel> dbProducts) {
        productModels.clear();
        productModels.addAll(dbProducts);
        adapter = new products_RVAdapter(this, dbProducts, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
    }

    @Override
    public void onItemClick(int position) {
        if (productModelsFiltered.isEmpty())
            productDialog(position);
        else {
            int code1 = productModelsFiltered.get(position).getCode();
            for (int i = 0; i < productModels.size(); i++)
                if (productModels.get(i).getCode() == code1)
                    productDialog(i);
        }
    }

    @SuppressLint("SetTextI18n")
    public void productDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View productPopupView = getLayoutInflater().inflate(R.layout.product_popup, null);
        product_popup_code = productPopupView.findViewById(R.id.product_popup_code1);
        product_popup_name = productPopupView.findViewById(R.id.product_popup_name1);
        product_popup_barcode = productPopupView.findViewById(R.id.product_popup_barcode1);
        product_popup_warranty = productPopupView.findViewById(R.id.product_popup_warranty1);
        product_popup_incomeSum = productPopupView.findViewById(R.id.product_popup_incomesum1);
        product_popup_available = productPopupView.findViewById(R.id.product_popup_available1);
        product_popup_charged = productPopupView.findViewById(R.id.product_popup_charged1);
        available_plus = productPopupView.findViewById(R.id.available_plus);
        charged_plus = productPopupView.findViewById(R.id.charged_plus);
        container = productPopupView.findViewById(R.id.container);

        product_popup_code.setText(String.valueOf(productModels.get(pos).getCode()));
        product_popup_name.setText(productModels.get(pos).getName());
        product_popup_barcode.setText(productModels.get(pos).getBarcode());
        product_popup_warranty.setText(String.valueOf(productModels.get(pos).getWarranty()));
        //product_popup_incomeSum.setText(helper.productsGetIncomeSum(productModels.get(pos).getCode()) + "");
        helper.productsGetIncomeSum(products.this, productModels.get(pos).getCode(), product_popup_incomeSum);
        //product_popup_available.setText(helper.productsGetAvailable(productModels.get(pos).getCode()) + "");
        helper.productsGetAvailable(products.this, productModels.get(pos).getCode(), product_popup_available);
        //product_popup_charged.setText(helper.productsGetCharged(productModels.get(pos).getCode()) + "");
        helper.productsGetCharged(products.this, productModels.get(pos).getCode(), product_popup_charged);
        final boolean[] availableClicked = {false};
        final boolean[] chargedClicked = {false};

        available_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (availableClicked[0]) {
                    availableClicked[0] = false;
                    available_plus.setImageResource(R.drawable.down);
                    charged_plus.setImageResource(R.drawable.down);
                    container.removeAllViews();

                } else {
                    availableClicked[0] = true;
                    chargedClicked[0] = false;
                    available_plus.setImageResource(R.drawable.up);
                    charged_plus.setImageResource(R.drawable.down);
                    container.removeAllViews();

                    productGetAllAvailableSN(productModels.get(pos).getCode());


                }
            }
        });

        charged_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chargedClicked[0]) {
                    chargedClicked[0] = false;
                    available_plus.setImageResource(R.drawable.down);
                    charged_plus.setImageResource(R.drawable.down);
                    container.removeAllViews();

                } else {
                    chargedClicked[0] = true;
                    availableClicked[0] = false;
                    available_plus.setImageResource(R.drawable.down);
                    charged_plus.setImageResource(R.drawable.up);
                    container.removeAllViews();
                    employees = helper.employeesGetAllNamesWithSN(productModels.get(pos).getCode());

                    for (int i = 0; i < employees.size(); i++) {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
                        TextView employeeName = addView.findViewById(R.id.income_popup_row_product);
                        LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
                        employeeName.setText(employees.get(i));
                        int emp_code = helper.employeeGetCode(employees.get(i));
                        ArrayList<String> serials = helper.serialsGetFromEmpProd(productModels.get(pos).getCode(), emp_code);
                        for (int j = 0; j < serials.size(); j++) {
                            LayoutInflater layoutInflaterSN = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                            TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                            serialnumber.setText(serials.get(j));
                            containerSN.addView(addViewSN);
                        }
                        container.addView(addView);
                    }
                }
            }
        });


        dialogBuilder.setView(productPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        product_popup_savebtn = productPopupView.findViewById(R.id.product_popup_savebtn);
        product_popup_savebtn.setOnClickListener(view -> {
            dialogAlert = new alertDialogs();
            try {
                int isError = checkFields();
                if (isError == 0) {
                    int id = Integer.parseInt(product_popup_code.getText().toString());
                    String name = product_popup_name.getText().toString().trim();
                    String barcode = String.valueOf(product_popup_barcode.getText());
                    int warranty = Integer.parseInt(product_popup_warranty.getText().toString());
                    productModel product = new productModel(id, name, barcode, warranty);

                    DBHelper helper = new DBHelper(products.this);
                    helper.productUpdate(product, getApplicationContext(), new DBHelper.MyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                            dialogAlert.launchSuccess(products.this, "Επιτυχής ενημέρωση");
                            dialog.dismiss();
                            if (!searchView.getQuery().equals(""))
                                adapter.getFilter().filter(searchView.getQuery());
                            else setUpProductModels();
                        }

                        @Override
                        public void onError(String error) {
                            // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                            dialogAlert.launchFail(products.this, error);
                        }
                    });
                } else {
                    dialogAlert.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                Log.e(getClass().toString(), e.toString());
            }
        });

        product_popup_editbtn = productPopupView.findViewById(R.id.product_popup_editbtn);
        product_popup_editbtn.setOnClickListener(view -> {
            //product_popup_code.setEnabled(true);
            product_popup_name.setEnabled(true);
            product_popup_barcode.setEnabled(true);
            product_popup_warranty.setEnabled(true);
        });

    }

    void productGetAllAvailableSN(int code){
        productSerials.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(products.this);
        String url = "http://" + ip + "/storekeeper/products/productGetAllAvailableSN.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resp = new JSONObject(response);
                    String status = resp.getString("status");
                    JSONArray message = resp.getJSONArray("message");
                    if (status.equals("success")) for (int i = 0; i < message.length(); i++) {
                        JSONObject productObject = message.getJSONObject(i);
                        String serial = productObject.getString("serial_number");
                        productSerials.add(serial);
                    }
                    for (int i = 0; i < productSerials.size(); i++) {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.serial_number_row, null);
                        TextView textOut = addView.findViewById(R.id.textout);
                        textOut.append(productSerials.get(i));
                        container.addView(addView);
                    }

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
                paramV.put("prod_code", String.valueOf(code));
                return paramV;
            }
        };
        queue.add(stringRequest);

    }

    int checkFields() {
        int error = 0;
        if (product_popup_name.getText().toString().equals("")) {
            product_popup_name.setError("Error!!!");
            error += 1;
        }

        if (product_popup_barcode.getText().toString().equals("")) {
            product_popup_barcode.setError("Error!!!");
            error += 1;
        }

        if (product_popup_warranty.getText().toString().equals("")) {
            product_popup_warranty.setError("Error!!!");
            error += 1;
        }
        return error;
    }

}