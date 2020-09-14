package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import io.paperdb.Paper;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.carapp.HelperClass.CheckInternet;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {
    LinearLayout loginLinear;
    private CountryCodePicker countryCodePicker;
    private TextInputLayout phoneNumber, password;
    private Dialog loadingBar;
    private AppCompatCheckBox checkBox;

    public static boolean fromDelivery = false;

    FirebaseAuth mAuth;

    ScrollView loginScroll;

    String Name, Email, Password, Phone;
    ImageView disable_btn;
    boolean registered;
    Button signUpBtn;
    public static boolean disableCloseBtn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        countryCodePicker = findViewById(R.id.login_country_code_picker);
        phoneNumber = findViewById(R.id.login_phone);
        password = findViewById(R.id.login_password);

        signUpBtn = findViewById(R.id.signUpBtn);

        checkBox = findViewById(R.id.remember_me_chkb);
        disable_btn = findViewById(R.id.disable_btn);
        loginScroll = findViewById(R.id.loginScroll);
        Paper.init(this);

        Name = getIntent().getStringExtra("name");
        Email = getIntent().getStringExtra("email");
        Password = getIntent().getStringExtra("password");
        Phone = getIntent().getStringExtra("phone");
        registered = getIntent().getBooleanExtra("registered", false);

        loadingBar = new Dialog(this);

        loadingBar.setContentView(R.layout.loading_progress_dialog);
        loadingBar.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingBar.setCanceledOnTouchOutside(false);

        loginLinear = findViewById(R.id.login_linear);

        loginLinear.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_scale_anim));

        String UserPhoneKey = Paper.book().read(DBquaries.USERPHONEKEY);
        String UserPasswordKey = Paper.book().read(DBquaries.PASSWORDKEY);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

        if (fromDelivery) {
            loginScroll.setVisibility(View.GONE);
            fromDelivery = false;
        } else {
            loginScroll.setVisibility(View.VISIBLE);
        }

        if (UserPhoneKey != null && UserPasswordKey != null) {
            loadingBar.show();


            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {

                allowUserLogin(UserPhoneKey, UserPasswordKey);
            }

        }

        if (disableCloseBtn) {
            disable_btn.setVisibility(View.GONE);
        } else {
            disable_btn.setVisibility(View.VISIBLE);
        }


    }

    public void callSignUpScreen(View view) {


        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        Pair[] pairs = new Pair[3];

        pairs[0] = new Pair<View, String>(findViewById(R.id.logo_image), "logo_image");
        pairs[1] = new Pair<View, String>(findViewById(R.id.logo_name), "logo_text");
        pairs[2] = new Pair<View, String>(findViewById(R.id.title1), "bottomLogo");


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }

    }


    public void letUserLogin(View view) {

        loadingBar.show();


        if (!validatePhoneNumber() | !validatePassword()) {
            return;
        }

        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(this)) {
            Toast.makeText(this, "Connect to internet", Toast.LENGTH_SHORT).show();
            return;
        }
        ///Loading Bar


        String countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        String phoneNo = phoneNumber.getEditText().getText().toString().trim();

        if (phoneNo.charAt(0) == '0') {
            phoneNo = phoneNo.substring(1);
        }
        final String mPassword = password.getEditText().getText().toString().trim();
        final String mPhone = countryCode + phoneNo;

        allowUserLogin(mPhone, mPassword);


    }

    private void allowUserLogin(String mPhone, String mPassword) {

        if (checkBox.isChecked()) {
            //storing phone no into it
            Paper.book().write(DBquaries.USERPHONEKEY, mPhone);
            Paper.book().write(DBquaries.PASSWORDKEY, mPassword);
        }
        Query checkUser = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("phone").equalTo(mPhone);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String serverPassword = snapshot.child(mPhone).child("password").getValue(String.class);
                    String serverPhone = snapshot.child(mPhone).child("phone").getValue(String.class);
                    registered = true;
                    if (serverPassword.equals(mPassword)) {
                        Intent intent = new Intent(getApplicationContext(), MainDashboard.class);
                        intent.putExtra("name", Name);
                        intent.putExtra("email", Email);
                        intent.putExtra("password", Password);
                        intent.putExtra("phone", serverPhone);
                        intent.putExtra("registered", registered);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        loadingBar.dismiss();

                        //Do what you need to do with the id
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                .update("Last seen", FieldValue.serverTimestamp());

                    } else {
                        Toast.makeText(getApplicationContext(), "Password doesn't match!", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getApplicationContext(), "Server under maintenance!", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
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
                "$";

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

    //When cross button will be clicked
    public void directMainPage(View view) {

        Intent intent = new Intent(getApplicationContext(), MainDashboard.class);
        intent.putExtra("registered", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        disableCloseBtn = false;
        finish();


    }


}