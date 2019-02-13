package examples.mob.yenepay.com.checkoutcounter.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
@Dao
public interface OrderedItemsDao extends BaseDao<OrderedItemEntity> {
    @Query("select * from ordered_items")
    LiveData<List<OrderedItemEntity>> getAll();
    @Query("select * from ordered_items where orderId = :orderId")
    LiveData<List<OrderedItemEntity>> getByOrderId(String orderId);


}
