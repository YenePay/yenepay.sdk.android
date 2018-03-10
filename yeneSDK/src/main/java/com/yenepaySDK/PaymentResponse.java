package com.yenepaySDK;

import android.text.TextUtils;

/**
 * Created by Sisay on 2/5/2017.
 */

public class PaymentResponse {
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
    private double shippingFee;
    private double handlingFee;
    private double tax1;
    private double tax2;
    private double grandTotal;
    private double merchantCommisionFee;
    private String invoiceId;
    private String invoiceUrl;
    private int itemsCount;
    private String customerCode;

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

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
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

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "customerEmail='" + customerEmail + '\'' +
                ", paymentOrderId='" + paymentOrderId + '\'' +
                ", orderCode='" + orderCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", merchantOrderId='" + merchantOrderId + '\'' +
                ", merchantCode='" + merchantCode + '\'' +
                ", status=" + status +
                ", statusText='" + statusText + '\'' +
                ", statusDescription='" + statusDescription + '\'' +
                ", itemsTotal=" + itemsTotal +
                ", discount=" + discount +
                ", shippingFee=" + shippingFee +
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
}
