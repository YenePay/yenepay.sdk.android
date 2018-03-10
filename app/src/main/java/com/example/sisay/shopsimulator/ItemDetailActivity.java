package com.example.sisay.shopsimulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.yenepaySDK.PaymentOrderManager;
import com.yenepaySDK.PaymentResponse;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String message = "onActivityResult Called, requestCode: " + requestCode + ", resultCode: " + resultCode + ", data: " + data;
        if(resultCode == RESULT_OK) {
            ItemDetailFragment fragment = (ItemDetailFragment)getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            PaymentResponse response = PaymentOrderManager.parseResponse(data.getExtras());
            if(fragment != null && response != null){
                fragment.setPaymentResponse(response);
            }
            Log.d(TAG, "onActivityResult: success response :" + response);
            showMessage(message);
        } else if(resultCode == RESULT_CANCELED && data != null){
            showMessage(message);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showMessage(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ItemDetailActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void checkOutItem(Intent intent){
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 1);
            //Log.d(TAG, "Activity Resolved: ");
        }
    }
}
