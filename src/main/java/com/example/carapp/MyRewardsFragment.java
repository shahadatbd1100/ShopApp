package com.example.carapp;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.Adapter.RewardsAdapter;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.RewardsModel;
import com.example.carapp.User.MainDashboard;

import java.util.ArrayList;
import java.util.List;

public class MyRewardsFragment extends Fragment {

    private List<RewardsModel> rewardsModelList;
    public static RewardsAdapter rewardsAdapter;
    private RecyclerView rewardRecyclerView;
    private Dialog loadingDialog;

    public MyRewardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        rewardRecyclerView = v.findViewById(R.id.rewardRecyclerView);
        rewardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        rewardsAdapter = new RewardsAdapter(DBquaries.rewardsModelList,false);
        rewardRecyclerView.setAdapter(rewardsAdapter);
        rewardsAdapter.notifyDataSetChanged();

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        if (DBquaries.rewardsModelList.size() == 0){
            DBquaries.loadRewards(getContext(),loadingDialog,true);
        }
        else {
            loadingDialog.dismiss();
        }


        return v;
    }
}