package examples.mob.yenepay.com.checkoutcounter.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.yenepaySDK.PaymentResponse;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Entity(tableName = "payment_responses", foreignKeys = @ForeignKey(entity = CustomerOrder.class,
        parentColumns = "id",
        childColumns = "orderId"))
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponseEntity extends PaymentResponse {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String orderId;

    @NonNull
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(@NonNull String orderId) {
        this.orderId = orderId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
