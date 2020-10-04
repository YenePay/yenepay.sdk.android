package com.example.sisay.shopsimulator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Locale;

public class Utils {
    public static final String WEB_PAY_FORMAT = "http://192.168.0.100/checkout/Home/Process/?ItemName=%s&ItemId=%s&UnitPrice=%.2f&Quantity=%d&Process=Express&SuccessUrl=&IPNUrl=&MerchantId=%s";

    public static String getMerchantCode(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("merchant_code", null);
        /*else if(merchantCodeText != null && !TextUtils.isEmpty(merchantCodeText.getText().toString())){
            return merchantCodeText.getText().toString();
        }*/
        //return Constants.YENEPAY_MERCHANT_CODE;
    }

    public static String getAmountString(double amount) {
        return String.format(Locale.ENGLISH, "%1$,.2f", amount);
    }

    public static String getReturnUrl() {
        return "com.yenepay.example.shopsimulator:/payment2redirect";
    }

    public static boolean getUseSandboxEnabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_use_sandbox", true);
    }
}
