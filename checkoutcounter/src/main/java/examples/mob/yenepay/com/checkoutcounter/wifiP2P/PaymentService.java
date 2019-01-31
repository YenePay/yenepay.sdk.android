package examples.mob.yenepay.com.checkoutcounter.wifiP2P;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.IBinder;
import android.util.Log;

import com.yenepaySDK.PaymentResponse;

import java.io.IOException;
import java.net.Socket;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;

public class PaymentService extends IntentService {
    private static final String TAG = "PaymentService";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_START_SERVER = "com.checkoutcounter.wifiP2P.ACTION_START_SERVER";
    public static final String EXTRAS_HOST_ADDRESS = "server_host";
    public static final String EXTRAS_HOST_PORT = "server_port";
    private StoreServer mServer;

    public PaymentService() {
        super("PaymentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_START_SERVER)) {
//            PaymentResponse response = (PaymentResponse) intent.getExtras().getSerializable(EXTRAS_PAYMENT_RESPONSE);
            String host = intent.getExtras().getString(EXTRAS_HOST_ADDRESS);
            int port = intent.getExtras().getInt(EXTRAS_HOST_PORT);
            if(mServer == null || mServer.getListeningPort() != port){
                mServer = new StoreServer(host, port);

            } else if (!mServer.isAlive()){
                mServer.stop();
            }
            try {
                mServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            ServerRunner.executeInstance(mServer);
        }
    }
}
