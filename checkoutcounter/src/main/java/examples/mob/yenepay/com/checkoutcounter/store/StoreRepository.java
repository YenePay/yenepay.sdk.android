package examples.mob.yenepay.com.checkoutcounter.store;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class StoreRepository {
    private ItemsDao mItemsDao;
    private CategoryDao mCategoryDao;
    private LiveData<List<ItemCategory>> mAllCategories;
    private LiveData<List<StoreItem>> mAllItems;

    public StoreRepository(Application application){
        StoreDatabase database = StoreDatabase.getDatabase(application);
        mItemsDao = database.itemsDao();
        mCategoryDao = database.categoryDao();
        mAllCategories = mCategoryDao.getAll();
        mAllItems = mItemsDao.getAll();
    }

    public LiveData<List<ItemCategory>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<List<StoreItem>> getAllItems() {
        return mAllItems;
    }

    public void insertCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Insert)
                .execute(category);
    }

    public void updateCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Update)
                .execute(category);
    }

    public void deleteCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Delete)
                .execute(category);
    }

    public void insertItem(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Insert)
                .execute(item);
    }

    public void updateCategory(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Update)
                .execute(item);
    }

    public void deleteCategory(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Delete)
                .execute(item);
    }

    public LiveData<StoreItem> getItem(String id) {
        return mItemsDao.getItem(id);
    }
}
