package com.example.carapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.chaos.view.PinView;
import com.example.carapp.HelperClass.ConfirmOrderModel;
import com.example.carapp.HelperClass.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VerificationOTP extends AppCompatActivity {

    private TextView phoneNo, verifyTest;
    String codeBySystem;
    private Button verifyBtn;
    private String Phone;
    private PinView pinFromUser;
    FirebaseFirestore mAuth;
    String userNo;
    String name, address, mobile, pincode, payment;
    String mUID;
    String code;
    String order_id;
    int OTP_number;
    Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_o_t_p);


        phoneNo = findViewById(R.id.phone_no_otp);
        pinFromUser = findViewById(R.id.pin_view_new);
        verifyBtn = findViewById(R.id.verify_button_new);
        verifyTest = findViewById(R.id.verifyTest);

        mAuth = FirebaseFirestore.getInstance();
        mUID = FirebaseAuth.getInstance().getUid();

        Phone = getIntent().getStringExtra("mobileNo");
        userNo = getIntent().getStringExtra("mobileNo");

        phoneNo.setText("Verification code has been sent to \n " + userNo);

        random = new Random();
        OTP_number = random.nextInt(999999 - 111111) + 111111;


        order_id = getIntent().getStringExtra("order_id");

//        String sms_API = "https://www.fast2sms.com/dev/bulk";
        code = pinFromUser.getText().toString();
//        pinFromUser.setText(String.valueOf(OTP_number));
        verifyTest.setText("For testing your otp will be : " + OTP_number);


        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pinFromUser.getText().toString().equals(String.valueOf(OTP_number))) {

                    Map<String, Object> updateStatus = new HashMap<>();
                    updateStatus.put("Order Status", "Ordered");

                    FirebaseFirestore.getInstance().collection("ORDERS").document(order_id).update(updateStatus)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Map<String, Object> userOrder = new HashMap<>();
                                        userOrder.put("order_id", order_id);
                                        userOrder.put("time", FieldValue.serverTimestamp());
                                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").document(order_id).set(userOrder)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(VerificationOTP.this, DeliveryActivity.class);
                                                            intent.putExtra("TRANSACTION_ID", order_id);
                                                            intent.putExtra("Success", true);
                                                            DeliveryActivity.codOrderConfirm = true;
                                                            startActivity(intent);
                                                            finish();

                                                        } else {
                                                            Toast.makeText(VerificationOTP.this, "Failed to update user order list", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(VerificationOTP.this, "Order Cancelled", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(VerificationOTP.this, "error code", Toast.LENGTH_SHORT).show();
                }

            }
        });


        /////////////////////


//        StringRequest stringRequest = new StringRequest(Request.Method.POST,sms_API , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                verifyBtn.setOnClickListener(v -> {
//
//                    if (pinFromUser.getText().toString().equals(String.valueOf(OTP_number))){
//
//                        Intent intent = new Intent(VerificationOTP.this, DeliveryActivity.class);
//                        intent.putExtra("TRANSACTION_ID", "adafiena-afafjafa-affafa-adfadfaef");
//                        intent.putExtra("Success", true);
//                        DeliveryActivity.codOrderConfirm = true;
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        Toast.makeText(VerificationOTP.this, "Wrong Code", Toast.LENGTH_SHORT).show();
//                    }
//
//
//
//
////                    if (!code.equals(String.valueOf(OTP_number))) {
////
////                        Intent intent = new Intent(VerificationOTP.this, DeliveryActivity.class);
////                        intent.putExtra("TRANSACTION_ID", "adafiena-afafjafa-affafa-adfadfaef");
////                        intent.putExtra("PAID_AMOUNT", payment);
////                        intent.putExtra("Success", true);
////                        startActivity(intent);
////                        finish();
////                    } else {
////                        Toast.makeText(VerificationOTP.this, "Wrong Code", Toast.LENGTH_SHORT).show();
////                    }
//
//                });
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                finish();
//                Toast.makeText(VerificationOTP.this, "Failed to send OTP Verification Code", Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> headers = new HashMap<>();
//                headers.put("authorization","dfgs4o3n60OLAwPBMHkNQD7vR1l8JhKbxX2SItem9FjW5GcTzUO0TkV1tDFCn7lbjMfNwXaeLEZry8mQ");
//                return headers;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> body = new HashMap<>();
//                body.put("sender_id","FSTSMS");
//                body.put("language","english");
//                body.put("route","qt");
//                body.put("numbers","+917428730930");
//                body.put("message","34755");
//                body.put("variables","{#BB#}");
//                body.put("variables_values", String.valueOf(OTP_number));
//                return body;
//            }
//        };
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//
//        RequestQueue requestQueue = Volley.newRequestQueue(VerificationOTP.this);
//        requestQueue.add(stringRequest);


    }


}