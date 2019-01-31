package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.store.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.store.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class ItemViewModel extends AndroidViewModel {
    StoreRepository mRepo;
    private final LiveData<StoreItem> mObservableItem;
    public ObservableField<StoreItem> storeItem = new ObservableField<>();
    private final String mItemId;
    private LiveData<List<ItemCategory>> mObservableCategories;

    public ItemViewModel(@NonNull Application application, String itemId) {
        super(application);
        mRepo = new StoreRepository(application);
        mItemId = itemId;
        mObservableItem = mRepo.getItem(mItemId);
        mObservableCategories = mRepo.getAllCategories();
    }

    public void setItem(StoreItem item) {
        this.storeItem.set(item);
    }



    public LiveData<List<ItemCategory>> getAllCategories(){
        return mObservableCategories;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final String mProductId;


        public Factory(@NonNull Application application, String productId) {
            mApplication = application;
            mProductId = productId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new ItemViewModel(mApplication, mProductId);
        }
    }
}
