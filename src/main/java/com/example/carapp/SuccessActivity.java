package com.example.carapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SuccessActivity extends AppCompatActivity {

    String Name,Email, Password, Phone ;
    boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_success);

        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Password = getIntent().getStringExtra("password");
        Phone = getIntent().getStringExtra("phone");
        registered = getIntent().getBooleanExtra("registered",false);;
    }

    public void callLogInPage(View view) {

        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.putExtra("name",Name);
        intent.putExtra("email",Email);
        intent.putExtra("password",Password);
        intent.putExtra("phone",Phone);
        intent.putExtra("registered",registered);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}