package examples.mob.yenepay.com.checkoutcounter.ui.item;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.db.entity.StoreItem;

public class ManageItemsActivity extends AppCompatActivity implements
        OnItemInteractionListner {

    private boolean mTwoPane;
    private FloatingActionButton mFabAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        new StoreRepository(getApplication()).generateSampleData();
        mFabAddNew = findViewById(R.id.fab_add_new);
        mFabAddNew.setOnClickListener(view -> onNewItemClicked());
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.items_container, ItemsFragment.newInstance())
                .commit();
    }

    @Override
    public void onItemSelected(StoreItem item, int quantity) {
        ManageItemFragment fragment = ManageItemFragment.getInstanceForItem(item.id);
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.items_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onItemChangeImageClicked(StoreItem item) {

    }

    @Override
    public void onItemEditClicked(StoreItem item) {
        ItemEditFragment fragment = ItemEditFragment.getInstanceForItem(item.id);
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.items_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        hideAddNewFab();
    }

    private void showAddNewFab() {
        if(!mFabAddNew.isShown()) {
            mFabAddNew.show();
        }
    }

    private void hideAddNewFab() {
        if(mFabAddNew.isShown()) {
            mFabAddNew.hide();
        }
    }

    @Override
    public void onItemSaved(StoreItem item) {
        if(mTwoPane){
            ManageItemFragment fragment = ManageItemFragment.getInstanceForItem(item.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().popBackStack();
            ManageItemFragment fragment = ManageItemFragment.getInstanceForItem(item.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.items_container, fragment)
                    .commit();
        }
        showAddNewFab();
    }

    @Override
    public void onItemEditCancelled(StoreItem item) {
        if(mTwoPane){
            if(!TextUtils.isEmpty(item.id)) {
                ManageItemFragment fragment = ManageItemFragment.getInstanceForItem(item.id);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
                if(fragment != null){
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                }
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }
        showAddNewFab();
    }

    @Override
    public void onItemDelete(StoreItem item) {
        if(mTwoPane){
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
            if(fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .remove(fragment)
                        .commit();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }
        showAddNewFab();
    }

    @Override
    public void onNewItemClicked() {
        ItemEditFragment fragment = ItemEditFragment.getInstanceForNewItem();
        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.items_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        hideAddNewFab();
    }
}
