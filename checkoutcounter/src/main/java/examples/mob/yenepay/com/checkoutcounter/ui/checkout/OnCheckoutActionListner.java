package examples.mob.yenepay.com.checkoutcounter.ui.checkout;

import com.yenepaySDK.model.OrderedItem;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.OrderedItemClickCallback;

public interface OnCheckoutActionListner extends OrderedItemClickCallback {
    void onAddNewItem();
    void onNewOrder();
    void onCancelOrder(CustomerOrder order);
}
