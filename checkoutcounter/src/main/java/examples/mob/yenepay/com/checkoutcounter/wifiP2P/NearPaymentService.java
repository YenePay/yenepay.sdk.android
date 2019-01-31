// Copyright 2011 Google Inc. All Rights Reserved.

package examples.mob.yenepay.com.checkoutcounter.wifiP2P;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yenepaySDK.PaymentResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import examples.mob.yenepay.com.checkoutcounter.CheckoutActivity;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class NearPaymentService extends IntentService {
    private static final String TAG = "NearPaymentService";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_PAYMENT_RECIEPT = "com.checkoutcounter.wifiP2P.ACTION_PAYMENT_RECIEPT";
    public static final String EXTRAS_PAYMENT_RESPONSE = "payment_response";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public NearPaymentService(String name) {
        super(name);
    }

    public NearPaymentService() {
        super("NearPaymentService");
    }

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_PAYMENT_RECIEPT)) {
            PaymentResponse response = (PaymentResponse) intent.getExtras().getSerializable(EXTRAS_PAYMENT_RESPONSE);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
//                ContentResolver cr = context.getContentResolver();

                InputStream is = null;
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream);
                try {
                    objectOutputStream.writeObject(response);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
//                CheckoutActivity.copyFile(is, stream);
                Log.d(TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }
}
