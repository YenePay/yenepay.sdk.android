package com.yenepaySDK.handlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.YenePayUriParser;
import com.yenepaySDK.YenepayCheckOutIntentAction;
import com.yenepaySDK.model.YenePayConfiguration;

public class PaymentHandlerActivity extends AppCompatActivity {

    public static final String KEY_PAYMENT_INTENT = "key_payment_intent";
    private static final String KEY_COMPLETE_INTENT = "key_complete_intent";
    private static final String KEY_CANCEL_INTENT = "key_cancel_intent";
    public static final String KEY_PAYMENT_RESPONSE = "key_payment_response";
    public static final String KEY_ERROR_MESSAGE = "key_error_message";
    public static final String KEY_PAYMENT_STARTED = "key_payment_started";
    public static final int PAYMENT_REQUEST_CODE = 131;
    public static final String USER_TERMINATED_PAYMENT_FLOW = "User terminated payment flow";

    private PendingIntent mCompleteIntent;
    private PendingIntent mCancelIntent;
    private Intent mPaymentRequestIntent;
    private boolean mIsPaymentFlowStarted;
    /**
     * Creates an intent to start an authorization flow.
     * @param context the package context for the app.
     * @param paymentIntent the intent to be used to get authorization from the user.
     * @param completeIntent the intent to be sent when the flow completes.
     * @param cancelIntent the intent to be sent when the flow is canceled.
     */
    public static Intent createStartIntent(
            Context context,
            Intent paymentIntent,
            PendingIntent completeIntent,
            PendingIntent cancelIntent) {
        Intent intent = createBaseIntent(context);
        intent.putExtra(KEY_PAYMENT_INTENT, paymentIntent);
        intent.putExtra(KEY_COMPLETE_INTENT, completeIntent);
        intent.putExtra(KEY_CANCEL_INTENT, cancelIntent);
        return intent;
    }

    /**
     * Creates an intent to start an authorization flow.
     * @param context the package context for the app.
     * @param paymentIntent the intent to be used to get authorization from the user.
     */
    public static Intent createStartForResultIntent(
            Context context,
            Intent paymentIntent) {
        return createStartIntent(context, paymentIntent, null, null);
    }

    /**
     * Creates an intent to handle the completion of an authorization flow. This restores
     * the original AuthorizationManagementActivity that was created at the start of the flow.
     * @param context the package context for the app.
     * @param responseUri the response URI, which carries the parameters describing the response.
     */
    public static Intent createResponseHandlingIntent(Context context, Uri responseUri) {
        Intent intent = createBaseIntent(context);
        intent.setData(responseUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    private static Intent createBaseIntent(Context context) {
        return new Intent(context, PaymentHandlerActivity.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            extractState(getIntent().getExtras());
        } else {
            extractState(savedInstanceState);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mPaymentRequestIntent != null){
            if(mCompleteIntent != null || !TextUtils.equals(mPaymentRequestIntent.getAction(),
                    YenepayCheckOutIntentAction.YENEPAY_INTENT_FILTER_ACTION_CHECKOUT)) {
                startActivity(mPaymentRequestIntent);
                finish();
            } else {
                startActivityForResult(mPaymentRequestIntent, PAYMENT_REQUEST_CODE);
            }
            mPaymentRequestIntent = null;
            mIsPaymentFlowStarted = true;
            return;
        }


        if (getIntent().getData() != null) {
            handlePaymentResponseComplete();
        } else {
            handlePaymentCanceled();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PAYMENT_REQUEST_CODE){
            if(resultCode == RESULT_OK) {
                setResult(RESULT_OK, extractResponseData(data));
            } else if(data != null){
                Intent cancelResponse = extractResponseData(data);
                if(cancelResponse.hasExtra(KEY_PAYMENT_RESPONSE)){
                    //Even if user cancels here if response data is returned we change the result to ok
                    //so that user can handle the payment status appropriately
                    setResult(RESULT_OK, cancelResponse);
                } else {
                    setResult(RESULT_CANCELED, cancelResponse);
                }
            } else {
                //If no intent just return cancel
                Intent cancelData = new Intent();
                cancelData.putExtra(KEY_ERROR_MESSAGE, USER_TERMINATED_PAYMENT_FLOW);
                setResult(RESULT_CANCELED, cancelData);
            }
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handlePaymentResponseComplete() {
        Uri responseUri = getIntent().getData();
        Intent responseData = extractResponseData(responseUri);
        if (responseData == null) {
//            Logger.error("Failed to extract OAuth2 response from redirect");
            return;
        }
        responseData.setData(responseUri);
        PendingIntent pendingIntent = getCompletionIntent();
        if (pendingIntent != null) {
//            Logger.debug("Authorization complete - invoking completion intent");
            try {
                pendingIntent.send(this, 0, responseData);
            } catch (PendingIntent.CanceledException ex) {
//                Logger.error("Failed to send completion intent", ex);
            }
        } else {
            setResult(RESULT_OK, responseData);
        }
    }

    private PendingIntent getCompletionIntent(){
        if(mCompleteIntent != null){
            return mCompleteIntent;
        } else {
            YenePayConfiguration configuration = YenePayConfiguration.getDefaultInstance();
            if(!mIsPaymentFlowStarted && configuration.getGlobalCompletionIntent() != null){
                return configuration.getGlobalCompletionIntent();
            }
        }
        return null;
    }

    private PendingIntent getCancelationIntent(){
        if(mCancelIntent != null){
            return mCancelIntent;
        } else {
            YenePayConfiguration configuration = YenePayConfiguration.getDefaultInstance();
            if(!mIsPaymentFlowStarted && configuration.getGlobalCancelIntent() != null){
                return configuration.getGlobalCancelIntent();
            }
        }
        return null;
    }

    private void handlePaymentCanceled() {
//        Logger.debug("Authorization flow canceled by user");
        Intent cancelData = new Intent();
        cancelData.putExtra(KEY_ERROR_MESSAGE, "User terminated payment flow");
        PendingIntent pendingIntent = getCancelationIntent();
        if (pendingIntent != null) {
            try {
                pendingIntent.send(this, 0, cancelData);
            } catch (PendingIntent.CanceledException ex) {
//                Logger.error("Failed to send cancel intent", ex);
            }
        } else {
            setResult(RESULT_CANCELED, cancelData);
//            Logger.debug("No cancel intent set - will return to previous activity");
        }
    }

    private void extractState(Bundle state) {
        if(state == null){
            return;
        }
        if(state.containsKey(KEY_PAYMENT_INTENT)){
            mPaymentRequestIntent = state.getParcelable(KEY_PAYMENT_INTENT);
        }
        mIsPaymentFlowStarted = state.getBoolean(KEY_PAYMENT_STARTED, false);
        mCompleteIntent = state.getParcelable(KEY_COMPLETE_INTENT);
        mCancelIntent = state.getParcelable(KEY_CANCEL_INTENT);
    }

    private Intent extractResponseData(Uri responseUri) {
        Intent intent = new Intent();
        if(responseUri != null) {
            String errorMsg = responseUri.getQueryParameter(YenePayUriParser.YENEPAY_ERROR_MSG);
            if(!TextUtils.isEmpty(errorMsg)){
                intent.putExtra(KEY_ERROR_MESSAGE, errorMsg);
            } else {
                PaymentResponse response = PaymentOrderManager.parseResponse(responseUri);
                if(response != null) {
                    intent.putExtra(KEY_PAYMENT_RESPONSE, response);
                }
            }
            return intent;
        }
        return null;
    }

    private Intent extractResponseData(Intent responseIntent) {
        if(responseIntent != null && responseIntent.getExtras() != null && !responseIntent.getExtras().isEmpty()) {
            Intent intent = new Intent();
            if(responseIntent.hasExtra(KEY_ERROR_MESSAGE)){
                intent.putExtra(KEY_ERROR_MESSAGE, responseIntent.getStringExtra(KEY_ERROR_MESSAGE));
            } else {
                PaymentResponse response = PaymentOrderManager.parseResponse(responseIntent.getExtras());
                if (response != null) {
                    intent.putExtra(KEY_PAYMENT_RESPONSE, response);
                }
            }
            return intent;
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_PAYMENT_STARTED, mIsPaymentFlowStarted);
        outState.putParcelable(KEY_COMPLETE_INTENT, mCompleteIntent);
        outState.putParcelable(KEY_CANCEL_INTENT, mCancelIntent);
    }




}
