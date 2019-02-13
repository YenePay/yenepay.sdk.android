package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import com.yenepaySDK.model.OrderedItem;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;

public interface OrderedItemClickCallback {
    void onProductClick(OrderedItem item);
}
