package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;

public class ItemsViewModel extends ViewModel {
    private MutableLiveData<List<StoreManager.StoreItem>> items;

    public LiveData<List<StoreManager.StoreItem>> getUsers() {
        if (items == null) {
            items = new MutableLiveData<List<StoreManager.StoreItem>>();
            loadUsers();
        }
        return items;
    }

    private void loadUsers() {
        items.setValue(StoreManager.ITEMS);
        // Do an asynchronous operation to fetch users.
    }
}
