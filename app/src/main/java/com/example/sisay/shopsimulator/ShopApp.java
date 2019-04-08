package com.example.sisay.shopsimulator;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.model.YenePayConfiguration;

public class ShopApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PendingIntent completionIntent = PendingIntent.getActivity(getApplicationContext(),
                PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
                new Intent(getApplicationContext(), PaymentResponseActivity.class), 0);
        PendingIntent cancelationIntent = PendingIntent.getActivity(getApplicationContext(),
                PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE,
                new Intent(getApplicationContext(), PaymentResponseActivity.class), 0);
        YenePayConfiguration.setDefaultInstance(new YenePayConfiguration.Builder(getApplicationContext())
        .setGlobalCompletionIntent(completionIntent)
        .setGlobalCancelIntent(cancelationIntent)
        .build());
    }
}
