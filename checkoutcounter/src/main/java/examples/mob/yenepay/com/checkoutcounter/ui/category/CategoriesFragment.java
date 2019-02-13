package examples.mob.yenepay.com.checkoutcounter.ui.category;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.CategoriesFragmentBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.ui.CategoryAdapter;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CategoriesViewModel;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CategoryViewModel;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.ItemsViewModel;

public class CategoriesFragment extends Fragment {

    private RecyclerView mListView;
    private CategoryClickCallback mListner;
    private CategoryAdapter mCategoriesAdapter;
    private CategoriesFragmentBinding mBinding;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.categories_fragment, container, false);
        mCategoriesAdapter = new CategoryAdapter(mCategoryClickCallback);
        mBinding.itemList.setAdapter(mCategoriesAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoriesViewModel viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);

        viewModel.getAllCategories().observe(this, items -> {
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
            mListner = (CategoryClickCallback) context;
        } catch (Exception e){
            throw new IllegalArgumentException("Activity must implement CategoryClickCallback");
        }
    }

    private final CategoryClickCallback mCategoryClickCallback = new CategoryClickCallback() {
        @Override
        public void onProductClick(ItemCategory category) {

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                mListner.onProductClick(category);
            }
        }
    };
}
