package examples.mob.yenepay.com.checkoutcounter.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableField;
import android.net.wifi.p2p.WifiP2pInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.yenepaySDK.model.OrderedItem;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import examples.mob.yenepay.com.checkoutcounter.db.StoreDatabase;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;
import examples.mob.yenepay.com.checkoutcounter.utils.SettingUtil;

public class CheckoutViewModel extends AndroidViewModel {
    private static final String TAG = "CheckoutViewModel";
    private final StoreRepository mRepo;
    // TODO: Implement the ViewModel
    private MutableLiveData<List<OrderedItem>> orderedItems;
    private MutableLiveData<Boolean> twoPane;
    private MutableLiveData<String> status;
    private MutableLiveData<String> deviceAddress;
    private MutableLiveData<String> deviceName;
    private MutableLiveData<String> passPhrase;
    private String mOrderId;

    private MediatorLiveData<Order> mObservableOrder;
    private LiveData<Order> currentOrder;
    public final ObservableField<Order> order = new ObservableField<>();

//    private SettingUtil mSetting;

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        mRepo = StoreRepository.getInstance(application);
        deviceAddress = new MutableLiveData<>();
        deviceName = new MutableLiveData<>();
        passPhrase = new MutableLiveData<>();
        mObservableOrder = new MediatorLiveData<>();
        mObservableOrder.setValue(null);
//        mSetting = SettingUtil.getInstance();
//        initNewOrder();

    }

    public void setOrderId(String orderId) {
        String oldId = mOrderId;
        this.mOrderId = orderId;
        if(currentOrder != null){
            mObservableOrder.removeSource(currentOrder);
        }
        currentOrder = mRepo.getOrder(mOrderId);
        mObservableOrder.addSource(currentOrder, mObservableOrder::setValue);
    }

    public void initNewOrder() {
        setOrderId(createNewOrder());

    }

    public String createNewOrder() {
        CustomerOrder order = new CustomerOrder();
        order.setId(UUID.randomUUID().toString());
        order.setStoreCode(StoreManager.getStoreCode());
        long createdDate = new Date().getTime();
        order.setCreatedDate(createdDate);
        order.setLastStatusDate(createdDate);
        order.setProcess("Cart");
        String orderId = order.getId();
        mRepo.insertOrder(order);
        Log.d(TAG, "createNewOrder: Order Inserted - " + orderId);
        return orderId;
    }

    public void setOrder(Order order){
        if(order != null) {
//            order.initTotals();
        }
        this.order.set(order);
    }

    public void addItem(StoreItem item){
        Order order = this.order.get();
        OrderedItemEntity existing = order.getItem(item.id);
        if(existing != null){
            existing.setQuantity(existing.getQuantity() + 1);
            mRepo.updateOrderedItem(existing);
        } else {
            OrderedItemEntity orderedItemEntity = order.convertToEntity(item);
            orderedItemEntity.setOrderId(order.getId());
            mRepo.insertOrderedItem(orderedItemEntity);
        }
//        order.initTotals();
//        mRepo.updateOrder(order);
    }

    public void removeItem(OrderedItem item){
        Order order = this.order.get();
        OrderedItemEntity existing = order.getItem(item.getItemId());
        if(existing != null) {
            if (existing.getQuantity() > 1) {
                existing.setQuantity(existing.getQuantity() - 1);
                mRepo.updateOrderedItem(existing);
            } else if (existing.getQuantity() == 1) {
                mRepo.deleteOrderedItem(existing);
            }
//            order.initTotals();
//            mRepo.updateOrder(order);
        }
    }

    public LiveData<Order> getObservableOrder() {
        return mObservableOrder;
    }

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
        return deviceAddress;
    }
    public LiveData<String> getDeviceName(){
        return deviceName;
    }

    public LiveData<String> getPassPhrase(){
        return passPhrase;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress.setValue(deviceAddress);
    }

    public void setDeviceName(String deviceName) {
        this.deviceName.setValue(deviceName);
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase.setValue(passPhrase);
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

//    public static class Factory extends ViewModelProvider.NewInstanceFactory {
//
//        @NonNull
//        private final Application mApplication;
//
//        private final String mCategoryId;
//
//
//        public Factory(@NonNull Application application, String id) {
//            mApplication = application;
//            mCategoryId = id;
//        }
//
//        @Override
//        public <T extends ViewModel> T create(Class<T> modelClass) {
//            //noinspection unchecked
//            return (T) new CheckoutViewModel(mApplication, mCategoryId);
//        }
//    }

}
