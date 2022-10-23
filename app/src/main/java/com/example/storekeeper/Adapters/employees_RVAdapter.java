package com.example.storekeeper.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storekeeper.Interfaces.employees_RVInterface;
import com.example.storekeeper.Models.employeesModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class employees_RVAdapter extends RecyclerView.Adapter<employees_RVAdapter.MyViewHolder> implements Filterable {

    private final employees_RVInterface employees_rvInterface;

    Context context;
    ArrayList<employeesModel> employeesModels;
    ArrayList<employeesModel> employeesModelsFull;

    public employees_RVAdapter(Context context, ArrayList<employeesModel> employeesModels, employees_RVInterface employees_rvInterface) {
        this.context = context;
        this.employeesModelsFull = employeesModels;
        this.employees_rvInterface = employees_rvInterface;
        this.employeesModels = new ArrayList<>(employeesModelsFull);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<employeesModel> filteredList) {
        this.employeesModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public employees_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.employees_row, parent, false);

        return new employees_RVAdapter.MyViewHolder(view, employees_rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull employees_RVAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(employeesModels.get(position).getName());
        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.recycleervie_animation));
    }

    @Override
    public int getItemCount() {

        return employeesModels.size();
    }

    @Override
    public Filter getFilter() {
        return empFilter;
    }

    private final Filter empFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<employeesModel> filteredEmployeesList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredEmployeesList.addAll(employeesModelsFull);

            } else {
                String filterPatern = charSequence.toString().toUpperCase().trim();

                for (employeesModel employee : employeesModelsFull) {
                    if (employee.getName().toUpperCase().contains(filterPatern))
                        filteredEmployeesList.add(employee);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredEmployeesList;
            results.count = filteredEmployeesList.size();
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            employeesModels.clear();
            employeesModels.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, employees_RVInterface employees_rvInterface) {
            super(itemView);

            tvName = itemView.findViewById(R.id.employees_name);
            cardView = itemView.findViewById(R.id.employees_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (employees_rvInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            employees_rvInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}