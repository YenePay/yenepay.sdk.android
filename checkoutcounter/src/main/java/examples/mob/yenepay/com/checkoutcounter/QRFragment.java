package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QRFragment extends Fragment {

    private QrViewModel mViewModel;
    private WebView webView;
    private CheckoutViewModel mCheckoutViewModel;
    private String mQRURI;

    public static QRFragment newInstance() {
        return new QRFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_fragment, container, false);
        webView = view.findViewById(R.id.qrwebView);
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

        mQRURI = "file:///android_asset/html/qr.html#uri=%s&size=%s";

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(QrViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);
        webView.loadUrl(mQRURI);
        // TODO: Use the ViewModel
    }

}
