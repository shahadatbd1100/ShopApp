package com.example.carapp.Adapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.DeliveryActivity;
import com.example.carapp.HelperClass.CartItemModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.RewardsModel;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;
import com.example.carapp.User.MainDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter {
    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                CartItemViewHolder cartItemViewHolder = new CartItemViewHolder(view);
                return cartItemViewHolder;
            case CartItemModel.TOTAL_AMOUNT:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                CartTotalAMountViewHolder cartTotalAMountViewHolder = new CartTotalAMountViewHolder(view2);
                return cartTotalAMountViewHolder;
            default:
                return null;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductId();
                String resource = cartItemModelList.get(position).getProductImage();
                String Title = cartItemModelList.get(position).getProductTitle();
                long freeCoupons = cartItemModelList.get(position).getFreeCoupons();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                long offersApplied = cartItemModelList.get(position).getOffersApplied();
                long productQuantity = cartItemModelList.get(position).getProductQuantity();
                long maxQuantity = cartItemModelList.get(position).getMax_quantity();
                boolean inStock = cartItemModelList.get(position).isInStock();
                boolean qtyError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();

                ((CartItemViewHolder) holder).setItemDetails(productID, resource, Title, freeCoupons, productPrice, cuttedPrice, offersApplied, position, inStock, String.valueOf(productQuantity), maxQuantity, qtyError, qtyIds, stockQty,COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:

                int totalItem = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;

                for (int x = 0; x < cartItemModelList.size(); x++) {

                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {

                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItem = totalItem + quantity;

                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = (totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                        } else {
                            totalItemPrice = (totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                        }

                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;

                            }
                        } else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;

                            }
                        }
                    }
                }

                if (totalItemPrice > 500) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                } else {
                    deliveryPrice = "100";
                    totalAmount = totalItemPrice + 100;
                }

                cartItemModelList.get(position).setTotalItems(totalItem);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);

                ((CartTotalAMountViewHolder) holder).setTotalAmount(totalItem, totalItemPrice, deliveryPrice, totalAmount, savedAmount);
                break;
            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_scale_anim);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, freeCouponIcon;
        TextView productTitle;
        TextView freeCoupon;
        LinearLayout removeButton;
        TextView productPrice, cuuttedPrice, offerApplied, couponApplied, productQuantity;
        RelativeLayout couponRedeemLayout;
        private Button redeemBtn;
        ////couponDialog
        private TextView couponTitle, couponBody, couponExpiryDate;
        private RecyclerView couponRecyclerView;
        private LinearLayout selectedCoupon;
        private TextView originalPrice;
        private TextView couponRedemptionBody;
        private TextView discountedPrice;
        private Button removeCouponBtn;
        private Button applyCouponBtn;
        private LinearLayout applyORremoveBtnContainer;
        private TextView footerText;
        private String productOriginalPrice;
        private LinearLayout codIndicator;
        ////couponDialog


        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);


            removeButton = itemView.findViewById(R.id.remove_item_button);
            productImage = itemView.findViewById(R.id.wishlist_Image);
            freeCouponIcon = itemView.findViewById(R.id.wishlist_free_coupon_icon);
            productTitle = itemView.findViewById(R.id.wishlist_product_title);
            freeCoupon = itemView.findViewById(R.id.wishlist_coupon_text_view);
            productPrice = itemView.findViewById(R.id.wishlist_product_price);
            cuuttedPrice = itemView.findViewById(R.id.wishlist_cutted_price);
            offerApplied = itemView.findViewById(R.id.offers_aplied);
            couponApplied = itemView.findViewById(R.id.coupons_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            couponRedeemLayout = itemView.findViewById(R.id.coupon_redeem_layout);
            redeemBtn = itemView.findViewById(R.id.coupon_redeem_btn);
            couponRedemptionBody = itemView.findViewById(R.id.tv_coupon_redemption);
            codIndicator = itemView.findViewById(R.id.cash_on_deliver_layout);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

        private void setItemDetails(String productID, String resource, String title, long freeCouponNo, String pPrice, String cutPrice, long offerAppliedNo, final int position, boolean inStock, String quantity, long maxQuantity, boolean qtyError, List<String> qtyIds, long stockQty,final boolean COD) {


            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_photo)).into(productImage);
            productTitle.setText(title);
            if (freeCouponNo > 0) {
                freeCouponIcon.setVisibility(View.VISIBLE);
                freeCoupon.setVisibility(View.VISIBLE);
                if (freeCouponNo == 1) {
                    freeCoupon.setText("free " + freeCouponNo + " Coupon");
                } else {
                    freeCoupon.setText("free " + freeCouponNo + " Coupons");
                }
            } else {
                freeCouponIcon.setVisibility(View.INVISIBLE);
                freeCoupon.setVisibility(View.INVISIBLE);
            }


            final Dialog couponDialog = new Dialog(itemView.getContext());
            couponDialog.setContentView(R.layout.coupon_redeem_dialog);
            couponDialog.setCancelable(false);
            couponDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (COD){
                codIndicator.setVisibility(View.VISIBLE);
            }else {
                codIndicator.setVisibility(View.INVISIBLE);

            }


            if (inStock) {

                if (offerAppliedNo > 0) {
                    offerApplied.setVisibility(View.VISIBLE);
                    offerApplied.setText(offerAppliedNo + " Offers applied");
                } else {
                    offerApplied.setVisibility(View.INVISIBLE);
                }


                productPrice.setText("Tk. " + pPrice + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuuttedPrice.setText("Tk. " + cutPrice + "/-");
                couponRedeemLayout.setVisibility(View.VISIBLE);

                //////coupon Dialog////////

                couponRecyclerView = couponDialog.findViewById(R.id.coupons_recycler_view);
                selectedCoupon = couponDialog.findViewById(R.id.selected_coupon);

                couponTitle = couponDialog.findViewById(R.id.coupon_title);
                couponExpiryDate = couponDialog.findViewById(R.id.coupon_validity);
                couponBody = couponDialog.findViewById(R.id.coupon_body);
                removeCouponBtn = couponDialog.findViewById(R.id.remove_btn);
                applyCouponBtn = couponDialog.findViewById(R.id.apply_btn);
                couponBody = couponDialog.findViewById(R.id.coupon_body);
                footerText = couponDialog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer = couponDialog.findViewById(R.id.apply_or_remove_btns_container);
                ImageView toggleRecyclerVIew = couponDialog.findViewById(R.id.toggle_recycler_view);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);

                originalPrice = couponDialog.findViewById(R.id.original_price);
                discountedPrice = couponDialog.findViewById(R.id.discounted_price);
                couponRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));

                ///for coupon Dialog///
                originalPrice.setText(productPrice.getText());
                productOriginalPrice = pPrice;
                RewardsAdapter rewardsAdapter = new RewardsAdapter(position, DBquaries.rewardsModelList, true, couponRecyclerView, selectedCoupon, productOriginalPrice, couponTitle, couponExpiryDate, couponBody, discountedPrice, cartItemModelList);
                couponRecyclerView.setAdapter(rewardsAdapter);
                rewardsAdapter.notifyDataSetChanged();

                toggleRecyclerVIew.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();
                    }
                });

                selectedCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();
                    }
                });


                applyCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!couponExpiryDate.getText().toString().equals("Validity")) {
                            if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                                for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {

                                    if (rewardsModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                        rewardsModel.setAlreadyUsed(true);
                                        couponRedeemLayout.setBackground(itemView.getContext().getDrawable(R.drawable.reward_gradiant_background));
                                        couponRedemptionBody.setText(rewardsModel.getBody());
                                        redeemBtn.setText("Coupons");
                                    }
                                }

                                couponApplied.setVisibility(View.VISIBLE);
                                if (!discountedPrice.getText().toString().equals("invalid!")) {
                                    cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2));
                                    productPrice.setText(discountedPrice.getText());
                                    String offerDiscountedAmount = String.valueOf(Long.valueOf(pPrice) - Long.valueOf(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2)));
                                    couponApplied.setText("Coupon applied -" + offerDiscountedAmount + " Tk.");
                                } else {
                                    couponApplied.setText("Invalid Coupon");
                                }
                                notifyItemChanged(cartItemModelList.size() - 1);
                                couponDialog.dismiss();

                            }
                        }
                    }
                });

                removeCouponBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {
                            if (rewardsModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardsModel.setAlreadyUsed(false);
                            }
                        }
                        couponTitle.setText("Coupon");
                        couponExpiryDate.setText("Validity");
                        couponBody.setText("Tap Here to select your coupons");
                        couponApplied.setVisibility(View.INVISIBLE);
                        couponRedeemLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                        couponRedemptionBody.setText("Apply your coupon here!");
                        redeemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCouponId(null);
                        productPrice.setText("Tk." + pPrice + "/-");
                        notifyItemChanged(cartItemModelList.size() - 1);
                        couponDialog.dismiss();
                    }
                });


                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                    for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {

                        if (rewardsModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            couponRedeemLayout.setBackground(itemView.getContext().getDrawable(R.drawable.reward_gradiant_background));
                            couponRedemptionBody.setText(rewardsModel.getBody());
                            redeemBtn.setText("Coupons");

                            couponBody.setText(rewardsModel.getBody());
                            if (rewardsModel.getType().equals("Discount")) {
                                couponTitle.setText(rewardsModel.getType());
                            } else {
                                couponTitle.setText("Flat Tk." + rewardsModel.getDiscORamt() + " OFF");
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM,YYYY");
                            couponExpiryDate.setText(simpleDateFormat.format(rewardsModel.getTimestamp()));

                        }
                    }

                    discountedPrice.setText("Tk." + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    couponApplied.setVisibility(View.VISIBLE);

                    productPrice.setText("Tk." + cartItemModelList.get(position).getDiscountedPrice() + "/-");
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(pPrice) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    couponApplied.setText("Coupon applied -" + offerDiscountedAmount + " Tk.");

                } else {

                    couponApplied.setVisibility(View.INVISIBLE);
                    couponRedeemLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                    couponRedemptionBody.setText("Apply your coupon here!");
                    redeemBtn.setText("Redeem");

                }
                //////coupon Dialog////////

                productQuantity.setText("Quantity: " + quantity);

                if (!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.errorRed)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.black)));

                    }
                }

                final Dialog quantityDialog = new Dialog(itemView.getContext());
                quantityDialog.setContentView(R.layout.quantity_dialog);
                quantityDialog.setCancelable(true);
                quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_number);
                Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn_dialog);
                Button okBtn = quantityDialog.findViewById(R.id.ok_btn_dialog);

                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        quantityNo.setHint("Max " + String.valueOf(maxQuantity));
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (!TextUtils.isEmpty(quantityNo.getText())) {

                                    if (Long.valueOf(quantityNo.getText().toString()) <= maxQuantity && Long.valueOf(quantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof MainDashboard) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {

                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Quantity: " + quantityNo.getText().toString());

                                        notifyItemChanged(cartItemModelList.size() - 1);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                        if (!showDeleteBtn) {

                                            DeliveryActivity.loadingDialog.show();

                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            int initialQty = Integer.parseInt(quantity);
                                            ///finding diff between def value and user value
                                            int finalQuantity = Integer.parseInt(quantityNo.getText().toString());
                                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQuantity > initialQty) {


                                                for (int y = 0; y < finalQuantity - initialQty; y++) {
                                                    String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timestamp = new HashMap<>();
                                                    timestamp.put("time", FieldValue.serverTimestamp());

                                                    int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY")
                                                            .document(quantityDocumentName).set(timestamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            qtyIds.add(quantityDocumentName);

                                                            if (finalY + 1 == finalQuantity - initialQty) {

                                                                firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY")
                                                                        .orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    List<String> serverQuantity = new ArrayList<>();
                                                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                                        serverQuantity.add(queryDocumentSnapshot.getId());
                                                                                    }

                                                                                    long availableQty = 0;

                                                                                    for (String qtyId : qtyIds) {
                                                                                        if (!serverQuantity.contains(qtyId)) {

                                                                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                            DeliveryActivity.cartItemModelList.get(position).setMax_quantity(availableQty);
                                                                                            Toast.makeText(itemView.getContext(), "Sorry! All Products may not be available in required quantity", Toast.LENGTH_SHORT).show();


                                                                                        } else {
                                                                                            availableQty++;
                                                                                        }

                                                                                    }
                                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                                } else {
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                                DeliveryActivity.loadingDialog.dismiss();
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });
                                                }
                                            } else if (initialQty > finalQuantity) {

                                                for (int x = 0; x < initialQty - finalQuantity; x++) {
                                                    String qtyID = qtyIds.get(qtyIds.size() - 1 - x);

                                                    int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").document(qtyID)
                                                            .delete()
                                                            .addOnSuccessListener(aVoid -> {
                                                                qtyIds.remove(qtyID);
                                                                DeliveryActivity.cartAdapter.notifyDataSetChanged();

                                                                if (finalX+1 == initialQty - finalQuantity){
                                                                    DeliveryActivity.loadingDialog.dismiss();
                                                                }
                                                            });

                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max Quantity " + String.valueOf(maxQuantity), Toast.LENGTH_SHORT).show();
                                        quantityDialog.dismiss();
                                    }
                                }
                                quantityDialog.dismiss();
                            }
                        });

                        quantityDialog.show();

                    }
                });

                if (offerAppliedNo > 0) {
                    offerApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(cutPrice) - Long.valueOf(pPrice));
                    offerApplied.setText("offer applied -" + offerDiscountedAmount + " Tk.");
                } else {
                    offerApplied.setVisibility(View.GONE);
                }

            } else {
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                cuuttedPrice.setText("");
                couponRedeemLayout.setVisibility(View.GONE);
                productQuantity.setVisibility(View.INVISIBLE);
                freeCoupon.setVisibility(View.INVISIBLE);
                couponApplied.setVisibility(View.INVISIBLE);
                offerApplied.setVisibility(View.INVISIBLE);
                freeCouponIcon.setVisibility(View.INVISIBLE);


            }


            if (showDeleteBtn) {
                removeButton.setVisibility(View.VISIBLE);
            } else {

                removeButton.setVisibility(View.GONE);
            }


            redeemBtn.setOnClickListener(v -> {

                for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {
                    if (rewardsModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                        rewardsModel.setAlreadyUsed(false);
                    }
                }

                couponDialog.show();

            });


            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!ProductDetailsActivity.running_cart_query) {
                        ProductDetailsActivity.running_cart_query = true;
                        DBquaries.removeFromCart(position, itemView.getContext(), cartTotalAmount);
                    }

                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                        for (RewardsModel rewardsModel : DBquaries.rewardsModelList) {
                            if (rewardsModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardsModel.setAlreadyUsed(false);
                            }
                        }
                    }




                }
            });
        }

        private void showDialogRecyclerView() {

            if (couponRecyclerView.getVisibility() == View.GONE) {
                couponRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupon.setVisibility(View.GONE);
            } else {
                couponRecyclerView.setVisibility(View.GONE);
                selectedCoupon.setVisibility(View.VISIBLE);
            }


        }
    }


    class CartTotalAMountViewHolder extends RecyclerView.ViewHolder {
        private TextView totalItems, totalItemsPrice, deliveryPrice, totalAmount, savedAmount;

        public CartTotalAMountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemsPrice = itemView.findViewById(R.id.total_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_charge_price);
            totalAmount = itemView.findViewById(R.id.final_total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);

        }

        private void setTotalAmount(int totalItemText, int totalItemPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price(" + totalItemText + ") items");
            totalItemsPrice.setText("Tk. " + totalItemPriceText + "/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText("Tk. " + deliveryPriceText + "/-");
            }

            totalAmount.setText("Tk. " + totalAmountText + "/-");
            cartTotalAmount.setText("Tk. " + totalAmountText + "/-");
            savedAmount.setText("You saved " + savedAmountText + "Tk. on this order.");
            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();

            if (totalItemPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
