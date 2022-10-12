package com.example.storekeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Products_RVAdapter extends RecyclerView.Adapter<Products_RVAdapter.MyViewHolder> {

    Context context;
    ArrayList<productModel> productModels;

    public Products_RVAdapter(Context context, ArrayList<productModel> productModels){
        this.context = context;
        this.productModels = productModels;
    }

    @NonNull
    @Override
    public Products_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.product_row,parent,false);

        return new Products_RVAdapter.MyViewHolder(view);
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

         public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.product_title);
        }
    }
}
