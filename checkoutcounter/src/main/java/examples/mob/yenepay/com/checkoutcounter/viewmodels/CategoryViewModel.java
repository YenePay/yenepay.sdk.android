package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class CategoryViewModel extends AndroidViewModel {
    StoreRepository mRepo;
    private final LiveData<ItemCategory> mObservableItem;
    public ObservableField<ItemCategory> categoryItem = new ObservableField<>();
    private final int mId;
    private View.OnFocusChangeListener mOnItemNameFocusChange;

    public CategoryViewModel(@NonNull Application application, int itemId) {
        super(application);
        mRepo = StoreRepository.getInstance(application);
        mId = itemId;
        if(mId != 0) {
            mObservableItem = mRepo.getCategory(mId);
        } else {
            mObservableItem = null;
            this.setItem(new ItemCategory());
        }

        mOnItemNameFocusChange = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isNameValid(true);
                }
            }
        };
    }
    public ObservableField<Integer> nameError = new ObservableField<>();

    public View.OnFocusChangeListener getNameChangeListner() {
        return mOnItemNameFocusChange;
    }
    public boolean isValid() {
        boolean valid = isNameValid(true);
        return valid;
    }

    public boolean isNameValid(boolean setMessage) {
        // Minimum a@b.c
        ItemCategory product = categoryItem.get();
        if(product != null && !TextUtils.isEmpty(product.name)){
            nameError.set(null);
            return true;
        } else {
            if (setMessage)
                nameError.set(R.string.validation_empty_field_not_allowed);
            return false;
        }
    }

    public void setItem(ItemCategory item) {
        this.categoryItem.set(item);
    }




    public LiveData<ItemCategory> getObservableItem() {
        return mObservableItem;
    }

    public void save(ItemCategory item, StoreRepository.OnCategoryCreated callback) {
        if(item.id == 0){
            mRepo.insertCategory(item, callback);
        } else {
            mRepo.updateCategory(item);
        }
    }

    public void delete(ItemCategory item) {
        mRepo.deleteCategory(item);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final int mCategoryId;


        public Factory(@NonNull Application application, int id) {
            mApplication = application;
            mCategoryId = id;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new CategoryViewModel(mApplication, mCategoryId);
        }
    }
}
