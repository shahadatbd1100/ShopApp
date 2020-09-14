package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.Adapter.ProductDetailsAdapter;
import com.example.carapp.Adapter.ProductImagesAdapter;
import com.example.carapp.Adapter.RewardsAdapter;
import com.example.carapp.HelperClass.CartItemModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.ProductSpecificationModel;
import com.example.carapp.HelperClass.RewardsModel;
import com.example.carapp.HelperClass.WishlistModel;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.carapp.HelperClass.DBquaries.cartList;
import static com.example.carapp.User.MainDashboard.showCart;

public class ProductDetailsActivity extends AppCompatActivity {
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;
    List<Integer> productImages;
    List<RewardsModel> rewardsModelList;
    private boolean inStock = false;

    private Button couponRedeemBtn;

    private Dialog loadingDialog;
    public static MenuItem cartItem;
//    public static Activity productDetailsActivity;

    public static boolean fromSearch = false;

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    ////couponDialog
    private TextView couponTitle, couponBody, couponExpiryDate;
    private RecyclerView couponRecyclerView;
    private LinearLayout selectedCoupon;
    private TextView originalPrice;
    private TextView discountedPrice;
    ////couponDialog

    private RewardsAdapter rewardsAdapter;

    List<String> productDetailsDesc;
    private TextView productTitle;
    private TextView productAverageRating;
    private TextView productTotalRating;
    private TextView productTotalPrice;
    private TextView productCuttedPrice;
    private TextView wishlist_product_cash_on_delivery;
    private TextView wishlist_product_availability_delivery, averageRating;

    //product description


    private LinearLayout available_layout;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private TabLayout productDetailsTabLayout;
    private ConstraintLayout productDetailsLayout;
    private ConstraintLayout productDetailsLayoutTabbed;
    private ViewPager productDetailsViewPager;
    private String productDescription;
    private String productOtherDetails;
    private TextView productOnlyDescriptionBody;
    //product description
    private TextView reward_body;
    private TextView badgeCount;

    private String productOriginalPrice;

    private LinearLayout buyNowLinear;

    private Button buyNowBtn, addToCart;
    Dialog signInDialog;

    ////rating bar layout
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingProgressBarContainer;
    private LinearLayout ratingNoContainer;
    ////rating bar layout
    public static boolean ALREADY_ADDED_TO_WISH_LIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;
    public static FloatingActionButton addToWishListBtn;
    private FirebaseFirestore firebaseFirestore;
    public static String itemId;
    private FirebaseUser currentUser;
    private DocumentSnapshot documentSnapshot;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MainDashboard.showCart = false;

        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        productDetailsLayout = findViewById(R.id.product_details_container);
        productDetailsLayoutTabbed = findViewById(R.id.product_details_tabs_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);

        addToCart = findViewById(R.id.addToCart);
        averageRating = findViewById(R.id.average_rating);
        productTitle = findViewById(R.id.wishlist_product_title);
        productAverageRating = findViewById(R.id.wishlist_product_rating);
        productTotalRating = findViewById(R.id.wishlist_product_total_rating);
        productTotalPrice = findViewById(R.id.product_main_price);
        productCuttedPrice = findViewById(R.id.product_old_price);
        wishlist_product_cash_on_delivery = findViewById(R.id.wishlist_product_cash_on_delivery);
        wishlist_product_availability_delivery = findViewById(R.id.wishlist_product_availability_delivery);
        reward_body = findViewById(R.id.reward_text_not_important);
        ratingNoContainer = findViewById(R.id.rating_no_container);
        ratingProgressBarContainer = findViewById(R.id.rating_progressbar_container);
        available_layout = findViewById(R.id.available_layout);

        totalRatings = findViewById(R.id.total_ratings);

        itemId = getIntent().getStringExtra("PRODUCT_ID");
        ////loadingDialog///
        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        ///Loading Dialog////

        //////coupon Dialog////////


        final Dialog couponDialog = new Dialog(ProductDetailsActivity.this);
        couponDialog.setContentView(R.layout.coupon_redeem_dialog);
        couponDialog.setCancelable(true);
        couponDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        couponRecyclerView = couponDialog.findViewById(R.id.coupons_recycler_view);
        selectedCoupon = couponDialog.findViewById(R.id.selected_coupon);

        couponTitle = couponDialog.findViewById(R.id.coupon_title);
        couponExpiryDate = couponDialog.findViewById(R.id.coupon_validity);
        couponBody = couponDialog.findViewById(R.id.coupon_body);
        ImageView toggleRecyclerVIew = couponDialog.findViewById(R.id.toggle_recycler_view);

        originalPrice = couponDialog.findViewById(R.id.original_price);
        discountedPrice = couponDialog.findViewById(R.id.discounted_price);
        couponRecyclerView.setLayoutManager(new LinearLayoutManager(ProductDetailsActivity.this));



        toggleRecyclerVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });

        selectedCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });

        //////coupon Dialog////////


        final List<String> productImages = new ArrayList<>();
        firebaseFirestore.collection("PRODUCTS").document(itemId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                documentSnapshot = task.getResult();
                firebaseFirestore.collection("PRODUCTS").document(itemId).collection("QUANTITY")
                        .orderBy("time", Query.Direction.ASCENDING).get()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {

                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                }
                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                productImagesViewPager.setAdapter(productImagesAdapter);

                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                productAverageRating.setText(documentSnapshot.get("average_rating").toString());
                                productTotalRating.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")");
                                productTotalPrice.setText("Tk." + documentSnapshot.get("product_price").toString() + "/-");

                                ///for coupon Dialog///
                                originalPrice.setText(productTotalPrice.getText());
                                productOriginalPrice =documentSnapshot.get("product_price").toString();
                                rewardsAdapter = new RewardsAdapter(DBquaries.rewardsModelList, true,couponRecyclerView,selectedCoupon,productOriginalPrice,couponTitle,couponExpiryDate,couponBody,discountedPrice);
                                couponRecyclerView.setAdapter(rewardsAdapter);
                                rewardsAdapter.notifyDataSetChanged();
                                ///for coupon Dialog///

                                productCuttedPrice.setText("Tk." + documentSnapshot.get("cutted_price").toString() + "/-");
                                if ((boolean) documentSnapshot.get("COD")) {
                                    wishlist_product_cash_on_delivery.setText("Cash On Delivery");
                                } else {
                                    wishlist_product_cash_on_delivery.setText("Paid");
                                }
//                                if ((boolean) documentSnapshot.get("in_stock")) {
//                                    wishlist_product_availability_delivery.setText("AvailAble");
//                                } else {
//                                    wishlist_product_availability_delivery.setText("Out of stock");
//                                    available_layout.setBackgroundColor(R.drawable.rating_circle_background_red);
//                                }
                                reward_body.setText(documentSnapshot.get("free_coupon_body").toString());
                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                    productDetailsLayoutTabbed.setVisibility(View.VISIBLE);
                                    productDetailsLayout.setVisibility(View.GONE);
                                    productDescription = documentSnapshot.get("product_description").toString();
                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                    //                        for (long x = 1; x<(long)documentSnapshot.get("total_spec_titles")+1;x++) {
                                    //                           productSpecificationModelList.add(new ProductSpecificationModel(documentSnapshot.get("spec_title_" + x).toString(), documentSnapshot.get("spec_title_" + x + "_field_" + x + "_name").toString()));
                                    //
                                    //                            for (long y = 1; y < (long) documentSnapshot.get("spec_title_"+x+"_total_fields")+1;y++){
                                    //                                productSpecificationModelList.add(new ProductSpecificationModel(documentSnapshot.get("spec_title_"+x).toString(), documentSnapshot.get("spec_title_" + x + "_field_" + x + "_name").toString()));
                                    //
                                    //                            }
                                    //
                                    //                        }
                                    for (long x = 1; x < (long) documentSnapshot.get("spec_value"); x++) {
                                        productSpecificationModelList.add(new ProductSpecificationModel(documentSnapshot.get("spec_title_" + x).toString(), documentSnapshot.get("spec_value_" + x).toString()));
                                    }

                                } else {
                                    productDetailsLayoutTabbed.setVisibility(View.GONE);
                                    productDetailsLayout.setVisibility(View.VISIBLE);
                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                }
                                totalRatings.setText(((long) documentSnapshot.get("total_ratings") + 1 + ""));
                                for (int x = 0; x < 5; x++) {
                                    TextView rating = (TextView) ratingNoContainer.getChildAt(x);
                                    rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                    ProgressBar progressBar = (ProgressBar) ratingProgressBarContainer.getChildAt(x);
                                    int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                    progressBar.setMax(maxProgress);
                                    progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));

                                }
                                averageRating.setText(documentSnapshot.get("average_rating").toString());

                                ProductDetailsAdapter productDetailsAdapter = new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList);
                                productDetailsViewPager.setAdapter(productDetailsAdapter);

                                if (MainDashboard.registered) {

                                    if (DBquaries.myRating.size() == 0) {
                                        DBquaries.loadRating(ProductDetailsActivity.this);
                                    }
                                    if (DBquaries.cartList.size() == 0) {
                                        DBquaries.loadCart(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                    }
                                    if (DBquaries.wishList.size() == 0) {
                                        DBquaries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
                                    }if (DBquaries.rewardsModelList.size()==0){
                                        DBquaries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
                                    }
                                    if (DBquaries.cartList.size() != 0 && DBquaries.wishList.size() != 0 && DBquaries.rewardsModelList.size()!=0 ){
                                        loadingDialog.dismiss();
                                    }

                                }else {
                                    loadingDialog.dismiss();
                                }
                                if (DBquaries.myRatedIds.contains(itemId)) {
                                    int index = DBquaries.myRatedIds.indexOf(itemId);
                                    initialRating = Integer.parseInt(String.valueOf(DBquaries.myRating.get(index))) - 1;
                                    setRating(initialRating);
                                }

                                if (DBquaries.cartList.contains(itemId)) {
                                    ALREADY_ADDED_TO_CART = true;
                                } else {
                                    ALREADY_ADDED_TO_CART = false;
                                }

                                if (DBquaries.wishList.contains(itemId)) {
                                    ALREADY_ADDED_TO_WISH_LIST = true;
                                    addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.errorRed));
                                } else {
                                    loadingDialog.dismiss();
                                    ALREADY_ADDED_TO_WISH_LIST = false;
                                    addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                }


                                if (task1.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {

                                    inStock = true;
                                    buyNowLinear.setVisibility(View.VISIBLE);
                                    addToCart.setOnClickListener(v -> {
                                        if (MainDashboard.registered) {
                                            ////////////////////////

                                            if (!running_cart_query) {
                                                running_cart_query = true;
                                                if (ALREADY_ADDED_TO_CART) {

                                                    running_cart_query = true;
                                                    Toast.makeText(ProductDetailsActivity.this, "Already added To cart", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    String pValue = String.valueOf(DBquaries.cartList.size());
                                                    Map<String, Object> addProduct = new HashMap<>();
                                                    addProduct.put("product_ID_" + pValue, itemId);
                                                    addProduct.put("list_size", (long) DBquaries.cartList.size() + 1);

                                                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                            .document("MY_CART")
                                                            .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task12) {
                                                            if (task12.isSuccessful()) {


                                                                if (DBquaries.wishlistModelList.size() != 0) {
                                                                    DBquaries.cartItemModelList.add(0, new CartItemModel(
                                                                            documentSnapshot.getBoolean("COD")
                                                                            ,CartItemModel.CART_ITEM
                                                                            , itemId
                                                                            , documentSnapshot.get("product_image_1").toString()
                                                                            , documentSnapshot.get("product_title").toString()
                                                                            , (long) documentSnapshot.get("free_coupons")
                                                                            , documentSnapshot.get("product_price").toString()
                                                                            , documentSnapshot.get("cutted_price").toString()
                                                                            , 1
                                                                            , (long)documentSnapshot.get("offers_applied")
                                                                            , 1
                                                                            , inStock
                                                                            , (long) documentSnapshot.get("max_quantity")
                                                                            , (long) documentSnapshot.get("stock_quantity")));
                                                                }
                                                                ALREADY_ADDED_TO_CART = true;
                                                                DBquaries.cartList.add(itemId);
                                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                                                invalidateOptionsMenu();
                                                                running_cart_query = false;
                                                            } else {
                                                                running_cart_query = false;
                                                                Toast.makeText(ProductDetailsActivity.this, "Could Not added to Cart at that moment", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });

                                                }


                                            }


                                        } else {
                                            signInDialog.show();
                                        }
                                    });
                                } else {
                                    inStock = false;
                                    buyNowBtn.setVisibility(View.GONE);
                                    buyNowLinear.setVisibility(View.GONE);
                                    addToCart.setText("Out Of Stock");
                                    addToCart.setTextColor(getResources().getColor(R.color.errorRed));
                                    addToCart.setCompoundDrawables(null, null, null, null);

                                }
                            } else {
                                String error = task1.getException().getMessage();
                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });


            } else {
                loadingDialog.dismiss();
                Toast.makeText(ProductDetailsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        couponRedeemBtn = findViewById(R.id.coupon_redeem_btn);

        initialRating = -1;
        buyNowBtn = findViewById(R.id.buy_now_btn);
        buyNowLinear = findViewById(R.id.buyNowLinear);

        productImagesViewPager = findViewById(R.id.product_images_view_pager);
        viewPagerIndicator = findViewById(R.id.viewPagerIndicator);
        addToWishListBtn = findViewById(R.id.add_to_wishList);

        productDetailsViewPager = findViewById(R.id.product_details_view_pager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);

        addToWishListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainDashboard.registered) {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISH_LIST) {
                            int index = DBquaries.wishList.indexOf(itemId);
                            DBquaries.removeFromWishList(index, ProductDetailsActivity.this);
                            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));

                            Toast.makeText(ProductDetailsActivity.this, "Removed from wishlist", Toast.LENGTH_SHORT).show();

                        } else {

                            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.errorRed));
                            String pValue = String.valueOf(DBquaries.wishList.size());
                            Map<String, Object> productId = new HashMap<>();
                            productId.put("product_ID_" + pValue, itemId);
                            productId.put("list_size", (long) DBquaries.wishList.size() + 1);


                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                    .document("MY_WISHLIST")
                                    .update(productId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (DBquaries.wishlistModelList.size() != 0) {
                                            DBquaries.wishlistModelList.add(new WishlistModel(itemId
                                                    , documentSnapshot.get("product_image_1").toString()
                                                    , documentSnapshot.get("product_title").toString()
                                                    , (long) documentSnapshot.get("free_coupons")
                                                    , documentSnapshot.get("average_rating").toString()
                                                    , (long) documentSnapshot.get("total_ratings")
                                                    , documentSnapshot.get("product_price").toString()
                                                    , documentSnapshot.get("cutted_price").toString()
                                                    , (boolean) documentSnapshot.get("COD")
                                                    , inStock));
                                        }


                                        addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.errorRed));
                                        ALREADY_ADDED_TO_WISH_LIST = true;
                                        DBquaries.wishList.add(itemId);
                                        Toast.makeText(ProductDetailsActivity.this, "Added to wishlist", Toast.LENGTH_SHORT).show();


                                    } else {
                                        addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        Toast.makeText(ProductDetailsActivity.this, "Could Not added to wishlist at that moment", Toast.LENGTH_SHORT).show();
                                    }
                                    running_wishlist_query = false;

                                }
                            });

                        }


                    }
                } else {
                    signInDialog.show();
                }
            }
        });


        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);


        productDetailsViewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));


        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Rating layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (MainDashboard.registered) {

                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;

                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();

                                long count = 0;
                                long item1 = (long) documentSnapshot.get("1_star");
                                long item2 = (long) documentSnapshot.get("2_star") * 2;
                                long item3 = (long) documentSnapshot.get("3_star") * 3;
                                long item4 = (long) documentSnapshot.get("4_star") * 4;
                                long item5 = (long) documentSnapshot.get("5_star") * 5;
                                long totalAV = (long) documentSnapshot.get("total_ratings");

                                count = (item1 + item2 + item3 + item4 + item5) / totalAV;

                                if (DBquaries.myRatedIds.contains(itemId)) {
                                    //already rated

                                    TextView oldRating = (TextView) ratingNoContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingNoContainer.getChildAt(5 - starPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star", (long) documentSnapshot.get(initialRating + 1 + "_star") - 1);
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", (calculateAverageRating((long) starPosition - initialRating, true)));

                                } else {
                                    //new rated

                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_rating", (calculateAverageRating((long) starPosition + 1, false)));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }

                                firebaseFirestore.collection("PRODUCTS").document(itemId)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Map<String, Object> myRating = new HashMap<>();

                                            if (DBquaries.myRatedIds.contains(itemId)) {

                                                myRating.put("rating_" + DBquaries.myRatedIds.indexOf(itemId), (long) starPosition + 1);

                                            } else {
                                                myRating.put("list_size", (long) DBquaries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DBquaries.myRatedIds.size(), itemId);
                                                myRating.put("rating_" + DBquaries.myRatedIds.size(), (long) starPosition + 1);

                                            }
                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        if (DBquaries.myRatedIds.contains(itemId)) {

                                                            DBquaries.myRating.set(DBquaries.myRatedIds.indexOf(itemId), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingNoContainer.getChildAt(5 - (initialRating + 1));
                                                            TextView finalRating = (TextView) ratingNoContainer.getChildAt(5 - (starPosition - 1));

                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) - 1));

                                                        } else {

                                                            DBquaries.myRatedIds.add(itemId);
                                                            DBquaries.myRating.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingNoContainer.getChildAt(5 - starPosition - 1);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

////////////////////////////
                                                            productTotalRating.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")");
                                                            totalRatings.setText(((long) documentSnapshot.get("total_ratings")) + 1 + " ratings");

                                                            Toast.makeText(ProductDetailsActivity.this, "Thank You fot your Rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {

                                                            TextView ratingFigures = (TextView) ratingNoContainer.getChildAt(x);

                                                            ProgressBar progressBar = (ProgressBar) ratingProgressBarContainer.getChildAt(x);
                                                            if (!DBquaries.myRatedIds.contains(itemId)) {


                                                                long count = 0;
                                                                long item1 = (long) documentSnapshot.get("1_star");
                                                                long item2 = (long) documentSnapshot.get("2_star");
                                                                long item3 = (long) documentSnapshot.get("3_star");
                                                                long item4 = (long) documentSnapshot.get("4_star");
                                                                long item5 = (long) documentSnapshot.get("5_star");
                                                                long totalAV = (long) documentSnapshot.get("total_ratings");

                                                                count = (item1 + item2 + item3 + item4 + item5);

                                                                int maxProgress = Integer.parseInt(String.valueOf(count));
                                                                progressBar.setMax(maxProgress);
                                                            }
                                                            progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));

                                                        }
                                                        initialRating = starPosition;
                                                        averageRating.setText((calculateAverageRating(0, true)));
                                                        productAverageRating.setText((calculateAverageRating(0, true)));


                                                        if (DBquaries.wishList.contains(itemId) && DBquaries.wishlistModelList.size() != 0) {
                                                            int index = DBquaries.wishList.indexOf(itemId);
                                                            DBquaries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                            /////////////////
                                                            DBquaries.wishlistModelList.get(index).setTotalRating(Long.parseLong(totalRatings.getText().toString()));
                                                        }


                                                    } else {
                                                        setRating(initialRating);
                                                        Toast.makeText(ProductDetailsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                    }
                                                    running_rating_query = false;
                                                }
                                            });

                                        } else {
                                            running_rating_query = false;
                                            setRating(initialRating);
                                            Toast.makeText(ProductDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        signInDialog.show();
                    }

                }
            });

        }

        //Rating layout

        buyNowBtn.setOnClickListener(v -> {

            loadingDialog.show();
            if (MainDashboard.registered) {
                DeliveryActivity.fromCart = false;
                loadingDialog.show();
//                    productDetailsActivity = ProductDetailsActivity.this;

                DeliveryActivity.cartItemModelList = new ArrayList<>();


                DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD")
                        ,CartItemModel.CART_ITEM
                        , itemId
                        , documentSnapshot.get("product_image_1").toString()
                        , documentSnapshot.get("product_title").toString()
                        , (long) documentSnapshot.get("free_coupons")
                        , documentSnapshot.get("product_price").toString()
                        , documentSnapshot.get("cutted_price").toString()
                        , 1
                        , (long)documentSnapshot.get("offers_applied")
                        , 1
                        , inStock
                        , (long) documentSnapshot.get("max_quantity")
                        , (long) documentSnapshot.get("stock_quantity")));

                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                if (DBquaries.addressModelsList.size() == 0) {
                    DBquaries.loadAddresses(getApplicationContext(), loadingDialog,true,false);
                } else {
                    loadingDialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                    startActivity(intent);
                }
            } else {
                signInDialog.show();
            }

        });


////coupon Dialog

////coupon Dialog
        couponRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainDashboard.registered) {
                    couponDialog.show();
                } else {
                    signInDialog.show();
                }
            }
        });


        ///////Sign in Dialog

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.cancel_btn_dialog);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.ok_btn_dialog);


        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.disableCloseBtn = true;
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                signInDialog.dismiss();
            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.disableCloseBtn = true;
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                signInDialog.dismiss();
            }
        });

        //////

    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
        MainDashboard.showCart = false;

        if (MainDashboard.registered) {

            if (DBquaries.myRating.size() == 0) {
                DBquaries.loadRating(ProductDetailsActivity.this);
            }
            if (DBquaries.wishList.size() == 0) {
                DBquaries.loadWishList(ProductDetailsActivity.this, loadingDialog, false);
            }if (DBquaries.rewardsModelList.size()==0){
                DBquaries.loadRewards(ProductDetailsActivity.this,loadingDialog,false);
            }
            if (DBquaries.cartList.size() != 0 && DBquaries.wishList.size() != 0 && DBquaries.rewardsModelList.size()!=0 ){
                loadingDialog.dismiss();
            }

        }else {
            loadingDialog.dismiss();
        }


        if (DBquaries.myRatedIds.contains(itemId)) {
            int index = DBquaries.myRatedIds.indexOf(itemId);
            initialRating = Integer.parseInt(String.valueOf(DBquaries.myRating.get(index))) - 1;
            setRating(initialRating);
        }

        //////////////////////////////////////
        if (DBquaries.cartList.contains(itemId)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }
        if (DBquaries.wishList.contains(itemId)) {
            ALREADY_ADDED_TO_WISH_LIST = true;
            addToWishListBtn.setSupportImageTintList(getResources().getColorStateList(R.color.errorRed));
        } else {
            ALREADY_ADDED_TO_WISH_LIST = false;
            addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
        }
        invalidateOptionsMenu();
    }

    private void showDialogRecyclerView() {
        if (couponRecyclerView.getVisibility() == View.GONE) {
            couponRecyclerView.setVisibility(View.VISIBLE);
            selectedCoupon.setVisibility(View.GONE);
        } else {
            couponRecyclerView.setVisibility(View.GONE);
            selectedCoupon.setVisibility(View.VISIBLE);
        }

    }

    public static void setRating(int starPosition) {


        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#858478")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }
        }
    }

    private String calculateAverageRating(long currentUserRating, boolean update) {

        Double totalStars = Double.valueOf(0);

        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingNoContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRating;
        long count = 0;
        long item1 = (long) documentSnapshot.get("1_star");
        long item2 = (long) documentSnapshot.get("2_star");
        long item3 = (long) documentSnapshot.get("3_star");
        long item4 = (long) documentSnapshot.get("4_star");
        long item5 = (long) documentSnapshot.get("5_star");

        count = (item1 + item2 + item3 + item4 + item5);
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(String.valueOf(count))).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(String.valueOf(count)) + 1)).substring(0, 3);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.details_cart_icon);


        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.cart_black_icon);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (MainDashboard.registered) {
            if (DBquaries.cartList.size() == 0) {
                DBquaries.loadCart(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
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
                if (MainDashboard.registered) {
                    Intent intent = new Intent(getApplicationContext(), MainDashboard.class);
                    showCart = true;
                    startActivity(intent);

                } else {
                    signInDialog.show();
                }

            }
        });

        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.details_search_icon) {
            if (fromSearch){
                finish();
            }else {

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
            return true;
        } else if (item.getItemId() == R.id.details_cart_icon) {
            if (MainDashboard.registered) {
                Intent intent = new Intent(getApplicationContext(), MainDashboard.class);
                showCart = true;
                startActivity(intent);
                return true;
            } else {
                signInDialog.show();
                return false;
            }
        } else if (item.getItemId() == android.R.id.home) {
//            productDetailsActivity = null;
            MainDashboard.showCart = false;
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        productDetailsActivity = null;
    }
}