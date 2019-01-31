package examples.mob.yenepay.com.checkoutcounter.store;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;
@Dao
public interface CategoryDao extends BaseDao<ItemCategory> {
    @Query("select * from categories")
    LiveData<List<ItemCategory>> getAll();
}
