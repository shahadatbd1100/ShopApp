package com.example.carapp.HelperClass;

public class ConfirmOrderModel {

    String UID,name,mobileNo,address,pincode,payment;

    public ConfirmOrderModel(String UID, String name, String mobileNo, String address, String pincode,String payment) {
        this.UID = UID;
        this.name = name;
        this.mobileNo = mobileNo;
        this.address = address;
        this.pincode = pincode;
        this.payment = payment;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
