package examples.mob.yenepay.com.checkoutcounter.ui.checkout;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.model.OrderedItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import examples.mob.yenepay.com.checkoutcounter.QRFragment;
import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.SettingsActivity;
import examples.mob.yenepay.com.checkoutcounter.StoreApp;
import examples.mob.yenepay.com.checkoutcounter.databinding.StoreHeaderBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;
import examples.mob.yenepay.com.checkoutcounter.ui.SimpleItemRecyclerViewAdapter;
import examples.mob.yenepay.com.checkoutcounter.ui.category.ManageCategoriesActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.item.ItemDetailActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.item.ItemDetailFragment;
import examples.mob.yenepay.com.checkoutcounter.ui.item.ItemsFragment;
import examples.mob.yenepay.com.checkoutcounter.ui.item.ManageItemsActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.item.OnItemInteractionListner;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.ManageOrdersActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.OrderedItemClickCallback;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.PaymentStatusFragment;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CheckoutViewModel;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.DeviceActionListener;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.NearPaymentService;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.PaymentService;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.StoreTerminal;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.TerminalStatus;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.WifiAccessPoint;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.WifiConnection;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.WifiServiceSearcher;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CheckoutActivity extends AppCompatActivity implements
        OnCheckoutActionListner,
        OrderedItemClickCallback,
        OnItemInteractionListner,
        WifiP2pManager.ChannelListener,
        WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.PeerListListener,
        DeviceActionListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "CheckoutActivity";

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    public static final int SERVER_PORT = 8988;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public boolean mTwoPane;
    private CheckoutViewModel mViewModel;



    private BroadcastReceiver mReceiver = null;
    private IntentFilter mIntentFilter = new IntentFilter();

    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private WifiP2pInfo info;

    WifiP2pManager.Channel mChannel;
    WifiP2pManager mManager;
    private Toolbar toolbar;
    private FloatingActionButton mQRFab;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;
    private StoreHeaderBinding mNavBinding;

    MainBCReceiver mBRReceiver;
    private IntentFilter filter;

    private int mInterval = 1000; // 1 second by default, can be changed later
    private Handler timeHandler;
    private int timeCounter = 0;
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            // call function to update timer
            timeCounter = timeCounter + 1;
//            ((TextView) findViewById(R.id.TimeBox)).setText("T: " + timeCounter);
            timeHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    WifiServiceSearcher    mWifiServiceSearcher = null;
    WifiAccessPoint        mWifiAccessPoint = null;
    WifiConnection         mWifiConnection = null;
    Boolean serviceRunning = false;

    //change me  to be dynamic!!
    public String CLIENT_PORT_INSTANCE = "38765";
    public String SERVICE_PORT_INSTANCE = "38765";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;

//    GroupOwnerSocketHandler  groupSocket = null;
//    ClientSocketHandler clientSocket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        setUpDrawer(toolbar);
//        mNavBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.store_header, navigationView, false);
//        navigationView.addHeaderView(mNavBinding.getRoot());
//        mNavBinding.setTerminal(StoreApp.getStoreTerminal());
//        mNavBinding.getTerminal().status.observe(this, status -> mNavBinding.invalidateAll());
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mBRReceiver = new MainBCReceiver();
        filter = new IntentFilter();
        filter.addAction(WifiAccessPoint.DSS_WIFIAP_VALUES);
        filter.addAction(WifiAccessPoint.DSS_WIFIAP_SERVERADDRESS);
        filter.addAction(WifiServiceSearcher.DSS_WIFISS_PEERAPINFO);
        filter.addAction(WifiServiceSearcher.DSS_WIFISS_PEERCOUNT);
        filter.addAction(WifiServiceSearcher.DSS_WIFISS_VALUES);
        filter.addAction(WifiConnection.DSS_WIFICON_VALUES);
        filter.addAction(WifiConnection.DSS_WIFICON_STATUSVAL);
        filter.addAction(WifiConnection.DSS_WIFICON_SERVERADDRESS);
//        filter.addAction(ClientSocketHandler.DSS_CLIENT_VALUES);
//        filter.addAction(GroupOwnerSocketHandler.DSS_GROUP_VALUES);


        LocalBroadcastManager.getInstance(this).registerReceiver((mBRReceiver), filter);

        try{
            startGroupOwnerService();
            print_line("","Group socketserver started.");
        }catch (Exception e){
            print_line("", "groupseocket error, :" + e.toString());
        }

        timeHandler  = new Handler();
        mStatusChecker.run();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    CheckoutActivity.PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }

        mQRFab = findViewById(R.id.fab);
        mQRFab.setOnClickListener(view -> {
//                mListner.onAddNewItem();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(!mTwoPane){
                transaction.addToBackStack(null);
            }
            transaction
                    .replace(getDetailContainer(), QRFragment.newInstance())
                    .commit();
            mQRFab.hide();
        });
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        mViewModel = ViewModelProviders.of(this).get(CheckoutViewModel.class);
        initViewModel();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.checkout_container, CheckoutFragment.newInstance())
                .commit();




    }

    private void startServer() {
        if(mWifiConnection != null) {
            mWifiConnection.Stop();
            mWifiConnection = null;
            // should stop etc.
//                        clientSocket = null;
        }
        // make sure services are re-started
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.Stop();
            mWifiAccessPoint = null;
        }
        mWifiAccessPoint = new WifiAccessPoint(this);
        mWifiAccessPoint.Start();

//        if(mWifiServiceSearcher != null){
//            mWifiServiceSearcher.Stop();
//            mWifiServiceSearcher = null;
//        }

        mWifiServiceSearcher = new WifiServiceSearcher(this);
        mWifiServiceSearcher.Start();
    }

    private void toogleState() {
        if(serviceRunning){
            stopP2P();
//            print_line("","Stopped");
        }else{
            serviceRunning = true;
//            print_line("","Started");

            startAccessPoint();

            startSearching();
        }
    }

    private void startSearching() {
        mWifiServiceSearcher = new WifiServiceSearcher(this);
        mWifiServiceSearcher.Start();
    }

    private void startAccessPoint() {
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.Stop();
            mWifiAccessPoint = null;
        }
        mWifiAccessPoint = new WifiAccessPoint(getApplicationContext());
        mWifiAccessPoint.Start();
    }

    private void stopP2P() {
        serviceRunning = false;
        stopAccessPoint();

        stopWifiServiceSearcher();

        stopConnection();
    }

    private void stopConnection() {
        if(mWifiConnection != null) {
            mWifiConnection.Stop();
            mWifiConnection = null;
        }
    }

    private void stopAccessPoint() {
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.Stop();
            mWifiAccessPoint = null;
        }
    }

    private void stopWifiServiceSearcher() {
        if(mWifiServiceSearcher != null){
            mWifiServiceSearcher.Stop();
            mWifiServiceSearcher = null;
        }
    }

    private void setUpDrawer(Toolbar toolbar) {
        Log.d(TAG, "setUpDrawer: called");
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open_desc, R.string.nav_close_desc);
        this.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                onBackPressed();
            }
        });
        drawerLayout.setDrawerListener(this.mDrawerToggle);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.navigationView = (NavigationView) this.findViewById(R.id.navigation_drawer);
        this.navigationView.setNavigationItemSelectedListener(this);



    }

    private void initViewModel() {
        mViewModel.initNewOrder();
        mViewModel.getObservableOrder().observe(this, order -> {
            if(order == null){
                return;
            }
            if(mTwoPane) {
                if(order.isNew()) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, ItemsFragment.newInstance())
                            .commit();
                    mQRFab.show();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, PaymentStatusFragment.newInstance(order.getId()))
                            .commit();
                }
            } else {
                if (!order.isNew()) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.checkout_container);
                    if (fragment instanceof CheckoutFragment) {
                        return;
                    } else {
                        getSupportFragmentManager().popBackStack();
                        getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.checkout_container, PaymentStatusFragment.newInstance(order.getId()))
                                .commit();
                    }
                }
            }
            if(order.isNew()){ mQRFab.show(); } else { mQRFab.hide();}
        });
        mViewModel.setTwoPane(mTwoPane);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION:
                if  (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Coarse location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, StoreManager.ITEMS, mTwoPane));
    }

    @Override
    public void onAddNewItem() {
        loadAddNewItemFragment();
    }

    @Override
    public void onNewOrder() {
        initViewModel();
    }

    @Override
    public void onCancelOrder(CustomerOrder order) {
        order.setStatus(CustomerOrder.STATUS_CANCELED);
        order.setLastStatusDate(new Date().getTime());
        StoreRepository.getInstance(getApplication()).updateOrder(order);
    }

    private void loadAddNewItemFragment() {
        int container = getDetailContainer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!mTwoPane){
            transaction.addToBackStack(null);
        }
        transaction
                .replace(container, ItemsFragment.newInstance())
                .commit();
    }

    private int getDetailContainer() {
        return mTwoPane? R.id.item_detail_container: R.id.checkout_container;
    }

    @Override
    public void onItemSelected(StoreItem item, int quantity) {
//        StoreManager.addOrder(item, quantity);
        mViewModel.addItem(item);
        if(!mTwoPane){
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onItemChangeImageClicked(StoreItem item) {

    }

    @Override
    public void onItemEditClicked(StoreItem item) {

    }

    @Override
    public void onItemSaved(StoreItem item) {

    }

    @Override
    public void onItemEditCancelled(StoreItem item) {

    }

    @Override
    public void onItemDelete(StoreItem item) {

    }

    @Override
    public void onNewItemClicked() {

    }

    private CheckoutFragment getCheckoutFragment() {
        return (CheckoutFragment) getSupportFragmentManager().findFragmentById(R.id.checkout_container);
    }


    public void setIsWifiP2pEnabled(boolean enabled) {

    }

    public void resetData() {

    }

    private void startRegistration() {
        //  Create a string map containing information about your service.
        StoreTerminal storeTerminal = StoreApp.getStoreTerminal();
        Map record = new HashMap();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("terminalName", storeTerminal.terminalName);
        record.put("yenepayCode", storeTerminal.terminalCode);
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("yp2p", "_presence._tcp", record);

        // Add the local service, sending the service info, network channel,
        // and listener that will be used to indicate success or failure of
        // the request.
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
            }

            @Override
            public void onFailure(int arg0) {
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }

    @Override
    public void onChannelDisconnected() {
// we will try once more
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (mManager != null) {
            final QRFragment fragment = (QRFragment) getSupportFragmentManager()
                    .findFragmentById(getDetailContainer());
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(CheckoutActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(CheckoutActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void connect(WifiP2pConfig config) {
//        config.groupOwnerIntent = 15;
//        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onFailure(int i) {
//                Toast.makeText(CheckoutActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
        startAccessPoint();
//        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
//            }
//
//            @Override
//            public void onFailure(int reason) {
//                Toast.makeText(CheckoutActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void disconnect() {
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "Group Removed successfully");
            }

        });
    }

    @Override
    public void dicoverPeers() {
        toogleState();
//        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                Toast.makeText(CheckoutActivity.this, "Discovery Initiated",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(int reasonCode) {
//                Toast.makeText(CheckoutActivity.this, "Discovery Failed : " + reasonCode,
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        this.info = wifiP2pInfo;
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
//            new PaymentServerAsyncTask(this)
//                    .execute();
//            StoreServer server =AAAA
//            startRegistration();
//            mViewModel.setP2PInfo(wifiP2pInfo);
//            startGroupOwnerService();
            Snackbar.make(toolbar, "GroupOwner connected", Snackbar.LENGTH_LONG).show();
        } else if (wifiP2pInfo.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            String msg = "Client Connected";
            Snackbar.make(toolbar, msg, Snackbar.LENGTH_LONG).show();
            // Allow user to pick an image from Gallery or other
            // registered apps
//            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);


//            testPayment();

//            mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
//            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
//                    .getString(R.string.client_text));
        }
    }

    private void startGroupOwnerService() {
        Intent serviceIntent = new Intent(this, PaymentService.class);
        serviceIntent.setAction(PaymentService.ACTION_START_SERVER);
//            serviceIntent.putExtra(NearPaymentService.EXTRAS_PAYMENT_RESPONSE, response);
        serviceIntent.putExtra(PaymentService.EXTRAS_HOST_ADDRESS,
                "");
        serviceIntent.putExtra(PaymentService.EXTRAS_HOST_PORT, SERVER_PORT);
//        startService(serviceIntent);
        getApplicationContext().startService(serviceIntent);
//        ContextCompat.startForegroundService(StoreApp.getContext(), serviceIntent);
        StoreApp.setTerminalStatus(TerminalStatus.Online);
    }

    private void testPayment() {
        testSendMessage();
    }

    public void updateThisDevice(WifiP2pDevice device) {

    }

    @Override
    public void onResume() {
        super.onResume();
//        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
//        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.checkout_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_manage_orders:
                startActivity(new Intent(this, ManageOrdersActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_manage_categories:
                startActivity(new Intent(this, ManageCategoriesActivity.class));
                return true;
            case R.id.menu_manage_Items:
                startActivity(new Intent(this, ManageItemsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getDetailContainer());
        if(fragment instanceof WifiP2pManager.PeerListListener){
            ((WifiP2pManager.PeerListListener) fragment).onPeersAvailable(wifiP2pDeviceList);
        }
    }

    @Override
    public void onProductClick(OrderedItem item) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getItemId());
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getItemId());

            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return onOptionsItemSelected(menuItem);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public class PaymentServerAsyncTask extends AsyncTask<Void, Void, PaymentResponse> {

        private Context context;

        /**
         * @param context
         */
        public PaymentServerAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected PaymentResponse doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(CheckoutActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(CheckoutActivity.TAG, "Server: connection done");
//                final File f = new File(context.getExternalFilesDir("received"),
//                        "wifip2pshared-" + System.currentTimeMillis()
//                                + ".jpg");
//
//                File dirs = new File(f.getParent());
//                if (!dirs.exists())
//                    dirs.mkdirs();
//                f.createNewFile();

                Log.d(CheckoutActivity.TAG, "server: accepting response ");
                InputStream inputstream = client.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputstream);
                PaymentResponse paymentResponse = null;
                try {
                    paymentResponse = (PaymentResponse) objectInputStream.readObject();
                    Log.d(TAG, "doInBackground: Payment response found");
                    Log.d(TAG, "doInBackground: " + paymentResponse);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
//                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return paymentResponse;
            } catch (IOException e) {
                Log.e(CheckoutActivity.TAG, e.getMessage());
                return null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(PaymentResponse result) {
            if (result != null) {
//                statusText.setText("File copied - " + result);

                CustomerOrder order = StoreManager.generateCustomerOrder();
                if(order != null){
//                    order.setResponse(result);
                    mViewModel.setStatus("Completed");
                    mViewModel.loadItems();
                }
//                File recvFile = new File(result);
//                Uri fileUri = FileProvider.getUriForFile(
//                        context,
//                        "yenepay.sdk.android.wifiP2P.fileprovider",
//                        recvFile);
//                Intent intent = new Intent();
//                intent.setAction(android.content.Intent.ACTION_VIEW);
//                intent.setDataAndType(fileUri, "image/*");
//                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                context.startActivity(intent);
            }

        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
//            statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);

            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(CheckoutActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // NearPaymentService.
        Uri uri = data.getData();
//        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
//        statusText.setText("Sending: " + uri);
        testSendMessage();
    }

    private void testSendMessage() {
        PaymentResponse response = new PaymentResponse();
        response.setMerchantOrderId(UUID.randomUUID().toString());
//        Log.d(TAG, "Intent----------- " + uri);
        sendPaymentReponseBack(response);
    }

    private void sendPaymentReponseBack(PaymentResponse response) {
        Intent serviceIntent = new Intent(this, NearPaymentService.class);
        serviceIntent.setAction(NearPaymentService.ACTION_PAYMENT_RECIEPT);
        serviceIntent.putExtra(NearPaymentService.EXTRAS_PAYMENT_RESPONSE, response);
        serviceIntent.putExtra(NearPaymentService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(NearPaymentService.EXTRAS_GROUP_OWNER_PORT, 8988);
        ContextCompat.startForegroundService(this, serviceIntent);
        Log.d(TAG, "sendPaymentReponseBack: sent to server");
        Log.d(TAG, "sendPaymentReponseBack: " + response);
    }

    private class MainBCReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiAccessPoint.DSS_WIFIAP_VALUES.equals(action)) {
                String s = intent.getStringExtra(WifiAccessPoint.DSS_WIFIAP_MESSAGE);
                print_line("AP", s);

            }else if (WifiAccessPoint.DSS_WIFIAP_SERVERADDRESS.equals(action)) {
                InetAddress address = (InetAddress)intent.getSerializableExtra(WifiAccessPoint.DSS_WIFIAP_INETADDRESS);
                print_line("AP", "inet address" + address.getHostAddress());

            }else if (WifiServiceSearcher.DSS_WIFISS_VALUES.equals(action)) {
                String s = intent.getStringExtra(WifiServiceSearcher.DSS_WIFISS_MESSAGE);
                print_line("SS", s);

            }else if (WifiServiceSearcher.DSS_WIFISS_PEERCOUNT.equals(action)) {
                int s = intent.getIntExtra(WifiServiceSearcher.DSS_WIFISS_COUNT, -1);
                print_line("SS", "found " + s + " peers");
//                mySpeech.speak(s+ " peers discovered.");

            }else if (WifiServiceSearcher.DSS_WIFISS_PEERAPINFO.equals(action)) {
                String s = intent.getStringExtra(WifiServiceSearcher.DSS_WIFISS_INFOTEXT);

                String[] separated = s.split(":");
                print_line("SS", "found SSID:" + separated[0] + ", pwd:"  + separated[1]+ "IP: " + separated[2]);

                if(mWifiConnection == null) {
//                    if(mWifiAccessPoint != null){
//                        mWifiAccessPoint.Stop();
//                        mWifiAccessPoint = null;
//                    }
//                    if(mWifiServiceSearcher != null){
//                        mWifiServiceSearcher.Stop();
//                        mWifiServiceSearcher = null;
//                    }

                    final String networkSSID = separated[0];
                    final String networkPass = separated[1];
                    final String ipAddress   = separated[2];

//                    mWifiConnection = new WifiConnection(CheckoutActivity.this,networkSSID,networkPass);
//                    mWifiConnection.SetInetAddress(ipAddress);
                    StoreApp.setStoreApInfo(networkSSID, networkPass, ipAddress);
                    mViewModel.setDeviceName(networkSSID);
                    mViewModel.setPassPhrase(networkPass);
                    mViewModel.setDeviceAddress(ipAddress);


//                    mySpeech.speak("found accesspoint");
                }
            }else if (WifiConnection.DSS_WIFICON_VALUES.equals(action)) {
                String s = intent.getStringExtra(WifiConnection.DSS_WIFICON_MESSAGE);
                print_line("CON", s);

            }else if (WifiConnection.DSS_WIFICON_SERVERADDRESS.equals(action)) {
                int addr = intent.getIntExtra(WifiConnection.DSS_WIFICON_INETADDRESS, -1);
                print_line("COM", "IP" + Formatter.formatIpAddress(addr));

//                if(clientSocket == null &&  mWifiConnection != null) {
//                    String IpToConnect = mWifiConnection.GetInetAddress();
//                    print_line("","Starting client socket conenction to : " + IpToConnect);
//                    clientSocket = new ClientSocketHandler(myHandler,IpToConnect, Integer.parseInt(CLIENT_PORT_INSTANCE), that);
//                    clientSocket.start();
//                }
            }else if (WifiConnection.DSS_WIFICON_STATUSVAL.equals(action)) {
                int status = intent.getIntExtra(WifiConnection.DSS_WIFICON_CONSTATUS, -1);

                String conStatus = "";
                if(status == WifiConnection.ConectionStateNONE) {
                    conStatus = "NONE";
                }else if(status == WifiConnection.ConectionStatePreConnecting) {
                    conStatus = "PreConnecting";
                }else if(status == WifiConnection.ConectionStateConnecting) {
                    conStatus = "Connecting";
//                    mySpeech.speak("Accesspoint connected");
                }else if(status == WifiConnection.ConectionStateConnected) {
                    conStatus = "Connected";
                }else if(status == WifiConnection.ConectionStateDisconnected) {
                    conStatus = "Disconnected";
//                    mySpeech.speak("Accesspoint Disconnected");
                    if(mWifiConnection != null) {
                        mWifiConnection.Stop();
                        mWifiConnection = null;
                        // should stop etc.
//                        clientSocket = null;
                    }
                    // make sure services are re-started
                    if(mWifiAccessPoint != null){
                        mWifiAccessPoint.Stop();
                        mWifiAccessPoint = null;
                    }
                    mWifiAccessPoint = new WifiAccessPoint(CheckoutActivity.this);
                    mWifiAccessPoint.Start();

//                    if(mWifiServiceSearcher != null){
//                        mWifiServiceSearcher.Stop();
//                        mWifiServiceSearcher = null;
//                    }
//
//                    mWifiServiceSearcher = new WifiServiceSearcher(CheckoutActivity.this);
//                    mWifiServiceSearcher.Start();
                }

                print_line("COM", "Status " + conStatus);
            }//else if (ClientSocketHandler.DSS_CLIENT_VALUES.equals(action)) {
//                String s = intent.getStringExtra(ClientSocketHandler.DSS_CLIENT_MESSAGE);
//                print_line("Client", s);
//
//            }else if (GroupOwnerSocketHandler.DSS_GROUP_VALUES.equals(action)) {
//                String s = intent.getStringExtra(GroupOwnerSocketHandler.DSS_GROUP_MESSAGE);
//                print_line("Group", s);
//
//            }
        }
    }

    public void print_line(String who,String line) {
        timeCounter = 0;
        Log.d(TAG, "print_line: " + who + line);
//        ((TextView)findViewById(R.id.debugdataBox)).append(who + " : " + line + "\n");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWifiConnection != null) {
            mWifiConnection.Stop();
            mWifiConnection = null;
        }
        if(mWifiAccessPoint != null){
            mWifiAccessPoint.Stop();
            mWifiAccessPoint = null;
        }

        if(mWifiServiceSearcher != null){
            mWifiServiceSearcher.Stop();
            mWifiServiceSearcher = null;
        }

        timeHandler.removeCallbacks(mStatusChecker);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBRReceiver);
    }
}
