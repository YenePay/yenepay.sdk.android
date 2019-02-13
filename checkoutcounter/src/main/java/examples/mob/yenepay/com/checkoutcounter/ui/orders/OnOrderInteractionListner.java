package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;

public interface OnOrderInteractionListner {
    void onOrderSelected(CustomerOrder item);
    void onNewOrderClicked();
}
