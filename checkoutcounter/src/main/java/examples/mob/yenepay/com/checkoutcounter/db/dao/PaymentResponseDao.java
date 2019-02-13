package examples.mob.yenepay.com.checkoutcounter.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.PaymentResponseEntity;
@Dao
public interface PaymentResponseDao extends BaseDao<PaymentResponseEntity> {
    @Query("select * from payment_responses")
    LiveData<List<PaymentResponseEntity>> getAll();
    @Query("select * from payment_responses where orderId = :orderId")
    LiveData<List<PaymentResponseEntity>> getByOrderId(String orderId);
}
