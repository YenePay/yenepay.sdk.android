package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import examples.mob.yenepay.com.checkoutcounter.store.StoreItem;
import examples.mob.yenepay.com.checkoutcounter.viewmodels.ItemsViewModel;

public class ItemsFragment extends Fragment {

    private ItemsViewModel mViewModel;
    private RecyclerView mListView;
    private OnItemSelectedListner mListner;

    public static ItemsFragment newInstance() {
        return new ItemsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.items_fragment, container, false);
        mListView = view.findViewById(R.id.item_list);
        mListView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);

        mViewModel.getAllItems().observe(this, items -> {
            // update UI

            mListView.setAdapter(new SimpleItemRecyclerViewAdapter(mListner, items, false));
        });
        /* TODO: Use the ViewModel */
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListner = (OnItemSelectedListner) context;
        } catch (Exception e){
            throw new IllegalArgumentException("Activity must implement OnItemSelectedListner");
        }
    }

    public interface OnItemSelectedListner{
        void onItemSelected(StoreItem item, int quantity);
    }


}
