package com.yenepaySDK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yenepaySDK.errors.InvalidPaymentException;
import com.yenepaySDK.handlers.PaymentHandlerActivity;
import com.yenepaySDK.mobsdk.R;

public class YenePayPaymentActivity extends AppCompatActivity {
    private static final String TAG = "YenePayPaymentActivity";
    private final Handler mHandler = new Handler();
    private PaymentResponse mPaymentResponse;
    private String mPaymentErrorMessage;
    private String DEFAULT_CANCELED_MSG;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DEFAULT_CANCELED_MSG = getString(R.string.default_payment_request_cancelled_msg);
        if(savedInstanceState != null) {
            processIntent(savedInstanceState);
        } else if(getIntent() != null){
            processIntent(getIntent().getExtras());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        checkResponse();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkResponse() {
        if(hasPaymentResponse()){
            if(isPaymentResponseError()){
                onPaymentResponseError(getPaymentErrorMessage());
            } else if(getPaymentResponse() != null){
                onPaymentResponseArrived(getPaymentResponse());
            }
        }
    }

    private void processIntent(Bundle extras) {
        if(extras != null){
            if(extras.containsKey(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE)) {
                mPaymentResponse = (PaymentResponse) extras.getSerializable(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE);
            } else if(extras.containsKey(PaymentHandlerActivity.KEY_ERROR_MESSAGE)){
                mPaymentErrorMessage = extras.getString(PaymentHandlerActivity.KEY_ERROR_MESSAGE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mPaymentResponse != null){
            outState.putSerializable(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE, mPaymentResponse);
        } else if(!TextUtils.isEmpty(mPaymentErrorMessage)){
            outState.putString(PaymentHandlerActivity.KEY_ERROR_MESSAGE, mPaymentErrorMessage);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE) {
            clearResponse();
            if (resultCode == RESULT_OK) {

                final PaymentResponse response = PaymentOrderManager.parseResponse(data);
                mPaymentResponse = response;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPaymentResponseArrived(response);
                    }
                }, 100);
            } else if (resultCode == RESULT_CANCELED) {
                final String error = data != null && data.hasExtra(PaymentHandlerActivity.KEY_ERROR_MESSAGE)?
                        data.getStringExtra(PaymentHandlerActivity.KEY_ERROR_MESSAGE):
                        DEFAULT_CANCELED_MSG;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPaymentResponseError(error);
                    }
                }, 100);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void startPayment(PaymentOrderManager orderManager){
        if(orderManager == null){
            throw new IllegalArgumentException("PaymentOrderManager can not be null");
        }

        try {
            orderManager.startCheckout(this);
        } catch (InvalidPaymentException e) {
            Log.e(TAG, "startPayment: ", e);
        }

    }

    public boolean hasPaymentResponse(){
        return mPaymentResponse != null || !TextUtils.isEmpty(mPaymentErrorMessage);
    }

    protected boolean isPaymentResponseError() {
        return !TextUtils.isEmpty(mPaymentErrorMessage);
    }

    protected PaymentResponse getPaymentResponse() {
        return mPaymentResponse;
    }

    protected String getPaymentErrorMessage() {
        return mPaymentErrorMessage;
    }

    protected void clearResponse() {
        mPaymentErrorMessage = null;
        mPaymentResponse = null;
    }

    public void onPaymentResponseArrived(PaymentResponse response) {
        if(response != null) {
            Toast.makeText(this, response.toString(), Toast.LENGTH_SHORT);
        }
    }

    public void onPaymentResponseError(String error){
        if(!TextUtils.isEmpty(error)) {
            Toast.makeText(this, "Payment Error: " + error, Toast.LENGTH_SHORT);
        }
    }
}
