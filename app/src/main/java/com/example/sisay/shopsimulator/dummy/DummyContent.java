package com.example.sisay.shopsimulator.dummy;

import com.example.sisay.shopsimulator.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        /*for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }*/
        addItem(new DummyItem("1", "Fikir esike Mekabir", "Book - Fikir esike Mekabir by Haddis Alemayehu", 250, R.drawable.item1, R.drawable.item_large_1));
        addItem(new DummyItem("2", "Women shoes", "Quality women shoes - black, size 36", 400, R.drawable.item2, R.drawable.item_large_2));
        addItem(new DummyItem("3", "Nike Sniker", "Nike Sniker - white, size 42", 1500, R.drawable.item3, R.drawable.item_large_3));
        addItem(new DummyItem("4", "Port wrist watch", "Original Port wrist watch black", 700, R.drawable.item4, R.drawable.item_large_4));
        addItem(new DummyItem("5", "Electric stove", "Electric cooking stove 220 watt", 190.50, R.drawable.item5, R.drawable.item_large_5));
        addItem(new DummyItem("6", "Women hand bag", "Leather women hand bag grey color", 250, R.drawable.item6, R.drawable.item_large_6));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /*private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }*/

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;
        public final double price;
        public final int imageResId;
        public final int largeImageResId;

        public DummyItem(String id, String content, String details, double price, int imageResId, int largeImageResId) {
            this.id = id;
            this.content = content;
            this.details = details;
            this.price = price;
            this.imageResId = imageResId;
            this.largeImageResId = largeImageResId;
        }

        @Override
        public String toString() {
            return "DummyItem{" +
                    "content='" + content + '\'' +
                    ", price=" + price +
                    '}';
        }
    }
}
