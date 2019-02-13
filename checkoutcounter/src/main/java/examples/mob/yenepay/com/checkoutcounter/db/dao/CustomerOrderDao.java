package examples.mob.yenepay.com.checkoutcounter.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;

@Dao
public interface CustomerOrderDao extends BaseDao<CustomerOrder> {
    @Query("Select * from orders order by lastStatusDate desc")
    LiveData<List<CustomerOrder>> getAll();
    @Query("Select * from orders order by lastStatusDate desc limit 20")
    LiveData<List<CustomerOrder>> getRecent();
//    @Query("Select * from items where categoryId = :categoryId")
//    LiveData<List<CustomerOrder>> getByCategory(int categoryId);
//    @Query("Select * from items where categoryId in (:categoryIds)")
//    LiveData<List<CustomerOrder>> getByCategory(List<Integer> categoryIds);
    @Query("Select * from orders where id = :id")
    LiveData<Order> getOrder(String id);
    @Query("Select * from orders where id = :id")
    Order getOrderPOJO(String id);
    @Query("Delete from orders")
    void deleteAllOrders();
}
