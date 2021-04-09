package com.yenepaySDK;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sisay on 2/5/2017.
 */

public class PaymentResponse implements Serializable {
    public static final int CANCELED = 3;
    public static final int ERROR = 4;
    public static final int UNKOWN = 7;
    public static final int VERIFYING = 11;
    public static final int PROCESSING = 2;
    public static final int EXPIERED = 12;
    public static final int WAITING = 8;
    public static final int NEW = 1;
    public static final int DELIVERED = 10;
    public static final int PAID = 9;
    public static final int COMPLETED = 5;
    public static final int DISPUTED = 6;
    private String paymentOrderId;
    private String orderCode;
    private String customerName;
    private String merchantOrderId;
    private String customerEmail;
    private String merchantCode;
    private int status;
    private String statusText;
    private String statusDescription;
    private double itemsTotal;
    private double discount;
    private double deliveryFee;
    private double handlingFee;
    private double tax1;
    private double tax2;
    private double grandTotal;
    private double merchantCommisionFee;
    private String invoiceId;
    private String invoiceUrl;
    private int itemsCount;
    private String customerCode;
    private String buyerId;
    private String merchantId;
    private double transactionFee;
    private String signature;
    private String currency;
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getHandlingFee() {
        return handlingFee;
    }

    public void setHandlingFee(double handlingFee) {
        this.handlingFee = handlingFee;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceUrl() {
        return invoiceUrl;
    }

    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public void setItemsTotal(double itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public double getMerchantCommisionFee() {
        return merchantCommisionFee;
    }

    public void setMerchantCommisionFee(double merchantCommisionFee) {
        this.merchantCommisionFee = merchantCommisionFee;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getPaymentOrderId() {
        return paymentOrderId;
    }

    public void setPaymentOrderId(String paymentOrderId) {
        this.paymentOrderId = paymentOrderId;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
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

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public boolean isPending(){
        boolean result = false;
        switch(this.status){
            case COMPLETED:
            case PAID:
            case DELIVERED:
                break;
            case NEW:
            case WAITING:
            case PROCESSING:
                result = true;
                break;
            case VERIFYING:
                result = true;
                break;
            case CANCELED:
            case EXPIERED:
                break;

        }
        return result;
    }

    public boolean isVerifying(){
        return !TextUtils.isEmpty(this.statusText) && this.status == VERIFYING;
    }
    public boolean isExpiered(){
        return !TextUtils.isEmpty(this.statusText) && this.status == EXPIERED;
    }
    public boolean isCanceled(){
        return !TextUtils.isEmpty(this.statusText) && this.status == CANCELED;
    }
    public boolean isPaymentCompleted(){
        boolean result = false;
        switch(this.status){
            case COMPLETED:
            case PAID:
            case DELIVERED:
                result = true;
                break;

        }
        return result;
    }

    public boolean isDelivered(){
        return !TextUtils.isEmpty(this.statusText) && this.status == DELIVERED;
    }

    public boolean hasOpenDipute(){
        return !TextUtils.isEmpty(this.statusText) && this.status == DISPUTED;
    }

    public String getVerificationString(){
        List<String> keyValues = new ArrayList<>();
        keyValues.add("TotalAmount=" + String.format(Locale.US, "%1$,.2f",this.grandTotal));
        keyValues.add("BuyerId=" + buyerId);
//        keyValues.add("BuyerId=" + "7354");
        keyValues.add("MerchantOrderId=" + this.merchantOrderId);
        keyValues.add("MerchantCode=" + this.merchantCode);
        keyValues.add("MerchantId=" + this.merchantId);
        keyValues.add("TransactionCode=" + this.orderCode);
        keyValues.add("TransactionId=" + this.paymentOrderId);
        keyValues.add("Status=" + this.status);
        keyValues.add("Currency=" + "ETB");

        return TextUtils.join("&", keyValues);
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setStatusFromText(String data){
        if(TextUtils.isDigitsOnly(data)){
            setStatus(Integer.parseInt(data));
            setStatusTextFromInt(status);
            return;
        }
        status = UNKOWN;
        switch (data.toLowerCase()){
            case "paid":
                status = PAID;
                break;
            case "completed":
                status = COMPLETED;
                break;
            case "canceled":
                status = CANCELED;
                break;
            case "delivered":
                status = DELIVERED;
                break;
            case "disputed":
                status = DISPUTED;
                break;
            case "error":
                status = ERROR;
                break;
            case "expired":
                status = EXPIERED;
                break;
            case "new":
                status = NEW;
                break;
            case "waiting":
                status = WAITING;
                break;
            case "verifying":
                status = VERIFYING;
                break;
            case "processing":
                status = PROCESSING;
                break;
        }
        setStatusTextFromInt(status);
    }

    private void setStatusTextFromInt(int status){
        String genStatusText = "Unknown";
        switch(this.status){
            case COMPLETED:
            case PAID:
            case DELIVERED:
                genStatusText = "Completed";
                break;
            case NEW:
            case WAITING:
            case PROCESSING:
                genStatusText = "Pending";
                break;
            case VERIFYING:
                genStatusText = "Processing";
                break;
            case CANCELED:
                genStatusText = "Canceled";
                break;
            case EXPIERED:
                genStatusText = "Expired";
                break;

        }
        setStatusText(genStatusText);
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "customerEmail='" + customerEmail + '\'' +
                ", paymentOrderId='" + paymentOrderId + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", merchantOrderId='" + merchantOrderId + '\'' +
                ", merchantId='" + merchantId + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", merchantCode='" + merchantCode + '\'' +
                ", status=" + status +
                ", statusText='" + statusText + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", itemsTotal=" + itemsTotal +
                ", discount=" + discount +
                ", shippingFee=" + deliveryFee +
                ", handlingFee=" + handlingFee +
                ", tax1=" + tax1 +
                ", tax2=" + tax2 +
                ", grandTotal=" + grandTotal +
                ", merchantCommisionFee=" + merchantCommisionFee +
                ", invoiceId='" + invoiceId + '\'' +
                ", invoiceUrl='" + invoiceUrl + '\'' +
                ", itemsCount=" + itemsCount + '\'' +
                ", customerCode=" + customerCode +
                '}';
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
