package examples.mob.yenepay.com.checkoutcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiFiDirectActivity extends AppCompatActivity {
    private static final String TAG = "WiFiDirectActivity";

    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    TextView mStatus;
    Button mCreateGroupButton, mConnectButton;
    private ListView mPeers;

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    private final IntentFilter intentFilter = new IntentFilter();


    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                // If an AdapterView is backed by this data, notify it
                // of the change. For instance, if you have a ListView of
                // available peers, trigger an update.
//                getListAdapter().notifyDataSetChanged();
                reloadPeers();

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {
                Log.d(WiFiDirectActivity.TAG, "No devices found");
                showMessage("No devices found");
                return;
            } else {
                showMessage(peers.size() + " Peers found");
            }
        }
    };

    private void reloadPeers() {
        mPeers.setAdapter(new WiFiPeerListAdapter(WiFiDirectActivity.this, 0, -1, peers));
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            // InetAddress from WifiP2pInfo struct.
            //String groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();

            // After the group negotiation, we can determine the group owner
            // (server).
            if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.a
                // One common case is creating a group owner thread and accepting
                // incoming connections.
//                Server server = new Server(WiFiDirectActivity.this);
                showMessage("Group Owner Connected");
//                requestGroupInfo();
            } else if (wifiP2pInfo.groupFormed) {
                // The other device acts as the peer (client). In this case,
                // you'll want to create a peer thread that connects
                // to the group owner.
                showMessage("Client Connected");
//                requestGroupInfo();
            }
        }
    };


    private WiFiPeerListAdapter getListAdapter() {
        return (WiFiPeerListAdapter)mPeers.getAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wi_fi_direct);
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);




        mPeers = findViewById(R.id.lst_peers);
        mPeers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pDevice item = getListAdapter().getItem(i);
                connect(item);
            }
        });
        reloadPeers();
        mStatus = findViewById(R.id.txt_group_name);
        mConnectButton = findViewById(R.id.btn_connect);
        mCreateGroupButton = findViewById(R.id.btn_create_group);

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                connect(peers.get(0));
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);

            }
        });
        mCreateGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initGroup();
            }
        });

        findViewById(R.id.btn_discover_peers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });

        findViewById(R.id.btn_group_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGroupInfo();
            }
        });


        registerReceiver(mPaymentBroadcastReceiver, intentFilter);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
//        discoverPeers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPaymentBroadcastReceiver);
    }

    private void initGroup(){
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Device is ready to accept incoming connections from peers.
                showMessage("P2P group creation onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WiFiDirectActivity.this, "P2P group creation failed. Retry.",
                        Toast.LENGTH_SHORT).show();
                showMessage("P2P group creation failed. Retry.");
            }
        });
    }

    private void requestGroupInfo(){
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                String groupPassword = group.getPassphrase();
                Log.d(TAG, "onGroupInfoAvailable: " + group);
//                Toast.makeText(WiFiDirectActivity.this, group.toString(), Toast.LENGTH_LONG);
                showMessage(group.toString());
            }
        });
    }

    private void connect(WifiP2pDevice device){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
//                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
                showMessage("Connect failed. Retry.");
            }
        });
    }

    private void discoverPeers(){
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank. Code for peer discovery goes in the
                // onReceive method, detailed below.
                showMessage("Discover Peers Initiative onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                showMessage("Discover Peers Initiative Failed");
            }
        });
    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        Map record = new HashMap();
        record.put("listenport", String.valueOf(4556));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                Log.d(TAG, "onSuccess: Service Added");
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                Log.d(TAG, "onSuccess: Service Failed");
            }
        });
    }
    private class WiFiPeerListAdapter extends ArrayAdapter<WifiP2pDevice> {
        public WiFiPeerListAdapter(Context context, int resource, int textViewResourceId, List<WifiP2pDevice> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView,ViewGroup parent) {
            View view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            WifiP2pDevice device = getItem(position);
            ((TextView)view.findViewById(android.R.id.text1)).setText(device.deviceName + " (" + getDeviceStatus(device.status) + ")");
            return view;
        }


    }
    private String getDeviceStatus(int deviceStatus) {
        Log.d(TAG, "Peer status :" + deviceStatus);
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }
    private void showMessage(String message){
        Snackbar.make(mStatus, message, Snackbar.LENGTH_LONG).show();
    }

    private BroadcastReceiver mPaymentBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Determine if Wifi P2P mode is enabled or not, alert the Activity.
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                activity.setIsWifiP2pEnabled(true);
                    mStatus.setText("On");
                    Log.d(TAG, "onReceive: WifiP2pManager.WIFI_P2P_STATE_ENABLED - true");
                    showMessage("onReceive: WifiP2pManager.WIFI_P2P_STATE_ENABLED - true");
                } else {
                    mStatus.setText("Off");
                    Log.d(TAG, "onReceive: WifiP2pManager.WIFI_P2P_STATE_ENABLED - false");
                    showMessage("onReceive: WifiP2pManager.WIFI_P2P_STATE_ENABLED - false");
//                activity.setIsWifiP2pEnabled(false);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (mManager != null) {
                    mManager.requestPeers(mChannel, peerListListener);
                }
                Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
                showMessage("P2P peers changed");
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {
                    // We are connected with the other device, request connection
                    // info to find group owner IP
                    showMessage("WIFI_P2P_CONNECTION_CHANGED_ACTION - Connected");
                    mManager.requestConnectionInfo(mChannel, connectionInfoListener);
                } else {
                    showMessage("WIFI_P2P_CONNECTION_CHANGED_ACTION - Disconneted");
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                reloadPeers();
                WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                showMessage("WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                showMessage(device.deviceName + " - " + getDeviceStatus(device.status));
//            DeviceListFragment fragment = (DeviceListFragment) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
//                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
//
            }
        }
    };
}
