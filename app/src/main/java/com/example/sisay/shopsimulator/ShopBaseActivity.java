package com.example.sisay.shopsimulator;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.YenePayPaymentActivity;
import com.yenepaySDK.handlers.PaymentHandlerActivity;

public class ShopBaseActivity extends YenePayPaymentActivity {
    private static final String TAG = "ShopBaseActivity";
    public static final int CHECKOUT_REQ_CODE = 78;

    private final Handler mHandler = new Handler();

    protected void showMessage(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShopBaseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String message = "onActivityResult Called, requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data;
//        if(requestCode == PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE) {
//            if (resultCode == RESULT_OK) {
//
//                final PaymentResponse response = PaymentOrderManager.parseResponse(data);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        PaymentResponseFragment fragment = PaymentResponseFragment.getInstance(response);
//                        fragment.show(getSupportFragmentManager(), "payment_response");
//                    }
//                }, 100);
//
//                Log.d(TAG, "onActivityResult: success response :" + response);
////                showMessage(message);
//            } else if (resultCode == RESULT_CANCELED && data != null) {
//                showMessage("YenePay Checkout Request Canceled");
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    @Override
    public void onPaymentResponseArrived(PaymentResponse response) {
        PaymentResponseFragment fragment = PaymentResponseFragment.getInstance(response);
        fragment.show(getSupportFragmentManager(), "payment_response");
    }

    @Override
    public void onPaymentResponseError(String error) {
        showMessage(error);
    }
}
