package com.yenepaySDK;

/**
 * Created by Sisay on 1/29/2017.
 */

public class Constants {
//    public static final String YENEPAY_MERCHANT_CODE = "ILS4PIDC";
    public static final String YENEPAY_MERCHANT_CODE = "0158";
    public static final String STORE_DOMAIN = "com.yenepay.sdk.android:/payment2redirect";
    public static final String YENEPAY_IPN_URL = STORE_DOMAIN;// + "Home/Completed";
    public static final String YENEPAY_SUCCESS_URL = STORE_DOMAIN;// + "Home/IPN";
    public static final String YENEPAY_CANCEL_URL = STORE_DOMAIN;// + "Home/Cancelled";
    public static final String CHECKOUT_SERVER_URL = "https://www.yenepay.com/checkout/";
//    public static final String CHECKOUT_SERVER_URL = "http://192.168.0.100/checkout/";
    public static final String SANDBOX_CHECKOUT_SERVER_URL = "https://test.yenepay.com/";
    public static final String DEFAULT_CURRENCY = "ETB";
}
