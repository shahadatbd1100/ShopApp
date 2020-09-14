package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.carapp.HelperClass.CheckInternet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    Animation left,right,up,down;

    TextInputLayout fullname,email,phoneNumber,password,confirmPassword;
    CountryCodePicker countryCodePicker;
    private Dialog loadingBar;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        linearLayout = findViewById(R.id.linear2);

        firebaseFirestore = FirebaseFirestore.getInstance();

        fullname = findViewById(R.id.signup_name);
        email = findViewById(R.id.signup_email);
        phoneNumber = findViewById(R.id.signup_phone);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        countryCodePicker = findViewById(R.id.signup_country_code);

        loadingBar = new Dialog(this);
        loadingBar.setContentView(R.layout.loading_progress_dialog);
        loadingBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingBar.setCancelable(false);

        up = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_scale_anim);
        linearLayout.setAnimation(up);

    }


    public void callOtpScreen(View view) {

        loadingBar.show();

        if (!validateFullname() |!validateEmail() | !validatePhoneNumber() | !validatePassword() | !confirmPassword())
        {
            loadingBar.dismiss();
            return;

        }

        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)){
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
            return;
        }
        else{


            String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
            String phoneNo = phoneNumber.getEditText().getText().toString().trim();

            String fName = fullname.getEditText().getText().toString().trim();
            String fEmail = email.getEditText().getText().toString().trim();
            String fPhone = countryCode+phoneNo;
            String fPassword = password.getEditText().getText().toString().trim();

            Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);

            intent.putExtra("name",fName);
            intent.putExtra("email",fEmail);
            intent.putExtra("password",fPassword);
            intent.putExtra("phone",fPhone);

            startActivity(intent);
            loadingBar.dismiss();

        }


    }


    private boolean validateFullname() {
        String fname = fullname.getEditText().getText().toString().trim();

        if (fname.isEmpty()) {
            fullname.setError("Field can not be empty");
            return false;
        } else {
            fullname.setError(null);
            fullname.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateEmail() {
        String fEmail = email.getEditText().getText().toString().trim();
        String checkemail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";



        if (fEmail.isEmpty()) {
            email.setError("Field can not be empty");
            loadingBar.dismiss();
            return false;
        }
        else if (!fEmail.matches(checkemail)) {
            email.setError("Invalid Email");
            return false;
        }
        else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String checkspaces = "Aw{1,20}z";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        } else if (val.matches(checkspaces)) {
            phoneNumber.setError("No White spaces are allowed!");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {

        String fPassword = password.getEditText().getText().toString().trim();
        String checkPassword = "^" +

                // "(?=.*[0-9])" +    //At least one digit
                // "(?=.*[a-z])" +    //At least one lowercase
                //  "(?=.*[A-Z])" +    //At least one UPPERCASE

                "(?=.*[a-zA-Z])" +    //Any letter
                "(?=.*[@#$%^&+=])" +    //At least one Special Character
                "(?=\\S+$)" +    //No Whitespace
                ".{4,}" +    //At least 4 Character
                "$" ;

        if (fPassword.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (fPassword.length() < 4) {
            password.setError("Password should contain 4 characters");
            return false;
        } else if (!fPassword.matches(checkPassword)) {
            password.setError("use letters and at least one special character @#$%^&+= ");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

    private boolean confirmPassword() {
        String fPassword = password.getEditText().getText().toString().trim();
        String cPassword = confirmPassword.getEditText().getText().toString().trim();

        if (!fPassword.equals(cPassword)) {
            confirmPassword.setError("Password doesn't matched");
            return false;
        }else {
            confirmPassword.setError(null);
            confirmPassword.setErrorEnabled(false);
            return true;
        }

    }

    private boolean checkUser() {
        String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        final String phoneNo = phoneNumber.getEditText().getText().toString().trim();
        final String fPhone = countryCode+phoneNo;
        final String fEmail = email.getEditText().getText().toString().trim();


        if (!fEmail.isEmpty() && !fPhone.isEmpty())
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(fPhone);


            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String serverEmail = snapshot.child("email").getValue(String.class);
                        if (fEmail.equals(serverEmail))
                        {
                            email.setError("User Already Exist");
                            email.requestFocus();
                            loadingBar.dismiss();

                        }
                        else {

                            loadingBar.dismiss();
                        }
                    }
                    else {
                        email.setError(null);
                        email.setErrorEnabled(false);
                        loadingBar.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return false;
        }
       else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }

    }
}