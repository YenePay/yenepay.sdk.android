package examples.mob.yenepay.com.checkoutcounter.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
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
    @Ignore
    public ItemCategory(@NonNull String name, String description) {
        this.name = name;
        this.description = description;
    }
}
