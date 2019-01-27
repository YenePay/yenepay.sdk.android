package examples.mob.yenepay.com.checkoutcounter;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import examples.mob.yenepay.com.checkoutcounter.store.StoreManager;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CheckoutActivity extends AppCompatActivity implements
        CheckoutFragment.OnCheckoutActionListner,
        ItemsFragment.OnItemSelectedListner {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private CheckoutViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        mViewModel = ViewModelProviders.of(this).get(CheckoutViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mListner.onAddNewItem();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if(!mTwoPane){
                    transaction.addToBackStack(null);
                }
                transaction
                        .replace(getDetailContainer(), QRFragment.newInstance())
                        .commit();
            }
        });
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, ItemsFragment.newInstance())
                    .commit();
        }
        mViewModel.setTwoPane(mTwoPane);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.checkout_container, CheckoutFragment.newInstance())
                .commit();


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, StoreManager.ITEMS, mTwoPane));
    }

    @Override
    public void onAddNewItem() {
        loadAddNewItemFragment();
    }

    private void loadAddNewItemFragment() {
        int container = getDetailContainer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!mTwoPane){
            transaction.addToBackStack(null);
        }
        transaction
                .replace(container, ItemsFragment.newInstance())
                .commit();
    }

    private int getDetailContainer() {
        return mTwoPane? R.id.item_detail_container: R.id.checkout_container;
    }

    @Override
    public void onItemSelected(StoreManager.StoreItem item, int quantity) {
        StoreManager.addOrder(item, quantity);
        if(mTwoPane){
            CheckoutFragment fragment = getCheckoutFragment();
            mViewModel.loadItems();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private CheckoutFragment getCheckoutFragment() {
        return (CheckoutFragment) getSupportFragmentManager().findFragmentById(R.id.checkout_container);
    }
}
