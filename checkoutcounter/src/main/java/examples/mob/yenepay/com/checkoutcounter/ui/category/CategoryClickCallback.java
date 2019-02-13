package examples.mob.yenepay.com.checkoutcounter.ui.category;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public interface CategoryClickCallback {
    void onProductClick(ItemCategory item);
}
