package com.yenepaySDK.errors;

public class InvalidPaymentException extends YenePayException {
    public InvalidPaymentException(String message) {
        super(message);
    }
}
