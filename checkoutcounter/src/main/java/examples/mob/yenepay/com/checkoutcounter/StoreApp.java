package examples.mob.yenepay.com.checkoutcounter;

import android.app.Application;
import android.content.Context;

public class StoreApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        StoreApp.context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
