package examples.mob.yenepay.com.checkoutcounter.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.yenepaySDK.model.OrderedItem;
@Entity(tableName = "ordered_items",
        foreignKeys = @ForeignKey(entity = CustomerOrder.class,
        parentColumns = "id",
        childColumns = "orderId", onDelete = ForeignKey.CASCADE))
public class OrderedItemEntity extends OrderedItem {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String orderId;
    @Ignore
    public OrderedItemEntity(OrderedItem item){
        super(item.getItemId(), item.getItemName(), item.getQuantity(), item.getUnitPrice());
    }
    public OrderedItemEntity(String itemId, String itemName, int quantity, double unitPrice) {
        super(itemId, itemName, quantity, unitPrice);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
