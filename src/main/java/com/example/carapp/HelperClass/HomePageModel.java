package com.example.carapp.HelperClass;

import android.text.Layout;

import java.util.List;

public class HomePageModel {

    public static  final int BANNER_SLIDER = 0;
    public static  final int STRIP_AD_BANNER = 1;
    public static  final int HORIZONTAL_PRODUCT_VIEW = 2;
    public static  final int GRID_PRODUCT_VIEW = 3;

    private int type;
    private String backgroundColor;

    ////Banner Slider
    private List<SliderModel> mData;

    public HomePageModel(int type, List<SliderModel> mData) {
        this.type = type;
        this.mData = mData;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public List<SliderModel> getmData() {
        return mData;
    }
    public void setmData(List<SliderModel> mData) {
        this.mData = mData;
    }
    ////Banner Slider


    ////Strip Ad Banner
    private String resource;

    public HomePageModel(int type, String  resource) {
        this.type = type;
        this.resource = resource;
    }
    public String getResource() {
        return resource;
    }
    public void setResource(String resource) {
        this.resource = resource;
    }
    ////Strip Ad Banner

    // //Horizontal Product Layout And Grid Layout Product///////
    private String title;
    private List<HorizontalProduct> horizontalProductList;
    private List<WishlistModel> viewAllProductList;

    public HomePageModel(int type, String title,String backgroundColor ,List<HorizontalProduct> horizontalProductList,List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductList = horizontalProductList;
        this.viewAllProductList= viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }
    // //Horizontal Product Layout ///////

    // //Grid Product Layout ///////
    public HomePageModel(int type, String title, String backgroundColor , List<HorizontalProduct> horizontalProductList) {
        this.type = type;
        this.title = title;
        this.backgroundColor = backgroundColor;
        this.horizontalProductList = horizontalProductList;
    }
    // //Grid Product Layout ///////
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProduct> getHorizontalProductList() {
        return horizontalProductList;
    }

    public void setHorizontalProductList(List<HorizontalProduct> horizontalProductList) {
        this.horizontalProductList = horizontalProductList;
    }

    // //Horizontal Product Layout///////
    // //Grid Product Layout///////

    //same as Horizontal Product

    // //Grid Product Layout///////

}
