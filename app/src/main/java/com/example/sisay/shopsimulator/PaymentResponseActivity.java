package com.example.sisay.shopsimulator;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.handlers.PaymentHandlerActivity;

public class PaymentResponseActivity extends AppCompatActivity {

    private PaymentResponse mPaymentResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        PaymentResponseFragment fragment = null;
        if(getIntent() != null){
            if(getIntent().hasExtra(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE)) {
                mPaymentResponse = (PaymentResponse) getIntent().getSerializableExtra(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE);
                fragment = PaymentResponseFragment.getInstance(mPaymentResponse);
            } else if(getIntent().hasExtra(PaymentHandlerActivity.KEY_ERROR_MESSAGE)){
                String errorMessage = getIntent().getStringExtra(PaymentHandlerActivity.KEY_ERROR_MESSAGE);
                fragment = PaymentResponseFragment.getInstance(errorMessage);
            }
        }
        if(fragment != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }
    }

}
