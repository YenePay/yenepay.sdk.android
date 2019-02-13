package examples.mob.yenepay.com.checkoutcounter.ui.category;

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

import examples.mob.yenepay.com.checkoutcounter.ui.item.OnItemInteractionListner;
import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.FragmentCategoryEditBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.CategoryViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnItemInteractionListner} interface
 * to handle interaction events.
 * Use the {@link CategoryEditFragment#getInstanceForItem} factory method to
 * create an instance of this fragment.
 */
public class CategoryEditFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_CATEGORY_ID = "extra_category_id";
    private static final String EXTRA_OPERATION_TYPE = "extra_op_type";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentCategoryEditBinding mBinding;

    private OnCategoryInteractionListner mListener;

    public CategoryEditFragment() {
        // Required empty public constructor
    }

    public static CategoryEditFragment getInstanceForItem(int itemId){
        CategoryEditFragment fragment = new CategoryEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_CATEGORY_ID, itemId);
        bundle.putInt(EXTRA_OPERATION_TYPE, 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CategoryEditFragment getInstanceForNewItem(){
        CategoryEditFragment fragment = new CategoryEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_OPERATION_TYPE, 0);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(EXTRA_CATEGORY_ID);
//            mParam2 = getArguments().getString(EXTRA_OPERATION_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_edit, container, false);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isEdit = false;
        if(getArguments().getInt(EXTRA_OPERATION_TYPE, 0) == 1 && getArguments().containsKey(EXTRA_CATEGORY_ID)){
            isEdit = true;
        }
        CategoryViewModel.Factory factory = new CategoryViewModel.Factory(getActivity().getApplication(),
                getArguments().getInt(EXTRA_CATEGORY_ID, 0));

        final CategoryViewModel model = ViewModelProviders.of(this, factory).get(CategoryViewModel.class);
        mBinding.setCategoryViewModel(model);
        mBinding.setCallback(mProductClickCallback);
//        mBinding.setLifecycleOwner(this);
        if(isEdit){
            model.getObservableItem().observe(this, storeItem -> model.setItem(storeItem));
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(ItemCategory item) {
        if (mListener != null) {
            mListener.onCategorySaved(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryInteractionListner) {
            mListener = (OnCategoryInteractionListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemEditInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private final OnCategoryInteractionListner mProductClickCallback = new OnCategoryInteractionListner() {
        @Override
        public void onCategorySelected(ItemCategory item) {

        }

        @Override
        public void onCategoryEditClicked(ItemCategory item) {

        }

        @Override
        public void onCategorySaved(final ItemCategory item) {
            if(!mBinding.getCategoryViewModel().isValid()){
                return;
            }
            mBinding.getCategoryViewModel().save(item, new StoreRepository.OnCategoryCreated() {
                @Override
                public void onCategoryCreated(long[] newCategoryIds) {
                    if (newCategoryIds.length > 0) {
                        item.id = (int) newCategoryIds[0];
                        mListener.onCategorySaved(item);
                    }
                }
            });
            if(item.id != 0) {
                mListener.onCategorySaved(item);
            }

        }

        @Override
        public void onCategoryEditCancelled(ItemCategory item) {
            mListener.onCategoryEditCancelled(item);
        }

        @Override
        public void onCategoryDelete(ItemCategory item) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setIcon(R.drawable.ic_delete_forever_black_24dp)
                    .setTitle("Delete Warning")
                    .setMessage("Are you sure you want to delete this category. Once deleted you can not get back this category so be sure to continue")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mBinding.getCategoryViewModel().delete(item);
                            mListener.onCategoryDelete(item);
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

        @Override
        public void onNewCategoryClicked() {

        }
    };
}
