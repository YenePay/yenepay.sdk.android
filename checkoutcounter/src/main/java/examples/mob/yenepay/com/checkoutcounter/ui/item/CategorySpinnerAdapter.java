package examples.mob.yenepay.com.checkoutcounter.ui.item;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.databinding.CategoryItemBinding;
import examples.mob.yenepay.com.checkoutcounter.databinding.CategorySpinnerItemBinding;
import examples.mob.yenepay.com.checkoutcounter.db.entity.ItemCategory;
import examples.mob.yenepay.com.checkoutcounter.ui.category.CategoryClickCallback;

public class CategorySpinnerAdapter extends ArrayAdapter<ItemCategory> {

    List<ItemCategory> mCategories;
    CategorySpinnerItemBinding mBinding;

    public CategorySpinnerAdapter(Context context, List<ItemCategory> categories) {
        super(context, 0, categories);
        mCategories = categories;
    }

    public void setList(List<ItemCategory> categories){
        if(mCategories == null){
            mCategories = categories;
        } else {
            mCategories.clear();
            mCategories.addAll(categories);
        }
        notifyDataSetChanged();
    }

    @Override
    public ItemCategory getItem(int position) {
        if(mCategories != null && !mCategories.isEmpty()){
            return mCategories.get(position);
        }
        return super.getItem(position);
    }

    @Override
    public int getPosition(ItemCategory item) {
        if(mCategories != null && !mCategories.isEmpty()){
            for (int i = 0; i < mCategories.size(); i++){
                if(item != null && item.id == mCategories.get(i).id){
                    return i;
                }
            }
        }
        return super.getPosition(item);
    }

    public int getPosition(long id){
        if(mCategories != null && !mCategories.isEmpty()){
            for (int i = 0; i < mCategories.size(); i++){
                ItemCategory itemCategory = mCategories.get(i);
                if(id == itemCategory.id){
                    return i;
                }
            }
        }
        return -1;
    }

    public ItemCategory getItemById(long id){
        if(mCategories != null && !mCategories.isEmpty()){
            for (int i = 0; i < mCategories.size(); i++){
                ItemCategory itemCategory = mCategories.get(i);
                if(id == itemCategory.id){
                    return itemCategory;
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if(mCategories != null && !mCategories.isEmpty()){
            return mCategories.get(position).id;
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCategoryView(position, parent);
    }

    @NonNull
    private View getCategoryView(int position, ViewGroup parent) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.category_spinner_item, parent, false);
        mBinding.setCategory(getItem(position));
        return mBinding.getRoot();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCategoryView(position, parent);
    }
}
