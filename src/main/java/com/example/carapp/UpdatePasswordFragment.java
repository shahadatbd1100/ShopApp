package com.example.carapp;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carapp.HelperClass.DBquaries;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordFragment extends Fragment {


    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    private Dialog loadingDialog;
    private String email;

    private EditText oldPassword, newPassword, confirmNewPassword;
    private Button updateBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_password, container, false);

        oldPassword = view.findViewById(R.id.old_password);
        newPassword = view.findViewById(R.id.new_password);
        confirmNewPassword = view.findViewById(R.id.confirm_new_password);
        updateBtn = view.findViewById(R.id.update_btn_password);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        email = getArguments().getString("Email");


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePassword() | !confirmPassword()) {
                    return;
                } else {

//                    loadingDialog.show();
                    String oldPass = oldPassword.getText().toString().trim();
                    String newPass = newPassword.getText().toString().trim();
                    String confirmPass = confirmNewPassword.getText().toString().trim();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    Toast.makeText(getContext(), "This Feature will available soon", Toast.LENGTH_SHORT).show();




//                    AuthCredential credential = EmailAuthProvider
//                            .getCredential(email, oldPass);
//
//
//                    user.reauthenticate(credential)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//
//                                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//
//                                                    oldPassword.setText("");
//                                                    newPassword.setText("");
//                                                    confirmNewPassword.setText("");
//                                                    Toast.makeText(getContext(), "Password Updated Successfully", Toast.LENGTH_SHORT).show();
//                                                }else {
//                                                    String error = task.getException().getMessage();
//                                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
//                                                }
//                                                loadingDialog.dismiss();
//                                            }
//                                        });
//
//                                    }else {
//                                        loadingDialog.dismiss();
//                                        String error = task.getException().getMessage();
//                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });


                }
            }
        });


        return view;
    }


    private boolean validatePassword() {

        String fPassword = newPassword.getText().toString().trim();
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
            newPassword.setError("Field can not be empty");
            return false;
        } else if (fPassword.length() < 4) {
            newPassword.setError("Password should contain 4 characters");
            return false;
        } else if (!fPassword.matches(checkPassword)) {
            newPassword.setError("use letters and at least one special character @#$%^&+= ");
            return false;
        } else {
            newPassword.setError(null);
            return true;
        }

    }

    private boolean confirmPassword() {
        String fPassword = newPassword.getText().toString().trim();
        String cPassword = confirmNewPassword.getText().toString().trim();
        String oPassword = oldPassword.getText().toString().trim();

        if (!fPassword.equals(cPassword)) {
            confirmNewPassword.setError("Password doesn't matched");
            return false;
        } else if (fPassword.equals(oPassword)) {
            newPassword.setError("New password can't be same as Old password");
            return false;
        } else {
            confirmNewPassword.setError(null);
            return true;
        }

    }
}