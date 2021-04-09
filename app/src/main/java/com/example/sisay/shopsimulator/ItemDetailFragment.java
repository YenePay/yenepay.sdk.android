package com.example.sisay.shopsimulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.sisay.shopsimulator.store.StoreManager;
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
    private StoreManager.DummyItem mItem;
    private EditText merchantCodeText;
    private ItemDetailActionListner mListner;

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
            mItem = StoreManager.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

//            Activity activity = this.getActivity();
//
//

        }
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
            ((ImageView)rootView.findViewById(R.id.item_image)).setImageResource(mItem.imageResId);
            ((TextView) rootView.findViewById(R.id.item_content)).setText(mItem.content);
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.details);
            ((TextView) rootView.findViewById(R.id.item_price)).setText(String.format(getString(R.string.money_amount_format), mItem.price));
            merchantCodeText = (EditText)rootView.findViewById(R.id.editMerchantCode);
            ((TextView) rootView.findViewById(R.id.item_currency)).setText(Utils.getStoreCurrency(requireContext()));
            rootView.findViewById(R.id.btnPayViaWeb).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListner != null){
                        mListner.onItemWebCheckoutClicked(mItem);
                    }
                }
            });
            rootView.findViewById(R.id.btn_checkout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListner != null){
                        mListner.onItemCheckoutClicked(mItem);
                    }
                }
            });
            rootView.findViewById(R.id.btn_add_to_cart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListner != null){
                        mListner.onAddToCartClicked(mItem);
                    }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ItemDetailActionListner){
            mListner = (ItemDetailActionListner)context;
        } else {
            throw new IllegalArgumentException("Activity must implement ItemDetailActionListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListner = null;
    }

    public interface ItemDetailActionListner {
        void onItemCheckoutClicked(StoreManager.DummyItem item);
        void onItemWebCheckoutClicked(StoreManager.DummyItem item);
        void onAddToCartClicked(StoreManager.DummyItem item);
    }
}
