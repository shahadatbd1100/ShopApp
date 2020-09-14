package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.carapp.HelperClass.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {

    PinView pinFromUser;
    String codeBySystem;
    FirebaseAuth mAuth;
    TextView phoneTextView, confirmText;
    ImageView success;


    String Name, Email, Password, Phone;
    boolean registered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_o_t_p);

        mAuth = FirebaseAuth.getInstance();

        pinFromUser = findViewById(R.id.pin_view_new);
        phoneTextView = findViewById(R.id.phone_text_view);
        confirmText = findViewById(R.id.confirmText);
        success = findViewById(R.id.errorImageView);


        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Password = getIntent().getStringExtra("password");
        Phone = getIntent().getStringExtra("phone");
        registered = true;


        phoneTextView.setText("A verification code will be sent to\n" + Phone);

        sendVerificationToUser(Phone);


    }

    private void sendVerificationToUser(String phone) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    codeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinFromUser.setText(code);
                        confirmText.setText(R.string.confirm);

                        int color = getResources().getColor(android.R.color.holo_green_dark);
                        confirmText.setTextColor(color);
                        success.setImageResource(R.drawable.success);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    Toast.makeText(VerifyOTP.this, "Exception :" + e, Toast.LENGTH_SHORT).show();
                }
            };

    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            storeDataInFirebase();
                            Intent intent = new Intent(VerifyOTP.this, SuccessActivity.class);
                            intent.putExtra("name", Name);
                            intent.putExtra("email", Email);
                            intent.putExtra("password", Password);
                            intent.putExtra("phone", Phone);
                            intent.putExtra("registered", registered);
                            startActivity(intent);
                            Toast.makeText(VerifyOTP.this, "Verification Successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(VerifyOTP.this, "Verification Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void storeDataInFirebase() {

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference userRef = rootNode.getReference().child("Users");
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        UserHelperClass newUser = new UserHelperClass(Name, Email, Phone, Password,"");


        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    CollectionReference userDataReference = firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA");


                    Map<String, Object> wishlistMap = new HashMap<>();
                    wishlistMap.put("list_size", (long) 0);

                    Map<String, Object> ratingsMap = new HashMap<>();
                    ratingsMap.put("list_size", (long) 0);

                    Map<String, Object> cartMap = new HashMap<>();
                    cartMap.put("list_size", (long) 0);

                    Map<String, Object> myAddressesMap = new HashMap<>();
                    myAddressesMap.put("list_size", (long) 0);

                    Map<String, Object> notificationsMap = new HashMap<>();
                    notificationsMap.put("list_size", (long) 0);


                    final List<String> documentNames = new ArrayList<>();
                    documentNames.add("MY_WISHLIST");
                    documentNames.add("MY_RATINGS");
                    documentNames.add("MY_CART");
                    documentNames.add("MY_ADDRESSES");
                    documentNames.add("MY_NOTIFICATIONS");

                    List<Map<String, Object>> documentFields = new ArrayList<>();
                    documentFields.add(wishlistMap);
                    documentFields.add(ratingsMap);
                    documentFields.add(cartMap);
                    documentFields.add(myAddressesMap);
                    documentFields.add(notificationsMap);

                    for (int x=0;x<documentNames.size();x++)
                    {
                        final int finalX = x;
                        userDataReference.document(documentNames.get(x)).set(documentFields.get(x))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            if (finalX ==documentNames.size() -1 )
                                            Toast.makeText(VerifyOTP.this, "Task Complete", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(VerifyOTP.this, "Error Creating List", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    Toast.makeText(VerifyOTP.this, "Task is Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
        userRef.child(Phone).setValue(newUser);
    }

    public void callNextScreenFromOTP(View view) {
        String code = pinFromUser.getText().toString();

        if (!code.isEmpty()) {
            verifyCode(code);
        }
    }
}