package com.example.carapp.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ProductImagesAdapter extends PagerAdapter {

    List<String> productImages;

    public ProductImagesAdapter(List<String> productImages) {
        this.productImages = productImages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView productImage = new ImageView(container.getContext());
        Glide.with(container.getContext()).load(productImages.get(position)).apply(new RequestOptions().placeholder(R.drawable.placeholder_photo)).into(productImage);
        container.addView(productImage,0);
        return productImage;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return productImages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
