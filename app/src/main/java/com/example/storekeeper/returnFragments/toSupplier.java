package com.example.storekeeper.returnFragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.storekeeper.Adapters.return_toSupAdapter;
import com.example.storekeeper.DBClasses.DBHelper;
import com.example.storekeeper.Interfaces.return_toSupInterface;
import com.example.storekeeper.Models.toSupReturnModel;
import com.example.storekeeper.R;
import com.example.storekeeper.newInserts.toSupReturn_CreateNew;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class toSupplier extends Fragment implements return_toSupInterface {
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton floatingActionButton;
    TextInputEditText date_start, date_end;
    return_toSupAdapter adapter;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    ArrayList<toSupReturnModel> toSupReturnModels = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_supplier, container, false);
    }

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
                Intent intent = new Intent(toSupplier.this.getContext(), toSupReturn_CreateNew.class);
                startActivity(intent);
            }
        });
        try {
            setUpReturnToSup(date_start.getText().toString(), date_end.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                try {
                    setUpReturnToSup(date_start.getText().toString(), date_end.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
                return true;
            }
        });
    }

    private void setUpReturnToSup(String start, String end) throws ParseException {
        DBHelper helper = new DBHelper(getActivity());
        ArrayList<toSupReturnModel> dbToSupReturns = helper.returnsToSupGetAll(start, end);
        toSupReturnModels.clear();
        toSupReturnModels.addAll(dbToSupReturns);
        adapter = new return_toSupAdapter(this.getContext(), dbToSupReturns, this);
        recyclerView.setAdapter(adapter);
    }

    void datePicker(TextInputEditText field) {
        Calendar calendar = Calendar.getInstance(Locale.ROOT);
        int mDay = calendar.get(Calendar.DATE);
        int mmMonth = calendar.get(Calendar.MONTH);
        int mYear = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                field.setText(date + "/" + (month + 1) + "/" + year);
            }
        }, mYear, mmMonth, mDay);
        datePickerDialog.show();
    }

    @Override
    public void onItemClick(int position) {
        returnDialog(position);
    }

    @SuppressLint("MissingInflatedId")
    private void returnDialog(int pos) {
        DBHelper helper = new DBHelper(this.getContext());
        dialogBuilder = new MaterialAlertDialogBuilder(this.getContext());
        final View supReturnPopupView = getLayoutInflater().inflate(R.layout.return_to_sup_popup, null);
        TextInputEditText returnToSup_popup_employee = supReturnPopupView.findViewById(R.id.returnToSup_popup_employee1);
        TextInputEditText returnToSup_popup_date = supReturnPopupView.findViewById(R.id.returnToSup_popup_date1);
        TextInputEditText return_msg = supReturnPopupView.findViewById(R.id.returnToSup_popup_msg1);
        LinearLayout container = supReturnPopupView.findViewById(R.id.container);
        returnToSup_popup_employee.setText(toSupReturnModels.get(pos).getName());
        returnToSup_popup_date.setText(toSupReturnModels.get(pos).getDate());
        return_msg.setText(toSupReturnModels.get(pos).getMsg());


        //int employeeCode = helper.employeeGetCode(fromEmpReturnModels.get(pos).getName());
        ArrayList<String> products = helper.productsGetAllNamesRerunSup(toSupReturnModels.get(pos).getName(), toSupReturnModels.get(pos).getDate());

        for (int i = 0; i < products.size(); i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.income_popup_row, null);
            TextView productName = addView.findViewById(R.id.income_popup_row_product);
            LinearLayout containerSN = addView.findViewById(R.id.containerSerials);
            productName.setText(products.get(i).toString());
            int prod_code = helper.productGetCode(products.get(i));
            ArrayList<String> serials = helper.serialGetAllReturnSup(toSupReturnModels.get(pos).getName(), toSupReturnModels.get(pos).getDate(),prod_code);
            for (int j = 0; j < serials.size(); j++) {
                LayoutInflater layoutInflaterSN = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addViewSN = layoutInflaterSN.inflate(R.layout.income_popup_row_sn, null);
                TextView serialnumber = addViewSN.findViewById(R.id.income_popup_row_sn_serial);
                serialnumber.setText(serials.get(j));
                containerSN.addView(addViewSN);
            }
            container.addView(addView);
        }
        dialogBuilder.setView(supReturnPopupView);
        dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }
}