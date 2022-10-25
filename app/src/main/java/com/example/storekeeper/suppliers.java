package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storekeeper.Adapters.suppliers_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.suppliers_RVInterface;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.newInserts.supplier_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class suppliers extends AppCompatActivity implements suppliers_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    suppliers_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText supplier_popup_code, supplier_popup_name, supplier_popup_phone, supplier_popup_mobile, supplier_popup_mail, supplier_popup_afm;
    CardView supplier_popup_savebtn;
    ImageView supplier_popup_editbtn;
    ArrayList<supplierModel> supplierModels = new ArrayList<>();
    alertDialogs dialogAlert;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers);
        recyclerView = findViewById(R.id.suppliersRV);
        refreshLayout = findViewById(R.id.suppliers_refresh);
        SearchView searchView = findViewById(R.id.suppliers_searchView);
        floatingActionButton = findViewById(R.id.suppliers_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(suppliers.this, supplier_CreateNew.class);
                startActivity(intent);
            }
        });
        setUpSuppliersModels();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
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
                adapter.getFilter().filter(s);
                return true;
            }
        });
    }

    private void setUpSuppliersModels() {
        DBHelper helper = new DBHelper(suppliers.this);
        ArrayList<supplierModel> dbSuppliers = helper.suppliersGetAll();
        supplierModels.clear();
        supplierModels.addAll(dbSuppliers);
        adapter = new suppliers_RVAdapter(this, dbSuppliers, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

        supploerDialog(position);
    }

    public void supploerDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View supplierPopupView = getLayoutInflater().inflate(R.layout.suppliers_popup, null);
        supplier_popup_code = supplierPopupView.findViewById(R.id.suppliers_popup_code1);
        supplier_popup_name = supplierPopupView.findViewById(R.id.suppliers_popup_name1);
        supplier_popup_phone = supplierPopupView.findViewById(R.id.suppliers_popup_phone1);
        supplier_popup_mobile = supplierPopupView.findViewById(R.id.suppliers_popup_mobile1);
        supplier_popup_mail = supplierPopupView.findViewById(R.id.suppliers_popup_mail1);
        supplier_popup_afm = supplierPopupView.findViewById(R.id.suppliers_popup_afm1);

        supplier_popup_code.setText(String.valueOf(supplierModels.get(pos).getCode()));
        supplier_popup_name.setText(supplierModels.get(pos).getName());
        supplier_popup_phone.setText(supplierModels.get(pos).getPhone());
        supplier_popup_mobile.setText(supplierModels.get(pos).getMobile());
        supplier_popup_mail.setText(supplierModels.get(pos).getMail());
        supplier_popup_afm.setText(supplierModels.get(pos).getAfm());

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
                    int isError = checkFileds();
                    if (isError == 0) {
                        int code = Integer.parseInt(supplier_popup_code.getText().toString());
                        String name = supplier_popup_name.getText().toString().trim();
                        String phone = supplier_popup_phone.getText().toString().trim();
                        String mobile = supplier_popup_mobile.getText().toString().trim();
                        String mail = supplier_popup_mail.getText().toString().trim();
                        String afm = supplier_popup_afm.getText().toString().trim();
                        supplierModel supplier = new supplierModel(code, name, phone, mobile, mail, afm);

                        DBHelper helper = new DBHelper(suppliers.this);
                        boolean success = helper.supplierUpdate(supplier);
                        if (success) {
                            dialogAlert.launchSuccess(suppliers.this, "Επιτυχής ενημέρωση");
                            dialog.dismiss();
                            adapter.notifyItemChanged(pos);
                        } else
                            dialogAlert.launchFail(suppliers.this, "");
                    } else {
                        dialogAlert.launchFail(suppliers.this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                    }
                } catch (Exception e) {
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

    int checkFileds() {
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
        if (supplier_popup_afm.getText().toString().equals("") || supplier_popup_afm.length()!=9) {
            supplier_popup_afm.setError("Error!!!");
            error += 1;
        }
        return error;
    }
}