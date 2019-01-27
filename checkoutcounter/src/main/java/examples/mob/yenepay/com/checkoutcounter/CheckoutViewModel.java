package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.yenepaySDK.model.OrderedItem;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;

public class CheckoutViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<OrderedItem>> orderedItems;
    private MutableLiveData<Boolean> twoPane;

    public LiveData<List<OrderedItem>> getOrderedItems(){
        if(orderedItems == null){
            orderedItems = new MutableLiveData<>();
            loadItems();
        }
        return orderedItems;
    }

    public LiveData<Boolean> isTwoPane(){
        initTwoPaneIfNull();
        return twoPane;
    }

    private void initTwoPaneIfNull() {
        if(twoPane == null){
            twoPane = new MutableLiveData<>();
            twoPane.setValue(false);
        }
    }

    public void loadItems() {
        orderedItems.setValue(StoreManager.getOrders());
    }

    public void setTwoPane(boolean value){
        initTwoPaneIfNull();
        twoPane.setValue(value);
    }


}
