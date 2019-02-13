package examples.mob.yenepay.com.checkoutcounter.ui.checkout;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yenepaySDK.model.OrderedItem;

import java.util.ArrayList;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.CheckoutSalesBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.ui.OrderItemRecyclerViewAdapter;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CheckoutViewModel;

public class CheckoutFragment extends Fragment {

    public static final String EXTRA_ORDER_ID = "extra_order_id";
    private CheckoutViewModel mViewModel;
    private View mRecyclerView;
    private OnCheckoutActionListner mListner;
    private TextView mItemsCount;
    private TextView mSubTotal;
    private TextView mDiscount;
    private TextView mTax;
    private TextView mHandlingFee;
    private TextView mShippingFee;
    private TextView mGrandTotal;
    private TextView mStatus;
    private CheckoutSalesBinding mBinding;
    private OrderItemRecyclerViewAdapter mOrderedItemsAdapter;

    public static CheckoutFragment newInstance() {
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        checkoutFragment.setArguments(new Bundle());
        return checkoutFragment;
    }

    public static CheckoutFragment newInstance(String orderId) {
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        checkoutFragment.setArguments(new Bundle());
        return checkoutFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.checkout_sales, container, false);
        mOrderedItemsAdapter = new OrderItemRecyclerViewAdapter(mProductClickCallback);
        mBinding.orderDetail.itemList.setAdapter(mOrderedItemsAdapter);
//        mRecyclerView = view.findViewById(R.id.item_list);
//        mItemsCount = view.findViewById(R.id.txt_items_count);
//        mSubTotal = view.findViewById(R.id.txt_subtotal);
//        mDiscount = view.findViewById(R.id.txt_discount);
//        mTax = view.findViewById(R.id.txt_tax);
//        mHandlingFee = view.findViewById(R.id.txt_handling);
//        mShippingFee = view.findViewById(R.id.txt_shipping);
//        mGrandTotal = view.findViewById(R.id.txt_total);
//        mStatus = view.findViewById(R.id.txt_group_name);
//        FloatingActionButton fab = view.findViewById(R.id.fab_add);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListner.onAddNewItem();
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////                getActivity().getSupportFragmentManager().beginTransaction()
////                        .addToBackStack(null)
////                        .replace(R.id.checkout_container, ItemsFragment.newInstance())
////                        .commit();
//            }
//        });
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final CheckoutViewModel mViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);
        String orderId = getArguments().getString(EXTRA_ORDER_ID);
        if(!TextUtils.isEmpty(orderId)){
            mViewModel.setOrderId(orderId);
        }
//        assert mRecyclerView != null;
        mBinding.setViewModel(mViewModel);
        mBinding.setCallback(mProductClickCallback);
        mViewModel.getObservableOrder().observe(this, order -> {
            mViewModel.setOrder(order);
            if(order != null) {
                mOrderedItemsAdapter.setItems(order.orderedItems);
            } else {
                mOrderedItemsAdapter.setItems(new ArrayList<>());
            }

//            CustomerOrder order = StoreManager.generateCustomerOrder();
//            mItemsCount.setText(String.format("%d Items", order.getTotalQuantity()));
//            mSubTotal.setText(Util.getFormattedAmt(order.getItemsTotal()));
//            mDiscount.setText("(" + Util.getFormattedAmt(order.getDiscount()) + ")");
//            mHandlingFee.setText(Util.getFormattedAmt(order.getHandlingFee()));
//            mTax.setText(Util.getFormattedAmt(order.getTax()));
//            mShippingFee.setText(Util.getFormattedAmt(order.getShippingFee()));
//            mGrandTotal.setText(Util.getFormattedAmt(order.getGrandTotal()));
//            mStatus.setText(mViewModel.getStatus().getValue());
//            setupRecyclerView((RecyclerView)mRecyclerView);
        });
//        setupRecyclerView((RecyclerView) mRecyclerView);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListner = (OnCheckoutActionListner) context;
        } catch (Exception e){
            throw new IllegalArgumentException("Activitis must implement OnCheckoutActionListner");
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(mOrderedItemsAdapter);
    }

    private final OnCheckoutActionListner mProductClickCallback = new OnCheckoutActionListner() {
        @Override
        public void onAddNewItem() {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                mListner.onAddNewItem();
            }
        }

        @Override
        public void onNewOrder() {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//                mListner.onProductClick(item);
                mListner.onNewOrder();
            }
        }

        @Override
        public void onCancelOrder(CustomerOrder order) {
           if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//                mListner.onProductClick(item);
               mListner.onCancelOrder(order);
           }
        }

        @Override
        public void onProductClick(OrderedItem item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//                mListner.onProductClick(item);
                mBinding.getViewModel().removeItem(item);
            }
        }
    };

}
