package examples.mob.yenepay.com.checkoutcounter.db.model;

import android.arch.persistence.room.Relation;
import android.text.TextUtils;

import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.model.OrderedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.PaymentResponseEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public class Order extends CustomerOrder {

    public Order(){
        super();
    }

    @Relation(parentColumn = "id", entityColumn = "orderId")
    public List<OrderedItemEntity> orderedItems;

    @Relation(parentColumn = "id", entityColumn = "orderId")
    public List<PaymentResponseEntity> paymentResponses;

    public PaymentResponseEntity getPaymentResponse(){
        if(paymentResponses != null && !paymentResponses.isEmpty()){
            return paymentResponses.get(0);
        }
        return null;
    }

    public OrderedItemEntity getItem(String itemId){
        if(orderedItems != null && !orderedItems.isEmpty()){
            for (OrderedItemEntity entry: orderedItems){
                if(TextUtils.equals(entry.getItemId(), itemId)){
                    return entry;
                }
            }
        }
        return null;
    }

    public int getItemsCount(){
        return orderedItems != null? orderedItems.size():0;
    }

    public void initTotals(){
        setItems(orderedItems);
        initOrder();
    }

}
