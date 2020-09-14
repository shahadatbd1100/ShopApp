package com.example.carapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.carapp.Adapter.CartAdapter;
import com.example.carapp.HelperClass.CartItemModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.RewardsModel;
import com.example.carapp.User.MainDashboard;

import java.util.ArrayList;
import java.util.List;


public class MyCartFragment extends Fragment {

    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemsRecyclerView;
    List<CartItemModel> cartItemModelList;
    public  static  CartAdapter cartAdapter;
    private Button continueBtn;
    private Dialog loadingDialog;
    private  TextView total;
    private LinearLayout bottom_linear;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);


        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();


        bottom_linear = view.findViewById(R.id.bottom_linear1);
        continueBtn = view.findViewById(R.id.cart_continue_btn);
        total = view.findViewById(R.id.total_cart_amount);
        cartItemsRecyclerView = view.findViewById(R.id.cart_items_recycler_view);
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        MainDashboard.registered = true;

        cartAdapter = new CartAdapter(DBquaries.cartItemModelList,total,true);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart =true;

                for (int x=0 ;x<DBquaries.cartItemModelList.size();x++){
                    CartItemModel cartItemModel = DBquaries.cartItemModelList.get(x);
                    if (cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                loadingDialog.show();

                if (DBquaries.addressModelsList.size()==0) {

                    DBquaries.loadAddresses(getContext(), loadingDialog,true,false);
                }else {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(intent);
                }

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        cartAdapter.notifyDataSetChanged();

        if (DBquaries.rewardsModelList.size()==0){
            loadingDialog.show();
            DBquaries.loadRewards(getContext(),loadingDialog,false);
        }

        if (DBquaries.cartItemModelList.size() == 0){
            DBquaries.cartList.clear();
            DBquaries.loadCart(getContext(),loadingDialog,true,new TextView(getContext()),total);
        }
        else {
            if (DBquaries.cartItemModelList.get(DBquaries.cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT){
                bottom_linear.setVisibility(View.VISIBLE);
            }
            loadingDialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (CartItemModel cartItemModel: DBquaries.cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCouponId())){
                for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {
                    if (rewardsModel.getCouponId().equals(cartItemModel.getSelectedCouponId())) {
                        rewardsModel.setAlreadyUsed(false);
                    }
                }

                cartItemModel.setSelectedCouponId(null);

                if (MyRewardsFragment.rewardsAdapter != null) {
                    MyRewardsFragment.rewardsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}