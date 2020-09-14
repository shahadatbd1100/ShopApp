package com.example.carapp.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.HelperClass.CartItemModel;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.RewardsModel;
import com.example.carapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.MyRewardViewHolder> {

    List<RewardsModel> rewardsModelList;
    List<CartItemModel> cartItemModelList;
    private Boolean useMiniLayout = false;
    private RecyclerView couponRecyclerView;
    private LinearLayout selectedCoupon;
    private String productOriginalPrice;
    private TextView selectedCouponTitle;
    private TextView selectedCouponExpiryDate;
    private TextView selectedCouponBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;

    public RewardsAdapter( int cartItemPosition,List<RewardsModel> rewardsModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView selectedCouponTitle, TextView selectedCouponExpiryDate, TextView selectedCouponBody, TextView discountedPrice,List<CartItemModel> cartItemModelList) {
        this.cartItemPosition = cartItemPosition;
        this.rewardsModelList = rewardsModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView = couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = selectedCouponTitle;
        this.selectedCouponExpiryDate = selectedCouponExpiryDate;
        this.selectedCouponBody = selectedCouponBody;
        this.discountedPrice = discountedPrice;
        this.cartItemModelList = cartItemModelList;
    }

    public RewardsAdapter( List<RewardsModel> rewardsModelList, Boolean useMiniLayout, RecyclerView couponRecyclerView, LinearLayout selectedCoupon, String productOriginalPrice, TextView selectedCouponTitle, TextView selectedCouponExpiryDate, TextView selectedCouponBody, TextView discountedPrice) {
        this.cartItemPosition = cartItemPosition;
        this.rewardsModelList = rewardsModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponRecyclerView = couponRecyclerView;
        this.selectedCoupon = selectedCoupon;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = selectedCouponTitle;
        this.selectedCouponExpiryDate = selectedCouponExpiryDate;
        this.selectedCouponBody = selectedCouponBody;
        this.discountedPrice = discountedPrice;
    }

    public RewardsAdapter(List<RewardsModel> rewardsModelList, Boolean useMiniLayout) {
        this.rewardsModelList = rewardsModelList;
        this.useMiniLayout = useMiniLayout;
    }

    @NonNull
    @Override
    public MyRewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useMiniLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_rewards_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        MyRewardViewHolder myRewardViewHolder = new MyRewardViewHolder(view);
        return myRewardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRewardViewHolder holder, int position) {
        String couponId = rewardsModelList.get(position).getCouponId();
        String type = rewardsModelList.get(position).getType();
        Date validity = rewardsModelList.get(position).getTimestamp();
        String body = rewardsModelList.get(position).getBody();
        String lowerLimit = rewardsModelList.get(position).getLowerLimit();
        String upperLimit = rewardsModelList.get(position).getUpperLimit();
        String discORamt = rewardsModelList.get(position).getDiscORamt();
        boolean alreadyUsed = rewardsModelList.get(position).isAlreadyUsed();

        holder.setReward(couponId,type, validity, body, upperLimit, lowerLimit, discORamt,alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardsModelList.size();
    }

    public class MyRewardViewHolder extends RecyclerView.ViewHolder {
        private TextView rewardTitle, rewardDate, rewardBody;

        public MyRewardViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardTitle = itemView.findViewById(R.id.coupon_title);
            rewardDate = itemView.findViewById(R.id.coupon_validity);
            rewardBody = itemView.findViewById(R.id.coupon_body);
        }

        public void setReward(final String couponId,final String type, final Date validity, final String body, String upperLimit, String lowerLimit, String discORamt, boolean alreadyUsed) {


            if (type.equals("Discount")) {
                rewardTitle.setText(type);
            } else {
                rewardTitle.setText("Flat Tk." + discORamt + " OFF");
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM,YYYY");


            if (alreadyUsed){
                rewardDate.setText("Already used");
                rewardDate.setTextColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                rewardBody.setTextColor(Color.parseColor("#50ffffff"));
                rewardTitle.setTextColor(Color.parseColor("#50ffffff"));
            }else {
                rewardBody.setTextColor(Color.parseColor("#ffffff"));
                rewardTitle.setTextColor(Color.parseColor("#ffffff"));
                rewardDate.setTextColor(itemView.getContext().getResources().getColor(R.color.coupon_purple));
                rewardDate.setText(simpleDateFormat.format(validity));
            }


//            rewardDate.setText("till "+validity);
            rewardBody.setText(body);


            if (useMiniLayout) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!alreadyUsed){


                            selectedCouponTitle.setText(type);
                            selectedCouponBody.setText(body);
                            selectedCouponExpiryDate.setText(simpleDateFormat.format(validity));

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discORamt) / 100;
                                    discountedPrice.setText("Tk." + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                } else {
                                    discountedPrice.setText("Tk." + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discORamt)) + "/-");
                                }

                                if (cartItemPosition != -1) {
                                   cartItemModelList.get(cartItemPosition).setSelectedCouponId(couponId);
                                }

                            } else {

                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(null);
                                }
                                discountedPrice.setText("invalid!");
                                Toast.makeText(itemView.getContext(), "Sorry ! Product does not match the coupon terms!", Toast.LENGTH_SHORT).show();
                            }

                            if (couponRecyclerView.getVisibility() == View.GONE) {
                                couponRecyclerView.setVisibility(View.VISIBLE);
                                selectedCoupon.setVisibility(View.GONE);
                            } else {
                                couponRecyclerView.setVisibility(View.GONE);
                                selectedCoupon.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
    }
}
