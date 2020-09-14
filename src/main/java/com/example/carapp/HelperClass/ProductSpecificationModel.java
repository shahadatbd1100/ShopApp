package com.example.carapp.HelperClass;

public class ProductSpecificationModel {


    String featureName,FeatureDesc;

    public ProductSpecificationModel(String featureName, String featureDesc) {
        this.featureName = featureName;
        FeatureDesc = featureDesc;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureDesc() {
        return FeatureDesc;
    }

    public void setFeatureDesc(String featureDesc) {
        FeatureDesc = featureDesc;
    }
}
