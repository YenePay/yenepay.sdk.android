package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.db.model.Product;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class ItemViewModel extends AndroidViewModel {
    StoreRepository mRepo;
    private final LiveData<Product> mObservableItem;
    public ObservableField<Product> storeItem = new ObservableField<>();
    public ObservableField<Boolean> isNameValid = new ObservableField<>();
    public ObservableField<Boolean> isPriceValid = new ObservableField<>();
    private final String mItemId;
    private LiveData<List<ItemCategory>> mObservableCategories;

    private View.OnFocusChangeListener mOnItemNameFocusChange;
    private View.OnFocusChangeListener mOnItemPriceFocusChange;

    public ItemViewModel(@NonNull Application application, String itemId) {
        super(application);
        mRepo = StoreRepository.getInstance(application);
        mItemId = itemId;
        if(!TextUtils.isEmpty(mItemId)) {
            mObservableItem = mRepo.getItem(mItemId);
        } else {
            mObservableItem = null;
            this.setItem(new Product());
        }
        mObservableCategories = mRepo.getAllCategories();

        mOnItemNameFocusChange = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isNameValid(true);
                }
            }
        };

        mOnItemPriceFocusChange = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isPriceValid(true);
                }
            }
        };
    }

    public void setItem(Product item) {
        this.storeItem.set(item);
    }

    public ObservableField<Integer> nameError = new ObservableField<>();
    public ObservableField<Integer> priceError = new ObservableField<>();

    public View.OnFocusChangeListener getNameChangeListner() {
        return mOnItemNameFocusChange;
    }

    public View.OnFocusChangeListener getPriceChangeListner() {
        return mOnItemPriceFocusChange;
    }
    public boolean isValid() {
        boolean valid = isNameValid(true);
        valid = isPriceValid(true) && valid;
        return valid;
    }

    public boolean isNameValid(boolean setMessage) {
        // Minimum a@b.c
        Product product = storeItem.get();
        if(product != null && !TextUtils.isEmpty(product.content)){
            nameError.set(null);
            return true;
        } else {
            if (setMessage)
                nameError.set(R.string.validation_empty_field_not_allowed);
            return false;
        }
    }

    public boolean isPriceValid(boolean setMessage) {
        Product product = storeItem.get();
        if (product != null && product.price > 0) {
            priceError.set(null);
            return true;
        } else {
            if (setMessage)
                priceError.set(R.string.invalid_price);
            return false;
        }
    }

    public LiveData<List<ItemCategory>> getAllCategories(){
        return mObservableCategories;
    }

    public LiveData<Product> getObservableItem() {
        return mObservableItem;
    }

    public void save(StoreItem item) {
        if(TextUtils.isEmpty(item.id)){
            item.id = UUID.randomUUID().toString();
            mRepo.insertItem(item);
        } else {
            mRepo.updateItem(item);
        }
    }

    public void delete(StoreItem item) {
        mRepo.deleteItem(item);
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
