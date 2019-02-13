package examples.mob.yenepay.com.checkoutcounter.db;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.yenepaySDK.PaymentResponse;

import examples.mob.yenepay.com.checkoutcounter.AppExecutors;
import examples.mob.yenepay.com.checkoutcounter.db.dao.CategoryDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.CustomerOrderDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.ItemsDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.OrderedItemsDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.PaymentResponseDao;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.PaymentResponseEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

@Database(entities = {
        ItemCategory.class,
        StoreItem.class,
        CustomerOrder.class,
        OrderedItemEntity.class,
        PaymentResponseEntity.class}, version = 3)
public abstract class StoreDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "store_app_database";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public abstract ItemsDao itemsDao();
    public abstract CategoryDao categoryDao();
    public abstract CustomerOrderDao customerOrderDao();
    public abstract OrderedItemsDao orderedItemsDao();
    public abstract PaymentResponseDao paymentResponseDao();

    private static volatile StoreDatabase INSTANCE;

    public static StoreDatabase getDatabase(Application application) {
        if (INSTANCE == null) {
            synchronized (StoreDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildDatabase(application);
                    INSTANCE.updateDatabaseCreated(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    private static StoreDatabase buildDatabase(final Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), StoreDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        new AppExecutors().diskIO().execute(() -> {
                            // Add a delay to simulate a long-running operation
//                            addDelay();
                            // Generate the data for pre-population
                            StoreDatabase database = StoreDatabase.getDatabase(application);
                            StoreItem[] products = SampleDataGenerator.generateProducts();
                            ItemCategory[] categories = SampleDataGenerator.generateCategories();

                            insertData(database, categories, products);
                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
//                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build();
    }

    private static void insertData(final StoreDatabase database, final ItemCategory[] categories,
                                   final StoreItem[] items) {
        database.runInTransaction(() -> {
            long id = database.categoryDao().insert(categories[0]);
            for (StoreItem item: items){
                item.categoryId = (int)id;
            }
            database.itemsDao().insertAll(items);
        });
    }
    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4(`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                    + "SELECT `id`, `name`, `description` FROM products");

        }
    };
}
