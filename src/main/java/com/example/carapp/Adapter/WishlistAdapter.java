package com.example.carapp.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.HelperClass.DBquaries;
import com.example.carapp.HelperClass.WishlistModel;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private boolean fromSearch;
    List<WishlistModel> wishlistModelList;
    Boolean wishlist;
    private int lastPosition = -1;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String productID = wishlistModelList.get(position).getProductID();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCoupon = wishlistModelList.get(position).getFreeCoupon();
        String rating = wishlistModelList.get(position).getRating();
        long totalRating = wishlistModelList.get(position).getTotalRating();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();

        holder.setData(resource, title, freeCoupon, rating, totalRating, productPrice, cuttedPrice, paymentMethod, position, productID, inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_scale_anim);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }


    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle, freeCoupons,extraRating;
        private ImageView couponIcon;
        private TextView productPrice, cuttedPrice, paymentMethod, rating, totalRatings;
        private View priceCut;
        private ImageView deleteBtn;
        private LinearLayout wishlist_cash_on_delivery_layout,new_linear_layout;
        private View price_cut_divider_new;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.wishlist_Image);
            productTitle = itemView.findViewById(R.id.wishlist_product_title);
            freeCoupons = itemView.findViewById(R.id.wishlist_coupon_text_view);
            couponIcon = itemView.findViewById(R.id.wishlist_free_coupon_icon);
            productPrice = itemView.findViewById(R.id.wishlist_product_price);
            cuttedPrice = itemView.findViewById(R.id.wishlist_cutted_price);
            paymentMethod = itemView.findViewById(R.id.wishlist_product_cash_on_delivery);
            rating = itemView.findViewById(R.id.wishlist_product_rating);
            totalRatings = itemView.findViewById(R.id.wishlist_product_total_rating);
            priceCut = itemView.findViewById(R.id.price_cut_divider_new);
            deleteBtn = itemView.findViewById(R.id.wishlist_remove_btn);
            wishlist_cash_on_delivery_layout = itemView.findViewById(R.id.wishlist_cash_on_delivery_layout);
            extraRating = itemView.findViewById(R.id.extra_rating);
            new_linear_layout = itemView.findViewById(R.id.new_linear_layout);
            price_cut_divider_new = itemView.findViewById(R.id.price_cut_divider_new);
        }

        private void setData(String resource, String title, long freeCouponsNo, String avaraeRate, long totalRatingsNo, String price, String cuttedPriceValue, boolean paymentMethodValue, final int index, final String productId, boolean inStock) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_photo)).into(productImage);
            productTitle.setText(title);
            if (freeCouponsNo != 0 && inStock) {
                couponIcon.setVisibility(View.VISIBLE);
                if (freeCouponsNo == 1) {
                    freeCoupons.setText("free " + freeCouponsNo + " coupon");
                } else {
                    freeCoupons.setText("free " + freeCouponsNo + " coupons");
                }
            } else {
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock) {

                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
                cuttedPrice.setVisibility(View.VISIBLE);
                extraRating.setVisibility(View.VISIBLE);
                price_cut_divider_new.setVisibility(View.VISIBLE);
                new_linear_layout.setVisibility(View.VISIBLE);


                rating.setText(avaraeRate);
                totalRatings.setText("(" + totalRatingsNo + ") ratings");
                productPrice.setText("Tk." + price + "/-");
                cuttedPrice.setText("Tk." + cuttedPriceValue + "/-");
                linearLayout.setVisibility(View.VISIBLE);

                if (paymentMethodValue) {
                    paymentMethod.setVisibility(View.VISIBLE);
                    paymentMethod.setText("Cash On Deliver");
                } else {
                    //paymentMethod.setVisibility(View.VISIBLE);
                    paymentMethod.setText("Paid");
                }
            } else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                new_linear_layout.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                price_cut_divider_new.setVisibility(View.INVISIBLE);
                extraRating.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.errorRed));
                paymentMethod.setVisibility(View.INVISIBLE);
                cuttedPrice.setVisibility(View.INVISIBLE);
            }

            if (wishlist) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.INVISIBLE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBquaries.removeFromWishList(index, itemView.getContext());
                    }
                }
            });

            itemView.setOnClickListener(v -> {
                if (fromSearch){
                    ProductDetailsActivity.fromSearch = true;
                }
                Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_ID", productId);
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
