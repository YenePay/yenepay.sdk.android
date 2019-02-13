package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.model.Order;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;
import examples.mob.yenepay.com.checkoutcounter.ui.PeerDeviceRecyclerViewAdapter;
import examples.mob.yenepay.com.checkoutcounter.ui.checkout.CheckoutActivity;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CheckoutViewModel;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.QrViewModel;
import examples.mob.yenepay.com.checkoutcounter.wifiP2P.DeviceActionListener;

public class QRFragment extends Fragment implements
        WifiP2pManager.PeerListListener,
        PeerDeviceRecyclerViewAdapter.OnDeviceSelectedListner {

    private QrViewModel mViewModel;
    private WebView webView;
    private CheckoutViewModel mCheckoutViewModel;
    private String mQRURI;
    private DeviceActionListener mListner;
    private Button mBtnConnect;
    private RecyclerView mPeers;
    private WifiP2pDevice mSelectedDevice;

    public static QRFragment newInstance() {
        return new QRFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_fragment, container, false);
        webView = view.findViewById(R.id.qrwebView);

        mBtnConnect = view.findViewById(R.id.btn_connect);
        WebSettings settings = this.webView.getSettings();
        settings.setBuiltInZoomControls(false);
        settings.setLoadWithOverviewMode(false);
        settings.setUseWideViewPort(false);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){

            public void onPageFinished(WebView webView, String string2) {
            }

            public void onPageStarted(WebView webView, String string2, Bitmap bitmap) {
            }

            public void onReceivedError(WebView object, int n, String string2, String string3) {
                super.onReceivedError(object, n, string2, string3);
//                object = new StringBuilder();
//                ((StringBuilder)object).append("Error code : ");
//                ((StringBuilder)object).append(n);
//                ((StringBuilder)object).append(", descritpion :");
//                ((StringBuilder)object).append(string2);
                Log.e((String)"Error loading page", string2);
            }
        });
        mPeers = view.findViewById(R.id.peers_list);
        mQRURI = "file:///android_asset/html/qr.html#uri=%s&size=%s";
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListner.dicoverPeers();
//                mListner.connect(null);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(QrViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);
        Order customerOrder = mCheckoutViewModel.order.get();
        customerOrder.initTotals();
        mCheckoutViewModel.setDeviceAddress(null);
        mCheckoutViewModel.getDeviceAddress().observe(this, address -> {
            if(!TextUtils.isEmpty(address) && address != "Not Started") {
                String ssid = mCheckoutViewModel.getDeviceName().getValue();
                String passPhrase = mCheckoutViewModel.getPassPhrase().getValue();
                loadQRWebView(customerOrder, address, ssid, passPhrase);
            } else {
//                mListner.connect(null);
            }
        });

        mListner.connect(null);
        // TODO: Use the ViewModel
    }

    private void loadQRWebView(CustomerOrder customerOrder, String address, String ssid, String passPhrase) {
        String size = mCheckoutViewModel.isTwoPane().getValue()? "500" : "300";
        Toast.makeText(QRFragment.this.getContext(), "Group Address - " + address, Toast.LENGTH_LONG).show();
        webView.loadUrl(String.format(mQRURI,
                customerOrder.getQRPaymentUri(address, CheckoutActivity.SERVER_PORT, ssid, passPhrase), size));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListner = (DeviceActionListener) context;
        } catch (Exception e){
            throw new IllegalArgumentException("Activity must implement DeviceActionListener interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
        bindDevicesList(wifiP2pDeviceList);
    }

    private void bindDevicesList(WifiP2pDeviceList wifiP2pDeviceList) {
        List<WifiP2pDevice> list = new ArrayList<>();
        list.addAll(wifiP2pDeviceList.getDeviceList());
        mPeers.setAdapter(new PeerDeviceRecyclerViewAdapter(this, list, mCheckoutViewModel.isTwoPane().getValue()));
    }

    @Override
    public void onDeviceSelected(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mSelectedDevice = device;
        mListner.connect(config);
    }


    public WifiP2pDevice getDevice() {
        return mSelectedDevice;
    }
}
