package com.example.carapp.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.OrderModel;
import com.example.carapp.MyOrdersFragment;
import com.example.carapp.OrderDetailsActivity;
import com.example.carapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    List<OrderModel> orderData;
    private Dialog loadingDialog;

    public OrderAdapter(List<OrderModel> orderData,Dialog loadingDialog) {
        this.orderData = orderData;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_item_layout, parent, false);
        OrderViewHolder orderViewHolder = new OrderViewHolder(view);
        return orderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        String resource = orderData.get(position).getProductImage();
        String productId = orderData.get(position).getProductId();
        int rating = orderData.get(position).getRating();
        String pTitle = orderData.get(position).getProductTitle();
        Date date;
        String orderStatus = orderData.get(position).getOrderStatus();
        switch (orderStatus) {
            case "Ordered":
                date = orderData.get(position).getOrderedDate();
                break;

            case "Packed":
                date = orderData.get(position).getPackedDate();
                break;

            case "Shipped":
                date = orderData.get(position).getShippedDate();
                break;

            case "Delivered":
                date = orderData.get(position).getDeliveredDate();
                break;

            case "Cancelled":
                date = orderData.get(position).getCancelledDate();
                break;
            default:
                date = orderData.get(position).getCancelledDate();
        }


        holder.setOrder(resource, pTitle, orderStatus, date, rating, productId, position);
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView image, orderIndicator;
        TextView productTitle, description;
        private LinearLayout rateNowContainer;

        public OrderViewHolder(@NonNull final View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.order_product_image);
            orderIndicator = itemView.findViewById(R.id.order_indicator);
            productTitle = itemView.findViewById(R.id.wishlist_product_title);
            description = itemView.findViewById(R.id.order_deliverd_date);
            rateNowContainer = itemView.findViewById(R.id.rate_now_container);


        }

        public void setOrder(String oImage, String title, String orderStatus, Date date, int rating, String productID, int position) {

            Glide.with(itemView.getContext()).load(oImage).into(image);

            productTitle.setText(title);

            if (orderStatus.equals("Cancelled")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.errorRed)));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    orderIndicator.setImageTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.success)));
                }
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd-MMM-YYYY-hh:mm aa");

            description.setText(orderStatus + " - " + String.valueOf(simpleDateFormat.format(date)));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), OrderDetailsActivity.class);
                    intent.putExtra("Position", position);
                    itemView.getContext().startActivity(intent);
                }
            });

            //rating bar
            setRating(rating);
            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                final int starPosition = x;
                rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyOrdersFragment.loadingDialog.show();
                        setRating(starPosition);
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(productID);

                        FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                            @Nullable
                            @Override
                            public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                if (rating != 0) {
                                    Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                    Long decrease = documentSnapshot.getLong(rating + 1 + "_star") - 1;
                                    transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                    transaction.update(documentReference, rating + 1 + "_star", decrease);
                                } else {
                                    Long increase = documentSnapshot.getLong(starPosition + 1 + "_star") + 1;
                                    transaction.update(documentReference, starPosition + 1 + "_star", increase);
                                }

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Object>() {
                            @Override
                            public void onSuccess(Object o) {
                                Map<String, Object> myRating = new HashMap<>();

                                if (DBquaries.myRatedIds.contains(productID)) {

                                    myRating.put("rating_" + DBquaries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                } else {
                                    myRating.put("list_size", (long) DBquaries.myRatedIds.size() + 1);
                                    myRating.put("product_ID_" + DBquaries.myRatedIds.size(), productID);
                                    myRating.put("rating_" + DBquaries.myRatedIds.size(), (long) starPosition + 1);

                                }
                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                        .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            DBquaries.myOrderItemModelList.get(position).setRating(starPosition);
                                            if (DBquaries.myRatedIds.contains(productID)) {
                                                DBquaries.myRating.set(DBquaries.myRatedIds.indexOf(productID), Long.parseLong(String.valueOf(starPosition + 1)));
                                            } else {
                                                DBquaries.myRatedIds.add(productID);
                                                DBquaries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                            }
                                        }else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                        }
                                        MyOrdersFragment.loadingDialog.dismiss();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                MyOrdersFragment.loadingDialog.dismiss();
                            }
                        });
                    }
                });

            }

        }

        private void setRating(int starPosition) {

            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#858478")));
                    if (x <= starPosition) {
                        starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                    }
                }

            }
        }
    }
}
