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
import com.example.storekeeper.Adapters.employees_RVAdapter;
import com.example.storekeeper.HelperClasses.DBHelper;
import com.example.storekeeper.Interfaces.employees_RVInterface;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.employee_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class employees extends AppCompatActivity implements employees_RVInterface {

    SearchView searchView;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    employees_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText employee_popup_code, employee_popup_name, employee_popup_phone, employee_popup_mobile, employee_popup_mail, employee_popup_work, employee_popup_id, employee_popup_chargedProd;
    CardView employee_popup_savebtn;
    ImageView employee_popup_editbtn;
    ImageButton charged_plus;
    LinearLayout container;
    ArrayList<employeesModel> employeeModelsFiltered = new ArrayList<>();
    ArrayList<employeesModel> employeeModels = new ArrayList<>();
    ArrayList<employeesModel> dbEmployees = new ArrayList<>();
    ArrayList<productModel> products = new ArrayList<>();
    ArrayList<String> serials = new ArrayList<>();

    alertDialogs dialogAlert;
    DBHelper helper = new DBHelper(employees.this);
    ProgressDialog progress;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        recyclerView = findViewById(R.id.employeesRV);
        refreshLayout = findViewById(R.id.employee_refresh);
        searchView = findViewById(R.id.employees_searchView);
        floatingActionButton = findViewById(R.id.employees_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(employees.this, employee_CreateNew.class);
                startActivity(intent);
            }
        });
        showLoading();
        setUpEmployeesModels();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                showLoading();
                setUpEmployeesModels();
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
        ArrayList<employeesModel> filteredList = new ArrayList<>();
        employeeModelsFiltered.clear();
        for (employeesModel employee : employeeModels) {
            if (employee.getName().toUpperCase().contains(s.toUpperCase())) {
                filteredList.add(employee);
                employeeModelsFiltered.add(employee);
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


    private void setUpEmployeesModels() {
//        ArrayList<employeesModel> dbEmployees = helper.employeesGetAll();
//        employeeModels.clear();
//        employeeModels.addAll(dbEmployees);
//        adapter = new employees_RVAdapter(this, dbEmployees, this);
//        recyclerView.setAdapter(adapter);
        employeeModelsFiltered.clear();
        dbEmployees.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(employees.this);
        String url = "http://" + ip + "/storekeeper/employees/employeesGetAll.php";

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
                        String work = productObject.getString("work");
                        String id = productObject.getString("id");
                        employeesModel newEmployee = new employeesModel(code, name, phone, mobile, mail, work, id);
                        dbEmployees.add(newEmployee);
                    }
                } catch (JSONException e) {
                    Log.e(getClass().toString(), e.toString());
                }
                setuprecyclerview(dbEmployees);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    private void setuprecyclerview(ArrayList<employeesModel> dbEmployees) {
        employeeModels.clear();
        employeeModels.addAll(dbEmployees);
        adapter = new employees_RVAdapter(this, dbEmployees, this);
        recyclerView.removeAllViews();
        recyclerView.setAdapter(adapter);
        dismissLoading();
    }

    @Override
    public void onItemClick(int position) {
        if (employeeModelsFiltered.isEmpty())
            employeeDialog(position);
        else {
            int code1 = employeeModelsFiltered.get(position).getCode();
            for (int i = 0; i < employeeModels.size(); i++)
                if (employeeModels.get(i).getCode() == code1)
                    employeeDialog(i);
        }
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public void employeeDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View employeesPopupView = getLayoutInflater().inflate(R.layout.employees_popup, null);
        employee_popup_code = employeesPopupView.findViewById(R.id.employees_popup_code1);
        employee_popup_name = employeesPopupView.findViewById(R.id.employees_popup_name1);
        employee_popup_phone = employeesPopupView.findViewById(R.id.employees_popup_phone1);
        employee_popup_mobile = employeesPopupView.findViewById(R.id.employees_popup_mobile1);
        employee_popup_mail = employeesPopupView.findViewById(R.id.employees_popup_mail1);
        employee_popup_work = employeesPopupView.findViewById(R.id.employees_popup_work1);
        employee_popup_id = employeesPopupView.findViewById(R.id.employees_popup_id1);
        employee_popup_chargedProd = employeesPopupView.findViewById(R.id.employees_popup_chargedProd1);
        charged_plus = employeesPopupView.findViewById(R.id.charged_plus);
        container = employeesPopupView.findViewById(R.id.container);

        employee_popup_code.setText(String.valueOf(employeeModels.get(pos).getCode()));
        employee_popup_name.setText(employeeModels.get(pos).getName());
        employee_popup_phone.setText(employeeModels.get(pos).getPhone());
        employee_popup_mobile.setText(employeeModels.get(pos).getMobile());
        employee_popup_mail.setText(employeeModels.get(pos).getMail());
        employee_popup_work.setText(employeeModels.get(pos).getWork());
        employee_popup_id.setText(employeeModels.get(pos).getId());
        //employee_popup_chargedProd.setText(helper.employeeGetChargedProd(employeeModels.get(pos).getCode()) + "");
        helper.employeeGetChargedProd(employees.this,employeeModels.get(pos).getCode(),employee_popup_chargedProd);
        final boolean[] chargedClicked = {false};
        charged_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chargedClicked[0]) {
                    chargedClicked[0] = false;
                    charged_plus.setImageResource(R.drawable.down);
                    container.removeAllViews();

                } else {
                    chargedClicked[0] = true;
                    charged_plus.setImageResource(R.drawable.up);
                    container.removeAllViews();
                    //ArrayList<String> products = helper.productsGetAllNamesCharge(employeeModels.get(pos).getCode());
                    productsGetAllNamesCharge(employeeModels.get(pos).getCode(),pos);

                }
            }
        });

        dialogBuilder.setView(employeesPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        employee_popup_savebtn = employeesPopupView.findViewById(R.id.employees_popup_savebtn);
        employee_popup_savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlert = new alertDialogs();
                try {
                    int isError = checkFields();
                    if (isError == 0) {
                        int code = Integer.parseInt(employee_popup_code.getText().toString());
                        String name = employee_popup_name.getText().toString().trim();
                        String phone = employee_popup_phone.getText().toString().trim();
                        String mobile = employee_popup_mobile.getText().toString().trim();
                        String mail = employee_popup_mail.getText().toString().trim();
                        String work = employee_popup_work.getText().toString().trim();
                        String id = employee_popup_id.getText().toString().trim();
                        employeesModel employee = new employeesModel(code, name, phone, mobile, mail, work, id);

                        DBHelper helper = new DBHelper(employees.this);
                        helper.employeeUpdate(employee, getApplicationContext(), new DBHelper.MyCallback() {
                            @Override
                            public void onSuccess(String response) {
                                // Εδώ μπορείτε να χειριστείτε την επιτυχή απάντηση (response)
                                dialogAlert.launchSuccess(employees.this, "Επιτυχής ενημέρωση");
                                dialog.dismiss();
                                setUpEmployeesModels();
                                if (!searchView.getQuery().equals(""))
                                    adapter.getFilter().filter(searchView.getQuery());
                                else setUpEmployeesModels();
                            }

                            @Override
                            public void onError(String error) {
                                // Εδώ μπορείτε να χειριστείτε το σφάλμα (error)
                                dialogAlert.launchFail(employees.this, error);
                            }
                        });
                    } else {
                        dialogAlert.launchFail(employees.this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                    }
                } catch (Exception e) {
                    Log.e(getClass().toString(), e.toString());
                }
            }
        });

        employee_popup_editbtn = employeesPopupView.findViewById(R.id.employees_popup_editbtn);
        employee_popup_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employee_popup_name.setEnabled(true);
                employee_popup_phone.setEnabled(true);
                employee_popup_mobile.setEnabled(true);
                employee_popup_mail.setEnabled(true);
                employee_popup_work.setEnabled(true);
                employee_popup_id.setEnabled(true);
            }
        });

    }

    private void productsGetAllNamesCharge(int code, int pos) {
        products.clear();
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(employees.this);
        String url = "http://" + ip + "/storekeeper/employees/productsGetAllNamesCharge.php";

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
                        int prod_code = products.get(i).getCode();;
                        //ArrayList<String> serials = helper.serialGetAllCharge(prod_code, employeeModels.get(pos).getCode());
                        serialGetAllCharge(prod_code, employeeModels.get(pos).getCode(),container,containerSN,addView);

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
                paramV.put("employee", String.valueOf(code));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private void serialGetAllCharge(int prod_code, int code, LinearLayout container, LinearLayout containerSN, View addView) {
        String ip = helper.getSettingsIP();
        RequestQueue queue = Volley.newRequestQueue(employees.this);
        String url = "http://" + ip + "/storekeeper/employees/serialGetAllCharge.php";

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
                paramV.put("employee", String.valueOf(code));
                paramV.put("prod_code", String.valueOf(prod_code));
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    int checkFields() {
        int error = 0;
        if (employee_popup_name.getText().toString().equals("")) {
            employee_popup_name.setError("Error!!!");
            error += 1;
        }
        if (employee_popup_phone.getText().toString().equals("") || employee_popup_phone.length() != 10) {
            employee_popup_phone.setError("Error!!!");
            error += 1;
        }
        if (employee_popup_mobile.getText().toString().equals("") || employee_popup_mobile.length() != 10) {
            employee_popup_mobile.setError("Error!!!");
            error += 1;
        }
        if (employee_popup_mail.getText().toString().equals("")) {
            employee_popup_mail.setError("Error!!!");
            error += 1;
        }
        if (employee_popup_work.getText().toString().equals("")) {
            employee_popup_work.setError("Error!!!");
            error += 1;
        }
        if (employee_popup_id.getText().toString().equals("")) {
            employee_popup_id.setError("Error!!!");
            error += 1;
        }
        return error;
    }
}