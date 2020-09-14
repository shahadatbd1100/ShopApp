package com.example.carapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carapp.HelperClass.BkashJavaScriptInterface;
import com.example.carapp.HelperClass.PaymentRequest;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.CheckedOutputStream;

public class BkashPaymentActivity extends AppCompatActivity {

    TextView paytext;
    private WebView wvBkashPayment;
    ProgressBar progressBar;
    String order_id;
    String amount = "";
    String request = "";
    private Button successBTN;
    private String transactionID,saveCurrentDate,saveCurrentTime;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bkash_payment);

        final String payment = getIntent().getStringExtra("PAYMENT");

        paytext = findViewById(R.id.payText);
        wvBkashPayment = findViewById(R.id.wvBkashPayment);
        progressBar = findViewById(R.id.progressBarWeb);
        successBTN = findViewById(R.id.successBTN);

        order_id = getIntent().getStringExtra("order_id");

        paytext.setText(payment);


        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(payment);
        paymentRequest.setIntent("sale");

        Gson gson = new Gson();
        request = gson.toJson(paymentRequest);

        WebSettings webSettings = wvBkashPayment.getSettings();
        webSettings.setJavaScriptEnabled(true);


        //////////////Demo/////////////////


        successBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BkashPaymentActivity.this, DeliveryActivity.class);
                intent.putExtra("TRANSACTION_ID",order_id);
                intent.putExtra("PAID_AMOUNT",payment);
                intent.putExtra("Success",true);
                startActivity(intent);
                finish();
            }
        });

        //////////////Demo/////////////////


        //enabling webview Setting

        wvBkashPayment.setClickable(true);
        wvBkashPayment.getSettings().setDomStorageEnabled(true);
        wvBkashPayment.getSettings().setAppCacheEnabled(false);
        wvBkashPayment.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wvBkashPayment.clearCache(true);
        wvBkashPayment.getSettings().setAllowFileAccessFromFileURLs(true);
        wvBkashPayment.getSettings().setAllowUniversalAccessFromFileURLs(true);

        /////control any kind of interaction any html

        wvBkashPayment.addJavascriptInterface(new BkashJavaScriptInterface(BkashPaymentActivity.this),"KinYardsPaymentData");

        wvBkashPayment.loadUrl("https://ashikbd1100.000webhostapp.com/");

        wvBkashPayment.setWebViewClient(new CheckoutWebViewClient());
    }

    private class CheckoutWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.equals("https://www.bkash.com/terms-and-conditions")){
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(myIntent);
                return true;
            }

            return super.shouldOverrideUrlLoading(view,url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String paymentRequest = "(paymentRequest:" + request + ")";
            wvBkashPayment.loadUrl("javascript:callReconfigure("+ paymentRequest + " )");
            wvBkashPayment.loadUrl("javascript:clickPayButton()");
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        popupPaymentCancelAlert();
    }

    private void popupPaymentCancelAlert() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Want to cancel payment process?");
        alert.setCancelable(false);
        alert.setTitle("Alert");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BkashPaymentActivity.this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                BkashPaymentActivity.super.onBackPressed();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final  AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}