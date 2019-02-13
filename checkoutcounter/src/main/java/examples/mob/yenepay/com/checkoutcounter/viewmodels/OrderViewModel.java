package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class OrderViewModel extends AndroidViewModel {

    private final StoreRepository mRepo;
    private final String mOrderId;
    private final LiveData<Order> mObservableOrder;
    public ObservableField<Order> order = new ObservableField<>();

    public OrderViewModel(@NonNull Application application, String id) {
        super(application);
        mRepo = StoreRepository.getInstance(application);
        mOrderId = id;
        mObservableOrder = mRepo.getOrder(mOrderId);
    }

    public LiveData<Order> getmObservableOrder() {
        return mObservableOrder;
    }

    public void setOrder(Order order) {
        this.order.set(order);
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
            return (T) new OrderViewModel(mApplication, mProductId);
        }
    }
}
