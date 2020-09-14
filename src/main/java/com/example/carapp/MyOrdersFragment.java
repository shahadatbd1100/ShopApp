package com.example.carapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.Adapter.OrderAdapter;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersFragment extends Fragment {


    public MyOrdersFragment() {
        // Required empty public constructor
    }

    private RecyclerView orderRecyclerView;
    public static OrderAdapter orderAdapter;
    List<OrderModel> orderData;
    public static Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_orders, container, false);

        orderRecyclerView = v.findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        orderAdapter = new OrderAdapter(DBquaries.myOrderItemModelList, loadingDialog);
        orderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        if (DBquaries.myOrderItemModelList.size() == 0) {
            DBquaries.loadOrders(getContext(), orderAdapter, loadingDialog);
        } else {
            loadingDialog.dismiss();
        }


        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        orderAdapter.notifyDataSetChanged();
    }
}