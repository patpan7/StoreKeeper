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

import com.example.storekeeper.Adapters.products_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.products_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.newInserts.product_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class products extends AppCompatActivity implements products_RVInterface {

    private SearchView searchView;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    products_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText product_popup_code, product_popup_name, product_popup_barcode, product_popup_balance;
    CardView product_popup_savebtn;
    ImageView product_popup_editbtn;
    ArrayList<productModel> productModels = new ArrayList<>();
    alertDialogs dialogAlert;

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
        setUpProductModels();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
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
                adapter.getFilter().filter(s);
                return true;
            }
        });

    }

    private void setUpProductModels() {
        DBHelper helper = new DBHelper(products.this);
        ArrayList<productModel> dbProducts = helper.productsGetAll();
        productModels.clear();
        productModels.addAll(dbProducts);
        adapter = new products_RVAdapter(this, dbProducts, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        productDialog(position);

        //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    public void productDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View productPopupView = getLayoutInflater().inflate(R.layout.product_popup, null);
        product_popup_code = productPopupView.findViewById(R.id.product_popup_code1);
        product_popup_name = productPopupView.findViewById(R.id.product_popup_name1);
        product_popup_barcode = productPopupView.findViewById(R.id.product_popup_barcode1);
        product_popup_balance = productPopupView.findViewById(R.id.product_popup_balance1);

        product_popup_code.setText(String.valueOf(productModels.get(pos).getCode()));
        product_popup_name.setText(productModels.get(pos).getName());
        product_popup_barcode.setText(productModels.get(pos).getBarcode());
        product_popup_balance.setText("0");

        dialogBuilder.setView(productPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        product_popup_savebtn = productPopupView.findViewById(R.id.product_popup_savebtn);
        product_popup_savebtn.setOnClickListener(view -> {
            dialogAlert = new alertDialogs();
            try {
                int isError = checkFileds();
                if (isError == 0) {
                    int id = Integer.parseInt(product_popup_code.getText().toString());
                    String name = product_popup_name.getText().toString().trim();
                    String barcode = String.valueOf(product_popup_barcode.getText());
                    productModel product = new productModel(id, name, barcode);

                    DBHelper helper = new DBHelper(products.this);
                    boolean success = helper.productUpdate(product);
                    if (success) {
                        dialogAlert.launchSuccess(this,"Επιτυχής ενημέρωση");
                        dialog.dismiss();
                        adapter.notifyItemChanged(pos);
                    }else
                        dialogAlert.launchFail(this, "");
                } else {
                    dialogAlert.launchFail(this, "Τα απαιτούμενα πεδία δεν είναι συμπληρωμένα");
                }
            } catch (Exception e) {
                //product = new productModel(-1,"error","error",0);
            }
        });

        product_popup_editbtn = productPopupView.findViewById(R.id.product_popup_editbtn);
        product_popup_editbtn.setOnClickListener(view -> {
            //product_popup_code.setEnabled(true);
            product_popup_name.setEnabled(true);
            product_popup_barcode.setEnabled(true);
            //product_popup_balance.setEnabled(true);
        });

    }

    int checkFileds() {
        int error = 0;
        if (product_popup_name.getText().toString().equals("")) {
            product_popup_name.setError("Error!!!");
            error += 1;
        }

        if (product_popup_barcode.getText().toString().equals("")) {
            product_popup_barcode.setError("Error!!!");
            error += 1;
        }
        return error;
    }

}