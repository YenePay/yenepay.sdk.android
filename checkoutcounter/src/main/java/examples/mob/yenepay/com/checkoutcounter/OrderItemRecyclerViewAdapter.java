package examples.mob.yenepay.com.checkoutcounter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yenepaySDK.model.OrderedItem;

import java.util.List;

public class OrderItemRecyclerViewAdapter
        extends RecyclerView.Adapter<OrderItemRecyclerViewAdapter.ViewHolder> {

    private final FragmentActivity mParentActivity;
    private final List<OrderedItem> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            OrderedItem item = (OrderedItem) view.getTag();
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getItemId());
                ItemDetailFragment fragment = new ItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, ItemDetailActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.getItemId());

                context.startActivity(intent);
            }
        }
    };

    OrderItemRecyclerViewAdapter(FragmentActivity parent,
                                 List<OrderedItem> items,
                                 boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getItemName());
        holder.mContentView.setText(Util.getFormattedAmt(holder.mItem.getItemTotalPrice()));
//        holder.mImageView.setImageResource(mValues.get(position).imageResId);
        holder.mIdView.setOnClickListener(mOnClickListener);
        holder.mIdView.setTag(holder.mItem);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        OrderedItem mItem;
//        final ImageView mImageView;
        final TextView mIdView;
        final TextView mContentView;

        ViewHolder(View view) {
            super(view);
            mItem = null;
            mIdView = view.findViewById(R.id.id_text);
            mContentView = view.findViewById(R.id.content);
//            mImageView = view.findViewById(R.id.img);
        }
    }
}
