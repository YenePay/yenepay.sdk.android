package examples.mob.yenepay.com.checkoutcounter.ui.orders;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.store.StoreRepository;
import examples.mob.yenepay.com.checkoutcounter.ui.category.CategoriesFragment;
import examples.mob.yenepay.com.checkoutcounter.ui.category.CategoryClickCallback;
import examples.mob.yenepay.com.checkoutcounter.ui.category.CategoryEditFragment;
import examples.mob.yenepay.com.checkoutcounter.ui.category.ManageCategoryFragment;
import examples.mob.yenepay.com.checkoutcounter.ui.category.OnCategoryInteractionListner;

public class ManageOrdersActivity extends AppCompatActivity implements
        OnOrderInteractionListner,
        OrderClickCallback{

    private boolean mTwoPane;
    private FloatingActionButton mFabAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        new StoreRepository(getApplication()).generateSampleData();
        mFabAddNew = findViewById(R.id.fab_add_new);
        mFabAddNew.setOnClickListener(view -> onNewOrderClicked());
        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.items_container, OrdersFragment.newInstance())
                .commit();
    }


    @Override
    public void onOrderSelected(CustomerOrder item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.orders_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear_orders:
                StoreRepository.getInstance(getApplication()).deleteAllOrders();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewOrderClicked() {

    }

    @Override
    public void onProductClick(CustomerOrder item) {

    }
}
