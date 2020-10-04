package com.example.sisay.shopsimulator;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.sisay.shopsimulator.store.StoreManager;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends ShopBaseActivity implements
        ItemDetailFragment.ItemDetailActionListner {
    private static final String TAG = "ItemDetailActivity";
    private static final String FRAGMENT_TAG = "ItemDetailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);



        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ItemDetailActivity.this, CartActivity.class));
                }
            });

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String itemId = getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
            if(StoreManager.ITEM_MAP.containsKey(itemId)){
                StoreManager.DummyItem item = StoreManager.ITEM_MAP.get(itemId);
                actionBar.setTitle(item.content);
                ImageView toolBarImage = findViewById(R.id.toolbar_image);
                CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);
//                appBarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                toolBarImage.setImageResource(item.largeImageResId);
            }
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    itemId);
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }







    @Override
    public void onItemCheckoutClicked(StoreManager.DummyItem item) {
        if (item != null) {

            StoreManager.checkoutToApp(this, item);

        } else {
            Snackbar.make(findViewById(R.id.detail_toolbar), "Please select an Item before paying", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemWebCheckoutClicked(StoreManager.DummyItem item) {
        StoreManager.checkoutWithBrowser(this, item);
    }

    @Override
    public void onAddToCartClicked(StoreManager.DummyItem item) {
        StoreManager.addToCart(item, 1);
        Snackbar.make(findViewById(R.id.detail_toolbar), "Item added to your cart", Snackbar.LENGTH_LONG)
                .setAction("View Cart", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ItemDetailActivity.this, CartActivity.class));
                    }
                }).show();
    }
}
