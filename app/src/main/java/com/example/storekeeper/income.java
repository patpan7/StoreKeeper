package com.example.storekeeper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storekeeper.Adapters.income_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.income_RVInterface;
import com.example.storekeeper.Models.incomeModel;
import com.example.storekeeper.newInserts.income_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class income extends AppCompatActivity implements income_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    income_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<incomeModel> incomeModel = new ArrayList<>();
    alertDialogs dialogAlert;
    DBHelper helper = new DBHelper(income.this);

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
            setUpIncomes(date_start.getText().toString(), date_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                try {
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
                return true;
            }
        });

    }

    private void setUpIncomes(String start, String end) throws ParseException {
        ArrayList<incomeModel> dbIncomes = helper.incomeGetAll(start, end);
        incomeModel.addAll(dbIncomes);
        adapter = new income_RVAdapter(this, dbIncomes, this);
        recyclerView.setAdapter(adapter);
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
        incomeDialog(position);

    }

    public void incomeDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View incomePopupView = getLayoutInflater().inflate(R.layout.income_popup, null);
        TextInputEditText income_popup_supplier = incomePopupView.findViewById(R.id.income_popup_supplier1);
        TextInputEditText income_popup_date = incomePopupView.findViewById(R.id.income_popup_date1);
        LinearLayout container = incomePopupView.findViewById(R.id.container);
        income_popup_supplier.setText(incomeModel.get(pos).getSupplier());
        income_popup_date.setText(incomeModel.get(pos).getDate());

        int supplierCode = helper.supplierGetCode(incomeModel.get(pos).getSupplier());
        ArrayList<String> products = helper.productsGetAllNamesIncome(supplierCode, incomeModel.get(pos).getDate());

        for (int i = 0; i < products.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
            TextView productName = addView.findViewById(R.id.income_popup_row_product);
            LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
            productName.setText(products.get(i).toString());
            int prod_code = helper.productGetCode(products.get(i));
            ArrayList<String> serials = helper.serialGetAllIncome(prod_code,incomeModel.get(pos).getDate());
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
}