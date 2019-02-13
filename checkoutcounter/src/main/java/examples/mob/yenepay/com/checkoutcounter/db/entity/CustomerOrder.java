package examples.mob.yenepay.com.checkoutcounter.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yenepaySDK.Constants;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.YenePayUriParser;
import com.yenepaySDK.model.OrderedItem;
import com.yenepaySDK.model.Payment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.StoreApp;

@Entity(tableName = "orders")
public class CustomerOrder {
    private static final String TAG = "CustomerOrder";
    public static final int STATUS_NEW = 0;
    public static final int STATUS_PROCESSING = 1;
    public static final int STATUS_PAID = 2;
    public static final int STATUS_CANCELED = 3;
    public static final int STATUS_EXPIRED = 4;
    @PrimaryKey
    @NonNull
    private String id;
    @Ignore
    private List<OrderedItemEntity> items;
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
    private int status;
    private long lastStatusDate;
    private long createdDate;
    private Long expireDate;
    private Long paidDate;

    public long getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(long lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Long expireDate) {
        this.expireDate = expireDate;
    }

    public Long getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Long paidDate) {
        this.paidDate = paidDate;
    }

    public CustomerOrder() {
        items = new ArrayList<>();
//        id = UUID.randomUUID().toString();
    }
    @Ignore
    public CustomerOrder(String storeCode, String process, List<OrderedItem> items) {
        this();
        this.items = convertToEntity(items);
        this.storeCode = storeCode;
        this.process = process;
        initOrder();
    }

    public List<OrderedItemEntity> convertToEntity(List<OrderedItem> items){
        List<OrderedItemEntity> list = new ArrayList<>();
        for (OrderedItem item: items){
            list.add(convertToEntity(item));
        }
        return list;
    }

    public OrderedItemEntity convertToEntity(OrderedItem item){
        return new OrderedItemEntity(item);
    }

    public OrderedItemEntity convertToEntity(StoreItem item){
        return new OrderedItemEntity(new OrderedItem(item.id, item.content, 1, item.price));
    }

    protected void initOrder() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setHandlingFee(double handlingFee) {
        this.handlingFee = handlingFee;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setItemsTotal(double itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<OrderedItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderedItemEntity> items) {
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

    @Ignore
    public String getQRPaymentUri(String host, int port, String ssid, String passPhrase){
        Uri.Builder builder = Uri.parse("https://yp2p.net").buildUpon();
        builder.appendQueryParameter("oid", getId());
        builder.appendQueryParameter("go", String.format("http://%s:%d", host, port));
        builder.appendQueryParameter("sid", ssid);
        builder.appendQueryParameter("p", passPhrase);
        builder.appendQueryParameter("a", String.valueOf(grandTotal));
        String string = builder.build().toString();
        Log.d(TAG, "getQRPaymentUri: QR URI Generated - " + string);
        return Uri.encode(string);
    }
    @Ignore
    public String getPaymentUri(String host, int port){
        Payment payment = getPaymentObj(host, port);
        String checkoutPath = YenePayUriParser.generateWebPaymentStringUri(payment);
        return Uri.encode(checkoutPath);
    }

    @NonNull
    @Ignore
    private Payment getPaymentObj(String host, int port) {
        host = StoreApp.getStoreTerminal().host;
        Payment payment = new Payment();
        payment.setProcess(process);
        payment.setMerchantOrderId(getId());
        payment.setMerchantId(storeCode);
        payment.setSuccessUrl(String.format("http://%s:%d?q=r", host, port));
        payment.setCancelUrl(String.format("http://%s:%d?q=r", host, port));
        payment.setFailureUrl(String.format("http://%s:%d?q=r", host, port));
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
        ArrayList<OrderedItem> items = new ArrayList<>();
        items.addAll(getItems());
        payment.setItems(items);
        return payment;
    }

    private String getPaymentStatusString(){
        String result = "Pending";
        switch (status){
            case STATUS_CANCELED:
                result = "Canceled";
                break;
            case STATUS_EXPIRED:
                result = "Expired";
                break;
            case STATUS_PAID:
                result = "Processing";
                break;
            case STATUS_PROCESSING:
                result = "Processing";
                break;
        }
        return result;
    }

    private int getPaymentStatusColor(){
        int result = R.color.yenepay_blue;
        switch (status){
            case STATUS_CANCELED:
            case STATUS_EXPIRED:
                result = R.color.colorAccent;
                break;
            case STATUS_PAID:
                result = R.color.yenepay_green;
                break;
            case STATUS_PROCESSING:
                result = R.color.yenepay_blue;
                break;
        }
        return result;
    }

    public boolean setAppropriateStatus(PaymentResponse response){
        if(response.isPaymentCompleted()){
            status = STATUS_PAID;
            setPaidDate(new Date().getTime());
        } else if(response.isCanceled()){
            status = STATUS_CANCELED;
        } else if(response.isExpiered()){
            status = STATUS_EXPIRED;
            setExpireDate(new Date().getTime());
        } else {
            return false;
        }
        setLastStatusDate(new Date().getTime());
        return true;
    }

    public boolean isPending() {
        return status == STATUS_NEW || status == STATUS_PROCESSING;
    }
    public boolean isClosed() {
        return status == STATUS_PAID || status == STATUS_CANCELED || status == STATUS_EXPIRED;
    }

    public boolean isNew() {
        return status == STATUS_NEW;
    }
    public boolean isNotNew() {
        return !isNew();
    }
    public boolean isProcessing() {
        return status == STATUS_PROCESSING;
    }
    public boolean isPaid() {
        return status == STATUS_PAID;
    }
    public boolean isCanceled() {
        return status == STATUS_CANCELED;
    }
    public boolean isExpired() {
        return status == STATUS_EXPIRED;
    }

    public OrderedItemEntity[] getItemsEntityArray() {
        if(items != null && !items.isEmpty()){
            OrderedItemEntity[] array = new OrderedItemEntity[items.size()];
            for (int i = 0; i < items.size(); i++){
                OrderedItemEntity entity = new OrderedItemEntity(items.get(i));
                entity.setOrderId(getId());
                array[i] = entity;
            }
            return array;
        }
        return null;
    }


}
