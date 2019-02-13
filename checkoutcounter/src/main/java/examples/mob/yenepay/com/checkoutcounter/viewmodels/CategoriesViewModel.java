package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class CategoriesViewModel extends AndroidViewModel {
    private StoreRepository mStoreRepo;
    private LiveData<List<StoreItem>> items;

    private MediatorLiveData<List<ItemCategory>> mObservableCategories;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        mStoreRepo = StoreRepository.getInstance(application);
        mObservableCategories = new MediatorLiveData<>();
        mObservableCategories.setValue(null);
        LiveData<List<ItemCategory>> allCategories = mStoreRepo.getAllCategories();

        mObservableCategories.addSource(allCategories, mObservableCategories::setValue);
//        items = mStoreRepo.getAllItems();
    }


    public LiveData<List<StoreItem>> getAllItems() {
//        if (items == null) {
//            items = new MutableLiveData<List<StoreItem>>();
//            loadItems();
//        }
        return items;
    }

    public LiveData<List<ItemCategory>> getAllCategories() {
//        if (items == null) {
//            items = new MutableLiveData<List<StoreItem>>();
//            loadItems();
//        }
        return mObservableCategories;
    }

    private void loadItems() {
//        items.setValue(StoreManager.ITEMS);
        // Do an asynchronous operation to fetch users.
    }
}
