package examples.mob.yenepay.com.checkoutcounter.ui.item;

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
import examples.mob.yenepay.com.checkoutcounter.databinding.FragmentManageItemBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.ItemViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ManageItemFragment extends Fragment {

    public static final String EXTRA_ITEM_ID = "extra_item_id";
    FragmentManageItemBinding mBinding;
    OnItemInteractionListner mListner;

    public ManageItemFragment() {
    }

    public static ManageItemFragment getInstanceForItem(String itemId){
        ManageItemFragment fragment = new ManageItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ITEM_ID, itemId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_item, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ItemViewModel.Factory factory = new ItemViewModel.Factory(getActivity().getApplication(),
                getArguments().getString(EXTRA_ITEM_ID));
        final ItemViewModel model = ViewModelProviders.of(this, factory).get(ItemViewModel.class);
        mBinding.setItemViewModel(model);
        mBinding.setCallback(mProductClickCallback);
        mBinding.setDeleteCallback(mDeleteProductClickCallback);
        if(model.getObservableItem() != null) {
            model.getObservableItem().observe(this, storeItem -> {
                model.setItem(storeItem);
//                model.notifyAll();
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemInteractionListner) {
            mListner = (OnItemInteractionListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemEditInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListner = null;
    }

    private final ProductClickCallback mProductClickCallback = new ProductClickCallback() {
        @Override
        public void onProductClick(StoreItem item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                mListner.onItemEditClicked(item);
            }
        }
    };

    private final ProductClickCallback mDeleteProductClickCallback = new ProductClickCallback() {
        @Override
        public void onProductClick(StoreItem item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setIcon(R.drawable.ic_delete_forever_black_24dp)
                        .setTitle("Delete Warning")
                        .setMessage("Are you sure you want to delete this product. Once deleted you can not get back this item.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mBinding.getItemViewModel().delete(item);
                                mListner.onItemDelete(item);
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
