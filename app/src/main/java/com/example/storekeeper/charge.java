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

import com.example.storekeeper.Adapters.charge_RVAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.charge_RVInterface;
import com.example.storekeeper.Models.chargeModel;
import com.example.storekeeper.newInserts.charge_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class charge extends AppCompatActivity implements charge_RVInterface {

    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    charge_RVAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<chargeModel> chargeModel = new ArrayList<>();
    DBHelper helper = new DBHelper(charge.this);


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
                Intent intent = new Intent(charge.this, charge_CreateNew.class);
                startActivity(intent);
            }
        });
        try {
            setUpCharges(date_start.getText().toString(), date_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                try {
                    setUpCharges(date_start.getText().toString(), date_end.getText().toString());
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

    private void setUpCharges(String start, String end) throws ParseException {
        ArrayList<chargeModel> dbCharges = helper.chargeGetAll(start, end);
        chargeModel.clear();
        chargeModel.addAll(dbCharges);
        adapter = new charge_RVAdapter(this, dbCharges, this);
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
        chargeDialog(position);

    }
    @SuppressLint("MissingInflatedId")
    public void chargeDialog(int pos) {
        dialogBuilder = new MaterialAlertDialogBuilder(this);
        final View chargePopupView = getLayoutInflater().inflate(R.layout.charge_popup, null);
        TextInputEditText charge_popup_employee = chargePopupView.findViewById(R.id.charge_popup_employee1);
        TextInputEditText charge_popup_date = chargePopupView.findViewById(R.id.charge_popup_date1);
        LinearLayout container = chargePopupView.findViewById(R.id.container);
        charge_popup_employee.setText(chargeModel.get(pos).getName());
        charge_popup_date.setText(chargeModel.get(pos).getDate());

        ArrayList<String> products = helper.productsGetAllNamesCharge(chargeModel.get(pos).getName(), chargeModel.get(pos).getDate());

        for (int i = 0; i < products.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
            TextView productName = addView.findViewById(R.id.income_popup_row_product);
            LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
            productName.setText(products.get(i).toString());
            int prod_code = helper.productGetCode(products.get(i));
            ArrayList<String> serials = helper.serialGetAllCharge(chargeModel.get(pos).getName(), chargeModel.get(pos).getDate(),prod_code);
            for (int j = 0; j<serials.size();j++){
                LayoutInflater layoutInflaterSN = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                serialnumber.setText(serials.get(j));
                containerSN.addView(addViewSN);
            }
            container.addView(addView);
        }


        dialogBuilder.setView(chargePopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }
}