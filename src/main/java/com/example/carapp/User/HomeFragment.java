package com.example.carapp.User;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.carapp.Adapter.HomePageAdapter;
import com.example.carapp.HelperClass.CategoryModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.HomePageModel;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.HelperClass.SliderModel;
import com.example.carapp.MainActivity;
import com.example.carapp.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.carapp.HelperClass.DBquaries.categoryModelList;
import static com.example.carapp.HelperClass.DBquaries.lists;
import static com.example.carapp.HelperClass.DBquaries.loadCategories;
import static com.example.carapp.HelperClass.DBquaries.loadFragmentData;
import static com.example.carapp.HelperClass.DBquaries.loadedCategoriesNames;

public class HomeFragment extends Fragment {


    List<HorizontalProduct> mProduct;
    List<HorizontalProduct> gProduct;
    HomePageAdapter adapter;
    public static SwipeRefreshLayout swipeRefreshLayout;



    private FirebaseFirestore firebaseFirestore;
    private LottieAnimationView no_internet_connection;
    long x = 1;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    RecyclerView homePageRecyclerView;
   //


    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

      //
        swipeRefreshLayout = v.findViewById(R.id.swip_refresh);
        no_internet_connection = v.findViewById(R.id.no_internet_connection);

        firebaseFirestore = FirebaseFirestore.getInstance();
        homePageRecyclerView = v.findViewById(R.id.testing);
        homePageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        //setFragment(new HomeFragment());
        if (networkInfo != null && networkInfo.isConnected()) {
            MainDashboard.drawerLayout.setDrawerLockMode(0);
            no_internet_connection.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            if (lists.size() == 0) {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                adapter = new HomePageAdapter(lists.get(0));
                loadFragmentData(adapter, getContext(), 0, "HOME");
                //shimmer1.stopShimmer();
            } else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);

        } else {
            homePageRecyclerView.setVisibility(View.GONE);
//           Glide.with(this).load(R.drawable.no_connection).into(no_internet_connection);
            no_internet_connection.setVisibility(View.VISIBLE);
        }


        ///////////////swipe refresh

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                homePageRecyclerView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(true); //loading bar showing

                connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectivityManager.getActiveNetworkInfo();
//                categoryModelList.clear();
//                lists.clear();
//                loadedCategoriesNames.clear();


                DBquaries.clearData();

                if (networkInfo != null && networkInfo.isConnected()) {
                    no_internet_connection.setVisibility(View.GONE);
                    homePageRecyclerView.setVisibility(View.VISIBLE);
                    loadedCategoriesNames.add("HOME");
                    lists.add(new ArrayList<HomePageModel>());
                    loadFragmentData(adapter, getContext(), 0, "HOME");

                } else {
                  //  homePageRecyclerView.setVisibility(View.GONE);
                    no_internet_connection.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        return v;
    }
}