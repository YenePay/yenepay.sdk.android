package examples.mob.yenepay.com.checkoutcounter;

import android.app.Application;
import android.content.Context;

import examples.mob.yenepay.com.checkoutcounter.wifiP2P.StoreTerminal;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.TerminalStatus;

public class StoreApp extends Application {
    private static Context context;
    private static StoreTerminal storeTerminal;
    private AppExecutors mAppExecutors;

    public static void setStoreApInfo(String networkSSID, String networkPass, String ipAddress) {
        if(storeTerminal == null){
            storeTerminal = new StoreTerminal();
        }
        storeTerminal.ssid = networkSSID;
        storeTerminal.networkPass = networkPass;
        storeTerminal.host = ipAddress;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StoreApp.context = getApplicationContext();
        storeTerminal = new StoreTerminal();
    }

    public static StoreTerminal getStoreTerminal() {
        return storeTerminal;
    }

    public static void setTerminalStatus(TerminalStatus status){
        storeTerminal.setStatus(status);
    }

    public static Context getContext() {
        return context;
    }
}
