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

import com.example.storekeeper.Interfaces.return_fromEmpInterface;
import com.example.storekeeper.Models.fromEmpReturnModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class return_fromEmpAdapter extends RecyclerView.Adapter<return_fromEmpAdapter.MyViewHolder> implements Filterable {

    private final return_fromEmpInterface return_fromEmpInterface;

    Context context;
    ArrayList<fromEmpReturnModel> fromEmpReturnModels;
    ArrayList<fromEmpReturnModel> fromEmpReturnModelsFull;

    public return_fromEmpAdapter(Context context, ArrayList<fromEmpReturnModel> fromEmpReturnModels, return_fromEmpInterface return_fromEmpInterface) {
        this.context = context;
        this.fromEmpReturnModelsFull = fromEmpReturnModels;
        this.return_fromEmpInterface = return_fromEmpInterface;
        this.fromEmpReturnModels = new ArrayList<>(fromEmpReturnModelsFull);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<fromEmpReturnModel> filteredList) {
        this.fromEmpReturnModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public return_fromEmpAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.return_from_employee_row, parent, false);

        return new return_fromEmpAdapter.MyViewHolder(view, return_fromEmpInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull return_fromEmpAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(fromEmpReturnModels.get(position).getDate());
        holder.tvSupplier.setText(fromEmpReturnModels.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return fromEmpReturnModels.size();
    }

    @Override
    public Filter getFilter() {
        return returnFromEmpFilter;
    }

    private final Filter returnFromEmpFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<fromEmpReturnModel> filteredIncomesList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredIncomesList.addAll(fromEmpReturnModelsFull);

            } else {
                String filterPatern = charSequence.toString().toUpperCase().trim();

                for (fromEmpReturnModel fromEmpReturn : fromEmpReturnModelsFull) {
                    if (fromEmpReturn.getDate().toUpperCase().contains(filterPatern) || fromEmpReturn.getName().toUpperCase().contains(filterPatern))
                        filteredIncomesList.add(fromEmpReturn);
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
            fromEmpReturnModels.clear();
            fromEmpReturnModels.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvSupplier;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, return_fromEmpInterface return_fromEmpInterface) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.return_employee_date);
            tvSupplier = itemView.findViewById(R.id.return_employee_name);
            cardView = itemView.findViewById(R.id.return_employee_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (return_fromEmpInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            return_fromEmpInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }


}
