package com.example.carapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HorizontalProductAdapter extends RecyclerView.Adapter<HorizontalProductAdapter.ProductViewHolder> {

    List<HorizontalProduct> mProduct;

    public HorizontalProductAdapter(List<HorizontalProduct> mProduct) {
        this.mProduct = mProduct;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_horizontal_product_item_layout,parent,false);
        ProductViewHolder productViewHolder = new ProductViewHolder(v);
        return productViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {

        String image = mProduct.get(position).getImage();
        final String itemID = mProduct.get(position).getProductId();
        holder.title.setText(mProduct.get(position).getTitle());
        holder.desc.setText(mProduct.get(position).getDesc());
        holder.price.setText("Tk."+mProduct.get(position).getPrice()+"/-");
        holder.setProductImage(image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID",itemID);
                holder.itemView.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mProduct.size()>8) {
            return 8;
        }
        else
        {
            return mProduct.size();
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder  {

        ImageView image;
        TextView title,desc,price;
        public ProductViewHolder(@NonNull final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.horizontal_item_image_view);
            title = itemView.findViewById(R.id.horizontal_item_title_text_view);
            desc = itemView.findViewById(R.id.horizontal_item_desc_text_view);
            price = itemView.findViewById(R.id.horizontal_item_price_text_view);

        }
        private void setProductImage(String imageView){
            Glide.with(itemView.getContext()).load(imageView).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(image);
        }
    }
}
