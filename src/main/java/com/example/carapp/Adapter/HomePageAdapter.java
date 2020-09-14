package com.example.carapp.Adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.gesture.GestureLibraries;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carapp.HelperClass.HomePageModel;
import com.example.carapp.HelperClass.HorizontalProduct;
import com.example.carapp.HelperClass.SliderModel;
import com.example.carapp.HelperClass.WishlistModel;
import com.example.carapp.ProductDetailsActivity;
import com.example.carapp.R;
import com.example.carapp.ViewAllActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
    private List<HorizontalProduct> horizontalProductList;
//    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.STRIP_AD_BANNER;
            case 2:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 3:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case HomePageModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ads_layout, parent, false);
                BannerSliderViewHolder bannerSliderViewHolder = new BannerSliderViewHolder(bannerSliderView);
                return bannerSliderViewHolder;

            case HomePageModel.STRIP_AD_BANNER:
                View stripAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ads_layout, parent, false);
                StripAdBannerViewHolder stripAdBannerViewHolder = new StripAdBannerViewHolder(stripAdView);
                return stripAdBannerViewHolder;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                HorizontalProductViewHolder horizontalProductViewHolder = new HorizontalProductViewHolder(horizontalView);
                return horizontalProductViewHolder;

            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                GridProductViewHolder gridProductViewHolder = new GridProductViewHolder(gridProductView);
                return gridProductViewHolder;
            default:
                return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getmData();
                ((BannerSliderViewHolder) holder).bannerSliderViewPager(sliderModelList);
                break;

            case HomePageModel.STRIP_AD_BANNER:
                String resource = homePageModelList.get(position).getResource();
                ((StripAdBannerViewHolder) holder).setStripAd(resource);
                break;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String color = homePageModelList.get(position).getBackgroundColor();
                String title = homePageModelList.get(position).getTitle();
                List<HorizontalProduct> horizontalProductList = homePageModelList.get(position).getHorizontalProductList();
                List<WishlistModel> viewAllProducts = homePageModelList.get(position).getViewAllProductList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductList, title,color,viewAllProducts);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String mColor = homePageModelList.get(position).getBackgroundColor();
                String title2 = homePageModelList.get(position).getTitle();
                List<HorizontalProduct> gridProductLayout = homePageModelList.get(position).getHorizontalProductList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductLayout, title2,mColor);
                break;


            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }


    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager sliderViewPager;
        private int currentPage ;
        private Timer timer;
        final private long DELAY_TIME = 2000;
        final private long PERIOD_TIME = 2000;
        private List<SliderModel> arrangeList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            sliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }

        private void bannerSliderViewPager(final List<SliderModel> mData) {

            currentPage = 2;
            if (timer!=null){
                timer.cancel();
            }
            arrangeList= new ArrayList<>();
            for (int x= 0 ; x<mData.size(); x++){
                arrangeList.add(x,mData.get(x));
            }

            arrangeList.add(0,mData.get(mData.size()-2));
            arrangeList.add(1,mData.get(mData.size()-1));
            arrangeList.add(mData.get(0));
            arrangeList.add(mData.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangeList);
            sliderViewPager.setAdapter(sliderAdapter);
            sliderViewPager.setClipToPadding(false);
            sliderViewPager.setPageMargin(20);
            sliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    if (state == ViewPager.SCROLL_STATE_IDLE) ;
                    pageLooper(arrangeList);
                }
            };

            sliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerAnimation(arrangeList);

            sliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    pageLooper(arrangeList);
                    stopBannerAnimation();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerAnimation(arrangeList);
                    }
                    return false;
                }
            });

            /////////////////////////////End Banner Slider///////////

        }

        private void pageLooper(List<SliderModel> mData) {

            if (currentPage == mData.size() - 2) {
                currentPage = 2;
                sliderViewPager.setCurrentItem(currentPage);

            }
            if (currentPage == 1) {
                currentPage = mData.size() - 2;
                sliderViewPager.setCurrentItem(currentPage);

            }

        }

        private void startBannerAnimation(final List<SliderModel> mData) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= mData.size()) {
                        currentPage = 1;
                    }
                    sliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerAnimation() {
            timer.cancel();
        }
    }

    public class StripAdBannerViewHolder extends RecyclerView.ViewHolder {

        ImageView stripImageView;
        LinearLayout stripAdContainer;


        public StripAdBannerViewHolder(@NonNull View itemView) {
            super(itemView);

            stripImageView = itemView.findViewById(R.id.strip_ads_image);
            stripAdContainer = itemView.findViewById(R.id.strip_ads_container);
        }

        private void setStripAd(String resource) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.strip_ads)).into(stripImageView);
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private TextView horizontalLayoutTitle;
        private Button viewAllBtn;
        private RecyclerView horizontalRecyclerView;
        private CardView container_horizontal;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);

            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_scroll_recycler_layout);
            viewAllBtn = itemView.findViewById(R.id.horizontal_scroll_layout_btn);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            container_horizontal = itemView.findViewById(R.id.container_horizontal);

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setHorizontalProductLayout(List<HorizontalProduct> mProduct, final String title, String color, final List<WishlistModel> viewAllProductList) {

            //container_horizontal.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitle.setText(title);

            if (mProduct.size() > 8) {
                viewAllBtn.setVisibility(View.VISIBLE);
                viewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent intent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        intent.putExtra("layout_code",0);
                        intent.putExtra("title",title);
                        itemView.getContext().startActivity(intent);
                    }
                });
            } else {
                viewAllBtn.setVisibility(View.INVISIBLE);
            }

            HorizontalProductAdapter horizontalProductAdapter = new HorizontalProductAdapter(mProduct);
            horizontalRecyclerView.setAdapter(horizontalProductAdapter);
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder{

        NewGridProductAdapter gridProductAdapter;
        Button gridViewAll;
        TextView title;
        ConstraintLayout container_grid;
        private GridLayout gridProductLayout;
        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container_grid = itemView.findViewById(R.id.container_grid);
            gridViewAll = itemView.findViewById(R.id.grid_scroll_layout_btn);
            title = itemView.findViewById(R.id.grid_scroll_layout_title);
            gridProductLayout = itemView.findViewById(R.id.gridLayout);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void setGridProductLayout(final List<HorizontalProduct> gProduct, final String myTitle, String color) {
            container_grid.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            title.setText(myTitle);

            for (int x=0;x<4;x++){
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_item_image_view);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_item_title_text_view);
                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_item_desc_text_view);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.horizontal_item_price_text_view);

                Glide.with(itemView.getContext()).load(gProduct.get(x).getImage()).apply(new RequestOptions().placeholder(R.drawable.profile_photo)).into(productImage);
                productTitle.setText(gProduct.get(x).getTitle());
                productDescription.setText(gProduct.get(x).getDesc());
                productPrice.setText("TK."+gProduct.get(x).getPrice()+"/-");

                final int finalX = x;
                gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        intent.putExtra("PRODUCT_ID",gProduct.get(finalX).getProductId());
                        itemView.getContext().startActivity(intent);
                    }
                });
            }


            gridViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewAllActivity.gridData = gProduct;
                    Intent intent = new Intent(itemView.getContext(), ViewAllActivity.class);
                    intent.putExtra("layout_code",1);
                    intent.putExtra("title",myTitle);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
