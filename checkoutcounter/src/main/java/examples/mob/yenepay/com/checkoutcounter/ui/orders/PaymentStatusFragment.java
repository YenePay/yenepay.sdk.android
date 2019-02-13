package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.FragmentPaymentStatusBinding;
import examples.mob.yenepay.com.checkoutcounter.ui.checkout.OnCheckoutActionListner;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CheckoutViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCheckoutActionListner} interface
 * to handle interaction events.
 * Use the {@link PaymentStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_ORDER_ID = "extra_order_id";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnCheckoutActionListner mListener;
    private FragmentPaymentStatusBinding mBinding;

    public PaymentStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orderId Parameter 1.
     * @return A new instance of fragment PaymentStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentStatusFragment newInstance(String orderId) {
        PaymentStatusFragment fragment = new PaymentStatusFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(EXTRA_ORDER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_status, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final CheckoutViewModel mViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);
        mBinding.setCheckout(mViewModel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCheckoutActionListner) {
            mListener = (OnCheckoutActionListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStatusFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
