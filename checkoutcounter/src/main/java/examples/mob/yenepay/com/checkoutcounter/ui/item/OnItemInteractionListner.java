package examples.mob.yenepay.com.checkoutcounter.ui.item;

import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public interface OnItemInteractionListner {
    void onItemSelected(StoreItem item, int quantity);
    void onItemChangeImageClicked(StoreItem item);
    void onItemEditClicked(StoreItem item);
    void onItemSaved(StoreItem item);
    void onItemEditCancelled(StoreItem item);
    void onItemDelete(StoreItem item);
    void onNewItemClicked();
}
