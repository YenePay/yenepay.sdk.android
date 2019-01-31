package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import examples.mob.yenepay.com.checkoutcounter.store.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CheckoutViewModel;

public class CheckoutFragment extends Fragment {

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

    public static CheckoutFragment newInstance() {
        return new CheckoutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_sales, container, false);
        mRecyclerView = view.findViewById(R.id.item_list);
        mItemsCount = view.findViewById(R.id.txt_items_count);
        mSubTotal = view.findViewById(R.id.txt_subtotal);
        mDiscount = view.findViewById(R.id.txt_discount);
        mTax = view.findViewById(R.id.txt_tax);
        mHandlingFee = view.findViewById(R.id.txt_handling);
        mShippingFee = view.findViewById(R.id.txt_shipping);
        mGrandTotal = view.findViewById(R.id.txt_total);
        mStatus = view.findViewById(R.id.txt_group_name);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListner.onAddNewItem();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .addToBackStack(null)
//                        .replace(R.id.checkout_container, ItemsFragment.newInstance())
//                        .commit();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);

        assert mRecyclerView != null;
        mViewModel.getOrderedItems().observe(this, orderedItems -> {
            CustomerOrder order = StoreManager.generateCustomerOrder();
            mItemsCount.setText(String.format("%d Items", order.getTotalQuantity()));
            mSubTotal.setText(Util.getFormattedAmt(order.getItemsTotal()));
            mDiscount.setText("(" + Util.getFormattedAmt(order.getDiscount()) + ")");
            mHandlingFee.setText(Util.getFormattedAmt(order.getHandlingFee()));
            mTax.setText(Util.getFormattedAmt(order.getTax()));
            mShippingFee.setText(Util.getFormattedAmt(order.getShippingFee()));
            mGrandTotal.setText(Util.getFormattedAmt(order.getGrandTotal()));
            mStatus.setText(mViewModel.getStatus().getValue());
            setupRecyclerView((RecyclerView)mRecyclerView);
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
        OrderItemRecyclerViewAdapter adapter = new OrderItemRecyclerViewAdapter(getActivity(), StoreManager.ORDERS, false);
        recyclerView.setAdapter(adapter);
    }

    public interface OnCheckoutActionListner{
        void onAddNewItem();
    }

}
