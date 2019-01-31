package examples.mob.yenepay.com.checkoutcounter.store;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
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
    public int imageResId;
    public int largeImageResId;
    public int categoryId;

    public StoreItem(String id, String content, String details, double price, int imageResId, int largeImageResId) {
        this.id = id;
        this.content = content;
        this.details = details;
        this.price = price;
        this.imageResId = imageResId;
        this.largeImageResId = largeImageResId;
    }

    @Override
    public String toString() {
        return "StoreItem{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", details='" + details + '\'' +
                ", price=" + price +
                ", imageResId=" + imageResId +
                ", largeImageResId=" + largeImageResId +
                '}';
    }
}
