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
import android.view.LayoutInflater;
import android.view.ViewGroup;



import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.ui.category.CategoryClickCallback;
import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.CategoryItemBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<ItemCategory> mProductList;

    @Nullable
    private final CategoryClickCallback mProductClickCallback;

    public CategoryAdapter(@Nullable CategoryClickCallback clickCallback) {
        mProductClickCallback = clickCallback;
        setHasStableIds(true);
    }

    public void setProductList(final List<ItemCategory> productList) {
        if (mProductList == null) {
            mProductList = productList;
            notifyItemRangeInserted(0, productList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mProductList.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mProductList.get(oldItemPosition).id ==
                            productList.get(newItemPosition).id;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ItemCategory newProduct = productList.get(newItemPosition);
                    ItemCategory oldProduct = mProductList.get(oldItemPosition);
                    return newProduct.id == oldProduct.id
                            && newProduct.description == oldProduct.description
                            && newProduct.name == oldProduct.name;
                }
            });
            mProductList = productList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CategoryItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.category_item,
                        parent, false);
        binding.setCallback(mProductClickCallback);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.binding.setCategory(mProductList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mProductList == null ? 0 : mProductList.size();
    }

    @Override
    public long getItemId(int position) {
        return mProductList.get(position).id;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        final CategoryItemBinding binding;

        public CategoryViewHolder(CategoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
