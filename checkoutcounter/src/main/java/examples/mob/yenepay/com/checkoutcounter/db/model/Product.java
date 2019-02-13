package examples.mob.yenepay.com.checkoutcounter.db.model;

import android.arch.persistence.room.Relation;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public class Product extends StoreItem {

    public Product() {
        super();
    }

    @Relation(parentColumn = "categoryId", entityColumn = "id")
    public List<ItemCategory> categories;

    public ItemCategory getCategory(){
        if(categories != null && !categories.isEmpty()){
            return categories.get(0);
        }
        return null;
    }

    public String getCategoryName(){
        return getCategory() != null? getCategory().name: "No category selected";
    }
}
