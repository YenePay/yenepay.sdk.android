package examples.mob.yenepay.com.checkoutcounter.store;

import android.preference.PreferenceManager;

import com.yenepaySDK.model.OrderedItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.StoreApp;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class StoreManager {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<StoreItem> ITEMS = new ArrayList<StoreItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, StoreItem> ITEM_MAP = new HashMap<String, StoreItem>();

    public static final List<OrderedItem> ORDERS = new ArrayList<OrderedItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, OrderedItem> ORDERS_MAP = new HashMap<String, OrderedItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        /*for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }*/
        addItem(new StoreItem("1", "Fikir esike Mekabir", "Book - Fikir esike Mekabir by Haddis Alemayehu", 250, null, R.drawable.item_large_1));
        addItem(new StoreItem("2", "Women shoes", "Quality women shoes - black, size 36", 400, null, R.drawable.item_large_2));
        addItem(new StoreItem("3", "Nike Sniker", "Nike Sniker - white, size 42", 1500, null, R.drawable.item_large_3));
        addItem(new StoreItem("4", "Port wrist watch", "Original Port wrist watch black", 700, null, R.drawable.item_large_4));
        addItem(new StoreItem("5", "Electric stove", "Electric cooking stove 220 watt", 190.50, null, R.drawable.item_large_5));
        addItem(new StoreItem("6", "Women hand bag", "Leather women hand bag grey color", 250, null, R.drawable.item_large_6));
    }

    private static void addItem(StoreItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }


    /*private static StoreItem createDummyItem(int position) {
        return new StoreItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public static List<OrderedItem> getOrders() {
        return ORDERS;
    }

    public static void addOrder(StoreItem item, int qty){
        if(ORDERS_MAP.containsKey(item.id)){
            OrderedItem orderedItem = ORDERS_MAP.get(item.id);
            orderedItem.setQuantity(orderedItem.getQuantity() + qty);
        } else {
            OrderedItem orderedItem = new OrderedItem(item.id, item.content, qty, item.price);
            ORDERS.add(orderedItem);
            ORDERS_MAP.put(item.id, orderedItem);
        }

    }

    public static CustomerOrder generateCustomerOrder(){
        return new CustomerOrder(getStoreCode(), "Cart", ORDERS);
    }

    public static String getStoreCode() {
        return PreferenceManager.getDefaultSharedPreferences(StoreApp.getContext()).getString("account_no", "0095");
    }

    public static CustomerOrder getNewOrder(String orderId, String nounce) {
        return generateCustomerOrder();
    }
    public static String getStoreName() {
        String defaultName = "Your-Store";
        return PreferenceManager.getDefaultSharedPreferences(StoreApp.getContext()).getString("store_name", defaultName);
    }

    public static String getStoreTerminalName() {
        String defaultName = "YenePay-" + getStoreCode() + "-Store-" + (int) (Math.random() * 100);
        return PreferenceManager.getDefaultSharedPreferences(StoreApp.getContext()).getString("store_name", defaultName);
    }
}
