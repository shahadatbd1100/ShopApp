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

public class NewGridProductAdapter extends RecyclerView.Adapter<NewGridProductAdapter.MyGridHolder> {
    List<HorizontalProduct> gProduct;
    public NewGridProductAdapter(List<HorizontalProduct> gProduct) {
        this.gProduct = gProduct;
    }
    @NonNull
    @Override
    public MyGridHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.staggerded_item_layout,parent,false);
        MyGridHolder myGridHolder = new MyGridHolder(view);
        return myGridHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyGridHolder holder, final int position) {
        //holder.productImage.setImageResource(gProduct.get(position).getImage());

        Glide.with(holder.itemView.getContext()).load(gProduct.get(position).getImage()).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(holder.productImage);
        holder.title.setText(gProduct.get(position).getTitle());
        holder.desc.setText(gProduct.get(position).getDesc());
        holder.price.setText("Tk."+gProduct.get(position).getPrice()+"/-");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item_id = gProduct.get(position).getProductId();
                Intent intent = new Intent(holder.itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID",item_id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
//        String image = gProduct.get(position).getImage();
//        holder.setImage(image);
    }

    @Override
    public int getItemCount() {
        return gProduct.size();
    }

    public class MyGridHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView title,desc,price;
        public MyGridHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.horizontal_item_image_view);
            title = itemView.findViewById(R.id.horizontal_item_title_text_view);
            desc = itemView.findViewById(R.id.horizontal_item_desc_text_view);
            price = itemView.findViewById(R.id.horizontal_item_price_text_view);




        }
//        public void setImage(String myImage){
//            Glide.with(itemView.getContext()).load(myImage).apply(new RequestOptions().placeholder(R.drawable.strip_ads)).into(productImage);
//        }
    }
}
