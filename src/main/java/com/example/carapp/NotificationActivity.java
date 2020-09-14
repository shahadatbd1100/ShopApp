package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.carapp.Adapter.NotificationAdapter;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.NotificationModel;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    public static NotificationAdapter adapter;
    private  boolean runQuery = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.notification_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.recycler_view_notification);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(NotificationActivity.this, DBquaries.notificationModelList);
        recyclerView.setAdapter(adapter);

        Map<String,Object> readMap = new HashMap<>();

         for (int x = 0 ; x <DBquaries.notificationModelList.size();x++){
             if (!DBquaries.notificationModelList.get(x).isReaded()){
                 runQuery = true;
             }
             readMap.put("Readed_"+x,true);
         }

         if (runQuery) {
             FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                     .update(readMap);
         }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int x = 0 ; x <DBquaries.notificationModelList.size();x++){
            DBquaries.notificationModelList.get(x).setReaded(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == android.R.id.home) {

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}