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

import com.example.storekeeper.Interfaces.income_RVInterface;
import com.example.storekeeper.Models.incomeModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class income_RVAdapter extends RecyclerView.Adapter<income_RVAdapter.MyViewHolder> implements Filterable {

    private final income_RVInterface income_RVInterface;

    Context context;
    ArrayList<incomeModel> incomeModels;
    ArrayList<incomeModel> incomeModelsFull;

    public income_RVAdapter(Context context, ArrayList<incomeModel> incomeModels, income_RVInterface income_RVInterface) {
        this.context = context;
        this.incomeModelsFull = incomeModels;
        this.income_RVInterface = income_RVInterface;
        this.incomeModels = new ArrayList<>(incomeModelsFull);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<incomeModel> filteredList) {
        this.incomeModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public income_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.income_row, parent, false);

        return new income_RVAdapter.MyViewHolder(view, income_RVInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull income_RVAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(incomeModels.get(position).getDate());
        holder.tvSupplier.setText(incomeModels.get(position).getSupplier());
    }

    @Override
    public int getItemCount() {

        return incomeModels.size();
    }

    @Override
    public Filter getFilter() {
        return incomeFilter;
    }

    private final Filter incomeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<incomeModel> filteredIncomesList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredIncomesList.addAll(incomeModelsFull);

            } else {
                String filterPatern = charSequence.toString().toUpperCase().trim();

                for (incomeModel income : incomeModelsFull) {
                    if (income.getDate().toUpperCase().contains(filterPatern) || income.getSupplier().toUpperCase().contains(filterPatern))
                        filteredIncomesList.add(income);
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
            incomeModels.clear();
            incomeModels.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvSupplier;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, income_RVInterface income_rvInterface) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.income_date);
            tvSupplier = itemView.findViewById(R.id.income_supplier);
            cardView = itemView.findViewById(R.id.income_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (income_rvInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            income_rvInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
