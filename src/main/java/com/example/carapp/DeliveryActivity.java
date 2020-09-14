package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carapp.Adapter.CartAdapter;
import com.example.carapp.HelperClass.CartItemModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView cartItemsRecyclerView;
    public static List<CartItemModel> cartItemModelList;
    public static CartAdapter cartAdapter;
    private Button changeOrAddAddressBtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private Button continueBtn;

    private TextView fullName;
    private String name, mobileNo;
    private String paymentMethod = "BKASH";
    private TextView fullAddress;
    private TextView pincode;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private TextView codTitle;
    private View divider;
    private ConstraintLayout orderConfirmationLayout;
    private Button continueShopping;
    private TextView orderId;
    boolean successIntent, successCOD;
    public static boolean fromCart;
    public static boolean codOrderConfirm = false;
    String transactionID;
    private boolean successResponse = false;
    private FirebaseFirestore firebaseFirestore;
    private boolean getQtyIDs = true;
    String flatNo;
    String locality;
    String landmark;
    String city;
    String state;

    private ImageButton bKash;
    private ImageButton cashOnDelivery;
    String order_id;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        continueBtn = findViewById(R.id.deliveryContinueBtn);
        fullName = findViewById(R.id.full_name_shipping);
        fullAddress = findViewById(R.id.address_shipping);
        pincode = findViewById(R.id.pincode_shipping);

        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShopping = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        order_id = UUID.randomUUID().toString().substring(0, 20);

        totalAmount = findViewById(R.id.total_cart_amount);
        changeOrAddAddressBtn = findViewById(R.id.change_or_add_new_address);
        cartItemsRecyclerView = findViewById(R.id.delivery_recycler_view);
        cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        cartItemsRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        getQtyIDs = true;

        changeOrAddAddressBtn.setVisibility(View.VISIBLE);

        firebaseFirestore = FirebaseFirestore.getInstance();

        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);


        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getApplicationContext().getDrawable(R.drawable.slider_circle_background_white));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bKash = paymentMethodDialog.findViewById(R.id.bkash);
        cashOnDelivery = paymentMethodDialog.findViewById(R.id.cod_btn);
        codTitle = paymentMethodDialog.findViewById(R.id.cod_btn_title);
        divider = paymentMethodDialog.findViewById(R.id.divider_pm);

        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        changeOrAddAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getQtyIDs = false;

                Intent intent = new Intent(getApplicationContext(), MyAdressesActivity.class);
                intent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(intent);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean allProductAvailable = true;

                for (CartItemModel cartItemModel : cartItemModelList) {
                    if (cartItemModel.isQtyError()) {
                        allProductAvailable = false;
                        break;
                    }

                    if (cartItemModel.getType() == CartItemModel.CART_ITEM){
                        if (!cartItemModel.isCOD()){
                            cashOnDelivery.setEnabled(false);
                            cashOnDelivery.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);

                            break;
                        }else {
                            cashOnDelivery.setEnabled(true);
                            cashOnDelivery.setAlpha(1f);
                            codTitle.setAlpha(1f);
                        }
                    }



                }
                if (allProductAvailable) {
                    paymentMethodDialog.show();
                }
            }
        });


        name = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getName();
        mobileNo = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getMobileNo();
        if (DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        }else {
            fullName.setText(name + " - " + mobileNo+" or "+DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo());
        }

         flatNo   = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getFlatNo();
         locality = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLocality();
         landmark = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLandmark();
         city     = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getCity();
         state    = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getState();

        if (landmark.equals("")) {
            fullAddress.setText(flatNo + " " + locality + " " + city + " " + state);
        }else {
            fullAddress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);

        }
        pincode.setText(DBquaries.addressModelsList.get(DBquaries.selectedAddress).getPinCode());

        cashOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethod = "COD";
                placeOrderDetails();
            }
        });

        bKash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paymentMethod = "BKASH";
                placeOrderDetails();

            }
        });


        String payment = getIntent().getStringExtra("PAID_AMOUNT");
        transactionID = getIntent().getStringExtra("TRANSACTION_ID");
        successIntent = getIntent().getBooleanExtra("Success", false);
        successCOD = getIntent().getBooleanExtra("Success", false);

        if (successIntent) {
            Map<String, Object> updateStatus = new HashMap<>();
            updateStatus.put("Payment Status", "Paid");
            updateStatus.put("Order Status", "Ordered");

            firebaseFirestore.collection("ORDERS").document(transactionID).update(updateStatus)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Map<String,Object> userOrder = new HashMap<>();
                                userOrder.put("order_id",transactionID);
                                userOrder.put("time",FieldValue.serverTimestamp());

                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(transactionID).set(userOrder)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    showConfirmation(transactionID);
                                                }else {
                                                    Toast.makeText(DeliveryActivity.this, "Failed to update user order list", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            } else {
                                Toast.makeText(DeliveryActivity.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }

        if (successCOD) {

            showConfirmation(transactionID);

        }

    }

    private void showConfirmation(String transactionID) {

        successResponse = true;
        codOrderConfirm = false;
        getQtyIDs = false;

        MainDashboard.resetMainActivity = true;

        for (int x = 0; x < cartItemModelList.size() - 1; x++) {

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID)
                        .update("user_ID", FirebaseAuth.getInstance().getUid());


            }

        }


        String sms_API = "https://www.fast2sms.com/dev/bulk";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, sms_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "dfgs4o3n60OLAwPBMHkNQD7vR1l8JhKbxX2SItem9FjW5GcTzUO0TkV1tDFCn7lbjMfNwXaeLEZry8mQ");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> body = new HashMap<>();
                body.put("sender_id", "FSTSMS");
                body.put("language", "english");
                body.put("route", "qt");
                body.put("numbers", "+917428730930");
                body.put("message", "34755");
                body.put("variables", "{#BB#}");
                body.put("variables_values", order_id);
                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        RequestQueue requestQueue = Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);


        /////////////////////////


        orderId.setText("OrderID : " + order_id);
        continueBtn.setEnabled(false);
        changeOrAddAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromCart) {
                    loadingDialog.show();
                    Map<String, Object> updateCartList = new HashMap<>();
                    long cartListSize = 0;
                    final List<Integer> indexList = new ArrayList<>();

                    for (int x = 0; x < DBquaries.cartList.size(); x++) {
                        if (!cartItemModelList.get(x).isInStock()) {
                            updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductId());
                            cartListSize++;
                        } else {
                            indexList.add(x);
                        }
                    }
                    updateCartList.put("list_size", cartListSize);

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                            .document("MY_CART")
                            .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                for (int x = 0; x < indexList.size(); x++) {
                                    DBquaries.cartList.remove(indexList.get(x).intValue());
                                    DBquaries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBquaries.cartItemModelList.remove(DBquaries.cartItemModelList.size() - 1);
                                }
                            } else {
                                Toast.makeText(DeliveryActivity.this, "Error when Purchasing", Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });

                }
                Intent intent = new Intent(DeliveryActivity.this, LoginActivity.class);
                MainDashboard.showCart = false;
                LoginActivity.fromDelivery = true;
//              ProductDetailsActivity.productDetailsActivity = null;
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID)
                            .update("user_ID", FirebaseAuth.getInstance().getUid());


                }
            }

            /////accessing quantity
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++) {
                    String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                    Map<String, Object> timestamp = new HashMap<>();
                    timestamp.put("time", FieldValue.serverTimestamp());

                    int finalX = x;
                    int finalY = y;
                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY")
                            .document(quantityDocumentName).set(timestamp)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                        if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()) {

                                            firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductId()).collection("QUANTITY")
                                                    .orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX).getStockQuantity()).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                List<String> serverQuantity = new ArrayList<>();

                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                                }

                                                                long availableQty = 0;
                                                                boolean noLongerAvailable = true;
                                                                for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()) {

                                                                    cartItemModelList.get(finalX).setQtyError(false);
                                                                    if (!serverQuantity.contains(qtyId)) {

                                                                        if (noLongerAvailable) {
                                                                            cartItemModelList.get(finalX).setInStock(false);
                                                                        } else {

                                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                                            cartItemModelList.get(finalX).setMax_quantity(availableQty);
                                                                            Toast.makeText(DeliveryActivity.this, "Sorry! All Products may not be available in required quantity", Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    } else {
                                                                        availableQty++;
                                                                        noLongerAvailable = false;
                                                                    }

                                                                }
                                                                cartAdapter.notifyDataSetChanged();

                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });

                                        }
                                    } else {
                                        loadingDialog.dismiss();
                                        String error = task.getException().getMessage();
                                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });
                }

            }

        } else {
            getQtyIDs = true;
        }
        /////accessing quantity▐
        loadingDialog.dismiss();
        name = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getName();
        mobileNo = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getMobileNo();
        if (DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        }else {
            fullName.setText(name + " - " + mobileNo+" or "+DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo());
        }

        flatNo   = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getFlatNo();
        locality = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLocality();
        landmark = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLandmark();
        city     = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getCity();
        state    = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getState();

        if (landmark.equals("")) {
            fullAddress.setText(flatNo + " " + locality + " " + city + " " + state);
        }else {
            fullAddress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);

        }
        pincode.setText(DBquaries.addressModelsList.get(DBquaries.selectedAddress).getPinCode());

        if (codOrderConfirm) {
            showConfirmation(transactionID);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                if (!successIntent) {
                    for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                        int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductId()).collection("QUANTITY").document(qtyID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))) {

                                            cartItemModelList.get(finalX).getQtyIDs().clear();


                                        }
                                    }
                                });

                    }
                } else {
                    cartItemModelList.get(x).getQtyIDs().clear();
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

    @Override
    public void onBackPressed() {

        if (successIntent) {
            Intent intent = new Intent(DeliveryActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            MainDashboard.showCart = false;
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }


    private void placeOrderDetails() {
        loadingDialog.show();

        String userID = FirebaseAuth.getInstance().getUid();

        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductId());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User Id", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("Cutted Price", "");
                }
                orderDetails.put("Product Price", cartItemModel.getProductPrice());

                if (cartItemModel.getSelectedCouponId() != null) {
                    orderDetails.put("Coupon Id", cartItemModel.getSelectedCouponId());
                } else {
                    orderDetails.put("Coupon Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted_Price", cartItemModel.getDiscountedPrice());
                } else {
                    orderDetails.put("Discounted_Price", "");
                }
                orderDetails.put("Ordered Date", FieldValue.serverTimestamp());
                orderDetails.put("Packed Date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped Date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered Date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled Date", FieldValue.serverTimestamp());
                orderDetails.put("Order Status", "Ordered");
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("FullName", fullName.getText());
                orderDetails.put("Pincode", pincode.getText());
                orderDetails.put("Free Coupons", cartItemModel.getFreeCoupons());
                orderDetails.put("Delivery Price", cartItemModelList.get(cartItemModelList.size()-1).getDeliveryPrice());
                orderDetails.put("Cancellation requested", false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductId())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Map<String, Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemsPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "not Paid");
                orderDetails.put("Order Status", "Cancelled");

                firebaseFirestore.collection("ORDERS").document(order_id).set(orderDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (paymentMethod.equals("BKASH")) {
                                        bkash();
                                    } else {
                                        cod();
                                    }
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        }
    }

    private void bkash() {
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();
        Intent intent = new Intent(getApplicationContext(), BkashPaymentActivity.class);
        intent.putExtra("PAYMENT", totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2));
        intent.putExtra("mobileNo", "+88" + mobileNo.substring(0, 11));
        intent.putExtra("order_id", order_id);
        startActivity(intent);
    }

    private void cod() {
        getQtyIDs = false;
        loadingDialog.dismiss();
        paymentMethodDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), VerificationOTP.class);
        intent.putExtra("order_id", order_id);
        startActivity(intent);
    }

}