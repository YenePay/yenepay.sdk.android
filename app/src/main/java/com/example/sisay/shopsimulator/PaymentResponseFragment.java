package com.example.sisay.shopsimulator;

import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yenepaySDK.PaymentResponse;


public class PaymentResponseFragment extends BottomSheetDialogFragment {

    public static final String EXTRA_PAYMENT_RESPONSE = "extra_payment_response";
    public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";
    private PaymentResponse mResponse;
    private String mErrorMessage;
    private TextView mPaymentStatus;
    private TextView mJsonDump;

    public static PaymentResponseFragment getInstance(PaymentResponse response){
        PaymentResponseFragment fragment = new PaymentResponseFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PAYMENT_RESPONSE, response);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaymentResponseFragment getInstance(String errorMessage) {
        PaymentResponseFragment fragment = new PaymentResponseFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ERROR_MESSAGE, errorMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_response, container, false);
        mPaymentStatus = view.findViewById(R.id.txtPaymentStatus);
        mJsonDump = view.findViewById(R.id.txtObjDump);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState == null){
            if(getArguments().containsKey(EXTRA_ERROR_MESSAGE)){
                mErrorMessage = getArguments().getString(EXTRA_ERROR_MESSAGE);
            } else {
                mResponse = (PaymentResponse) getArguments().getSerializable(EXTRA_PAYMENT_RESPONSE);
            }
        } else {
            if(savedInstanceState.containsKey(EXTRA_ERROR_MESSAGE)){
                mErrorMessage = savedInstanceState.getString(EXTRA_ERROR_MESSAGE);
            } else {
                mResponse = (PaymentResponse) savedInstanceState.getSerializable(EXTRA_PAYMENT_RESPONSE);
            }
        }

        if(mResponse != null){
            mPaymentStatus.setText(mResponse.getStatusText());
            mJsonDump.setText(mResponse.toString());
        } else {
            mPaymentStatus.setText(R.string.error_occurred);
            mJsonDump.setText(mErrorMessage);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_PAYMENT_RESPONSE, mResponse);
        outState.putString(EXTRA_ERROR_MESSAGE, mErrorMessage);
    }
}
