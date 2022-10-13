package com.example.storekeeper.Adapters;

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

import com.example.storekeeper.Interfaces.Suppliers_RVInterface;
import com.example.storekeeper.Models.supplierModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class Suppliers_RVAdapter extends RecyclerView.Adapter<Suppliers_RVAdapter.MyViewHolder> implements Filterable {

    private final Suppliers_RVInterface supplier_rvInterface;

    Context context;
    ArrayList<supplierModel> supplierModels;
    ArrayList<supplierModel> supplierModelsFull;

    public Suppliers_RVAdapter(Context context, ArrayList<supplierModel> supplierModels, Suppliers_RVInterface supplier_rvInterface){
        this.context = context;
        this.supplierModelsFull = supplierModels;
        this.supplier_rvInterface = supplier_rvInterface;
        this.supplierModels = new ArrayList<>(supplierModelsFull);
    }

    public void setFilteredList(ArrayList<supplierModel> filteredList){
        this.supplierModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Suppliers_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.suppliers_row,parent,false);

        return new Suppliers_RVAdapter.MyViewHolder(view,supplier_rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Suppliers_RVAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(supplierModels.get(position).getSupplierName());

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycleervie_animation));
    }

    @Override
    public int getItemCount() {


        return supplierModels.size();
    }
    @Override
    public Filter getFilter() {
        return empFilter;
    }

    private final Filter empFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<supplierModel> filteredSupplierList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0){
                filteredSupplierList.addAll(supplierModelsFull);

            }else {
                String filterPatern = charSequence.toString().toLowerCase().trim();

                for (supplierModel employee : supplierModelsFull){
                    if(employee.getSupplierName().toLowerCase().contains(filterPatern))
                        filteredSupplierList.add(employee);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredSupplierList;
            results.count = filteredSupplierList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            supplierModels.clear();
            supplierModels.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView, Suppliers_RVInterface supplier_rvInterface) {
            super(itemView);

            tvName = itemView.findViewById(R.id.suppliers_name);
            cardView = itemView.findViewById(R.id.suppliers_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (supplier_rvInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            supplier_rvInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
