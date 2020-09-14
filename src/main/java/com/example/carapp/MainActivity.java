package com.example.carapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ImageView imageView,title1;

    TextView title2,desc;

    FirebaseAuth mAuth;
    Animation left,right,up,down;
    private static int SPLASH_SCREEN = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView);
        title1 = findViewById(R.id.title1);

        desc = findViewById(R.id.tagline);

        up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.top_animation);
        down = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_animation);
        left = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.left_animation);
        right = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.right_animation);

        imageView.setAnimation(up);
        title1.setAnimation(left);
        desc.setAnimation(down);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                Pair[] pairs = new Pair[3];

                pairs[0] = new Pair<View, String>(imageView, "logo_image");
                pairs[1] = new Pair<View, String>(findViewById(R.id.linear1), "logo_text");
                pairs[2] = new Pair<View, String>(findViewById(R.id.title1), "bottomLogo");


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                    startActivity(intent, options.toBundle());

                } else {
                    startActivity(intent);
                }


            }
        },SPLASH_SCREEN);


    }


}