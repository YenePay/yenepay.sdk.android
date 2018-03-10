package com.yenepaySDK.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Sisay on 1/29/2017.
 */

public class OrderedItem implements Serializable {
    private static final long serialVersionUID = 8936736441520995536L;
    private String itemId;
    private String itemName;
    private double unitPrice;
    private int quantity;

    public OrderedItem(String itemId, String itemName, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }



    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getItemTotalPrice(){
        return this.unitPrice * this.quantity;
    }

}
