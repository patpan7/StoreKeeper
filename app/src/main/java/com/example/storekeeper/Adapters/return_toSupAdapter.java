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

import com.example.storekeeper.Interfaces.return_toSupInterface;
import com.example.storekeeper.Models.toSupReturnModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class return_toSupAdapter extends RecyclerView.Adapter<return_toSupAdapter.MyViewHolder> implements Filterable {

    private final return_toSupInterface return_toSupInterface;

    Context context;
    ArrayList<toSupReturnModel> toSupReturnModels;
    ArrayList<toSupReturnModel> toSupReturnModelsFull;

    public return_toSupAdapter(Context context, ArrayList<toSupReturnModel> toSupReturnModels, return_toSupInterface return_toSupInterface) {
        this.context = context;
        this.toSupReturnModelsFull = toSupReturnModels;
        this.return_toSupInterface = return_toSupInterface;
        this.toSupReturnModels = new ArrayList<>(toSupReturnModelsFull);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<toSupReturnModel> filteredList) {
        this.toSupReturnModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public return_toSupAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.return_to_supplier_row, parent, false);

        return new return_toSupAdapter.MyViewHolder(view, return_toSupInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull return_toSupAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(toSupReturnModels.get(position).getDate());
        holder.tvSupplier.setText(toSupReturnModels.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return toSupReturnModels.size();
    }

    @Override
    public Filter getFilter() {
        return returntoSupFilter;
    }

    private final Filter returntoSupFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<toSupReturnModel> filteredIncomesList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredIncomesList.addAll(toSupReturnModelsFull);

            } else {
                String filterPattern = charSequence.toString().toUpperCase().trim();

                for (toSupReturnModel toSupReturn : toSupReturnModelsFull) {
                    if (toSupReturn.getDate().toUpperCase().contains(filterPattern) || toSupReturn.getName().toUpperCase().contains(filterPattern))
                        filteredIncomesList.add(toSupReturn);
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
            toSupReturnModels.clear();
            toSupReturnModels.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvSupplier;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, return_toSupInterface return_toSupInterface) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.return_supplier_date);
            tvSupplier = itemView.findViewById(R.id.return_supplier_name);
            cardView = itemView.findViewById(R.id.return_supplier_card);
            itemView.setOnClickListener(view -> {
                if (return_toSupInterface != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        return_toSupInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
}
