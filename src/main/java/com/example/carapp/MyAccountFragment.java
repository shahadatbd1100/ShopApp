package com.example.carapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.OrderModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MyAccountFragment extends Fragment {

    private CircleImageView profileImageView, currentOrderImage;
    private TextView fullName, email, tvCurrentOrderStatus;
    private LinearLayout layoutContainer;
    private ImageView orderIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar O_P_progress, P_S_progress, S_D_progress;
    private TextView yourRecentOrdersTitle;
    private LinearLayout recentLayoutContainer;
    private TextView addressName, address, pinCode;
    private Button signOutBtn;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton settingsBtn;
    private Button viewAll;
    public static final int MANAGE_ADDRESS = 1;
    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_account, container, false);

        viewAll = v.findViewById(R.id.view_all_addresses_btn);
        profileImageView = v.findViewById(R.id.profile_image);
        fullName = v.findViewById(R.id.user_name_last);
        email = v.findViewById(R.id.user_email_last);
        settingsBtn = v.findViewById(R.id.setting_button_last);
        layoutContainer = v.findViewById(R.id.layout_container);
        currentOrderImage = v.findViewById(R.id.current_order_image);
        tvCurrentOrderStatus = v.findViewById(R.id.current_order_status_tv);

        orderIndicator = v.findViewById(R.id.ordered_indicator_1);
        packedIndicator = v.findViewById(R.id.packed_indicator_2);
        shippedIndicator = v.findViewById(R.id.shipped_indicator_3);
        deliveredIndicator = v.findViewById(R.id.delivered_indicator_4);

        signOutBtn = v.findViewById(R.id.sign_out_btn);

        addressName = v.findViewById(R.id.address_fullname);
        address = v.findViewById(R.id.adress_full_address);
        pinCode = v.findViewById(R.id.address_pincode);

        O_P_progress = v.findViewById(R.id.ordered_packed_progress1);
        P_S_progress = v.findViewById(R.id.ordered_packed_progress2);
        S_D_progress = v.findViewById(R.id.ordered_packed_progress3);

        yourRecentOrdersTitle = v.findViewById(R.id.your_recent_orders_title);
        recentLayoutContainer = v.findViewById(R.id.recent_order_container);

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();




        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (OrderModel orderItemModel : DBquaries.myOrderItemModelList) {
                    if (!orderItemModel.isCancelRequest()) {
                        if (!orderItemModel.getOrderStatus().equals("Delivered") && !orderItemModel.getOrderStatus().equals("Cancelled")) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);

                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.android_option)).into(currentOrderImage);

                            tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());

                            switch (orderItemModel.getOrderStatus()) {
                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    O_P_progress.setProgress(30);
                                    break;
                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    O_P_progress.setProgress(100);
                                    break;
                                case "Shipped":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.success)));
                                    O_P_progress.setProgress(100);
                                    P_S_progress.setProgress(100);
                                    S_D_progress.setProgress(100);
                                    break;

                            }


                        }
                    }
                }
                int i = 0;
                for (OrderModel myOrderItemModel : DBquaries.myOrderItemModelList) {
                    if (i < 4) {
                        if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.android_option)).into((CircleImageView) recentLayoutContainer.getChildAt(i));
                            i++;
                        }
                    } else {
                        break;
                    }
                }
                if (i == 0) {
                    yourRecentOrdersTitle.setText("No recent orders");
                }
                if (i < 3) {
                    for (int x = i; x < 4; x++) {
                        recentLayoutContainer.getChildAt(x).setVisibility(View.INVISIBLE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(dialog1 -> {
                    loadingDialog.setOnDismissListener(null);
                    if (DBquaries.addressModelsList.size() == 0) {
                        addressName.setText("No Address");
                        address.setText("-");
                        pinCode.setText("-");
                    } else {
                        setAddress();
                    }
                });
                DBquaries.loadAddresses(getContext(), loadingDialog, false,true);
            }
        });
        if (DBquaries.myOrderItemModelList.size() == 0) {
            DBquaries.loadOrders(getContext(), null, loadingDialog);
            loadingDialog.dismiss();
        } else {
            loadingDialog.dismiss();
        }

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyAdressesActivity.class);
                intent.putExtra("MODE", MANAGE_ADDRESS);
                startActivity(intent);
            }
        });


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserPhoneKey = Paper.book().read(DBquaries.USERPHONEKEY);
                String UserPasswordKey = Paper.book().read(DBquaries.PASSWORDKEY);


                if (UserPhoneKey != "" && UserPasswordKey != "") {
                    if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                        Paper.book().destroy();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        DBquaries.clearData();
                        getActivity().finish();
                    } else {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    }
                }
            }
        });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),UpdateUserInfoActivity.class);
                intent.putExtra("Name",fullName.getText());
                intent.putExtra("Email",email.getText());
                intent.putExtra("Photo",DBquaries.profile);
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        fullName.setText(DBquaries.fullName);
        email.setText(DBquaries.email);

        if (DBquaries.profile != null) {
            Glide.with(getContext()).load(DBquaries.profile).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(profileImageView);
        }else {
            profileImageView.setImageResource(R.drawable.profile_photo);
        }
        if (!loadingDialog.isShowing()){
            if (DBquaries.addressModelsList.size() == 0) {
                addressName.setText("No Address");
                address.setText("-");
                pinCode.setText("-");
            } else {
                setAddress();
            }
        }
    }

    private void setAddress() {

        String nameText, mobileNo;
        nameText = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getName();
        mobileNo = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getMobileNo();
        if (DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo().equals("")) {
            addressName.setText(nameText + " - " + mobileNo);
        } else {
            addressName.setText(nameText + " - " + mobileNo + " or " + DBquaries.addressModelsList.get(DBquaries.selectedAddress).getAlternateMobileNo());
        }

        String flatNo = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getFlatNo();
        String locality = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLocality();
        String landmark = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getLandmark();
        String city = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getCity();
        String state = DBquaries.addressModelsList.get(DBquaries.selectedAddress).getState();

        if (landmark.equals("")) {
            address.setText(flatNo + ", " + locality + ", " + city + ", " + state);
        } else {
            address.setText(flatNo + ", " + locality + ", " + landmark + ", " + city + ", " + state);

        }
        pinCode.setText(DBquaries.addressModelsList.get(DBquaries.selectedAddress).getPinCode());


    }
}