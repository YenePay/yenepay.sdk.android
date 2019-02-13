package examples.mob.yenepay.com.checkoutcounter.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;

@Dao
public interface CategoryDao extends BaseDao<ItemCategory> {
    @Query("select * from categories")
    LiveData<List<ItemCategory>> getAll();
    @Query("Select * from categories where id = :id")
    LiveData<ItemCategory> getCategory(int id);
}
