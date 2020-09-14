package com.example.carapp.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.AddNewAddressActivity;
import com.example.carapp.HelperClass.AddressModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.carapp.DeliveryActivity.SELECT_ADDRESS;
import static com.example.carapp.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.carapp.MyAdressesActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.MyAddressViewHolder> {

    List<AddressModel> addressesModelList;
    private int MODE;
    private int PreSelectedPosition;
    private boolean refresh = false;
    private Dialog loadingDialog;

    public AddressesAdapter(List<AddressModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        PreSelectedPosition = DBquaries.selectedAddress;
        this.loadingDialog = loadingDialog;

    }

    @NonNull
    @Override
    public MyAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout, parent, false);
        MyAddressViewHolder myAddressViewHolder = new MyAddressViewHolder(v);
        return myAddressViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyAddressViewHolder holder, int position) {


        String city = addressesModelList.get(position).getCity();
        String locality = addressesModelList.get(position).getLocality();
        String flatNo = addressesModelList.get(position).getFlatNo();
        String pincode = addressesModelList.get(position).getPinCode();
        String landmark = addressesModelList.get(position).getLandmark();
        String name = addressesModelList.get(position).getName();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        String alternateMobileNo = addressesModelList.get(position).getAlternateMobileNo();
        String state = addressesModelList.get(position).getState();
        Boolean selected = addressesModelList.get(position).getSelected();

        holder.setAddress(name, city, pincode, selected, position, mobileNo, alternateMobileNo, flatNo, locality, state, landmark);


    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class MyAddressViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, pincode;
        ImageView icon;
        private LinearLayout optionContainer;

        public MyAddressViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_text_view);
            address = itemView.findViewById(R.id.address_text_view);
            pincode = itemView.findViewById(R.id.pincode_text_view);
            icon = itemView.findViewById(R.id.icon_view);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private boolean loved = false;
        private int likes = 0;

        public void setAddress(String myName, String city, String myPincode, final Boolean selected, final int position, String mobileNo, String alternativeMobileNo, String flatNo, String locality, String state, String landmark) {


            if (alternativeMobileNo.equals("")) {
                name.setText(myName + " - " + mobileNo);
            } else {
                name.setText(myName + " - " + mobileNo + " or " + alternativeMobileNo);
            }


            if (landmark.equals("")) {
                address.setText(flatNo + " " + locality + " " + city + " " + state);
            } else {
                address.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);

            }
            pincode.setText(myPincode);

            if (MODE == SELECT_ADDRESS) {
                icon.setImageResource(R.drawable.checked_icon);
                if (selected) {
                    PreSelectedPosition = position;
                    icon.setVisibility(View.VISIBLE);
                } else {
                    icon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PreSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(PreSelectedPosition).setSelected(false);
                            refreshItem(PreSelectedPosition, position);
                            PreSelectedPosition = position;
                            DBquaries.selectedAddress = position;
                        }
                    }
                });
            } else if (MODE == MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { ////edit address

                        Intent intent = new Intent(itemView.getContext(), AddNewAddressActivity.class);
                        intent.putExtra("INTENT", "update_address");
                        intent.putExtra("index", position);
                        itemView.getContext().startActivity(intent);
                        refresh = false;

                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {  ///////remove address

                        loadingDialog.show();

                        Map<String, Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for (int i = 0; i < DBquaries.addressModelsList.size(); i++) {
                            if (i != position) {
                                x++;
                                addresses.put("city_" + x, addressesModelList.get(i).getCity());
                                addresses.put("locality_" + x, addressesModelList.get(i).getLocality());
                                addresses.put("flat_no_" + x, addressesModelList.get(i).getFlatNo());
                                addresses.put("pincode_" + x, addressesModelList.get(i).getPinCode());
                                addresses.put("landmark_" + x, addressesModelList.get(i).getLandmark());
                                addresses.put("name_" + x, addressesModelList.get(i).getName());
                                addresses.put("mobile_no_" + x, addressesModelList.get(i).getMobileNo());
                                addresses.put("alternate_mobile_no_" + x, addressesModelList.get(i).getAlternateMobileNo());
                                addresses.put("state_" + x, addressesModelList.get(i).getState());

                                if (addressesModelList.get(position).getSelected()) {
                                    if (position - 1 >= 0) {
                                        if (x == position) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        }else{
                                            addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                        }
                                    } else {
                                        if (x == 1) {
                                            addresses.put("selected_" + x, true);
                                            selected = x;
                                        }else {
                                            addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                        }
                                    }
                                } else {
                                    addresses.put("selected_" + x, addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()){
                                        selected = x;
                                    }
                                }

                            }
                        }
                        addresses.put("list_size", x);

                        final int finalSelected = selected;

                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    DBquaries.addressModelsList.remove(position);
                                    if (finalSelected != -1) {
                                        DBquaries.selectedAddress = finalSelected - 1;
                                        DBquaries.addressModelsList.get(finalSelected - 1).setSelected(true);
                                    }else if (DBquaries.addressModelsList.size()==0){
                                        DBquaries.selectedAddress = -1;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }

                                loadingDialog.dismiss();
                            }
                        });
                        refresh = false;
                    }
                });
                icon.setImageResource(R.drawable.plus);
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(PreSelectedPosition, PreSelectedPosition);

                        } else {
                            refresh = true;
                        }
                        PreSelectedPosition = position;


                    }
                });


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(PreSelectedPosition, PreSelectedPosition);
                        PreSelectedPosition = -1;
                    }
                });
            }

        }
    }
}
