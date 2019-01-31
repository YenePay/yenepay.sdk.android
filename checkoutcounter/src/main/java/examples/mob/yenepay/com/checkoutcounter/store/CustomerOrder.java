package examples.mob.yenepay.com.checkoutcounter.store;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.yenepaySDK.Constants;
import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.YenePayUriParser;
import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerOrder {
    private List<OrderedItem> items;
    private String storeCode;
    private String process;
    private double tax; // subtotal * tax %
    private double handlingFee;
    private double discount;
    private double shippingFee;
    private int totalQuantity;
    private double itemsTotal;
    private double subTotal; //itemsTotal - discount + handlingFee
    private double total; // subtotal + tax
    private double grandTotal; // total + shippingFee
    private PaymentResponse response;

    public CustomerOrder() {
        items = new ArrayList<>();
    }

    public CustomerOrder(String storeCode, String process, List<OrderedItem> items) {
        this.items = items;
        this.storeCode = storeCode;
        this.process = process;
        initOrder();
    }

    private void initOrder() {
        totalQuantity = 0; subTotal = 0; total = 0;
        itemsTotal = 0; grandTotal = 0; tax = 0;
        for (OrderedItem item: items){
            totalQuantity += item.getQuantity();
            itemsTotal += item.getItemTotalPrice();
        }
        subTotal = itemsTotal - discount + handlingFee;
        tax =  subTotal * 0.15;
        total = subTotal + tax;
        grandTotal = total + shippingFee;
    }

    public double getTax() {
        return tax;
    }

    public double getHandlingFee() {
        return handlingFee;
    }

    public double getDiscount() {
        return discount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public double getTotal() {
        return total;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public List<OrderedItem> getItems() {
        return items;
    }

    public void setItems(List<OrderedItem> items) {
        this.items = items;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }
    public String getQRPaymentUri(String host, int port){
        Uri.Builder builder = Uri.parse("https://yp2p.net").buildUpon();
        builder.appendQueryParameter("oid", UUID.randomUUID().toString());
        builder.appendQueryParameter("go", String.format("http:/%s:%d", host, port));
        builder.appendQueryParameter("p", UUID.randomUUID().toString());
        builder.appendQueryParameter("a", String.valueOf(grandTotal));
        String string = builder.build().toString();
        return Uri.encode(string);
    }
    public String getPaymentUri(String host, int port){
        Payment payment = getPaymentObj(host, port);
        String checkoutPath = YenePayUriParser.generateWebPaymentStringUri(payment);
        return Uri.encode(checkoutPath);
    }

    @NonNull
    private Payment getPaymentObj(String host, int port) {
        Payment payment = new Payment();
        payment.setProcess(process);
        payment.setMerchantOrderId(UUID.randomUUID().toString());
        payment.setMerchantId(storeCode);
        payment.setSuccessUrl(String.format("http://%s:%d", host, port));
        payment.setCancelUrl(String.format("http://%s:%d", host, port));
        payment.setFailureUrl(String.format("http://%s:%d", host, port));
        payment.setIpnUrl(Constants.YENEPAY_IPN_URL);
        if(tax > 0){
            payment.setTax1(tax);
        }
        if(discount > 0){
            payment.setDiscount(discount);
        }
        if(handlingFee > 0){
            payment.setHandlingFee(handlingFee);
        }
        if(shippingFee > 0){
            payment.setShippingFee(shippingFee);
        }
        payment.setItems(getItems());
        return payment;
    }

    public PaymentResponse getResponse() {
        return response;
    }

    public void setResponse(PaymentResponse response) {
        this.response = response;
    }

    public boolean isPending() {
        return true;
    }
}
