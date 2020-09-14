package com.example.carapp.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.Adapter.CategoryAdapter;
import com.example.carapp.HelperClass.CategoryModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.LoginActivity;
import com.example.carapp.MyAccountFragment;
import com.example.carapp.MyCartFragment;
import com.example.carapp.MyOrdersFragment;
import com.example.carapp.MyRewardsFragment;
import com.example.carapp.NotificationActivity;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;
import com.example.carapp.SearchActivity;
import com.example.carapp.SignupActivity;
import com.example.carapp.WishlistFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.example.carapp.HelperClass.DBquaries.cartList;
import static com.example.carapp.HelperClass.DBquaries.categoryModelList;
import static com.example.carapp.HelperClass.DBquaries.firebaseFirestore;
import static com.example.carapp.HelperClass.DBquaries.lists;
import static com.example.carapp.HelperClass.DBquaries.loadCategories;
import static com.example.carapp.HelperClass.DBquaries.loadedCategoriesNames;

public class MainDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    public static DrawerLayout drawerLayout;
    ImageView drawerIcon, addProfileIcon;
    FrameLayout frameLayout;
    LinearLayout linearLayout;

    RecyclerView optionRecycler;
    CategoryAdapter categoryAdapter;

    TextView name, status, email_address, toolbarText, logout;
    String Name, Email, Password, Phone;
    public static boolean registered;
    private Toolbar toolbar;
    private Window window;
    TextView badgeCount;
    CircleImageView profileView;
    private LottieAnimationView no_internet_connection;


    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDER_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int REWARD_FRAGMENT = 4;
    private static final int MY_ACCOUNT_FRAGMENT = 5;
    public static boolean showCart = false;
    public static MenuItem cartItem;
    public static boolean resetMainActivity = false;
    FirebaseAuth mAuth ;
    FirebaseUser mFirebaseUser ;
//    public  static Activity mainActivity;

    public static ShimmerFrameLayout shimmer1, shimmer2, shimmer3, shimmer4, shimmer5;

    private int currentFragment = -1;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        invalidateOptionsMenu();

        Paper.init(this);
        no_internet_connection = findViewById(R.id.no_internet_connection);
        toolbarText = findViewById(R.id.textToolbar);
        linearLayout = findViewById(R.id.mainLinear);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
//        drawerIcon = findViewById(R.id.drawerIcon);

//        drawerIcon.setVisibility(View.GONE);

        View headerView = navigationView.getHeaderView(0);
        //////data from another intent

        no_internet_connection = findViewById(R.id.no_internet_connection1);
        name = headerView.findViewById(R.id.user_name);
        status = headerView.findViewById(R.id.status_info);
        profileView = headerView.findViewById(R.id.main_profile_image);
        email_address = headerView.findViewById(R.id.menu_slogan);
        addProfileIcon = headerView.findViewById(R.id.add_profile_icon);

        shimmer1 = findViewById(R.id.shimmer1);
        shimmer2 = findViewById(R.id.shimmer2);
        shimmer3 = findViewById(R.id.shimmer3);
        shimmer4 = findViewById(R.id.shimmer4);
        shimmer5 = findViewById(R.id.shimmer5);

        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Password = getIntent().getStringExtra("password");
        Phone = getIntent().getStringExtra("phone");
        registered = getIntent().getBooleanExtra("registered", false);

        Menu navMenu = navigationView.getMenu();

        if (registered) {
            registeredUserInfo();
            navMenu.findItem(R.id.nav_logout).setVisible(true);
            navMenu.findItem(R.id.nav_orders).setVisible(true);
            navMenu.findItem(R.id.nav_cart).setVisible(true);
            navMenu.findItem(R.id.nav_rewards).setVisible(true);
            navMenu.findItem(R.id.nav_wishlist).setVisible(true);
        } else {
            unRegisteredUserInfo();
            navMenu.findItem(R.id.nav_logout).setVisible(false);
            navMenu.findItem(R.id.nav_orders).setVisible(false);
            navMenu.findItem(R.id.nav_cart).setVisible(false);
            navMenu.findItem(R.id.nav_rewards).setVisible(false);
            navMenu.findItem(R.id.nav_wishlist).setVisible(false);
        }




        //////data from another intent


        navigationView.setCheckedItem(R.id.nav_mall);
        optionRecycler = findViewById(R.id.optionRecycler);


        categoryAdapter = new CategoryAdapter(categoryModelList);
        optionRecycler.setAdapter(categoryAdapter);
        optionRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        if (categoryModelList.size() == 0) {
            loadCategories(optionRecycler, this);

        } else {
            categoryAdapter.notifyDataSetChanged();
        }

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        navigationView.getMenu().getItem(0).setChecked(true);

//        drawerIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//
//                }
//            }
//        });

        frameLayout = findViewById(R.id.main_frame_layout);

        checkInternet();

    }


    @SuppressLint("WrongConstant")
    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {

            if (!showCart) {

                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                        this, drawerLayout, toolbar, 0, 0);
                drawerLayout.addDrawerListener(toggle);
                toggle.syncState();
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                toolbarText.setText("");
                drawerLayout.setDrawerLockMode(0);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                optionRecycler.setVisibility(View.VISIBLE);
//                drawerIcon.setVisibility(View.VISIBLE);
                params.setScrollFlags(scrollFlags);
            } else {
//                mainActivity = this;
                callFragment(new MyCartFragment(), -2);
                toolbarText.setText("Cart Menu");
                drawerLayout.setDrawerLockMode(1);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                drawerIcon.setVisibility(View.GONE);
                params.setScrollFlags(0);


            }
        } else {
            optionRecycler.setVisibility(View.GONE);
            no_internet_connection.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (currentFragment == HOME_FRAGMENT) {
            getMenuInflater().inflate(R.menu.main_dashboard, menu);



            cartItem = menu.findItem(R.id.main_cart_icon);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.cart_black_icon);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

            if (registered) {

                if (DBquaries.rewardsModelList.size() == 0) {
                    DBquaries.loadRewards(MainDashboard.this, new Dialog(this), false);
                }

                if (cartList.size() == 0) {
                    DBquaries.loadCart(MainDashboard.this, new Dialog(MainDashboard.this), false, badgeCount, new TextView(MainDashboard.this));
                } else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }
                }
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog signInDialog = new Dialog(MainDashboard.this);
                    signInDialog.setContentView(R.layout.sign_in_dialog);
                    signInDialog.setCancelable(true);
                    signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    Button dialogSignInBtn = signInDialog.findViewById(R.id.cancel_btn_dialog);
                    Button dialogSignUpBtn = signInDialog.findViewById(R.id.ok_btn_dialog);

                    if (registered) {
                        callFragment(new MyCartFragment(), CART_FRAGMENT);
                        toolbarText.setText("Cart Menu");
                    } else {
                        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginActivity.disableCloseBtn = true;
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LoginActivity.disableCloseBtn = true;
                                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                                startActivity(intent);
                            }
                        });

                        signInDialog.show();

                    }

                }
            });



            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.mynotification);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);

            if (registered){
                DBquaries.checkNotifications(false,notifyCount);
            }
            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_search_icon:
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.main_notification_icon:
                Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(notificationIntent);
                break;
            case R.id.main_cart_icon:
                final Dialog signInDialog = new Dialog(MainDashboard.this);
                signInDialog.setContentView(R.layout.sign_in_dialog);
                signInDialog.setCancelable(true);
                signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Button dialogSignInBtn = signInDialog.findViewById(R.id.cancel_btn_dialog);
                Button dialogSignUpBtn = signInDialog.findViewById(R.id.ok_btn_dialog);

                if (registered) {
                    callFragment(new MyCartFragment(), CART_FRAGMENT);
                    toolbarText.setText("Cart Menu");
                } else {
                    dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginActivity.disableCloseBtn = true;
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LoginActivity.disableCloseBtn = true;
                            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                            startActivity(intent);
                        }
                    });

                    signInDialog.show();

                }

                break;
            case android.R.id.home:
                if (showCart) {
//                    mainActivity = null;
                    showCart = false;
                    finish();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onPause() {
        super.onPause();

        if (registered) {
            DBquaries.checkNotifications(true, null);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!registered) {

        } else {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

            //////////////////
            DBquaries.loadOrders(MainDashboard.this,null,new Dialog(this));
            //////////////////
            if (DBquaries.email == null) {

                if (mFirebaseUser != null) {
                    FirebaseFirestore.getInstance().collection("USERS").document(mFirebaseUser.getUid())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DBquaries.fullName = task.getResult().getString("name");
                                DBquaries.email = task.getResult().getString("email");
                                DBquaries.profile = task.getResult().getString("profile");

                                name.setText(DBquaries.fullName);
                                email_address.setText(DBquaries.email);

                                if (DBquaries.profile.equals("")) {

                                    addProfileIcon.setVisibility(View.VISIBLE);
                                } else {
                                    addProfileIcon.setVisibility(View.INVISIBLE);
                                    Glide.with(MainDashboard.this).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(profileView);
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(MainDashboard.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }else {
                name.setText(DBquaries.fullName);
                email_address.setText(DBquaries.email);

                if (DBquaries.profile.equals("")) {
                    profileView.setImageResource(R.drawable.profile_photo);
                    addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(MainDashboard.this).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(profileView);
                }
            }
        }

        if (resetMainActivity) {
            resetMainActivity = false;
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);
        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
//        drawerLayout.closeDrawer(GravityCompat.START);

        android.app.Fragment fragment;
        switch (item.getItemId()) {
            case R.id.nav_mall:
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                toolbarText.setVisibility(View.INVISIBLE);
                drawerLayout.closeDrawer(GravityCompat.START); //for closing the Drawer
                optionRecycler.setVisibility(View.VISIBLE);
                supportInvalidateOptionsMenu();
                break;
            case R.id.nav_orders:
                drawerLayout.closeDrawer(GravityCompat.START);
                callFragment(new MyOrdersFragment(), ORDER_FRAGMENT);
                toolbarText.setText("My Orders");
                break;
            case R.id.nav_rewards:
                drawerLayout.closeDrawer(GravityCompat.START);
                callFragment(new MyRewardsFragment(), REWARD_FRAGMENT);
                toolbarText.setText("Rewards");
                break;
            case R.id.nav_cart:
                drawerLayout.closeDrawer(GravityCompat.START);
                callFragment(new MyCartFragment(), CART_FRAGMENT);
                toolbarText.setText("Cart Menu");
                break;

            case R.id.nav_wishlist:
                drawerLayout.closeDrawer(GravityCompat.START);
                callFragment(new WishlistFragment(), WISHLIST_FRAGMENT);
                toolbarText.setText("My Wishlist");
                break;
            case R.id.nav_account:
                if (registered) {

                    drawerLayout.closeDrawer(GravityCompat.START);
                    callFragment(new MyAccountFragment(), MY_ACCOUNT_FRAGMENT);
                    toolbarText.setText("Profile");
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                }

                break;

            case R.id.nav_logout:
                String UserPhoneKey = Paper.book().read(DBquaries.USERPHONEKEY);
                String UserPasswordKey = Paper.book().read(DBquaries.PASSWORDKEY);


                if (UserPhoneKey != "" && UserPasswordKey != "") {
                    if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                        Paper.book().destroy();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        DBquaries.clearData();
                        finish();
                    } else {
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }

                break;
        }
        return true;
    }

    private void callFragment(Fragment fragment, int fragNo) {
        invalidateOptionsMenu(); //for removing title bar
        toolbarText.setVisibility(View.VISIBLE);
        setFragment(fragment, fragNo);
        optionRecycler.setVisibility(View.GONE);
    }

    private void setFragment(Fragment fragment, int fragmentNo) {
//        if (fragmentNo != currentFragment) {
//        if (fragmentNo==REWARD_FRAGMENT){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.setStatusBarColor(Color.parseColor("#5B04B1"));
//                toolbar.setBackgroundColor(Color.parseColor("#5B04B1"));
//            }
//        }else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                window.setStatusBarColor(getResources().getColor(R.color.black));
//                toolbar.setBackgroundColor(getResources().getColor(R.color.black));
//            }
//        }
        currentFragment = fragmentNo;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        //}
    }


    private void registeredUserInfo() {
        FirebaseDatabase user = FirebaseDatabase.getInstance();
        DatabaseReference userRef = user.getReference().child("Users").child(Phone);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String serverName = snapshot.child("name").getValue(String.class);
                    String serverEmail = snapshot.child("email").getValue(String.class);
                    name.setText(serverName);
                    email_address.setText(serverEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        status.setText("Registered");
    }

    @Override
    public void onBackPressed() {
        if (currentFragment != HOME_FRAGMENT) {
            if (showCart) {
//                mainActivity = null;
                showCart = false;
                finish();
            } else {
                setFragment(new HomeFragment(), HOME_FRAGMENT);
                toolbarText.setVisibility(View.INVISIBLE);
                drawerLayout.closeDrawer(GravityCompat.START); //for closing the Drawer
                optionRecycler.setVisibility(View.VISIBLE);
                supportInvalidateOptionsMenu();
                navigationView.getMenu().getItem(0).setChecked(true);
            }
        } else {
            super.onBackPressed();
            finish();
            categoryModelList.clear();
            lists.clear();

        }
    }

    private void unRegisteredUserInfo() {
        name.setText("Guest");
        email_address.setText("Guest");
        status.setText("UnRegistered");
    }
}