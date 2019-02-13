package examples.mob.yenepay.com.checkoutcounter.ui;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ComplexColorCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

import examples.mob.yenepay.com.checkoutcounter.R;
import examples.mob.yenepay.com.checkoutcounter.StoreApp;
import examples.mob.yenepay.com.checkoutcounter.db.entity.CustomerOrder;
import examples.mob.yenepay.com.checkoutcounter.ui.item.CategorySpinnerAdapter;
import examples.mob.yenepay.com.checkoutcounter.utils.Util;

public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("imageFile")
    public static void imageFile(ImageView view, String imageName) {
        view.setImageBitmap(Util.getProductImage(imageName));
    }

    @BindingAdapter("money")
    public static void money(EditText view, Double money) {
        if(money == null){
            money = 0d;
        }
        view.setText(StoreApp.getContext().getString(R.string.product_price2, money));
    }

    @BindingAdapter(value = "moneyAttrChanged")
    public static void setListener(EditText editText, final InverseBindingListener listener) {
        if (listener != null) {
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    listener.onChange();
                }
            });
        }
    }

    @InverseBindingAdapter(attribute = "money")
    public static double money(EditText view) {
        try {
            return NumberFormat.getInstance().parse(view.getText().toString()).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    @BindingAdapter("itemCategory")
    public static void itemCategory(Spinner view, Integer categoryId) {
        if(categoryId == null || view == null){
            categoryId = 1;
        }
        CategorySpinnerAdapter adapter = (CategorySpinnerAdapter) view.getAdapter();
        if(adapter == null){
            categoryId = 1;
        }
        view.setSelection(adapter.getPosition(categoryId));
    }

    @BindingAdapter(value = "itemCategoryAttrChanged")
    public static void setCategoryListener(Spinner editText, final InverseBindingListener listener) {
        if (listener != null) {
            editText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    listener.onChange();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    listener.onChange();
                }
            });
        }
    }

    @InverseBindingAdapter(attribute = "itemCategory")
    public static int itemCategory(Spinner view) {
        try {
            int selectedItemId = (int) view.getSelectedItemId();
            return selectedItemId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @BindingAdapter("error")
    public static void setError(TextInputLayout editText, Object strOrResId) {
        if (strOrResId instanceof Integer) {
            editText.setError(editText.getContext().getString((Integer) strOrResId));
        } else {
            editText.setError((String) strOrResId);
        }

    }

    @BindingAdapter("onFocus")
    public static void bindFocusChange(EditText editText, View.OnFocusChangeListener onFocusChangeListener) {
        if (editText.getOnFocusChangeListener() == null) {
            editText.setOnFocusChangeListener(onFocusChangeListener);
        }
    }

    @BindingAdapter("orderStatusText")
    public static void orderStatusText(TextView view, int status) {
        view.setText(getPaymentStatusString(status));
    }

    @BindingAdapter("orderStatusColor")
    public static void orderStatusColor(TextView view, int status) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), getPaymentStatusColor(status)));
    }

    @BindingAdapter("orderStatus")
    public static void orderStatus(TextView view, CustomerOrder order) {
        if(order != null) {
            Context context = view.getContext();
            view.setTextColor(ContextCompat.getColor(context, getPaymentStatusColor(order.getStatus())));
            view.setText(getPaymentStatusString(order.getStatus()));
        }
    }

    @BindingAdapter("prettyTime")
    public static void prettyTime(TextView view, long date) {
        if(date != 0l) {
            view.setText(Util.getHumanRedableTimeFormat(new Date(date)));
//            view.setText(String.valueOf(date));
        } else {
            view.setText("Unkown");
        }
    }

    @BindingAdapter("orderStatusDrawable")
    public static void orderStatusDrawable(ImageView view, CustomerOrder order) {
        if(order != null) {
            Context context = view.getContext();
            Drawable drawable = AppCompatResources.getDrawable(context, getPaymentStatusDrawable(order.getStatus()));
            int color = ContextCompat.getColor(context, getPaymentStatusColor(order.getStatus()));
            DrawableCompat.setTint(drawable, color);
            view.setImageDrawable(drawable);
        }
    }

    private static String getPaymentStatusString(int status){
        String result = "Pending";
        switch (status){
            case CustomerOrder.STATUS_CANCELED:
                result = "Canceled";
                break;
            case CustomerOrder.STATUS_EXPIRED:
                result = "Expired";
                break;
            case CustomerOrder.STATUS_PAID:
                result = "Paid";
                break;
            case CustomerOrder.STATUS_PROCESSING:
                result = "Processing";
                break;
        }
        return result;
    }

    private static int getPaymentStatusColor(int status){
        int result = R.color.yenepay_blue;
        switch (status){
            case CustomerOrder.STATUS_CANCELED:
            case CustomerOrder.STATUS_EXPIRED:
                result = R.color.colorAccent;
                break;
            case CustomerOrder.STATUS_PAID:
                result = R.color.colorPrimaryDark;
                break;
            case CustomerOrder.STATUS_PROCESSING:
                result = R.color.yenepay_blue;
                break;
        }
        return result;
    }

    private static int getPaymentStatusDrawable(int status){
        int result = R.drawable.ic_add_shopping_cart_black_24dp;
        switch (status){
            case CustomerOrder.STATUS_CANCELED:
                result = R.drawable.ic_cancel_black_24dp;
                break;
            case CustomerOrder.STATUS_EXPIRED:
                result = R.drawable.ic_alarm_off_black_24dp;
                break;
            case CustomerOrder.STATUS_PAID:
                result = R.drawable.ic_check_circle_black_24dp;
                break;
            case CustomerOrder.STATUS_PROCESSING:
                result = R.drawable.ic_phonelink_setup_black_24dp;
                break;
        }
        return result;
    }

    @BindingAdapter("itemsCount")
    public static void itemsCount(TextView view, int count) {
        Context context = view.getContext();
        view.setText(context.getResources().getQuantityString(R.plurals.items_count, count, count));
    }
}
