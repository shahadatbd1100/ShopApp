package com.example.carapp.HelperClass;

import android.content.Context;
import android.content.Intent;

import com.example.carapp.PaymentSuccessActivity;

public class BkashJavaScriptInterface {

    Context mContext;


    public BkashJavaScriptInterface(Context mContext) {
        this.mContext = mContext;
    }


    public void OnPaymentSuccess(String data){
        String[] paymentData = data.split("&");
        String paymentID = paymentData[0].trim().replace("PaymentID=","").trim();
        String transactionID = paymentData[1].trim().replace("TransactionID=","").trim();
        String amount = paymentData[2].trim().replace("Amount=","").trim();


        Intent intent = new Intent(mContext, PaymentSuccessActivity.class);
        intent.putExtra("TRANSACTION_ID",transactionID);
        intent.putExtra("PAID_AMOUNT",amount);
        intent.putExtra("PAYMENT_SERIALIZE",data);
        mContext.startActivity(intent);

    }
}
