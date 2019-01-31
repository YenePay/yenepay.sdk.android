package examples.mob.yenepay.com.checkoutcounter.store;

import android.os.AsyncTask;

public class DatabaseAsyncTask<T> extends AsyncTask<T, Void, Void> {
    private BaseDao<T> mDao;
    private AsyncDbOperation mOperation;
    public DatabaseAsyncTask(BaseDao<T> dao, AsyncDbOperation operation){
        mDao = dao;
        mOperation = operation;
    }
    @Override
    protected Void doInBackground(T... params) {
        switch (mOperation){
            case Delete:
                mDao.deleteAll(params);
                break;
            case Insert:
                mDao.insertAll(params);
                break;
            case Update:
                mDao.updateAll(params);
                break;
        }
        return null;
    }

}
