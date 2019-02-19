package examples.mob.yenepay.com.checkoutcounter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.yenepaySDK.verify.Verification;

import examples.mob.yenepay.com.checkoutcounter.databinding.StoreHeaderBinding;
import examples.mob.yenepay.com.checkoutcounter.ui.category.ManageCategoriesActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.checkout.CheckoutActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.item.ManageItemsActivity;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.ManageOrdersActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private FloatingActionButton mQRFab;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;
    private StoreHeaderBinding mNavBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setUpDrawer(toolbar);
        mNavBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.store_header, navigationView, false);
        navigationView.addHeaderView(mNavBinding.getRoot());
        mNavBinding.setTerminal(StoreApp.getStoreTerminal());
        mNavBinding.getTerminal().status.observe(this, status -> mNavBinding.invalidateAll());



        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CheckoutActivity.class));
            }
        });
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setUpDrawer(Toolbar toolbar) {
        Log.d(TAG, "setUpDrawer: called");
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        this.mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open_desc, R.string.nav_close_desc);
        this.mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    drawerLayout.openDrawer(GravityCompat.START);

                } else {
                    onBackPressed();
                }
            }
        });
        drawerLayout.setDrawerListener(this.mDrawerToggle);
        this.getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.navigationView = (NavigationView) this.findViewById(R.id.navigation_drawer);
        this.navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_manage_orders:
                startActivity(new Intent(this, ManageOrdersActivity.class));
                return true;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_manage_categories:
                startActivity(new Intent(this, ManageCategoriesActivity.class));
                return true;
            case R.id.menu_manage_Items:
                startActivity(new Intent(this, ManageItemsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        new DownloadServerCertificateTask().execute();
        mDrawerToggle.syncState();
    }

    static class DownloadServerCertificateTask extends   AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new Verification(StoreApp.getContext()).getPublicKey();
            return null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return onOptionsItemSelected(menuItem);
    }

}
