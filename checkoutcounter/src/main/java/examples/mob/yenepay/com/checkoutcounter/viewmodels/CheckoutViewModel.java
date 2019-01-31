package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.net.wifi.p2p.WifiP2pInfo;

import com.yenepaySDK.model.OrderedItem;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;

public class CheckoutViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<OrderedItem>> orderedItems;
    private MutableLiveData<Boolean> twoPane;
    private MutableLiveData<String> status;
    private MutableLiveData<String> deviceAddress;
    private MutableLiveData<String> deviceName;

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

    public LiveData<String> getStatus(){
        if(status == null){
            status = new MutableLiveData<>();
            status.setValue("Pending");
        }
        return status;
    }

    public void setStatus(String status){
        this.status.setValue(status);
    }


    public LiveData<String> getDeviceAddress(){
        if(deviceAddress == null){
            deviceAddress = new MutableLiveData<>();
            deviceAddress.setValue("Not Started");
        }
        return deviceAddress;
    }
    public LiveData<String> getDeviceName(){
        if(deviceName == null){
            deviceName = new MutableLiveData<>();
            deviceName.setValue("Pending");
        }
        return deviceName;
    }
    public void setP2PInfo(WifiP2pInfo info){
        if(deviceAddress == null){
            deviceAddress = new MutableLiveData<>();
            deviceAddress.setValue("Not Started");
        }
        this.deviceAddress.setValue(info.groupOwnerAddress.toString());
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
