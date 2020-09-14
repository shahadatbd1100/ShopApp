package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.carapp.Adapter.HomePageAdapter;
import com.example.carapp.HelperClass.HomePageModel;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.HelperClass.SliderModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.carapp.HelperClass.DBquaries.lists;
import static com.example.carapp.HelperClass.DBquaries.loadFragmentData;
import static com.example.carapp.HelperClass.DBquaries.loadedCategoriesNames;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoryRecyclerView;
    List<SliderModel> mData;
    List<HorizontalProduct> mProduct;
    List<HorizontalProduct> gProduct;
    HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.categoryToolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mData = new ArrayList<>();




        mProduct = new ArrayList<>();



        // Grid product layout//////
        gProduct = new ArrayList<>();

        /////////////////////////

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        int listPosition = 0;
       for (int x = 0 ; x<loadedCategoriesNames.size();x++){
           if (loadedCategoriesNames.get(x).equals(title.toUpperCase())){
               listPosition = x;
           }
       }
       if (listPosition==0){
           loadedCategoriesNames.add(title.toUpperCase());
           lists.add(new ArrayList<HomePageModel>());
           adapter = new HomePageAdapter(lists.get(loadedCategoriesNames.size()-1));
           loadFragmentData(adapter, this,loadedCategoriesNames.size()-1,title);
       }else {
           adapter = new HomePageAdapter(lists.get(listPosition));
       }
        //strip ads
        // homePageModelList.add(new HomePageModel(1, R.drawable.strip_ads));
        

        //strip ads
        //homePageModelList.add(new HomePageModel(1, R.drawable.strip_ads));


        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_icon, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.category_search_icon) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId()==android.R.id.home)
        {
            finish();
            return true;
        }
        else {
            return false;
        }
    }
}