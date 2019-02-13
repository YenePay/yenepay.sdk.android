package examples.mob.yenepay.com.checkoutcounter.ui.category;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import examples.mob.yenepay.com.checkoutcounter.ui.item.ItemsFragment;
import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;

public class ManageCategoriesActivity extends AppCompatActivity implements
        OnCategoryInteractionListner,
        CategoryClickCallback{

    private boolean mTwoPane;
    private FloatingActionButton mFabAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_categories);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        new StoreRepository(getApplication()).generateSampleData();
        mFabAddNew = findViewById(R.id.fab_add_new);
        mFabAddNew.setOnClickListener(view -> onNewCategoryClicked());
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.items_container, CategoriesFragment.newInstance())
                .commit();
    }

    @Override
    public void onCategorySelected(ItemCategory item) {
        ManageCategoryFragment fragment = ManageCategoryFragment.getInstanceForItem(item.id);
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
    public void onCategoryEditClicked(ItemCategory item) {
        CategoryEditFragment fragment = CategoryEditFragment.getInstanceForItem(item.id);
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
    public void onCategorySaved(ItemCategory item) {
        if(mTwoPane){
            ManageCategoryFragment fragment = ManageCategoryFragment.getInstanceForItem(item.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            getSupportFragmentManager().popBackStack();
            ManageCategoryFragment fragment = ManageCategoryFragment.getInstanceForItem(item.id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.items_container, fragment)
                    .commit();
        }
        showAddNewFab();
    }

    @Override
    public void onCategoryEditCancelled(ItemCategory item) {
        if(mTwoPane){
            if(item.id != 0) {
                ManageCategoryFragment fragment = ManageCategoryFragment.getInstanceForItem(item.id);
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
    public void onCategoryDelete(ItemCategory item) {
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
    public void onNewCategoryClicked() {
        CategoryEditFragment fragment = CategoryEditFragment.getInstanceForNewItem();
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

    @Override
    public void onProductClick(ItemCategory item) {
        onCategorySelected(item);
    }
}
