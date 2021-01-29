package com.yenepaySDK;

import android.support.annotation.NonNull;

import com.yenepaySDK.errors.InvalidPaymentException;
import com.yenepaySDK.model.OrderedItem;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class PaymentOrderManagerTest {

    private PaymentOrderManager testManager;
    @Before
    public void setUp(){
        testManager = getPaymentOrderManagerWithoutItems("0089");
    }

    public PaymentOrderManager getTestOrderManager(){
        PaymentOrderManager manager = getPaymentOrderManagerWithoutItems("0089");
        try {
            manager.addItem(getTestOrderItem());
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @NonNull
    private PaymentOrderManager getPaymentOrderManagerWithoutItems(String merchantCode) {
        PaymentOrderManager manager = new PaymentOrderManager(merchantCode, UUID.randomUUID().toString());
        manager.setPaymentProcess(PaymentOrderManager.PROCESS_CART);
        manager.setDiscount(5);
        manager.setHandlingFee(50);
        manager.setIpnUrl("http://test.com/ipn");
        manager.setReturnUrl("http://test.com/return");
        manager.setDeliveryFee(20);
        manager.setTax1(12);
        manager.setTax2(45);
        manager.setShoppingCartMode(true);
        return manager;
    }

    public OrderedItem getTestOrderItem(){
        return new OrderedItem(UUID.randomUUID().toString(), "Test Item", 1, 15);
    }

    @Test
    public void validate() {
    }

    @Test
    public void testValidationOrderedItem(){
        OrderedItem testOrderItem = getTestOrderItem();
        PaymentOrderManager.PaymentValidationResult result = testManager.validateOrderedItem(testOrderItem);
        assertTrue(result.isValid);
    }

    @Test
    public void testValidationEmptyItemName(){
        OrderedItem testOrderItem = getTestOrderItem();
        testOrderItem.setItemName("");
        PaymentOrderManager.PaymentValidationResult result = testManager.validateOrderedItem(testOrderItem);
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationItemQuantity(){
        OrderedItem testOrderItem = getTestOrderItem();
        testOrderItem.setQuantity(0);
        PaymentOrderManager.PaymentValidationResult result = testManager.validateOrderedItem(testOrderItem);
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationItemUnitPrice(){
        OrderedItem testOrderItem = getTestOrderItem();
        testOrderItem.setUnitPrice(0);
        PaymentOrderManager.PaymentValidationResult result = testManager.validateOrderedItem(testOrderItem);
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationPayment(){
        PaymentOrderManager manager = getTestOrderManager();
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertTrue(result.isValid);
    }

    @Test
    public void testValidationMerchantCode(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setMerchantCode(null);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationProcessEmpty(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setPaymentProcess(null);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationProcessInvalid(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setPaymentProcess("InvalidProcess");
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationTax1(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setTax1(-10);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationTax2(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setTax2(-10);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationHandlingFee(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setHandlingFee(-10);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationShippingFee(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setDeliveryFee(-10);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationDiscount(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setDiscount(-10);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationReturnUrl(){
        PaymentOrderManager manager = getTestOrderManager();
        manager.setReturnUrl(null);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testValidationItems(){
        PaymentOrderManager manager = getPaymentOrderManagerWithoutItems(null);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testShoppingCartModeEnabled(){
        PaymentOrderManager manager = getPaymentOrderManagerWithoutItems(null);
        manager.setShoppingCartMode(true);
        OrderedItem first = getTestOrderItem();
        OrderedItem second = getTestOrderItem();
        String id = UUID.randomUUID().toString();
        first.setItemId(id);
        second.setItemId(id);
        int totalQty = first.getQuantity() + second.getQuantity();
        double totalPrice = first.getItemTotalPrice() + second.getItemTotalPrice();

        try {
            manager.addItem(first);
            manager.addItem(second);
        } catch (InvalidPaymentException e) {
            fail("Adding item failed");
        }

        assertEquals(1, manager.getItems().size());
        assertEquals(totalQty, manager.getItems().get(0).getQuantity());
        assertEquals(totalPrice, manager.getItems().get(0).getItemTotalPrice(), 0);
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }

    @Test
    public void testShoppingCartModeDisabled(){
        PaymentOrderManager manager = getPaymentOrderManagerWithoutItems(null);
        manager.setShoppingCartMode(false);
        OrderedItem first = getTestOrderItem();
        OrderedItem second = getTestOrderItem();
        String id = UUID.randomUUID().toString();
        first.setItemId(id);
        second.setItemId(id);

        try {
            manager.addItem(first);
            manager.addItem(second);
        } catch (InvalidPaymentException e) {
            fail("Adding item failed");
        }

        assertEquals(2, manager.getItems().size());
        PaymentOrderManager.PaymentValidationResult result = manager.validate();
        assertFalse(result.isValid);
    }
}