package com.yenepaySDK;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.yenepaySDK.handlers.PaymentHandlerActivity;
import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Sisay on 1/29/2017.
 */

public class PaymentOrderManager implements Serializable {

    public static final String PROCESS_CART = "Cart";
    public static final String PROCESS_EXPRESS = "Express";
    public static final int YENEPAY_CHECKOUT_REQ_CODE = 199;

    private String merchantCode;
    private String merchantOrderId;
    private String paymentProcess = PROCESS_EXPRESS;
    private double tax1;
    private double tax2;
    private double handlingFee;
    private double discount;
    private double shippingFee;
    private double itemsTotal;
    private String returnUrl;
    private String ipnUrl;
    private Map<String, OrderedItem> items = new HashMap<String, OrderedItem>();



    public PaymentOrderManager(String merchantCode, String merchantOrderId) {
        this.merchantCode = merchantCode;
        this.merchantOrderId = merchantOrderId;
    }
    public List<OrderedItem> getItems(){
        return new ArrayList<OrderedItem>(this.items.values());
    }
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public double getTax1() {
        return tax1;
    }

    public void setTax1(double tax1) {
        this.tax1 = tax1;
    }

    public double getTax2() {
        return tax2;
    }

    public void setTax2(double tax2) {
        this.tax2 = tax2;
    }

    public double getHandlingFee() {
        return handlingFee;
    }

    public void setHandlingFee(double handlingFee) {
        this.handlingFee = handlingFee;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public String getPaymentProcess() {
        return paymentProcess;
    }

    public void setPaymentProcess(String paymentProcess) {
        this.paymentProcess = paymentProcess;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getIpnUrl() {
        return ipnUrl;
    }

    public void setIpnUrl(String ipnUrl) {
        this.ipnUrl = ipnUrl;
    }

    public void addItem(OrderedItem item){
        if(TextUtils.isEmpty(item.getItemId())){
            item.setItemId(UUID.randomUUID().toString());
        }
        if(items.containsKey(item.getItemId())){
            items.get(item.getItemId()).setQuantity(items.get(item.getItemId()).getQuantity() + item.getQuantity());
        } else {
            items.put(item.getItemId(), item);
        }
        itemsTotal += item.getItemTotalPrice();
    }

    public void addItems(List<OrderedItem> items){
        for(OrderedItem item: items){
            addItem(item);
        }
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public Intent generatePaymentArguments(){
        Intent args = new Intent();
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ID, this.merchantCode);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ORDER_ID, this.merchantOrderId);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_PROCESS, this.paymentProcess);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_IPN_URL, getIpnUrl());
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CANCEL_URL, getReturnUrl());
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_SUCCESS_URL, getReturnUrl());
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_1, this.tax1);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_2, this.tax2);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_DISCOUNT, this.discount);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_HANDLING_FEE, this.handlingFee);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_SHIPPING_FEE, this.shippingFee);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS, new ArrayList<OrderedItem>(getItems()));

        args.setAction(YenepayCheckOutIntentAction.YENEPAY_INTENT_FILTER_ACTION_CHECKOUT);
        args.addCategory("android.intent.category.DEFAULT");
        args.setType("*/*");
        return args;
    }

    public void openPaymentBrowser(Context context){
        Payment payment = generatePayment();
        Intent intent = getPaymentRequestIntent(context, payment);
        ((Activity)context).startActivityForResult(intent, YENEPAY_CHECKOUT_REQ_CODE);
    }

    @NonNull
    public Intent getPaymentRequestIntent(Context context, Payment payment) {
        String checkoutPath = YenePayUriParser.generateWebPaymentStringUri(payment);
        Uri url = Uri.parse(Uri.decode(checkoutPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(url);
        return PaymentHandlerActivity.createStartForResultIntent(context, intent);
    }

    public void startCheckout(Context context){
        Intent intent = generatePaymentArguments();
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity)context).startActivityForResult(intent, YENEPAY_CHECKOUT_REQ_CODE);
            //Log.d(TAG, "Activity Resolved: ");
        } else {
            openPaymentBrowser((Activity)context);
        }
    }

    @NonNull
    public Payment generatePayment() {
        Payment payment = new Payment();
        payment.setProcess(this.paymentProcess);
        payment.setMerchantOrderId(this.merchantOrderId);
        payment.setMerchantId(getMerchantCode());
        payment.setSuccessUrl(getReturnUrl());
        payment.setCancelUrl(getReturnUrl());
        payment.setFailureUrl(getReturnUrl());
        payment.setIpnUrl(getIpnUrl());
        payment.setItems(getItems());
        return payment;
    }

    public void performPayment(){

    }

    public static PaymentResponse parseResponse(Intent intent){
        PaymentResponse response = null;
        if(intent.hasExtra(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE)){
            response = (PaymentResponse)intent.getSerializableExtra(PaymentHandlerActivity.KEY_PAYMENT_RESPONSE);
        } else if(intent.getData() != null) {
            response = parseResponse(intent.getData());
        } else {
            response = parseResponse(intent.getExtras());
        }
        return response;
    }

    public static PaymentResponse parseResponse(Bundle args){
        PaymentResponse response = null;
        if(args != null && args.containsKey(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ORDER_ID)){
            response = new PaymentResponse();
            response.setPaymentOrderId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ORDER_ID));
            response.setCustomerCode(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_CODE, ""));
            response.setCustomerEmail(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_EMAIL, ""));
            response.setCustomerName(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_NAME, ""));
            response.setInvoiceId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_INVOICE_ID,""));
            response.setMerchantCode(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ID));
            response.setMerchantOrderId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ORDER_ID));
            response.setStatusText(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_STATUS_TEXT));
            response.setStatus(args.getInt(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_STATUS, -1));
            //response.setItemsCount(args.getInt(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS, -1));
            response.setDiscount(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_DISCOUNT, 0));
            response.setGrandTotal(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ORDER_TOTAL, 0));
            response.setHandlingFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_HANDLING_FEE, 0));
            response.setItemsTotal(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS_TOTAL, 0));
            response.setShippingFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_SHIPPING_FEE, 0));
            response.setTax1(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_1, 0));
            response.setTax2(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_2, 0));
            response.setMerchantCommisionFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_COMMISION_FEE, 0));
        }
        return response;
    }

    public static PaymentResponse parseResponse(Uri data){
        return YenePayUriParser.parsePaymentResponse(data);
    }

    public static void setGlobalPendingIntents(PendingIntent completeIntent, PendingIntent cancelIntent){

    }
}
