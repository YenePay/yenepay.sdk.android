package examples.mob.yenepay.com.checkoutcounter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import examples.mob.yenepay.com.checkoutcounter.StoreApp;

public class SettingUtil {
    public static final String PREF_KEY_STORE_NAME = "store_name";
    public static final String PREF_KEY_ACCOUNT_NO = "account_no";

    public static final String PREF_KEY_ENABLE_HANDLING = "handling_enable";
    public static final String PREF_KEY_HANDLING_TYPE = "handling_type";
    public static final String PREF_KEY_HANDLING_AMOUNT = "handling_amount";

    public static final String PREF_KEY_ENABLE_SHIPPING = "shipping_enable";
    public static final String PREF_KEY_SHIPPING_TYPE = "shipping_type";
    public static final String PREF_KEY_SHIPPING_AMOUNT = "shipping_amount";

    public static final String PREF_KEY_ENABLE_DISCOUNT = "discount_enable";
    public static final String PREF_KEY_DISCOUNT_TYPE = "discount_type";
    public static final String PREF_KEY_DISCOUNT_AMOUNT = "discount_amount";

    public static final String PREF_KEY_TAX_TYPE = "tax_type";

    public static final int PREF_TAX_TYPE_NONE = -1;
    public static final int PREF_TAX_TYPE_VAT = 1;
    public static final int PREF_TAX_TYPE_TOT = 0;

    public static final int PREF_FEE_CALC_TYPE_PERCENT = 1;
    public static final int PREF_FEE_CALC_TYPE_FIXED = 0;

    public static final int TAX_VAT_PERCENTAGE = 15;
    public static final int TAX_TOT_PERCENTAGE = 10;
    private final SharedPreferences preferences;

    public Context context;

    public SettingUtil(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public static SettingUtil getInstance() {
        return new SettingUtil(StoreApp.getContext());
    }

    public boolean isTaxEnabled(){
        return getInt(PREF_KEY_TAX_TYPE, -1) != PREF_TAX_TYPE_NONE;
    }

    public boolean isTaxVat(){
        return getInt(PREF_KEY_TAX_TYPE, -1) == PREF_TAX_TYPE_VAT;
    }

    public boolean isTaxTOT(){
        return getInt(PREF_KEY_TAX_TYPE, -1) == PREF_TAX_TYPE_TOT;
    }

    public double getTaxAmount(){
        return !isTaxEnabled()? 0 : isTaxVat()? TAX_VAT_PERCENTAGE: TAX_TOT_PERCENTAGE;
    }

    public double getTaxAmount(double amount){
        double discountAmount = getTaxAmount();
        return (discountAmount / 100) * amount;
    }

    public boolean isDiscountEnabled(){
        return getBoolean(PREF_KEY_ENABLE_DISCOUNT, false);
    }

    public boolean isDiscountPercentage(){
        return getInt(PREF_KEY_DISCOUNT_TYPE, 0) == PREF_FEE_CALC_TYPE_PERCENT;
    }

    public boolean isDiscountFixed(){
        return getInt(PREF_KEY_DISCOUNT_TYPE, 0) == PREF_FEE_CALC_TYPE_FIXED;
    }

    public double getDiscountAmount(){
        return Double.parseDouble(preferences.getString(PREF_KEY_DISCOUNT_AMOUNT, "0.00"));
    }

    public double getDiscountAmount(double amount){
        double discountAmount = getDiscountAmount();
        return !isDiscountEnabled() ? 0: isDiscountFixed()? discountAmount : (discountAmount / 100) * amount;
    }

    public boolean isHandlingEnabled(){
        return getBoolean(PREF_KEY_ENABLE_HANDLING, false);
    }

    public boolean isHandlingPercentage(){
        return getInt(PREF_KEY_HANDLING_TYPE, 0) == PREF_FEE_CALC_TYPE_PERCENT;
    }

    public boolean isHandlingFixed(){
        return getInt(PREF_KEY_HANDLING_TYPE, 0) == PREF_FEE_CALC_TYPE_FIXED;
    }

    public double getHandlingAmount(){
        return Double.parseDouble(preferences.getString(PREF_KEY_HANDLING_AMOUNT, "0.00"));
    }

    public double getHandlingAmount(double amount){
        double discountAmount = getHandlingAmount();
        return !isHandlingEnabled()? 0: isHandlingFixed()? discountAmount : (discountAmount / 100) * amount;
    }

    public boolean isShippingEnabled(){
        return getBoolean(PREF_KEY_ENABLE_SHIPPING, false);
    }

    public boolean isShippingPercentage(){
        return getInt(PREF_KEY_SHIPPING_TYPE, 0) == PREF_FEE_CALC_TYPE_PERCENT;
    }

    public boolean isShippingFixed(){
        return getInt(PREF_KEY_SHIPPING_TYPE, 0) == PREF_FEE_CALC_TYPE_FIXED;
    }

    public double getShippingAmount(){
        return Double.parseDouble(preferences.getString(PREF_KEY_SHIPPING_AMOUNT, "0.00"));
    }

    public double getShippingAmount(double amount){
        double discountAmount = getShippingAmount();
        return !isShippingEnabled()? 0: isShippingFixed()? discountAmount : (discountAmount / 100) * amount;
    }

    private int getInt(String key, int defaultValue){
        return Integer.parseInt(preferences.getString(key, defaultValue+""));
    }

    private boolean getBoolean(String key, boolean defaultValue){
        return preferences.getBoolean(key, defaultValue);
    }



}
