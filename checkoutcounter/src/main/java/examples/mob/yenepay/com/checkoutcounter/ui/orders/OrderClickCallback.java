package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;

public interface OrderClickCallback {
    void onProductClick(CustomerOrder item);
}
