package com.example.carapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.CategoryActivity;
import com.example.carapp.HelperClass.CategoryModel;
import com.example.carapp.R;
import com.example.carapp.User.MallFragment;
import com.google.android.gms.common.internal.service.Common;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<CategoryModel> mData;
    int row_index = -1;

    public CategoryAdapter(List<CategoryModel> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String icon = mData.get(position).getCategoryIconLink();
        String name = mData.get(position).getCategoryName();

        holder.setCategoryIcon(icon);
        holder.setCategoryName(name);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {


              Fragment fragment;
              int position = holder.getAdapterPosition();
              String name = mData.get(holder.getAdapterPosition()).getCategoryName();


              switch (position){
                  case 0:
                      fragment = new MallFragment();
                      setFragment(v, fragment);
                      Toast.makeText(holder.itemView.getContext(), "Home", Toast.LENGTH_SHORT).show();
                      break;

                  default:
                      Intent intent = new Intent(holder.itemView.getContext(), CategoryActivity.class);
                      intent.putExtra("CategoryName",name);
                      holder.itemView.getContext().startActivity(intent);
                      Toast.makeText(holder.itemView.getContext(), "Welcome to "+name +" Section", Toast.LENGTH_SHORT).show();

              }



          }
      });


    }

    private void setFragment(View v, Fragment fragment) {
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryIcon;
        private TextView categoryName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
        }

        private void setCategoryIcon(String iconUrl){
            if (!iconUrl.equals("null")) {
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.drawable.home_icon_final)).into(categoryIcon);
            }
        }

        private void setCategoryName(String name){

            categoryName.setText(name);
        }
    }

}
