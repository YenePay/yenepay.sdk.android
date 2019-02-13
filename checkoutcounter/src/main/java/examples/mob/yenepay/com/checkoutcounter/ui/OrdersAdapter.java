/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package examples.mob.yenepay.com.checkoutcounter.ui;


import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.CategoryItemBinding;
import examples.mob.yenepay.com.checkoutcounter.databinding.OrderListItemBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.ui.orders.OrderClickCallback;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<CustomerOrder> mOrderList;

    @Nullable
    private final OrderClickCallback mOrderClickCallback;

    public OrdersAdapter(@Nullable OrderClickCallback clickCallback) {
        mOrderClickCallback = clickCallback;
        setHasStableIds(true);
    }

    public void setProductList(final List<CustomerOrder> productList) {
        if (mOrderList == null) {
            mOrderList = productList;
            notifyItemRangeInserted(0, productList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mOrderList.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return TextUtils.equals(mOrderList.get(oldItemPosition).getId(),
                            productList.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    CustomerOrder newProduct = productList.get(newItemPosition);
                    CustomerOrder oldProduct = mOrderList.get(oldItemPosition);
                    return TextUtils.equals(newProduct.getId(), oldProduct.getId())
                            && newProduct.getGrandTotal() == oldProduct.getGrandTotal()
                            && TextUtils.equals(newProduct.getStoreCode(), oldProduct.getStoreCode())
                            && newProduct.getStatus() == oldProduct.getStatus()
                            && newProduct.getLastStatusDate() == oldProduct.getLastStatusDate();
                }
            });
            mOrderList.clear();
            mOrderList.addAll(productList);
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OrderListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.order_list_item,
                        parent, false);
        binding.setCallback(mOrderClickCallback);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.binding.setOrder(mOrderList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return mOrderList.get(position).getId();
//    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        final OrderListItemBinding binding;

        public OrderViewHolder(OrderListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
