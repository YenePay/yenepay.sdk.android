package examples.mob.yenepay.com.checkoutcounter.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * A store item representing a piece of content.
 */
@Entity(tableName = "items", foreignKeys = @ForeignKey(entity = ItemCategory.class,
        parentColumns = "id",
        childColumns = "categoryId"))
public class StoreItem {
    @PrimaryKey
    @NonNull
    public String id;
    public String content;
    public String details;
    public double price;
    public String image;
    public int largeImageResId;
    public int categoryId;

    @Ignore
    public StoreItem(){
//        id = UUID.randomUUID().toString();
    }

    public StoreItem(String id, String content, String details, double price, String image, int largeImageResId) {
        this.id = id;
        this.content = content;
        this.details = details;
        this.price = price;
        this.image = image;
        this.largeImageResId = largeImageResId;
        this.categoryId = 1;
    }

    @Override
    public String toString() {
        return "StoreItem{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", details='" + details + '\'' +
                ", price=" + price +
                ", image=" + image +
                ", largeImageResId=" + largeImageResId +
                '}';
    }
}
