package com.example.carapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import io.paperdb.DbStoragePlainFile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carapp.Adapter.AddressesAdapter;
import com.example.carapp.HelperClass.AddressModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewAddressActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatSpinner stateSpinner;
    private EditText city, locality, flatNo, pinCode, landmark, name, mobileNo, alternateMobileNo;
    private String selectedState;
    private Dialog loadingDialog;
    private String[] stateList;
    private boolean updateAddress = false;
    private AddressModel addressModel;
    private int position;
    private Button saveBtn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address);
        toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add New Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stateList = getResources().getStringArray(R.array.countries_array);

        city = findViewById(R.id.city_edit_text);
        locality = findViewById(R.id.locality_edit_text);
        flatNo = findViewById(R.id.street_address_edit_text);
        pinCode = findViewById(R.id.post_code_edit_text);
        stateSpinner = findViewById(R.id.state_spinner);
        landmark = findViewById(R.id.landmark_edit_text);
        name = findViewById(R.id.name_edit_text);
        mobileNo = findViewById(R.id.mobile_edit_text);
        saveBtn = findViewById(R.id.save_button_id);
        alternateMobileNo = findViewById(R.id.alternative_phone_edit_text);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        loadingDialog.setCancelable(false);

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getIntent().getStringExtra("INTENT").equals("update_address")) {
            updateAddress = true;
            position = getIntent().getIntExtra("index", -1);
            addressModel = DBquaries.addressModelsList.get(position);

            city.setText(addressModel.getCity());
            locality.setText(addressModel.getLocality());
            flatNo.setText(addressModel.getFlatNo());
            landmark.setText(addressModel.getLandmark());
            name.setText(addressModel.getName());
            mobileNo.setText(addressModel.getMobileNo());
            alternateMobileNo.setText(addressModel.getAlternateMobileNo());
            pinCode.setText(addressModel.getPinCode());

            for (int i = 0; i < stateList.length; i++) {
                if (stateList[i].equals(addressModel.getState())) {
                    stateSpinner.setSelection(i);
                }
            }
            saveBtn.setText("Update");
        } else {
            position = DBquaries.addressModelsList.size();
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

    public void callDeliveryPage(View view) {

        if (!TextUtils.isEmpty(city.getText())) {
            if (!TextUtils.isEmpty(locality.getText())) {
                if (!TextUtils.isEmpty(flatNo.getText())) {
                    if (!TextUtils.isEmpty(mobileNo.getText()) && mobileNo.getText().length() == 11) {
                        if (!TextUtils.isEmpty(name.getText())) {
                            if (!TextUtils.isEmpty(pinCode.getText())) {
                                loadingDialog.show();


                                Map<String, Object> addAddress = new HashMap();

                                addAddress.put("city_" + String.valueOf(position + 1), city.getText().toString());
                                addAddress.put("locality_" + String.valueOf(position + 1), locality.getText().toString());
                                addAddress.put("flat_no_" + String.valueOf(position + 1), flatNo.getText().toString());
                                addAddress.put("pincode_" + String.valueOf(position + 1), pinCode.getText().toString());
                                addAddress.put("landmark_" + String.valueOf(position + 1), landmark.getText().toString());
                                addAddress.put("name_" + String.valueOf(position + 1), name.getText().toString());
                                addAddress.put("mobile_no_" + String.valueOf(position + 1), mobileNo.getText().toString());
                                addAddress.put("alternate_mobile_no_" + String.valueOf(position + 1), alternateMobileNo.getText().toString());
                                addAddress.put("state_" + String.valueOf(position + 1), selectedState);

                                if (!updateAddress) {
                                    addAddress.put("list_size", (long) DBquaries.addressModelsList.size() + 1);
                                    if (getIntent().getStringExtra("INTENT").equals("manage")){
                                        if (DBquaries.addressModelsList.size()==0){
                                            addAddress.put("selected_" + String.valueOf(position + 1), true);
                                        }else {
                                            addAddress.put("selected_" + String.valueOf(position + 1), false);
                                        }
                                    }else {
                                        addAddress.put("selected_" + String.valueOf(position + 1), true);
                                    }
                                    if (DBquaries.addressModelsList.size() > 0) {
                                        addAddress.put("selected_" + (DBquaries.selectedAddress + 1), false);
                                    }
                                }

                                FirebaseFirestore.getInstance().collection("USERS")
                                        .document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                        .document("MY_ADDRESSES")
                                        .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            if (!updateAddress) {
                                                if (DBquaries.addressModelsList.size() > 0) {
                                                    DBquaries.addressModelsList.get(DBquaries.selectedAddress).setSelected(false);
                                                }
                                                DBquaries.addressModelsList.add(new AddressModel(true, city.getText().toString(), locality.getText().toString(), flatNo.getText().toString(), pinCode.getText().toString(), landmark.getText().toString(), name.getText().toString(), mobileNo.getText().toString(), alternateMobileNo.getText().toString(), selectedState));

                                                if (getIntent().getStringExtra("INTENT").equals("manage")){
                                                    if (DBquaries.addressModelsList.size()==0){
                                                        DBquaries.selectedAddress = DBquaries.addressModelsList.size() - 1;
                                                    }
                                                }else {
                                                    DBquaries.selectedAddress = DBquaries.addressModelsList.size() - 1;
                                                }
                                            } else {
                                                DBquaries.addressModelsList.set(position, new AddressModel(true, city.getText().toString(), locality.getText().toString(), flatNo.getText().toString(), pinCode.getText().toString(), landmark.getText().toString(), name.getText().toString(), mobileNo.getText().toString(), alternateMobileNo.getText().toString(), selectedState));
                                            }

                                            if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                                                startActivity(intent);
                                            } else {
                                                MyAdressesActivity.refreshItem(DBquaries.selectedAddress, DBquaries.addressModelsList.size() - 1);
                                            }
                                            finish();
                                        } else {
                                            Toast.makeText(AddNewAddressActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }

                                        loadingDialog.dismiss();
                                    }
                                });
                            } else {
                                pinCode.setError("Please insert a valid Pin Code");
                                pinCode.requestFocus();
                            }
                        } else {

                            name.setError("Please insert a Name");
                            name.requestFocus();
                        }
                    } else {
                        mobileNo.setError("Please provide a 11 Digit Mobile No");
                        mobileNo.requestFocus();
                    }
                } else {
                    flatNo.setError("Please provide your flat No");
                    flatNo.requestFocus();
                }

            } else {
                locality.setError("Please provide your locality");
                locality.requestFocus();
            }
        } else {
            city.setError("Please insert a city");
            city.requestFocus();
        }
    }
}