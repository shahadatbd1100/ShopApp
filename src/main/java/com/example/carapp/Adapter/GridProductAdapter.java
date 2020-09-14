package com.example.carapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;

import java.util.List;

public class GridProductAdapter extends BaseAdapter {
    List<HorizontalProduct> gProduct;

    public GridProductAdapter(List<HorizontalProduct> gProduct) {
        this.gProduct = gProduct;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view ;
       if (convertView==null)
       {
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_horizontal_product_item_layout,parent,false);
           ImageView productImage = view.findViewById(R.id.horizontal_item_image_view);
           TextView title = view.findViewById(R.id.horizontal_item_title_text_view);
           TextView desc = view.findViewById(R.id.horizontal_item_desc_text_view);
           TextView price = view.findViewById(R.id.horizontal_item_price_text_view);

           ///on click listener setup per object
           view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(parent.getContext(), ProductDetailsActivity.class);
                   intent.putExtra("PRODUCT_ID",gProduct.get(position).getProductId());
                   parent.getContext().startActivity(intent);
               }
           });

           Glide.with(parent.getContext()).load(gProduct.get(position)).apply(new RequestOptions().placeholder(R.drawable.strip_ads)).into(productImage);
           title.setText(gProduct.get(position).getTitle());
           desc.setText(gProduct.get(position).getDesc());
           price.setText("Tk."+gProduct.get(position).getPrice()+"/-");
       }
       else
       {

           view = convertView;
       }
       return view;
    }
}
