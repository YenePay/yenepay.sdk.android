package com.yenepaySDK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yenepaySDK.errors.InvalidPaymentException;
import com.yenepaySDK.handlers.PaymentHandlerActivity;
import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;
import com.yenepaySDK.model.YenePayConfiguration;

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
    private static final String TAG = "PaymentOrderManager";
    public static final String PROCESS_CART = "Cart";
    public static final String PROCESS_EXPRESS = "Express";
    public static final int YENEPAY_CHECKOUT_REQ_CODE = 199;


    private String merchantCode;
    private String merchantOrderId;
    private String paymentProcess = PROCESS_EXPRESS;
    private String currency = Constants.DEFAULT_CURRENCY;
    private double tax1;
    private double tax2;
    private double handlingFee;
    private double discount;
    private double deliveryFee;
    private double itemsTotal;
    private String returnUrl;
    private String ipnUrl;
    private boolean useSandboxEnabled;
    private boolean shoppingCartMode = true;
    private Map<String, OrderedItem> items = new HashMap<String, OrderedItem>();

    private PaymentOrderManager(){
        shoppingCartMode = true;
    }

    public PaymentOrderManager(String merchantCode, String merchantOrderId) {
        this();
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

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
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

    public boolean isUseSandboxEnabled() {
        return useSandboxEnabled;
    }

    public void setUseSandboxEnabled(boolean useSandboxEnabled) {
        this.useSandboxEnabled = useSandboxEnabled;
    }

    public void addItem(OrderedItem item) throws InvalidPaymentException {
        PaymentValidationResult validationResult = validateOrderedItem(item);
        if(!validationResult.isValid){
            throw new InvalidPaymentException(validationResult.toString());
        }
        if(shoppingCartMode) {
            if (TextUtils.isEmpty(item.getItemId())) {
                item.setItemId(UUID.randomUUID().toString());
            }
            if (items.containsKey(item.getItemId())) {
                items.get(item.getItemId()).setQuantity(items.get(item.getItemId()).getQuantity() + item.getQuantity());
            } else {
                items.put(item.getItemId(), item);
            }
        } else {
            items.put(UUID.randomUUID().toString(), item);
        }
        itemsTotal += item.getItemTotalPrice();
    }

    public void addItems(List<OrderedItem> items) throws InvalidPaymentException {
        for(OrderedItem item: items){
            addItem(item);
        }
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public Intent generatePaymentArguments(){
        Intent args = new Intent(YenepayCheckOutIntentAction.YENEPAY_INTENT_FILTER_ACTION_CHECKOUT);
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
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_DELIVERY_FEE, this.deliveryFee);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CURRENCY, this.currency);
        args.putExtra(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS, new ArrayList<OrderedItem>(getItems()));
        return args;
    }

    public void openPaymentBrowser(Context context){
        Payment payment = generatePayment();
        Intent intent = getPaymentRequestIntent(context, payment);
        ((Activity)context).startActivity(intent/*, PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE*/);
    }

    @NonNull
    public Intent getPaymentRequestIntent(Context context, Payment payment) {
        String checkoutPath = YenePayUriParser.generateWebPaymentStringUri(payment, isUseSandboxEnabled());
        Uri url = Uri.parse(Uri.decode(checkoutPath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(url);
        return PaymentHandlerActivity.createStartForResultIntent(context, intent);
    }

    public void startCheckout(Context context) throws InvalidPaymentException {
        PaymentValidationResult validationResult = validate();
        if(!validationResult.isValid){
            throw new InvalidPaymentException(validationResult.toString());
        }
        Intent intent = generatePaymentArguments();
        if (!isUseSandboxEnabled() && intent.resolveActivity(context.getPackageManager()) != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Activity) context).startActivityForResult(
                        PaymentHandlerActivity.createStartForResultIntent(context, intent),
                        PaymentOrderManager.YENEPAY_CHECKOUT_REQ_CODE);
            } else {
                YenePayConfiguration configuration = YenePayConfiguration.getDefaultInstance();
                Intent startIntent = PaymentHandlerActivity.createStartIntent(context, intent,
                        configuration.getGlobalCompletionIntent(),
                        configuration.getGlobalCancelIntent());
                ((Activity) context).startActivity(startIntent);
            }
            //Log.d(TAG, "Activity Resolved: ");
        } else {
            openPaymentBrowser(context);
        }
    }

    public PaymentValidationResult validate(){
        List<String> errors = new ArrayList<>();

        if(TextUtils.isEmpty(merchantCode)){
            errors.add("Invalid/Empty merchant code");
        }

        if(TextUtils.isEmpty(paymentProcess)){
            errors.add("Empty Payment process");
        }

        if(!TextUtils.equals(paymentProcess, PROCESS_CART) && !TextUtils.equals(paymentProcess, PROCESS_EXPRESS)){
            errors.add(String.format("Invalid Payment process only ( %s or %s)", PROCESS_CART, PROCESS_EXPRESS));
        }

        if(tax1 < 0){
            errors.add("Invalid tax1 value, must be 0 or positive");
        }

        if(tax2 < 0){
            errors.add("Invalid tax2 value, must be 0 or positive");
        }

        if(handlingFee < 0){
            errors.add("Invalid handlingFee value, must be 0 or positive");
        }

        if(deliveryFee < 0){
            errors.add("Invalid shippingFee value, must be 0 or positive");
        }
        if(discount < 0){
            errors.add("Invalid shippingFee value, must be 0 or positive");
        }

        if(TextUtils.isEmpty(returnUrl)){
            errors.add("Empty returnUrl value");
        }

        if(items.isEmpty()){
            errors.add("Empty items, add at least one item");
        }

        for (OrderedItem item: items.values()){
            PaymentValidationResult result = validateOrderedItem(item);
            if(!result.isValid){
                errors.addAll(result.errors);
            }
        }
        PaymentValidationResult validationResult = new PaymentValidationResult();
        validationResult.isValid = errors.isEmpty();
        validationResult.errors = errors;
        return validationResult;
    }

    public PaymentValidationResult validateOrderedItem(OrderedItem item) {
        List<String> itemErrors = new ArrayList<>();
        if(item == null){
            itemErrors.add("Item can not be null");
        } else {
            if(TextUtils.isEmpty(item.getItemName())){
                itemErrors.add("Item Name can not be empty");
            } /*else if(item.getItemName().length() > 350){
                itemErrors.add("Item Name can not exceed 350 characters");
            }*/

            if(item.getUnitPrice() <= 0){
                itemErrors.add("Item Unit Price must be greater than zero");
            }

            if(item.getQuantity() < 1){
                itemErrors.add("Item Quantity must be greater than or equal to 1");
            }
        }

        PaymentValidationResult result = new PaymentValidationResult();
        result.isValid = itemErrors.isEmpty();
        result.errors = itemErrors;
        return result;
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
        payment.setCurrency(getCurrency());
        return payment;
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
            try {
                response.setPaymentOrderId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ORDER_ID));
                response.setCustomerCode(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_CODE, ""));
                response.setCustomerEmail(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_EMAIL, ""));
                response.setCustomerName(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CUSTOMER_NAME, ""));
                response.setInvoiceId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_INVOICE_ID, ""));
                response.setMerchantCode(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ID));
                response.setMerchantOrderId(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_MERCHANT_ORDER_ID));
                response.setCurrency(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_CURRENCY));
                response.setStatusText(args.getString(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_STATUS_TEXT));
                response.setStatus(args.getInt(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_STATUS, -1));
                //response.setItemsCount(args.getInt(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS, -1));
                response.setDiscount(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_DISCOUNT, 0));
                response.setGrandTotal(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ORDER_TOTAL, 0));
                response.setHandlingFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_HANDLING_FEE, 0));
                response.setItemsTotal(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_ITEMS_TOTAL, 0));
                response.setDeliveryFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_DELIVERY_FEE, 0));
                response.setTax1(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_1, 0));
                response.setTax2(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_TAX_2, 0));
                response.setMerchantCommisionFee(args.getDouble(YenepayCheckOutIntentAction.YENEPAY_INTENT_EXTRA_COMMISION_FEE, 0));
            } catch (Exception e){
                Log.d(TAG, "Error occuered wile parsing payment response: ", e);
            }
        }
        return response;
    }

    public static PaymentResponse parseResponse(Uri data){
        return YenePayUriParser.parsePaymentResponse(data);
    }

    public boolean isShoppingCartMode() {
        return shoppingCartMode;
    }

    public void setShoppingCartMode(boolean shoppingCartMode) {
        this.shoppingCartMode = shoppingCartMode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static class PaymentValidationResult {
        public PaymentValidationResult() {
            isValid = false;
            errors = new ArrayList<>();
        }

        public boolean isValid;
        public List<String> errors;

        public void showResultToast(Context context){
            Toast.makeText(context, toString(), Toast.LENGTH_LONG);
        }

        @Override
        public String toString(){
            if(errors != null && !errors.isEmpty()){
                String joined = TextUtils.join("\n", errors);
                return "PaymentValidationErrors: \n" + joined;
            }
            return null;
        }
    }
}
