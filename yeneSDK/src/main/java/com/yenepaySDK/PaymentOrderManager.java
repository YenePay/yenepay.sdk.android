package com.yenepaySDK;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.io.Serializable;
import java.net.URLDecoder;
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

    private String merchantCode;
    private String merchantOrderId;
    private String paymentProcess = PROCESS_EXPRESS;
    private double itemsTotal;
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

    public double getItemsTotal() {
        return itemsTotal;
    }

    public Intent generatePaymentArguments(){
        Intent args = new Intent();
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ID, this.merchantCode);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ORDER_ID, this.merchantOrderId);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_PROCESS, this.paymentProcess);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_IPN_URL, Constants.YENEPAY_IPN_URL);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CANCEL_URL, Constants.YENEPAY_CANCEL_URL);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_SUCCESS_URL, Constants.YENEPAY_SUCCESS_URL);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS, new ArrayList<OrderedItem>(getItems()));
        return args;
    }

    public void openPaymentBrowser(Activity context){
        Payment payment = new Payment();
        payment.setProcess(this.paymentProcess);
        payment.setMerchantOrderId(this.merchantOrderId);
        payment.setMerchantId(getMerchantCode());
        payment.setCancelUrl(Constants.YENEPAY_CANCEL_URL);
        payment.setFailureUrl(Constants.YENEPAY_CANCEL_URL);
        payment.setIpnUrl(Constants.YENEPAY_IPN_URL);
        payment.setItems(getItems());
        String checkoutPath = YenePayUriParser.generateWebPaymentStringUri(payment);
        Uri url = Uri.parse(URLDecoder.decode(checkoutPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(url);
        context.startActivityForResult(intent, 100);
    }

    public void performPayment(){

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
}
