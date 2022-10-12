package com.example.storekeeper.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storekeeper.Interfaces.Products_RVInterface;
import com.example.storekeeper.Models.productModel;
import com.example.storekeeper.R;

import java.util.ArrayList;

public class Products_RVAdapter extends RecyclerView.Adapter<Products_RVAdapter.MyViewHolder> {

    private final Products_RVInterface products_rvInterface;

    Context context;
    ArrayList<productModel> productModels;

    public Products_RVAdapter(Context context, ArrayList<productModel> productModels, Products_RVInterface products_rvInterface){
        this.context = context;
        this.productModels = productModels;
        this.products_rvInterface = products_rvInterface;
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

    }

    @Override
    public int getItemCount() {

        return productModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;

         public MyViewHolder(@NonNull View itemView, Products_RVInterface products_rvInterface) {
            super(itemView);

            tvName = itemView.findViewById(R.id.product_title);

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
