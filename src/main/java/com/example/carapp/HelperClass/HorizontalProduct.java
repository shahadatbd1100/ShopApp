package com.example.carapp.HelperClass;

public class HorizontalProduct {

    private String productId;
    String image;
    String title, desc,price;

    public HorizontalProduct(String productId, String image, String title, String desc, String price) {
        this.productId = productId;
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
