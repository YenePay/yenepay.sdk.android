package com.example.sisay.shopsimulator;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sisay.shopsimulator.store.StoreManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yenepaySDK.model.OrderedItem;

import java.util.List;
import java.util.Locale;

public class CartActivity extends ShopBaseActivity {
    private OrderedItemsRecyclerAdapter mListAdapter;
    private TextView mCountText;
    private TextView mTotalText;
    private View mBtnClear;
    private View mBtnCheckout;
    private View mCheckoutWeb;
    private View mEmptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mEmptyView = findViewById(R.id.empty_view);
        RecyclerView mListView = findViewById(R.id.list_cart);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mListAdapter = new OrderedItemsRecyclerAdapter(this, mCartUpdateListener);

        mListView.setAdapter(mListAdapter);
        mCountText = findViewById(R.id.txt_cart_items_count);
        mTotalText = findViewById(R.id.txt_cart_total);

        mBtnClear = findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreManager.clearCart();
                mListAdapter.notifyDataSetChanged();
                updateCartTotals();
            }
        });

        mBtnCheckout = findViewById(R.id.btn_checkout);
        mBtnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreManager.checkoutToApp(CartActivity.this);
//                StoreManager.clearCart();
                mListAdapter.notifyDataSetChanged();
                updateCartTotals();
            }
        });

        mCheckoutWeb = findViewById(R.id.btn_checkout_web);
        mCheckoutWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreManager.checkoutWithBrowser(CartActivity.this);
//                StoreManager.clearCart();
                mListAdapter.notifyDataSetChanged();
                updateCartTotals();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private final OnCartUpdatedActionListener mCartUpdateListener = new OnCartUpdatedActionListener() {
        @Override
        public void onCartUpdated() {
            updateCartTotals();
        }
    };

    private void updateCartTotals() {
        mTotalText.setText(Utils.getAmountString(StoreManager.getCartTotal()));
        mCountText.setText(String.format(Locale.ENGLISH, "%d - items", StoreManager.getCartItemsCount()));

        int visibility = mListAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE;
        mBtnClear.setVisibility(visibility);
        mBtnCheckout.setVisibility(visibility);
        mCheckoutWeb.setVisibility(visibility);
        mEmptyView.setVisibility(mListAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    public static class OrderedItemsRecyclerAdapter extends RecyclerView.Adapter<OrderedItemViewHolder> {
        private final OnCartUpdatedActionListener mListener;
        private List<OrderedItem> mOrderedItems;
        private Context mContext;

        public OrderedItemsRecyclerAdapter(Context context, OnCartUpdatedActionListener listener) {
            this.mContext = context;
            this.mOrderedItems = StoreManager.ORDERS;
            this.mListener = listener;
        }

        @NonNull
        @Override
        public OrderedItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.cart_list_content, viewGroup, false);
            return new OrderedItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderedItemViewHolder viewHolder, int i) {
            final OrderedItem item = getItem(i);
            if (StoreManager.ITEM_MAP.containsKey(item.getItemId())) {
                StoreManager.DummyItem storeItem = StoreManager.ITEM_MAP.get(item.getItemId());
                viewHolder.itemImage.setImageResource(storeItem.imageResId);
            }
            viewHolder.itemName.setText(item.getItemName());
            viewHolder.quantity.setText(String.format(Locale.ENGLISH, "Quantity - %d", item.getQuantity()));
            viewHolder.total.setText(Utils.getAmountString(item.getItemTotalPrice()));
            viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreManager.addToCart(item.getItemId(), 1);
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onCartUpdated();
                    }
                }
            });

            viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StoreManager.removeFromCart(item.getItemId(), 1);
                    notifyDataSetChanged();
                    if (mListener != null) {
                        mListener.onCartUpdated();
                    }
                }
            });
        }

        public OrderedItem getItem(int position) {
            if (mOrderedItems == null) {
                return null;
            }
            return mOrderedItems.get(position);
        }

        @Override
        public int getItemCount() {
            return mOrderedItems != null ? mOrderedItems.size() : 0;
        }

    }

    public static class OrderedItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImage;
        private TextView itemName;
        private TextView quantity;
        private TextView total;
        private FloatingActionButton addButton;
        private FloatingActionButton removeButton;

        public OrderedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.txt_item_name);
            quantity = itemView.findViewById(R.id.txt_quantity);
            total = itemView.findViewById(R.id.txt_total);
            addButton = itemView.findViewById(R.id.fab_add);
            removeButton = itemView.findViewById(R.id.fab_minus);
        }
    }

    public interface OnCartUpdatedActionListener {
        void onCartUpdated();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartTotals();
    }
}
