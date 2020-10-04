package com.yenepaySDK.handlers;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;

public class ReturnListenerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentResponse response = PaymentOrderManager.parseResponse(getIntent().getData());
        startActivity(PaymentHandlerActivity.createResponseHandlingIntent(
                this, getIntent().getData()));
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
