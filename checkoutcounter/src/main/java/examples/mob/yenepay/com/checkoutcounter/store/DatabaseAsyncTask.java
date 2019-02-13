package examples.mob.yenepay.com.checkoutcounter.store;

import android.os.AsyncTask;

import examples.mob.yenepay.com.checkoutcounter.db.dao.BaseDao;

public class DatabaseAsyncTask<T> extends AsyncTask<T, Void, long[]> {
    private BaseDao<T> mDao;
    private AsyncDbOperation mOperation;
    public DatabaseAsyncTask(BaseDao<T> dao, AsyncDbOperation operation){
        mDao = dao;
        mOperation = operation;
    }
    @Override
    protected long[] doInBackground(T... params) {
        long[] ids = null;
        switch (mOperation){
            case Delete:
                mDao.deleteAll(params);
                break;
            case Insert:
                ids = mDao.insertAll(params);
                break;
            case Update:
                mDao.updateAll(params);
                break;
            case Custom:
                ids = customOperation(mDao, params);
                break;
        }
        return ids;
    }

    protected long[] customOperation(BaseDao<T> dao, T... params){
        return null;
    }

}
