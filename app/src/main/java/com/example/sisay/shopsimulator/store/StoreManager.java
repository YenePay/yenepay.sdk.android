package com.example.sisay.shopsimulator.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.sisay.shopsimulator.CartActivity;
import com.example.sisay.shopsimulator.R;
import com.example.sisay.shopsimulator.ShopBaseActivity;
import com.example.sisay.shopsimulator.Utils;
import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.YenepayCheckOutIntentAction;
import com.yenepaySDK.errors.InvalidPaymentException;
import com.yenepaySDK.model.OrderedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class StoreManager {
    private static final String TAG = "StoreManager";
    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    public static final List<OrderedItem> ORDERS = new ArrayList<>();

    private static double cartTotal;
    private static int cartItemsCount;

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, OrderedItem> ORDERS_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        /*for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }*/
        addItem(new DummyItem("1", "Fikir esike Mekabir", "Book - Fikir esike Mekabir by Haddis Alemayehu", 250, R.drawable.item1, R.drawable.item_large_1));
        addItem(new DummyItem("2", "Women shoes", "Quality women shoes - black, size 36", 400, R.drawable.item2, R.drawable.item_large_2));
        addItem(new DummyItem("3", "Nike Sniker", "Nike Sniker - white, size 42", 1500, R.drawable.item3, R.drawable.item_large_3));
        addItem(new DummyItem("4", "Port wrist watch", "Original Port wrist watch black", 700, R.drawable.item4, R.drawable.item_large_4));
        addItem(new DummyItem("5", "Electric stove", "Electric cooking stove 220 watt", 190.50, R.drawable.item5, R.drawable.item_large_5));
        addItem(new DummyItem("6", "Women hand bag", "Leather women hand bag grey color", 250, R.drawable.item6, R.drawable.item_large_6));
    }

    public static double getCartTotal() {
        return cartTotal;
    }

    public static int getCartItemsCount() {
        return cartItemsCount;
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void addToCart(String itemId, int qty){
        if(ITEM_MAP.containsKey(itemId)){
            DummyItem item = ITEM_MAP.get(itemId);
            addToCart(item, qty);
        }
    }

    public static void addToCart(DummyItem item, int qty){
        if(ORDERS_MAP.containsKey(item.id)){
            OrderedItem orderedItem = ORDERS_MAP.get(item.id);
            orderedItem.setQuantity(orderedItem.getQuantity() + qty);
        } else {
            OrderedItem orderedItem = item.convertToOrderedItem(qty);
            ORDERS.add(orderedItem);
            ORDERS_MAP.put(item.id, orderedItem);
        }
        calculateTotals();
    }

    public static void removeFromCart(String id, int qty){
        if(ORDERS_MAP.containsKey(id)){
            OrderedItem orderedItem = ORDERS_MAP.get(id);
            int newQty = orderedItem.getQuantity() - qty;
            if(newQty > 0) {
                orderedItem.setQuantity(newQty);
            } else {
                ORDERS_MAP.remove(id);
                ORDERS.remove(orderedItem);
            }
            calculateTotals();
        }
    }

    public static void clearCart(){
        ORDERS_MAP.clear();
        ORDERS.clear();
        cartTotal = 0;
        cartItemsCount = 0;
    }

    private static void calculateTotals(){
        double totalAmt = 0;
        int totalQty = 0;
        for (String id: ORDERS_MAP.keySet()){
            OrderedItem item = ORDERS_MAP.get(id);
            totalQty += item.getQuantity();
            totalAmt += item.getItemTotalPrice();
        }
        cartTotal = totalAmt;
        cartItemsCount = totalQty;
    }

    public static void  checkoutWithBrowser(Context context){
//        String url = String.format(Utils.WEB_PAY_FORMAT, item.content, item.id, item.price, 1, Utils.getMerchantCode(context));
////                    Uri uri = Uri.parse(URLDecoder.decode(successReturnUrl));
//        Uri uri = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        context.startActivity(intent);
        PaymentOrderManager paymentMgr = generatePaymentOrderManager(context);
        try {
            paymentMgr.addItems(ORDERS);
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
        }
        paymentMgr.openPaymentBrowser(context);
    }

    public static void  checkoutWithBrowser(Context context, DummyItem item){
//        String url = String.format(Utils.WEB_PAY_FORMAT, item.content, item.id, item.price, 1, Utils.getMerchantCode(context));
////                    Uri uri = Uri.parse(URLDecoder.decode(successReturnUrl));
//        Uri uri = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(uri);
//        context.startActivity(intent);

        PaymentOrderManager paymentMgr = generatePaymentOrderManager(context);
        try {
            paymentMgr.addItem(item.convertToOrderedItem(1));
            paymentMgr.openPaymentBrowser(context);
        } catch (InvalidPaymentException e) {
            Log.e(TAG, "checkoutWithBrowser: ", e);
            showErrorDialog(context, e.getMessage());
        }

    }
    public static void checkoutToApp(Context context){
        PaymentOrderManager paymentMgr = generatePaymentOrderManager(context);
        try {
            paymentMgr.addItems(ORDERS);
            paymentMgr.startCheckout(context);
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            showErrorDialog(context, e.getMessage());
        }
    }

    public static void checkoutToApp(Context context, DummyItem item){
        PaymentOrderManager paymentMgr = generatePaymentOrderManager(context);
        try {
            paymentMgr.addItem(item.convertToOrderedItem(1));
            paymentMgr.startCheckout(context);
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
            showErrorDialog(context, e.getMessage());
        }
    }

    private static void showErrorDialog(Context context,String message){
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .show();
    }

    @NonNull
    public static PaymentOrderManager generatePaymentOrderManager(Context context) {
        PaymentOrderManager paymentMgr = new PaymentOrderManager(
                Utils.getMerchantCode(context),
                UUID.randomUUID().toString());
        paymentMgr.setPaymentProcess(PaymentOrderManager.PROCESS_CART);
        paymentMgr.setReturnUrl(Utils.getReturnUrl());
        paymentMgr.setUseSandboxEnabled(Utils.getUseSandboxEnabled(context));
        return paymentMgr;
    }

    public static void checkOutItem(Context context, Intent intent){
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((ShopBaseActivity)context).startActivityForResult(intent, ShopBaseActivity.CHECKOUT_REQ_CODE);
            //Log.d(TAG, "Activity Resolved: ");
        }
    }

    /*private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;
        public final double price;
        public final int imageResId;
        public final int largeImageResId;

        public DummyItem(String id, String content, String details, double price, int imageResId, int largeImageResId) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.price = price;
            this.imageResId = imageResId;
            this.largeImageResId = largeImageResId;
        }

        public OrderedItem convertToOrderedItem(int quantity){
            if(quantity < 1){
                quantity = 1;
            }
            OrderedItem item = new OrderedItem(id, content, quantity, price);
            return item;
        }

        @Override
        public String toString() {
            return "DummyItem{" +
                    "content='" + content + '\'' +
                    ", price=" + price +
                    '}';
        }
    }


}
