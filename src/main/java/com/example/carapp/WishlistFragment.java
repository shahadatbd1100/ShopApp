package com.example.carapp;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.Adapter.WishlistAdapter;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.WishlistModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment {

    public WishlistFragment() {
        // Required empty public constructor
    }

    private RecyclerView wishlistRecyclerView;
    public static WishlistAdapter wishlistAdapter;
    private Dialog loadingDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_wishlist, container, false);
        wishlistRecyclerView = v.findViewById(R.id.wishlist_recycler_view);
        wishlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        if (DBquaries.wishlistModelList.size() == 0){
            DBquaries.wishList.clear();
            DBquaries.loadWishList(getContext(),loadingDialog,true);
        }
        else {
            loadingDialog.dismiss();
        }


        wishlistAdapter = new WishlistAdapter(DBquaries.wishlistModelList,true);
        wishlistRecyclerView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();



        return v;
    }
}