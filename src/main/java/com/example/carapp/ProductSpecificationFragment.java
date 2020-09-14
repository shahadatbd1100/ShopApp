package com.example.carapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.Adapter.ProductSpecificationAdapter;
import com.example.carapp.HelperClass.ProductSpecificationModel;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecificationFragment extends Fragment {

    private RecyclerView productSpecificationRecycleView;
    private ProductSpecificationAdapter productSpecificationAdapter;
    public ProductSpecificationFragment() {
        // Required empty public constructor
    }
    public List<ProductSpecificationModel> productSpecificationModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_product_specification, container, false);

        productSpecificationRecycleView = v.findViewById(R.id.product_specification_recycle_view);

        productSpecificationRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));



        productSpecificationAdapter = new ProductSpecificationAdapter(productSpecificationModelList);
        productSpecificationRecycleView.setAdapter(productSpecificationAdapter);

        return v;
    }
}