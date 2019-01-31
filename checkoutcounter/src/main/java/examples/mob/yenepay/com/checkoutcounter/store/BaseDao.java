package examples.mob.yenepay.com.checkoutcounter.store;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

public interface BaseDao<T> {
    @Insert
    void insert(T entity);
    @Insert
    void insertAll(T... entities);
    @Update
    void update(T entity);
    @Update
    void updateAll(T... entities);
    @Delete
    void delete(T entity);
    @Delete
    void deleteAll(T... entities);
}
