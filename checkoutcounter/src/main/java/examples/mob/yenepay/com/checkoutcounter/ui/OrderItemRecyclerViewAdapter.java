package examples.mob.yenepay.com.checkoutcounter.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.databinding.OrderItemListContentBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.OrderedItemEntity;
import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.OrderedItemClickCallback;

public class OrderItemRecyclerViewAdapter
        extends RecyclerView.Adapter<OrderItemRecyclerViewAdapter.OrderedItemViewHolder> {

    private List<OrderedItemEntity> mItemsList;
    private OrderedItemClickCallback mOrderedItemClickCallback;

    public OrderItemRecyclerViewAdapter(OrderedItemClickCallback callback){
        mOrderedItemClickCallback = callback;
    }

    @Override
    public OrderedItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OrderItemListContentBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.order_item_list_content,
                        parent, false);
        binding.setCallback(mOrderedItemClickCallback);
        return new OrderedItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final OrderedItemViewHolder holder, int position) {
        holder.binding.setOrderedItem(mItemsList.get(position));
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return mItemsList == null ? 0 : mItemsList.size();
    }

    @Override
    public long getItemId(int position) {
        return mItemsList.get(position).getId();
    }

    public void setItems(List<OrderedItemEntity> itemEntities) {
        if (mItemsList == null) {
            mItemsList = itemEntities;
            notifyItemRangeInserted(0, itemEntities.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mItemsList.size();
                }

                @Override
                public int getNewListSize() {
                    return itemEntities.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mItemsList.get(oldItemPosition).getId() == itemEntities.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    OrderedItemEntity newProduct = itemEntities.get(newItemPosition);
                    OrderedItemEntity oldProduct = mItemsList.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && newProduct.getItemId() == oldProduct.getItemId()
                            && newProduct.getItemName() == oldProduct.getItemName()
                            && newProduct.getQuantity() == oldProduct.getQuantity()
                            && newProduct.getUnitPrice() == oldProduct.getUnitPrice();
                }
            });
            mItemsList = itemEntities;
            result.dispatchUpdatesTo(this);
        }
    }

    static class OrderedItemViewHolder extends RecyclerView.ViewHolder {

        final OrderItemListContentBinding binding;

        public OrderedItemViewHolder(OrderItemListContentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
