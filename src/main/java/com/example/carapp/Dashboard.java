package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Dashboard extends AppCompatActivity {
    TextView name,status;

    String Name,Email, Password, Phone ;
    boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        name = findViewById(R.id.textView);
        status = findViewById(R.id.textView2);

        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Password = getIntent().getStringExtra("password");
        Phone = getIntent().getStringExtra("phone");
        registered = getIntent().getBooleanExtra("registered",false);



        if (registered){


            registeredUserInfo();
        }
        else
        {
            unRegisteredUserInfo();

        }

    }

    private void unRegisteredUserInfo() {
        name.setText("Guest");
        status.setText("UnRegistered");
    }

    private void registeredUserInfo() {
        FirebaseDatabase user = FirebaseDatabase.getInstance();
        DatabaseReference userRef = user.getReference().child("Users").child(Phone);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String serverName = snapshot.child("name").getValue(String.class);
                    name.setText(serverName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        status.setText("Registered");
    }
}