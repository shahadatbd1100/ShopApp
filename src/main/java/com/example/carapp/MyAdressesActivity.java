package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.Adapter.AddressesAdapter;
import com.example.carapp.HelperClass.AddressModel;
import com.example.carapp.HelperClass.DBquaries;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.carapp.DeliveryActivity.SELECT_ADDRESS;

public class MyAdressesActivity extends AppCompatActivity {

    private int previousAddress;
    private Toolbar toolbar;
    private static AddressesAdapter addressesAdapter;
    private List<AddressModel> addressData;
    private RecyclerView addressRecyclerView;
    private Button deliverHereBtn;
    private LinearLayout addNewAddress;
    private TextView addressSaved;
    private Dialog loadingDialog;
    private int mode;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adresses);
        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add New Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressSaved.setText(String.valueOf(DBquaries.addressModelsList.size())+" Saved Addresses");

            }
        });


        previousAddress = DBquaries.selectedAddress;

        addressSaved = findViewById(R.id.address_saved);
        deliverHereBtn = findViewById(R.id.delivere_here_btn);
        addNewAddress = findViewById(R.id.add_new_address_btn);
        addressRecyclerView = findViewById(R.id.addresses_recycler_view);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mode = getIntent().getIntExtra("MODE", -1);

        if (mode == SELECT_ADDRESS) {
            deliverHereBtn.setVisibility(View.VISIBLE);
        } else {
            deliverHereBtn.setVisibility(View.GONE);
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DBquaries.selectedAddress != previousAddress) {
                    final int previousAddressIndex = previousAddress;

                    loadingDialog.show();

                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBquaries.selectedAddress+1),true);

                    previousAddress = DBquaries.selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA")
                            .document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                Toast.makeText(MyAdressesActivity.this, "Sorry Can not add address!", Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });

                }else{
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(DBquaries.addressModelsList, mode,loadingDialog);
        addressRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator) addressRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AddNewAddressActivity.class);
                if (mode!= SELECT_ADDRESS) {
                    intent.putExtra("INTENT", "manage");
                }else {
                    intent.putExtra("INTENT", "null");
                }
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        addressSaved.setText(String.valueOf(DBquaries.addressModelsList.size())+" Saved Addresses");

    }

    public static void refreshItem(int deselect, int select) {
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (mode == SELECT_ADDRESS) {
                if (DBquaries.selectedAddress != previousAddress) {
                    DBquaries.addressModelsList.get(DBquaries.selectedAddress).setSelected(false);
                    DBquaries.addressModelsList.get(previousAddress).setSelected(true);
                    DBquaries.selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mode== SELECT_ADDRESS) {
            if (DBquaries.selectedAddress != previousAddress) {
                DBquaries.addressModelsList.get(DBquaries.selectedAddress).setSelected(false);
                DBquaries.addressModelsList.get(previousAddress).setSelected(true);
                DBquaries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();

    }
}