package examples.mob.yenepay.com.checkoutcounter.store;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.yenepaySDK.PaymentResponse;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.StoreDatabase;
import examples.mob.yenepay.com.checkoutcounter.db.dao.BaseDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.CategoryDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.CustomerOrderDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.ItemsDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.OrderedItemsDao;
import examples.mob.yenepay.com.checkoutcounter.db.dao.PaymentResponseDao;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.PaymentResponseEntity;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;
import examples.mob.yenepay.com.checkoutcounter.db.model.Product;

public class StoreRepository {
    private static StoreRepository sInstance;
    private ItemsDao mItemsDao;
    private CategoryDao mCategoryDao;

    private CustomerOrderDao mCustomerOrderDao;
    private OrderedItemsDao mOrderedItemsDao;
    private PaymentResponseDao mPaymenrResponseDao;

    private LiveData<List<ItemCategory>> mAllCategories;
    private LiveData<List<StoreItem>> mAllItems;

    private LiveData<List<CustomerOrder>> mRecentOrders;
    private final StoreDatabase mDatabase;


    public StoreRepository(Application application){
        mDatabase = StoreDatabase.getDatabase(application);
        mItemsDao = mDatabase.itemsDao();
        mCategoryDao = mDatabase.categoryDao();
        mAllCategories = mCategoryDao.getAll();
        mAllItems = mItemsDao.getAll();
        mCustomerOrderDao = mDatabase.customerOrderDao();
        mOrderedItemsDao = mDatabase.orderedItemsDao();
        mPaymenrResponseDao = mDatabase.paymentResponseDao();
    }

    public static StoreRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (StoreRepository.class) {
                if (sInstance == null) {
                    sInstance = new StoreRepository(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<ItemCategory>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<List<StoreItem>> getAllItems() {
        return mAllItems;
    }

    public LiveData<List<CustomerOrder>> getRecentOrders(){ return mCustomerOrderDao.getAll(); }

    public LiveData<Order> getOrder(String id) { return mCustomerOrderDao.getOrder(id); }

    public Order getOrderPOJO(String id) { return mCustomerOrderDao.getOrderPOJO(id); }

    public LiveData<List<OrderedItemEntity>> getOrderedItems(String orderId){
        return mOrderedItemsDao.getByOrderId(orderId);
    }

    public LiveData<List<PaymentResponseEntity>> getPaymentResponses(String orderId){
        return mPaymenrResponseDao.getByOrderId(orderId);
    }

    public void insertCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Insert)
                .execute(category);
    }

    public void insertCategory(ItemCategory category, final OnCategoryCreated callback){
        new InsertCategoryTask(mCategoryDao, callback).execute(category);
    }

    public void updateCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Update)
                .execute(category);
    }

    public void deleteCategory(ItemCategory category){
        new DatabaseAsyncTask<ItemCategory>(mCategoryDao, AsyncDbOperation.Delete)
                .execute(category);
    }

    public void insertItem(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Insert)
                .execute(item);
    }

    public void updateItem(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Update)
                .execute(item);
    }

    public void deleteItem(StoreItem item){
        new DatabaseAsyncTask<StoreItem>(mItemsDao, AsyncDbOperation.Delete)
                .execute(item);
    }

    public void insertNewOrder(CustomerOrder order){
        new InsertNewOrderTask(mDatabase).execute(order);
    }

    public void updateOrder(CustomerOrder item){
        new DatabaseAsyncTask<CustomerOrder>(mCustomerOrderDao, AsyncDbOperation.Update)
                .execute(item);
    }

    public void insertPaymentResponse(PaymentResponseEntity response){
        new DatabaseAsyncTask<>(mPaymenrResponseDao, AsyncDbOperation.Insert)
                .execute(response);
    }

    public LiveData<Product> getItem(String id) {
        return mItemsDao.getItem(id);
    }

    public LiveData<ItemCategory> getCategory(int id) {
        return mCategoryDao.getCategory(id);
    }

    public void updateOrderedItem(OrderedItemEntity existing) {
//        new DatabaseAsyncTask<OrderedItemEntity>(mOrderedItemsDao, AsyncDbOperation.Update).execute(existing);
        new AddOrUpdateOrderItemTask(mDatabase, AsyncDbOperation.Update).execute(existing);
    }

    public void insertOrderedItem(OrderedItemEntity orderedItemEntity) {
        new AddOrUpdateOrderItemTask(mDatabase, AsyncDbOperation.Insert).execute(orderedItemEntity);
    }

    public void insertOrder(CustomerOrder order) {
        new DatabaseAsyncTask<>(mCustomerOrderDao, AsyncDbOperation.Insert).execute(order);
    }

    public void deleteOrderedItem(OrderedItemEntity orderedItemEntity) {
//        new DatabaseAsyncTask<>(mOrderedItemsDao, AsyncDbOperation.Delete).execute(orderedItemEntity);
        new AddOrUpdateOrderItemTask(mDatabase, AsyncDbOperation.Delete).execute(orderedItemEntity);
    }

    public void deleteAllOrders() {
        new DeleteAllOrdersTask(mCustomerOrderDao).execute();
    }


    static class InsertNewOrderTask extends AsyncTask<CustomerOrder, Void, Void> {
        StoreDatabase mRepo;
        public InsertNewOrderTask(StoreDatabase repo) {
            mRepo = repo;
        }

        @Override
        protected Void doInBackground(CustomerOrder... params) {
            CustomerOrder order = params[0];
            OrderedItemEntity[] items = order.getItemsEntityArray();
            if(items != null) {
                mRepo.runInTransaction(() -> {
                    mRepo.customerOrderDao().insert(order);
                    mRepo.orderedItemsDao().insertAll(items);
                });
            }
            return null;
        }
    }

    static class AddOrUpdateOrderItemTask extends AsyncTask<OrderedItemEntity, Void, Void> {
        private final AsyncDbOperation mOperation;
        StoreDatabase mRepo;

        public AddOrUpdateOrderItemTask(StoreDatabase repo, AsyncDbOperation operation) {
            mRepo = repo;
            mOperation = operation;
        }

        @Override
        protected Void doInBackground(OrderedItemEntity... params) {
            OrderedItemEntity itemEntity = params[0];
            if(itemEntity != null) {
                mRepo.runInTransaction(() -> {
                    if(mOperation == AsyncDbOperation.Insert){
                        mRepo.orderedItemsDao().insert(itemEntity);
                    } else if(mOperation == AsyncDbOperation.Update){
                        mRepo.orderedItemsDao().update(itemEntity);
                    } else if(mOperation == AsyncDbOperation.Delete){
                        mRepo.orderedItemsDao().delete(itemEntity);
                    }
                    CustomerOrderDao orderDao = mRepo.customerOrderDao();
                    Order order = orderDao.getOrderPOJO(itemEntity.getOrderId());
                    order.initTotals();
                    orderDao.update(order);
                });
            }
            return null;
        }
    }

    static class InsertCategoryTask extends DatabaseAsyncTask<ItemCategory>{
        private OnCategoryCreated mCallback;
        public InsertCategoryTask(BaseDao<ItemCategory> dao, OnCategoryCreated callback) {
            super(dao, AsyncDbOperation.Insert);
            mCallback = callback;
        }

        @Override
        protected void onPostExecute(long[] longs) {
            if(longs != null && mCallback != null){
                mCallback.onCategoryCreated(longs);
            }
        }
    }
    public interface OnCategoryCreated {
        void onCategoryCreated(long[] newCategoryIds);
    }

    static class DeleteAllOrdersTask extends DatabaseAsyncTask<CustomerOrder>{

        public DeleteAllOrdersTask(BaseDao<CustomerOrder> dao) {
            super(dao, AsyncDbOperation.Custom);
        }

        @Override
        protected long[] customOperation(BaseDao<CustomerOrder> dao, CustomerOrder... params) {
            ((CustomerOrderDao)dao).deleteAllOrders();
            return null;
        }
    }
}


