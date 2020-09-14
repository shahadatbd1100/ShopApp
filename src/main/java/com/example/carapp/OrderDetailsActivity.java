package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.OrderModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position;
    private TextView title, price, quantity;
    private ImageView productImage, orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView orderedTitle, packedTitle, shippedTitle, deliveredTitle;
    private TextView orderedDate, packedDate, shippedDate, deliveredDate;
    private TextView orderedBody, packedBody, shippedBody, deliveredBody;
    private LinearLayout rateNowContainer;
    private int rating;
    private TextView fullName, address, pincode;
    private Dialog loadingDialog, cancelDialog;
    private TextView totalItemPrice, deliveryPrice, totalAmount, savedAmount, totalItems;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderBtn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("Position", -1);
        OrderModel model = DBquaries.myOrderItemModelList.get(position);


        ////loading dialog///
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        ///loading dialog///

        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
//        cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_circle_background_white));
        cancelDialog.setCancelable(true);

        rateNowContainer = findViewById(R.id.rate_now_container_last);
        fullName = findViewById(R.id.full_name_shipping);
        address = findViewById(R.id.address_shipping);
        pincode = findViewById(R.id.pincode_shipping);


        title = findViewById(R.id.order_details_product_title);
        price = findViewById(R.id.order_details_product_price);
        quantity = findViewById(R.id.order_details_product_quantity);
        productImage = findViewById(R.id.order_details_product_image);

        orderedIndicator = findViewById(R.id.orderd_indicator_sh);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipped_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);

        O_P_progress = findViewById(R.id.ordered_packed_progress);
        P_S_progress = findViewById(R.id.packed_shipping_progress);
        S_D_progress = findViewById(R.id.shipping_delivered_progress);

        orderedTitle = findViewById(R.id.ordered_title);
        packedTitle = findViewById(R.id.packed_title);
        shippedTitle = findViewById(R.id.shipped_title);
        deliveredTitle = findViewById(R.id.delivery_title);

        orderedDate = findViewById(R.id.ordered_date);
        packedDate = findViewById(R.id.packed_date);
        shippedDate = findViewById(R.id.shipped_date);
        deliveredDate = findViewById(R.id.delivery_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody = findViewById(R.id.packed_body);
        shippedBody = findViewById(R.id.shipped_body);
        deliveredBody = findViewById(R.id.delivery_body);

        totalItemPrice = findViewById(R.id.total_price);
        deliveryPrice = findViewById(R.id.delivery_charge_price);
        totalAmount = findViewById(R.id.final_total_price);
        savedAmount = findViewById(R.id.saved_amount);
        totalItems = findViewById(R.id.total_items);

        cancelOrderBtn = findViewById(R.id.cancel_btn_order);


        title.setText(model.getProductTitle());

        if (model.getDiscountedPrice().equals("")) {
            price.setText("Tk." + model.getProductPrice() + "/-");
        } else {
            price.setText("Tk." + model.getDiscountedPrice() + "/-");
        }

        quantity.setText("Quantity : " + String.valueOf(model.getProductQuantity()));

        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat = new SimpleDateFormat("EEE, dd-MMM-YYYY-hh:mm aa");

        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));
                O_P_progress.setProgress(30);

                /////////

//                P_S_progress.setVisibility(View.GONE);
//                S_D_progress.setVisibility(View.GONE);
//                O_P_progress.setVisibility(View.GONE);
//
//
//                packedIndicator.setVisibility(View.GONE);
//                packedBody.setVisibility(View.GONE);
//                packedDate.setVisibility(View.GONE);
//                packedTitle.setVisibility(View.GONE);
//
//
//                shippedIndicator.setVisibility(View.GONE);
//                shippedBody.setVisibility(View.GONE);
//                shippedDate.setVisibility(View.GONE);
//                shippedTitle.setVisibility(View.GONE);
//
//                deliveredIndicator.setVisibility(View.GONE);
//                deliveredBody.setVisibility(View.GONE);
//                deliveredDate.setVisibility(View.GONE);
//                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Packed":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                packedBody.setText("Your Product has been successfully packed");

                O_P_progress.setProgress(100);

//                P_S_progress.setVisibility(View.GONE);
//                S_D_progress.setVisibility(View.GONE);
//
//                shippedIndicator.setVisibility(View.GONE);
//                shippedBody.setVisibility(View.GONE);
//                shippedDate.setVisibility(View.GONE);
//                shippedTitle.setVisibility(View.GONE);
//
//                deliveredIndicator.setVisibility(View.GONE);
//                deliveredBody.setVisibility(View.GONE);
//                deliveredDate.setVisibility(View.GONE);
//                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Shipped":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                packedBody.setText("Your Product has been successfully packed");

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));
                shippedBody.setText("Your Product has been successfully shipped");


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);

//                S_D_progress.setVisibility(View.GONE);
//
//                deliveredIndicator.setVisibility(View.GONE);
//                deliveredBody.setVisibility(View.GONE);
//                deliveredDate.setVisibility(View.GONE);
//                deliveredTitle.setVisibility(View.GONE);


                break;

            case "Out for Delivery":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                packedBody.setText("Your Product has been successfully packed");

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));
                shippedBody.setText("Your Product has been shipped successfully");

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));
                deliveredBody.setText("Your Product has been delivered successfully");


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);

                deliveredTitle.setText("Out for delivery");
                deliveredBody.setText("Your order is out for delivery");

                break;

            case "Delivered":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));
                packedBody.setText("Your Product has been successfully packed");

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));
                shippedBody.setText("Your Product has been shipped successfully");

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));
                deliveredBody.setText("Your Product has been delivered successfully");


                O_P_progress.setProgress(100);
                P_S_progress.setProgress(100);
                S_D_progress.setProgress(100);


                break;

            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {
                    if (model.getShippedDate().after(model.getPackedDate())) {

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorRed)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled");


                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);
                        S_D_progress.setProgress(100);

                    } else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorRed)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled");

                        O_P_progress.setProgress(100);
                        P_S_progress.setProgress(100);

//                        S_D_progress.setVisibility(View.GONE);

//                        deliveredIndicator.setVisibility(View.GONE);
//                        deliveredBody.setVisibility(View.GONE);
//                        deliveredDate.setVisibility(View.GONE);
//                        deliveredTitle.setVisibility(View.GONE);
                    }
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorRed)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled");

                    O_P_progress.setProgress(100);

//                    P_S_progress.setVisibility(View.GONE);
//                    S_D_progress.setVisibility(View.GONE);
//
//                    shippedIndicator.setVisibility(View.GONE);
//                    shippedBody.setVisibility(View.GONE);
//                    shippedDate.setVisibility(View.GONE);
//                    shippedTitle.setVisibility(View.GONE);
//
//                    deliveredIndicator.setVisibility(View.GONE);
//                    deliveredBody.setVisibility(View.GONE);
//                    deliveredDate.setVisibility(View.GONE);
//                    deliveredTitle.setVisibility(View.GONE);
                }


                break;
        }


        //rating bar


        rating = model.getRating();

        setRating(rating);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());

                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                            if (rating != 0) {
                                Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                Long decrease = documentSnapshot.getLong(rating + 1 + "_star") - 1;
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                transaction.update(documentReference, rating + 1 + "_star", decrease);
                            } else {
                                Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                transaction.update(documentReference, starPosition + 1 + "_star", increase);
                            }

                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Map<String, Object> myRating = new HashMap<>();

                            if (DBquaries.myRatedIds.contains(model.getProductId())) {

                                myRating.put("rating_" + DBquaries.myRatedIds.indexOf(model.getProductId()), (long) starPosition + 1);

                            } else {
                                myRating.put("list_size", (long) DBquaries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBquaries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DBquaries.myRatedIds.size(), (long) starPosition + 1);

                            }
                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        DBquaries.myOrderItemModelList.get(position).setRating(starPosition);
                                        if (DBquaries.myRatedIds.contains(model.getProductId())) {
                                            DBquaries.myRating.set(DBquaries.myRatedIds.indexOf(model.getProductId()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DBquaries.myRatedIds.add(model.getProductId());
                                            DBquaries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }
            });

        }
        ////////rating layout////////

        if (model.isCancelRequest()) {
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText("Cancellation in process");
            cancelOrderBtn.setTextColor(getResources().getColor(R.color.errorRed));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E5E4E2")));

        } else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")) {
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setEnabled(true);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.findViewById(R.id.not_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });

                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();

                                Map<String, Object> map = new HashMap<>();
                                map.put("Order Id", model.getOrderID());
                                map.put("Product Id", model.getProductId());
                                map.put("Order Cancelled", false);

                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID()).collection("OrderItems").document(model.getProductId()).update("Cancellation requested", true)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        model.setCancelRequest(true);
                                                                        cancelOrderBtn.setEnabled(false);
                                                                        cancelOrderBtn.setText("Cancellation in process");
                                                                        cancelOrderBtn.setTextColor(getResources().getColor(R.color.errorRed));
                                                                        cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    loadingDialog.dismiss();
                                                                }
                                                            });
                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        cancelDialog.show();
                    }
                });


            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());


        totalItems.setText("Price (" + model.getProductQuantity() + " items)");

        Long totalItemsPriceValue;

        if (model.getDiscountedPrice().equals("")) {

            totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getProductPrice());

            totalItemPrice.setText("Tk." + totalItemsPriceValue + "/-");

        } else {
            totalItemsPriceValue = model.getProductQuantity() * Long.valueOf(model.getDiscountedPrice());
            totalItemPrice.setText("Tk." + totalItemsPriceValue + "/-");

        }
        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemPrice.getText());
        } else {
            deliveryPrice.setText("Tk." + model.getDeliveryPrice() + "/-");
            totalAmount.setText("Tk." + (totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice())) + "/-");
        }

        if (!model.getCuttedPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Tk." + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order");
            } else {
                savedAmount.setText("You saved Tk." + model.getProductQuantity() * (Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice())) + "/- on this order");
            }
        } else {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You saved Tk." + model.getProductQuantity() * (Long.valueOf(model.getProductPrice()) - Long.valueOf(model.getDiscountedPrice())) + "/- on this order");
            } else {
                savedAmount.setText("You saved Tk." + 0 + "/- on this order");
            }
        }


    }

    private void setRating(int starPosition) {

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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}