package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.carapp.Adapter.GridProductAdapter;
import com.example.carapp.Adapter.NewGridProductAdapter;
import com.example.carapp.Adapter.WishlistAdapter;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.HelperClass.WishlistModel;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView gridRecyclerView;
    public static List<HorizontalProduct> gridData;
    public static  List<WishlistModel> wishlistModelList ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        toolbar = findViewById(R.id.toolbarNew);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int layoutCode = getIntent().getIntExtra("layout_code", -1);

        recyclerView = findViewById(R.id.recycler_view);
        gridRecyclerView = findViewById(R.id.grid_recycler_view);

        if (layoutCode == 0) {
            recyclerView.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            WishlistAdapter wishlistAdapter = new WishlistAdapter(wishlistModelList, false);
            recyclerView.setAdapter(wishlistAdapter);
            wishlistAdapter.notifyDataSetChanged();
        }
        else if (layoutCode == 1) {

            gridRecyclerView.setVisibility(View.VISIBLE);
            gridRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


            NewGridProductAdapter gridProductAdapter = new NewGridProductAdapter(gridData);
            gridRecyclerView.setAdapter(gridProductAdapter);
            gridProductAdapter.notifyDataSetChanged();
        }

        }


        @Override
        public boolean onOptionsItemSelected (@NonNull MenuItem item){

            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }