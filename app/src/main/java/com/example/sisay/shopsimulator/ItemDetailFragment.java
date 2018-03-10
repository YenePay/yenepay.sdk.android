package com.example.sisay.shopsimulator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.sisay.shopsimulator.dummy.DummyContent;
import com.yenepaySDK.Constants;
import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;
import com.yenepaySDK.YenepayCheckOutIntentAction;
import com.yenepaySDK.model.OrderedItem;

import java.util.UUID;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    private static final String TAG = "ItemDetailFragment";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private EditText merchantCodeText;
    private CollapsingToolbarLayout appBarLayout;
    private static final String WEB_PAY_FORMAT = "https://checkout.yenepay.com/Home/Process/?ItemName=%s&ItemId=%s&UnitPrice=%.2f&Quantity=%d&Process=Express&SuccessUrl=&IPNUrl=&MerchantId=%s";
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }

            FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItem != null) {
                        PaymentOrderManager paymentMgr = new PaymentOrderManager(
                                getMerchantCode(),
                                UUID.randomUUID().toString());
                        //ComponentName info = new ComponentName("com.yenepay.mob.YenepayApp", "MainActivity");
                        paymentMgr.addItem(new OrderedItem(mItem.id, mItem.content, 1, mItem.price));
                        Intent yenePay = paymentMgr.generatePaymentArguments();
                        yenePay.setAction(YenepayCheckOutIntentAction.YENEPAY_INTENT_FILTER_ACTION_CHECKOUT);
                        yenePay.addCategory("android.intent.category.DEFAULT");
                        yenePay.setType("*/*");
                        ((ItemDetailActivity)getActivity()).checkOutItem(yenePay);
//                        yenePay.setClassName("com.yenepay.mob.YenepayApp", "MainActivity");
                        //startActivityForResult(yenePay, 99);

                    } else {
                        Snackbar.make(view, "Please select an Item before paying", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
            });
        }
    }
    private String getMerchantCode(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString("example_text", null);
        /*else if(merchantCodeText != null && !TextUtils.isEmpty(merchantCodeText.getText().toString())){
            return merchantCodeText.getText().toString();
        }*/
        //return Constants.YENEPAY_MERCHANT_CODE;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        paymentStatus = (TextView)rootView.findViewById(R.id.txtPaymentStatus);
        objDump = (TextView)rootView.findViewById(R.id.txtObjDump);
        paymentInfoContainer = (LinearLayout)rootView.findViewById(R.id.paymentInfoContainer);
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_content)).setText(mItem.content);
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
            ((TextView) rootView.findViewById(R.id.item_price)).setText(String.format(getString(R.string.money_amount_format), mItem.price));
            merchantCodeText = (EditText)rootView.findViewById(R.id.editMerchantCode);
            appBarLayout.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            appBarLayout.setBackground(ContextCompat.getDrawable(getActivity(), mItem.largeImageResId));
            ((Button)rootView.findViewById(R.id.btnPayViaWeb)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = String.format(WEB_PAY_FORMAT, mItem.content, mItem.id, mItem.price, 1, getMerchantCode());
//                    Uri uri = Uri.parse(URLDecoder.decode(successReturnUrl));
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    startActivity(intent);
                }
            });
            populatePaymentInfo();
        }



        return rootView;
    }
    private PaymentResponse response;
    TextView paymentStatus;
    TextView objDump;
    LinearLayout paymentInfoContainer;
    public void setPaymentResponse(PaymentResponse response) {
        this.response = response;
        populatePaymentInfo();
    }

    private void populatePaymentInfo() {
        if(response != null){
            paymentInfoContainer.setVisibility(View.VISIBLE);
            paymentStatus.setText(response.getStatusText());
            objDump.setText(response.toString());
            merchantCodeText.setText(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("example_text", null));

        }
    }
}
