package com.example.storekeeper.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storekeeper.Interfaces.charge_RVInterface;
import com.example.storekeeper.Models.chargeModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class charge_RVAdapter extends RecyclerView.Adapter<charge_RVAdapter.MyViewHolder> implements Filterable {

    private final charge_RVInterface charge_rvInterface;

    Context context;
    ArrayList<chargeModel> chargeModels;
    ArrayList<chargeModel> chargeModelsFull;

    public charge_RVAdapter(Context context, ArrayList<chargeModel> chargeModels, charge_RVInterface charge_rvInterface){
        this.context = context;
        this.chargeModelsFull = chargeModels;
        this.charge_rvInterface = charge_rvInterface;
        this.chargeModels = new ArrayList<>(chargeModelsFull);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<chargeModel> filteredList) {
        this.chargeModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public charge_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.charge_row, parent, false);

        return new charge_RVAdapter.MyViewHolder(view, charge_rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull charge_RVAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(chargeModels.get(position).getDate());
        holder.tvEmployeeName.setText(chargeModels.get(position).getName());
    }

    @Override
    public int getItemCount() {

        return chargeModels.size();
    }

    @Override
    public Filter getFilter() {
        return chargeFilter;
    }

    private final Filter chargeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<chargeModel> filteredIncomesList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredIncomesList.addAll(chargeModelsFull);

            } else {
                String filterPatern = charSequence.toString().toUpperCase().trim();

                for (chargeModel charge : chargeModelsFull) {
                    if (charge.getDate().toUpperCase().contains(filterPatern) || charge.getName().toUpperCase().contains(filterPatern))
                        filteredIncomesList.add(charge);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredIncomesList;
            results.count = filteredIncomesList.size();
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            chargeModels.clear();
            chargeModels.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvEmployeeName;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, charge_RVInterface charge_rvInterface) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.charge_date);
            tvEmployeeName = itemView.findViewById(R.id.charge_employee_name);
            cardView = itemView.findViewById(R.id.charge_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (charge_rvInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            charge_rvInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
