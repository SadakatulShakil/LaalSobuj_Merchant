
package com.futureskyltd.app.utils.OrderDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {

    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("item_image")
    @Expose
    private String itemImage;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_skucode")
    @Expose
    private String itemSkucode;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("deal_percentage")
    @Expose
    private String dealPercentage;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemSkucode() {
        return itemSkucode;
    }

    public void setItemSkucode(String itemSkucode) {
        this.itemSkucode = itemSkucode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDealPercentage() {
        return dealPercentage;
    }

    public void setDealPercentage(String dealPercentage) {
        this.dealPercentage = dealPercentage;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemImage='" + itemImage + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemSkucode='" + itemSkucode + '\'' +
                ", quantity=" + quantity +
                ", price='" + price + '\'' +
                ", size='" + size + '\'' +
                ", dealPercentage='" + dealPercentage + '\'' +
                '}';
    }
}
