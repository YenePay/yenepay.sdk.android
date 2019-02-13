package examples.mob.yenepay.com.checkoutcounter.ui.category;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public interface OnCategoryInteractionListner {
    void onCategorySelected(ItemCategory item);
    void onCategoryEditClicked(ItemCategory item);
    void onCategorySaved(ItemCategory item);
    void onCategoryEditCancelled(ItemCategory item);
    void onCategoryDelete(ItemCategory item);
    void onNewCategoryClicked();
}
