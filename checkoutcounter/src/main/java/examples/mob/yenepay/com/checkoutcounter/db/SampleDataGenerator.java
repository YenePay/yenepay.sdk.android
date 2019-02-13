package examples.mob.yenepay.com.checkoutcounter.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public class SampleDataGenerator {

    private static final String[] FIRST = new String[]{
            "Special edition", "New", "Cheap", "Quality", "Used"};
    private static final String[] SECOND = new String[]{
            "Three-headed Monkey", "Rubber Chicken", "Pint of Grog", "Monocle"};
    private static final String[] DESCRIPTION = new String[]{
            "is finally here", "is recommended by Stan S. Stanman",
            "is the best sold product on Mêlée Island", "is \uD83D\uDCAF", "is ❤️", "is fine"};
    private static final String[] COMMENTS = new String[]{
            "Comment 1", "Comment 2", "Comment 3", "Comment 4", "Comment 5", "Comment 6"};

    public static StoreItem[] generateProducts() {
        List<StoreItem> products = new ArrayList<>(FIRST.length * SECOND.length);
        Random rnd = new Random();
        for (int i = 0; i < FIRST.length; i++) {
            for (int j = 0; j < SECOND.length; j++) {
                StoreItem product = new StoreItem();
                product.content = FIRST[i] + " " + SECOND[j];
                product.details = product.content + " " + DESCRIPTION[j];
                product.price = rnd.nextInt(240);
                product.id = String.valueOf(FIRST.length * i + j + 1);
                products.add(product);
            }
        }
        StoreItem[] array = new StoreItem[products.size()];
        products.toArray(array);
        return products.toArray(array);
    }

    public static ItemCategory[] generateCategories() {
        ItemCategory[] comments = new ItemCategory[] { new ItemCategory("Default", "Default Category") };
        return comments;
    }
}
