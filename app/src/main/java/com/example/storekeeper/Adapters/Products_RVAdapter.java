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

import com.example.storekeeper.Interfaces.Products_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class Products_RVAdapter extends RecyclerView.Adapter<Products_RVAdapter.MyViewHolder> implements Filterable {

    private final Products_RVInterface products_rvInterface;

    Context context;
    ArrayList<productModel> productModels;
    ArrayList<productModel> productModelsFull;

    public Products_RVAdapter(Context context, ArrayList<productModel> productModels, Products_RVInterface products_rvInterface){
        this.context = context;
        this.productModelsFull = productModels;
        this.products_rvInterface = products_rvInterface;
        this.productModels = new ArrayList<>(productModelsFull);
    }

    public void setFilteredList(ArrayList<productModel> filteredList){
        this.productModels = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Products_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_row,parent,false);

        return new Products_RVAdapter.MyViewHolder(view,products_rvInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull Products_RVAdapter.MyViewHolder holder, int position) {
        holder.tvName.setText(productModels.get(position).getProductName());
        holder.tvCode.setText("00001");
        holder.tvbarcode.setText("1234567890");

        holder.cardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.recycleervie_animation));
    }

    @Override
    public int getItemCount() {

        return productModels.size();
    }

    @Override
    public Filter getFilter() {
        return prodFilter;
    }

    private final Filter prodFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<productModel> filteredProductList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0){
                filteredProductList.addAll(productModelsFull);

            }else {
                String filterPatern = charSequence.toString().toLowerCase().trim();

                for (productModel product : productModelsFull){
                    if(product.getProductName().toLowerCase().contains(filterPatern))
                        filteredProductList.add(product);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredProductList;
            results.count = filteredProductList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productModels.clear();
            productModels.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvCode, tvbarcode;
        CardView cardView;

         public MyViewHolder(@NonNull View itemView, Products_RVInterface products_rvInterface) {
            super(itemView);

            tvName = itemView.findViewById(R.id.product_title);
            tvCode = itemView.findViewById(R.id.product_code);
            tvbarcode = itemView.findViewById(R.id.product_barcode);
             cardView = itemView.findViewById(R.id.product_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (products_rvInterface != null){
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION){
                            products_rvInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
