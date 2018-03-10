package com.yenepaySDK;

import android.content.ClipData;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by Sisay Getnet on 2/13/2018.
 */

public class YenePayUriParser {
    private static final String TAG = "YenePayUriParser";
    private static final String YENEPAY_DOMAIN = "yenepay.com";
    private static final String YENEPAY_CHECKOUT_PATH = "Process";
    private static final String YENEPAY_MERCHANT_ID = "MerchantId";
    private static final String YENEPAY_PROCESS = "Process";
    private static final String YENEPAY_PROCESS_CART = "Cart";
    private static final String YENEPAY_PROCESS_EXPRESS = "Express";
    private static final String YENEPAY_ITEM_ID = "ItemId";
    private static final String YENEPAY_ITEM_NAME = "ItemName";
    private static final String YENEPAY_UNIT_PRICE = "UnitPrice";
    private static final String YENEPAY_QUANTITY = "Quantity";
    private static final String YENEPAY_TAX_1 = "Tax1";
    private static final String YENEPAY_TAX_2 = "Tax2";
    private static final String YENEPAY_ITEMS = "Items";
    private static final String YENEPAY_DISCOUNT = "Discount";
    private static final String YENEPAY_CANCEL_URL = "CancelUrl";
    private static final String YENEPAY_SUCCESS_URL = "SuccessUrl";
    private static final String YENEPAY_FAILURE_URL = "FailureUrl";
    private static final String YENEPAY_IPN_URL = "IpnUrl";
    private static final String YENEPAY_ITEMS_FORMAT = YENEPAY_ITEMS + "[%1$d].%2$s" ;

    private boolean isMultipleItem;
    private int itemsCount;
    private boolean isValidCheckout;

    private Uri uri;
    Set<String> queryParams;

    public YenePayUriParser(){}


    public boolean isMultipleItem() {
        return isMultipleItem;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public boolean isValidCheckout() {
        return isValidCheckout;
    }
    public Uri getUri() {
        return uri;
    }

    public static YenePayUriParser getParser(String uri, boolean checkHost) throws MalformedURLException {
        return new YenePayUriParser();
    }

    public static Hashtable<String, String> generateCheckOutParams(Payment order){
        Hashtable<String, String> parameters = new Hashtable<String, String>();
        parameters.put(YENEPAY_MERCHANT_ID, order.getMerchantId());
        parameters.put(YENEPAY_PROCESS, order.getProcess());
        if(order.getIpnUrl() != null) {
            parameters.put(YENEPAY_IPN_URL, order.getIpnUrl());
        }
        if(order.getSuccessUrl() != null) {
            parameters.put(YENEPAY_SUCCESS_URL, order.getSuccessUrl());
        }
        if(order.getCancelUrl() != null) {
            parameters.put(YENEPAY_CANCEL_URL, order.getCancelUrl());
        }
        if(order.getFailureUrl() != null) {
            parameters.put(YENEPAY_FAILURE_URL, order.getFailureUrl());
        }
        if(order.getProcess().equals(YENEPAY_PROCESS_EXPRESS) && order.getItems().size() == 1){
            OrderedItem singleItem = order.getItems().get(0);
            if(singleItem.getItemId() != null) {
                parameters.put(YENEPAY_ITEM_ID, singleItem.getItemId());
            }
            parameters.put(YENEPAY_ITEM_NAME, singleItem.getItemName());
            parameters.put(YENEPAY_QUANTITY, String.valueOf(singleItem.getQuantity()));
            parameters.put(YENEPAY_UNIT_PRICE, String.valueOf(singleItem.getUnitPrice()));
        } else if(order.getProcess().equals(YENEPAY_PROCESS_CART) && order.getItems().size() >= 1) {
            for(int i = 0; i < order.getItems().size(); i++){
                OrderedItem item = order.getItems().get(i);
                if(item.getItemId() != null) {
                    parameters.put(String.format(YENEPAY_ITEMS_FORMAT, i, YENEPAY_ITEM_ID), item.getItemId());
                }
                parameters.put(String.format(YENEPAY_ITEMS_FORMAT, i, YENEPAY_ITEM_NAME), item.getItemName());
                parameters.put(String.format(YENEPAY_ITEMS_FORMAT, i, YENEPAY_QUANTITY), String.valueOf(item.getQuantity()));
                parameters.put(String.format(YENEPAY_ITEMS_FORMAT, i, YENEPAY_UNIT_PRICE), String.valueOf(item.getUnitPrice()));
            }
        }
        Log.d(TAG, "generateCheckOutParams: " + parameters.toString());
        return parameters;
    }

    public static String generateWebPaymentStringUri(Payment order){
        StringBuilder url = new StringBuilder();
        Hashtable<String, String> parameters = YenePayUriParser.generateCheckOutParams(order);
        Set<String> keys = parameters.keySet();
        url.append(Constants.CHECKOUT_SERVER_URL);
        url.append("Home/" + YENEPAY_CHECKOUT_PATH + "/?");
        List<String> keyValues = new ArrayList<String>();
        for (String key: keys){
            keyValues.add(key + "=" + parameters.get(key));
        }
        String values = TextUtils.join("&", keyValues);
        url.append(values);
        String result = null;
        try{
            result = URLEncoder.encode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "generateWebPaymentStringUri: " + result);
        return result;
    }



    @Override
    public String toString() {
        return "{" + "/n" +
                "getHost : " + uri.getHost() + "/n" +
                "getAuthority : " + uri.getAuthority() + "/n" +
                "getFragment : " + uri.getFragment() + "/n" +
                "getPath : " + uri.getPath() + "/n" +
                "getLastPathSegment : " + uri.getLastPathSegment() + "/n" +
                "getLastPathSegment : " + uri.getQueryParameterNames() + "/n" +
                "}"
                ;
    }
}
