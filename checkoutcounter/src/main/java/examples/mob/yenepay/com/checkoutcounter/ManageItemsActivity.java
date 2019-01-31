package examples.mob.yenepay.com.checkoutcounter;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import examples.mob.yenepay.com.checkoutcounter.store.StoreItem;

public class ManageItemsActivity extends AppCompatActivity implements
        ItemsFragment.OnItemSelectedListner{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onItemSelected(StoreItem item, int quantity) {

    }
}
