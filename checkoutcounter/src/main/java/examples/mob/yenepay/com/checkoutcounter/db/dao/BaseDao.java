package examples.mob.yenepay.com.checkoutcounter.db.dao;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

public interface BaseDao<T> {
    @Insert
    long insert(T entity);
    @Insert
    long[] insertAll(T... entities);
    @Update
    void update(T entity);
    @Update
    void updateAll(T... entities);
    @Delete
    void delete(T entity);
    @Delete
    void deleteAll(T... entities);
}
