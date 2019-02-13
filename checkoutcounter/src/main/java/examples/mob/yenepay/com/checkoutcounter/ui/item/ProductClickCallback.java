package examples.mob.yenepay.com.checkoutcounter.ui.item;

import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public interface ProductClickCallback {
    void onProductClick(StoreItem item);
}
