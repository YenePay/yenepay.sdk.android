package examples.mob.yenepay.com.checkoutcounter.store;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import examples.mob.yenepay.com.checkoutcounter.StoreApp;

@Database(entities = {ItemCategory.class, StoreItem.class}, version = 1)
public abstract class StoreDatabase extends RoomDatabase {

    public abstract ItemsDao itemsDao();
    public abstract CategoryDao categoryDao();

    private static volatile StoreDatabase INSTANCE;

    static StoreDatabase getDatabase(Application application) {
        if (INSTANCE == null) {
            synchronized (StoreDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(application.getApplicationContext(),
                            StoreDatabase.class, "store_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
