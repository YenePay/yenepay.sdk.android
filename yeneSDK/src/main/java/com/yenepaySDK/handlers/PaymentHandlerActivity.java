package com.yenepaySDK.handlers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.model.YenePayConfiguration;

public class PaymentHandlerActivity extends AppCompatActivity {

    public static final String KEY_PAYMENT_INTENT = "key_payment_intent";
    private static final String KEY_COMPLETE_INTENT = "key_complete_intent";
    private static final String KEY_CANCEL_INTENT = "key_cancel_intent";
    public static final String KEY_PAYMENT_RESPONSE = "key_payment_response";
    public static final String KEY_ERROR_MESSAGE = "key_error_message";
    public static final String KEY_PAYMENT_STARTED = "key_payment_started";

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
    protected void onResume() {
        super.onResume();
        if(mPaymentRequestIntent != null){
            startActivity(mPaymentRequestIntent);
            mPaymentRequestIntent = null;
            mIsPaymentFlowStarted = true;
            return;
        }


        if (getIntent().getData() != null) {
            handleAuthorizationComplete();
        } else {
            handleAuthorizationCanceled();
        }
        finish();

    }

    private void handleAuthorizationComplete() {
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

    private void handleAuthorizationCanceled() {
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
        mCancelIntent = state.getParcelable(KEY_COMPLETE_INTENT);
        mCancelIntent = state.getParcelable(KEY_CANCEL_INTENT);
    }

    private Intent extractResponseData(Uri responseUri) {
        PaymentResponse response = PaymentOrderManager.parseResponse(responseUri);
        Intent intent = new Intent();
        intent.putExtra(KEY_PAYMENT_RESPONSE, response);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_PAYMENT_STARTED, mIsPaymentFlowStarted);
        outState.putParcelable(KEY_COMPLETE_INTENT, mCompleteIntent);
        outState.putParcelable(KEY_CANCEL_INTENT, mCancelIntent);
    }


}
