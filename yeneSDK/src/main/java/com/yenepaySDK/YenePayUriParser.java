package com.yenepaySDK;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
    public static final String YENEPAY_MERCHANT_ID = "MerchantId";
    public static final String YENEPAY_MERCHANT_ORDER_ID = "MerchantOrderId";
    public static final String YENEPAY_PROCESS = "Process";
    public static final String YENEPAY_PROCESS_CART = "Cart";
    public static final String YENEPAY_PROCESS_EXPRESS = "Express";
    public static final String YENEPAY_ITEM_ID = "ItemId";
    public static final String YENEPAY_ITEM_NAME = "ItemName";
    public static final String YENEPAY_UNIT_PRICE = "UnitPrice";
    public static final String YENEPAY_QUANTITY = "Quantity";
    public static final String YENEPAY_TAX_1 = "Tax1";
    public static final String YENEPAY_TAX_2 = "Tax2";
    public static final String YENEPAY_ITEMS = "Items";
    public static final String YENEPAY_DISCOUNT = "Discount";
    public static final String YENEPAY_HANDLING_FEE = "HandlingFee";
    public static final String YENEPAY_DELIVERY_FEE = "DeliveryFee";
    public static final String YENEPAY_CANCEL_URL = "CancelUrl";
    public static final String YENEPAY_SUCCESS_URL = "SuccessUrl";
    public static final String YENEPAY_FAILURE_URL = "FailureUrl";
    public static final String YENEPAY_IPN_URL = "IpnUrl";
    public static final String YENEPAY_ITEMS_FORMAT = YENEPAY_ITEMS + "[%1$d].%2$s" ;
    public static final String YENEPAY_BUYER_ID = "BuyerId";
    public static final String YENEPAY_SIGNATURE = "Signature";
    public static final String YENEPAY_STATUS = "Status";
    public static final String YENEPAY_TRANSACTION_ID = "TransactionId";
    public static final String YENEPAY_TRANSACTION_CODE = "TransactionCode";
    public static final String YENEPAY_TOTAL_AMOUNT = "TotalAmount";
    public static final String YENEPAY_ERROR_MSG = "ErrorMsg";
    public static final String YENEPAY_CURRENCY = "Currency";

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
        if(!TextUtils.isEmpty(order.getMerchantOrderId())){
            parameters.put(YENEPAY_MERCHANT_ORDER_ID, order.getMerchantOrderId());
        }
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
        if(order.getTax1() != null) {
            parameters.put(YENEPAY_TAX_1, String.valueOf(order.getTax1()));
        }
        if(order.getTax2() != null) {
            parameters.put(YENEPAY_TAX_2, String.valueOf(order.getTax2()));
        }
        if(order.getDiscount() != null) {
            parameters.put(YENEPAY_DISCOUNT, String.valueOf(order.getDiscount()));
        }
        if(order.getHandlingFee() != null) {
            parameters.put(YENEPAY_HANDLING_FEE, String.valueOf(order.getHandlingFee()));
        }
        if(order.getDeliveryFee() != null) {
            parameters.put(YENEPAY_DELIVERY_FEE, String.valueOf(order.getDeliveryFee()));
        }
        if(!TextUtils.isEmpty(order.getCurrency())) {
            parameters.put(YENEPAY_CURRENCY, order.getCurrency());
        } else {
            parameters.put(YENEPAY_CURRENCY, Constants.DEFAULT_CURRENCY);
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
        return generateWebPaymentStringUri(order, false);
    }
    public static String generateWebPaymentStringUri(Payment order, boolean useSandBox){
        StringBuilder url = new StringBuilder();
        Hashtable<String, String> parameters = YenePayUriParser.generateCheckOutParams(order);
        Set<String> keys = parameters.keySet();
        String serverUrl = useSandBox?
                Constants.SANDBOX_CHECKOUT_SERVER_URL :
                Constants.CHECKOUT_SERVER_URL;
        url.append(serverUrl);
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

    public static PaymentResponse parsePaymentResponse(Uri uri){
        PaymentResponse response = new PaymentResponse();
        response.setBuyerId(uri.getQueryParameter(YENEPAY_BUYER_ID));
        response.setSignature(uri.getQueryParameter(YENEPAY_SIGNATURE));
        response.setMerchantId(uri.getQueryParameter(YENEPAY_MERCHANT_ID));
        response.setMerchantOrderId(uri.getQueryParameter(YENEPAY_MERCHANT_ORDER_ID));
        String statusQueryParam = uri.getQueryParameter(YENEPAY_STATUS);
        if(!TextUtils.isEmpty(statusQueryParam)) {
            response.setStatusFromText(statusQueryParam);
        }
        response.setPaymentOrderId(uri.getQueryParameter(YENEPAY_TRANSACTION_ID));
        response.setOrderCode(uri.getQueryParameter(YENEPAY_TRANSACTION_CODE));
        response.setCurrency(uri.getQueryParameter(YENEPAY_CURRENCY));
        String amt = uri.getQueryParameter(YENEPAY_TOTAL_AMOUNT);
        if(!TextUtils.isEmpty(amt)) {
            amt = amt.replace(",", "");
            response.setGrandTotal(Double.parseDouble(amt));
        }
        return response;
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
