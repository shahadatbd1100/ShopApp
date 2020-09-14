package com.example.carapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carapp.HelperClass.ProductSpecificationModel;
import com.example.carapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductSpecificationAdapter extends RecyclerView.Adapter<ProductSpecificationAdapter.ProductViewHolder> {

    List<ProductSpecificationModel> specificationData;

    public ProductSpecificationAdapter(List<ProductSpecificationModel> specificationData) {
        this.specificationData = specificationData;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_layout, parent, false);
        ProductViewHolder productViewHolder = new ProductViewHolder(view);
        return productViewHolder;


    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.title.setText(specificationData.get(position).getFeatureName());
        holder.desc.setText(specificationData.get(position).getFeatureDesc());

    }

    @Override
    public int getItemCount() {
        return specificationData.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.feature_name);
            desc = itemView.findViewById(R.id.feature_description);
        }
    }


}
