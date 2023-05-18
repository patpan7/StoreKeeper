package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storekeeper.Adapters.employees_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.employees_RVInterface;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.newInserts.employee_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class employees extends AppCompatActivity implements employees_RVInterface {

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
    ArrayList<employeesModel> employeeModels = new ArrayList<>();
    alertDialogs dialogAlert;
    DBHelper helper = new DBHelper(employees.this);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        recyclerView = findViewById(R.id.employeesRV);
        refreshLayout = findViewById(R.id.employee_refresh);
        SearchView searchView = findViewById(R.id.employees_searchView);
        floatingActionButton = findViewById(R.id.employees_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(employees.this, employee_CreateNew.class);
                startActivity(intent);
            }
        });
        setUpEmployeesModels();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
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
                adapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private void setUpEmployeesModels() {
        ArrayList<employeesModel> dbEmployees = helper.employeesGetAll();
        employeeModels.clear();
        employeeModels.addAll(dbEmployees);
        adapter = new employees_RVAdapter(this, dbEmployees, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

        productDialog(position);
    }

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public void productDialog(int pos) {
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
        employee_popup_chargedProd.setText(helper.employeeGetChargedProd(employeeModels.get(pos).getCode())+"");

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
                    ArrayList<String> products = helper.productsGetAllNamesCharge(employeeModels.get(pos).getCode());

                    for (int i = 0; i < products.size(); i++) {
                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
                        TextView productName = addView.findViewById(R.id.income_popup_row_product);
                        LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
                        productName.setText(products.get(i).toString());
                        int prod_code = helper.productGetCode(products.get(i));
                        ArrayList<String> serials = helper.serialGetAllCharge(prod_code,employeeModels.get(pos).getCode());
                        for (int j = 0; j<serials.size();j++){
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
                    int isError = checkFileds();
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
                        boolean success = helper.employeeUpdate(employee);
                        if (success) {
                            dialogAlert.launchSuccess(employees.this, "Επιτυχής ενημέρωση");
                            dialog.dismiss();
                            adapter.notifyItemChanged(pos);
                        } else
                            dialogAlert.launchFail(employees.this, "");
                    } else {
                        dialogAlert.launchFail(employees.this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                    }
                } catch (Exception e) {
                    //product = new productModel(-1,"error","error",0);
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

    int checkFileds() {
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