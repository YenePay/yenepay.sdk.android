package examples.mob.yenepay.com.checkoutcounter.store;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "categories")
public class ItemCategory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String name;
    public String description;
    public ItemCategory(){
    }
}
