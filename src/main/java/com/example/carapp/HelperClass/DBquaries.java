package com.example.carapp.HelperClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.Adapter.CartAdapter;
import com.example.carapp.Adapter.CategoryAdapter;
import com.example.carapp.Adapter.HomePageAdapter;
import com.example.carapp.Adapter.OrderAdapter;
import com.example.carapp.AddNewAddressActivity;
import com.example.carapp.DeliveryActivity;
import com.example.carapp.MyCartFragment;
import com.example.carapp.MyOrdersFragment;
import com.example.carapp.MyRewardsFragment;
import com.example.carapp.NotificationActivity;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;
import com.example.carapp.User.HomeFragment;
import com.example.carapp.User.MainDashboard;
import com.example.carapp.WishlistFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.carapp.User.MainDashboard.registered;
import static com.example.carapp.User.MainDashboard.shimmer1;
import static com.example.carapp.User.MainDashboard.shimmer2;
import static com.example.carapp.User.MainDashboard.shimmer3;
import static com.example.carapp.User.MainDashboard.shimmer4;
import static com.example.carapp.User.MainDashboard.shimmer5;

public class DBquaries {
    public static final String USERPHONEKEY = "phone";
    public static final String PASSWORDKEY = "password";

    public static String email, fullName, profile;

    public static boolean addressSelected = false;

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static FirebaseUser currentUser = firebaseAuth.getCurrentUser();

    public static List<CategoryModel> categoryModelList = new ArrayList<>();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static List<AddressModel> addressModelsList = new ArrayList<>();
    public static int selectedAddress = -1;

    public static List<RewardsModel> rewardsModelList = new ArrayList<>();

    public static List<OrderModel> myOrderItemModelList = new ArrayList<>();


    public static List<NotificationModel> notificationModelList = new ArrayList<>();
    private static ListenerRegistration registration;


    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context) {

        categoryModelList.clear();

        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadFragmentData(final HomePageAdapter homePageAdapter, final Context context, final int index, String categoryName) {

        shimmer1.setVisibility(View.VISIBLE);
        shimmer2.setVisibility(View.VISIBLE);
        shimmer3.setVisibility(View.VISIBLE);
        shimmer4.setVisibility(View.VISIBLE);
        shimmer5.setVisibility(View.VISIBLE);
        shimmer1.startShimmer();
        shimmer2.startShimmer();
        shimmer3.startShimmer();
        shimmer4.startShimmer();
        shimmer5.startShimmer();
        firebaseFirestore.collection("CATEGORIES").document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long numberOfBanners = (long) documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x < numberOfBanners + 1; x++) {
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0, sliderModelList));

                                } else if ((long) documentSnapshot.get("view_type") == 1) {
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString()));
                                } else if ((long) documentSnapshot.get("view_type") == 2) {
                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProduct> horizontalProductList = new ArrayList<>();

                                    long numberOfProducts = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < numberOfProducts + 1; x++) {
                                        horizontalProductList.add(new HorizontalProduct(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_full_title_" + x).toString()
                                                , (long) documentSnapshot.get("free_coupons_" + x)
                                                , documentSnapshot.get("average_rating_" + x).toString()
                                                , (long) documentSnapshot.get("total_ratings_" + x)
                                                , documentSnapshot.get("product_price_" + x).toString()
                                                , documentSnapshot.get("cutted_price_" + x).toString()
                                                , (boolean) documentSnapshot.get("COD_" + x)
                                                , (boolean) documentSnapshot.get("in_stock_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductList, viewAllProductList));


                                } else if ((long) documentSnapshot.get("view_type") == 3) {
                                    List<HorizontalProduct> gridProductList = new ArrayList<>();

                                    long numberOfProducts = (long) documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < numberOfProducts + 1; x++) {
                                        gridProductList.add(new HorizontalProduct(documentSnapshot.get("product_ID_" + x).toString()
                                                , documentSnapshot.get("product_image_" + x).toString()
                                                , documentSnapshot.get("product_title_" + x).toString()
                                                , documentSnapshot.get("product_subtitle_" + x).toString()
                                                , documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(3, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridProductList));


                                }

                            }


                            shimmer1.stopShimmer();
                            shimmer2.stopShimmer();
                            shimmer3.stopShimmer();
                            shimmer4.stopShimmer();
                            shimmer5.stopShimmer();
                            shimmer1.setVisibility(View.GONE);
                            shimmer2.setVisibility(View.GONE);
                            shimmer3.setVisibility(View.GONE);
                            shimmer4.setVisibility(View.GONE);
                            shimmer5.setVisibility(View.GONE);
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                            homePageAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            shimmer1.stopShimmer();
                            shimmer2.stopShimmer();
                            shimmer3.stopShimmer();
                            shimmer4.stopShimmer();
                            shimmer5.stopShimmer();
                            shimmer1.setVisibility(View.GONE);
                            shimmer2.setVisibility(View.GONE);
                            shimmer3.setVisibility(View.GONE);
                            shimmer4.setVisibility(View.GONE);
                            shimmer5.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProduct) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBquaries.wishList.contains(ProductDetailsActivity.itemId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = true;
                            if (ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.errorRed));
                            }
                        } else {
                            if (ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                        }

                        if (loadProduct) {
                            wishlistModelList.clear();

                            final String productId = task.getResult().get("product_ID_" + x).toString();

                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).
                                                collection("QUANTITY")
                                                .orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        if (task1.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {

                                                            wishlistModelList.add(new WishlistModel(productId
                                                                    , documentSnapshot.get("product_image_1").toString()
                                                                    , documentSnapshot.get("product_title").toString()
                                                                    , (long) documentSnapshot.get("free_coupons")
                                                                    , documentSnapshot.get("average_rating").toString()
                                                                    , (long) documentSnapshot.get("total_ratings")
                                                                    , documentSnapshot.get("product_price").toString()
                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                    , (boolean) documentSnapshot.get("COD")
                                                                    , true));

                                                        } else {
                                                            wishlistModelList.add(new WishlistModel(productId
                                                                    , documentSnapshot.get("product_image_1").toString()
                                                                    , documentSnapshot.get("product_title").toString()
                                                                    , (long) documentSnapshot.get("free_coupons")
                                                                    , documentSnapshot.get("average_rating").toString()
                                                                    , (long) documentSnapshot.get("total_ratings")
                                                                    , documentSnapshot.get("product_price").toString()
                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                    , (boolean) documentSnapshot.get("COD")
                                                                    , false));
                                                        }
                                                        WishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();

                                                    }
                                                });


                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishList(final int index, final Context context) {

        final String removedProductID = wishList.get(index);

        wishList.remove(index);
        Map<String, Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++) {
            updateWishlist.put("product_ID_" + x, wishList.get(x));
        }
        updateWishlist.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (wishlistModelList.size() != 0) {
                        wishlistModelList.remove(index);
                        WishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                    Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    if (ProductDetailsActivity.addToWishListBtn != null) {
                        ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.errorRed));
                    }
                    wishList.add(index, removedProductID);
                    String err = task.getException().getMessage();
                    Toast.makeText(context, "Error : " + err, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });

    }

    public static void loadRating(final Context context) {
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();


            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        List<String> orderProductIds = new ArrayList<>();
                        for (int x = 0; x < myOrderItemModelList.size(); x++) {
                            orderProductIds.add(myOrderItemModelList.get(x).getProductId());
                        }


                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.itemId)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                }
                            }

                            if (orderProductIds.contains(task.getResult().get("product_ID_" + x).toString())) {
                                myOrderItemModelList.get(orderProductIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRating(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }
                        }
                        if (MyOrdersFragment.orderAdapter != null) {
                            MyOrdersFragment.orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCart(final Context context, final Dialog dialog, final boolean loadProduct, final TextView badgeCount, final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBquaries.cartList.contains(ProductDetailsActivity.itemId)) {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }

                        if (loadProduct) {
                            cartItemModelList.clear();

                            final String productId = task.getResult().get("product_ID_" + x).toString();

                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {


                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).
                                                collection("QUANTITY")
                                                .orderBy("time", Query.Direction.ASCENDING).get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {

                                                        int index = 0;
                                                        if (cartList.size() >= 2) {
                                                            index = cartList.size() - 2;
                                                        }


                                                        if (task1.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {

                                                            cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                    , CartItemModel.CART_ITEM
                                                                    , productId
                                                                    , documentSnapshot.get("product_image_1").toString()
                                                                    , documentSnapshot.get("product_title").toString()
                                                                    , (long) documentSnapshot.get("free_coupons")
                                                                    , documentSnapshot.get("product_price").toString()
                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                    , 1
                                                                    , (long) documentSnapshot.get("offers_applied")
                                                                    , 0
                                                                    , true
                                                                    , (long) documentSnapshot.get("max_quantity")
                                                                    , (long) documentSnapshot.get("stock_quantity")));

                                                        } else {
                                                            cartItemModelList.add(index, new CartItemModel(documentSnapshot.getBoolean("COD")
                                                                    , CartItemModel.CART_ITEM
                                                                    , productId
                                                                    , documentSnapshot.get("product_image_1").toString()
                                                                    , documentSnapshot.get("product_title").toString()
                                                                    , (long) documentSnapshot.get("free_coupons")
                                                                    , documentSnapshot.get("product_price").toString()
                                                                    , documentSnapshot.get("cutted_price").toString()
                                                                    , 1
                                                                    , (long) documentSnapshot.get("offers_applied")
                                                                    , 0
                                                                    , false
                                                                    , (long) documentSnapshot.get("max_quantity")
                                                                    , (long) documentSnapshot.get("stock_quantity")));
                                                        }
                                                        if (cartList.size() == 1) {
                                                            cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                            parent.setVisibility(View.VISIBLE);
                                                        }
                                                        if (cartList.size() == 0) {
                                                            cartItemModelList.clear();
                                                        }

                                                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                    if (cartList.size() != 0) {
                        badgeCount.setVisibility(View.VISIBLE);
                    } else {
                        badgeCount.setVisibility(View.GONE);
                    }
                    if (DBquaries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBquaries.cartList.size()));
                    } else {
                        badgeCount.setText("99");
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {

        final String removedProductID = cartList.get(index);
        cartList.remove(index);

        Map<String, Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged();
                    }

                    if (cartList.size() == 0) {
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }

                    Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    cartList.add(index, removedProductID);
                    String err = task.getException().getMessage();
                    Toast.makeText(context, "Error : " + err, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });

    }

    public static void loadAddresses(final Context context, final Dialog dialog, boolean gotoDeliveryActivity,boolean fromMyAccount) {

        addressModelsList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    Intent deliveryIntent = null;

                    if ((long) task.getResult().get("list_size") == 0 ) {
                        if (!fromMyAccount) {
                            deliveryIntent = new Intent(context, AddNewAddressActivity.class);
                            deliveryIntent.putExtra("INTENT", "deliveryIntent");
                            deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(deliveryIntent);
                        }
                    } else {
                        for (long x = 1; x < (long) task.getResult().get("list_size") + 1; x++) {
                            addressModelsList.add(new AddressModel(
                                      task.getResult().getBoolean("selected_" + x)
                                    , task.getResult().getString("city_" + x)
                                    , task.getResult().getString("locality_" + x)
                                    , task.getResult().getString("flat_no_" + x)
                                    , task.getResult().getString("pincode_" + x)
                                    , task.getResult().getString("landmark_" + x)
                                    , task.getResult().getString("name_" + x)
                                    , task.getResult().getString("mobile_no_" + x)
                                    , task.getResult().getString("alternate_mobile_no_" + x)
                                    , task.getResult().getString("state_" + x)
                            ));

                            if ((boolean) task.getResult().get("selected_" + x)) {
                                selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }

                        }
                        if (gotoDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                            deliveryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                    }
                    if (gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }

                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void loadRewards(Context context, final Dialog dialog, boolean onRewardFragment) {
        rewardsModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final Date lastSeen = task.getResult().getDate("Last seen");

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                            if (documentSnapshot.get("type").toString().equals("Discount")) {

                                                rewardsModelList.add(new RewardsModel(documentSnapshot.getId()
                                                        , documentSnapshot.get("type").toString(),
                                                        documentSnapshot.get("lower_limit").toString(),
                                                        documentSnapshot.get("upper_limit").toString(),
                                                        documentSnapshot.get("percentage").toString(),
                                                        documentSnapshot.get("body").toString(),
                                                        documentSnapshot.getDate("validity"),
                                                        (boolean) documentSnapshot.get("already_used")
                                                ));

                                            } else if (documentSnapshot.get("type").toString().equals("Flat Tk.* OFF") && lastSeen.before(documentSnapshot.getDate("validity"))) {
                                                rewardsModelList.add(new RewardsModel(documentSnapshot.getId(),
                                                        documentSnapshot.get("type").toString(),
                                                        documentSnapshot.get("lower_limit").toString(),
                                                        documentSnapshot.get("upper_limit").toString(),
                                                        documentSnapshot.get("amount").toString(),
                                                        documentSnapshot.get("body").toString(),
                                                        documentSnapshot.getDate("validity"),
                                                        (boolean) documentSnapshot.get("already_used")
                                                ));
                                            }
                                        }

                                        if (onRewardFragment) {
                                            MyRewardsFragment.rewardsAdapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, "Error " + error, Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });


                        } else {
                            dialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, "Error " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    public static void loadOrders(Context context, @Nullable OrderAdapter orderAdapter, Dialog dialog) {
        myOrderItemModelList.clear();


        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                        OrderModel myOrderItemModel = new OrderModel(orderItems.getString("Product Id")
                                                                , orderItems.getString("Order Status")
                                                                , orderItems.getString("Address")
                                                                , orderItems.getString("Coupon Id")
                                                                , orderItems.getString("Cutted Price")
                                                                , orderItems.getDate("Ordered Date")
                                                                , orderItems.getDate("Packed Date")
                                                                , orderItems.getDate("Shipped Date")
                                                                , orderItems.getDate("Delivered Date")
                                                                , orderItems.getDate("Cancelled Date")
                                                                , orderItems.getString("Discounted_Price")
                                                                , orderItems.getLong("Free Coupons")
                                                                , orderItems.getString("FullName")
                                                                , orderItems.getString("ORDER ID")
                                                                , orderItems.getString("Payment Method")
                                                                , orderItems.getString("Pincode")
                                                                , orderItems.getString("Product Price")
                                                                , orderItems.getLong("Product Quantity")
                                                                , orderItems.getString("User Id")
                                                                , orderItems.getString("Product Image")
                                                                , orderItems.getString("Product Title")
                                                                , orderItems.getString("Delivery Price")
                                                                , orderItems.getBoolean("Cancellation requested"));

                                                        myOrderItemModelList.add(myOrderItemModel);
                                                    }

                                                    loadRating(context);

                                                    if (orderAdapter != null) {
                                                        orderAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                                else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                            }

                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
//                        dialog.dismiss();
                    }
                });

    }


    public static void checkNotifications(boolean remove,@Nullable TextView notifyCount){

        if (remove){

            registration.remove();

        }else {
            registration = firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                            if (documentSnapshot != null && documentSnapshot.exists()){
                                notificationModelList.clear();
                                int unread =  0;

                                for (long x = 0; x < (long) documentSnapshot.get("list_size"); x++) {

                                    notificationModelList.add(0,new NotificationModel(documentSnapshot.getString("Image_"+x)
                                            ,documentSnapshot.getString("Body_"+x)
                                            ,documentSnapshot.getBoolean("Readed_"+x)));

                                    if (!documentSnapshot.getBoolean("Readed_"+x)){
                                        unread++;

                                        if (notifyCount!= null){

                                            if (unread > 0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (cartList.size() < 99) {
                                                    notifyCount.setText(String.valueOf(unread));
                                                } else {
                                                    notifyCount.setText("99");
                                                }
                                            }else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }

                                }

                                if (NotificationActivity.adapter!=null){
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }

                            }

                        }
                    });

        }





    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressModelsList.clear();
        rewardsModelList.clear();
        myOrderItemModelList.clear();
        notificationModelList.clear();
    }


}
