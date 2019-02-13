package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.OrdersFragmentBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.ui.OrdersAdapter;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.OrderListViewModel;

public class OrdersFragment extends Fragment {

    private RecyclerView mListView;
    private OrderClickCallback mListner;
    private OrdersAdapter mCategoriesAdapter;
    private OrdersFragmentBinding mBinding;

    public static OrdersFragment newInstance() {
        return new OrdersFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.orders_fragment, container, false);
        mCategoriesAdapter = new OrdersAdapter(mCategoryClickCallback);
        mBinding.itemList.setAdapter(mCategoriesAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OrderListViewModel viewModel = ViewModelProviders.of(this).get(OrderListViewModel.class);

        viewModel.getOrbservableOrders().observe(this, items -> {
            // update UI
            if (items != null) {
                mBinding.setIsLoading(false);
                mCategoriesAdapter.setProductList(items);
            } else {
                mBinding.setIsLoading(true);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
//            mListView.setAdapter(new CategoryAdapter(mListner));
        });
        /* TODO: Use the ViewModel */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListner = (OrderClickCallback) context;
        } catch (Exception e){
            throw new IllegalArgumentException("Activity must implement CategoryClickCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListner = null;
    }

    private final OrderClickCallback mCategoryClickCallback = new OrderClickCallback() {
        @Override
        public void onProductClick(CustomerOrder category) {

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                mListner.onProductClick(category);
            }
        }
    };
}
