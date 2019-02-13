package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;

public class OrderListViewModel extends AndroidViewModel {

    private final StoreRepository mRepo;
    private final LiveData<List<CustomerOrder>> mOrbservableOrders;

    public OrderListViewModel(@NonNull Application application) {
        super(application);
        mRepo = StoreRepository.getInstance(application);
        mOrbservableOrders = mRepo.getRecentOrders();
    }

    public LiveData<List<CustomerOrder>> getOrbservableOrders() {
        return mOrbservableOrders;
    }
}
