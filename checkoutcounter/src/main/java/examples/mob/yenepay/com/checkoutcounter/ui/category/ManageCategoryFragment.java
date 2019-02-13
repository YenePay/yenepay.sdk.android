package examples.mob.yenepay.com.checkoutcounter.ui.category;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.FragmentManageCategoryBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CategoryViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ManageCategoryFragment extends Fragment {

    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    FragmentManageCategoryBinding mBinding;
    OnCategoryInteractionListner mListner;

    public ManageCategoryFragment() {
    }

    public static ManageCategoryFragment getInstanceForItem(int itemId){
        ManageCategoryFragment fragment = new ManageCategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CATEGORY_ID, itemId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_category, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CategoryViewModel.Factory factory = new CategoryViewModel.Factory(getActivity().getApplication(),
                getArguments().getInt(EXTRA_CATEGORY_ID, 0));
        final CategoryViewModel model = ViewModelProviders.of(this, factory).get(CategoryViewModel.class);
        mBinding.setCategoryViewModel(model);
        mBinding.setCallback(mProductClickCallback);
        mBinding.setDeleteCallback(mOnCategoryDeleteCallback);
        model.getObservableItem().observe(this, category -> {
            model.setItem(category);
//                model.notifyAll();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryInteractionListner) {
            mListner = (OnCategoryInteractionListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryInteractionListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListner = null;
    }

    private final CategoryClickCallback mProductClickCallback = new CategoryClickCallback() {
        @Override
        public void onProductClick(ItemCategory item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                mListner.onCategoryEditClicked(item);
            }
        }
    };

    private final CategoryClickCallback mOnCategoryDeleteCallback = new CategoryClickCallback() {
        @Override
        public void onProductClick(ItemCategory item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_delete_forever_black_24dp)
                        .setTitle("Delete Warning")
                        .setMessage("Are you sure you want to delete this category. Once deleted you can not get back this category so be sure to continue")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mBinding.getCategoryViewModel().delete(item);
                                mListner.onCategoryDelete(item);
                            }
                        })
                        .setPositiveButtonIcon(AppCompatResources.getDrawable(getContext(),R.drawable.ic_delete_forever_black_24dp))
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.show();
            }
        }
    };
}
