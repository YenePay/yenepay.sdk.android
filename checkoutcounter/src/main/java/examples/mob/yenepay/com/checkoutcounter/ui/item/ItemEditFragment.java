package examples.mob.yenepay.com.checkoutcounter.ui.item;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.FragmentItemEditBinding;
import examples.mob.yenepay.com.checkoutcounter.databinding.ProductDetaiEditItemBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.utils.ImagePicker;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.ItemViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnItemInteractionListner} interface
 * to handle interaction events.
 * Use the {@link ItemEditFragment#getInstanceForItem} factory method to
 * create an instance of this fragment.
 */
public class ItemEditFragment extends Fragment {
    private static final int PICK_IMAGE_ID = 234;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_ITEM_ID = "extra_item_id";
    private static final String EXTRA_OPERATION_TYPE = "extra_op_type";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentItemEditBinding mBinding;

    private OnItemInteractionListner mListener;
    private CategorySpinnerAdapter mCategoriesSpinnerAdapter;

    public ItemEditFragment() {
        // Required empty public constructor
    }

    public static ItemEditFragment getInstanceForItem(String itemId){
        ItemEditFragment fragment = new ItemEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_ITEM_ID, itemId);
        bundle.putInt(EXTRA_OPERATION_TYPE, 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ItemEditFragment getInstanceForNewItem(){
        ItemEditFragment fragment = new ItemEditFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_OPERATION_TYPE, 0);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getContext());
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(EXTRA_ITEM_ID);
//            mParam2 = getArguments().getString(EXTRA_OPERATION_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_item_edit, container, false);
        mCategoriesSpinnerAdapter = new CategorySpinnerAdapter(getContext(), new ArrayList<>());
        mBinding.detail.categorySpinner.setAdapter(mCategoriesSpinnerAdapter);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        boolean isEdit = false;
        if(getArguments().getInt(EXTRA_OPERATION_TYPE, 0) == 1 && getArguments().containsKey(EXTRA_ITEM_ID)){
            isEdit = true;
        }
        ItemViewModel.Factory factory = new ItemViewModel.Factory(getActivity().getApplication(),
                getArguments().getString(EXTRA_ITEM_ID));

        final ItemViewModel model = ViewModelProviders.of(this, factory).get(ItemViewModel.class);
        mBinding.setItemViewModel(model);
        mBinding.setCallback(mProductClickCallback);
        mBinding.setSpinnerCallback(new OnCategorySelectionListner());
//        mBinding.setLifecycleOwner(this);
        if(isEdit){
            model.getObservableItem().observe(this, storeItem -> model.setItem(storeItem));
        }
        model.getAllCategories().observe(this, categories -> {
            mCategoriesSpinnerAdapter.setList(categories);
        });

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(StoreItem item) {
        if (mListener != null) {
            mListener.onItemSaved(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemInteractionListner) {
            mListener = (OnItemInteractionListner) context;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(getContext(), resultCode, data);
                try {
                    String image = ImagePicker.saveImage(bitmap);
                    ItemViewModel itemViewModel = mBinding.getItemViewModel();
                    StoreItem storeItem = itemViewModel.storeItem.get();
                    if(storeItem != null) {
                        storeItem.image = image;
                        mBinding.invalidateAll();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // TODO use bitmap
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private final OnItemInteractionListner mProductClickCallback = new OnItemInteractionListner() {
        @Override
        public void onItemSelected(StoreItem item, int quantity) {

        }

        @Override
        public void onItemChangeImageClicked(StoreItem item) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                onPickImage(null);
            }
        }

        @Override
        public void onItemEditClicked(StoreItem item) {

        }

        @Override
        public void onItemSaved(StoreItem item) {
            if(mBinding.getItemViewModel().isValid()) {
                mBinding.getItemViewModel().save(item);
                mListener.onItemSaved(item);
            }
        }

        @Override
        public void onItemEditCancelled(StoreItem item) {
            mListener.onItemEditCancelled(item);
        }

        @Override
        public void onItemDelete(StoreItem item) {

        }

        @Override
        public void onNewItemClicked() {

        }
    };

    public class OnCategorySelectionListner implements AbsListView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ItemCategory item = mCategoriesSpinnerAdapter.getItem(i);
            mBinding.getItemViewModel().storeItem.get().categoryId = item.id;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    public class SelectedItemCategory extends BaseObservable {
        private ObservableField<Integer> selectedItemPosition;

        @Bindable
        public int getSelectedItemPosition() {
            return selectedItemPosition.get();
        }

        public void setSelectedItemPosition(int selectedItemPosition) {
            mBinding.getItemViewModel().storeItem.get().categoryId = (int) mCategoriesSpinnerAdapter.getItemId(selectedItemPosition);
            this.selectedItemPosition.set(selectedItemPosition);
        }
    }
}
