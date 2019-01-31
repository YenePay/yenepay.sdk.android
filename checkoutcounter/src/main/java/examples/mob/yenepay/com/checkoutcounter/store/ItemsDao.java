package examples.mob.yenepay.com.checkoutcounter.store;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ItemsDao extends BaseDao<StoreItem> {
    @Query("Select * from items")
    LiveData<List<StoreItem>> getAll();
    @Query("Select * from items where categoryId = :categoryId")
    LiveData<List<StoreItem>> getByCategory(int categoryId);
    @Query("Select * from items where categoryId in (:categoryIds)")
    LiveData<List<StoreItem>> getByCategory(List<Integer> categoryIds);
    @Query("Select * from items where id = :id")
    LiveData<StoreItem> getItem(String id);
}
